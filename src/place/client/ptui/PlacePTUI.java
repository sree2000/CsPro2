package place.client.ptui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class PlacePTUI implements Observer {
    
	@Override
    public void update(Observable o, Object arg) {

    }
    public static void main(String[] args) throws UnknownHostException, IOException {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
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
