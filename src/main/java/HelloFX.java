import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloFX extends Application {

    private TextField userIdField;
    private TextField newNoteField;
    private TextArea outputArea;

    @Override
    public void start(Stage stage) {
        // --- top row: User ID + Login ---
        Label userIdLabel = new Label("User ID:");
        userIdField = new TextField();
        userIdField.setPrefWidth(80);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String id = userIdField.getText().trim();
            if (id.isEmpty()) {
                appendLine("Please enter a user ID.");
            } else {
                appendLine("LOGIN as user " + id + " (dummy for now).");
            }
        });

        HBox row1 = new HBox(10, userIdLabel, userIdField, loginButton);
        row1.setAlignment(Pos.CENTER_LEFT);

        // --- second row: New Note + Add Note ---
        Label newNoteLabel = new Label("New Note:");
        newNoteField = new TextField();
        newNoteField.setPrefWidth(400);

        Button addNoteButton = new Button("Add Note");
        addNoteButton.setOnAction(e -> {
            String note = newNoteField.getText().trim();
            if (note.isEmpty()) {
                appendLine("Please type a note before adding.");
            } else {
                appendLine("Added note: " + note);
                newNoteField.clear();
            }
        });

        HBox row2 = new HBox(10, newNoteLabel, newNoteField, addNoteButton);
        row2.setAlignment(Pos.CENTER_LEFT);

        // you can add more buttons later (Read All, Delete, etc.)
        // for now we keep it simple

        VBox topBox = new VBox(8, row1, row2);
        topBox.setPadding(new Insets(10));

        // --- center: big output area ---
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(outputArea);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 900, 400);
        stage.setScene(scene);
        stage.setTitle("Notes Client (Simple)");
        stage.show();
    }

    private void appendLine(String text) {
        if (!outputArea.getText().isEmpty()) {
            outputArea.appendText("\n");
        }
        outputArea.appendText(text);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
