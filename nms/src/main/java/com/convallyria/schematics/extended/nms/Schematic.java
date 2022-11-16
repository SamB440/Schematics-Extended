package com.convallyria.schematics.extended.nms;

import com.convallyria.schematics.extended.nms.block.NBTBlock;
import com.convallyria.schematics.extended.nms.example.BuildTask;
import com.convallyria.schematics.extended.nms.example.SchematicPlugin;
import com.convallyria.schematics.extended.nms.material.BlockDataMaterial;
import com.convallyria.schematics.extended.nms.material.DirectionalBlockDataMaterial;
import com.convallyria.schematics.extended.nms.material.MultipleFacingBlockDataMaterial;
import com.convallyria.schematics.extended.nms.util.PacketSender;
import me.lucko.helper.scheduler.Task;
import me.lucko.helper.scheduler.builder.TaskBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A utility class that previews and pastes schematics block-by-block with asynchronous support.
 * <br></br>
 * @version 2.0.5
 * @author SamB440 - Schematic previews, centering and pasting block-by-block, class itself
 * @author brainsynder - 1.13+ Palette Schematic Reader
 * @author Math0424 - Rotation calculations
 * @author Jojodmo - Legacy (< 1.12) Schematic Reader
 */
public class Schematic {

    private final SchematicPlugin plugin;
    private final File schematic;

    private short width = 0;
    private short height = 0;
    private short length = 0;

    private byte[] blockDatas;

    private final List<Material> materials = new ArrayList<>();
    private final LinkedHashMap<Vector, NBTBlock> nbtBlocks = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, BlockData> blocks = new LinkedHashMap<>();

    /**
     * @param plugin your plugin instance
     * @param schematic file to the schematic
     */
    public Schematic(final SchematicPlugin plugin, final File schematic) {
        this.plugin = plugin;
        this.schematic = schematic;
    }

    /**
     * Pastes a schematic, with a specified time
     * @param paster player pasting
     * @param time time in ticks to paste blocks
     * @return collection of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     * @throws SchematicNotLoadedException when schematic has not yet been loaded
     * @see #loadSchematic()
     */
    @Nullable
    public Collection<Location> pasteSchematic(final Location loc, final Player paster, final int time, final Options... option) throws SchematicNotLoadedException {
        if (width == 0 || height == 0 || length == 0 || blocks.isEmpty()) {
            throw new SchematicNotLoadedException("Data has not been loaded yet");
        }

        try {
            final List<Options> options = Arrays.asList(option);
            final Data tracker = new Data();

            final LinkedHashMap<Integer, Location> indexLocations = new LinkedHashMap<>();
            final LinkedHashMap<Integer, Location> delayedIndexLocations = new LinkedHashMap<>();

            final LinkedHashMap<Integer, NBTBlock> nbtData = new LinkedHashMap<>();

            final BlockFace face = paster.getFacing();

            /*
             * Loop through all the blocks within schematic size.
             */
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++ y) {
                    for (int z = 0; z < length; ++z) {
                        final int index = y * width * length + z * width + x;
                        final Vector point = new Vector(x, y, z);
                        Location location = null;
                        final int width2 = width / 2;
                        final int length2 = length / 2;
                        switch (face) {
                            case NORTH:
                                location = new Location(loc.getWorld(), (x * - 1 + loc.getBlockX()) + width2, y + loc.getBlockY(), (z + loc.getBlockZ()) + length2);
                                break;
                            case EAST:
                                location = new Location(loc.getWorld(), (-z + loc.getBlockX()) - length2, y + loc.getBlockY(), (-x - 1) + (width + loc.getBlockZ()) - width2);
                                break;
                            case SOUTH:
                                location = new Location(loc.getWorld(), (x + loc.getBlockX()) - width2, y + loc.getBlockY(), (z * - 1 + loc.getBlockZ()) - length2);
                                break;
                            case WEST:
                                location = new Location(loc.getWorld(), (z + loc.getBlockX()) + length2, y + loc.getBlockY(), (x + 1) - (width - loc.getBlockZ()) + width2);
                                break;
                            default:
                                break;
                        }

                        final BlockData data = blocks.get((int) blockDatas[index]);

                        /*
                         * Ignore blocks that aren't air. Change this if you want the air to destroy blocks too.
                         * Add items to delayedBlocks if you want them placed last, or if they get broken.
                         */
                        final Material material = data.getMaterial();
                        if (material != Material.AIR) {
                            NBTMaterial nbtMaterial = NBTMaterial.fromBukkit(material);
                            if (nbtMaterial == null || !nbtMaterial.isDelayed()) {
                                indexLocations.put(index, location);
                            } else {
                                delayedIndexLocations.put(index, location);
                            }
                        }

                        if (nbtBlocks.containsKey(point)) {
                            nbtData.put(index, nbtBlocks.get(point));
                        }
                    }
                }
            }

