package files.FileSerialization;

import com.google.gson.*;
import files.*;

import java.lang.reflect.Type;

/**
 * Created by Tolik on 30.11.2014.
 */
//TODO Add attachment feature
public class ItemSerializer implements JsonSerializer<Item> {
    @Override
    public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("item_description", item.getText());
        if (item.getParent() == null || item.getParent().equals(""))
            result.addProperty("parent", "root");
        else
            result.addProperty("parent", item.getParent());
        if (item.get_rev() != null && !item.get_rev().equals(""))
            result.addProperty("_rev", item.get_rev());
        result.addProperty("item_name", item.getItemName());
        result.addProperty("hasAttachments", item.isHasAttachments());

        if (item.isHasAttachments()) {
            JsonArray attachArray = new JsonArray();
            boolean hasVideo = item.getVideoFiles() != null && !item.getVideoFiles().isEmpty();
            boolean hasAudio = item.getAudioFiles() != null && !item.getAudioFiles().isEmpty();
            boolean hasPictures = item.getPictureFiles() != null && !item.getPictureFiles().isEmpty();

            if (hasVideo)
                for (VideoFile videoFile : item.getVideoFiles()) {
                    attachArray.add(jsonSerializationContext.serialize(videoFile, VideoFile.class));
                }

            if (hasAudio)
                for (AudioFile audioFile : item.getAudioFiles()) {
                    attachArray.add(jsonSerializationContext.serialize(audioFile, AudioFile.class));
                }

            if (hasPictures)
                for (PictureFile pictureFile : item.getPictureFiles()) {
                    attachArray.add(jsonSerializationContext.serialize(pictureFile, PictureFile.class));
                }
            result.add("files_meta_data", attachArray);

        }
        return result;
    }

    public static Gson getTunedForSerializationGson() {
        AttacheFileSerializer fileSerializer = new AttacheFileSerializer();
        return new GsonBuilder().registerTypeAdapter(Item.class, new ItemSerializer())
                .registerTypeAdapter(AttacheFile.class, fileSerializer)
                .registerTypeAdapter(VideoFile.class, fileSerializer)
                .registerTypeAdapter(AudioFile.class, fileSerializer)
                .registerTypeAdapter(PictureFile.class, fileSerializer)
                .setPrettyPrinting()
                .create();
    }
}
