package net.islandearth.schematics.extended.example;

import net.islandearth.schematics.extended.Scheduler;
import net.islandearth.schematics.extended.Schematic;
import net.islandearth.schematics.extended.Schematic.Options;
import net.islandearth.schematics.extended.SchematicNotLoadedException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-player task that previews schematics async
 * @author SamB440
 */
public class BuildTask {

	private SchematicPlugin plugin;

	private Player player;

	private Map<Player, List<Location>> cache = new HashMap<>();
	
	public BuildTask(SchematicPlugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	@SuppressWarnings("deprecation")
	public BuildTask start() {
		Scheduler scheduler = new Scheduler();
		scheduler.setTask(Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
			PlayerManagement pm = plugin.getPlayerManagement();
			if (pm.isBuilding(player.getUniqueId())) {
				Schematic schematic = pm.getBuilding(player.getUniqueId());
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.RED + "Left Click to cancel" + ChatColor.GRAY + " : " + ChatColor.GREEN + "Right Click to place").create());
				try {
					List<Location> locations = schematic.pasteSchematic(player.getTargetBlock(null, 10).getLocation().add(0, 1, 0), player, Options.PREVIEW);
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
						if (cache.containsKey(player)) {
							if (!cache.get(player).equals(locations)) {
								int current = 0;
								for (Location location : cache.get(player)) {
									if (current == locations.size()) break;
									if (location.distance(locations.get(current)) >= 1) player.sendBlockChange(location, location.getBlock().getBlockData());
									current++;
								}
								cache.remove(player);
							}
						}
					if (!cache.containsKey(player)) cache.put(player, locations);
					}, 2L);
				} catch (SchematicNotLoadedException e) {
					e.printStackTrace();
				}
			} else if (!pm.isBuilding(player.getUniqueId())) {
				for (Location location : cache.get(player)) {
					player.sendBlockChange(location, location.getBlock().getBlockData());
				}
				scheduler.cancel();
			}
		}, 5L, 1L));
		return this;
	}
}
