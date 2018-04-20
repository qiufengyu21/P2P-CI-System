import java.io.File;

public class Test {
	public static void main(String[] args) {
		File f = new File("D:\\Projects\\P2P-CI-System\\RFCs");
		int numOfRFC = f.listFiles().length;
		System.out.println(numOfRFC);
	}
}
