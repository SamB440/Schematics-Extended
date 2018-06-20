import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;

public class Scheduler {
	
	@Getter @Setter private int task;
	@Setter private boolean cancel;

	public void cancel()
	{
		Bukkit.getScheduler().cancelTask(task);
	}
}