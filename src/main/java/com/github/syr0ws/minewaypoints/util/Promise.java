package com.github.syr0ws.minewaypoints.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class Promise<T> {

    private final PromiseExecutor<T> executor;

    private Consumer<T> then;
    private Consumer<Throwable> except;
    private Runnable complete;

    public Promise(PromiseExecutor<T> executor) {

        if(executor == null) {
            throw new IllegalArgumentException("executor cannot be null");
        }

        this.executor = executor;
    }

    public Promise<T> then(Consumer<T> consumer) {

        if(consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null");
        }

        this.then = consumer;
        return this;
    }

    public Promise<T> except(Consumer<Throwable> consumer) {

        if(consumer == null) {
            throw new IllegalArgumentException("consumer cannot be null");
        }

        this.except = consumer;
        return this;
    }

    public Promise<T> complete(Runnable runnable) {

        if(runnable == null) {
            throw new IllegalArgumentException("runnable cannot be null");
        }

        this.complete = runnable;
        return this;
    }

    public void resolveSync(Plugin plugin) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        Bukkit.getScheduler().runTask(plugin, this::resolve);
    }

    public void resolveAsync(Plugin plugin) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::resolve);
    }

    private void onThen(T value) {
        if(this.then != null) {
            this.then.accept(value);
        }
    }

    private void onExcept(Throwable throwable) {
        if(this.except != null) {
            this.except.accept(throwable);
        }
    }

    private void onComplete() {
        if(this.complete != null) {
            this.complete.run();
        }
    }

    private void resolve() {
        try {
            this.executor.execute(this::onThen, this::onExcept);
        } catch (Exception exception) {
            this.onExcept(exception);
        }
        this.onComplete();
    }
}
