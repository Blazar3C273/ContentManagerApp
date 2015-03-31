package network;

/**
 * Created by Tolik on 29.11.2014.
 */
public class DbUrlFactory {
    private String uri;

    public DbUrlFactory(String uri) {
        this.uri = uri;
    }

    public String getAllItemsURL(String dbName) {
        return uri + "/" + dbName + "/_all_docs";
    }

    public String getItemURL(String id, String dataBaseName) {
        return uri + dataBaseName + "/" + id;
    }

    public String getUUIDsUri(int number) {
        if (number <= 1)
            return uri + "_uuids";
        else
            return uri + "_uuids?count=" + number;
    }

    public String getCategoriesListURI() {
        return uri + "exibit/_design/exhibit/_view/countParents?reduce=true&group_level=999";
    }

    public String getItemIdByCategoryURI(String category) {
        category = category.replace(' ', '+');

        return uri + "exibit/_design/exhibit/_view/getByCategory?key=%22" + category + "%22";
    }

    public String getPutFileURI(String dbName, String id, String name) {
        return uri + dbName + "/" + id + "/" + name;
    }

    public String getFeedbackUrl() {
        //http://192.168.1.3:5984/feedback/_design/byItemName/_view/byItemName?include_docs=true
        return uri + "/feedback/_design/byItemName/_view/byItemName?include_docs=true";
    }
}
