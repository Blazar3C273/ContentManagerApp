package files;

import files.FileSerialization.JsonFormatStrings;

import java.io.Serializable;

/**
 * Created by Tolik on 29.11.2014.
 */
public class PictureFile extends AttacheFile implements Serializable {
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int width;

    public int getDpi() {
        return dpi;
    }

    public int getHeight() {
        return height;
    }

    public PictureFile() {
        super(null);
        attachmentType = JsonFormatStrings.ATTACHMENT_TYPE_PICTURE;
    }

    @Override
    public String toString() {
        return this.getShortName();
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    private int dpi;
}
