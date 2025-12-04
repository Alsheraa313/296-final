import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9000, 100, InetAddress.getByName("localhost"));

            System.out.println("Server started at: " + serverSocket);

            while (true) {
                System.out.println("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Received a connection from " + clientSocket);

                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleClientRequest(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            int currentUserID = -1;
            String inMsg;

            while ((inMsg = reader.readLine()) != null) {
                System.out.println("Received from client: " + inMsg);

                if (inMsg.startsWith("LOGIN")) {
                    try {
                        currentUserID = Integer.parseInt(inMsg.substring(6).trim());
                    } catch (Exception e) {
                        writer.write("INVALID USERID\n");
                        writer.flush();
                        continue;
                    }

                    writer.write("LOGIN SUCCESSFUL\n");
                    writer.flush();
                    System.out.println("Logged in userID " + currentUserID);
                }

                else if (currentUserID == -1) {
                    writer.write("LOGIN first\n");
                    writer.flush();
                }

                else if (inMsg.startsWith("NEW NOTE")) {

                    String noteText = inMsg.substring(8).trim();

                    if (inMsg.length() <= 8 || noteText.isEmpty()) {
                        writer.write("TEXT REQUIRED\n");
                        writer.flush();
                        continue;
                    }

                    NotesDB.insertNote(currentUserID, noteText);
                    writer.write("DELIVERED\n");
                    writer.flush();
                    continue;
                }
                else if(inMsg.startsWith("READ ALL NOTES")){
                    String[] parts = inMsg.split(" ");

                    if (parts.length == 3) {
                        writer.write(NotesDB.readAllNotes(currentUserID));
                    } 
                    else {
                        writer.write("Invalid format\n");
                    }

                    writer.flush();
                }
                else if (inMsg.startsWith("READ NOTE")) {
                    String[] parts = inMsg.split(" ");

                     if (parts.length == 3) {
                        try {
                            int noteID = Integer.parseInt(parts[2]);
                            writer.write(NotesDB.readNote(currentUserID, noteID));
                        } catch (Exception e) {
                            writer.write("Invalid noteID\n");
                        }
                    } else {
                        writer.write("Invalid format\n");
                    }

                    writer.flush();
                }

                else if (inMsg.startsWith("WRITE NOTE")) {

                    String[] parts = inMsg.split(" ", 3);

                    if (parts.length < 3) {
                        writer.write("FORMAT: WRITE NOTE + ID + TEXT\n");
                        writer.flush();
                        continue;
                    }
                    
                    String[] note = parts[2].split(" ", 2);

                    if (note.length < 2) {
                        writer.write("FORMAT: WRITE NOTE + ID + TEXT\n");
                        writer.flush();
                        continue;
                    }

                    int noteID;
                    try {
                        noteID = Integer.parseInt(note[0]);
                    } catch (Exception e) {
                        writer.write("Invalid noteID\n");
                        writer.flush();
                        continue;
                    }

                    String newText = note[1];

                    NotesClass n = NotesDB.getNote(currentUserID, noteID);
                    if (n == null) {
                        writer.write("Invalid noteID\n");
                        writer.flush();
                        continue;
                    }

                    NotesDB.updateNote(currentUserID, noteID, newText);
                    writer.write("NOTE EDITED\n");
                    writer.flush();
                }

                else if (inMsg.startsWith("DELETE NOTE")) {
                    String[] parts = inMsg.split(" ", 3);

                    if (parts.length < 3) {
                        writer.write("FORMAT: DELETE NOTE + ID\n");
                        writer.flush();
                        continue;
                    }

                    int noteID;
                    try {
                        noteID = Integer.parseInt(parts[2]);
                    } catch (Exception e) {
                        writer.write("Invalid noteID\n");
                        writer.flush();
                        continue;
                    }

                    try {
                        NotesDB.deleteNote(currentUserID, noteID);
                        writer.write("NOTE DELETED\n");
                    } catch (SQLException e) {
                        writer.write("ERROR DELETING NOTE\n");
                        e.printStackTrace();
                    }
                    writer.flush();
                }

                else if (inMsg.startsWith("SHUTDOWN")) {
                    writer.write("SHUTTING SERVER DOWN\n");
                    writer.flush();
                    System.out.println("Server shutting down");
                    System.exit(0);
                }

                else {
                    writer.write("Unknown command\n");
                    writer.flush();
                }

            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}