            // Make sure delayed blocks are placed last
            indexLocations.putAll(delayedIndexLocations);
            delayedIndexLocations.clear();

            /*
             * Verify location of pasting
             */

            boolean validated = true;

            for (final Location validate : indexLocations.values()) {
                final boolean isWater = validate.clone().subtract(0, 1, 0).getBlock().getType() == Material.WATER;
                final boolean isAir = new Location(validate.getWorld(), validate.getX(), loc.getY() - 1, validate.getZ()).getBlock().getType() == Material.AIR;
                final boolean isSolid = validate.getBlock().getType() != Material.AIR;
                final boolean isTransparent = options.contains(Options.IGNORE_TRANSPARENT) && validate.getBlock().isPassable() && validate.getBlock().getType() != Material.AIR;

                if (!options.contains(Options.PLACE_ANYWHERE) && (isWater || isAir || isSolid) && !isTransparent) {
                    // Show fake block where block is interfering with schematic
                    if (options.contains(Options.USE_GAME_MARKER)) {
                        PacketSender.sendBlockHighlight(paster, validate, Color.RED, 100);
                    } else paster.sendBlockChange(validate, Material.RED_STAINED_GLASS.createBlockData());
                    validated = false;
                } else {
                    // Show fake block for air
                    if (options.contains(Options.USE_GAME_MARKER)) {
                        PacketSender.sendBlockHighlight(paster, validate, Color.GREEN, 100);
                    } else paster.sendBlockChange(validate, Material.GREEN_STAINED_GLASS.createBlockData());
                }

                if (!options.contains(Options.PREVIEW) && !options.contains(Options.USE_GAME_MARKER)) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                        if (validate.getBlock().getType() == Material.AIR) paster.sendBlockChange(validate.getBlock().getLocation(), Material.AIR.createBlockData());
                    }, 60);
                }
            }

            if (options.contains(Options.PREVIEW)) return indexLocations.values();
            if (!validated) return null;

            if (options.contains(Options.REALISTIC)) {
                //TODO
            }

            // Start pasting each block every tick
            final AtomicReference<Task> task = new AtomicReference<>();

            tracker.trackCurrentBlock = 0;

            // List of block faces to update *after* the schematic is done pasting.
            final List<Block> toUpdate = new ArrayList<>();
            indexLocations.forEach((index, location) -> {
                final Block block = location.getBlock();
                final BlockData data = blocks.get((int) blockDatas[index]);
                if (Tag.STAIRS.getValues().contains(data.getMaterial()) || Tag.FENCES.getValues().contains(data.getMaterial())) {
                    toUpdate.add(block);
                }
            });

            Runnable pasteTask = () -> {
                // Get the block, set the type, data, and then update the state.
                final List<Location> locations = new ArrayList<>(indexLocations.values());
                final List<Integer> indexes = new ArrayList<>(indexLocations.keySet());

                final Block block = locations.get(tracker.trackCurrentBlock).getBlock();
                final BlockData data = blocks.get((int) blockDatas[indexes.get(tracker.trackCurrentBlock)]);
                block.setType(data.getMaterial(), false);
                block.setBlockData(data);
                if (nbtData.containsKey(indexes.get(tracker.trackCurrentBlock))) {
                    final NBTBlock nbtBlock = nbtData.get(indexes.get(tracker.trackCurrentBlock));
                    try {
                        final BlockState state = block.getState();
                        nbtBlock.setData(state);
                        state.update();
                    } catch (final WrongIdException e) {
                        e.printStackTrace();
                    }
                }

                // Update block faces
                BlockDataMaterial blockDataMaterial = null;
                if (block.getState().getBlockData() instanceof Directional) {
                    blockDataMaterial = new DirectionalBlockDataMaterial(block.getState());
                } else if (block.getState().getBlockData() instanceof MultipleFacing) {
                    blockDataMaterial = new MultipleFacingBlockDataMaterial(block.getState());
                }
                if (blockDataMaterial != null) block.setBlockData(blockDataMaterial.update(face));

                block.getState().update(true, false);

                // Play block effects. Change to what you want.
                block.getLocation().getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 6);
                block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());

                tracker.trackCurrentBlock++;

                if (tracker.trackCurrentBlock >= locations.size() || tracker.trackCurrentBlock >= indexes.size()) {
                    task.get().stop();
                    tracker.trackCurrentBlock = 0;
                    toUpdate.forEach(b -> b.getState().update(true, true));
                }
            };

            task.set(TaskBuilder.newBuilder().sync().every(time).run(pasteTask));
            return indexLocations.values();
        } catch (final Exception e) {
            e.printStackTrace();
        } return null;
    }

    /**
     * Pastes a schematic, with the time defaulting to 1 block per second
     * @param location location to paste from
     * @param paster player pasting
     * @param options options to apply to this paste
     * @return list of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     * @throws SchematicNotLoadedException when schematic has not yet been loaded
     * @see #loadSchematic()
     */
    public Collection<Location> pasteSchematic(final Location location, final Player paster, final Options... options) throws SchematicNotLoadedException {
        return pasteSchematic(location, paster, 20, options);
    }

    /**
     * Creates a constant preview of this schematic for the player
     * @param player player
     */
    public void previewSchematic(Player player) {
        plugin.getPlayerManagement().setBuilding(player.getUniqueId(), this);
        new BuildTask(plugin, player).start();
    }

    /**
     * Loads the schematic file. This should <b>always</b> be used before pasting a schematic.
     * @return schematic (self)
     */
    public Schematic loadSchematic() {

        try {
            // Read the schematic file. Get the width, height, length, blocks, and block data.
            final CompoundTag nbt = NbtIo.readCompressed(schematic);

            width = nbt.getShort("Width");
            height = nbt.getShort("Height");
            length = nbt.getShort("Length");

            blockDatas = nbt.getByteArray("BlockData");

            final CompoundTag palette = nbt.getCompound("Palette");
            final ListTag tiles = (ListTag) nbt.get("BlockEntities");

            // Load NBT data
            if (tiles != null) {
                for (final net.minecraft.nbt.Tag tile : tiles) {
                    if (tile instanceof final CompoundTag compound) {
                        if (compound.isEmpty()) continue;
                        final NBTMaterial nbtMaterial = NBTMaterial.fromTag(compound);
                        if (nbtMaterial == null) continue;
                        final NBTBlock nbtBlock = nbtMaterial.getNbtBlock(compound);
                        if (!nbtBlock.isEmpty()) nbtBlocks.put(nbtBlock.getOffset(), nbtBlock);
                    }
                }
            }

            /*
             * 	Explanation:
             *    The "Palette" is setup like this
             *      "block_data": id (the ID is a Unique ID that WorldEdit gives that
             *                    corresponds to an index in the BlockDatas Array)
             *    So I loop through all the Keys in the "Palette" Compound
             *    and store the custom ID and BlockData in the palette Map
             */
            palette.getAllKeys().forEach(rawState -> {
                final int id = palette.getInt(rawState);
                final BlockData blockData = Bukkit.createBlockData(rawState);
                blocks.put(id, blockData);
            });

            // Load all material types - need to do more caching here sometime todo
            for (byte blockData : blockDatas) {
                final BlockData data = blocks.get((int) blockData);
                materials.add(data.getMaterial());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Returns the palette key from the schematic file.
     * The key is the unique id that corresponds to an index in the BlockData array.
     *
     * Note that the palette does not contain every block present in the schematic,
     * it is only a "preview" of what kind of blocks the schematic contains.
     *
     * If you are looking to read the material count and type from the schematic,
     * use {@link #getSchematicMaterialData()}.
     *
     * @see #getMaterials()
     * @return a linked hashmap by the unique id of the block in the palette key
     */
    public LinkedHashMap<Integer, BlockData> getPalette() {
        return blocks;
    }

    /**
     * Returns a list containing every material-block in the schematic.
     * @return list of every material-block in the schematic, with duplicates
     */
    public List<Material> getMaterials() {
        return materials;
    }

    /**
     * Returns a material-count map of the materials present in this schematic.
     *
     * The key corresponds to the Bukkit {@link Material} and the value is the
     * amount present in the schematic.
     *
     * @return material-count map of materials in the schematic
     */
    public Map<Material, Integer> getSchematicMaterialData() {
        Map<Material, Integer> materialValuesMap = new HashMap<>();
        for (Material material : materials) {
            int count = materialValuesMap.getOrDefault(material, 0);
            materialValuesMap.put(material, count + 1);
        }
        return materialValuesMap;
    }

    /**
     * Hacky method to avoid "final".
     */
    protected static class Data {
        int trackCurrentBlock;
    }

    /**
     * An enum of options to apply whilst previewing/pasting a schematic.
     */
    public enum Options {
        /**
         * Previews schematic
         */
        PREVIEW,
        /**
         * A realistic building method. Builds from the ground up, instead of in the default slices.
         * <hr></hr>
         * <b>*WIP, CURRENTLY DOES NOTHING*</b>
         * @deprecated does nothing
         */
        @Deprecated
        REALISTIC,
        /**
         * Bypasses the verification check and allows placing anywhere.
         */
        PLACE_ANYWHERE,
        /**
         * Ignores transparent blocks in the placement check
         */
        IGNORE_TRANSPARENT,
        /**
         * Instead of blocks, uses the game marker API by Mojang to show valid build areas.
         * <hr></hr>
         * Please note that in 1.17 Mojang unintentionally broke the RGB functionality, and anything that is not of the
         * {@link Color#GREEN} type will display as black. This can be fixed using a ResourcePack, described here:
         * <a href="https://bugs.mojang.com/browse/MC-234030">https://bugs.mojang.com/browse/MC-234030</a>
         */
        USE_GAME_MARKER
    }
}