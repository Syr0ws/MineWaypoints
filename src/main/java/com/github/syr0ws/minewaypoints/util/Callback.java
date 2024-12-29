package com.github.syr0ws.minewaypoints.util;

public interface Callback<T> {

    void onSuccess(T value);

    void onError(Throwable throwable);
}
