package net.islandearth.schematics.extended.example;

import net.islandearth.schematics.extended.Schematic;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to handle player's previews
 * @author SamB440
 */
public class PlayerManagement {

    private JavaPlugin plugin;

    private Map<UUID, Schematic> building = new HashMap<>();

    public PlayerManagement(JavaPlugin plugin) {
        this.plugin = plugin;
    }

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