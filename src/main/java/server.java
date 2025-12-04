import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket =
                    new ServerSocket(9000, 100,
                            InetAddress.getByName("localhost"));
            System.out.println("Server started at: " + serverSocket);

            while (true) {
                System.out.println("Waiting for a connection...");

                final Socket activeSocket = serverSocket.accept();
                System.out.println("Received a connection from "
                        + activeSocket.getRemoteSocketAddress());

                Runnable runnable = () -> handleClientRequest(activeSocket);
                new Thread(runnable).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle one client connection
    public static void handleClientRequest(Socket socket) {
        try (BufferedReader socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
             BufferedWriter socketWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()))) {

            String inMsg;

            while ((inMsg = socketReader.readLine()) != null) {
                inMsg = inMsg.trim();
                System.out.println("Received from client: " + inMsg);

                String response;

                // ADD|some note text
                if (inMsg.startsWith("ADD|")) {
                    String text = inMsg.substring(4); // everything after "ADD|"
                    NotesDB.insertNote(text);
                    response = "OK|Note added";

                // LIST
                } else if (inMsg.equals("LIST")) {
                    List<NotesClass> notes = NotesDB.getAllNotes();
                    StringBuilder sb = new StringBuilder();
                    for (NotesClass n : notes) {
                        // format each note as id:text;
                        sb.append(n.getUserID())
                          .append(":")
                          .append(n.getText())
                          .append(";");
                    }
                    response = "LIST|" + sb.toString();

                // DELETE|id
                } else if (inMsg.startsWith("DELETE|")) {
                    String idStr = inMsg.substring(7); // after "DELETE|"
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        NotesDB.deleteNote(id);
                        response = "OK|Note deleted";
                    } catch (NumberFormatException e) {
                        response = "ERROR|Invalid ID for DELETE";
                    }

                // UPDATE|id|new text
                } else if (inMsg.startsWith("UPDATE|")) {
                    String[] parts = inMsg.split("\\|", 3);
                    if (parts.length < 3) {
                        response = "ERROR|Bad UPDATE format";
                    } else {
                        try {
                            int id = Integer.parseInt(parts[1].trim());
                            String newText = parts[2];
                            NotesDB.updateNote(id, newText);
                            response = "OK|Note updated";
                        } catch (NumberFormatException e) {
                            response = "ERROR|Invalid ID for UPDATE";
                        }
                    }

                // EXIT
                } else if (inMsg.equals("EXIT")) {
                    response = "OK|Goodbye";
                    socketWriter.write(response + "\n");
                    socketWriter.flush();
                    break; // leave loop and close socket

                } else {
                    response = "ERROR|Unknown command";
                }

                socketWriter.write(response + "\n");
                socketWriter.flush();
            }

            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
