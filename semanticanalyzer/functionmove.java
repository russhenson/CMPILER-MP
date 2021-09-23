package semanticanalyzer;
import java.math.BigDecimal;
import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.ExpressionException;
import java.util.*;
public class functionmove {
	private String functype = "";
	public String getFunctype() {
		return functype;
	}
	public void setFunctype(String functype) {
		this.functype = functype;
	}
	private String funcinst = "";
	public String getFuncinst() {
		return funcinst;
	}
	private boolean notthrown = true;
	public boolean isNotthrown() {
		return notthrown;
	}
	public void setNotthrown(boolean notthrown) {
		this.notthrown = notthrown;
	}
	public int getNewcounter() {
		return newcounter;
	}
	public void setNewcounter(int newcounter) {
		this.newcounter = newcounter;
	}
	private int newcounter = 0;
	public void setFuncinst(String funcinst) {
		this.funcinst = funcinst;
	}
	public ArrayList<variableLocal> getVl() {
		return vl;
	}
	public void setVl(ArrayList<variableLocal> vl) {
		this.vl = vl;
	}
	private ArrayList<variableLocal> vl;
	private ArrayList<String> tokenList, typeList; 
	public ArrayList<String> getTokenList() {
		return tokenList;
	}
	public void setTokenList(ArrayList<String> tokenList) {
		this.tokenList = tokenList;
	}
	public ArrayList<String> getTypeList() {
		return typeList;
	}
	public void setTypeList(ArrayList<String> typeList) {
		this.typeList = typeList;
	}
	public functionmove() {
		
	}
	public void insertparam(ArrayList<String> token, ArrayList<String> type, int param, int newcount, boolean notthrown) {
		String exp = "";
		String typen ="";
		int ind = 0;
		try
		{
			for (int i = 0; i < param; i++) {
				ind = i;
				typen = type.get(i);
				exp = token.get(i);
				
		    	
		    	if (typen.equals("string")) {
		    		this.vl.get(i).setHasval(true);
		    		this.vl.get(i).setStringval(exp);
		    	}
		    	else if (typen.equals("char")) {
		    		this.vl.get(i).setHasval(true);
		    		this.vl.get(i).setCharval(exp);
		    	}
		    	else {
		    		BigDecimal result = null;
			    	Expression expression = new Expression(exp);
			    	result = expression.setPrecision(33).eval();
			    	if (typen.equals("integer")) {
			    		System.out.println(exp + " jun");
			    		int val = result.intValue();
			    		this.vl.get(i).setIntval(val);
			    		this.vl.get(i).setHasval(true);
			    	}
			    	else if (typen.equals("boolean")) {
			    		int val = result.intValue();
			    		boolean hun = false;
			    		if (val == 1) {
			    			hun = true;
			    		}
			    		this.vl.get(i).setBoolval(hun);
			    		this.vl.get(i).setHasval(true);
			    	}
			    	else if (typen.equals("real")) {
			    		float val = result.floatValue();
			    		this.vl.get(i).setFloatval(val);
			    		this.vl.get(i).setHasval(true);
			    	}
		    	}
			}
			
	}catch (ExpressionException f) {
		newcount++;
		this.notthrown = false;
		this.newcounter = newcount;
		System.out.println("Invalid boolean expression FUNCTION MOVE " + exp + " type " + typen + " at index " + ind);
	}
	}
}
