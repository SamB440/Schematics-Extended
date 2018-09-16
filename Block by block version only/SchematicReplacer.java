import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_13_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

/**
 * @author Jojodmo - Legacy Schematic Reader
 * @author brainsynder - 1.13 Schematic Reader
 * @author Math0424 - Rotation calculations
 * @author SamB440 - Schematic previews, centering and pasting block-by-block
 */
public class Schematic {
	
	private JavaPlugin plugin;
	private File schematic;
	private List<Integer> pastes = new ArrayList<Integer>();
	private int current = 0;
	@Getter @Setter private boolean pasted;
	
	public Schematic(JavaPlugin plugin, File schematic)
	{
		this.plugin = plugin;
		this.schematic = schematic;
	}

	public List<Location> pasteSchematic(Location loc, Player paster, boolean preview, int time)
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

			byte[] blockDatas = nbt.getByteArray("BlockData");
			NBTTagCompound palette = nbt.getCompound("Palette");
			
			Map<Integer, BlockData> blocks = new HashMap<>();
			
			/*
			 * 	Explanation: 
			 *    The "Palette" is setup like this
			 *      "block_data": id (the ID is a Unique ID that WorldEdit gives that corresponds to an index in the BlockDatas Array)
			 *    So I loop through all the Keys in the "Palette" Compound
			 *    and store the custom ID and BlockData in the palette Map
			 */
			
			palette.getKeys().forEach(rawState -> {
				int id = palette.getInt(rawState);
				BlockData blockData = Bukkit.createBlockData(rawState);
				blocks.put(id, blockData);
			});

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
						
						Location location = null;
						
						//final Location location = new Location(loc.getWorld(), (x + loc.getX()) - (int) width / 2, y + paster.getLocation().getY(), (z + loc.getZ()) - (int) length / 2);
						switch(getDirection(paster))
						{
							case NORTH:
								location = new Location(loc.getWorld(), (x * - 1 + loc.getX()) + (int) width / 2, y + paster.getLocation().getY(), (z + loc.getZ()) + (int) length / 2);
								break;
							case EAST:
								location = new Location(loc.getWorld(), (-z + loc.getX()) - (int) length / 2, y + paster.getLocation().getY(), (-x - 1) + (width + loc.getZ()) - (int) width / 2);
								break;
							case SOUTH:
								location = new Location(loc.getWorld(), (x + loc.getX()) - (int) width / 2, y + paster.getLocation().getY(), (z * - 1 + loc.getZ()) - (int) length / 2);
								break;
							case WEST:
								location = new Location(loc.getWorld(), (z + loc.getX()) + (int) length / 2, y + paster.getLocation().getY(), (x + 1) - (width - loc.getZ()) + (int) width / 2);
								break;
							default:
								break;
						}
						
						BlockData data = blocks.get((int) blockDatas[index]);
						
