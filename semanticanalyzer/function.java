package semanticanalyzer;
import java.util.*;
public class function {
//this class is gonna be the supposed storing of functions that includes
	//the declarations inside
	int paramnum = 0;
	public int getParamnum() {
		return paramnum;
	}
	public void setParamnum(int paramnum) {
		this.paramnum = paramnum;
	}
	private ArrayList<variableLocal> varLocal;
	public ArrayList<variableLocal> getVarLocal() {
		return varLocal;
	}
	public void setVarLocal(ArrayList<variableLocal> varLocal) {
		this.varLocal = varLocal;
	}
	private String funcname = "";
	public String getFuncname() {
		return funcname;
	}
	public void setFuncname(String funcname) {
		this.funcname = funcname;
	}
	public String getFunctype() {
		return functype;
	}
	public void setFunctype(String functype) {
		this.functype = functype;
	}
	public boolean isDoesret() {
		return doesret;
	}
	public void setDoesret(boolean doesret) {
		this.doesret = doesret;
	}
	private String functype = "";
	private boolean doesret = false;
	private ArrayList<String> bodydec;
	public ArrayList<String> getBodydec() {
		return bodydec;
	}
	public void setBodydec(ArrayList<String> bodydec) {
		this.bodydec = bodydec;
	}
}
