package place.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SCT {
	Socket serverClient;
	int clientNo;
	int squre;

	SCT(Socket inSocket, int counter) {
		serverClient = inSocket;
		clientNo = counter;
	}

	public void run() {
		try {
			InputStreamReader in = new InputStreamReader(serverClient.getInputStream());
			BufferedReader inStream = new BufferedReader(in);
			PrintWriter outStream = new PrintWriter(serverClient.getOutputStream());
			String clientMessage = "", serverMessage = "";
			while (!clientMessage.equals("bye")) {
				clientMessage = inStream.readLine();
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
}
