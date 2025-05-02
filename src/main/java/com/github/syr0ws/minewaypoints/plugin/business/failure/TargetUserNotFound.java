package com.github.syr0ws.minewaypoints.plugin.business.failure;

import com.github.syr0ws.crafter.business.BusinessFailure;

import java.util.UUID;

public record TargetUserNotFound(UUID targetId) implements BusinessFailure {

}
