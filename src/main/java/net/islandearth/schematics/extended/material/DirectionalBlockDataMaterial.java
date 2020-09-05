package net.islandearth.schematics.extended.material;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public class DirectionalBlockDataMaterial extends BlockDataMaterial {

    public DirectionalBlockDataMaterial(BlockState state) {
        super(state);
    }

    @Override
    public BlockData update(BlockFace face) {
        BlockState state = this.getState();
        Directional facing = (Directional) state.getBlockData();
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
        }
        return facing;
    }
}
