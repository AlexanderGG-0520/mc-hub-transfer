package io.github.mchubtransfer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

final class HubTransferAdminCommand {
    private HubTransferAdminCommand() {
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher, HubTransferConfigService configService) {
        dispatcher.register(Commands.literal("hubtransfer")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload").executes(context -> reload(context.getSource(), configService)))
                        .then(Commands.literal("show").executes(context -> show(context.getSource(), configService)))
                        .then(Commands.literal("save").executes(context -> save(context.getSource(), configService)))
                        .then(Commands.literal("set")
                                .then(Commands.literal("javaHost")
                                        .then(Commands.argument("host", StringArgumentType.word())
                                                .executes(context -> {
                                                    if (!canManage(context.getSource(), configService)) {
                                                        return 0;
                                                    }

                                                    return update(
                                                            context.getSource(),
                                                            configService,
                                                            configService.activeConfig().withJavaHost(StringArgumentType.getString(context, "host"))
                                                    );
                                                })))
                                .then(Commands.literal("javaPort")
                                        .then(Commands.argument("port", IntegerArgumentType.integer(1, 65535))
                                                .executes(context -> {
                                                    if (!canManage(context.getSource(), configService)) {
                                                        return 0;
                                                    }

                                                    return update(
                                                            context.getSource(),
                                                            configService,
                                                            configService.activeConfig().withJavaPort(IntegerArgumentType.getInteger(context, "port"))
                                                    );
                                                })))
                                .then(Commands.literal("bedrockHost")
                                        .then(Commands.argument("host", StringArgumentType.word())
                                                .executes(context -> {
                                                    if (!canManage(context.getSource(), configService)) {
                                                        return 0;
                                                    }

                                                    return update(
                                                            context.getSource(),
                                                            configService,
                                                            configService.activeConfig().withBedrockHost(StringArgumentType.getString(context, "host"))
                                                    );
                                                })))
                                .then(Commands.literal("bedrockPort")
                                        .then(Commands.argument("port", IntegerArgumentType.integer(1, 65535))
                                                .executes(context -> {
                                                    if (!canManage(context.getSource(), configService)) {
                                                        return 0;
                                                    }

                                                    return update(
                                                            context.getSource(),
                                                            configService,
                                                            configService.activeConfig().withBedrockPort(IntegerArgumentType.getInteger(context, "port"))
                                                    );
                                                })))
                                .then(Commands.literal("javaTransferFailureMessage")
                                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                                .executes(context -> {
                                                    if (!canManage(context.getSource(), configService)) {
                                                        return 0;
                                                    }

                                                    return update(
                                                            context.getSource(),
                                                            configService,
                                                            configService.activeConfig().withJavaTransferFailureMessage(StringArgumentType.getString(context, "message"))
                                                    );
                                                })))
                                .then(Commands.literal("bedrockReconnectMessage")
                                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                                .executes(context -> {
                                                    if (!canManage(context.getSource(), configService)) {
                                                        return 0;
                                                    }

                                                    return update(
                                                            context.getSource(),
                                                            configService,
                                                            configService.activeConfig().withBedrockReconnectMessage(StringArgumentType.getString(context, "message"))
                                                    );
                                                }))))));
    }

    private static int reload(CommandSourceStack source, HubTransferConfigService configService) {
        if (!canManage(source, configService)) {
            return 0;
        }

        configService.reload();
        source.sendSuccess(() -> Component.literal("Reloaded mc-hub-transfer config from " + configService.configPath()
                + ". Any unsaved runtime changes were discarded."), true);
        return 1;
    }

    private static int show(CommandSourceStack source, HubTransferConfigService configService) {
        if (!canManage(source, configService)) {
            return 0;
        }

        source.sendSuccess(() -> Component.literal(configService.activeConfig().toDisplayString()), false);
        return 1;
    }

    private static int save(CommandSourceStack source, HubTransferConfigService configService) {
        if (!canManage(source, configService)) {
            return 0;
        }

        configService.save();
        source.sendSuccess(() -> Component.literal("Saved mc-hub-transfer config to " + configService.configPath()), true);
        return 1;
    }

    private static int update(CommandSourceStack source, HubTransferConfigService configService, HubTransferConfig config) {
        configService.updateActiveConfig(config);
        source.sendSuccess(() -> Component.literal("Updated active mc-hub-transfer config. Run /hubtransfer config save to persist it."), true);
        return 1;
    }

    private static boolean canManage(CommandSourceStack source, HubTransferConfigService configService) {
        if (!source.getServer().isDedicatedServer() || !configService.isEnabled()) {
            source.sendFailure(configService.disabledComponent());
            return false;
        }

        return true;
    }
}
