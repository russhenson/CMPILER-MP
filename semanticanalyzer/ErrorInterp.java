package semanticanalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
//this is where I will place the error in the console
public class ErrorInterp {
	ArrayList<String> errparse;
	ArrayList<String> errorlist;
	public ErrorInterp() {
		errparse = new ArrayList<String>();
		errorlist = new ArrayList<String>();
	}
	ArrayList<String> get_errparselist() {
		return this.errparse;
	}
	void error_checker(int errnum, String filecatch, int counter, String lookahead) {
		String output;
		ArrayList<String> errlist = new ArrayList<>();
		try {
    		File myobj = new File(filecatch);
    		Scanner myreader = new Scanner(myobj);
    		while(myreader.hasNextLine()) {
    			String texter = myreader.nextLine().trim();
    			errlist.add(texter);
    		}
    	}
    	catch (Exception e) {
    		System.out.println("WACK");
    	}
		output = counter + " " + lookahead + " " + errlist.get(errnum - 1) + "\n";
		errparse.add(output);
		
	}
}