						/*
						 * Ignore blocks that aren't air. Change this if you want the air to destroy blocks too.
						 * Add items to blacklist if you want them placed last, or if they get broken.
						 */
						Material material = data.getMaterial();
						List<Material> blacklist = Arrays.asList(Material.LAVA, 
								Material.WATER,
								Material.GRASS,
								Material.ARMOR_STAND,
								Material.TALL_GRASS,
								Material.BLACK_BANNER,
								Material.BLACK_WALL_BANNER,
								Material.BLUE_BANNER,
								Material.BLUE_WALL_BANNER,
								Material.BROWN_BANNER,
								Material.BROWN_WALL_BANNER,
								Material.CYAN_BANNER,
								Material.CYAN_WALL_BANNER,
								Material.GRAY_BANNER,
								Material.GRAY_WALL_BANNER,
								Material.GREEN_BANNER,
								Material.GREEN_WALL_BANNER,
								Material.LIGHT_BLUE_BANNER,
								Material.LIGHT_BLUE_WALL_BANNER,
								Material.LIGHT_GRAY_BANNER,
								Material.LIGHT_GRAY_WALL_BANNER,
								Material.LIME_BANNER,
								Material.LIME_WALL_BANNER,
								Material.MAGENTA_BANNER,
								Material.MAGENTA_WALL_BANNER,
								Material.ORANGE_BANNER,
								Material.ORANGE_WALL_BANNER,
								Material.PINK_BANNER,
								Material.PINK_WALL_BANNER,
								Material.PURPLE_BANNER,
								Material.PURPLE_WALL_BANNER,
								Material.RED_BANNER,
								Material.RED_WALL_BANNER,
								Material.WHITE_BANNER,
								Material.WHITE_WALL_BANNER,
								Material.YELLOW_BANNER,
								Material.YELLOW_WALL_BANNER,
								
								Material.GRASS,
								Material.TALL_GRASS,
								Material.SEAGRASS,
								Material.TALL_SEAGRASS,
								Material.FLOWER_POT,
								Material.SUNFLOWER,
								Material.CHORUS_FLOWER,
								Material.OXEYE_DAISY,
								Material.DEAD_BUSH,
								Material.FERN,
								Material.DANDELION,
								Material.POPPY,
								Material.BLUE_ORCHID,
								Material.ALLIUM,
								Material.AZURE_BLUET,
								Material.RED_TULIP,
								Material.ORANGE_TULIP,
								Material.WHITE_TULIP,
								Material.PINK_TULIP,
								Material.BROWN_MUSHROOM,
								Material.RED_MUSHROOM,
								Material.END_ROD,
								Material.ROSE_BUSH,
								Material.PEONY,
								Material.LARGE_FERN,
								Material.REDSTONE,
								Material.REPEATER,
								Material.COMPARATOR,
								Material.LEVER,
								Material.SEA_PICKLE,
								Material.SUGAR_CANE,
								Material.FIRE,
								Material.WHEAT,
								Material.WHEAT_SEEDS,
								Material.CARROTS,
								Material.BEETROOT,
								Material.BEETROOT_SEEDS,
								Material.MELON,
								Material.MELON_STEM,
								Material.MELON_SEEDS,
								Material.POTATOES,
								Material.PUMPKIN,
								Material.PUMPKIN_STEM,
								Material.PUMPKIN_SEEDS);
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
			 * Start pasting each block every tick
			 */
			Scheduler scheduler = new Scheduler();
			
			scheduler.setTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
				
				/*
				 * Get the block, set the type, data, and then update the state.
				 */
				
				final Block block = locations.get(current).getBlock();
				BlockData data = blocks.get((int) blockDatas[indexes.get(current)]);
				block.setType(data.getMaterial());
				block.setBlockData(data);
				block.getState().update(true, false);
				
				/*
				 * Play block effects
				 */
				
				block.getLocation().getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 6);
				block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
				
				current++;
				
				if(current >= locations.size() || current >= indexes.size())
				{
					scheduler.cancel();
					pasted = true;
					current = 0;
				}
				
			}, 0, time));
			
			pastes.add(scheduler.getTask());
			
			return locations;
		} catch(Exception e) {
			e.printStackTrace();
		} return null;
	}
	
	public List<Location> pasteSchematic(Location loc, Player paster, boolean preview)
	{
		return pasteSchematic(loc, paster, preview, 20);
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
	
	private BlockFace getDirection(Player player) 
	{
		float yaw = player.getLocation().getYaw();
		if(yaw < 0) 
		{
			yaw += 360;
		}
		
		if(yaw >= 315 || yaw < 45) 
		{
			return BlockFace.SOUTH;
		} else if(yaw < 135) {
			return BlockFace.WEST;
		} else if(yaw < 225) {
			return BlockFace.NORTH;
		} else if(yaw < 315) {
			return BlockFace.EAST;
		} return BlockFace.NORTH;
	}
}