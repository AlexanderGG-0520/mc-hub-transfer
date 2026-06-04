package io.github.mchubtransfer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;

public final class AlecHubCommandMod implements ModInitializer {
    private static final String TRANSFER_COMMAND = "transfer play.alec-ofc.com 25565";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("hub").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    player.sendSystemMessage(Component.literal("Hub鯖に転送します..."));
                    CommandSourceStack elevatedSource = context.getSource().withPermission(PermissionSet.ALL_PERMISSIONS);

                    context.getSource().getServer().getCommands().performPrefixedCommand(
                            elevatedSource,
                            TRANSFER_COMMAND
                    );

                    return 1;
                }))
        );
    }
}
