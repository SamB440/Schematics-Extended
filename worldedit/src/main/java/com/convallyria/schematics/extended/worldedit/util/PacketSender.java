package com.convallyria.schematics.extended.worldedit.util;

import com.convallyria.schematics.extended.worldedit.example.SchematicPlugin;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.charset.StandardCharsets;

public class PacketSender {

    public static void sendBlockHighlight(@NotNull final Player player, @NotNull Location location, Color colour, int time) {
        ByteBuf packet = Unpooled.buffer();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        packet.writeLong(blockPosToLong(x, y, z));
        int argb = (0xFF & 175) << 24 | (0xFF & colour.getRed()) << 16 | (0xFF & colour.getGreen()) << 8 | (0xFF & colour.getBlue());
        packet.writeInt(argb);
        writeString(packet, "");
        packet.writeInt(time);

        sendPayload(player, "debug/game_test_add_marker", packet);
    }

    private static long blockPosToLong(int x, int y, int z) {
        return ((long) x & 67108863L) << 38 | (long) y & 4095L | ((long) z & 67108863L) << 12;
    }

    public static void wrap(ByteBuf packet, int i) {
        while ((i & -128) != 0) {
            packet.writeByte(i & 127 | 128);
            i >>>= 7;
        }
        packet.writeByte(i);
    }

    public static void writeString(@NotNull final ByteBuf packet, @NotNull final String string) {
        byte[] byteArray = string.getBytes(StandardCharsets.UTF_8);
        wrap(packet, byteArray.length);
        packet.writeBytes(byteArray);
    }

    private static void sendPayload(@NotNull final Player receiver, String channel, ByteBuf bytes) {
        PacketPlayOutCustomPayload customPayload = new PacketPlayOutCustomPayload(new MinecraftKey(channel), new PacketDataSerializer(bytes));
        sendPacket((CraftPlayer) receiver, customPayload);
    }

    private static void sendPacket(@NotNull final CraftPlayer player, @NotNull final Packet packet) {
        Bukkit.getScheduler().runTaskAsynchronously(
                JavaPlugin.getPlugin(SchematicPlugin.class), () -> player.getHandle().b.a(packet)
        );
    }
}
