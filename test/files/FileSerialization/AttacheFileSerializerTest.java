package files.FileSerialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import files.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AttacheFileSerializerTest {

    @Test
    public void testSerializeFiles() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(AttacheFile.class, new AttacheFileSerializer()).create();
        VideoFile videoFile = new VideoFile(null);
        String shortFileDescription = "short name of videofile";
        videoFile.setShortName(shortFileDescription);
        String videoId = "video1";
        videoFile.setId(videoId);
        String fileName = "Che.mpeg";
        videoFile.setFilename(fileName);
        int timeSec = 75;
        videoFile.setTimeSec(timeSec);
        videoFile.setDescription("STSTTsn ohueasn uoauhsnetho uaoes uorcunoeth u\noasneuhtc.,kmsl,p auaonhrc");
        String string = gson.toJson(videoFile);
        System.out.println(string);
        String canonicalJson = "{\n" +
                "  \"timeSec\": " + String.valueOf(timeSec) + ",\n" +
                "  \"id\": \"" + videoId + "\",\n" +
                "  \"shortName\": \"" + shortFileDescription + "\",\n" +
                "  \"filename\": \"" + fileName + "\",\n" +
                "  \"description\": \"STSTTsn ohueasn uoauhsnetho uaoes uorcunoeth u\\noasneuhtc.,kmsl,p auaonhrc\"\n" +
                "}";
        assertEquals("Json must be canonical", canonicalJson, string);
    }

    @Test
    public void testItemSerializer() throws Exception {
        Gson gson = new GsonBuilder().registerTypeAdapter(VideoFile.class, new AttacheFileSerializer())
                .registerTypeAdapter(Item.class, new ItemSerializer())
                .registerTypeAdapter(AudioFile.class, new AttacheFileSerializer())
                .registerTypeAdapter(PictureFile.class, new AttacheFileSerializer())
                .setPrettyPrinting()
                .create();
        Item item = new Item();
        ArrayList<AudioFile> audioFiles = new ArrayList<>();
        ArrayList<PictureFile> pictureFileAttacheFile = new ArrayList<>();

        audioFiles.add(new AudioFile(null));
        audioFiles.add(new AudioFile(null));
        pictureFileAttacheFile.add(new PictureFile());

        item.setAudioFiles(audioFiles);
        item.setPictureFiles(pictureFileAttacheFile);
        item.setParent("root");
        item.setText("Some <b>rich</b> text\nWith even <font color = \"ff0550\">color text</font>");
        item.setItemName("Some foo item name");
        item.setHasAttachments(true);
        item.set_id("6oeau8a0205600oeu5");
        String string = "{\n" +
                "  \"item_description\": \"Some \\u003cb\\u003erich\\u003c/b\\u003e text\\nWith even \\u003cfont color \\u003d \\\"ff0550\\\"\\u003ecolor text\\u003c/font\\u003e\",\n" +
                "  \"parent\": \"root\",\n" +
                "  \"item_name\": \"Some foo item name\",\n" +
                "  \"hasAttachments\": true,\n" +
                "  \"files_meta_data\": [\n" +
                "    {\n" +
                "      \"file_name\": \"\",\n" +
                "      \"id\": \"\",\n" +
                "      \"short_name\": \"\",\n" +
                "      \"description\": \"\",\n" +
                "      \"attachment_type\": \"audio\",\n" +
                "      \"timeSec\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"file_name\": \"\",\n" +
                "      \"id\": \"\",\n" +
                "      \"short_name\": \"\",\n" +
                "      \"description\": \"\",\n" +
                "      \"attachment_type\": \"audio\",\n" +
                "      \"timeSec\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"file_name\": \"\",\n" +
                "      \"id\": \"\",\n" +
                "      \"short_name\": \"\",\n" +
                "      \"description\": \"\",\n" +
                "      \"attachment_type\": \"picture\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        assertEquals(string, gson.toJson(item));
        System.out.println(gson.toJson(item));

    }
}