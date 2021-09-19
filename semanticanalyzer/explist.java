package semanticanalyzer;

public class explist {
	private String listnum = "";
	private String exprtype = "";
	private String expectedtype = "";
	private boolean canbereal = false;
	public boolean isCanbereal() {
		return canbereal;
	}
	public void setCanbereal(boolean canbereal) {
		this.canbereal = canbereal;
	}
	public String getExpectedtype() {
		return expectedtype;
	}
	public void setExpectedtype(String expectedtype) {
		this.expectedtype = expectedtype;
	}
	public String getExprtype() {
		return exprtype;
	}
	public void setExprtype(String exprtype) {
		this.exprtype = exprtype;
	}
	private boolean isbool = false;
	public boolean isIsbool() {
		return isbool;
	}
	public void setIsbool(boolean isbool) {
		this.isbool = isbool;
	}
	public String getListnum() {
		return listnum;
	}
	public void setListnum(String listnum) {
		this.listnum = listnum;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	private String expr = "";
	public explist(String listn, String exprn) {
		this.expectedtype = listn;
		this.expr = exprn;
	}
}
