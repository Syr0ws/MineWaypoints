package com.github.syr0ws.minewaypoints.util;

import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class Promise<T> {

    private final PromiseExecutor<T> executor;
    private Consumer<T> success;
    private Consumer<Throwable> error;

    public Promise(PromiseExecutor<T> executor) {
        this.executor = executor;
    }

    public Promise<T> onSuccess(Consumer<T> consumer) {
        this.success = consumer;
        return this;
    }

    public Promise<T> onError(Consumer<Throwable> consumer) {
        this.error = consumer;
        return this;
    }

    public void resolveSync() {
        try {
            this.executor.execute(value -> {
                if(this.success != null) {
                    this.success.accept(value);
                }
            }, error -> {
                if(this.error != null) {
                    this.error.accept(error);
                }
            });
        } catch (Exception exception) {
            this.error.accept(exception);
        }
    }

    public void resolveAsync(Plugin plugin) {
        Async.runAsync(plugin, this::resolveSync);
    }
}
