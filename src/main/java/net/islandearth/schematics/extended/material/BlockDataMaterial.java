package net.islandearth.schematics.extended.material;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public abstract class BlockDataMaterial {

    private final BlockState state;

    public BlockDataMaterial(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    public abstract BlockData update(BlockFace face);
}
