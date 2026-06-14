package io.github.mchubtransfer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public final class AlecHubCommandMod implements ModInitializer {
    public static final String MOD_ID = "alec_hub_command";

    private final HubTransferConfigService configService = new HubTransferConfigService(
            FabricLoader.getInstance().getConfigDir().resolve("mc-hub-transfer.json")
    );
    private final HubReturnService hubReturnService = new HubReturnService(configService);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            if (!server.isDedicatedServer()) {
                configService.disable("mc-hub-transfer is a dedicated-server-only mod. Singleplayer/integrated server is not supported.");
                server.sendSystemMessage(Component.literal(configService.disabledReason()));
                return;
            }

            configService.loadOrCreate();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            HubCommand.register(dispatcher, configService, hubReturnService);
            HubTransferAdminCommand.register(dispatcher, configService);
        });
    }
}
