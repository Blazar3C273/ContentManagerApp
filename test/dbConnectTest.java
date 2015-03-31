import files.*;
import files.FileSerialization.JsonFormatStrings;
import network.NetworkConnection;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Map;

import static junit.framework.Assert.assertEquals;


/**
 * Created by Tolik on 28.11.2014.
 */
@Category(MyTest.class)
@RunWith(MockitoJUnitRunner.class)

public class dbConnectTest {
    private Item standart;
    private static final String id = "ef036268c1d97d27319e0cc9d302669a";

    @Before
    public void setUp() throws Exception {
        standart = new Item();
        standart.setParent("root");
        standart.set_id(id);
        standart.set_rev("2-8a34b2b408c935ccd6aa48feae9e6b57");
        standart.setText("Some rich text with<font color =\"#ff0000\">red</font><b>bold</b>\ntext");
        standart.setItemName("Some short item name");
        standart.setHasAttachments(true);

        AttacheFile firstFile = new AudioFile(null);

        firstFile.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_AUDIO);

        firstFile.setId("audio0");
        firstFile.setFilename("lol.mp3");
        firstFile.setDescription("Это просто забавный аудиофайл.");
        firstFile.setShortName("Тестовая хохма.");
        ((AudioFile) firstFile).setTimeSec(65);


        AttacheFile secondFile = new VideoFile(null);

        secondFile.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_VIDEO);

        secondFile.setFilename("cats.mkv");
        secondFile.setDescription("Всем известно, что коты это Боги интернета...");
        secondFile.setShortName("Коты");
        secondFile.setId("video0");
        ((VideoFile) secondFile).setTimeSec(142);

        AttacheFile thirdFile = new PictureFile();

        thirdFile.setAttachmentType(JsonFormatStrings.ATTACHMENT_TYPE_PICTURE);

        thirdFile.setId("pic0");
        thirdFile.setShortName("Книга");
        thirdFile.setFilename("book.jpg");
        thirdFile.setDescription("Some random picture with book on it.");
        ((PictureFile) thirdFile).setDpi(75);

        ArrayList<AttacheFile> files = new ArrayList<>();
        files.add(firstFile);
        files.add(secondFile);
        files.add(thirdFile);

        standart.setAttachments(files);
    }

    @Test
    public void testCategorySetting() throws Exception {
        try {
            Map<String, Long> result = NetworkConnection.getCategoryNames();
            System.out.println(result.toString());
        } catch (NetworkConnection.DataBaseError dataBaseError) {
            dataBaseError.printStackTrace();
        }
    }

    public void testUUIDGetter() throws Exception {
        System.out.println(NetworkConnection.getUUID(5));
    }

    @Test
    public void testPushItem() throws Exception {
        try {
            System.out.print(NetworkConnection.putItemByID(standart, "exibit"));
        } catch (NetworkConnection.DataBaseError dataBaseError) {
            dataBaseError.printStackTrace();
        }
    }

    @Test
    public void testGetItem() throws Exception {
        Item item = null;
        try {
            item = NetworkConnection.getItemByID(id, "exibit");
        } catch (NetworkConnection.DataBaseError dataBaseError) {
            dataBaseError.printStackTrace();
        }
        assertEquals(standart, item);
    }

}
