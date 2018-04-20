import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerHandler implements Runnable {
	private Socket socket;
	private ConcurrentHashMap<Integer, int[]> activePeer;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	public ServerHandler(Socket socket, ConcurrentHashMap<Integer, int[]> activePeer) {
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
			System.out.println(inputStream.readUTF());
			outputStream.writeUTF("From server: Connected");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
