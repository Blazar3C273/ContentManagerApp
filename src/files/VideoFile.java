package files;


import files.FileSerialization.JsonFormatStrings;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Tolik on 29.11.2014.
 */
public class VideoFile extends AttacheFile implements Serializable {
    public int getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(int timeSec) {
        this.timeSec = timeSec;
    }

    public VideoFile(File file) {
        super(file);
        attachmentType = JsonFormatStrings.ATTACHMENT_TYPE_VIDEO;
    }

    private int timeSec = 0;
}
