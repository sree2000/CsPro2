package place.client.ptui;

import place.PlaceBoard;
import place.network.PlaceRequest;

import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.Observer;

public class PlacePTUI implements Observer {

    // board
    PlaceBoard board;
    // todo
    // create socket, connect w/ server, establish I/O object (unmodified) streams, and hand logic for sending PlaceTile objects

    /* The socket connection */
    private Socket socket;
    /* The output stream to the server*/
    private PrintWriter out;
    /* The input stream from the ReversiServer */
    private ObjectInputStream in;
    /* The Input stream from the command line */
    private BufferedReader stdIn;

    public void connect(String hostName, int portNumber, String username){
        System.out.println("Place client connecting to " + hostName + ":" + portNumber);

        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new ObjectInputStream(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            //username
            out.print(username);
            PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
            if (req.getType() == PlaceRequest.RequestType.BOARD) {
                PlaceBoard board = (PlaceBoard) req.getData();
            } else {      //need way of vvetting...
                System.out.println("Username taken");
                close();
                System.exit(0);
            }
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
     * A helper method to close IO streams
     */
    public void close(){
        try {
            socket.close();
            out.close();
            in.close();
            stdIn.close();

        } catch (IOException ioe) { ioe.printStackTrace();}
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
        } else {
            // set up
            PlacePTUI ptui = new PlacePTUI();
            ptui.connect(args[0], Integer.parseInt(args[1]), args[2]);

            // board

        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String username = args[2];

        Socket s = new Socket(hostName, portNumber);
        
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        
        InputStreamReader in  = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        
        
    }
}
