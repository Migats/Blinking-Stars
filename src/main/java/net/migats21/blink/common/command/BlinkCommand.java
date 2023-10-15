package net.migats21.blink.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.migats21.blink.network.ClientboundStarBlinkPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BlinkCommand {
    private static final SimpleCommandExceptionType NO_ENTITY = new SimpleCommandExceptionType(Component.translatableWithFallback("commands.blink.failed.no_entity", "This command needs to be run by entity"));
    private static final SimpleCommandExceptionType NOT_IN_OVERWORLD = new SimpleCommandExceptionType(Component.translatableWithFallback("commands.blink.failed.not_in_overworld", "This command can only be run in the overworld"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ignoredBuildContext, Commands.CommandSelection ignoredSelection) {
        dispatcher.register(Commands.literal("blink").executes(context -> {
            CommandSourceStack source = context.getSource();
            if (source.getEntity() == null) throw NO_ENTITY.create();
            if (!source.getLevel().dimensionType().hasSkyLight()) throw NOT_IN_OVERWORLD.create();
            ClientboundStarBlinkPacket packet = new ClientboundStarBlinkPacket(source.getEntity().getXRot(), source.getEntity().getYRot());
            for (ServerPlayer player : source.getLevel().getPlayers(player -> true)) {
                player.connection.send(packet.asPayload());
            }
            return 1;
        }));
    }
}
