package com.example.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.net.Socket;

public class clientController {

    private TextField userIdField = new TextField();
    private TextField newNoteField = new TextField();
    private TextField noteIdField = new TextField();
    private TextField noteTextField = new TextField();
    private TextArea output = new TextArea();

    private Socket socket;
    private BufferedReader socketReader;
    private BufferedWriter socketWriter;

    public VBox noteUI() {

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> login());

        HBox loginRow = new HBox(10,
                new Label("User ID:"),
                userIdField,
                loginButton
        );
        loginRow.setAlignment(Pos.TOP_LEFT);

        Button addNoteButton = new Button("Add Note");
        addNoteButton.setOnAction(e -> newNote());

        HBox newNoteRow = new HBox(10,
                new Label("New Note:"),
                newNoteField,
                addNoteButton
        );
        newNoteRow.setAlignment(Pos.TOP_LEFT);

        Button readButton = new Button("Read");
        readButton.setOnAction(e -> readNote());

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> editNote());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteNote());

        Button readAllButton = new Button("Read All");
        readAllButton.setOnAction(e -> readAllNotes());

        Button shutdownButton = new Button("Shutdown");
        shutdownButton.setOnAction(e -> shutdownServer());

        HBox noteActions = new HBox(10,
        new Label("Note ID:"),
        noteIdField,
        new Label("Edit Note:"),
        noteTextField,
        readButton,
        editButton,
        deleteButton,
        readAllButton,
        shutdownButton
);
noteActions.setAlignment(Pos.TOP_LEFT);


        output.setEditable(false);
        output.setPrefHeight(600);

        VBox root = new VBox(15, loginRow, newNoteRow, noteActions, output);
        root.setPadding(new Insets(10));

        return root;
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 9000);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            output.appendText("connected to server\n");

        } catch (IOException e) {
            e.printStackTrace();
            output.appendText("error connecting: " + e.getMessage() + "\n");
        }
    }

    private void sendCommand(String command) {
        try {
            socketWriter.write(command + "\n");
            socketWriter.flush();

            String response = socketReader.readLine();
            if (response != null) output.appendText(response + "\n");

        } catch (IOException e) {
            e.printStackTrace();
            output.appendText("Error: " + e.getMessage() + "\n");
        }
    }

    private void login() {
        String userId = userIdField.getText().trim();
        if (!userId.isEmpty()) sendCommand("LOGIN " + userId);
    }

    private void newNote() {
        String text = newNoteField.getText().trim();
        if (!text.isEmpty()) {
            sendCommand("NEW NOTE " + text);
            newNoteField.clear();
        }
    }

    private void readNote() {
        String id = noteIdField.getText().trim();
        if (!id.isEmpty()) sendCommand("READ NOTE " + id);
    }

    private void editNote() {
        String id = noteIdField.getText().trim();
        String newText = noteTextField.getText().trim();
        if (!id.isEmpty() && !newText.isEmpty()) {
            sendCommand("WRITE NOTE " + id + " " + newText);
            noteTextField.clear();
        }
    }

    private void deleteNote() {
        String id = noteIdField.getText().trim();
        if (!id.isEmpty()) sendCommand("DELETE NOTE " + id);
    }

    private void readAllNotes() {
        sendCommand("READ ALL NOTES");
    }

    private void shutdownServer() {
        sendCommand("SHUTDOWN");
    }

    public void closeSocket() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
