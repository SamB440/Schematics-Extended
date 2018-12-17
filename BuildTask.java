/**
 * @author SamB440
 */
@RequiredArgsConstructor
public class BuildTask {
	
	@NonNull
	private MyPlugin plugin;
	
	@NonNull
	private Player player;
	
	private Scheduler scheduler;
	
	@Getter 
	private Map<Player, List<Location>> cache = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	public BuildTask start() {
	    PlayerManagement pm = plugin.getPlayerManagement();
		scheduler = new Scheduler();
		scheduler.setTask(
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
			if (pm.isBuilding(player.getUniqueId())) {
				Schematic schematic = civ.getBuilding();
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
					if(!cache.containsKey(player)) cache.put(player, locations);
					}, 2L);
				} catch (SchematicNotLoadedException e) {
					e.printStackTrace();
				}
			} else if (!pm.isBuilding(player.getUniqueId())) {
				scheduler.cancel();
			}
		}, 5L, 1L));
		
		return this;
	}
}