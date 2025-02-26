package dreamjo;

import java.io.*; // importing classes for file input/output operations.
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // importing ArrayList for creating dynamic lists.
import java.util.Arrays;
import java.util.List;

public class CSVHandler {

    private final String csvFilePath; // first we store the file path of the CSV file, which we will use later.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CSVHandler(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public void writeEntry(DreamEntry entry) throws IOException {
        boolean fileExists = new File(csvFilePath).exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) { // writing everything in a buffer first and the into the file for a quicker system response.
            if (!fileExists) {
                writer.write("DateTime,Description,Emotion,Keywords\n");
            }
            // adding a newline character before writing the new entry
            if (fileExists && new File(csvFilePath).length() > 0) {
                writer.write("\n");
            }
            writer.write(String.format("%s,%s,%s,%s", // writes the entry data in a csv format.
                    entry.getDateTime().format(FORMATTER),
                    entry.getDescription().replace(",", ";"),
                    entry.getEmotion().replace(",", ";"),
                    String.join(";", entry.getKeywords())));
        }
    }

    public List<DreamEntry> readEntries() throws IOException {
        List<DreamEntry> entries = new ArrayList<>(); // creating a list to store dreamEntry objects.
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) { // reads until the end of the line.
                String[] parts = line.split(",", 4); // splitting in 4 parts.
                if (parts.length == 4) { // checking if it did the splitting correctly.
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0], FORMATTER);
                    String description = parts[1].replace(";", ",");
                    String emotion = parts[2].replace(";", ",");
                    List<String> keywords = Arrays.asList(parts[3].split(";"));
                    entries.add(new DreamEntry(dateTime, description, emotion, keywords)); // creating and adding a dreamEntry object.
                }
            }
        }
        return entries; // returns the list.
    }
}