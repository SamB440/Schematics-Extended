package net.islandearth.schematics.extended;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
	 * @param c
	 * @param position - position of text to read from
	 * @return text at the specified position on the sign
	 * @throws WrongIdException
	 */
	public static String getSignLineFromNBT(NBTTagCompound c, Position position) throws WrongIdException {
		if (c.getString("Id").equals("minecraft:sign")) {
			String s1 = c.getString(position.getId());
			JsonObject jobj = new Gson().fromJson(s1, JsonObject.class);
			if (jobj.get("extra") != null) {
				JsonArray array = jobj.get("extra").getAsJsonArray();
				return array.get(0).getAsJsonObject().get("text").getAsString();
			}
		} else {
			throw new WrongIdException("Id of NBT was not a sign, was instead " + c.getString("Id"));
		} return null;
	}
	
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

	/**
	 * Utility class for NBT sign positions
	 * @author SamB440
	 */
	public enum Position {
		TEXT_ONE("Text1"),
		TEXT_TWO("Text2"),
		TEXT_THREE("Text3"),
		TEXT_FOUR("Text4");

		public String getId() {
			return id;
		}

		private String id;
		
		Position(String id) {
			this.id = id;
		}
	}
}
