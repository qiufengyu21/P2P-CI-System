import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		File f = new File("D:\\Projects\\P2P-CI-System\\RFCs");
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
		String a = f.getAbsolutePath() + "\\"+ matchedFilePath;
		System.out.println(a);
	}
}
