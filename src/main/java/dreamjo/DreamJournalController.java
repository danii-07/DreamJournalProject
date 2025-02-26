package dreamjo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList; // special type of list with which is easiert to notify the ListView (or other UI elements) when items are changed.
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException; // this imports the IOException for handling file input/output exceptions.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DreamJournalController {
    @FXML // this just connect the FXML layout to the java code.
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
    private ListView<DreamEntry> dreamListView; // a listView is a JavaFX UI control that shows a list of items, in this case the dream entries from fxml.

    private ObservableList<DreamEntry> dreamList = FXCollections.observableArrayList(); // telling the observableList to hold dreamEntry objects for the listView.

    private CSVHandler csvHandler;

    @FXML
    public void initialize() {
        csvHandler = new CSVHandler("dream_journal.csv"); // handles writing and reading data.
        try {
            loadDreams();
            dreamListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    showDreamDetails(newSelection);
                }
            });
        } catch (IOException e) {
            showAlert("Error", "Failed to load dream entries. Please check the CSV file.");
            e.printStackTrace(); // just for debugging to see where something failed.
        }
    }

    @FXML
    private void addDreamEntry() { // private because ut is only meant to use it here, in the class (DreamJournalController) itself.
        LocalDate date = datePicker.getValue();
        String timeString = timeField.getText();
        String emotion = emotionField.getText();
        String keywords = keywordsField.getText();
        String description = descriptionArea.getText();

         // checking if the required fields are empty or not

        if (date == null || timeString.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        try { // here we basically go from parsing the time string into a LocalTime object.
            LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
            // and then creating a LocalDateTime object from the date and time.
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            List<String> keywordList = Arrays.stream(keywords.split(";")) // splitting things into keywords correctly
                    .map(String::trim) // no blanck space between the kwords
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
        String searchQuery = searchField.getText().toLowerCase(); // here i get the search query and convert it to lowercase. soo key sensitive :)
        try {
            List<DreamEntry> allEntries = csvHandler.readEntries(); // reading all
            List<DreamEntry> matchingEntries = allEntries.stream() // filtering the entries based on the input
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

    private void loadDreams() throws IOException { // with this we are able to read the entries from the csv file.
        List<DreamEntry> entries = csvHandler.readEntries();
        displayEntries(entries);
    }

    private void displayEntries(List<DreamEntry> entries) { // clearing and updating ou special list.
        dreamList.clear();
        dreamList.addAll(entries); // adding dreamEntry objects to the list.
        dreamListView.setItems(dreamList);
    }

    private void clearFields() { // clearing input fields after a dream is saved.
        datePicker.setValue(null);
        timeField.clear();
        emotionField.clear();
        keywordsField.clear();
        descriptionArea.clear();
    }

    private void showAlert(String title, String content) { // creating alerts and its content.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showDreamDetails(DreamEntry entry) { // this shows me my dream details.
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