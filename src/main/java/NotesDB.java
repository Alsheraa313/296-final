package src.main.java;
import java.sql.*;
import java.util.ArrayList;

public class NotesDB {

    private static Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:sqlite:Notes.db";
        Connection connection = DriverManager.getConnection(dbUrl);

        Statement stmt = connection.createStatement();
        stmt.execute("PRAGMA foreign_keys = ON;");
        stmt.close();

        return connection;
    }

    public static void insertNote(String text) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Notes(text) VALUES (?)");

            preparedStatement.setString(1, text);
            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static ArrayList<NotesClass> getAllNotes() {
        ArrayList<NotesClass> notes = new ArrayList<>();

        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM Notes");

            ResultSet notesQuerery = preparedStatement.executeQuery();

            while (notesQuerery.next()) {
                notes.add(new NotesClass(
                        notesQuerery.getInt("UserID"),
                        notesQuerery.getString("text")));
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return notes;
    }

    public static void deleteNote(int id) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM Notes WHERE UserID = ?");

            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public static void updateNote(int id, String newText) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Notes SET text = ? WHERE UserID = ?");

            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
}
