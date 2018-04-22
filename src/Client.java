import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		String serverIP = "localhost";
		int serverPort = 7734;
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
			String[] RFCs = new String[fileArray.length];
			for (int i = 0; i < RFCs.length; i++) {
				RFCs[i] = fileArray[i].getName();
				System.out.println(RFCs[i]);
			}

			clientInfo = hostname;
			for (String i : RFCs) {
				clientInfo += " ";
				clientInfo += i;
			}
			System.out.println(clientInfo);
			outStream.writeUTF("Hello from client");
			outStream.writeUTF(clientInfo);

			/*
			 * Scanner console = new Scanner(System.in); System.out.print("Enter option: ");
			 * int input = console.nextInt(); boolean connected = true; while (connected) {
			 * 
			 * switch (input) { case 1: outStream.writeInt(1);
			 * System.out.print("Enter option: "); input = console.nextInt(); break; case 2:
			 * outStream.writeInt(2); System.out.print("Enter option: "); input =
			 * console.nextInt(); break; case 0: outStream.writeInt(0); peerSocket.close();
			 * connected = false; console.close(); break; default: break; } }
			 */

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
