package io.github.mchubtransfer;

import java.util.UUID;

final class BedrockPlayerDetector {
    private static final String FLOODGATE_API_CLASS = "org.geysermc.floodgate.api.FloodgateApi";

    private BedrockPlayerDetector() {
    }

    static boolean isBedrockPlayer(UUID playerId) {
        try {
            Class<?> apiClass = Class.forName(FLOODGATE_API_CLASS);
            Object api = apiClass.getMethod("getInstance").invoke(null);
            Object result = apiClass.getMethod("isFloodgatePlayer", UUID.class).invoke(api, playerId);
            return Boolean.TRUE.equals(result);
        } catch (ReflectiveOperationException | LinkageError | SecurityException ignored) {
            return false;
        }
    }
}
