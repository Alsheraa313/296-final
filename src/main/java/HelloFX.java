import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;


public class HelloFX extends Application {

    private Stage primaryStage;
    private Scene loginScene;
    private Scene notesScene;

    private String currentUserName = "";
    private Label notesTitleLabel;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        initNotesScene();
        initLoginScene();
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Notes App");
        primaryStage.show();
    }

    private void initLoginScene() {
        Label title = new Label("Login");
        Label prompt = new Label("Enter your name or ID:");
        TextField nameField = new TextField();
        nameField.setMaxWidth(200);

        Label errorLabel = new Label();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String text = nameField.getText().trim();
            if (text.isEmpty()) {
                errorLabel.setText("Please enter something.");
            } else {
                currentUserName = text;
                notesTitleLabel.setText("Notes - User: " + currentUserName);
                errorLabel.setText("");
                primaryStage.setScene(notesScene);
            }
        });

        VBox centerBox = new VBox(10, title, prompt, nameField, loginButton, errorLabel);
        centerBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setPadding(new Insets(20));

        loginScene = new Scene(root, 500, 350);
    }

    private void initNotesScene() {
         notesTitleLabel = new Label("Notes");

        Button newButton = new Button("New");
        newButton.setOnAction(e -> System.out.println("New clicked"));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> System.out.println("Save clicked"));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> System.out.println("Delete clicked"));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> primaryStage.setScene(loginScene));

        HBox toolbar = new HBox(10, newButton, saveButton, deleteButton, logoutButton);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label info = new Label("This will become the main notes screen.");
        VBox centerBox = new VBox(20, notesTitleLabel, info);
        centerBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(centerBox);
        root.setPadding(new Insets(20));

        notesScene = new Scene(root, 600, 400);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
