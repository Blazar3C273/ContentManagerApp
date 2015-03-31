package files.FileSerialization;

import com.google.gson.*;
import files.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Tolik on 30.11.2014.
 */
public class ItemDeserializer implements JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Item result = new Item();
        JsonObject object = (JsonObject) jsonElement;
        result.set_id(object.get("_id").getAsString());
        result.set_rev(object.get("_rev").getAsString());
        result.setText(object.get("item_description").getAsString());
        result.setParent(object.get("parent").getAsString());
        result.setHasAttachments(object.get("hasAttachments").getAsBoolean());
        result.setItemName(object.get("item_name").getAsString());
        if (result.getParent() == null || result.getParent().equals("")) {
            result.setParent("root");
        }
        JsonElement element1 = object.get("_attachments");
        if (element1 != null) {
            result.setAttacmenString(element1.toString());
        }
        if (result.isHasAttachments()) {
            JsonArray jsonArray = object.getAsJsonArray("files_meta_data");
            ArrayList<AttacheFile> attacheFiles = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                attacheFiles.add((AttacheFile) jsonDeserializationContext.deserialize(element, AttacheFile.class));
            }
            result.setAttachments(attacheFiles);
        }
        return result;
    }

    public static Gson getTunedForDeserializationGson() {
        AttacheFileDeserializer fileDeserializer = new AttacheFileDeserializer();
        return new GsonBuilder().registerTypeAdapter(Item.class, new ItemDeserializer())
                .registerTypeAdapter(AttacheFile.class, fileDeserializer)
                .registerTypeAdapter(VideoFile.class, fileDeserializer)
                .registerTypeAdapter(AudioFile.class, fileDeserializer)
                .registerTypeAdapter(PictureFile.class, fileDeserializer)
                .create();
    }
}
