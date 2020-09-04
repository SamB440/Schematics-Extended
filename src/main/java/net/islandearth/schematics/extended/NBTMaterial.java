package net.islandearth.schematics.extended;

import net.minecraft.server.v1_16_R2.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class containing all supported NBT materials with the matching names
 * @author SamB440
 */
public enum NBTMaterial {
	SIGN;
	
	@Nullable
	public static NBTMaterial fromTag(NBTTagCompound nbtTagCompound) {
		try {
			return NBTMaterial.valueOf(nbtTagCompound.getString("Id").
					replace("minecraft:", "").
					toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
