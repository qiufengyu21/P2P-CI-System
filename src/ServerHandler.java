import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerHandler implements Runnable {
	private Socket socket;
	private ConcurrentHashMap<String, String[][]> activePeer;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private String clientInfo;
	private int uploadPort;
	private int numOfRFCs;

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
				int option = 0;
				try {
					option = inputStream.readInt(); // in: client option
				} catch (Exception e) {
					System.out.println("Client disconnected unexpectedly...");
				}
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
								responseMessage1 += activePeer.get(o)[i][0].trim() + " "
										+ activePeer.get(o)[i][3].trim() + " " + activePeer.get(o)[i][1] + " "
										+ activePeer.get(o)[i][2] + "\r\n";
							}
						}
					}
					outputStream.writeUTF(responseMessage1); // out: list all peers
				} else if (option == 2) { // RFC lookup
					String lookupRequest = inputStream.readUTF();
					String responseMessage2;
					String statusCode2 = "400 Bad Request";
					String[] lookupRequestParsed = lookupRequest.split("\r\n");
					String[] lookupRequestFirstLine = lookupRequestParsed[0].split(" ");
					String RFCNumber2 = lookupRequestFirstLine[1].trim();
					String result = "";
					for (Object o : activePeer.keySet()) {
						String[][] entry = activePeer.get(o);
						for (int j = 0; j < activePeer.get(o).length; j++) {
							if (entry[j][0].equals(RFCNumber2)) {
								result += entry[j][0] + " " + entry[j][3] + " " + entry[j][1] + " " + entry[j][2]
										+ "\r\n";
							}
						}

					}
					if (!lookupRequestFirstLine[2].trim().equals("P2P-CI/1.0")) {
						statusCode2 = "505 P2P-CI Version Not Supported";
						responseMessage2 = "P2P-CI/1.0 " + statusCode2 + "\r\n";
					} else if (result.equals("")) {
						statusCode2 = "404 Not Found";
						responseMessage2 = "P2P-CI/1.0 " + statusCode2 + "\r\n";
					} else {
						statusCode2 = "200 OK";
						responseMessage2 = "P2P-CI/1.0 " + statusCode2 + "\r\n";
						responseMessage2 += result;

					}
					outputStream.writeUTF(responseMessage2); // out: lookup

				} else if (option == 3) { // download RFC from peer
					numOfRFCs = inputStream.readInt(); // in: Number of RFCs
					mapValue = new String[numOfRFCs][4];
					mapKey = clientInfo + ":" + uploadPort;

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
				} else if (option == 0) {
					activePeer.remove(mapKey);
					System.out.println("Removing " + clientInfo + "registered entry...");
					System.out.println("Client " + clientInfo + " closed");
					connected = false;
				} else {
					;
				}
			}

		} catch (IOException e) {
			System.out.println("Client closed unexpectedly.");
			e.printStackTrace();
		}
	}
}
