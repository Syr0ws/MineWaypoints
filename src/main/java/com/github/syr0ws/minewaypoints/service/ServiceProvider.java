package com.github.syr0ws.minewaypoints.service;

public interface ServiceProvider {

    <T extends Service> void addService(T service, Class<T> serviceClass);

    <T extends Service> T provide(Class<T> serviceClass, ServiceType serviceType);

    enum ServiceType {
        SYNC, ASYNC
    }
}
