import java.sql.*;
import java.util.ArrayList;

public class NotesDB {

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        }

        String dbUrl = "jdbc:sqlite:src/notesTable.db";
        System.out.println("Opening db " + dbUrl);

        return DriverManager.getConnection(dbUrl);
    }


    public static void insertNote(int userID, String text) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO notesTable (userID, text) VALUES (?, ?)"
        );
        preparedStatement.setInt(1, userID);
        preparedStatement.setString(2, text);
        preparedStatement.execute();
        connection.close();
    }

    
    public static ArrayList<NotesClass> getNotes(int userID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "SELECT noteID, userID, text FROM notesTable WHERE userID = ?"
        );
        preparedStatement.setInt(1, userID);
        ResultSet noteQuerey = preparedStatement.executeQuery();

        ArrayList<NotesClass> list = new ArrayList<>();

        while (noteQuerey.next()) {
            list.add(new NotesClass(
                noteQuerey.getInt("noteID"),
                noteQuerey.getInt("userID"),
                noteQuerey.getString("text")
            ));
        }

        connection.close();
        return list;
    }

    
    public static void updateNote(int userID, int noteID, String newText) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "UPDATE notesTable SET text = ? WHERE noteID = ? AND userID = ?"
        );
        preparedStatement.setString(1, newText);
        preparedStatement.setInt(2, noteID);
        preparedStatement.setInt(3, userID);
        preparedStatement.executeUpdate();
        connection.close();
    }

    
    public static void deleteNote(int userID, int noteID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "DELETE FROM notesTable WHERE noteID = ? AND userID = ?"
        );
        preparedStatement.setInt(1, noteID);
        preparedStatement.setInt(2, userID);
        preparedStatement.executeUpdate();
        connection.close();
    }
}
