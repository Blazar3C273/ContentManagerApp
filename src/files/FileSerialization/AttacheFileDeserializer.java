package files.FileSerialization;

import com.google.gson.*;
import files.AttacheFile;
import files.AudioFile;
import files.PictureFile;
import files.VideoFile;

import java.lang.reflect.Type;

/**
 * Created by Tolik on 30.11.2014.
 */
public class AttacheFileDeserializer implements JsonDeserializer<AttacheFile> {
    @Override
    public AttacheFile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        AttacheFile result;
        JsonObject object = (JsonObject) jsonElement;
        String attachment_type = object.get(JsonFormatStrings.ATTACHMENT_TYPE_FIELD).getAsString();
        String filename = object.get("file_name").getAsString();
        //File file = NetworkConnection.getFileByName(filename,id,dbName);
        switch (attachment_type) {
            case JsonFormatStrings.ATTACHMENT_TYPE_AUDIO:
                result = new AudioFile(null);
                result.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_AUDIO);
                ((AudioFile) result).setTimeSec(object.get("timeSec").getAsInt());
                break;
            case JsonFormatStrings.ATTACHMENT_TYPE_PICTURE:
                result = new PictureFile();
                result.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_PICTURE);
                ((PictureFile) result).setWidth(object.get("width").getAsInt());
                ((PictureFile) result).setHeight(object.get("height").getAsInt());
                break;
            case JsonFormatStrings.ATTACHMENT_TYPE_VIDEO:
                result = new VideoFile(null);
                result.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_VIDEO);
                ((VideoFile) result).setTimeSec(object.get("timeSec").getAsInt());
                break;
            default: {
                throw new JsonParseException("Attachment parse error. Attachment in not Audio or Video or Picture or json with syntax error");
            }
        }
        result.setDescription(object.get("description").getAsString());
        result.setFilename(object.get("file_name").getAsString());
        result.setId(object.get("id").getAsString());
        result.setShortName(object.get("short_name").getAsString());

        return result;
    }
}
