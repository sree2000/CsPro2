package place.client.ptui;

import place.PlaceBoard;
import place.PlaceColor;
import place.client.NetworkClient;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * The PTUI representation of the Place client.
 *
 * @author John McCarroll & Sree Jupudy
 */

public class PlacePTUI implements Observer {

    /* holds state of the board for view */
    PlaceBoard board;
    /* the helper class that handles all the networking with the server */
    NetworkClient connection;
    /* a Scanner to read command line input */
    Scanner stdIn;
    /* the username of the client */
    String username;

    /**
     * get board from server
     * @return
     */
    public PlaceBoard receiveBoard(){
        return connection.getBoard(this);
    }

    /**
     * a helper method to display the board state on the command line
     */
    public void display(){
        System.out.println(board);
    }

    /**
     * The overriden update method to update the board state when it the model is changed
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        this.board = this.connection.getModel();
        this.display();
    }

    /**
     * Takes in a string command and parses it to send a tile change request to server
     * @param command
     */
    public void changeTile(String command){
        String[] fields = command.split(" ");
        if (fields.length != 3){
            System.out.println("Usage: row col color");
        } else {
            PlaceColor color = parseColor(fields[2]);
            connection.sendTile(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), color, username);
        }
    }

    /**
     * A helper method that returns a PlaceColor from a string
     * @param str
     * @return
     */
    public PlaceColor parseColor(String str){
        switch (str) {
            case "0":
                return PlaceColor.BLACK;
            case "1":
                return PlaceColor.GRAY;
            case "2":
                return PlaceColor.SILVER;
            case "3":
                return PlaceColor.WHITE;
            case "4":
                return PlaceColor.MAROON;
            case "5":
                return PlaceColor.RED;
            case "6":
                return PlaceColor.OLIVE;
            case "7":
                return PlaceColor.YELLOW;
            case "8":
                return PlaceColor.GREEN;
            case "9":
                return PlaceColor.LIME;
            case "A":
                return PlaceColor.TEAL;
            case "B":
                return PlaceColor.AQUA;
            case "C":
                return PlaceColor.NAVY;
            case "D":
                return PlaceColor.BLUE;
            case "E":
                return PlaceColor.PURPLE;
            case "F":
                return PlaceColor.FUCHSIA;
            default:
                return null;
        }
    }

    /**
     * The main method - sets up PlacePTUI object and board, activates the Connection thread to listen for updates, and
     * begins loop to take command line input
     * @param args command line arguments on launch
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
        } else {

            // set up
            PlacePTUI ptui = new PlacePTUI();
            ptui.connection = new NetworkClient(args[0], Integer.parseInt(args[1]), args[2]);
            ptui.username = args[2];
            ptui.stdIn = new Scanner(System.in);

            // board
            ptui.board = ptui.receiveBoard();
            ptui.display();

            // run
            Thread input = new Thread(ptui.connection);
            input.start();

            // scanner
            System.out.println("Enter: row col color");
            while (ptui.stdIn.hasNextLine()){
                ptui.changeTile(ptui.stdIn.nextLine());
            }
        }
    }
}