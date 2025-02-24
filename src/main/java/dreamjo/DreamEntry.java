package dreamjo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DreamEntry {

    private final LocalDateTime dateTime;
    private final String description;
    private final String emotion;
    private final List<String> keywords;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DreamEntry(LocalDateTime dateTime, String description, String emotion, List<String> keywords) {
        this.dateTime = dateTime;
        this.description = description;
        this.emotion = emotion;
        this.keywords = keywords;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public String getEmotion() {
        return emotion;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", dateTime.format(FORMATTER), description);
    }
    
}
