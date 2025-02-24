package dreamjo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.text.html.ListView;

public class DreamJournalController {
    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    @FXML
    private TextField emotionField;

    @FXML
    private TextField keywordsField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> dreamListView;

    private ObservableList<String> dreamList = FXCollections.observableArrayList();

    private CSVHandler csvHandler;
    private static final String CSV_FILE_PATH = "dream_journal.csv";

    @FXML
    public void initialize() {
        csvHandler = new CSVHandler(CSV_FILE_PATH);
        loadDreams();
    }

    @FXML
    private void addDreamEntry() {
        LocalDate date = datePicker.getValue();
        String timeString = timeField.getText();
        String emotion = emotionField.getText();
        String keywords = keywordsField.getText();
        String description = descriptionArea.getText();

        if (date == null || timeString.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            List<String> keywordList = List.of(keywords.split(";"));

            DreamEntry entry = new DreamEntry(dateTime, description, emotion, keywordList);
            csvHandler.writeEntry(entry);
            loadDreams();
            clearFields();

        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid time format. Please use HH:mm.");
        } catch (IOException e) {
            showAlert("Error", "Error writing to CSV file.");
            e.printStackTrace();
        }
    }

    @FXML
    private void searchDreams() {
        String searchQuery = searchField.getText().toLowerCase();
        try {
            List<DreamEntry> allEntries = csvHandler.readEntries();
            List<DreamEntry> matchingEntries = allEntries.stream()
                .filter(entry -> entry.getDescription().toLowerCase().contains(searchQuery) ||
                    entry.getEmotion().toLowerCase().contains(searchQuery) ||
                    entry.getKeywords().stream().anyMatch(keyword -> keyword.toLowerCase().contains(searchQuery)))
                .collect(Collectors.toList());

            displayEntries(matchingEntries);

        } catch (IOException e) {
            showAlert("Error", "Error reading from CSV file.");
            e.printStackTrace();
        }
    }

    private void loadDreams() {
        try {
            List<DreamEntry> entries = csvHandler.readEntries();
            displayEntries(entries);
        } catch (IOException e) {
            showAlert("Error", "Error loading dreams from CSV file.");
            e.printStackTrace();
        }
    }

    private void displayEntries(List<DreamEntry> entries) {
        dreamList.clear();
        for (DreamEntry entry : entries) {
            dreamList.add(entry.toString());
        }
        dreamListView.setItems(dreamList);
    }

    private void clearFields() {
        datePicker.setValue(null);
        timeField.clear();
        emotionField.clear();
        keywordsField.clear();
        descriptionArea.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
