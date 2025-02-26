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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private ListView<DreamEntry> dreamListView; // Change to DreamEntry

    private ObservableList<DreamEntry> dreamList = FXCollections.observableArrayList();

    private CSVHandler csvHandler;

    @FXML
    public void initialize() {
        csvHandler = new CSVHandler("dream_journal.csv");
        try {
            loadDreams();
            dreamListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    showDreamDetails(newSelection);
                }
            });
        } catch (IOException e) {
            showAlert("Error", "Failed to load dream entries. Please check the CSV file.");
            e.printStackTrace(); // Consider logging instead
        }
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
            List<String> keywordList = Arrays.stream(keywords.split(";"))
                    .map(String::trim)
                    .filter(keyword -> !keyword.isEmpty()) // filtering out empty keywords
                    .collect(Collectors.toList());

            DreamEntry entry = new DreamEntry(dateTime, description, emotion, keywordList);
            csvHandler.writeEntry(entry);
            loadDreams();
            clearFields();

        } catch (DateTimeParseException e) {
            showAlert("Error", "Invalid time format. Please use HH:mm.");
        } catch (IOException e) {
            showAlert("Error", "Failed to save dream entry. Please check file permissions or disk space.");
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
            showAlert("Error", "Failed to search dream entries. Please check the CSV file.");
            e.printStackTrace();
        }
    }

    private void loadDreams() throws IOException {
        List<DreamEntry> entries = csvHandler.readEntries();
        displayEntries(entries);
    }

    private void displayEntries(List<DreamEntry> entries) {
        dreamList.clear();
        dreamList.addAll(entries); // Add DreamEntry objects
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

    private void showDreamDetails(DreamEntry entry) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("your dream");
        alert.setHeaderText(entry.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        alert.setContentText(
                "Emotion: " + entry.getEmotion() + "\n" +
                "Keywords: " + String.join(", ", entry.getKeywords()) + "\n" +
                "Description: " + entry.getDescription()
        );
        alert.showAndWait();
    }
}