package com.convallyria.schematics.extended;

import com.convallyria.schematics.extended.example.BuildTask;
import com.convallyria.schematics.extended.example.SchematicPlugin;
import com.convallyria.schematics.extended.util.MathsUtil;
import com.convallyria.schematics.extended.util.PacketSender;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.world.block.BaseBlock;
import me.lucko.helper.scheduler.Task;
import me.lucko.helper.scheduler.builder.TaskBuilder;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private Clipboard clipboard;

    /**
     * @param plugin your plugin instance
     * @param schematic file to the schematic
     */
    public Schematic(final SchematicPlugin plugin, final File schematic) {
        this.schematic = schematic;
        this.plugin = plugin;

    }

    /**
     * Pastes a schematic, with a specified time
     * @param paster player pasting
     * @param time time in ticks to paste blocks
     * @return collection of locations where schematic blocks will be pasted, null if schematic locations will replace blocks
     */
    @Nullable
    public Collection<Location> pasteSchematic(final Location loc, final Player paster, final int time, final Options... option) {
        final BlockFace facing = paster.getFacing();
        final Map<Location, BaseBlock> blocks = new ConcurrentHashMap<>(); //TODO cache
        try {
            final List<Options> options = Arrays.asList(option);
            final Data tracker = new Data();
            try (FileInputStream inputStream = new FileInputStream(schematic)) {
                ClipboardFormat format = ClipboardFormats.findByFile(schematic);
                ClipboardReader reader = format.getReader(inputStream);
                this.clipboard = reader.read();

                // Rotate based off the player's facing direction
                double yaw = paster.getEyeLocation().getYaw();
                // So unfortunately, WorldEdit doesn't support anything other than multiples of 90.
                // Here we round it to the nearest multiple of 90.
                yaw = MathsUtil.roundHalfUp((int) yaw, 90);
                // Apply the rotation to the clipboard
                this.clipboard = clipboard.transform(new AffineTransform().rotateY(yaw));

                // Get all blocks in the schematic
                final BlockVector3 minimumPoint = clipboard.getMinimumPoint();
                final BlockVector3 maximumPoint = clipboard.getMaximumPoint();
                final int minX = minimumPoint.getX();
                final int maxX = maximumPoint.getX();
                final int minY = minimumPoint.getY();
                final int maxY = maximumPoint.getY();
                final int minZ = minimumPoint.getZ();
                final int maxZ = maximumPoint.getZ();

                final int width = clipboard.getRegion().getWidth();
                final int length = clipboard.getRegion().getLength();
                final int widthCentre = width / 2;
                final int lengthCentre = length / 2;

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            final BlockVector3 at = BlockVector3.at(x, y, z);
                            BaseBlock block = clipboard.getFullBlock(at);

                            // Ignore air blocks, change if you want
                            if (block.getBlockType().getMaterial().isAir()) continue;

                            // Here we find the relative offset based off the current location.
                            final double offsetX = Math.abs(maxX - x);
                            final double offsetY = Math.abs(maxY - y);
                            final double offsetZ = Math.abs(maxZ - z);

                            blocks.put(loc.clone().subtract(offsetX - widthCentre, offsetY, offsetZ - lengthCentre), block);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TODO parse the blocks with delaying their pasting etc, rotate

            /*
             * Verify location of pasting
             */

            //TODO verify the blocks
            blocks.forEach(((location, baseBlock) -> {
                if (options.contains(Options.USE_GAME_MARKER)) {
                    PacketSender.sendBlockHighlight(paster, location, Color.GREEN, 51);
                }
            }));

            boolean validated = true;

            if (options.contains(Options.PREVIEW)) return new ArrayList<>();
            if (!validated) return null;

            if (options.contains(Options.REALISTIC)) {
                //TODO
            }

            // Start pasting each block every tick
            final AtomicReference<Task> task = new AtomicReference<>();

            tracker.trackCurrentBlock = 0;

            Runnable pasteTask = () -> {
                // Get the block, set the type, data, and then update the state.
                Location key = (Location) blocks.keySet().toArray()[tracker.trackCurrentBlock];
                final BlockData data = BukkitAdapter.adapt(blocks.get(key));
                final Block block = key.getBlock();
                System.out.println(key + ":" + data.getMaterial());
                block.setType(data.getMaterial(), false);
                block.setBlockData(data);

                block.getState().update(true, false);

                // Play block effects. Change to what you want.
                block.getLocation().getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 6);
                block.getLocation().getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());

                tracker.trackCurrentBlock++;

                if (tracker.trackCurrentBlock >= blocks.size()) {
                    task.get().stop();
                    tracker.trackCurrentBlock = 0;
                }
            };

            task.set(TaskBuilder.newBuilder().sync().every(time).run(pasteTask));
            return blocks.keySet();
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
     */
    public Collection<Location> pasteSchematic(final Location location, final Player paster, final Options... options) {
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
     * Returns a list containing every block in the schematic.
     * @return list of every block in the schematic
     */
    /*public Map<Location, BaseBlock> getBlocks() {
        return blocks;
    }*/

    /**
     * Returns a material-count map of the materials present in this schematic.
     *
     * The key corresponds to the Bukkit {@link Material} and the value is the
     * amount present in the schematic.
     *
     * @return material-count map of materials in the schematic
     */
    /*public Map<Material, Integer> getSchematicMaterialData() {
        Map<Material, Integer> materialValuesMap = new HashMap<>();
        for (BaseBlock baseBlock : blocks.values()) {
            final Material material = BukkitAdapter.adapt(baseBlock.getBlockType());
            int count = materialValuesMap.getOrDefault(material, 0);
            materialValuesMap.put(material, count + 1);
        }
        return materialValuesMap;
    }*/

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
        //TODO fake block option
    }
}