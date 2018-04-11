import java.io.*;
import java.util.HashMap;
import java.net.*;

public class CentralizedServer extends Thread {
	private static final int PORT_NUMBER = 7734;
	private HashMap<String, String> map = new HashMap<String, String>();
	private ServerSocket serverSocket;

	public CentralizedServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();

				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				map.put(server.getRemoteSocketAddress().toString(), "123");
				DataInputStream in = new DataInputStream(server.getInputStream());

				System.out.println(in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\n");
				out.writeUTF("Here is the HashMap: " + map.toString() + "\n");
				// server.close();

			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		int port = PORT_NUMBER;
		try {
			Thread t = new CentralizedServer(port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
