import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		String serverIP = "localhost";
		int serverPort = 7734;
		int uploadPort = 7766;
		Socket peerSocket = null;
		Socket peerToPeerSocket = null;
		DataInputStream inStream = null;
		DataOutputStream outStream = null;
		String hostname = null;
		String clientInfo;

		try {
			peerSocket = new Socket(serverIP, serverPort);
			inStream = new DataInputStream(peerSocket.getInputStream());
			outStream = new DataOutputStream(peerSocket.getOutputStream());
			hostname = java.net.InetAddress.getLocalHost().getHostAddress();

			Scanner console = new Scanner(System.in);
			System.out.print("Enter the file path for RFCs: ");
			String path = console.next();
			// File f = new File("D:\\Projects\\P2P-CI-System\\RFCs");
			File f = new File(path);
			File fileArray[] = f.listFiles();
			clientInfo = hostname;

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
				System.out.println("2: Look up for an RFC by title");
				System.out.println("3: Download an RFC by RFC number");
				System.out.println("0: Close connection");
				input = console.nextInt();
				outStream.writeInt(input);

				switch (input) {
				case 1: // list all available RFCs
					String listAllReq = generateListAllRequest(hostname, uploadPort);
					outStream.writeUTF(listAllReq);
					String serverResponse = inStream.readUTF();
					System.out.println(serverResponse);
					break;
				case 2: // lookup
					System.out.print("Enter option: ");
					input = console.nextInt();
					break;
				case 3: // download (get)

					break;
				case 0: // close connection
					peerSocket.close();
					connected = false;
					console.close();
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
		String listAllReq = "LIST ALL" + " P2P-CI/1.0" + "\r\n" + "Host: " + hostname + "\r\n" + "Port: " + portnumber;

		return listAllReq;
	}
}
