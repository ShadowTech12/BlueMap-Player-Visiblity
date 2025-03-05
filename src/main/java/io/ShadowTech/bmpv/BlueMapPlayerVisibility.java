package io.ShadowTech.bmpv;

import de.bluecolored.bluemap.api.BlueMapAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static net.minecraft.server.command.CommandManager.literal;

public class BlueMapPlayerVisibility implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("BMPV");

    @Override
    public void onInitialize() {
        // Log a message when the mod initializes
        System.out.println("BlueMap Player Hiding (BMPH) has loaded successfully!");


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("bmph")
                        .then(literal("visible")
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayerOrThrow();
                                    setPlayerVisibility(source, player, true);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(literal("invisible")
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayerOrThrow();
                                    setPlayerVisibility(source, player, false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(literal("?")
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayerOrThrow();
                                    checkPlayerVisibility(source, player);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    private void setPlayerVisibility(ServerCommandSource source, ServerPlayerEntity player, boolean visible) throws CommandSyntaxException {
        if (BlueMapAPI.getInstance().isPresent()) {
            BlueMapAPI bluemap = BlueMapAPI.getInstance().get();
            bluemap.getWebApp().setPlayerVisibility(player.getUuid(), visible);
            if (visible) {
                source.sendMessage(Text.literal("Your live player icon on BlueMap is now set to visible."));
            } else {
                source.sendMessage(Text.literal("Your live player icon on BlueMap is now set to invisible."));
            }
        } else {
            throw new SimpleCommandExceptionType(Text.literal("Failed to access BlueMap API. Is BlueMap installed?")).create();
        }
    }

    private void checkPlayerVisibility(ServerCommandSource source, ServerPlayerEntity player) throws CommandSyntaxException {
        if (BlueMapAPI.getInstance().isPresent()) {
            BlueMapAPI bluemap = BlueMapAPI.getInstance().get();
            boolean isVisible = bluemap.getWebApp().getPlayerVisibility(player.getUuid());
            if (isVisible) {
                source.sendMessage(Text.literal("Your live player icon on BlueMap is currently set to visible."));
            } else {
                source.sendMessage(Text.literal("Your live player icon on BlueMap is currently set to invisible."));
            }
        } else {
            throw new SimpleCommandExceptionType(Text.literal("Failed to access BlueMap API. Is BlueMap installed?")).create();
        }
    }
}