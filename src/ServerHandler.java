import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ServerHandler implements Runnable {
	private Socket socket;
	private ConcurrentHashMap<String, String[][]> activePeer;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private String clientInfo;
	private int uploadPort;
	private int numOfRFCs;
	private int[] RFCs;

	public ServerHandler(Socket socket, ConcurrentHashMap<String, String[][]> activePeer) {
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
			System.out.println(inputStream.readUTF()); // in: Hello message from client
			outputStream.writeUTF("From server: Connected"); // out: connected message to client
			clientInfo = inputStream.readUTF(); // in: Client info and RFCs
			uploadPort = inputStream.readInt(); // in: upload port number
			System.out.println("Connected to " + clientInfo);
			numOfRFCs = inputStream.readInt(); // in: Number of RFCs
			String[][] mapValue = new String[numOfRFCs][4];
			String mapKey = clientInfo + ":" + uploadPort;

			for (int i = 0; i < numOfRFCs; i++) {
				String addRequest = inputStream.readUTF();
				String[] addRequestParsed = addRequest.split("\r\n");
				String[] addRequestFirstLine = addRequestParsed[0].split(" ");
				String[] addRequestHostName = addRequestParsed[1].split(":");
				String[] addRequestPort = addRequestParsed[2].split(":");
				String[] addRequestTitle = addRequestParsed[3].split(":");
				mapValue[i][0] = addRequestFirstLine[1].trim();
				mapValue[i][1] = addRequestHostName[1].trim();
				mapValue[i][2] = addRequestPort[1].trim();
				mapValue[i][3] = addRequestTitle[1].trim();
			}
			activePeer.put(mapKey, mapValue);

			boolean connected = true;
			while (connected) {
				int option = inputStream.readInt(); // in: client option
				if (option == 1) { // list all available RFCs
					String listAllRequest = inputStream.readUTF();
					String responseMessage1;
					String statusCode = "400 Bad Request";
					int count = 0;
					for (Object o : activePeer.keySet()) {
						if (activePeer.get(o).length != 0) {
							// System.out.println(activePeer.get(o).length);
							count++;
						}
					}
					String[] listAllRequestParsed = listAllRequest.split("\r\n");
					String[] listAllRequestFirstLine = listAllRequestParsed[0].split(" ");
					String[] listAllRequestHostName = listAllRequestParsed[1].split(":");
					String[] listAllRequestPort = listAllRequestParsed[2].split(":");
					if (!listAllRequestFirstLine[2].trim().equals("P2P-CI/1.0")) {
						statusCode = "505 P2P-CI Version Not Supported";
						responseMessage1 = "P2P-CI/1.0 " + statusCode + "\r\n";
					} else if (count == 0) {
						statusCode = "404 Not Found";
						responseMessage1 = "P2P-CI/1.0 " + statusCode + "\r\n";
					} else {
						statusCode = "200 OK";
						responseMessage1 = "P2P-CI/1.0 " + statusCode + "\r\n";
						for (Object o : activePeer.keySet()) {
							for (int i = 0; i < activePeer.get(o).length; i++) {
								responseMessage1 += activePeer.get(o)[i][0].trim() + " " + activePeer.get(o)[i][3].trim()
										+ " " + activePeer.get(o)[i][1] + " " + activePeer.get(o)[i][2] + "\r\n";
							}
						}
					}
					outputStream.writeUTF(responseMessage1); // out: list all peers
				} else if (option == 2) { // RFC lookup
					String lookupRequest = inputStream.readUTF();
					String responseMessage2;
					String statusCode2 = "400 Bad Request";
					String [] lookupRequestParsed = lookupRequest.split("\r\n");
					String [] lookupRequestFirstLine = lookupRequestParsed[0].split(" ");
					String [] lookupRequestHostName = lookupRequestParsed[1].split(":");
					String [] lookupRequestPort = lookupRequestParsed[2].split(":");
					String [] lookupRequestTitle = lookupRequestParsed[3].split(":");
					for(Object o : activePeer.keySet()) {
						for(int i = 0; i < activePeer.get(o).length; i++) {
							
						}
					}
					if (!lookupRequestFirstLine[2].trim().equals("P2P-CI/1.0")) {
						statusCode2 = "505 P2P-CI Version Not Supported";
						responseMessage2 = "P2P-CI/1.0 " + statusCode2 + "\r\n";
					}
					
					
				} else if (option == 3) { // download RFC from peer
					
				} else if (option == 0) {
					System.out.println("Client closed");
					connected = false;
				} else {
					;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
