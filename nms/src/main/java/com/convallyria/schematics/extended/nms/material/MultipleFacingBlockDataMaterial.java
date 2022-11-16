package com.convallyria.schematics.extended.nms.material;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

public class MultipleFacingBlockDataMaterial extends BlockDataMaterial {

    public MultipleFacingBlockDataMaterial(BlockState state) {
        super(state);
    }

    @Override
    public BlockData update(BlockFace face) {
        BlockState state = this.getState();
        MultipleFacing facing = (MultipleFacing) state.getBlockData();
        // ??? where's the api for setting these automatically... https://www.spigotmc.org/threads/force-updating-fence-connections.461672/
        facing.getAllowedFaces().forEach(bf -> {
            Block rel = state.getBlock().getRelative(bf);
            if (rel.getType() == Material.AIR
                    || rel.getType().toString().contains("SLAB")
                    || rel.getType().toString().contains("STAIRS")) {
                if (facing.hasFace(bf)) facing.setFace(bf, false);
            } else {
                if (!rel.getType().toString().contains("SLAB")
                        && !rel.getType().toString().contains("STAIRS")
                        && !Tag.ANVIL.getValues().contains(rel.getType())
                        && rel.getType().isSolid()
                        && rel.getType().isBlock()) {
                    if (!facing.hasFace(bf)) facing.setFace(bf, true);
                }
            }
        });

        return facing;
    }
}
