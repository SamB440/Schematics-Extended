import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;

/**
 * @author SamB440
 */
public class BuildTask implements Runnable {
	
	private JavaPlugin plugin;
	@Getter private Map<Player, List<Location>> cache = new HashMap<>();
	
	public BuildTask(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run() {
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(plugin.getPlayerManagement().isBuilding(player.getUniqueId()))
			{
				List<Location> locations = plugin.getPlayerManagement().getBuilding(player.getUniqueId()).pasteSchematic(player.getTargetBlock(null, 7).getLocation().add(0, 1, 0), player, Options.PREVIEW);
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
					if(cache.containsKey(player))
					{
						if(!cache.get(player).equals(locations))
						{
							int current = 0;
							for(Location location : cache.get(player))
							{
								if(current == locations.size()) break;
								if(location.distance(locations.get(current)) >= 1) player.sendBlockChange(location, location.getBlock().getBlockData());
								current++;
							}
							cache.remove(player);
						}
					}
				if(!cache.containsKey(player)) cache.put(player, locations);
				}, 2L);
			}
		}
	}
}