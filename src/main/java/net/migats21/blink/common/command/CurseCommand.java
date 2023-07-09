package net.migats21.blink.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.common.ServerSavedData;
import net.migats21.blink.network.ClientboundSolarCursePacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CurseCommand {
    private static final SimpleCommandExceptionType NOT_IN_OVERWORLD = new SimpleCommandExceptionType(Component.translatable("commands.blink.failed.not_in_overworld"));
    public static final Component MSG_EXECUTED = Component.translatableWithFallback("commands.blink.curse.executed", "Solar curse executed");
    public static final Component MSG_BROKEN = Component.translatableWithFallback("commands.blink.curse.broken", "Solar curse broken");
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("curse").requires((commandSourceStack) -> commandSourceStack.hasPermission(3)).executes(context -> {
            CommandSourceStack source = context.getSource();
            if (!source.getLevel().dimensionType().hasSkyLight()) throw NOT_IN_OVERWORLD.create();
            ServerSavedData solarCurseData = ServerSavedData.getSavedData(source.getLevel());
            solarCurseData.setCursed(!solarCurseData.isCursed());
            FriendlyByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(solarCurseData.isCursed());
            for (ServerPlayer player : source.getLevel().getPlayers(player -> true)) {
                ServerPlayNetworking.send(player, ClientboundSolarCursePacket.ID, buffer);
            }
            source.sendSuccess(() -> solarCurseData.isCursed() ? MSG_EXECUTED : MSG_BROKEN, true);
            return solarCurseData.isCursed() ? 1 : 0;
        }));
    }
}
