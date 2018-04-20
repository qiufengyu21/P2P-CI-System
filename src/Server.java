import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	public static void main(String[] args) {
		// int port = Integer.parseInt(args[0]);
		ConcurrentHashMap<Integer, int[]> m = new ConcurrentHashMap<Integer, int[]>();
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(7734);
		} catch (IOException e) {
			System.out.println("Server cannot be initialized.");
		}

		while (true) {
			try {
				socket = serverSocket.accept();

				/* Spawn a new thread for each connected peer */
				Thread t = new Thread(new ServerHandler(socket, m));
				t.start();
			} catch (SocketTimeoutException s) {

				System.out.println("Socket timed out.");
			} catch (IOException e) {

				System.out.println("IOException: " + e);
			}

		}

	}
}
