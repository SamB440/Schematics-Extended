package net.islandearth.schematics.extended;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.islandearth.schematics.extended.NBTUtils.Position;
import net.islandearth.schematics.extended.example.BuildTask;
import net.islandearth.schematics.extended.example.SchematicPlugin;
import net.minecraft.server.v1_13_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagList;

/**
 * A utility class that previews and pastes schematics block-by-block with asynchronous support.
 * <br></br>
 * <b>License:</b> 
 * 	<a href="https://gitlab.com/SamB440/Schematics-Extended/blob/master/LICENSE">https://gitlab.com/SamB440/Schematics-Extended/blob/master/LICENSE</a>
 * @version 2.0.2
 * @author SamB440 - Schematic previews, centering and pasting block-by-block, class itself
 * @author brainsynder - 1.13 Palette Schematic Reader
 * @author Math0424 - Rotation calculations
 * @author Jojodmo - Legacy (< 1.12) Schematic Reader
 */
public class Schematic {
	
	private SchematicPlugin plugin;
	private File schematic;
	
	private short width = 0;
	private short height = 0;
	private short length = 0;
	
	private byte[] blockDatas;
	
	private Map<Vector, List<String>> signs = new HashMap<>();
	private Map<Vector, Map<Integer, ItemStack>> chests = null;
	private Map<Integer, BlockData> blocks = new HashMap<>();
	
	private List<Material> delayedBlocks;
	
