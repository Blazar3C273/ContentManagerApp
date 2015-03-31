package network;

import files.Item;

import java.io.File;

/**
 * Created by Tolik on 28.11.2014.
 */
public interface DBConnector {
    public boolean isAvailable();

    public String getWelcomeMassage();

    public String getAuth(String login, String pass);

    public String getAllItemsRawList();

    public void setServerAdress(String ip, int port);

    public void addNewItem(Item item);

    public void changeItem(Item item);

    public void uploadAttach(Item item, File file);

    public void downloadAttach();

}
