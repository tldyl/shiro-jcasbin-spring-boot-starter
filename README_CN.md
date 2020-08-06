# Shiro jCasbin Spring Boot Starter

## Shiro整合Casbin，将Shiro自身的权限角色验证替换为Casbin权限角色验证

# [English](./README.md) | 中文

## 安装
目前尚未部署到maven，如要使用，请克隆工程自行在本地打包。

## 使用
1. 下载[model_request.conf](./src/main/resources/conf/model_request.conf)放在`classpath:casbin/model_request.conf`
或者自己编写model_request.conf来定制模型。如果选择自行定制模型，请参考[配置](#配置)部分在配置文件中指定模型文件所在的路径。
2. 在Controller方法上加上`@RequireCasbin`注解，该请求将验证该请求地址权限。\
这里填入Enforcer的enforce方法的三个参数分别是： Shiro的`principals`值、请求的地址`ServletPath`、请求方式`method`
    
        @RequireCasbin
        @GetMapping("/index")
        public String index() {
            return "index";
        }
3. 在Controller方法上加上`@HasRoleForUser(role="role1")`注解，该请求将验证用户是否具有指定的权限。\
这里填入Enforcer的hasRoleForUser方法的两个参数分别是：Shiro的`principals`值、在注解上填入的参数role

        @HasRoleForUser(role = "role1")
        @PostMapping("/update")
        @ResponseBody
        public String update() {
            ...
        }

4. 为请求接口分配权限
    
        CasbinSubject subject = (CasbinSubject) SecurityUtils.getSubject(); //从上下文拿到CasbinSubject
        Enforcer enforcer = subject.getEnforcer(); //从CasbinSubject拿到enforcer

        enforcer.addPermissionForUser(role, "/index", "GET"); //根据rbac的原则，第一个参数可以是role也可以是user
        enforcer.addRoleForUser(user, role); //给user赋予role
    

## 配置
下面的值为默认值

    shiro-jcasbin:
      enabled: true //启用jcasbin
      rule-table: casbin_rule_request //存储policy的数据表名称
      model: classpath:casbin/model_request.conf //模型所在的路径
      watcher: true //启用watcher，需要引用jetcd
      watcher-key: /casbin/watcher_request //watcher的键名
      synced: false //不使用读写锁(线程不安全)
```
