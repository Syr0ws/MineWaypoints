package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.service.Service;
import com.github.syr0ws.minewaypoints.service.ServiceProvider;

import java.util.HashSet;
import java.util.Set;

public class SimpleServiceProvider implements ServiceProvider {

    private final Set<ServiceData<?>> services = new HashSet<>();

    @Override
    public <T extends Service> void addService(T service, Class<T> serviceClass) {

        if(service == null) {
            throw new IllegalArgumentException("service cannot be null");
        }

        if(serviceClass == null) {
            throw new IllegalArgumentException("serviceClass cannot be null");
        }

        this.services.add(new ServiceData<>(service, serviceClass));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Service> T provide(Class<T> serviceClass, ServiceType serviceType) {

        if(serviceClass == null) {
            throw new IllegalArgumentException("serviceClass cannot be null");
        }

        if(serviceType == null) {
            throw new IllegalArgumentException("serviceType cannot be null");
        }

        return this.services.stream()
                .filter(data -> data.serviceClass().equals(serviceClass) && data.service().getType() == serviceType)
                .map(data -> (T) data.service())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Service not found"));
    }

    private record ServiceData<T extends Service>(T service, Class<T> serviceClass) {

    }
}
