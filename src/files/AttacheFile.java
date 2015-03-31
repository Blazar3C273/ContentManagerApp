package files;

import files.FileSerialization.JsonFormatStrings;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Tolik on 29.11.2014.
 */
public class AttacheFile implements Serializable {
    protected String id;
    protected String shortName;
    protected String filename;
    protected String description;
    protected String attachmentType;
    protected File file;

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    @Override
    public String toString() {
        return "AttacheFile{" +
                "id='" + id + '\'' +
                ", shortName='" + shortName + '\'' +
                ", filename='" + filename + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public AttacheFile(File file) {
        id = "";
        this.file = file;
        shortName = "";
        filename = "";
        description = "";
        attachmentType = JsonFormatStrings.ATTACHMENT_TYPE_AUDIO;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
