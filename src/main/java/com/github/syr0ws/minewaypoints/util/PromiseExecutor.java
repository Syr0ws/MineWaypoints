package com.github.syr0ws.minewaypoints.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface PromiseExecutor<T> {

    void execute(Consumer<T> resolve, Consumer<Throwable> reject) throws Exception;
}
