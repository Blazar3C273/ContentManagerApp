package files.FileSerialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import files.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ItemDeserializerTest {
    private Item sampleItem;
    private String inputJSON;

    @Before
    public void setUp() throws Exception {
        sampleItem = new Item();
        ArrayList<AudioFile> audioFiles = new ArrayList<>();
        ArrayList<PictureFile> pictureFileAttacheFile = new ArrayList<>();

        audioFiles.add(new AudioFile(null));
        audioFiles.add(new AudioFile(null));
        pictureFileAttacheFile.add(new PictureFile());

        sampleItem.setAudioFiles(audioFiles);
        sampleItem.setPictureFiles(pictureFileAttacheFile);

        sampleItem.setText("Some <b>rich</b> text\nWith even <font color = \"ff0550\">color text</font>");
        sampleItem.setItemName("Some foo item name");
        sampleItem.setHasAttachments(true);
        sampleItem.set_id("eb3e5bdddf50b1edc3880057c800ebd0");


        inputJSON = "{\"_id\":\"eb3e5bdddf50b1edc3880057c800ebd0\",\"_rev\":\"3-550265cd4d66301416106ac7fa467f27\",\"item_description\":\"Some <b>rich</b> text\\nWith even <font color = \\\"ff0550\\\">color text</font>\",\"parent\":\"root\",\"item_name\":\"Some foo item name\",\"hasAttachments\":true,\"files_meta_data\":[{\"file_name\":\"\",\"id\":\"\",\"short_name\":\"\",\"description\":\"\",\"attachment_type\":\"audio\",\"timeSec\":0},{\"file_name\":\"\",\"id\":\"\",\"short_name\":\"\",\"description\":\"\",\"attachment_type\":\"audio\",\"timeSec\":0},{\"file_name\":\"\",\"id\":\"\",\"short_name\":\"\",\"description\":\"\",\"attachment_type\":\"picture\"}]}";
    }

    @Test
    public void testDeserialize() throws Exception {
        AttacheFileDeserializer fileDeserializer = new AttacheFileDeserializer();
        Gson gson = new GsonBuilder().registerTypeAdapter(Item.class, new ItemDeserializer())
                .registerTypeAdapter(PictureFile.class, fileDeserializer)
                .registerTypeAdapter(VideoFile.class, fileDeserializer)
                .registerTypeAdapter(AudioFile.class, fileDeserializer)
                .registerTypeAdapter(AttacheFile.class, fileDeserializer)
                .create();
        assertEquals(sampleItem, gson.fromJson(inputJSON, Item.class));
    }
}