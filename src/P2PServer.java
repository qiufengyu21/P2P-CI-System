import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class P2PServer implements Runnable {
	private File file;
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream inStream;
	private DataOutputStream outStream;

	public P2PServer(File f, ServerSocket serversocket) {
		this.file = f;
		this.serverSocket = serversocket;
	}

	@Override
	public void run() {
		// DateTimeFormatter timestamp =
		// DateTimeFormatter.ofPattern("RFC_1123_DATE_TIME");
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		String OS = System.getProperty("os.name");
		while (true) {
			try {
				socket = serverSocket.accept();
				inStream = new DataInputStream(socket.getInputStream());
				outStream = new DataOutputStream(socket.getOutputStream());
				String getRequest = inStream.readUTF();
				String[] getRequestParsed = getRequest.split("\r\n");
				String[] getRequestFirstLine = getRequestParsed[0].split(" ");

				String RFCNumber = getRequestFirstLine[1].trim();

				File[] fileArray = file.listFiles();
				String[] RFCs = new String[fileArray.length];
				for (int i = 0; i < RFCs.length; i++) {
					RFCs[i] = fileArray[i].getName();
				}
				String matchedFilePath = null;
				for (int j = 0; j < RFCs.length; j++) {
					String[] fileRFCNumber = RFCs[j].split("_");
					if (fileRFCNumber[0].trim().equals(RFCNumber)) {
						matchedFilePath = RFCs[j];
					}
				}

				String absolutePath = file.getAbsolutePath();
				String matchedPath = absolutePath + "/" + matchedFilePath;
				File responseFile = new File(matchedPath);

				if (matchedFilePath != null) {
					String response = "P2P-CI/1.0 200 OK\r\n" + "Date: " + timeStamp + "\r\n" + "OS: " + OS + "\r\n"
							+ "Last-Modified: " + new Date(responseFile.lastModified()) + "\r\n" + "Content-Length: "
							+ responseFile.length() + "\r\n" + "Content-Type: text/text" + "\r\n";
					outStream.writeUTF(response);

					outStream.writeInt(countLines(matchedPath));
					outStream.writeUTF(matchedFilePath);
					Scanner fileScan = new Scanner(responseFile);
					while (fileScan.hasNextLine()) {
						outStream.writeUTF(fileScan.nextLine());
					}
					fileScan.close();
					System.out.println("upload Successful!");
				} else {
					String response = "P2P-CI/1.0 404 Not Found\r\n" + "Date: " + timeStamp + "\r\n" + "OS: " + OS
							+ "\r\n";
					outStream.writeUTF(response);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

}
