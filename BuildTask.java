import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.SamB440.Civilization.Civilization;
import com.SamB440.Civilization.API.data.CivPlayer;
import com.SamB440.Civilization.API.data.SettlementClaim;

import lombok.Getter;

/**
 * @author SamB440
 */
public class BuildTask implements Runnable {
	
	private Civilization plugin;
	@Getter private HashMap<Player, List<Location>> cache = new HashMap<Player, List<Location>>();
	
	public BuildTask(Civilization plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run() {
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			CivPlayer civ = new CivPlayer(plugin, player, true);
			SettlementClaim sc = new SettlementClaim(plugin, player.getTargetBlock(null, 7).getChunk());
			if(civ.isBuilding() && sc.isClaimed())
			{
				if(sc.getOwner().getName().equals(civ.getSettlement().getName()))
				{
					List<Location> locations = civ.getBuilding().pasteSchematic(player.getTargetBlock(null, 7).getLocation().add(0, 1, 0), player, true);
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
						if(cache.containsKey(player))
						{
							if(!cache.get(player).equals(locations))
							{
								for(Location location : cache.get(player))
								{
									if(!locations.contains(location)) Bukkit.getScheduler().runTask(plugin, () -> location.getBlock().getState().update(true, false));
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
}