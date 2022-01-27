package com.convallyria.schematics.extended.block;

import com.convallyria.schematics.extended.WrongIdException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBTSignBlock extends NBTBlock {

    private final Map<Position, String> lines = new HashMap<>();

    public NBTSignBlock(final CompoundTag nbtTag) {
        super(nbtTag);
    }

    @Override
    public void setData(final BlockState state) throws WrongIdException {
        final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) state;
        int current = 0;
        for (final String line : this.getLines()) {
            sign.setLine(current, line);
            current++;
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return getLines().isEmpty();
        } catch (final WrongIdException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param position - position of text to read from
     * @return text at the specified position on the sign
     * @throws WrongIdException
     */
    public String getLine(final Position position) throws WrongIdException {
        if (lines.containsKey(position)) {
            return lines.get(position);
        }

        final CompoundTag compound = this.getNbtTag();
        if (compound.getString("Id").equals("minecraft:sign")) {
            final String s1 = compound.getString(position.getId());
            final JsonObject jsonObject = new Gson().fromJson(s1, JsonObject.class);
            if (jsonObject.get("extra") != null) {
                final JsonArray array = jsonObject.get("extra").getAsJsonArray();
                return array.get(0).getAsJsonObject().get("text").getAsString();
            }
        } else {
            throw new WrongIdException("Id of NBT was not a sign, was instead " + compound.getString("Id"));
        }
        return null;
    }

    public List<String> getLines() throws WrongIdException {
        final List<String> lines = new ArrayList<>();
        for (final Position position : Position.values()) {
            lines.add(getLine(position));
        }
        return lines;
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

        private final String id;

        Position(final String id) {
            this.id = id;
        }
    }
}
