package io.github.mchubtransfer;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

final class HubCommand {
    private HubCommand() {
    }

    static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            HubTransferConfigService configService,
            HubReturnService hubReturnService
    ) {
        dispatcher.register(Commands.literal("hub").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();

            if (!context.getSource().getServer().isDedicatedServer() || !configService.isEnabled()) {
                player.sendSystemMessage(configService.disabledComponent());
                return 0;
            }

            hubReturnService.sendToHub(context.getSource(), player);
            return 1;
        }));
    }
}
