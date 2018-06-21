import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.SamB440.Civilization.Civilization;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

/**
 * @author Jojodmo - Schematic Reader
 * @author SamB440 - Schematic previews, centering and pasting block-by-block
 */
public class Schematic {
	
	private JavaPlugin plugin;
	private File schematic;
	private List<Integer> pastes = new ArrayList<Integer>();
	private int current = 0;
	@Getter @Setter boolean pasted;
	
	public Schematic(JavaPlugin plugin, File schematic)
	{
		this.plugin = plugin;
		this.schematic = schematic;
	}

	@SuppressWarnings("deprecation")
	public List<Location> pasteSchematic(Location loc, Player paster, boolean preview)
	{
		try {
			
			/*
			 * Read the schematic file. Get the width, height, length, blocks, and block data.
			 */
			FileInputStream fis = new FileInputStream(schematic);
			NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);

			short width = nbt.getShort("Width");
			short height = nbt.getShort("Height");
			short length = nbt.getShort("Length");

			byte[] blocks = nbt.getByteArray("Blocks");
			byte[] data = nbt.getByteArray("Data");

			fis.close();

			List<Integer> indexes = new ArrayList<Integer>();
			List<Location> locations = new ArrayList<Location>();
			List<Integer> otherindex = new ArrayList<Integer>();
			List<Location> otherloc = new ArrayList<Location>();
	   
			/*
			 * Loop through all the blocks within schematic size.
			 */
			for(int x = 0; x < width; ++x)
			{
				for(int y = 0; y < height; ++y)
				{
					for(int z = 0; z < length; ++z)
					{
						int index = y * width * length + z * width + x;
						final Location location = new Location(loc.getWorld(), (x + loc.getX()) - (int) width / 2, y + loc.getY(), (z + loc.getZ()) - (int) length / 2);
						
						/*
						 * Ignore blocks that aren't air. Change this if you want the air to destroy blocks too.
						 * Add items to blacklist if you want them placed last, or if they get broken.
						 * IMPORTANT!
						 * Make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted.
						 */
						Material material = Material.getMaterial(blocks[index] & 0xFF);
						List<Material> blacklist = Arrays.asList(Material.STATIONARY_LAVA, 
								Material.STATIONARY_WATER,
								Material.GRASS,
								Material.ARMOR_STAND,
								Material.LONG_GRASS,
								Material.BANNER,
								Material.STANDING_BANNER,
								Material.WALL_BANNER,
								Material.CHORUS_FLOWER,
								Material.CROPS,
								Material.DOUBLE_PLANT,
								Material.CHORUS_PLANT,
								Material.YELLOW_FLOWER,
								Material.TORCH);
						if(material != Material.AIR)
						{
							if(!blacklist.contains(material))
							{
								indexes.add(index);
								locations.add(location);
							} else {
								otherindex.add(index);
								otherloc.add(location);
							}
						}
					}
				}
			}
			
			/*
			 * Make sure liquids are placed last.
			 */
			
			for(Integer index : otherindex)
			{
				indexes.add(index);
			}
			
			otherindex.clear();
			
			for(Location location : otherloc)
			{
				locations.add(location);
			}
			
			otherloc.clear();
			
			/*
			 * ---------------------------
			 * Delete this section of code if you want schematics to be pasted anywhere
			 */
			
			boolean validated = true;
			
			for(Location validate : locations)
			{
				if((validate.getBlock().getType() != Material.AIR || new Location(validate.getWorld(), validate.getX(), paster.getLocation().getY() - 1, validate.getZ()).getBlock().getType() == Material.AIR))
				{
					
					/*
					 * Show fake block where block is interfering with schematic
					 */
					
		            paster.sendBlockChange(validate.getBlock().getLocation(), Material.STAINED_GLASS, (byte) 14);
		            if(!preview) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> validate.getBlock().getState().update(), 60);
		            validated = false;	
		            
				} else {
					
					/*
					 * Show fake block for air
					 */
					
		            paster.sendBlockChange(validate.getBlock().getLocation(), Material.STAINED_GLASS, (byte) 5);
		            if(!preview) Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> validate.getBlock().getState().update(), 60);
		            
				}
			}
			
			if(preview) return locations;
			if(!validated) return null;
			
			/*
			 * ---------------------------
			 */
			
			/*
			 * Start pasting each block every tick
			 */
			Scheduler scheduler = new Scheduler();
			
			scheduler.setTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
				
				/*
				 * Get the block, set the type, data, and then update the state.
				 */
				
				final Block block = locations.get(current).getBlock();
				block.setType(Material.getMaterial(blocks[indexes.get(current)] & 0xFF));
				block.setData(data[indexes.get(current)]);
				block.getState().update();
				
				/*
				 * Play block effects
				 */
				
				block.getLocation().getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 6);
				block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
				
				current++;
				
				if(current >= locations.size() || current >= indexes.size())
				{
					scheduler.cancel();
					pasted = true;
					current = 0;
				}
				
			}, 0, 20));
			
			pastes.add(scheduler.getTask());
			
			return locations;
		} catch(Exception e) {
			e.printStackTrace();
		} return null;
	}
	
	/**
	 * Cancels all current instances of pasting tasks for this schematic.
	 */
	public void cancel()
	{
		for(Integer tasks : pastes)
		{
			Bukkit.getScheduler().cancelTask(tasks);
			pastes.remove(tasks);
		}
	}
}

