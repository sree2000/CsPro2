package place.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws Exception {
		try {
			Socket socket = new Socket("127.0.0.1", 8888);
			InputStreamReader inStream = new InputStreamReader(socket.getInputStream());
			PrintWriter outStream = new PrintWriter(socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String clientMessage = "", serverMessage = "";
			while (!clientMessage.equals("bye")) {
				System.out.println("Enter number :");
				
				clientMessage = br.readLine();
				outStream.println(clientMessage);
				outStream.flush();
				serverMessage = br.readLine();
				System.out.println(serverMessage);
			}
			outStream.close();
			outStream.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
