import com.google.gson.Gson;
import files.AudioFile;
import files.Item;
import files.PictureFile;
import files.VideoFile;
import network.NetworkConnection;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tolik on 29.11.2014.
 */
@Category(MyTest.class)
@RunWith(MockitoJUnitRunner.class)
public class SerializationDeserializationItemsTest {
    String json;


    public void testItemDeSerializer() throws Exception {
        Gson gson = new Gson();
        Item item = new Item("eb3e5bdddf50b1edc3880057c8009934",
                "3-852ac92e32f06f6992d204c37dec0492",
                "some rich text with tags", null, null, null, null, "Название экспоната", false);
        ArrayList<AudioFile> audioFiles = new ArrayList<>();
        audioFiles.add(new AudioFile(null));
        audioFiles.get(0).setTimeSec(55);
        audioFiles.get(0).setShortName("Повесть о лете.");
        audioFiles.get(0).setId("audio1");
        audioFiles.get(0).setFilename("somefile.name");
        audioFiles.get(0).setDescription("soemFeeuao\nother\nstrings");
        audioFiles.add(new AudioFile(null));
        item.setAudioFiles(audioFiles);
        ArrayList<VideoFile> videoFiles = new ArrayList<>();
        videoFiles.add(new VideoFile(null));
        videoFiles.add(new VideoFile(null));
        item.setVideoFiles(videoFiles);
        ArrayList<PictureFile> pictureFiles = new ArrayList<>();
        pictureFiles.add(new PictureFile());
        item.setPictureFiles(pictureFiles);
        json = gson.toJson(item);

        System.out.println(json);
        item = gson.fromJson(json, Item.class);

        System.out.println("\n\n" + item.toString());
    }


    public void testVideoFileDeserialzation() throws Exception {
        Item item = (new Gson().fromJson(json, Item.class));
        System.out.println(item);
    }

    @Test
    public void testSendingFile() throws Exception {
        String id = "ef036268c1d97d27319e0cc9d304ab64";
        String dbName = "exibit";
        try {
            String fileName = "08. Linkin Park - Rebellion (Feat. Daron Malakian).mp3";
            File file = new File(fileName);
            System.out.println(NetworkConnection.putFileByItem(NetworkConnection.getItemByID(id, dbName), file, dbName));
        } catch (NetworkConnection.DataBaseError dataBaseError) {
            dataBaseError.printStackTrace();
        }

    }
}
