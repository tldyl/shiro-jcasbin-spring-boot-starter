package org.casbin.watcher;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.watch.WatchEvent;
import org.casbin.jcasbin.persist.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdWatcher implements Watcher {
    private static final Logger log = LoggerFactory.getLogger(EtcdWatcher.class);
    private Client client;
    private String keyName;
    private Runnable callback;

    public EtcdWatcher(Client client, String keyName) {
        this.client = client;
        this.keyName = keyName;
    }

    public ByteSequence getKeyName() {
        return ByteSequence.from(this.keyName, Charset.defaultCharset());
    }

    @Override
    public void setUpdateCallback(Runnable runnable) {
        this.callback = runnable;
    }

    @Override
    public void update() {
        CompletableFuture completableFuture = this.client.getKVClient().get(this.getKeyName());

        try {
            int val = 0;
            GetResponse response = (GetResponse)completableFuture.get();
            if (response.getCount() != 0L) {
                val = Integer.parseInt((response.getKvs().get(0)).getValue().toString(Charset.defaultCharset()));
                log.info("casbin watcher Get: {}", val);
                val++;
            }
            this.client.getKVClient().put(this.getKeyName(), ByteSequence.from(String.valueOf(val), Charset.defaultCharset()));
        } catch (ExecutionException | InterruptedException var4) {
            log.error("casbin watcher error", var4);
        }
    }

    public void startWatch() {
        this.client.getWatchClient().watch(this.getKeyName(), (response) -> {
            List<WatchEvent> eventList = response.getEvents();
            if (this.callback != null) {
                eventList.forEach((e) -> {
                    this.callback.run();
                });
            }
        });
    }

    public void stopWatch() {
        this.client.getWatchClient().close();
    }
}
