/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.mchubtransfer;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public final class McHubTransferClient implements ClientModInitializer {
    private static final int DEFAULT_PORT = 25565;
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("transfer")
                        .executes(McHubTransferClient::showUsage)
                        .then(ClientCommandManager.argument("host", StringArgumentType.word())
                                .executes(context -> transfer(context, DEFAULT_PORT))
                                .then(ClientCommandManager.argument("port", StringArgumentType.greedyString())
                                        .executes(McHubTransferClient::transferWithPort)))
        ));
    }

    private static int showUsage(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendError(Text.literal("Usage: /transfer <host> [port]"));
        return 0;
    }

    private static int transferWithPort(CommandContext<FabricClientCommandSource> context) {
        String portValue = StringArgumentType.getString(context, "port").trim();

        if (portValue.isEmpty()) {
            context.getSource().sendError(Text.literal("Port must be an integer between 1 and 65535."));
            return 0;
        }

        int port;
        try {
            port = Integer.parseInt(portValue);
        } catch (NumberFormatException ignored) {
            context.getSource().sendError(Text.literal("Port must be an integer between 1 and 65535."));
            return 0;
        }

        if (port < MIN_PORT || port > MAX_PORT) {
            context.getSource().sendError(Text.literal("Port must be an integer between 1 and 65535."));
            return 0;
        }

        return transfer(context, port);
    }

    private static int transfer(CommandContext<FabricClientCommandSource> context, int port) {
        String host = StringArgumentType.getString(context, "host").trim();
        FabricClientCommandSource source = context.getSource();

        if (host.isEmpty()) {
            source.sendError(Text.literal("Host must not be empty."));
            return 0;
        }

        MinecraftClient client = source.getClient();
        if (client.getCurrentServerEntry() == null) {
            source.sendError(Text.literal("You must be connected to a multiplayer server to transfer."));
            return 0;
        }

        String address = host + ":" + port;
        source.sendFeedback(Text.literal("Transferring to " + address + "..."));

        client.execute(() -> {
            ServerAddress serverAddress = ServerAddress.parse(address);
            ServerInfo serverInfo = new ServerInfo(address, address, false);

            client.disconnect(new MessageScreen(Text.literal("Transferring to " + address + "...")));
            ConnectScreen.connect(new TitleScreen(), client, serverAddress, serverInfo, false);
        });

        return 1;
    }
}
