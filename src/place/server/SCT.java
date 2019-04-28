package place.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import place.PlaceBoard;
import place.PlaceTile;
import place.network.PlaceRequest;

class SCT extends Thread {
    Socket serverClient;
    int clientNo;
    int squre;
    PlaceBoard board;

    SCT(Socket inSocket, int counter, PlaceBoard board) {
        serverClient = inSocket;
        clientNo = counter;
        this.board = board;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(serverClient.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(serverClient.getOutputStream());
            PlaceRequest<?> req = null;
            req = (PlaceRequest<?>)in.readUnshared();
            System.out.println("From Client-" + clientNo + ": message is :" + req);//just a checker
            if(req.getType().equals(PlaceRequest.RequestType.LOGIN)) {
                if(UsernameChecker.sameName((String)req.getData())){
                    out.writeUnshared(PlaceRequest.RequestType.ERROR);
                }
                else {
                    out.writeUnshared(PlaceRequest.RequestType.LOGIN_SUCCESS);
                    out.writeUnshared(PlaceRequest.RequestType.BOARD);
                }
            }
            while (!req.equals("bye")) {
                req = (PlaceRequest<?>)in.readUnshared();
                if(req.getType().equals(PlaceRequest.RequestType.CHANGE_TILE)) {
                    if(board.isValid((PlaceTile) req.getData())) {
                        board.setTile((PlaceTile) req.getData());
                        out.writeUnshared(PlaceRequest.RequestType.TILE_CHANGED);
                    }
                    else
                        out.writeUnshared(PlaceRequest.RequestType.ERROR);
                }

//				serverMessage = "From Server to Client-" + clientNo + " Square of " + req + " is " + squre;
//				out.println(serverMessage);
//				out.flush();
            }
            in.close();
            out.close();
            serverClient.close();
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            System.out.println("Client -" + clientNo + " exit!! ");
        }
    }
}
