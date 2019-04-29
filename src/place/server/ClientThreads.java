package place.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import place.PlaceBoard;
import place.PlaceTile;
import place.client.model.Model;
import place.network.PlaceRequest;

public class ClientThreads extends Thread implements Closeable {

    private Socket connection;
    private String clientName;
    private Model board;
    private Consumer<PlaceTile> tileBeingRecived;
    private Consumer<String> attemptingLogingIn;
    private List<Runnable> onDisconnect = new ArrayList<>();
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Instant waitToPlace = Instant.EPOCH;
    private static int TIMEOUT = 2000;

    public ClientThreads(Socket socket, Model board) throws IOException, SocketTimeoutException {
        this.board = board;
        this.connection = socket;
        this.connection.setSoTimeout(TIMEOUT);
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch(SocketTimeoutException e) { 
            socket.close();
            System.out.printf(
                "Info: Client with ip address %s failed to complete ObjectStream handshake in time%n",
                socket.getInetAddress().getHostName()
            );
            throw e;
        }

        this.attemptingLogingIn = (username) -> {
            System.out.printf("Info: Login attempt from %s with username %s.%n", socket.getInetAddress().getHostName(), username);
        };
        this.tileBeingRecived = (tile) -> {
            System.out.printf("Debug: Tile recieved from client %s: %s%n", getIdentifier(), tile);
        };
        this.onDisconnect.add(() -> {
            System.out.printf("Info: Client %s disconnected.%n", getIdentifier());
        });
    }

    public void run() {
        while(true) {
            try {
                PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
                synchronized(this) {
                	if(req.getType().equals(PlaceRequest.RequestType.LOGIN)) {
                		if(clientName == null) {
                            attemptingLogingIn.accept(""+req.getData());
                        } else {
                            sendError("Username already taken please choose another");
                        }
                	}
                	else if(req.getType().equals(PlaceRequest.RequestType.CHANGE_TILE)) {
                		if(clientName == null) {
                            sendError("Login process must be complete before you place a tile");
                            continue;
                        }

                        Instant now = Instant.now();
                        if(now.isBefore(waitToPlace)) {
                            sendError("wait to place another tile for " + waitToPlace);
                            continue;
                        }
                        PlaceTile tile = (PlaceTile) req.getData();
                        int length = board.getBoard().length;
                        if(tile.getRow() < 0 || tile.getRow() >= length || tile.getCol() < 0 || tile.getCol() >= length) {
                            sendError("Tile placement must be in bounds");
                            continue;
                        }

                        synchronized(board) {

                            tile = new PlaceTile(
                                tile.getRow(),
                                tile.getCol(),
                                clientName,
                                tile.getColor(),
                                now.toEpochMilli()
                            );
                            board.setTile(tile);
                        }

                        waitToPlace = now.plusMillis(500);
                        tileBeingRecived.accept(tile);
                	}
                }
            } catch(SocketTimeoutException e) { 
                try {
                    connection.close();
                } catch(IOException e2) { }
                System.out.printf("Client " + getIdentifier() + " was too slow to login %n");
            } catch(IOException e) {
                try {
                    connection.close();
                } catch(IOException e2) { }
                onDisconnect.forEach(Runnable::run);
                e.printStackTrace();
                break;
            } catch(ClassNotFoundException e) {
                System.out.printf("Incorrect data recived from client " + getIdentifier());
            }
        }
    }

    public synchronized void tileChange(PlaceTile tile) {
        try {
            out.writeUnshared(
                new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, tile)
            );
        } catch(IOException e) {
        	System.out.println("error");
        } 
    }

    public synchronized void sendError(String msg) {
        try {
            out.writeUnshared(
                new PlaceRequest<>(PlaceRequest.RequestType.ERROR, msg)
            );
        } catch(IOException e) {
        	System.out.println("error");
        } 
    }

    public synchronized void usernameAssignments(String username) throws IllegalStateException {
        if(clientName == null) {
            clientName = username;
            try {
                out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, username));
                out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board));
                connection.setSoTimeout(0); 
            } catch(IOException e) {} 
        } else {
            throw new IllegalStateException("Username has already been taken");
        }
    }
    public synchronized void registerOnLoginAttempt(Consumer<String> loginAttempt) {
        attemptingLogingIn = attemptingLogingIn.andThen(loginAttempt);
    }
    public synchronized void registerOnTileReceived(Consumer<PlaceTile> tileReceived) {
        tileBeingRecived = tileBeingRecived.andThen(tileReceived);
    }

    public synchronized void registerOnDisconnect(Runnable disconnect) {
        onDisconnect.add(disconnect);
    }

    public String getIdentifier() {
        if(clientName == null) {
            return "with ip address " + connection.getInetAddress().getHostName();
        } else {
            return "with username " + clientName;
        }
    }

    public String getUsername() {
        return clientName;
    }

    public Socket getSocket() {
        return connection;
    }

    public void close() throws IOException {
        connection.close();
    }
}
