package net.islandearth.schematics.extended.block;

import net.islandearth.schematics.extended.WrongIdException;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NBTChestBlock extends NBTBlock {

    public NBTChestBlock(NBTTagCompound nbtTag) {
        super(nbtTag);
    }

    /**
     * @return a map, with the key as the slot and the value as the item
     * @throws WrongIdException if it's not a chest
     */
    public Map<Integer, ItemStack> getItems() throws WrongIdException {
        NBTTagCompound compound = this.getNbtTag();
        Map<Integer, ItemStack> allItems = new HashMap<>();
        if (compound.getString("Id").equals("minecraft:chest")) {
            if (compound.get("Items") != null) {
                NBTTagList items = (NBTTagList) compound.get("Items");
                for (int i2 = 0; i2 < items.size(); i2++) {
                    NBTTagCompound anItem = items.getCompound(i2);
                    Material mat = Material.valueOf(anItem.getString("id").replace("minecraft:", "").toUpperCase());
                    ItemStack item = new ItemStack(mat, anItem.getInt("Count"));
                    allItems.put(anItem.getInt("Slot"), item);
                }
            }
        } else {
            throw new WrongIdException("Id of NBT was not a chest, was instead " + compound.getString("Id"));
        }
        return allItems;
    }
}
