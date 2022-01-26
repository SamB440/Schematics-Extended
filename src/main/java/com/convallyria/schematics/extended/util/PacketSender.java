package com.convallyria.schematics.extended.util;

import com.convallyria.schematics.extended.example.SchematicPlugin;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class PacketSender {

    public static void sendHighlightedBlock(final Player player, final Block block, int color, int time) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeLong(blockPosToLong(block.getX(), block.getY(), block.getZ()));
        packet.writeInt(color);
        packet.writeInt(time);

        sendPayload(player, packet);
    }

    private static long blockPosToLong(int x, int y, int z) {
        return ((long) x & 67108863L) << 38 | (long) y & 4095L | ((long) z & 67108863L) << 12;
    }

    private static void sendPayload(@NonNull final Player receiver, ByteBuf bytes) {
        PacketPlayOutCustomPayload customPayload = new PacketPlayOutCustomPayload(PacketPlayOutCustomPayload.n, new PacketDataSerializer(bytes));
        ((CraftPlayer) receiver).getHandle().b.a.a(customPayload);
    }

    private static void sendPacket(@NotNull final CraftPlayer player, @NotNull final Packet packet) {
        Bukkit.getScheduler().runTaskAsynchronously(
                JavaPlugin.getPlugin(SchematicPlugin.class), () -> player.getHandle().b.a(packet)
        );
    }
}
