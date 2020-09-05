package net.islandearth.schematics.extended.block;

import net.islandearth.schematics.extended.WrongIdException;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NBTChestBlock extends NBTBlock {

    private Map<Integer, ItemStack> allItems = new HashMap<>();

    public NBTChestBlock(NBTTagCompound nbtTag) {
        super(nbtTag);
    }

    @Override
    public void setData(BlockState state) throws WrongIdException {
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) state;
        for (Integer location : allItems.keySet()) {
            chest.getSnapshotInventory().setItem(location, allItems.get(location));
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return getItems().isEmpty();
        } catch (WrongIdException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @return a map, with the key as the slot and the value as the item
     * @throws WrongIdException if it's not a chest
     */
    public Map<Integer, ItemStack> getItems() throws WrongIdException {
        if (!allItems.isEmpty()) return allItems;

        NBTTagCompound compound = this.getNbtTag();
        if (compound.getString("Id").equals("minecraft:chest")) {
            if (compound.get("Items") != null) {
                NBTTagList items = (NBTTagList) compound.get("Items");
                for (int i = 0; i < items.size(); i++) {
                    NBTTagCompound anItem = items.getCompound(i);
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
