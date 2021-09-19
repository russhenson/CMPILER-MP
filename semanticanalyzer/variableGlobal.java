package semanticanalyzer;

import java.util.ArrayList;
//this here will be the class setup for the storing of class variables for global variables
public class variableGlobal {
	private String variablename = "";
	private float floatval = 0;
	private int intval = 0;
	private boolean boolval = false;
	private String stringval = "";
	private String charval = "";
	public String getCharval() {
		return charval;
	}
	public void setCharval(String charval) {
		this.charval = charval;
	}
	private String vartype = "";
	private ArrayList<String> boollist, stringlist, charlist;
	private ArrayList<Integer> intlist;
	private ArrayList<Float> floatlist;
	private boolean islist = false;
	private int arraynum = 0;
	private boolean hasval = false;
	public boolean isHasval() {
		return hasval;
	}
	public void setHasval(boolean hasval) {
		this.hasval = hasval;
	}
	public int getArraynum() {
		return arraynum;
	}
	public void setArraynum(int arraynum) {
		this.arraynum = arraynum;
	}
	public String getVariablename() {
		return variablename;
	}
	public void setVariablename(String variablename) {
		this.variablename = variablename;
	}
	public float getFloatval() {
		return floatval;
	}
	public void setFloatval(float floatval) {
		this.floatval = floatval;
	}
	public int getIntval() {
		return intval;
	}
	public void setIntval(int intval) {
		this.intval = intval;
	}
	public boolean isBoolval() {
		return boolval;
	}
	public void setBoolval(boolean boolval) {
		this.boolval = boolval;
	}
	public String getStringval() {
		return stringval;
	}
	public void setStringval(String stringval) {
		this.stringval = stringval;
	}
	public String getVartype() {
		return vartype;
	}
	public void setVartype(String vartype) {
		this.vartype = vartype;
	}
	
	public ArrayList<String> getBoollist() {
		return boollist;
	}
	public void setBoollist(ArrayList<String> boollist) {
		this.boollist = boollist;
	}
	public ArrayList<String> getStringlist() {
		return stringlist;
	}
	public void setStringlist(ArrayList<String> stringlist) {
		this.stringlist = stringlist;
	}
	public ArrayList<String> getCharlist() {
		return charlist;
	}
	public void setCharlist(ArrayList<String> charlist) {
		this.charlist = charlist;
	}
	public ArrayList<Integer> getIntlist() {
		return intlist;
	}
	public void setIntlist(ArrayList<Integer> intlist) {
		this.intlist = intlist;
	}
	public ArrayList<Float> getFloatlist() {
		return floatlist;
	}
	public void setFloatlist(ArrayList<Float> floatlist) {
		this.floatlist = floatlist;
	}
	public variableGlobal(String varname, String vartype, int listnum, boolean isitarray) {
		this.variablename = varname;
		this.vartype = vartype;
		this.arraynum = listnum;
		this.islist = isitarray;
	}
	public boolean isIslist() {
		return islist;
	}
	public void setIslist(boolean islist) {
		this.islist = islist;
	}
	
	
}
