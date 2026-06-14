package io.github.mchubtransfer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class HubTransferConfigServiceTest {
    @TempDir
    private Path tempDir;

    @Test
    void createsAndLoadsDefaultConfigWhenMissing() {
        Path configPath = tempDir.resolve("config").resolve("mc-hub-transfer.json");
        HubTransferConfigService service = new HubTransferConfigService(configPath);

        service.loadOrCreate();

        assertTrue(Files.exists(configPath));
        assertEquals("play.alec-ofc.com", service.activeConfig().hub().java().host());
        assertEquals(25565, service.activeConfig().hub().java().port());
        assertEquals("play.alec-ofc.com", service.activeConfig().hub().bedrock().host());
        assertEquals(19132, service.activeConfig().hub().bedrock().port());
    }

    @Test
    void invalidConfigFailsWithActionableMessage() throws IOException {
        Path configPath = tempDir.resolve("mc-hub-transfer.json");
        Files.writeString(configPath, """
                {
                  "hub": {
                    "java": { "host": " ", "port": 25565 },
                    "bedrock": { "host": "play.alec-ofc.com", "port": 19132 }
                  },
                  "javaTransferFailureMessage": "Unable to transfer.",
                  "bedrockReconnectMessage": "Reconnect to Bedrock."
                }
                """);

        HubTransferConfigService service = new HubTransferConfigService(configPath);
        HubTransferConfigException exception = assertThrows(HubTransferConfigException.class, service::reload);

        assertTrue(exception.getMessage().contains("hub.java.host must not be blank"));
    }

    @Test
    void reloadUpdatesActiveConfigFromDisk() throws IOException {
        Path configPath = tempDir.resolve("mc-hub-transfer.json");
        HubTransferConfigService service = new HubTransferConfigService(configPath);
        Files.writeString(configPath, configJson("first.example", 25565, "bedrock.first.example", 19132));

        service.reload();
        assertEquals("first.example", service.activeConfig().hub().java().host());

        Files.writeString(configPath, configJson("second.example", 25566, "bedrock.second.example", 19133));
        service.reload();

        assertEquals("second.example", service.activeConfig().hub().java().host());
        assertEquals(25566, service.activeConfig().hub().java().port());
        assertEquals("bedrock.second.example", service.activeConfig().hub().bedrock().host());
        assertEquals(19133, service.activeConfig().hub().bedrock().port());
    }

    @Test
    void hubReturnServiceReadsUpdatedActiveConfig() {
        HubTransferConfigService configService = new HubTransferConfigService(tempDir.resolve("mc-hub-transfer.json"));
        configService.updateActiveConfig(HubTransferConfig.defaultConfig());
        HubReturnService hubReturnService = new HubReturnService(configService);

        configService.updateActiveConfig(HubTransferConfig.defaultConfig().withJavaHost("updated.example").withJavaPort(25566));

        assertEquals("updated.example", hubReturnService.activeConfig().hub().java().host());
        assertEquals(25566, hubReturnService.activeConfig().hub().java().port());
    }

    private static String configJson(String javaHost, int javaPort, String bedrockHost, int bedrockPort) {
        return """
                {
                  "hub": {
                    "java": { "host": "%s", "port": %d },
                    "bedrock": { "host": "%s", "port": %d }
                  },
                  "javaTransferFailureMessage": "Unable to transfer to {endpoint}.",
                  "bedrockReconnectMessage": "Reconnect to {endpoint}."
                }
                """.formatted(javaHost, javaPort, bedrockHost, bedrockPort);
    }
}
