import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class server {

    static CopyOnWriteArrayList<String> users = new CopyOnWriteArrayList<>();
    static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> chats = new ConcurrentHashMap<>();

    public static void main(String[] args) {
         try {

            ServerSocket serverSocket =
                    new ServerSocket(9000, 100,
                            InetAddress.getByName("localhost"));
            System.out.println("Server started at: " +
                    serverSocket);
          
            while (true) {
                System.out.println(
                        "Waiting for a connection...");
    
                final Socket activeSocket =
                        serverSocket.accept();
                System.out.println(
                        "Received a connection from " +
                                activeSocket);

                Runnable runnable = () ->
                        handleClientRequest(activeSocket);
                new Thread(runnable).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleClientRequest(Socket socket) {
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String currentUser = "";
            String inMsg;

            while ((inMsg = socketReader.readLine()) != null) {
                System.out.println("Received from client: " + inMsg);

                if (inMsg.startsWith("LOGIN")) {
                    String username = inMsg.substring(6).trim();
                    currentUser = username;

                    if (!users.contains(username)) {
                        users.add(username);
                    }

                    chats.putIfAbsent(username, new CopyOnWriteArrayList<>());
                    CopyOnWriteArrayList<String> messages = chats.get(username);

                    String returnMessage = "LOGIN Successful-";
                    for (String m : messages) {
                        returnMessage += m + "-";
                    }
                    messages.clear();

                    socketWriter.write(returnMessage + "\n");
                    socketWriter.flush();

                    System.out.println("Logged in user " + username);
                }

                else if (inMsg.startsWith("LOGOUT") && !currentUser.isEmpty()) {
                    users.remove(currentUser);
                    socketWriter.write("LOGOUT successful\n");
                    socketWriter.flush();
                    System.out.println("logged out user " + currentUser);
                    currentUser = "";
                }

                else if (inMsg.startsWith("LIST_USERS") && !currentUser.isEmpty()) {
                    socketWriter.write(String.join(",", users) + "\n");
                    socketWriter.flush();
                }

                else if (inMsg.startsWith("NEW NOTE") && !currentUser.isEmpty()) {
                    int indexOfSecondSpace = inMsg.indexOf(' ', 8);
                    String targetUser = inMsg.substring(8, indexOfSecondSpace);
                    String message = currentUser + ": " + inMsg.substring(indexOfSecondSpace + 1);

                    chats.putIfAbsent(targetUser, new CopyOnWriteArrayList<>());
                    chats.get(targetUser).add(message);

                    socketWriter.write("DELIVERED\n");
                    socketWriter.flush();

                    System.out.println("saved message: " + message);
                }
                else if(inMsg.startsWith("DELETE NOTE") && !currentUser.isEmpty())
                 {
                    socketWriter.write("NOTE DELETED");
                    socketWriter.flush();
                    System.out.println("Note deleted for " + currentUser);

                 }
                 else if(inMsg.startsWith("WRITE NOTE") && !currentUser.isEmpty())
                 {
                    socketWriter.write("NOTE EDITED");
                    socketWriter.flush();
                    System.out.println("Note edited for " + currentUser);
                 }
                else if(inMsg.startsWith("READ NOTE") && !currentUser.isEmpty())
                 {
                    socketWriter.write("NOTE OPENED");
                    socketWriter.flush();
                    System.out.println("Note opened for " + currentUser);

                 } else if (inMsg.startsWith("SHUTDOWN") && !currentUser.isEmpty()) {
                    socketWriter.write("SHUTTING SERVER DOWN\n");
                    socketWriter.flush();
                    System.out.println("Server shutting down");
                    System.exit(0);
                } else {
                    if (currentUser.isEmpty()) {
                        socketWriter.write("LOGIN first\n");
                    } else {
                        socketWriter.write("Unknown command\n");
                    }
                    socketWriter.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
