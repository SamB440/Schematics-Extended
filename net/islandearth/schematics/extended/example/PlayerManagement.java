package net.islandearth.schematics.extended.example;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.islandearth.schematics.extended.Schematic;

/**
 * Utility class to handle player's previews
 * @author SamB440
 */
@RequiredArgsConstructor
public class PlayerManagement {
	
	@NonNull
	private JavaPlugin plugin;
	
	private Map<UUID, Schematic> building = new HashMap<>();
	
	public void setBuilding(UUID uuid, Schematic schematic) {
		if (!building.containsKey(uuid)) {
			building.put(uuid, schematic);
		} else {
		    building.replace(uuid, schematic);
		}
	}
	
	public boolean isBuilding(UUID uuid) {
		return building.containsKey(uuid);
	}
	
	public Schematic getBuilding(UUID uuid) {
		return building.get(uuid);
	}
	
	public void removeBuilding(UUID uuid) {
		building.remove(uuid);
	}
}