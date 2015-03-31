package files.FileSerialization;

import UI.FeedbackNote;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Anatoliy on 01.03.2015.
 */
public class FeedbackDeserializer implements JsonDeserializer<FeedbackNote> {
    @Override
    public FeedbackNote deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject element = ((JsonObject) jsonElement);
        String key = "";
        if (element.get("key").isJsonNull()) {
            key = "К выставке.";
        } else {
            key = element.get("key").getAsString();
        }
        JsonObject doc = element.get("doc").getAsJsonObject();
        String feedback_text = "без текста";
        Double rating = -1.0;
        String visitorName = "Анонимус";
        String visitorContactText = "Без обратной связи";
        String date = "Дата неизвестна";

        if (doc.has("_id"))
            date = doc.get("_id").getAsString();
        if (doc.has("feedback_text"))
            feedback_text = doc.get("feedback_text").getAsString();
        if (doc.has("rating"))
            rating = doc.get("rating").getAsDouble();
        if (doc.has("visitor_contact_text"))
            visitorContactText = doc.get("visitor_contact_text").getAsString();
        if (doc.has("visitor_name"))
            visitorName = doc.get("visitor_name").getAsString();

        return new FeedbackNote(key, element.toString(), feedback_text, visitorContactText, visitorName, rating, date);
    }
}
