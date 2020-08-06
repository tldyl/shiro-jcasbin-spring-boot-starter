package org.casbin.config;

import io.etcd.jetcd.Client;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.casbin.adapter.HibernateAdapter;
import org.casbin.advisor.CasbinAdvisor;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.main.SyncedEnforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.properties.CasbinProperties;
import org.casbin.subject.CasbinDefaultWebSubjectFactory;
import org.casbin.watcher.EtcdWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

@Configuration
@EnableConfigurationProperties(CasbinProperties.class)
@AutoConfigureBefore({ShiroWebAutoConfiguration.class, ShiroAutoConfiguration.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ConditionalOnProperty(prefix = CasbinProperties.PREFIX, value = "enabled", matchIfMissing = true)
public class CasbinAutoConfiguration {
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private CasbinProperties casbinProperties;

    @Bean
    @SneakyThrows
    public SubjectFactory subjectFactory(Client client) {
        Model model = new Model();
        String modelPath = new ClassPathResource(casbinProperties.getModel()).exists()
                ? casbinProperties.getModel() : "classpath:casbin/model_request.conf";
        @Cleanup
        InputStream is = ResourceUtils.getURL(modelPath).openStream();
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[100];
        int len;
        while ((len = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, len, Charset.defaultCharset()));
        }
        model.loadModelFromText(sb.toString());

        Enforcer enforcer;
        Class<? extends Enforcer> enforcerClass;

        if (casbinProperties.getSynced()) {
            enforcerClass = SyncedEnforcer.class;
        } else {
            enforcerClass = Enforcer.class;
        }
        if (dataSourceProperties != null) {
            enforcer = enforcerClass.getConstructor(Model.class, Adapter.class).newInstance(model, new HibernateAdapter(dataSourceProperties.getDriverClassName(),
                    dataSourceProperties.getUrl(),
                    dataSourceProperties.getUsername(),
                    dataSourceProperties.getPassword()));
        } else {
            enforcer = enforcerClass.newInstance();
        }
        EtcdWatcher watcher = new EtcdWatcher(client, casbinProperties.getWatcherKey());
        enforcer.setWatcher(watcher);
        if (casbinProperties.getWatcher()) watcher.startWatch();
        return new CasbinDefaultWebSubjectFactory(enforcer);
    }

    @Bean
    public CasbinAdvisor casbinAdvisor() {
        return new CasbinAdvisor();
    }
}
