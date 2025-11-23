
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class server {

    static ArrayList<String> users = new ArrayList<>();
    static ConcurrentHashMap<String, ArrayList<String>> chats = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            // https://link.springer.com/chapter/10.1007/978-1-4842-7135-3_8#PC18

            ServerSocket serverSocket = new ServerSocket(9000, 100,
                    InetAddress.getByName("localhost"));
            System.out.println("Server started at: " +
                    serverSocket);
         
            while (true) {
                System.out.println(
                        "Waiting for a connection...");
                // Accept a connection
                final Socket activeSocket = serverSocket.accept();
                System.out.println(
                        "Received a connection from " +
                                activeSocket);
             
                Runnable runnable = () -> handleClientRequest(activeSocket);
                new Thread(runnable).start();
              
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleClientRequest(Socket socket) {
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;

        try {
            String currentUser = "";

        
            socketReader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            socketWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream()));
            String inMsg = null;
            while ((inMsg = socketReader.readLine()) != null) {

                System.out.println(
                        "Received from client: " + inMsg);

                if (inMsg.startsWith("LOGIN")) {
    String username = inMsg.substring(6).trim();
    currentUser = username;

    if (!users.contains(username)) {
        users.add(username);
    }

    String returnMessage = "LOGIN Successful-";

    chats.putIfAbsent(username, new ArrayList<>());
    ArrayList<String> messages = chats.get(username);

    chats.put(username, new ArrayList<>());

    for (String message : messages) {
        returnMessage += message + "-";
    }

    socketWriter.write(returnMessage + "\n");
    socketWriter.flush();

    System.out.println("Logged in user " + username);
}
 else if (inMsg.startsWith("LOGOUT") && !currentUser.isEmpty()) {
                    users.remove(currentUser);
                    System.out.println("logged out user " + currentUser);
                    socketWriter.write("LOGOUT successful\n");
                    socketWriter.flush();
                    currentUser = "";
                }

                else if (inMsg.startsWith("LIST_USERS") && !currentUser.isEmpty()) {
                    String message = String.join(",", users);
                    socketWriter.write(message);
                    socketWriter.write("\n");
                    socketWriter.flush();
                } else if (inMsg.startsWith("MESSAGE") && !currentUser.isEmpty()) {
                    int indexOfSecondSpace = inMsg.indexOf(' ', 8);
                    String username = inMsg.substring(8, indexOfSecondSpace);
                    String message = currentUser + ": " + inMsg.substring(indexOfSecondSpace + 1);

                    ArrayList<String> messages = chats.getOrDefault(username, new ArrayList<>());
                    synchronized (messages) {
    messages.add(message);
}


                    chats.put(username, messages);

                    socketWriter.write(message);
                    socketWriter.write("\n");
                    socketWriter.flush();

                    System.out.println("saved message: " + message);
                }

                else {
                    if (currentUser.isEmpty()) {
                        socketWriter.write("LOGIN first");
                        socketWriter.write("\n");
                        socketWriter.flush();
                    } else {
                        socketWriter.write("Unknown command");
                        socketWriter.write("\n");
                        socketWriter.flush();
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}