package place.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServerClientThread extends Thread {
	Socket serverClient;
	int clientNo;
	int squre;
	int num;
	PrintWriter outStream;

	ServerClientThread(Socket inSocket, int counter) {
		serverClient = inSocket;
		clientNo = counter;
	}

	public void run() {
		try {
			InputStreamReader in = new InputStreamReader(serverClient.getInputStream());
			BufferedReader inStream = new BufferedReader(in);
			outStream = new PrintWriter(serverClient.getOutputStream());
			String clientMessage = "", serverMessage = "";
			while (!clientMessage.equals("bye")) {
				clientMessage = inStream.readLine();
				num = Integer.parseInt(clientMessage);
				System.out.println("From Client-" + clientNo + ": Number is :" + clientMessage);
				squre = Integer.parseInt(clientMessage) * Integer.parseInt(clientMessage);
				serverMessage = "From Server to Client-" + clientNo + " Square of " + clientMessage + " is " + squre;
				outStream.println(serverMessage);
				outStream.flush();
			}
			inStream.close();
			outStream.close();
			serverClient.close();
		} catch (Exception ex) {
			System.out.println(ex);
		} finally {
			System.out.println("Client -" + clientNo + " exit!! ");
		}
	}
	
	public void prt(){
		try {
		outStream = new PrintWriter(serverClient.getOutputStream());
		String serverMessage = "From Server to Client-" + clientNo + ": Number is :" + num;
		outStream.println(serverMessage);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
