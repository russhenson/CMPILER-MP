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
	void error_checker(String lex) {
		String output;
		//from: https://stackoverflow.com/questions/12885821/checking-if-a-character-is-a-special-character-in-java
		TokenType toktype = new TokenType(lex);
		String tokname = "" + toktype.getTokenType() + "";
		//System.out.println("Game");
		if (tokname.equals("ERROR")) {
			String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
			String samp = lex.trim();
			int symblen = specialChars.length();
			int samplen = samp.length();
			boolean founded = false;
			if (samplen == 1) {
				for (int i = 0; i < symblen; i++) {
					String symblet = specialChars.substring(i, i + 1);
					if (symblet.equals(samp)) {
						founded = true;
						break;
					}
				}
				if (founded) {
					output = "" + counter + " " + samp + " is an invalid symbol\n";
					errorlist.add(output);
					counter++;
					//System.out.println(samp + " is an invalid symbol");
				}
				else {
					output = "" + counter + " " + samp + " is an unknown symbol\n";
					errorlist.add(output);
					counter++;
					//System.out.println(samp + " is an unknown symbol");
				}
			}
			else {
				output = "" + counter + " " + samp + " is an invalid identifier\n";
				errorlist.add(output);
				counter++;
				//System.out.println(samp + " is an invalid identifier");
			}
		}
	}
}
