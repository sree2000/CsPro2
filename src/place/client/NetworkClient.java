package place.client;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;
import place.client.model.Model;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Observer;
import java.util.Scanner;

import static place.network.PlaceRequest.RequestType.CHANGE_TILE;
import static place.network.PlaceRequest.RequestType.LOGIN;

/**
 * The client side network interface to a Place server.
 *
 * @author John McCarroll & Sree Jupudy
 */
public class NetworkClient implements Runnable {

    /* The Socket used to communicate with the Place server */
    private Socket socket;
    /* The ObjectInputStream used to read requests from the Place server */
    private ObjectInputStream in;
    /* The ObjectOutputStream used to write responses to the Place server */
    private ObjectOutputStream out;
    /* The Model used to keep track of the state of the game */
    private Model board;
    /* a boolean to determine how long NetworkClient listens for updates */
    private boolean running = true;


    /**
     * Establishes client side socket connection and an in and out stream for communication with server. Calls login.
     *
     * @param hostName the name of the host running the server program
     * @param portNumber the port of the server socket on which the server is listening
     * @param username the username of the client
     */
    public NetworkClient( String hostName, int portNumber, String username ) {
        System.out.println("Place client connecting to " + hostName + ":" + portNumber);

        try {
            socket = new Socket(hostName, portNumber);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("ay");

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
     * Called by the constructor to establish valid login criteria for client
     *
     * @param username
     */
    public void login( String username ) throws ClassNotFoundException, IOException {
        //username
        out.writeUnshared(new PlaceRequest<String>(LOGIN, username));
        System.out.println("Attempting login");

        // login success?
        PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
        if (req.getType().equals(PlaceRequest.RequestType.LOGIN_SUCCESS) && req.getData().equals(username)) {
            System.out.println("Login Successful");
        } else {
            System.out.println("Username taken");
            close();
            System.exit(1);
        }
    }

    /**
     * Receives the board PlaceRequest from server and establishes client UI view and observer
     * @param observer the view
     * @return the PlaceBoard from server
     */
    public PlaceBoard getBoard(Observer observer){
        try {
            PlaceRequest<?> request = (PlaceRequest<?>) in.readUnshared();
            board = (Model) request.getData();
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
     * Creates and sends a PlaceTile object to server
     *
     * @param row the row of the new tile
     * @param col the column of the new tile
     * @param color the PlaceColor of the new tile
     * @param username the username of the client
     */
    public void sendTile(int row, int col, PlaceColor color, String username ) {
        Date date = new Date();
        long time = date.getTime();

        try {
            PlaceTile newTile = new PlaceTile(row, col, username, color, time);
            this.out.writeUnshared(new PlaceRequest<PlaceTile>(CHANGE_TILE, newTile));
        } catch (IOException e){
            e.printStackTrace();
            close();
        }
    }

    /**
     * A helper method that updates the proxy model when an update is sent by the server
     * @param tile
     */
    public void updateModel(PlaceTile tile){
        board.setTile(tile);
    }

    /**
     * Run the main client loop. Listens for and handles updates about the model from the server
     */
    public void run() {

        while ( running ) {
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

    /**
     * Simple board getter
     * @return Placeboard proxy model
     */
    public PlaceBoard getModel(){
        return board;
    }

    /**
     * Simple running setter
     * @param bool state of running
     */
    public void setRunning(boolean bool){
        this.running = bool;
    }
}
