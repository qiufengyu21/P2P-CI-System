import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		String serverIP = args[0];
		int serverPort = 7734;
		int uploadPort = Integer.parseInt(args[1]);
		Socket peerSocket = null;
		Socket peerToPeerSocket = null;
		ServerSocket serverSocket = null;
		DataInputStream inStream = null;
		DataOutputStream outStream = null;
		DataInputStream peerInStream = null;
		DataOutputStream peerOutStream = null;
		String hostname = null;
		String clientInfo;

		try {
			peerSocket = new Socket(serverIP, serverPort);
			inStream = new DataInputStream(peerSocket.getInputStream());
			outStream = new DataOutputStream(peerSocket.getOutputStream());
			hostname = java.net.InetAddress.getLocalHost().getHostAddress();

			Scanner console = new Scanner(System.in);
			System.out.print("Enter the file path for RFCs: ");
			String path = console.nextLine();
			// File f = new File("D:\\Projects\\P2P-CI-System\\RFCs");
			File f = new File(path);
			File fileArray[] = f.listFiles();
			clientInfo = hostname;

			// spawn a new thread for handling incoming peers
			boolean p2pServerConnected = true;
			serverSocket = new ServerSocket(uploadPort);
			Thread t = new Thread(new P2PServer(f, serverSocket, p2pServerConnected));
			t.start();

			outStream.writeUTF("Hello from client");// out: hello to server
			outStream.writeUTF(clientInfo); // out: write host name to server
			outStream.writeInt(uploadPort);// out: write upload Port number
			System.out.println(inStream.readUTF()); // in: hello from server

			int numOfRFCs = fileArray.length;
			outStream.writeInt(numOfRFCs); // out: write number of RFCs to server

			for (int i = 0; i < numOfRFCs; i++) {
				String addRequest = generateAddRequest(fileArray[i].getName(), hostname, uploadPort);
				outStream.writeUTF(addRequest); // out: Add request to the server
			}

			System.out.print("Enter option: ");
			int input;
			boolean connected = true;
			while (connected) {
				System.out.println("Please enter option:");
				System.out.println("1: List all available RFCs");
				System.out.println("2: Look up for an RFC its number");
				System.out.println("3: Download an RFC by RFC number");
				System.out.println("0: Close connection");
				input = Integer.parseInt(console.nextLine());
				outStream.writeInt(input);

				switch (input) {
				case 1: // list all available RFCs
					String listAllReq = generateListAllRequest(hostname, uploadPort);
					outStream.writeUTF(listAllReq);
					String serverResponse1 = inStream.readUTF();
					System.out.println(serverResponse1);
					break;
				case 2: // lookup
					System.out.print("Enter the RFC number you want to lookup: ");
					int lookupRFCNumber = Integer.parseInt(console.nextLine());

					System.out.print("Enter the RFC title you want to lookup: ");
					String lookupRFCTitle = console.nextLine();
					String lookupReq = generateLookupRequest(lookupRFCNumber, lookupRFCTitle, hostname, uploadPort);
					outStream.writeUTF(lookupReq);
					String serverResponse2 = inStream.readUTF();
					System.out.println(serverResponse2);
					break;
				case 3: // download (get)
					System.out.print("Enter the RFC number you want to download: ");
					int downloadRFCNumber = Integer.parseInt(console.nextLine());
					System.out.print("Enter the hostname you want to download the RFC from: ");
					String downloadHostName = console.nextLine();
					System.out.print("Enter the upload port number of the host: ");
					int downloadPortNumber = Integer.parseInt(console.nextLine());
					String getReq = generateGetRequest(downloadRFCNumber, hostname);

					// connect to the uploading peer
					peerToPeerSocket = new Socket(downloadHostName, downloadPortNumber);
					peerInStream = new DataInputStream(peerToPeerSocket.getInputStream());
					peerOutStream = new DataOutputStream(peerToPeerSocket.getOutputStream());
					peerOutStream.writeUTF(getReq);
					String response = peerInStream.readUTF();
					System.out.println(response);

					if (Integer.parseInt(response.split("\r\n")[0].split(" ")[1].trim()) == 404) {
						break;
					}
					// receive the file
					int numberOfLines = peerInStream.readInt();
					String fileName = peerInStream.readUTF();
					String downloadedRFCPath = path + "/" + fileName;

					File fin = new File(downloadedRFCPath);
					PrintStream output = new PrintStream(fin);
					for (int i = 0; i < numberOfLines; i++) {
						String line = peerInStream.readUTF();
						output.println(line);
					}
					output.close();

					// register new RFC to server
					System.out.println("Download successful. Updating new RFC to server...");
					f = new File(path);
					fileArray = f.listFiles();
					clientInfo = hostname;

					numOfRFCs = fileArray.length;
					outStream.writeInt(numOfRFCs); // out: write number of RFCs to server

					for (int i = 0; i < numOfRFCs; i++) {
						String addRequest = generateAddRequest(fileArray[i].getName(), hostname, uploadPort);
						outStream.writeUTF(addRequest); // out: Add request to the server
					}

					System.out.println("Update successful");
					break;
				case 0: // close connection
					peerSocket.close();
					connected = false;
					console.close();
					p2pServerConnected = false;
					break;

				default:
					break;
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String generateAddRequest(String fileName, String hostname, int portnumber) {
		String[] addRequest = fileName.split("_");
		return "ADD" + " " + addRequest[0] + " " + "P2P-CI/1.0" + "\r\n" + "Host:" + " " + hostname + "\r\n" + "Port:"
				+ " " + portnumber + "\r\n" + "Title:" + " " + addRequest[1].replaceAll(".txt", "") + "\r\n";
	}

	public static String generateListAllRequest(String hostname, int portnumber) {
		String listAllReq = "LIST ALL" + " P2P-CI/1.0" + "\r\n" + "Host: " + hostname + "\r\n" + "Port: " + portnumber
				+ "\r\n";

		return listAllReq;
	}

	public static String generateLookupRequest(int RFCNumber, String RFCTitle, String hostname, int portnumber) {
		return "LOOKUP" + " " + "RFC" + RFCNumber + " " + "P2P-CI/1.0" + "\r\n" + "Host: " + hostname + "\r\n"
				+ "Port: " + portnumber + "\r\n" + "Title: " + RFCTitle + "\r\n";
	}

	public static String generateGetRequest(int RFCNumber, String hostname) {
		return "GET" + " " + "RFC" + RFCNumber + " " + "P2P-CI/1.0" + "\r\n" + "Host: " + hostname + "\r\n" + "OS: "
				+ System.getProperty("os.name") + "\r\n";
	}
}
