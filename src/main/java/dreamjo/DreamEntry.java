package dreamjo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List; // importing the list interface for handling lists of strings.

public class DreamEntry { // defining what a dreamEntry is and what it should contain and how. they all have the final cause they can only be set once.

    private final LocalDateTime dateTime;
    private final String description;
    private final String emotion;
    private final List<String> keywords; // this stores a list of keywords associated with the dream entry.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DreamEntry(LocalDateTime dateTime, String description, String emotion, List<String> keywords) { // the constructor of when a new dreamEntry is created.
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
    } // finally it returns a string representation of the DreamEntry object.
    
}
