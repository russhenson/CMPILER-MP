package semanticanalyzer;

public class expressionbools {
	private boolean hasnum = false, hassign = true, hasopenparen = false, hascloseparen = false;
	int numexpr = 0;
	public int getNumexpr() {
		return numexpr;
	}

	public void setNumexpr(int numexpr) {
		this.numexpr = numexpr;
	}

	public boolean isHasnum() {
		return hasnum;
	}

	public void setHasnum(boolean hasnum) {
		this.hasnum = hasnum;
	}

	public boolean isHassign() {
		return hassign;
	}

	public void setHassign(boolean hassign) {
		this.hassign = hassign;
	}

	public boolean isHasopenparen() {
		return hasopenparen;
	}

	public void setHasopenparen(boolean hasopenparen) {
		this.hasopenparen = hasopenparen;
	}

	public boolean isHascloseparen() {
		return hascloseparen;
	}

	public void setHascloseparen(boolean hascloseparen) {
		this.hascloseparen = hascloseparen;
	}
	public void reverbool() {
hasnum = false; 
		
		hassign = true; 
		hasopenparen = false;
		hascloseparen = false;
		this.numexpr = 0;
	}
}
