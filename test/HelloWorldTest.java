import UI.LoginForm;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tolik on 25.11.2014.
 */
@Category(MyTest.class)
public class HelloWorldTest {
    LoginForm loginForm;
    Thread fuu;
    String LOGIN_STRING = "fuuu";
    String PASS_STRING = "qwerty123";

    @Before
    public void setUp() throws Exception {
        loginForm = new LoginForm();
        assertTrue(loginForm != null);
        writeTestLogPassInFile();
        loginForm.loadLogPass();
        loginForm.pack();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                loginForm.setVisible(true);
            }
        };
        fuu = new Thread(r, "Main Window thread.");
        fuu.start();
    }

    @After
    public void tearDown() throws Exception {
        fuu.interrupt();
        fuu = null;
        new File("properties.json").delete();
    }

    @Test
    public void firstTest() {
        assertTrue(fuu.isAlive());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("Form must be visible!", loginForm.isVisible());
        loginForm.dispose();
        assertTrue(!loginForm.isVisible());
    }

    @Test
    public void testLoadingLogPassFromFileInTextField() throws Exception {

        assertEquals("Login must be like in file", LOGIN_STRING, loginForm.getLogin());
        assertEquals("Pass must be like in file", PASS_STRING, loginForm.getPass());

    }

    public void writeTestLogPassInFile() {
        JSONObject object = new JSONObject();
        object.put("login", LOGIN_STRING);
        object.put("password", PASS_STRING);
        Writer writer = null;
        try {
            writer = new FileWriter("properties.json");
            object.writeJSONString(writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
