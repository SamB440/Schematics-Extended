package com.convallyria.schematics.extended.worldedit.example;

import com.convallyria.schematics.extended.worldedit.Schematic;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to handle player's previews
 * @author SamB440
 */
public class PlayerManagement {

    private final JavaPlugin plugin;

    private final Map<UUID, Schematic> building = new HashMap<>();

    public PlayerManagement(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setBuilding(final UUID uuid, final Schematic schematic) {
        if (!building.containsKey(uuid)) {
            building.put(uuid, schematic);
        } else {
            building.replace(uuid, schematic);
        }
    }

    public boolean isBuilding(final UUID uuid) {
        return building.containsKey(uuid);
    }

    public Schematic getBuilding(final UUID uuid) {
        return building.get(uuid);
    }

    public void removeBuilding(final UUID uuid) {
        building.remove(uuid);
    }
}