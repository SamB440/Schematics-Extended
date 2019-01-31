package net.islandearth.schematics.extended;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;

/**
 * Utility class that stores scheduler information.
 * @author SamB440
 */
public class Scheduler {
	
	@Getter 
	@Setter 
	private int task;

	public void cancel() {
		Bukkit.getScheduler().cancelTask(task);
	}
}