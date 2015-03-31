package UI;

/**
 * Created by Anatoliy on 01.03.2015.
 */
public class FeedbackNote {
    private String itemName;
    private String rawFeedback;
    private String feedback_text;
    private String visitorContactText;
    private String visitorName;
    private Double rating;
    private String date;

    public FeedbackNote(String itemName, String rawFeedback, String feedback_text, String visitorContactText, String visitorName, Double rating, String date) {
        this.itemName = itemName;
        this.rawFeedback = rawFeedback;
        this.feedback_text = feedback_text;
        this.visitorContactText = visitorContactText;
        this.visitorName = visitorName;
        this.rating = rating;
        this.date = date;
    }

    public String getItemName() {
        return itemName;
    }

    public String getRawFeedback() {
        return rawFeedback;
    }

    public String getFeedback_text() {
        return feedback_text;
    }

    public String getVisitorContactText() {
        return visitorContactText;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public Double getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Посетитель оставил следующие данные:\n" +
                "\nЭкспонат:\n" + itemName + '\n' +
                "\nТекст отзыва:\n" + feedback_text + '\n' +
                "\nТекст обратной свызи:\n" + visitorContactText + '\n' +
                "\nИма посетителя:\n" + visitorName + '\n' +
                "\nРейтинг:\n" + rating + '\n' +
                "\nДата отзыва:\n" + date + '\n';
    }
}
