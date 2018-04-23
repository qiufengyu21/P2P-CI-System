import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class Test {
	public static void main(String[] args) {
		File f = new File("D:\\Projects\\P2P-CI-System\\RFCs");
		File fileArray[] = f.listFiles();
		String[] RFCs = new String[fileArray.length];
		for (int i = 0; i < RFCs.length; i++) {
			RFCs[i] = fileArray[i].getName();
		}
		
		System.out.println(fileArray[0].getName());
		
		
		
		ConcurrentHashMap<String, String[][]> m = new ConcurrentHashMap<String, String[][]>();
		String [] [] a = new String [3][5];
		System.out.println(a.length);
	}
}
