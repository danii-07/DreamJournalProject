package dreamjo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVHandler {

    private final String csvFilePath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CSVHandler(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public void writeEntry(DreamEntry entry) throws IOException {
        boolean fileExists = new File(csvFilePath).exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
            if (!fileExists) {
                writer.write("DateTime,Description,Emotion,Keywords\n");
            }
            writer.write(String.format("%s,%s,%s,%s\n",
                    entry.getDateTime().format(FORMATTER),
                    entry.getDescription().replace(",", ";"),
                    entry.getEmotion().replace(",", ";"),
                    String.join(";", entry.getKeywords())));
        }
    }

    public List<DreamEntry> readEntries() throws IOException {
        List<DreamEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0], FORMATTER);
                    String description = parts[1].replace(";", ",");
                    String emotion = parts[2].replace(";", ",");
                    List<String> keywords = Arrays.asList(parts[3].split(";"));
                    entries.add(new DreamEntry(dateTime, description, emotion, keywords));
                }
            }
        }
        return entries;
    }
    
}
