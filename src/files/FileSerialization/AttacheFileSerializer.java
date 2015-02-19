package files.FileSerialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import files.AttacheFile;
import files.AudioFile;
import files.PictureFile;
import files.VideoFile;

import java.lang.reflect.Type;

/**
 * Created by Tolik on 30.11.2014.
 */
public class AttacheFileSerializer implements JsonSerializer<AttacheFile> {

    @Override
    public JsonElement serialize(AttacheFile attacheFile, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject element = new JsonObject();
        element.addProperty("file_name", attacheFile.getFilename());
        element.addProperty("id", attacheFile.getId());
        element.addProperty("short_name", attacheFile.getShortName());
        element.addProperty("description", attacheFile.getDescription());

        if (type.equals(AudioFile.class)) {
            element.addProperty("attachment_type", JsonFormatStrings.ATTACHMENT_TYPE_AUDIO);
            element.addProperty("timeSec", ((AudioFile) attacheFile).getTimeSec());
        }
        if (type.equals(VideoFile.class)) {
            element.addProperty("attachment_type", JsonFormatStrings.ATTACHMENT_TYPE_VIDEO);
            element.addProperty("timeSec", ((VideoFile) attacheFile).getTimeSec());
        }
        if (type.equals(PictureFile.class)) {
            element.addProperty("attachment_type", JsonFormatStrings.ATTACHMENT_TYPE_PICTURE);
            element.addProperty("height", ((PictureFile) attacheFile).getHeight());
            element.addProperty("width", ((PictureFile) attacheFile).getWidth());
        }
        return element;
    }
}
