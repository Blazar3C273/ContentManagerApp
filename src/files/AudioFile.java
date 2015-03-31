package files;

import files.FileSerialization.JsonFormatStrings;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Tolik on 29.11.2014.
 */
public class AudioFile extends AttacheFile implements Serializable {
    public AudioFile(File file) {
        super(file);
        setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_AUDIO);
    }

    public int getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(int timeSec) {
        this.timeSec = timeSec;
    }

    private int timeSec = 0;
}
