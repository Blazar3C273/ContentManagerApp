/**
 * Created by Tolik on 25.11.2014.
 */

import PropertiesLoader.PropertiesLoader;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Category(MyTest.class)
public class PropertiesLoaderTest {
    private static final String FILE_NAME = "properties.json";

    @After
    public void tearDown() throws Exception {
        File file = new File(FILE_NAME);
        file.delete();
    }

    @Before
    public void setUp() throws Exception {
        Writer writer = new FileWriter(FILE_NAME);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", "Login");
        jsonObject.put("password", "TestPassword");
        jsonObject.writeJSONString(writer);
        writer.close();
    }

    @Test
    public void testOpenFile() {
        System.out.println(System.getProperty("user.dir", "wrong property name"));
        PropertiesLoader propertiesLoader = new PropertiesLoader(FILE_NAME);
        assertTrue(propertiesLoader.loadFromFile());
        assertEquals("Login", propertiesLoader.getLogin());
        assertEquals("TestPassword", propertiesLoader.getPassword());
    }
}
