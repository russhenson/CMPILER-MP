import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import com.udojava.evalex.AbstractLazyFunction;
import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.ExpressionException;
import com.udojava.evalex.Expression.LazyNumber;

public class dummysample {
	public dummysample() {
		
	}
	private void message() {
		System.out.println("Huzzah");
	}
	    public static void main(String[] args) {
	    	Scanner sc = new Scanner(System.in);
	    	String inp = sc.next();
	    	inp = inp + "\n";
	    	System.out.print(inp + " hun");
	    	int value = 1 - -2;
	    	System.out.println(value);
	    	BigDecimal result = null;
	    	try {
	    		Expression expression = new Expression(" not ( true ) ");
		    	result = expression.eval();
		    	System.out.println(result);
	    	}
	    	catch (ExpressionException f) {
	    		System.out.println("ZSHANG");
	    	}
	    	/*
	    	float val2 = 2.032f;
	    	if (value == Math.round(value)) {
	    		  System.out.println("Integer");
	    		} else {
	    		  System.out.println("Not an integer");
	    		}
	    	dummysample ds = new dummysample();
	    	System.out.print("Hello " + val2);
	    	System.out.print("Smile\n");
	    	System.out.println("Rec " + val2);
	    	System.out.println("Smile");
	    	String a = "abc", b = "bcd";
	    	boolean dan  = true;
	    	BigDecimal result = null;
	    	try {
	    		Expression expression = new Expression(" not ( true ) ");
		    	result = expression.eval();
		    	System.out.println(result);
	    	}
	    	catch (ExpressionException f) {
	    		System.out.println("ZSHANG");
	    	}
	    	*/
	    	/*if (result == Math.round(result)) {
	    		
	    	}*/
	    	//float val3 = result.floatValue();
	    	//System.out.println(val3);
	    	/*Expression e = new Expression("STREQ(\"test\", \"test2\")");
	    	e.addLazyFunction(new AbstractLazyFunction("STREQ", 2) {
	    	    private LazyNumber ZERO = new LazyNumber() {
	    	        public BigDecimal eval() {
	    	            return BigDecimal.ZERO;
	    	        }
	    	        public String getString() {
	    	            return "0";
	    	        }
	    	    };
	    	    private LazyNumber ONE = new LazyNumber() {
	    	        public BigDecimal eval() {
	    	        	ds.message();
	    	            return BigDecimal.ONE;
	    	        }         
	    	        public String getString() {
	    	            return null;
	    	        }
	    	    };  
	    	    @Override
	    	    public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
	    	        if (lazyParams.get(0).getString().equals(lazyParams.get(1).getString())) {
	    	            return ZERO;
	    	        }
	    	        System.out.println("Zap");
	    	        return ONE;
	    	    }
				
	    	});
	    	*/
	    	
	    	//e.eval(); // returns 1
	    	//System.out.println(e.eval());
	    	
	    	
	    	 
	    	 
	    }
}
