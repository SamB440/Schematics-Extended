import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.SamB440.Civilization.Civilization;
import com.SamB440.Civilization.API.data.CivPlayer;
import com.SamB440.Civilization.API.data.Settlement;
import com.SamB440.Civilization.API.data.SettlementClaim;

import lombok.Getter;

/**
 * @author SamB440
 */
public class BuildTask implements Runnable {
	
	private JavaPlugin plugin;
	@Getter private HashMap<Player, List<Location>> cache = new HashMap<Player, List<Location>>();
	
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
				List<Location> locations = plugin.getPlayerManagement.getBuilding(player.getUniqueId()).pasteSchematic(player.getTargetBlock(null, 7).getLocation().add(0, 1, 0), player, true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					if(cache.containsKey(player))
					{
						if(!cache.get(player).equals(locations))
						{
							int current = 0;
							for(Location location : cache.get(player))
							{
								if(location.distance(locations.get(current)) >= 1) location.getBlock().getState().update(true, false);
								current++;
							}
							cache.remove(player);
						}
					}
				if(!cache.containsKey(player)) cache.put(player, locations);
				}, 20);
			}
		}
	}
}