import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ServerHandler implements Runnable {
	private Socket socket;
	private ConcurrentHashMap<String, String[]> activePeer;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private String clientInfo;
	private String hostName;
	private int[] RFCs;

	public ServerHandler(Socket socket, ConcurrentHashMap<String, String[]> activePeer) {
		this.socket = socket;
		this.activePeer = activePeer;

		try {
			this.inputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("==========InputStream IOException ==========");
			e.printStackTrace();
		}
		try {
			outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("==========OutputStream IOException ==========");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			System.out.println(inputStream.readUTF()); // 1. Hello message from client
			outputStream.writeUTF("From server: Connected");
			clientInfo = inputStream.readUTF(); // 2. Client info and RFCs
			String[] clientInfoArray = clientInfo.split(" ");
			hostName = clientInfoArray[0];
			System.out.println("raw client info: " + Arrays.toString(clientInfoArray));
			String[] RFCArray = new String[clientInfoArray.length - 1];
			for (int i = 0; i < RFCArray.length; i++) {
				RFCArray[i] = clientInfoArray[i + 1];
			}
			activePeer.put(clientInfoArray[0], RFCArray);

			/*
			 * boolean connected = true; while (connected) { int option =
			 * inputStream.readInt(); if (option == 1) {
			 * System.out.println("Client asks me to print 1"); } else if (option == 2) {
			 * System.out.println("Client asks me to print 2"); } else if (option == 0) {
			 * System.out.println("Client closed"); connected = false; } else { ; } }
			 */

			System.out.println(Arrays.toString(activePeer.get(hostName)));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
