package com.convallyria.schematics.extended.worldedit.example;

import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class SchematicPlugin extends ExtendedJavaPlugin {

    public PlayerManagement getPlayerManagement() {
        return playerManagement;
    }

    private PlayerManagement playerManagement;

    @Override
    public void enable() {
        this.saveResource("schematics/example.schem", false);
        this.playerManagement = new PlayerManagement(this);
        registerListeners();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new SchematicListener(this), this);
    }
}
