package network;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import files.FileSerialization.ItemDeserializer;
import files.FileSerialization.ItemSerializer;
import files.Item;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MIME;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;


/**
 * Created by Tolik on 30.11.2014.
 */
public class NetworkConnection {
    static private String myUri = "http://127.0.0.1:5984/";
    static private DbUrlFactory dbUrlFactory = new DbUrlFactory(myUri);

    /**
     * @param uri Url with port. By default "http://127.0.0.1:5984/". Slash at the end is required.
     */
    public static String setServerURL(String uri) {
        myUri = uri;
        dbUrlFactory = new DbUrlFactory(myUri);
        return myUri;
    }

    public static Header getCookie(String uri, String login, String password) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost authPost = new HttpPost(uri);
        List<BasicNameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("name", login));
        formParams.add(new BasicNameValuePair("password", password));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        authPost.setEntity(entity);
        HttpResponse response = null;
        Header cookieHeader = null;
        try {
            response = client.execute(authPost);
            System.out.println("Sending 'POST' request to URL:" + uri);
            System.out.println("\nSending 'POST' request to URL : " + uri);
            System.out.println("Post parameters : " + authPost.getEntity());
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            BufferedReader rd = null;

            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            Header[] haders = response.getAllHeaders();

            for (int i = 0; i < haders.length; i++) {
                if (haders[i].getName().compareTo("Set-Cookie") == 0) {
                    cookieHeader = haders[i];
                    break;
                }
            }

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cookieHeader;
    }

    public static HttpUriRequest getPostRequest(@NotNull String uri, HttpEntity entity, Header[] headers) {
        HttpPost request = new HttpPost(URI.create(uri));
        if (entity != null) {
            request.setEntity(entity);
        }
        if (headers != null) {
            request.setHeaders(headers);
        }
        return request;
    }

    public static HttpUriRequest getGetRequest(String uri, Header[] headers) {
        HttpGet request = new HttpGet(URI.create(uri));
        if (headers != null) {
            request.setHeaders(headers);
        }
        return request;
    }

    public static HttpUriRequest getPutRequest(@NotNull String uri, HttpEntity entity, Header[] headers) {
        HttpPut request = new HttpPut(URI.create(uri));
        if (entity != null) {
            request.setEntity(entity);
        }
        if (headers != null) {
            request.setHeaders(headers);
        }
        return request;
    }

    public static HttpUriRequest getDeleteRequest(@NotNull String uri, Header[] headers) {
        HttpDelete request = new HttpDelete(URI.create(uri));
        if (headers == null) {
            request.setHeaders(headers);
        }
        return request;
    }

    public static HttpResponse sentRequest(HttpUriRequest request) throws IOException {
        HttpResponse response = null;
        CloseableHttpClient client = HttpClients.createDefault();
        System.out.println(new Date().toString());
        System.out.println(request);
        response = client.execute(request);
        System.out.println(request.getRequestLine().toString());
        return response;
    }

    public static String getAnswerAsString(HttpResponse response) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getAllItemsRawList() {
        String rawItemList = "";
        try {
            return getAnswerAsString(sentRequest(getGetRequest(dbUrlFactory.getAllItemsURL("exibit"), null)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawItemList;
    }

    public static Item getItemByID(String id, String dbName) throws DataBaseError {
        Gson gson = ItemDeserializer.getTunedForDeserializationGson();
        String json = "";
        try {
            json = getAnswerAsString(sentRequest(getGetRequest(dbUrlFactory.getItemURL(id, dbName), null)));
            JSONObject object = (JSONObject) new JSONParser().parse(json);
            if (object.containsKey("error"))
                throw new DataBaseError(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return gson.fromJson(json, Item.class);
    }

    public static boolean isServerOnline() {
        HttpResponse response;
        try {
            response = sentRequest(getGetRequest(myUri, null));
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            String string = result.toString();
            System.out.println(string);
            return true;
        } catch (IOException e) {
            if (e.getCause().getClass().equals(ConnectException.class))
                System.out.println(e.getMessage());
            else
                e.printStackTrace();
        }
        return false;
    }

    //TODO Test it shit
    public static ArrayList<String> getUUID(int number) {
        ArrayList<String> UUIDs = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONObject object;
        try {
            object = (JSONObject) jsonParser.parse(getAnswerAsString(sentRequest(getGetRequest(dbUrlFactory.getUUIDsUri(number), null))));
            JSONArray uuids = (JSONArray) object.get("uuids");

            for (Object uuid : uuids) {
                UUIDs.add((String) uuid);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return UUIDs;
    }

    public static String putItemByID(Item item, String dbName) throws DataBaseError {
        Gson gson = ItemSerializer.getTunedForSerializationGson();
        String json = gson.toJson(item, Item.class);
        HttpEntity body = null;
        String answer = null;
        if (item.get_attachments() != null && !item.get_attachments().equals("")) {
            int lastindex = json.lastIndexOf("}");
            json = json.substring(0, lastindex) + ", \"_attachments\":" + item.get_attachments() + "}";
        }
        body = new StringEntity(json, Charset.defaultCharset());
        try {
            String itemURL;
            String itemID = item.get_id();
            if (itemID == null || itemID.equals("")) {
                itemID = getUUID(1).get(0);
            }
            //TODO обработать конфликты и ошибки:-(
            itemURL = dbUrlFactory.getItemURL(itemID, dbName);
            answer = getAnswerAsString(sentRequest(getPutRequest(itemURL, body, null)));
            System.out.println(answer);
            if (((JSONObject) (new JSONParser().parse(answer))).containsKey("error"))
                throw new DataBaseError(answer);
            item.set_rev((String) ((JSONObject) new JSONParser().parse(answer)).get("rev"));
            item.setAttacmenString(NetworkConnection.getItemByID(itemID, "exibit").get_attachments());
            item.setChanged(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static Map<String, Long> getCategoryNames() throws DataBaseError {
        Map<String, Long> categories = new HashMap<>();

        String jsonResponce = "";
        try {
            jsonResponce = getAnswerAsString(sentRequest(getGetRequest(dbUrlFactory.getCategoriesListURI(), null)));
            if (((JSONObject) (new JSONParser().parse(jsonResponce))).containsKey("error"))
                throw new DataBaseError(jsonResponce);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonResponce);
            JSONArray array = (JSONArray) jsonObject.get("rows");

            for (Object o : array.toArray()) {
                String categoryName = (String) ((JSONObject) o).get("key");
                Long itemCount = (Long) ((JSONObject) o).get("value");
                categories.put(categoryName, itemCount);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public static Map<String, String> getIDsByCategory(String categoryName) {
        Map<String, String> ids = new HashMap<>();
        String jsonAnswer = null;
        try {
            jsonAnswer = getAnswerAsString(sentRequest(getGetRequest(dbUrlFactory.getItemIdByCategoryURI(categoryName), null)));
            JSONObject object = (JSONObject) new JSONParser().parse(jsonAnswer);
            JSONArray jsonArray = (JSONArray) object.get("rows");
            for (Object o : jsonArray) {
                ids.put((String) ((JSONObject) o).get("id"), (String.valueOf(((JSONObject) o).get("value"))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public static String putFileByItem(Item item, File file, String dbName) throws DataBaseError {
        putItemByID(item, "exibit");
        Header[] headers = new Header[2];
        HttpEntity entity;
        String answer = "";
        try {
            String contentType = Files.probeContentType(file.toPath());
            headers[0] = new BasicHeader(MIME.CONTENT_TYPE, contentType);
            headers[1] = new BasicHeader("If-Match", item.get_rev());
            entity = new FileEntity(file);
            answer = getAnswerAsString(sentRequest(getPutRequest(dbUrlFactory.getPutFileURI(dbName, item.get_id(), file.getName().replace(' ', '+')), entity, headers)));
            System.out.println(answer);
            if (((JSONObject) new JSONParser().parse(answer)).containsKey("error")) {
                throw new DataBaseError(answer);
            }
            item.set_rev((String) ((JSONObject) new JSONParser().parse(answer)).get("rev"));
            item.setAttacmenString(NetworkConnection.getItemByID(item.get_id(), "exibit").get_attachments());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static class DataBaseError extends Throwable {
        public DataBaseError(String s) {
            super(s);
        }
    }

}
