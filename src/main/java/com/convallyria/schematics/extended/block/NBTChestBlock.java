package com.convallyria.schematics.extended.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import com.convallyria.schematics.extended.WrongIdException;

public class NBTChestBlock extends NBTBlock {

    private final Map<Integer, ItemStack> allItems = new HashMap<>();

    public NBTChestBlock(final NBTTagCompound nbtTag) {
        super(nbtTag);
    }

    @Override
    public void setData(final BlockState state) {
        final org.bukkit.block.Chest chest = (org.bukkit.block.Chest) state;
        for (final Integer location : allItems.keySet()) {
            chest.getSnapshotInventory().setItem(location, allItems.get(location));
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return getItems().isEmpty();
        } catch (final WrongIdException e) {
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

        final NBTTagCompound compound = this.getNbtTag();
        if (compound.getString("Id").equals("minecraft:chest")) {
            if (compound.get("Items") != null) {
                final NBTTagList items = (NBTTagList) compound.get("Items");
                for (int i = 0; i < items.size(); i++) {
                    final NBTTagCompound anItem = items.getCompound(i);
                    final Material mat = Material.valueOf(anItem.getString("id").replace("minecraft:", "").toUpperCase());
                    final ItemStack item = new ItemStack(mat, anItem.getInt("Count"));
                    allItems.put(anItem.getInt("Slot"), item);
                }
            }
        } else {
            throw new WrongIdException("Id of NBT was not a chest, was instead " + compound.getString("Id"));
        }
        return allItems;
    }
}
