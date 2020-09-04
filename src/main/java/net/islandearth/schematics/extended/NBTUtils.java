package net.islandearth.schematics.extended;

import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to get extra data from NBT
 * @author SamB440
 */
public class NBTUtils {
	
	/**
	 * @param l - blockentities
	 * @return a map, with the key as the vector, and the value as a second map with the key as the slot and the value as the item
	 * @throws WrongIdException
	 */
	public static Map<Vector, Map<Integer, ItemStack>> getItemsFromNBT(NBTTagList l) throws WrongIdException {
		
		Map<Vector, Map<Integer, ItemStack>> allItems = new HashMap<>();
		
		for (int i = 0; i <= l.size(); i++) {
			NBTTagCompound c = l.getCompound(i);
			if (c.getString("Id").equals("minecraft:chest")) {
				NBTTagList items = (NBTTagList) c.get("Items");
				for (int i2 = 0; i2 < items.size(); i2++) {
					NBTTagCompound anItem = items.getCompound(i2);
					Material mat = Material.valueOf(anItem.getString("id").replace("minecraft:", "").toUpperCase());
					ItemStack item = new ItemStack(mat, anItem.getInt("Count"));
					
					int[] pos = c.getIntArray("Pos");
					Map<Integer, ItemStack> result = new HashMap<>();
					result.put(anItem.getInt("Slot"), item);
					allItems.put(new Vector(pos[0], pos[1], pos[2]), result);
				}
			} else {
				throw new WrongIdException("Id of NBT was not a chest, was instead " + c.getString("Id"));
			}
		}
		return allItems;
	}


}
