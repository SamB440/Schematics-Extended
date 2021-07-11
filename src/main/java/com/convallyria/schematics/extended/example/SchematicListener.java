package com.convallyria.schematics.extended.example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.convallyria.schematics.extended.Scheduler;
import com.convallyria.schematics.extended.Schematic;
import com.convallyria.schematics.extended.SchematicNotLoadedException;

public record SchematicListener(SchematicPlugin plugin) implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent pie) {
        if (pie.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = pie.getPlayer();
        if (plugin.getPlayerManagement().isBuilding(player.getUniqueId())) {
            switch (pie.getAction()) {
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    try {
                        player.sendMessage(ChatColor.GREEN + "You are now building the schematic; " + plugin.getPlayerManagement().getBuilding(player.getUniqueId()) + "!");
                        Collection<Location> locationCollection = plugin.getPlayerManagement()
                                .getBuilding(player.getUniqueId())
                                .pasteSchematic(player.getTargetBlock(null, 10)
                                        .getLocation().add(0, 1, 0), player, 1, Schematic.Options.IGNORE_TRANSPARENT);
                        if (locationCollection != null) {
                            List<Location> locations = new ArrayList<>(locationCollection);
                            Scheduler scheduler = new Scheduler();
                            scheduler.setTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                                for (Location location : locations) {
                                    if (locations.get(locations.size() - 1).getBlock().getType() != Material.AIR) {
                                        scheduler.cancel();
                                    } else {
                                        if (location.getBlock().getType() == Material.AIR) {
                                            location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location.getX() + 0.5D, location.getY(), location.getZ() + 0.5D, 2);
                                        }
                                    }
                                }
                            }, 0L, 40L));
                            plugin.getPlayerManagement().removeBuilding(player.getUniqueId());
                        } else {
                            player.sendMessage(ChatColor.RED + "You can't place the schematic here, you need to clear the area first!");
                        }
                    } catch (SchematicNotLoadedException e) {
                        e.printStackTrace();
                    }
                    break;
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    plugin.getPlayerManagement().removeBuilding(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Cancelled building placement.");
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent pje) {
        Player player = pje.getPlayer();
        File file = new File(plugin.getDataFolder() + "/schematics/example.schem");
        if (!file.exists()) {
            player.sendMessage(ChatColor.RED + "Not creating schematic preview since example file does not exist!");
        } else {
            new Schematic(plugin, new File(plugin.getDataFolder() + "/schematics/example.schem"))
                    .loadSchematic()
                    .previewSchematic(player);
            player.sendMessage(ChatColor.GREEN + "Now previewing schematic!");
        }
    }
}
