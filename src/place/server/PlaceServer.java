package place.server;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import place.PlaceTile;
import place.client.model.Model;

public class PlaceServer extends Thread {

    private Model board;
    private Map<ClientThreads, InetAddress> clientList = new HashMap<>();
    private Set<InetAddress> clientsWaiting = new HashSet<>();
    private ServerSocket primarySocket;
    private int port;

    public PlaceServer(int port, int size) {
        this.port = port;
        this.board = new Model(size);
    }
    public void run() {
        try {
            primarySocket = new ServerSocket(port);
            while(true) {
                Socket connection = primarySocket.accept();

                new Thread(() -> {

                    InetAddress address = connection.getInetAddress();
                    synchronized(clientsWaiting) {
                        if(clientsWaiting.contains(address)) {
                            System.out.printf("Info: Rejecting connection " +
                                    "from %s due to pre-existing pending " +
                                    "connection.%n", address.getHostName());
                            try {
                                connection.close();
                            } catch(IOException e) {
                            	System.out.println("error");
                            }
                            return;
                        }
                        else {
                            clientsWaiting.add(address);
                        }
                    }

                    ClientThreads client;
                    try {
                        client = new ClientThreads(connection, board);
                        clientList.put(client, address);

                    } catch (SocketTimeoutException e) { // Thrown if setting up the connection times out.
                    	System.out.println("Timed out");
                    	return;
                    } catch (StreamCorruptedException e) {
                        System.out.println("Corrupted stream");
                        return;
                    } catch(IOException e) {
                        System.out.println( "error - " + e);
                        return;
                    } finally {
                        synchronized(clientsWaiting) {
                            clientsWaiting.remove(address);
                        }
                    }
                    synchronized(this) {
                        System.out.println(clientList.size());
                        if (clientList.size() > (board.DIM/2)) {
                            System.out.printf("Too many people are connected "
                            		+ "please try again " + address.getHostName());
                            client.sendError("Too many people were connected to " +
                                    "Please try connecting ");
                            removeClient(client);
                            try {
                                connection.close();
                            } catch (IOException e) {
                            	System.out.println( "error - " + e);
                            }
                            return;
                        }
                    }

                    client.registerOnLoginAttempt(uname -> loginClient(client, uname));
                    client.registerOnTileReceived(this::sendToAll);
                    client.registerOnDisconnect(() -> removeClient(client));

                    System.out.printf("Info: Client %s connected.%n", client.getIdentifier());
                    client.start();

                }).start();
            }
        } catch (SecurityException e) {
            System.err.println("Failed to bind to port: permision denied");
            System.err.println(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        if(args.length <2) {
            System.out.println("Usage: java PlaceServer <port> <size>/n Not enough arguments");
            System.exit(-1);
        } else {
            try {
                new PlaceServer(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1])
                ).start();
            } catch (NumberFormatException e) {
                System.out.println("All arguments must be integers.");
                System.exit(-1);
            }
        }
    }
    
    private void loginClient(ClientThreads client, String requestedUsername) {
    	int ID = 0;
        String username = requestedUsername;
        HashSet<String> usernames;
        synchronized(this) {
            usernames = clientList.keySet()
                                    .stream()
                                    .map(ClientThreads::getUsername)
                                    .collect(Collectors.toCollection(HashSet::new));
        }
        while(usernames.contains(username)) {
            username = requestedUsername + ++ID;
        }
        client.usernameAssignments(username);
    }
    
    private void sendToAll(PlaceTile tile) {
        clientList.keySet().stream().parallel().forEach(c -> c.tileChange(tile));
    }
    
    private void removeClient(ClientThreads client) {
        clientList.remove(client.getSocket().getInetAddress(), client);
    }
}
