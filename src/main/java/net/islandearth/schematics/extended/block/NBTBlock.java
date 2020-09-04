package net.islandearth.schematics.extended.block;

import net.minecraft.server.v1_16_R2.NBTTagCompound;

public class NBTBlock {
    
    private final NBTTagCompound nbtTagCompound;
    
    public NBTBlock(NBTTagCompound nbtTagCompound) {
        this.nbtTagCompound = nbtTagCompound;
    }
    
    public NBTTagCompound getNbtTagCompound() {
        return nbtTagCompound;
    }
}
