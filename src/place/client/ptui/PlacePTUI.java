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