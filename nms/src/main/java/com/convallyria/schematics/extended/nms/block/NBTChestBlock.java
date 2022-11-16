package com.convallyria.schematics.extended.nms.block;

import com.convallyria.schematics.extended.nms.WrongIdException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NBTChestBlock extends NBTBlock {

    private final Map<Integer, ItemStack> allItems = new HashMap<>();

    public NBTChestBlock(final CompoundTag nbtTag) {
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

        final CompoundTag compound = this.getNbtTag();
        if (compound.getString("Id").equals("minecraft:chest")) {
            if (compound.get("Items") != null) {
                final ListTag items = (ListTag) compound.get("Items");
                for (int i = 0; i < items.size(); i++) {
                    final CompoundTag anItem = items.getCompound(i);
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
