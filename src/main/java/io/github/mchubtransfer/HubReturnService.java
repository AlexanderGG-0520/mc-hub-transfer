package io.github.mchubtransfer;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;

public final class HubReturnService {
    private final HubTransferConfigService configService;

    public HubReturnService(HubTransferConfigService configService) {
        this.configService = configService;
    }

    public HubTransferConfig activeConfig() {
        return configService.activeConfig();
    }

    public void sendToHub(CommandSourceStack source, ServerPlayer player) {
        HubTransferConfig config = configService.activeConfig();

        if (BedrockPlayerDetector.isBedrockPlayer(player.getUUID())) {
            HubEndpoint bedrockHub = config.hub().bedrock();
            player.sendSystemMessage(Component.literal(format(config.bedrockReconnectMessage(), bedrockHub)));
            return;
        }

        HubEndpoint javaHub = config.hub().java();
        String transferCommand = "transfer " + javaHub.host() + " " + javaHub.port();

        try {
            CommandSourceStack elevatedSource = source.withPermission(PermissionSet.ALL_PERMISSIONS);
            source.getServer().getCommands().performPrefixedCommand(elevatedSource, transferCommand);
        } catch (RuntimeException exception) {
            player.sendSystemMessage(Component.literal(format(config.javaTransferFailureMessage(), javaHub)));
        }
    }

    private static String format(String message, HubEndpoint endpoint) {
        return message
                .replace("{host}", endpoint.host())
                .replace("{port}", Integer.toString(endpoint.port()))
                .replace("{endpoint}", endpoint.asHostPort());
    }
}
