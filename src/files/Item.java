package files;

import files.FileSerialization.JsonFormatStrings;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tolik on 29.11.2014.
 */
public class Item implements Serializable {
    private String _id;
    private String _rev;
    private String text;
    private String _attachments;
    private ArrayList<PictureFile> pictureFiles;
    private ArrayList<AudioFile> audioFiles;
    private ArrayList<VideoFile> videoFiles;
    private String parent;
    private String itemName;
    private boolean hasAttachments;
    private boolean isChanged = false;

    @Override
    public String toString() {
        return itemName;
    }

    public Item(String _id, String _rev, String text, ArrayList<PictureFile> pictureFiles, ArrayList<AudioFile> audioFiles, ArrayList<VideoFile> videoFiles, String parent, String itemName, boolean hasAttachments) {

        this._id = _id;
        this._rev = _rev;
        this.text = text;
        if (pictureFiles == null)
            this.pictureFiles = new ArrayList<>();
        else
            this.pictureFiles = pictureFiles;
        if (audioFiles == null)
            this.audioFiles = new ArrayList<>();
        else
            this.audioFiles = audioFiles;
        if (videoFiles == null)
            this.videoFiles = new ArrayList<>();
        else
            this.videoFiles = videoFiles;
        this.parent = parent;
        this.itemName = itemName;
        this.hasAttachments = hasAttachments;
    }

    public Item() {

    }

    public String get_id() {

        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
        this.setChanged(true);
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
        this.setChanged(true);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.setChanged(true);
    }

    public ArrayList<PictureFile> getPictureFiles() {
        return pictureFiles;
    }

    public void setPictureFiles(ArrayList<PictureFile> pictureFiles) {
        this.pictureFiles = pictureFiles;
        this.setChanged(true);
    }

    public ArrayList<AudioFile> getAudioFiles() {
        return audioFiles;
    }

    public void setAudioFiles(ArrayList<AudioFile> audioFiles) {
        this.audioFiles = audioFiles;
        this.setChanged(true);
    }

    public ArrayList<VideoFile> getVideoFiles() {
        return videoFiles;
    }

    public void setVideoFiles(ArrayList<VideoFile> videoFiles) {
        this.videoFiles = videoFiles;
        this.setChanged(true);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
        this.setChanged(true);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
        this.setChanged(true);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!_id.equals(item._id)) return false;
        if (!audioFiles.equals(item.audioFiles)) return false;
        if (!itemName.equals(item.itemName)) return false;
        if (!pictureFiles.equals(item.pictureFiles)) return false;
        if (parent != null ? !parent.equals(item.parent) : item.parent != null) return false;
        if (text != null ? !text.equals(item.text) : item.text != null) return false;
        if (!videoFiles.equals(item.videoFiles)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id.hashCode();
        result = 31 * result + _rev.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + pictureFiles.hashCode();
        result = 31 * result + audioFiles.hashCode();
        result = 31 * result + videoFiles.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + itemName.hashCode();
        return result;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
        this.setChanged(true);
    }

    public ArrayList<AttacheFile> getAttachmentFiles() {
        ArrayList<AttacheFile> files = new ArrayList<>();
        if (audioFiles != null) {
            files.addAll(getAudioFiles());
        }
        if (videoFiles != null) {
            files.addAll(getVideoFiles());
        }
        if (pictureFiles != null) {
            files.addAll(getPictureFiles());
        }
        return files;
    }

    public void setAttachments(ArrayList<AttacheFile> attacheFiles) {
        if (attacheFiles == null) {
            return;
        }

        //TODO test when getAttachmentType() return null
        for (AttacheFile attacheFile : attacheFiles) {
            switch (attacheFile.getAttachmentType()) {
                case JsonFormatStrings.ATTACHMENT_TYPE_PICTURE:
                    if (pictureFiles == null) {
                        pictureFiles = new ArrayList<>();
                    }
                    this.getPictureFiles().add((PictureFile) attacheFile);
                    break;
                case JsonFormatStrings.ATTACHMENT_TYPE_AUDIO:
                    if (audioFiles == null) {
                        audioFiles = new ArrayList<>();
                    }
                    this.getAudioFiles().add((AudioFile) attacheFile);
                    break;
                case JsonFormatStrings.ATTACHMENT_TYPE_VIDEO:
                    if (videoFiles == null) {
                        videoFiles = new ArrayList<>();
                    }
                    this.getVideoFiles().add((VideoFile) attacheFile);
                    break;
            }
        }
        this.setChanged(true);
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public void removeAllAttachments() {
        if (videoFiles != null) {
            videoFiles.clear();
        }
        if (audioFiles != null) {
            audioFiles.clear();
        }

        if (pictureFiles != null) {
            pictureFiles.clear();
        }
        this.setChanged(true);
    }

    public void setAttacmenString(String attacmenString) {
        this._attachments = attacmenString;
        this.setChanged(true);
    }

    public String get_attachments() {
        return _attachments;
    }
}
