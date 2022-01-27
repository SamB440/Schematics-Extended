package com.convallyria.schematics.extended.block;

import com.convallyria.schematics.extended.WrongIdException;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

public abstract class NBTBlock {
    
    private final CompoundTag nbtTag;
    
    public NBTBlock(CompoundTag nbtTag) {
        this.nbtTag = nbtTag;
    }

    public CompoundTag getNbtTag() {
        return nbtTag;
    }

    public Vector getOffset() {
        CompoundTag compound = this.getNbtTag();
        int[] pos = compound.getIntArray("Pos");
        return new Vector(pos[0], pos[1], pos[2]);
    }

    public abstract void setData(BlockState state) throws WrongIdException;

    public abstract boolean isEmpty();
}
