package net.islandearth.schematics.extended.block;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.islandearth.schematics.extended.WrongIdException;
import net.minecraft.server.v1_16_R2.NBTTagCompound;

public class NBTSignBlock extends NBTBlock {
    
    public NBTSignBlock(NBTTagCompound nbtTagCompound) {
        super(nbtTagCompound);
    }
    
    /**
     * @param position - position of text to read from
     * @return text at the specified position on the sign
     * @throws WrongIdException
     */
    public String getLine(Position position) throws WrongIdException {
        NBTTagCompound compound = this.getNbtTagCompound();
        if (compound.getString("Id").equals("minecraft:sign")) {
            String s1 = compound.getString(position.getId());
            JsonObject jsonObject = new Gson().fromJson(s1, JsonObject.class);
            if (jsonObject.get("extra") != null) {
                JsonArray array = jsonObject.get("extra").getAsJsonArray();
                return array.get(0).getAsJsonObject().get("text").getAsString();
            }
        } else {
            throw new WrongIdException("Id of NBT was not a sign, was instead " + compound.getString("Id"));
        }
        return null;
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
