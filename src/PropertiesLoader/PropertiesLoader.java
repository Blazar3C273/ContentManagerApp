package PropertiesLoader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

/**
 * Created by Tolik on 25.11.2014.
 */
public class PropertiesLoader implements Serializable {
    private String login;
    private String password;
    private String fileName;
    private String serverAddress;
    private String dbName;

    public PropertiesLoader(String fileName) {
        this.fileName = fileName;
    }

    public boolean loadFromFile() {
        boolean isSuccessful = false;
        JSONObject jsonObject;
        Reader reader = null;
        JSONParser jsonParser = new JSONParser();
        try {
            reader = new FileReader(fileName);
            jsonObject = (JSONObject) jsonParser.parse(reader);
            if (reader != null)
                reader.close();
            login = (String) jsonObject.get("login");
            password = (String) jsonObject.get("password");
            serverAddress = (String) jsonObject.get("server_address");
            dbName = (String) jsonObject.get("db_name");
            isSuccessful = true;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
        }
        if (!isSuccessful) {
            serverAddress = "http://127.0.0.1:5984/";
            dbName = "exibit";
        }
        return isSuccessful;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getDbName() {
        return dbName;
    }
}
