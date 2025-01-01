package com.github.syr0ws.minewaypoints.util;

import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class Promise<T> {

    private final PromiseExecutor<T> executor;
    private Consumer<T> success;
    private Consumer<Throwable> error;

    public Promise(PromiseExecutor<T> executor) {

        if(executor == null) {
            throw new IllegalArgumentException("executor cannot be null");
        }

        this.executor = executor;
    }

    public Promise<T> onSuccess(Consumer<T> consumer) {
        if(consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null");
        }
        this.success = consumer;
        return this;
    }

    public Promise<T> onError(Consumer<Throwable> consumer) {
        if(consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null");
        }
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
            if(this.error != null) {
                this.error.accept(exception);
            }
        }
    }

    public void resolveAsync(Plugin plugin) {
        Async.runAsync(plugin, this::resolveSync);
    }
}
