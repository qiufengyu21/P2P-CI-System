import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Test {
	public static void main(String[] args) throws IOException {
		File f = new File("/Users/yuqiufeng/Dev/P2P-CI-System/RFCs");
		File fileArray[] = f.listFiles();
		String[] RFCs = new String[fileArray.length];
		for (int i = 0; i < RFCs.length; i++) {
			RFCs[i] = fileArray[i].getName();
		}
		String matchedFilePath = null;
		for (int j = 0; j < RFCs.length; j++) {
			String[] fileRFCNumber = RFCs[j].split("_");
			if (fileRFCNumber[0].trim().equals("RFC1")) {
				matchedFilePath = RFCs[j];
				System.out.println(matchedFilePath);
			}
		}

		System.out.println(f.getAbsolutePath());
		String a = f.getAbsolutePath() + "/" + matchedFilePath;
		System.out.println(a);
		File FILE = new File(a);
		Scanner scann = new Scanner(FILE);
		
		
		
		File fos = new File("/Users/yuqiufeng/Dev/P2P-CI-System/RFCs/test.txt");
		PrintStream output = new PrintStream(fos);
		while (scann.hasNextLine()) {
			output.println(scann.nextLine());
		}
		output.close();

		System.out.println(new Date(FILE.lastModified()));
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
