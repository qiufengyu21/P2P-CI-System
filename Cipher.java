import java.util.*;
import java.io.*;

/**
 * This is a program that uses a scheme called Cipher to encode/decode the text file,
 * and output the encoded/decoded text file to a given directory.
 * @author Qiufeng Yu
 */
public class Cipher {
	private static String inputFilename;
	private static String outputFilename;
	public static final int DIVIDED_BY_TWO = 2;
	public static final int MULTIPLY_BY_TWO = 2;
	public static final int ADD_ONE = 1;
	
	
	/**
	 * Begins program execution
	 * @param args command line arguments (not used)
	 */
	public static void main (String [] args){
		userInterface();
	}
	
	/**
	 * Prompts user to choose the options either E-ncode, D-ecode or Q-uit,
	 * and outputs the encoded/decoded file to a given directory.
	 */
	public static void userInterface(){
		System.out.println("Hello, welcome to the Cipher encoding and decoding program!");
		System.out.println("This program will encode and decode the given input file and output the encoded or decoded file");
		System.out.println("To use this program, simply just type E for encode or D for decode or Q for quit.");
		System.out.println("After you type E or D, please enter the pathname of the file, then the application will provide the encoded/decoded file in an output file.");
		System.out.println();
		boolean flag = true;
		while (flag) {
			System.out.print("Enter E-ncode, D-ecode, or Q-uit: ");
			Scanner console = new Scanner(System.in);
			String action = console.nextLine().trim();
			//if user input "e", do encode.
			if (action.equalsIgnoreCase("e")) {
				Scanner input = getInputScanner(console);
				PrintStream output = getOutputPrintStream(console);
				processFile(true, input, output);
				System.out.println("");
			}
			//if user input "d", do decode.
			else if (action.equalsIgnoreCase("d")) {
				Scanner input = getInputScanner(console);
				PrintStream output = getOutputPrintStream(console);
				processFile(false, input, output);
				System.out.println("");
			}
			//if user input "q", exit the program.
			else if (action.equalsIgnoreCase("q")) {
				System.out.println("PROGRAM EXITS!");
				flag = false;
			}
			//prompts user invalid action.
			else {
				System.out.println("Invalid Action!\n");
			}
		}
	}
	
	/**
	 * Repeatedly prompts user for the name of an input file
	 * until a Scanner for the file can be created.
	 * @param console Scanner for user input
	 * @return Scanner for input file
	 */
	public static Scanner getInputScanner(Scanner console){
		Scanner input = null;
		while (input == null) {	
			System.out.print("Enter input file: ");
			String filename = console.nextLine().trim();	
			inputFilename = filename;
			try {
				input = new Scanner(new File(filename));
			}
			catch (FileNotFoundException e) {
				System.out.println(filename + " (No such file or directory, please enter it again)");
			}
		}
		return input;
	}
	
	/**
	 * Repeatedly prompts user for the name of an output file
	 * until a Scanner for the file can be created.
	 * If the user enters an output file that already exists,
	 * the user should be asked if it's OK to overwrite the file.
	 * If not, they should be reprompted for a new filename.
	 * @param console Scanner for user input
	 * @return Scanner for output file
	 */
	public static PrintStream getOutputPrintStream(Scanner console){
		PrintStream output = null;
		String filename = null;
		//prompts user to input a path that store the decoding file.
		while (output == null) {
			System.out.print("Enter output file: ");
			filename = console.nextLine().trim();	
			outputFilename = filename;
			
			File inputFile = new File(inputFilename);
			File outputFile = new File(outputFilename);
			try {
				if (inputFile.getCanonicalPath().equals(outputFile.getCanonicalPath())) {
					System.out.println("Same file! Please enter another output file!");
					continue;
				}
			} catch (IOException exception) {
				System.out.println("Require filesystem queries");
				continue;
			}
			
			try {
				File file = new File(filename);		
				//if file is existed, then prompt user whether it is OK
				//to overwrite the file.
				if (file.exists()) {
					System.out.println("File exists!");
					System.out.print("Is it OK to overwrite the file? (Y/N): ");
					String confirm = console.nextLine().trim();
					if (confirm.equalsIgnoreCase("Y")) {
						System.out.println("The file: '" + filename + "' has been overwritten!");
					}
					//if answer is no, put another file.
					else if (confirm.equalsIgnoreCase("N")) {	
						System.out.println("Please enter another output file!");
						continue;
					}
					//invalid action
					else {		
						System.out.println("Invalid Action\n");
						continue;
					}
				}
				//if file is not existed, create it.
				else {
					file.createNewFile();	
				}
			}
			catch (IOException e) {	
				System.out.println("The system could not find the specific path!");
				continue;
			}
			
			try {
				output = new PrintStream(filename);
			}
			catch (FileNotFoundException e) {
				System.out.println(filename + " (No such file or directory)");
			}
		}
		return output;
	}
	
	/**
	 * Process the file to either encodes likes in input and outputs encoded file,
	 * or decodes lines in input and outputs decoded file.
	 * @param encode boolean to determine either encode or decode
	 * @param input Scanner for user input
	 * @param output Scanner for user output
	 */
	public static void processFile (boolean encode, Scanner input, PrintStream output){
		if (input != null && output != null) {		
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String resultLine = null;
				if (encode) {
					resultLine = encodeLine(line);	
				}
				else {
					resultLine = decodeLine(line);	
				}
				output.println(resultLine);	
			}
			File outputFile = new File(outputFilename);
			
			try{
				System.out.println("Success!\nThe output file is: '"
					+ outputFile.getCanonicalPath() + "'");
			
				input.close();
				output.close();
			}
			catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
		else {
			System.out.println("input and output cannot be null!");
		}
	}

	/**
	 * Encode a line of plaintext, 
	 * For example,the message "we are discovered save yourself" will become
	 * "w r icvrdsv orefeaedsoee aeyusl" after encoding.
	 * @param line String need to be encoded.
	 * @return String containing encoded line.
	 */
	public static String encodeLine(String line){
		String encodedLine = "";
		if(line != null) {
			
			int halfOfLine = line.length() / DIVIDED_BY_TWO + line.length() % DIVIDED_BY_TWO;
			for(int index = 0; index < halfOfLine ; index ++) {
				encodedLine += line.charAt(MULTIPLY_BY_TWO * index);
			}
			for(int index = 0; index < line.length() - halfOfLine ; index ++) {
				encodedLine += line.charAt(MULTIPLY_BY_TWO * index + ADD_ONE);
			}
		}
		return encodedLine;
	}
	
	/**
	 * Decode a line of plaintext,
	 * For example, the message "w r icvrdsv orefeaedsoee aeyusl"
	 * will become "We are discovered save yourself" after encoding.
	 * @param line String need to be decoded 
	 * @return String containing decoded line
	 */
	public static String decodeLine(String line){
		String decodedLine = null;
		if (line != null){
			decodedLine = "";
			
			int halfOfLine = line.length() / DIVIDED_BY_TWO + line.length() % DIVIDED_BY_TWO;
			for(int index = 0; index < halfOfLine ; index ++){
				
				decodedLine += line.charAt(index);
				if (index + halfOfLine < line.length()) {
					
					decodedLine += line.charAt(index + halfOfLine);
				}
			}
		}
		return decodedLine;
	}
}
