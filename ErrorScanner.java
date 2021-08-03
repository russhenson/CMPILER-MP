import java.io.File;
import java.util.*;
public class ErrorScanner {
	private int counter = 1;
	private String point = "reserved";
	private ArrayList<String> errorlist;
	private boolean isValid = true;
	public ErrorScanner()
	{
		errorlist = new ArrayList<>();
	}
	ArrayList<String> listval() {
		return errorlist;
	}
	void error_checker(String lex, String filecatch, int counter) {
		String output;
		ArrayList<String> errlist = new ArrayList<>();
		//from: https://stackoverflow.com/questions/12885821/checking-if-a-character-is-a-special-character-in-java
		
		//System.out.println("Game");
	
		String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
		String samp = lex.trim();
		int symblen = specialChars.length();
		int samplen = samp.length();
		boolean founded = false;
		try {
    		File myobj = new File(filecatch);
    		Scanner myreader = new Scanner(myobj);
    		while(myreader.hasNextLine()) {
    			String texter = myreader.nextLine().trim();
    			errlist.add(texter);
    		}
    	}
    	catch (Exception e) {
    		
    	}
		if (samplen == 1) {
			for (int i = 0; i < symblen; i++) {
				String symblet = specialChars.substring(i, i + 1);
				if (symblet.equals(samp)) {
					founded = true;
					break;
				}
			}
			if (founded) {
				String msg = "" + counter + " " + lex + " " + " " + errlist.get(0) + "\n";
				errorlist.add(msg);
				
				//System.out.println(samp + " is an invalid symbol");
			}
			
		}
		else {
			
			String msg = "" + counter + " " + lex + " " + " " + errlist.get(1) + "\n";
			errorlist.add(msg);
			
			
			//System.out.println(samp + " is an invalid identifier");
		}
		
	}
}
