package com.github.syr0ws.minewaypoints.integration;

import com.github.syr0ws.crafter.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class IntegrationService {

    private final List<Integration> integrations = new ArrayList<>();

    public void registerIntegration(Integration integration) {
        Validate.notNull(integration, "integration cannot be null");
        this.integrations.add(integration);
    }

    public void unregisterIntegration(Integration integration) {
        Validate.notNull(integration, "integration cannot be null");
        this.integrations.remove(integration);
    }

    public void enableIntegrations() {
        this.integrations.stream()
                .filter(integration -> !integration.isEnabled() && integration.canBeEnabled())
                .forEach(Integration::enable);
    }

    public void disableIntegrations() {
        this.integrations.stream()
                .filter(Integration::isEnabled)
                .forEach(Integration::disable);
    }
}
