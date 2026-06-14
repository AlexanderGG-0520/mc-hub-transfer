package io.github.mchubtransfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HubTransferConfigService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configPath;
    private HubTransferConfig activeConfig;
    private boolean enabled = true;
    private String disabledReason = "mc-hub-transfer is disabled.";

    public HubTransferConfigService(Path configPath) {
        this.configPath = configPath;
    }

    public Path configPath() {
        return configPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String disabledReason() {
        return disabledReason;
    }

    public Component disabledComponent() {
        return Component.literal(disabledReason);
    }

    public void disable(String reason) {
        enabled = false;
        disabledReason = reason;
    }

    public HubTransferConfig activeConfig() {
        if (activeConfig == null) {
            throw new HubTransferConfigException("mc-hub-transfer config has not been loaded yet.");
        }

        return activeConfig;
    }

    public void loadOrCreate() {
        if (!Files.exists(configPath)) {
            activeConfig = HubTransferConfig.defaultConfig();
            save();
            return;
        }

        reload();
    }

    public void reload() {
        try (Reader reader = Files.newBufferedReader(configPath)) {
            updateActiveConfig(GSON.fromJson(reader, HubTransferConfig.class));
        } catch (IOException | JsonParseException exception) {
            throw new HubTransferConfigException("Failed to load mc-hub-transfer config from " + configPath + ": " + exception.getMessage(), exception);
        }
    }

    public void save() {
        try {
            Path parent = configPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(activeConfig(), writer);
            }
        } catch (IOException exception) {
            throw new HubTransferConfigException("Failed to save mc-hub-transfer config to " + configPath + ": " + exception.getMessage(), exception);
        }
    }

    public void updateActiveConfig(HubTransferConfig config) {
        validate(config);
        activeConfig = config;
        enabled = true;
    }

    private static void validate(HubTransferConfig config) {
        if (config == null) {
            throw new HubTransferConfigException("Invalid mc-hub-transfer config: root object is missing.");
        }
        if (config.hub() == null) {
            throw new HubTransferConfigException("Invalid mc-hub-transfer config: hub object is missing.");
        }

        validateEndpoint("hub.java", config.hub().java());
        validateEndpoint("hub.bedrock", config.hub().bedrock());
        validateMessage("javaTransferFailureMessage", config.javaTransferFailureMessage());
        validateMessage("bedrockReconnectMessage", config.bedrockReconnectMessage());
    }

    private static void validateEndpoint(String key, HubEndpoint endpoint) {
        if (endpoint == null) {
            throw new HubTransferConfigException("Invalid mc-hub-transfer config: " + key + " object is missing.");
        }
        if (endpoint.host() == null || endpoint.host().isBlank()) {
            throw new HubTransferConfigException("Invalid mc-hub-transfer config: " + key + ".host must not be blank.");
        }
        if (endpoint.port() < 1 || endpoint.port() > 65535) {
            throw new HubTransferConfigException("Invalid mc-hub-transfer config: " + key + ".port must be in 1..65535.");
        }
    }

    private static void validateMessage(String key, String message) {
        if (message == null || message.isBlank()) {
            throw new HubTransferConfigException("Invalid mc-hub-transfer config: " + key + " must not be blank.");
        }
    }
}
