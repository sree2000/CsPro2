package place.client.ptui;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;
import place.client.NetworkClient;
import place.client.model.Model;
import place.network.PlaceRequest;

import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import static place.network.PlaceRequest.RequestType.LOGIN;

public class PlacePTUI implements Observer {

    // board
    PlaceBoard board;
    // create socket, connect w/ server, establish I/O object (unmodified) streams, and hand logic for sending PlaceTile objects

    NetworkClient connection;

    Scanner stdIn;

    String username;

//    /**
//     * establish connections to server
//     * @param hostName
//     * @param portNumber
//     * @param username
//     * @return
//     */
//    public boolean connect(String hostName, int portNumber, String username){
//        System.out.println("Place client connecting to " + hostName + ":" + portNumber);
//
//        try {
//            socket = new Socket(hostName, portNumber);
//            out = new ObjectOutputStream(socket.getOutputStream());
//            in = new ObjectInputStream(socket.getInputStream());
//            stdIn = new BufferedReader(new InputStreamReader(System.in));
//
//            //username
//            out.writeUnshared(new PlaceRequest<String>(LOGIN, username));
//            System.out.println("attempting login");
//
//            // login success?
//            PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
//            if (req.getType().equals(PlaceRequest.RequestType.LOGIN_SUCCESS) && req.getData().equals(username)) {
//                return true;
//            } else {
//                System.out.println("Username taken");
//                close();
//                System.exit(1);
//            }
//
//        } catch (UnknownHostException e) {
//            System.err.println("Don't know about host " + hostName);
//            close();
//            System.exit(1);
//        } catch (IOException e) {
//            System.err.println("Couldn't get I/O for the connection to " + hostName);
//            close();
//            System.exit(1);
//        } catch (ClassNotFoundException e) {
//            System.err.println("Class of server message not found");
//            close();
//            System.exit(1);
//        }
//        return false;
//    }

    /**
     * get board from server
     * @return
     */
    public PlaceBoard receiveBoard(){
        return connection.getBoard(this);
    }

    public void display(){
        System.out.println(board);
    }

//    /**
//     * A helper method to close IO streams
//     */
//    public void close(){
//        try {
//            socket.close();
//            out.close();
//            in.close();
//            stdIn.close();
//
//        } catch (IOException ioe) { ioe.printStackTrace();}
//    }

    @Override
    public void update(Observable o, Object arg) {
        this.board = this.connection.getModel();
        this.display();
    }

    public void changeTile(String command){
        String[] fields = command.split(" ");
        if (fields.length != 3){
            System.out.println("Usage: row col color");
        } else {
            PlaceColor color = parseColor(fields[2]);
            connection.sendTile(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), color, username);
        }
    }

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