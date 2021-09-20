package semanticanalyzer;
import java.util.*;
public class function {
//this class is gonna be the supposed storing of functions that includes
	//the declarations inside
	public function () {
		this.tokenList = new ArrayList<String>();
		this.typeList = new ArrayList<String>();
		varLocal = new ArrayList<variableLocal>();
	}
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
		for (int i = 0; i < varLocal.size(); i++) {
			this.varLocal.add(varLocal.get(i));
		}
		
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
	private ArrayList<String> bodydec;
	public ArrayList<String> getBodydec() {
		return bodydec;
	}
	public void setBodydec(ArrayList<String> bodydec) {
		this.bodydec = bodydec;
	}
	public void insertlocvar(ArrayList<variableLocal> vl) {
		for (int i = 0; i < vl.size(); i++) {
			varLocal.add(vl.get(i));
		}
	}
	public void insertbody(ArrayList<String> token, ArrayList<String> type) {
		for (int i = 0 ; i < token.size(); i++) {
			this.tokenList.add(token.get(i));
			this.typeList.add(type.get(i));
		}
	}
}