	/**
	 * @param plugin - your plugin instance
	 * @param schematic - file to the schematic
	 */
	public Schematic(SchematicPlugin plugin, File schematic) {
		this.plugin = plugin;
		this.schematic = schematic;
		this.delayedBlocks = Arrays.asList(Material.LAVA, 
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
	}
	
	/**
	 * Pastes a schematic, with a specified time
	 * @param location - location to paste from
	 * @param paster - player pasting
	 * @param time - time in ticks to paste blocks
	 * @param options - options to apply to this paste
	 * @return list of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
	 * @throws SchematicNotLoadedException
	 */
	public List<Location> pasteSchematic(Location loc, 
			Player paster, 
			int time,
			Options... option) throws SchematicNotLoadedException {
		try {
			
			if (width == 0 
					|| height == 0 
					|| length == 0 
					|| blocks.isEmpty()) {
				throw new SchematicNotLoadedException("Data has not been loaded yet");
			}
			
			List<Options> options = Arrays.asList(option);
			Data tracker = new Data();

			List<Integer> indexes = new ArrayList<>();
			List<Location> locations = new ArrayList<>();
			List<Integer> otherindex = new ArrayList<>();
			List<Location> otherloc = new ArrayList<>();
			
			Map<Integer, Object> nbtData = new HashMap<>();
			
			BlockFace face = getDirection(paster);
	   
			/*
			 * Loop through all the blocks within schematic size.
			 */
			for(int x = 0; x < width; ++x) {
				for(int y = 0; y < height; ++y) {
					for(int z = 0; z < length; ++z) {
						int index = y * width * length + z * width + x;
						Vector point = new Vector(x, y, z);
						Location location = null;
						
						//final Location location = new Location(loc.getWorld(), (x + loc.getX()) - (int) width / 2, y + paster.getLocation().getY(), (z + loc.getZ()) - (int) length / 2);
						switch (face) {
							case NORTH:
								location = new Location(loc.getWorld(), (x * - 1 + loc.getX()) + (int) width / 2, y + loc.getY(), (z + loc.getZ()) + (int) length / 2);
								break;
							case EAST:
								location = new Location(loc.getWorld(), (-z + loc.getX()) - (int) length / 2, y + loc.getY(), (-x - 1) + (width + loc.getZ()) - (int) width / 2);
								break;
							case SOUTH:
								location = new Location(loc.getWorld(), (x + loc.getX()) - (int) width / 2, y + loc.getY(), (z * - 1 + loc.getZ()) - (int) length / 2);
								break;
							case WEST:
								location = new Location(loc.getWorld(), (z + loc.getX()) + (int) length / 2, y + loc.getY(), (x + 1) - (width - loc.getZ()) + (int) width / 2);
								break;
							default:
								break;
						}
						
						BlockData data = blocks.get((int) blockDatas[index]);
						
						/*
						 * Ignore blocks that aren't air. Change this if you want the air to destroy blocks too.
						 * Add items to delayedBlocks if you want them placed last, or if they get broken.
						 */
						Material material = data.getMaterial();
						if (material != Material.AIR) {
							if (!delayedBlocks.contains(material)) {
								indexes.add(index);
								locations.add(location);
							} else {
								otherindex.add(index);
								otherloc.add(location);
							}
						}
						
						if (signs.containsKey(point)) {
							nbtData.put(index, signs.get(point));
						}
						
						if (chests != null) {
							if (chests.containsKey(point)) {
								nbtData.put(index, chests.get(point));
							}
						}
					}
				}
			}
			
			/*
			 * Make sure liquids are placed last.
			 */
			
			for (Integer index : otherindex) {
				indexes.add(index);
			}
			
			otherindex.clear();
			
			for (Location location : otherloc) {
				locations.add(location);
			}
			
			otherloc.clear();
			
			/*
			 * ---------------------------
			 * Delete this section of code if you want schematics to be pasted anywhere
			 */
			
			boolean validated = true;
			
			for (Location validate : locations) {
				if ((validate.getBlock().getType() != Material.AIR || validate.clone().subtract(0, 1, 0).getBlock().getType() == Material.WATER) || new Location(validate.getWorld(), validate.getX(), loc.getY() - 1, validate.getZ()).getBlock().getType() == Material.AIR) {
					/*
					 * Show fake block where block is interfering with schematic
					 */
					
		            paster.sendBlockChange(validate.getBlock().getLocation(), Material.RED_STAINED_GLASS.createBlockData());
		            if (!options.contains(Options.PREVIEW)) {
			            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			            	if (validate.getBlock().getType() == Material.AIR) paster.sendBlockChange(validate.getBlock().getLocation(), Material.AIR.createBlockData());
			            }, 60);
		            }
		            validated = false;
				} else {
					/*
					 * Show fake block for air
					 */
					
		            paster.sendBlockChange(validate.getBlock().getLocation(), Material.GREEN_STAINED_GLASS.createBlockData());
		            if (!options.contains(Options.PREVIEW)) {
			            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			            	if (validate.getBlock().getType() == Material.AIR) paster.sendBlockChange(validate.getBlock().getLocation(), Material.AIR.createBlockData());
			            }, 60);
		            }
				}
			}
			
			if (options.contains(Options.PREVIEW)) return locations;
			if (!validated) return null;
			
			/*
			 * ---------------------------
			 */
			
			/*
			 * Start pasting each block every tick
			 */
			Scheduler scheduler = new Scheduler();
			
			tracker.trackCurrentBlock = 0;
			
			List<Material> validData = new ArrayList<>();
			validData.add(Material.LADDER);
			validData.add(Material.TORCH);
			validData.add(Material.CHEST);
			validData.add(Material.SIGN);
			validData.add(Material.BLACK_STAINED_GLASS_PANE);
			validData.add(Material.BLUE_STAINED_GLASS_PANE);
			validData.add(Material.BROWN_STAINED_GLASS_PANE);
			validData.add(Material.CYAN_STAINED_GLASS_PANE);
			validData.add(Material.GLASS_PANE);
			validData.add(Material.WHITE_STAINED_GLASS_PANE);
			validData.add(Material.GREEN_STAINED_GLASS_PANE);
			validData.add(Material.GRAY_STAINED_GLASS_PANE);
			validData.add(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
			validData.add(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
			validData.add(Material.LIME_STAINED_GLASS_PANE);
			validData.add(Material.MAGENTA_STAINED_GLASS_PANE);
			validData.add(Material.ORANGE_STAINED_GLASS_PANE);
			validData.add(Material.PINK_STAINED_GLASS_PANE);
			validData.add(Material.PURPLE_STAINED_GLASS_PANE);
			validData.add(Material.RED_STAINED_GLASS_PANE);
			validData.add(Material.YELLOW_STAINED_GLASS_PANE);
			validData.add(Material.COBBLESTONE_WALL);
			validData.add(Material.ACACIA_FENCE);
			validData.add(Material.ACACIA_FENCE_GATE);
			validData.add(Material.BIRCH_FENCE);
			validData.add(Material.BIRCH_FENCE_GATE);
			validData.add(Material.DARK_OAK_FENCE);
			validData.add(Material.DARK_OAK_FENCE_GATE);
			validData.add(Material.JUNGLE_FENCE);
			validData.add(Material.JUNGLE_FENCE_GATE);
			validData.add(Material.NETHER_BRICK_FENCE);
			validData.add(Material.OAK_FENCE);
			validData.add(Material.OAK_FENCE_GATE);
			validData.add(Material.SPRUCE_FENCE);
			validData.add(Material.SPRUCE_FENCE_GATE);
			
			for (Material material : org.bukkit.Tag.BANNERS.getValues()) {
				validData.add(material);
			}
			
			for (Material material : org.bukkit.Tag.STAIRS.getValues()) {
				validData.add(material);
			}
			
			scheduler.setTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
				
				/*
				 * Get the block, set the type, data, and then update the state.
				 */
				
				Block block = locations.get(tracker.trackCurrentBlock).getBlock();
				BlockData data = blocks.get((int) blockDatas[indexes.get(tracker.trackCurrentBlock)]);
				block.setType(data.getMaterial());
				block.setBlockData(data);
				
				switch (data.getMaterial()) {
					case SIGN: {
						Sign signData = (Sign) data;
						block.setBlockData(signData);
						
						if (nbtData.containsKey(indexes.get(tracker.trackCurrentBlock))) {
							
							@SuppressWarnings("unchecked")
							List<String> lines = (List<String>) nbtData.get(indexes.get(tracker.trackCurrentBlock));
							
							org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
							sign.setLine(0, lines.get(0));
							if (lines.size() >= 2) sign.setLine(1, lines.get(1));
							if (lines.size() >= 3) sign.setLine(2, lines.get(2));
							if (lines.size() >= 4) sign.setLine(3, lines.get(3));
							sign.update();
						}
						
						break;
					}
					
					case WALL_SIGN: {
						WallSign signData = (WallSign) data;
						block.setBlockData(signData);
						
						if (nbtData.containsKey(indexes.get(tracker.trackCurrentBlock))) {
							
							@SuppressWarnings("unchecked")
							List<String> lines = (List<String>) nbtData.get(indexes.get(tracker.trackCurrentBlock));
							
							org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
							sign.setLine(0, lines.get(0));
							if (lines.size() >= 2) sign.setLine(1, lines.get(1));
							if (lines.size() >= 3) sign.setLine(2, lines.get(2));
							if (lines.size() >= 4) sign.setLine(3, lines.get(3));
							sign.update();
						}
						
						break;
					}
					
					case CHEST:
					case TRAPPED_CHEST: {
						Chest chestData = (Chest) data;
						block.setBlockData(chestData);
						
						if (nbtData.containsKey(indexes.get(tracker.trackCurrentBlock))) {
							
							@SuppressWarnings("unchecked")
							Map<Integer, ItemStack> items = (Map<Integer, ItemStack>) nbtData.get(indexes.get(tracker.trackCurrentBlock));
							org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
							for (Integer location : items.keySet()) {
								chest.getBlockInventory().setItem(location, items.get(location));
							}
						}
						
						break;
					}
					
					default: {
						break;
					}
				}
				
				block.getState().update(true, false);
				
				if (validData.contains(data.getMaterial())) {
					Directional facing = (Directional) block.getState().getBlockData();
					switch (face) {
						case NORTH:
							switch (facing.getFacing()) {
								case NORTH:
									facing.setFacing(BlockFace.NORTH);
									break;
								case SOUTH:
									facing.setFacing(BlockFace.SOUTH);
									break;
								case EAST:
									facing.setFacing(BlockFace.WEST);
									break;
								case WEST:
									facing.setFacing(BlockFace.EAST);
									break;
								default:
									break;
							}
							
							break;
						case EAST:
							switch (facing.getFacing()) {
								case NORTH:
									facing.setFacing(BlockFace.EAST);
									break;
								case SOUTH:
									facing.setFacing(BlockFace.WEST);
									break;
								case EAST:
									facing.setFacing(BlockFace.NORTH);
									break;
								case WEST:
									facing.setFacing(BlockFace.SOUTH);
									break;
								default:
									break;
							}
							
							break;
						case SOUTH:
							switch (facing.getFacing()) {
								case NORTH:
									facing.setFacing(BlockFace.SOUTH);
									break;
								case SOUTH:
									facing.setFacing(BlockFace.NORTH);
									break;
								case EAST:
									facing.setFacing(BlockFace.EAST);
									break;
								case WEST:
									facing.setFacing(BlockFace.WEST);
									break;
								default:
									break;
							}
							
							break;
						case WEST:
							switch (facing.getFacing()) {
								case NORTH:
									facing.setFacing(BlockFace.WEST);
									break;
								case SOUTH:
									facing.setFacing(BlockFace.EAST);
									break;
								case EAST:
									facing.setFacing(BlockFace.SOUTH);
									break;
								case WEST:
									facing.setFacing(BlockFace.NORTH);
									break;
								default:
									break;
							}
							
							break;
						default:
							break;
					} block.setBlockData(facing);
				}
				
				block.getState().update(true, false);
				
				/*
				 * Play block effects, change to what you want
				 */
				
				block.getLocation().getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 6);
				block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
				
				tracker.trackCurrentBlock++;
				
				if (tracker.trackCurrentBlock >= locations.size() || tracker.trackCurrentBlock >= indexes.size()) {
					scheduler.cancel();
					tracker.trackCurrentBlock = 0;
				}
				
			}, 0, time));
			
			return locations;
		} catch (Exception e) {
			e.printStackTrace();
		} return null;
	}
	
	/**
	 * Pastes a schematic, with the time defaulting to 1 block per second
	 * @param location - location to paste from
	 * @param paster - player pasting
	 * @param options - options to apply to this paste
	 * @return list of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
	 * @throws SchematicNotLoadedException
	 */
	public List<Location> pasteSchematic(Location location, Player paster, Options... options) throws SchematicNotLoadedException {
		return pasteSchematic(location, paster, 20, options);
	}
	
	/**
	 * Creates a constant preview of this schematic for the player
	 * @param player - player
	 */
	public void previewSchematic(Player player) {
		plugin.getPlayerManagement().setBuilding(player.getUniqueId(), this);
		new BuildTask(plugin, player).start();
	}
	
	/**
	 * Loads the schematic file. This should <b>always</b> be used before pasting a schematic.
	 * @return schematic
	 */
	public Schematic loadSchematic() {
		
		try {
			Data tracker = new Data();
			
			/*
			 * Read the schematic file. Get the width, height, length, blocks, and block data.
			 */
			
			FileInputStream fis = new FileInputStream(schematic);
			NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
			
			width = nbt.getShort("Width");
			height = nbt.getShort("Height");
			length = nbt.getShort("Length");

			blockDatas = nbt.getByteArray("BlockData");
			
			NBTTagCompound palette = nbt.getCompound("Palette");
			NBTTagList tiles = (NBTTagList) nbt.get("BlockEntities");
			tracker.trackCurrentTile = 0;
			
			/*
			 * Load NBT data
			 */
			tiles.forEach(a -> {
				if (!tiles.getCompound(tracker.trackCurrentTile).isEmpty()) {
					NBTTagCompound c = tiles.getCompound(tracker.trackCurrentTile);
					
					if (EnumUtils.isValidEnum(NBTMaterial.class, c.getString("Id"))) {
						switch (NBTMaterial.valueOf(c.getString("Id").
								replace("minecraft:", "").
								toUpperCase())) {
							case SIGN: {
								try {
									List<String> lines = new ArrayList<>();
									lines.add(NBTUtils.getSignLineFromNBT(c, Position.TEXT_ONE));
									lines.add(NBTUtils.getSignLineFromNBT(c, Position.TEXT_TWO));
									lines.add(NBTUtils.getSignLineFromNBT(c, Position.TEXT_THREE));
									lines.add(NBTUtils.getSignLineFromNBT(c, Position.TEXT_FOUR));
									
									int[] pos = c.getIntArray("Pos");
									if (!lines.isEmpty()) signs.put(new Vector(pos[0], pos[1], pos[2]), lines);
									tiles.remove(tracker.trackCurrentTile);
								} catch (WrongIdException e) {
									//it wasn't a sign
								}
								
								break;
							}
							
							case BANNER: {
								//no more data to pull
								break;
							}
							
							default: {
								break;
							}
						}
					}
				} tracker.trackCurrentTile = tracker.trackCurrentTile + 1;
			});
			
			try {
				chests = NBTUtils.getItemsFromNBT(tiles);
			} catch (WrongIdException e) {
				//it wasn't a chest
			}
			
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	/**
	 * @param player
	 * @return blockface of cardinal direction player is facing
	 */
	private BlockFace getDirection(Player player) {
		float yaw = player.getLocation().getYaw();
		if (yaw < 0) {
			yaw += 360;
		}
		
		if (yaw >= 315 || yaw < 45) {
			return BlockFace.SOUTH;
		} else if(yaw < 135) {
			return BlockFace.WEST;
		} else if(yaw < 225) {
			return BlockFace.NORTH;
		} else if(yaw < 315) {
			return BlockFace.EAST;
		} return BlockFace.NORTH;
	}
	
	/**
	 * Hacky method to avoid "final".
	 */
	protected class Data {
		public int trackCurrentTile;
		public int trackCurrentBlock;
	}
	
	/**
	 * An enum of options to apply whilst previewing/pasting a schematic.
	 */
	public enum Options {
		PREVIEW
	}
}