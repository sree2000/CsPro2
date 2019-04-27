package place.client;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;
import place.client.model.Model;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Observer;
import java.util.Scanner;

import static place.network.PlaceRequest.RequestType.CHANGE_TILE;
import static place.network.PlaceRequest.RequestType.LOGIN;

/**
 * The client side network interface to a Reversi game server.
 * Each of the two players in a game gets its own connection to the server.
 * This class represents the controller part of a model-view-controller
 * triumvirate, in that part of its purpose is to forward user actions
 * to the remote server.
 *
 * @author Robert St Jacques @ RIT SE
 * @author Sean Strout @ RIT CS
 * @author James Heliotis @ RIT CS
 */
public class NetworkClient implements Runnable {

    /**
     * The {@link Socket} used to communicate with the reversi server.
     */
    private Socket socket;

    /**
     * The {@link Scanner} used to read requests from the reversi server.
     */
    private ObjectInputStream in;

    /**
     * The {@link PrintStream} used to write responses to the reversi server.
     */
    private ObjectOutputStream out;

    /**
     * The {@link PlaceBoard} used to keep track of the state of the game.
     */
    private PlaceBoard board;       // i think this should be the model that the clients observe

//    /**
//     * Sentinel used to control the main game loop.
//     */
//    private boolean go;
//
//    /**
//     * Accessor that takes multithreaded access into account
//     *
//     * @return whether it ok to continue or not
//     */
//    private synchronized boolean goodToGo() {
//        return this.go;
//    }
//
//    /**
//     * Multithread-safe mutator
//     */
//    private synchronized void stop() {
//        this.go = false;
//    }

    /**
     * Hook up with a Reversi game server already running and waiting for
     * two players to connect. Because of the nature of the server
     * protocol, this constructor actually blocks waiting for the first
     * message from the server that tells it how big the board will be.
     * Afterwards a thread that listens for server messages and forwards
     * them to the game object is started.
     *
     * @param hostName the name of the host running the server program
     * @param portNumber     the port of the server socket on which the server is
     *                 listening
     * @param username    the local object holding the state of the game that
     *                 must be updated upon receiving server messages
     */
    public NetworkClient( String hostName, int portNumber, String username ) {
        System.out.println("Place client connecting to " + hostName + ":" + portNumber);

        try {
            socket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            login(username);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            close();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            close();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Class of server message not found");
            close();
            System.exit(1);
        }
    }


    /**
     * Called by the constructor to set up the game board for this player now
     * that the server has sent the board dimensions with the
     * {@link } request.
     *
     * @param username
     */
    public void login( String username ) throws ClassNotFoundException, IOException {
        //username
        out.writeUnshared(new PlaceRequest<String>(LOGIN, username));
        System.out.println("attempting login");

        // login success?
        PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
        if (req.getType().equals(PlaceRequest.RequestType.LOGIN_SUCCESS) && req.getData().equals(username)) {
            System.out.println("Connection Successful");
        } else {
            System.out.println("Username taken");
            close();
            System.exit(1);
        }
    }

    /**
     * Called when the server sends a message saying that
     * gameplay is damaged. Ends the game.
     */
//    public void error( String arguments ) {
//        NetworkClient.dPrint( '!' + ERROR + ',' + arguments );
//        dPrint( "Fatal error: " + arguments );
//        this.game.error( arguments );
//        this.stop();
//    }

    public PlaceBoard getBoard(Observer observer){
        try {
            PlaceRequest<?> request = (PlaceRequest<?>) in.readUnshared();
            board = (PlaceBoard) request.getData();
            board.addObserver(observer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return board;   //will get board from model
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close() {
            try {
                socket.close();
                out.close();
                in.close();

            } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    /**
     * send tile
     *
     * @param row
     * @param col
     * @param color
     * @param username
     */
    public void sendTile(int row, int col, PlaceColor color, String username ) {
        try {
            PlaceTile newTile = new PlaceTile(row, col, username, color, 0L); //need proper timestamp
            this.out.writeUnshared(new PlaceRequest<PlaceTile>(CHANGE_TILE, newTile));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void updateModel(PlaceTile tile){
        board.setTile(tile);
    }

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    public void run() {

        while ( true ) {    // need boolean arg?
            try {

                    PlaceRequest<?> request = (PlaceRequest<?>) in.readUnshared();

                if ( request.getType().equals(PlaceRequest.RequestType.TILE_CHANGED) ) {

                    updateModel((PlaceTile) request.getData());
                } else {
                        System.err.println( "Unrecognized request: " + request );
                        break;
                }
            }
            catch( NoSuchElementException nse ) {
                // Looks like the connection shut down.
                nse.printStackTrace();
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
        this.close();
    }

    public PlaceBoard getModel(){
        return board;
    }
}
