package place.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
//		if (args.length != 2) {
//			System.err.println("Usage: java ReversiServer <port> <DIM>");
//			System.exit(1);
//		}
		//int dim = Integer.parseInt(args[0]);
		ServerSocket ss=new ServerSocket(8888);
		System.out.println("waiting for Player one ...");
		//ServerSocket ss = new ServerSocket(portNumber);
		int counter = 0;
		while (true) {
			counter++;
			Socket serverClient = ss.accept(); // server accept the client connection request
			System.out.println("Client No:" + counter + " started!");
			ServerClientThread sct = new ServerClientThread(serverClient, counter); // send the request to a separate																	// thread
			sct.start();
		}
	}
}
