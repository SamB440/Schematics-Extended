package net.islandearth.schematics.extended.example;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class SchematicPlugin extends JavaPlugin {
	
	@Getter
	private PlayerManagement playerManagement;
	
	@Override
	public void onEnable() {
		this.playerManagement = new PlayerManagement(this);
		registerListeners();
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new SchematicListener(this), this);
	}
}
