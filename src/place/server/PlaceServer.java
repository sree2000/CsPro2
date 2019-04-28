package place.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import place.PlaceBoard;

public class PlaceServer {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java ReversiServer <port> <DIM>");
            System.exit(1);
        }
        int dim = Integer.parseInt(args[0]);
        ServerSocket ss=new ServerSocket(Integer.parseInt(args[1]));
        System.out.println("waiting for Player one ...");
        int counter = 0;
        PlaceBoard board = new PlaceBoard(dim);
        while (true) {
            counter++;
            Socket serverClient = ss.accept();
            System.out.println("Client No:" + counter + " started!");
            SCT sct = new SCT(serverClient, counter, board);
            sct.start();
        }
    }
}
