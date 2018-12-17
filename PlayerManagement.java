/**
 * @author SamB440
 */
@RequiredArgsConstructor
public class PlayerManagement {
	
	@NonNull
	private JavaPlugin plugin;
	
	private Map<UUID, Schematic> building = new HashMap<>();
	
	public void setBuilding(UUID uuid, Schematic schematic) {
		if (!building.containsKey(uuid)) {
			building.put(uuid, val);
		} else {
		    building.replace(uuid, val);
		}
	}
	
	public boolean isBuilding(UUID uuid) {
		return building.containsKey(uuid);
	}
	
	public Schematic getBuilding(UUID uuid) {
		return building.get(uuid);
	}
}