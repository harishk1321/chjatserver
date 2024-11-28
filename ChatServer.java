    import java.io.*;
    import java.net.*;
    import java.util.*;
    
    public class ChatServer {
        private static Set<ClientHandler> clientHandlers = new HashSet<>();
    
        public static void main(String[] args) {
            System.out.println("Chat server is running...");
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                System.out.println("Error in server: " + e.getMessage());
            }
        }
    
        // Broadcast message to all clients
        public static void broadcastMessage(String message, ClientHandler excludeClient) {
            for (ClientHandler client : clientHandlers) {
                if (client != excludeClient) {
                    client.sendMessage(message);
                }
            }
        }
    
        // Remove client from the active client list
        public static void removeClient(ClientHandler clientHandler) {
            clientHandlers.remove(clientHandler);
            System.out.println("A client has disconnected");
        }
    }
    
    class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
    
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
    
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Received: " + clientMessage);
                    ChatServer.broadcastMessage(clientMessage, this);
                }
            } catch (IOException e) {
                System.out.println("Error in client handler: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
                ChatServer.removeClient(this);
            }
        }
    
        // Send a message to the client
        public void sendMessage(String message) {
            out.println(message);
        }
    }
    

