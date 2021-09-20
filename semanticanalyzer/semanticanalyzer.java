package semanticanalyzer;

import java.math.BigDecimal;
import java.util.*;

import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.ExpressionException;
//this here is the main semantic analyzer
//so I want to show you first on how I stored the global variables
public class semanticanalyzer {
	private int paramnoexpect = 0;
	private boolean identiffound = false;
	private boolean inloop = false;
	private String varloop = "";
	private int identifindsearch = 0;
	private boolean shak = false, sagemark = false;
	private ArrayList<explist> explister;
	private ArrayList<expressionbools> expb;
	private ArrayList<String> toklist, toktypelist;
	private String exprorder = "exporder ", exprorder_real = "";
	private ArrayList<functionmove> fm;
	private boolean canstarteval = false;
	private String funcinst = "funcno";
	private int exprcall = 0;
	private int funcno2 = 0;
	private int errornum = 0;
	private int funcind = 0;
	private Stack<String> tokenStack;
	private Stack<String> tokenTypeStack;
	private Stack<String> operatshunt;
	private Queue<String> formshunt;
	private ErrorInterp errparser;
	private String tokenLookAhead;
	private String typeLookAhead;
	private ArrayList<String> token_name, type_name;
	private int newcount = 0;
	private int statemode = 0;
	private ArrayList<variableGlobal> varGlobal;
	private ArrayList<variableLocal> varLocal;
	private ArrayList<function> functioncall;
	private ArrayList<String> tokenbackup, typebackup;
	private boolean shouldpopsemi = true;
	private String indic = "";
	private boolean notthrown = true;
	private int instamod = 0;
	private boolean cangostruct = true;
	private boolean hasnum = false, hassign = true, hasopenparen = false, hascloseparen = false;
	private String bodtype = "function";
	private String assigndata = "";
	private String globeexpr = "";
	private boolean isarraytype = false;
	private String searchedtype = "";
	private int searchind = 0;
	private boolean foundinlocal = false;
	private int numexpr = 0;
	
	void revertexpbool() {
		hasnum = false; 
		
		hassign = true; 
		hasopenparen = false;
		hascloseparen = false;
		this.numexpr = 0;
	}
	public semanticanalyzer(ArrayList<String> tokenn, ArrayList<String> typenames, int counter)
	{
		
		System.out.println("SEMANTICSER");
		this.tokenStack = new Stack<>();
		this.tokenTypeStack = new Stack<>();
		this.formshunt = new LinkedList<>();
		this.operatshunt = new Stack<>();
		
		for(int i = tokenn.size()-1; i >= 0; i--){
            tokenStack.push(tokenn.get(i));
            tokenTypeStack.push(typenames.get(i));
        }
      
		this.newcount = counter;
		this.tokenbackup = new ArrayList<String>();
		this.fm = new ArrayList<functionmove>();
		this.typebackup = new ArrayList<String>();
		this.explister = new ArrayList<explist>();
		expb = new ArrayList<expressionbools>();
		this.varGlobal = new ArrayList<variableGlobal>();
		this.varLocal = new ArrayList<variableLocal>();
		functioncall = new ArrayList<function>();
		this.tokenLookAhead = tokenStack.peek();
		this.typeLookAhead = this.tokenTypeStack.peek();
		int numtyp = tokenTypeStack.size();
		int numtok = tokenStack.size();
		System.out.println("CHECKING SEM " + numtyp + " " + numtok);
		startsem();
	}
	void testRuns() {
		String samp = "Hello my name is ";
		
	}
	boolean startsem() {
		boolean isValid = false;
		isValid = program();
		return isValid;
	}

	boolean program() {
		boolean isValid = false, hasfunc = false;
		System.out.println("toks " + tokenLookAhead);
		// Check if the first token is "program"
		if (tokenLookAhead.equals("program")) {

			tokenPopper();
			tokenTypePopper();
			peeker();

			// Checks the program name if it's valid
			if (typeLookAhead.equals("IDENTIFIER")) {

				tokenPopper();
				tokenTypePopper();
				peeker();

				// Check if it ends with semi colon
				if (typeLookAhead.equals("SEMICOLON")) {
					
					tokenPopper();
					tokenTypePopper();
					peeker();
					isValid = true;
					if (tokenLookAhead.equals("var")) {
						// if starts with var
						while (tokenLookAhead.equals("var") && notthrown) {
							isValid = this.variableDeclaration(0, "Global");
						}

					}
					if (notthrown) {
						printvariableglob();
					}
					if (notthrown) {
						if (tokenLookAhead.equals("function")) {
							// if starts with function
							hasfunc = true;
							while (tokenLookAhead.equals("function") && notthrown) {
								isValid = this.functionDeclaration();
								this.clearlocvariable();
							}
							

						}
						System.out.println("notthrown func " + notthrown + tokenLookAhead);
					}
					if (notthrown) {
						printvariablelocal();
					}
					System.out.println("notthrown " + notthrown);
					if (notthrown) {
						
						System.out.println("MAIN DECLARE GOES HERE");
						this.bodtype = "main";
						this.canstarteval = true;
						isValid = this.compoundStatement(0);
						System.out.println("CHECK THE SEMANTIC WEATHER");
						
						
					}

				} else {
					 // Missing a semicolon
					// get the error message from error.txt
					isValid = false;
					notthrown = false;
					newcount++;
					errparser.error_checker(7, "error.txt", newcount, tokenLookAhead);
					// panicmode("IDENTIFIER", 2, 0);

				}

			} else {
				 // Missing or invalid name
				// get the error message from error.txt
				newcount++;
				notthrown = false;
				errparser.error_checker(5, "error.txt", newcount, tokenLookAhead);
				// panic
				// panicmode("IDENTIFIER", 1, 0);

			}
		}
		// get the error message from error.txt
		else {
			notthrown = false;
			newcount++;
			errparser.error_checker(8, "error.txt", newcount, tokenLookAhead);
			// panicmode("program", 0, 1);
		}

		return isValid;

	}

	void burstfunc() {
		this.tokenPopper();
		this.tokenTypePopper();
		peeker();
	}

	boolean variableDeclaration(int mode, String funcname) {
		boolean isGoing = true;
		boolean isValid = false;

		boolean colonPopped = false;
		boolean startAgain = true;
		boolean hasarray = false;
		boolean isGlobal = false;
		String listofvarnames = "", identiftype = "";
		ArrayList<String> varnames = new ArrayList<String>();
		if (mode == 0) {
			isGlobal = true;
		}
		// Check if the first token is "var"

		if (tokenLookAhead.equals("var")) {
			this.burstfunc();
			// required identifier
			if (typeLookAhead.equals("IDENTIFIER")) {
				// keep looking for identifier
				System.out.println("SEMANTIC IDENTIF CHECK " + tokenLookAhead);
				while (typeLookAhead.equals("IDENTIFIER") && notthrown) {
					varnames.add(tokenLookAhead);
					this.burstfunc();
					// optional comma
					if (tokenLookAhead.equals(",")) {
						while (tokenLookAhead.equals(",") && notthrown) {
							this.burstfunc();
							if (typeLookAhead.equals("IDENTIFIER")) {
								varnames.add(tokenLookAhead);
								this.burstfunc();

							} else {
								// expected identifier
								notthrown = false;
							}
						}
					}

					if (notthrown) {
						System.out.println("CHECKZ " + tokenLookAhead);
						// required colon
						if (tokenLookAhead.equals(":")) {
							// required datatype
							this.burstfunc();
							if (tokenLookAhead.equals("array")) {
								hasarray = true;
								this.burstfunc();
								// if open brackets after array
								if (tokenLookAhead.equals("[")) {
									this.burstfunc();
									// if integer or identifier
									if (typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("INTEGER")) {
										this.burstfunc();
										// if up until data type
										if (typeLookAhead.equals("UP_UNTIL")) {
											this.burstfunc();
											// if identifier or integer
											if (typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("INTEGER")) {
												this.burstfunc();
												// expected close bracket
												if (tokenLookAhead.equals("]")) {
													this.burstfunc();
													if (tokenLookAhead.equals("of")) {
														this.burstfunc();
														

													} else {
														// expected of
														notthrown = false;
														newcount++;
														errparser.error_checker(34, "error.txt", newcount,
																tokenLookAhead);
													}
												} else {
													// expected close bracket
													notthrown = false;
													newcount++;
													errparser.error_checker(36, "error.txt", newcount, tokenLookAhead);
												}
											} else {
												// expected integer
												notthrown = false;
												newcount++;
												errparser.error_checker(37, "error.txt", newcount, tokenLookAhead);
											}
										} else {
											// expected up_until
											notthrown = false;
											newcount++;
											errparser.error_checker(38, "error.txt", newcount, tokenLookAhead);

										}
									} else {
										// expected integer
										notthrown = false;
										newcount++;
										errparser.error_checker(37, "error.txt", newcount, tokenLookAhead);
									}
								} else {
									// expected open bracket
									notthrown = false;
									newcount++;
									errparser.error_checker(35, "error.txt", newcount, tokenLookAhead);
								}

							}
							System.out.println("BAZ SEMANTIC " + tokenLookAhead);
							if (typeLookAhead.equals("DATA_TYPE") && !(tokenLookAhead.equals("array"))) {
								System.out.println("NOT ARRAY " + tokenLookAhead +  " namessize " + varnames.size());
								for (int i = 0; i < varnames.size(); i++) {
									if (mode == 0) {
										variableGlobal vg = new variableGlobal(varnames.get(i), tokenLookAhead, 0, hasarray);
										this.varGlobal.add(vg);
									}
									else if (mode ==  1) {
										variableLocal vl = new variableLocal(varnames.get(i), tokenLookAhead, 0, funcname, hasarray);
										this.varLocal.add(vl);
									}
								}
								hasarray = false;
								this.burstfunc();
								
								varnames.clear();
								if (mode == 0) {
									checkDuplicates(varGlobal);
								}
								else {
									this.checkLocalDupes(this.varLocal);
								}
								hasarray = false;
								if (notthrown) {
									// required semicolon
									if (tokenLookAhead.equals(";")) {
										// revert to check if it is an identifier still

										this.burstfunc();

									} else {
										// expected semicolon
										notthrown = false;
										newcount++;
										errparser.error_checker(7, "error.txt", newcount, tokenLookAhead);
									}
								}
								
							} else {
								// expected data type
								if (hasarray && tokenLookAhead.equals("array")) {
									hasarray = false;
									// repeated array
									notthrown = false;
									notthrown = false;
									newcount++;
									errparser.error_checker(39, "error.txt", newcount, tokenLookAhead);
								} else {
									notthrown = false;
									notthrown = false;
									newcount++;
									errparser.error_checker(12, "error.txt", newcount, tokenLookAhead);
								}

							}
						} else {
							// expected colon
							notthrown = false;
							newcount++;
							errparser.error_checker(9, "error.txt", newcount, tokenLookAhead);
						}
					}

				}

			} else {
				// expected identifier
				notthrown = false;
				newcount++;
				errparser.error_checker(5, "error.txt", newcount, tokenLookAhead);
			}
		} else {
			// expected var

			notthrown = false;
			newcount++;
			errparser.error_checker(13, "error.txt", newcount, tokenLookAhead);

		}
		

		return isValid;
	}

	boolean forStatement() {
		ArrayList<String> tokenlook, typeLook;
		tokenlook = new ArrayList<String>();
		typeLook = new ArrayList<String>();
		boolean isValid = false;
		String exprdata = "";
		String startassign = "", toktype = "";
		boolean islocal;
		int num1 = 0, num2 = 0, index = 0;
		// Check if the first token is "for"
		if (canstarteval){
		System.out.println("SEM FOR STATEMENT");
		if (tokenLookAhead.equals("for")) {
			// if assignment
			this.burstfunc();
			
			if (typeLookAhead.equals("IDENTIFIER")) {
				startassign = tokenLookAhead;
				this.searchvariable(startassign);
				
				// start
				 
				toktype = this.searchedtype;
				islocal = this.foundinlocal;
				index = this.searchind;
				this.burstfunc();
				isValid = this.assignment(1);
				if (notthrown) {
					if (!(toktype.equals("integer"))) {
						notthrown = false;
						newcount++;
						System.out.println("Should not be string for loop");
					}
					else {
						this.shunting_yard(startassign, toktype, islocal, index);
					}
					
    				this.removeexprarray();
    				
				}
				if (notthrown) {

				
					if (tokenLookAhead.equals("to")) {
						this.burstfunc();
						if (typeLookAhead.equals("INTEGER")) {
							num2 = Integer.parseInt(tokenLookAhead);
						}
						else if (typeLookAhead.equals("IDENTIFIER")) {
							this.search_identif(tokenLookAhead);
							
							islocal = this.identiffound;
							index = this.searchind;
							if (islocal) {
								
							}
							else {
								toktype = this.varGlobal.get(index).getVartype();
								if (toktype.equals("integer")) {
									boolean hasval = this.varGlobal.get(index).isHasval();
									if (hasval) {
										num2 = this.varGlobal.get(index).getIntval();
									}
									else {
										System.out.println("Integer contains no values ");
										notthrown = false;
										newcount++;
									}
								}
								else {
									newcount++;
									notthrown = false;
									System.out.println("To value should contain integer ");
								}
							}
							
						}
						boolean flocal = false;
						int num = 0;
						System.out.println("JASS " + notthrown);
						if (notthrown) {
							this.inloop = true;
							this.varloop = startassign;
							this.burstfunc();
							System.out.println("DOING FOR STATEMENT NOW " + tokenLookAhead);
							if (tokenLookAhead.equals("do")) {
								System.out.println("JOAN");
								this.burstfunc();
								boolean cond = false;
								this.canstarteval = false;
								System.out.println("JOASS " + tokenLookAhead);
								this.clearbackupfunc();
								isValid = this.compoundStatement(1);
								System.out.println("JASS2 " + notthrown);
								if (notthrown ){
								for (int i = 0; i < this.tokenbackup.size(); i++) {
									tokenlook.add(tokenbackup.get(i));
									typeLook.add(this.typebackup.get(i));
								}
								System.out.println("LAMAR " + tokenbackup.size() + " sad " + tokenlook.size());
								
								this.canstarteval = true;
								this.clearbackupfunc();
								System.out.println("2HAN " + tokenbackup.size() + " sad " + tokenlook.size());
								this.search_identif(startassign);
								flocal = this.identiffound;
								num = this.identifindsearch;
								System.out.println("HEYTHERE " + tokenLookAhead);
								try {
									
									if (flocal) {
										
									}
									else {
										String datavar = "";
										num1 = this.varGlobal.get(num).getIntval();
									}
									
								}
								catch(Exception e) {
									newcount++;
									notthrown = false;
									System.out.println("For parameters are not integers");
								}
							}
								if (notthrown) {
									System.out.println("Condition " + num1 + " " + num2);
									System.out.println("HEYTHERE " + tokenLookAhead + " size " + tokenlook.size());
									//for loop
									while ((num1 <= num2) && notthrown) {
										this.return_tokens(tokenlook, typeLook);
										this.peeker();
										System.out.println("FOR LOOPERS ONLY " + tokenLookAhead + " i " + num1);
										isValid = this.compoundStatement(1);
										System.out.println("LAST TOKEN is " + tokenLookAhead);
										if (notthrown) {
											if (flocal) {
												
											}
											else {
												num1 = this.varGlobal.get(num).getIntval();
												int newnum = num1 + 1;
												this.varGlobal.get(num).setIntval(newnum);
											}
										}
									}
								}
								System.out.println("LAST IS " + tokenLookAhead + " " + notthrown);
								if (notthrown) {
									num1 = this.varGlobal.get(num).getIntval();
									int newnum = num1 - 1;
									this.varGlobal.get(num).setIntval(newnum);
									tokenStack.push(";");
									this.tokenTypeStack.push("SEMICOLON");
									peeker();
									inloop = false;
									varloop = "";
								}

							} else {
								// expect do
								notthrown = false;
								newcount++;
								errparser.error_checker(41, "error.txt", newcount, tokenLookAhead);
							}
						}
					} else {
						// expected to
						notthrown = false;
						newcount++;
						errparser.error_checker(42, "error.txt", newcount, tokenLookAhead);
					}
				}

				// end
			} else {
				// expected identifier
				notthrown = false;
				newcount++;
				errparser.error_checker(5, "error.txt", newcount, tokenLookAhead);
			}

		} else {
			// expected for
			notthrown = false;
			newcount++;
			errparser.error_checker(40, "error.txt", newcount, tokenLookAhead);
		}
	}
		//do not evaluate
		else {
			 // Check if the first token is "for"
	        System.out.println("FOR STATEMENT");
	        if (tokenLookAhead.equals("for")) {
	        	//if assignment
	        	this.backupfunc();
	        	this.burstfunc();
	        	System.out.println("IS IT ASSIGN " + tokenLookAhead);
	        	if (typeLookAhead.equals("IDENTIFIER")) {
	        		this.backupfunc();
	        		this.burstfunc();
	        		//start
	            	isValid = this.assignment(1);
	            	
	            	if (notthrown) {
	            	
	            		System.out.println("IS IT TO " + tokenLookAhead);
	            		if (tokenLookAhead.equals("to")) {
	            			this.backupfunc();
	            			this.burstfunc();
	            			isValid = this.expression(0, exprdata);
	            			if (notthrown) {
	            				
	            				if (tokenLookAhead.equals("do")) {
	            					this.backupfunc();
	            					this.burstfunc();
	            					
	            					isValid = this.compoundStatement(1);
	            					if (notthrown) {
	                            		tokenStack.push(";");
	                            		this.tokenTypeStack.push("SEMICOLON");
	                            		peeker();
	                            		System.out.println("GASSED UP " + tokenLookAhead);
	                            	}
	            					
	            					
	            					
	                            	
	            					
	            				}
	            				else {
	            					//expect do
	            					notthrown = false;
	                            	newcount++;
	                    			errparser.error_checker(41, "error.txt" , newcount, tokenLookAhead);
	            				}
	            			}
	            		}
	            		else {
	            			//expected to
	            			notthrown = false;
	                    	newcount++;
	            			errparser.error_checker(42, "error.txt" , newcount, tokenLookAhead);
	            		}
	            	}
	            	
	            	//end
	        	}
	        	else {
	        		//expected identifier
	        		notthrown = false;
	            	newcount++;
	    			errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
	        	}
	        	
	        	
	        }
	        else {
	        	//expected for
	        	notthrown = false;
	        	newcount++;
				errparser.error_checker(40, "error.txt" , newcount, tokenLookAhead);
	        }
	       
		}

		return isValid;
	}

	boolean ifStatement() {
		boolean isValid = false;
		String exprdata ="";
		boolean cancont = true;
		boolean isequiv = false;
		boolean cangonext = true;
		int numind = this.explister.size() - 1;
		String exp = "";
		ArrayList<String> tokenlook, typeLook;
		tokenlook = new ArrayList<String>();
		typeLook = new ArrayList<String>();
        //GO BACK TO HERE SOMETIME VERY IMPORTANT
		if (this.canstarteval) {
        this.burstfunc();
        
        if (tokenLookAhead.equals("(")) {
        	this.burstfunc();
        	this.searchedtype = "boolean";
        	this.new_express_assign();
        	isValid = this.expression(0, exprdata);
        	this.revertexpbool();
            if (notthrown) {
            	//if )
            	if (tokenLookAhead.equals(")")) {
            		this.burstfunc();
            		numind = this.explister.size() - 1;
            		exp = this.explister.get(numind).getExpr();
            		isequiv = this.shunt_yard3(exp);
            		this.removeexprarray();
            		if (notthrown)
            		{	if (tokenLookAhead.equals("then")) {
            			
                    	
                    	this.burstfunc();
                    	System.out.println("Go here " + tokenLookAhead);
                    	
                    	int num = 1;
                    	this.clearbackupfunc();
                    	this.canstarteval = false;
            			
                    	isValid = this.compoundStatement(2);
                    	if (notthrown) {
                    	
                    	for (int i = 0; i < this.tokenbackup.size(); i++) {
							tokenlook.add(tokenbackup.get(i));
							typeLook.add(this.typebackup.get(i));
						}
                    	
                    	this.clearbackupfunc();
                    	this.canstarteval = true;
                    	System.out.println("IS FIRST IF TRUE " + isequiv + " gun ");
                    	if (isequiv) {
                    		System.out.println("FIRST HERE ");
                    		cangonext = false;
                    		this.return_tokens(tokenlook, typeLook);
                    		this.peeker();
                    		isValid = this.compoundStatement(2);
                    	}
                    	tokenlook.clear();
                    	typeLook.clear();
                    	//System.out.println(tokenLookAhead + " jitters " + this.errparser.get_errparselist().size());
                    	if (notthrown) {
                    		if (tokenLookAhead.equals("else")) {
                        		while (tokenLookAhead.equals("else") && notthrown && cancont) {
                        			this.burstfunc();
                        			if (tokenLookAhead.equals("if")) {
                        				this.burstfunc();
                        				if (tokenLookAhead.equals("(")) {
                        					this.searchedtype = "boolean";
                        		        	this.new_express_assign();
                        		        	this.burstfunc();
                        		        	isValid = this.expression(0, exprdata);
                        		        	this.revertexpbool();
                        		        	
                        		        	if (notthrown) {
                        		        		System.out.println("SHAPE " + tokenLookAhead);
                        		        		if (tokenLookAhead.equals(")")) {
                        		        			numind = this.explister.size() - 1;
                        		        			exp = this.explister.get(numind).getExpr();
                        		            		isequiv = this.shunt_yard3(exp);
                        		            		System.out.println("exp " + exp + " isequiv " + isequiv + " can " + cangonext);
                        		            		this.removeexprarray();
                        		        			this.burstfunc();
                        		        			if (notthrown) {
                        		        				if (tokenLookAhead.equals("then")) {
                            		        				this.canstarteval = false;
                            		            			
                            		                    	
                            		                    	this.burstfunc();
                            		                    	
                            		        			}
                            		        			else {
                            		        				//expected then
                            		        				//expected )
                            		                		notthrown = false;
                            		                    	newcount++;
                            		            			errparser.error_checker(20, "error.txt" , newcount, tokenLookAhead);
                            		        			}
                        		        			}
                        		        			
                        		        		}
                        		        		else {
                        		        			//expected close parenthesis
                        		        			//expected )
                        		            		notthrown = false;
                        		                	newcount++;
                        		        			errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
                        		        		}
                        		        	}
                        				}
                        				else {
                        					//expected openparent
                        					//expected )
                                    		notthrown = false;
                                        	newcount++;
                                			errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
                        				}
                        				
                        			}
                        			else {
                        				System.out.println("OUT OF ELSE BOUNDS " + tokenLookAhead + " can " + cangonext);
                        				if (cangonext) {
                        					isequiv = true;
                        				}
                        				
                        			}
                        			this.canstarteval = false;
                        			this.clearbackupfunc();
                        			isValid = this.compoundStatement(2);
                            		if (notthrown) {
                            			for (int i = 0; i < this.tokenbackup.size(); i++) {
                							tokenlook.add(tokenbackup.get(i));
                							typeLook.add(this.typebackup.get(i));
                						}
                                    	this.clearbackupfunc();
                                    	if (!(cangonext)) {
                                			isequiv = false;
                                		}
                                    	this.canstarteval = true;
                                		if (isequiv) {
                                			System.out.println("SECOND ONWARDS HERE ");
                                			this.return_tokens(tokenlook, typeLook);
                                			this.peeker();
                                			cangonext = false;
                                			isValid = this.compoundStatement(2);
                                		}
                                		tokenlook.clear();
                                    	typeLook.clear();
                            		}
                            		System.out.println("NOW CHECKING HERE " + tokenLookAhead);
                            		
                            		//System.out.println("LAST " + tokenLookAhead + " GOING  UP " + notthrown + " " + this.errparser.get_errparselist().size());
                        		}
                        		
                        		
                        	}
                    	}
                    	
                    	/*if (notthrown) {
                    		tokenStack.push(";");
                    		this.tokenTypeStack.push("SEMICOLON");
                    		peeker();
                    	}*/
                    	
                    	
                    	
                    }
                    else {
                    	//expected then
                   
            			notthrown = false;
                    	newcount++;
            			errparser.error_checker(20, "error.txt" , newcount, tokenLookAhead);
                    }
            		}
            		//up until here 1
            	}
            	else {
            		//expected )
            		notthrown = false;
                	newcount++;
        			errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
            	}
            }
            }
            
        }
        else {
        	//expected (
        	notthrown = false;
        	newcount++;
			errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
        }
	}
		//do not evaluate
		else {
			this.backupfunc();
			this.burstfunc();
	        if (tokenLookAhead.equals("(")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	isValid = this.expression(0, exprdata);
	            if (notthrown) {
	            	//if )
	            	if (tokenLookAhead.equals(")")) {
	            		this.backupfunc();
	            		this.burstfunc();
	            		if (tokenLookAhead.equals("then")) {
	            			
	            			
	            			this.backupfunc();
	                    	this.burstfunc();
	                    	System.out.println("Go here " + tokenLookAhead);
	                    	
	                    	int num = 1;
	                    	isValid = this.compoundStatement(2);
	                    	System.out.println(tokenLookAhead + " jitters " + this.errparser.get_errparselist().size());
	                    	if (notthrown) {
	                    		if (tokenLookAhead.equals("else")) {
	                        		while (tokenLookAhead.equals("else") && notthrown && cancont) {
	                        			this.backupfunc();
	                        			this.burstfunc();
	                        			if (tokenLookAhead.equals("if")) {
	                        				this.backupfunc();
	                        				this.burstfunc();
	                        				if (tokenLookAhead.equals("(")) {
	                        					this.backupfunc();
	                        					this.burstfunc();
	                        		        	isValid = this.expression(0, exprdata);
	                        		        	if (notthrown) {
	                        		        		if (tokenLookAhead.equals(")")) {
	                        		        			this.backupfunc();
	                        		        			this.burstfunc();
	                        		        			if (tokenLookAhead.equals("then")) {
	                        		        				this.backupfunc();
	                        		        				this.burstfunc();
	                        		        			}
	                        		        			else {
	                        		        				//expected then
	                        		        				//expected )
	                        		                		notthrown = false;
	                        		                    	newcount++;
	                        		            			errparser.error_checker(20, "error.txt" , newcount, tokenLookAhead);
	                        		        			}
	                        		        		}
	                        		        		else {
	                        		        			//expected close parenthesis
	                        		        			//expected )
	                        		            		notthrown = false;
	                        		                	newcount++;
	                        		        			errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
	                        		        		}
	                        		        	}
	                        				}
	                        				else {
	                        					//expected openparent
	                        					//expected )
	                                    		notthrown = false;
	                                        	newcount++;
	                                			errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
	                        				}
	                        				
	                        			}
	                        			else {
	                        				System.out.println("OUT OF ELSE BOUNDS " + tokenLookAhead);
	                        				cancont = false;
	                        			}
	                        			
	                            		
	                            		System.out.println("NOW CHECKING HERE " + tokenLookAhead);
	                            		isValid = this.compoundStatement(2);
	                            		System.out.println("LAST " + tokenLookAhead + " GOING  UP " + notthrown + " " + this.errparser.get_errparselist().size());
	                        		}
	                        		
	                        		
	                        	}
	                    	}
	                    	
	                    	/*if (notthrown) {
	                    		tokenStack.push(";");
	                    		this.tokenTypeStack.push("SEMICOLON");
	                    		peeker();
	                    	}*/
	                    	
	                    	
	                    	
	                    }
	                    else {
	                    	//expected then
	                   
	            			notthrown = false;
	                    	newcount++;
	            			errparser.error_checker(20, "error.txt" , newcount, tokenLookAhead);
	                    }
	            	}
	            	else {
	            		//expected )
	            		notthrown = false;
	                	newcount++;
	        			errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
	            	}
	            }
	            
	        }
	        else {
	        	//expected (
	        	notthrown = false;
	        	newcount++;
				errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
	        }
		}
		

		return isValid;
	}

	boolean ifThenElse(String exprdata) {
		boolean isValid = false;

		System.out.println("Semantic ifThenElse function called.");

		if (tokenLookAhead.equals("if")) {

			tokenPopper();
			tokenTypePopper();
			peeker();

			if (typeLookAhead.equals("OPEN_PAREN")) {

				tokenPopper();
				tokenTypePopper();
				peeker();

				if (expression(0, exprdata)) {

					if (typeLookAhead.equals("CLOSE_PAREN")) {

						tokenPopper();
						tokenTypePopper();
						peeker();

						if (tokenLookAhead.equals("then")) {

							tokenPopper();
							tokenTypePopper();
							peeker();

							if (compoundStatement(1)) {

								if (tokenLookAhead.equals("else")) {

									tokenPopper();
									tokenTypePopper();
									peeker();

									if (compoundStatement(1)) {
										isValid = true;
										
									}
								}
								// Error: Missing an "else"
								else {
									// dummy code change with whatever applicable
									newcount++;
									errparser.error_checker(19, "error.txt", newcount, tokenLookAhead);
								}

							}
						}
						// Error: Missing a then
						else {
							// dummy code change with whatever applicable
							newcount++;
							errparser.error_checker(20, "error.txt", newcount, tokenLookAhead);
							// this.panicmode("PERIOD", 10, 0); //dummy code
						}
					}
					// Error: Missing a )
					else {
						// dummy code change with whatever applicable
					
						newcount++;
						errparser.error_checker(16, "error.txt", newcount, tokenLookAhead);
						// this.panicmode("PERIOD", 10, 0); //dummy code
					}
				}
			}
			// Error: Missing a (
			else {
				// dummy code change with whatever applicable
				newcount++;
				errparser.error_checker(23, "error.txt", newcount, tokenLookAhead);
				// this.panicmode("PERIOD", 10, 0); //dummy code
			}
		}
		// Error: Missing if
		else {
			// dummy code change with whatever applicable
			newcount++;
			errparser.error_checker(22, "error.txt", newcount, tokenLookAhead);
			// this.panicmode("PERIOD", 10, 0); //dummy code
		}

		return isValid;
	}

	boolean expression(int mode, String exprdata) {
		String beftoken, beftype;
	
		Boolean isValid = false;
		if (canstarteval) {
			isValid = this.simpleExpression(mode, exprdata);
			if (this.tokenLookAhead.equals(">") || this.tokenLookAhead.equals("<>") || this.tokenLookAhead.equals("<")
					|| this.tokenLookAhead.equals("=") || this.tokenLookAhead.equals(">=")
					|| this.tokenLookAhead.equals("<=") || tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
				this.checkexpress(1, tokenLookAhead);
				numexpr++;
				if (notthrown) {
					
					this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
					if (notthrown) {
						this.set_exprcall(tokenLookAhead, exprdata);
						if (notthrown) {
							this.burstfunc();
							isValid = this.factor(mode, exprdata);
						}
					}
				}
				

			}
			
		}
		//do not evaluate
		else {
			 isValid = this.simpleExpression(mode, exprdata);
		        if (this.tokenLookAhead.equals(">") || this.tokenLookAhead.equals("<>") || 
		        		this.tokenLookAhead.equals("<")  || this.tokenLookAhead.equals("=")  || 
		        		this.tokenLookAhead.equals(">=")  || this.tokenLookAhead.equals("<=") || 
		        		tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
		        	this.backupfunc();
		        	this.burstfunc();
		        	isValid = this.simpleExpression(mode, exprdata);
		        	
		        }
		}
		

		return isValid;
	}

	boolean simpleExpression(int mode, String exprdata) {
		boolean isValid = false;
		System.out.println("semantic simpleExpression function called. " + tokenLookAhead);
		if (canstarteval) {
			if (tokenLookAhead.equals("+") || tokenLookAhead.equals("-")) {
				this.checkexpress(1, tokenLookAhead);
				numexpr++;
				if (notthrown) {
					
					this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
					if (notthrown) {
						this.set_exprcall(tokenLookAhead, exprdata);
						if (notthrown) {
							this.burstfunc();
							isValid = this.factor(mode, exprdata);
						}
					}
				}
				
			}
			if (notthrown) {
				isValid = this.term(mode, exprdata);
				while ((tokenLookAhead.equals("+") || tokenLookAhead.equals("-")) && notthrown) {
					
					this.checkexpress(1, tokenLookAhead);
					numexpr++;
					if (notthrown) {
						
						this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
						if (notthrown) {
							this.set_exprcall(tokenLookAhead, exprdata);
							if (notthrown) {
								this.burstfunc();
								isValid = this.factor(mode, exprdata);
							}
						}
					}
					
				}
			}
		}
		//do not evaluate
		else {
			if (tokenLookAhead.equals("+") || tokenLookAhead.equals("-")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        }
	        isValid = this.term(mode, exprdata);
	        while (tokenLookAhead.equals("+") || tokenLookAhead.equals("-")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	isValid = this.term(mode, exprdata);
	        }
		}
		
		
		

		return isValid;
	}

	boolean term(int mode, String exprdata) {
		boolean isValid = false;
		if (canstarteval) {
			System.out.println("semantic term function called. " + tokenLookAhead);
			isValid = this.factor(mode, exprdata);

			while ((tokenLookAhead.equals("*") || tokenLookAhead.equals("/")) && notthrown) {
				
				this.checkexpress(1, tokenLookAhead);
				numexpr++;
				if (notthrown) {
					
					this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
					if (notthrown) {
						this.set_exprcall(tokenLookAhead, exprdata);
						if (notthrown) {
							this.burstfunc();
							isValid = this.factor(mode, exprdata);
						}
					}
				}
				
				
				
			}
		}
		//do not evaluate
		else {
			System.out.println("term function called. " + tokenLookAhead);
	        isValid = this.factor(mode, exprdata);

	        while (tokenLookAhead.equals("*") || tokenLookAhead.equals("/")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	isValid = this.factor(mode, exprdata);
	        }
		}
		
		

		return isValid;
	}

	boolean factor(int mode, String exprdata) {
		boolean isValid = false;
		if (canstarteval) {
		System.out.println("semantic factor function called. " + tokenLookAhead);
		if (tokenLookAhead.equals("not:")) {
			//GET BACK TO THIS
			this.checkexpress(2, tokenLookAhead);
			numexpr++;
			if (notthrown) {
				this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
				if (notthrown) {
					this.set_exprcall(tokenLookAhead, exprdata);
					if (notthrown) {
						this.burstfunc();
						if (typeLookAhead.equals("OPEN_PAREN")) {
							
							this.checkexpress(2, tokenLookAhead);
							numexpr++;
							if (notthrown) {
								this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
								
								if (notthrown) {
									this.set_exprcall(tokenLookAhead, exprdata);
									if (notthrown) {
										
										burstfunc();
										isValid = expression(mode, exprdata);
										if (notthrown) {
											if (typeLookAhead.equals("CLOSE_PAREN")) {
												this.checkexpress(3, tokenLookAhead);
												numexpr++;
												if (notthrown) {
													this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
													if (notthrown) {
														this.set_exprcall(tokenLookAhead, exprdata);
														if (notthrown) {
															
															
															burstfunc();
														}
													}
													
												}
												
												
												//up until here
											} else {
												// expected close paren
												notthrown = false;
											}
										}
									}
								}
							}
							
							
							
						}
					}
				}
			}
			
		}
		if ( typeLookAhead.equals("REAL") || typeLookAhead.equals("INTEGER")
				) {
			//this should be direct values such as 67, true 2.0
			System.out.println("Integer or real " + tokenLookAhead + " type is " + typeLookAhead);
			this.checkexpress(0, tokenLookAhead);
			numexpr++;
			if (notthrown) {
				this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
				if (notthrown) {
					this.set_exprcall(tokenLookAhead, exprdata);
					if (notthrown) {
						
						this.burstfunc();
					}
				}
				
			}
			
			
			
			
		}
		else if (typeLookAhead.equals("STRING") ) {
			System.out.println("HEY STRING " + tokenLookAhead + "zap");
			this.tokenLookAhead = tokenLookAhead.trim();
			this.checkexpress(0, tokenLookAhead);
			numexpr++;
			if (notthrown) {
				this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
				if (notthrown) {
					this.set_exprcall(tokenLookAhead, exprdata);
					if (notthrown) {
						
						this.burstfunc();
					}
				}
				
			}
		}
		else if ( tokenLookAhead.equals("true") || tokenLookAhead.equals("false")) {
			
			this.tokenLookAhead = tokenLookAhead.trim();
			this.checkexpress(0, tokenLookAhead);
			numexpr++;
			if (notthrown) {
				this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
				if (notthrown) {
					this.set_exprcall(tokenLookAhead, exprdata);
					if (notthrown) {
						
						this.burstfunc();
						
					}
				}
				
			}
		}
		else if (typeLookAhead.equals("IDENTIFIER")) {
			String sam = tokenLookAhead, sam2 = typeLookAhead;
			
			this.burstfunc();
			
			// if array
			if (tokenLookAhead.equals("[")) {
				//get back to this cuz expression includes values
				isValid = this.arrayDeclare(exprdata);
				/*if (notthrown) {
					numexpr++;
				}*/
			}
			// if function
			else if (tokenLookAhead.equals("(")) {
				//get back to this cuz expression includes function IMPORTANT ONE
				this.checkexpress(0, sam);
				numexpr++;
				if (notthrown) {
					this.searchFunc(sam);
					if (notthrown) {
						
						int index = this.funcind;
						this.countparam(index);
						this.newfunccall(sam, index);
						this.revertexpbool();
						isValid = this.funcDeclare(exprdata);
						if (notthrown) {
							this.evalfunc(sam, index);
							this.removefunccall();
							this.paramnoexpect = 0;
						}
						
					}
					
				}
				
				/*if (notthrown) {
					numexpr++;
				}*/
			} else {
				this.checkexpress(0, sam);
				numexpr++;
				if (notthrown) {
					
					this.confirm_valuetype(false, sam, sam2);
					if (notthrown) {
						this.set_exprcall(sam, sam2);
						if (notthrown) {
							System.out.println("CHECKERS " + tokenLookAhead);
							
						}
					}
					
				}
				
			}
		} else if (typeLookAhead.equals("OPEN_PAREN")) {
			
			this.checkexpress(2, tokenLookAhead);
			numexpr++;
			if (notthrown) {
				this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
				
				if (notthrown) {
					this.set_exprcall(tokenLookAhead, exprdata);
					if (notthrown) {
						
						burstfunc();
						isValid = expression(mode, exprdata);
						if (notthrown) {
							if (typeLookAhead.equals("CLOSE_PAREN")) {
								this.checkexpress(3, tokenLookAhead);
								numexpr++;
								if (notthrown) {
									this.confirm_valuetype(false, tokenLookAhead, typeLookAhead);
									if (notthrown) {
										this.set_exprcall(tokenLookAhead, exprdata);
										if (notthrown) {
											
											
											burstfunc();
										}
									}
									
								}
								
								
								//up until here
							} else {
								// expected close paren
								notthrown = false;
							}
						}
					}
				}
			}
			
			
			
		}
	}
		//do not evaluate
		else {
			System.out.println("factor function called. " + tokenLookAhead);
	        if (tokenLookAhead.equals("not:")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	if (typeLookAhead.equals("OPEN_PAREN")) {
	        		this.backupfunc();
	        		burstfunc();
	            	isValid = expression(mode, exprdata);
	            	if (notthrown) {
	            		if (typeLookAhead.equals("CLOSE_PAREN")) {
	            			this.backupfunc();
	                		burstfunc();
	                	}
	                	else {
	                		//expected close paren
	                		notthrown = false;
	                    	newcount++;
	                    	errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
	                	}
	            	}
	        	}
	        }
	        if (typeLookAhead.equals("STRING") || typeLookAhead.equals("REAL") || typeLookAhead.equals("INTEGER") || tokenLookAhead.equals("true") || tokenLookAhead.equals("false")) {
	        	System.out.println("BUZZING SOUND " + tokenLookAhead);
	        	this.backupfunc();
	        	this.burstfunc();
	        	System.out.println("WHEEZE SOUND " + tokenLookAhead);
	        }
	        else if (typeLookAhead.equals("IDENTIFIER")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	System.out.println("U TRY THIS " + tokenLookAhead);
	        	//if array
	        	if (tokenLookAhead.equals("[")) {
	        		
	        	
	        		isValid = this.arrayDeclare(exprdata);
	        		/*isValid = this.expression();
	        		if (notthrown) {
	        			if (tokenLookAhead.equals("]")) {
	        				
	        			}
	        			else {
	        				//expected close bracket
	        			}
	        			
	        		}*/
	        	}
	        	//if function
	        	else if (tokenLookAhead.equals("(")) {
	        		System.out.println("GOING FUCTION ASD");
	        		
	        		isValid = this.funcDeclare(exprdata);
	        	}
	        	else {
	        		
	        	}
	        }
	        else if (typeLookAhead.equals("OPEN_PAREN")) {
	        	this.backupfunc();
	        	burstfunc();
	        	isValid = expression(mode, exprdata);
	        	if (notthrown) {
	        		if (typeLookAhead.equals("CLOSE_PAREN")) {
	        			this.backupfunc();
	            		burstfunc();
	            	}
	            	else {
	            		//expected close paren
	            		notthrown = false;
	            	}
	        	}
	        	
	        }
		}

		return isValid;
	}

	boolean relationalOperator() {
		boolean isValid = false;

		if (typeLookAhead.equals("NOT_EQUAL") || typeLookAhead.equals("LESS_THAN") || typeLookAhead.equals("LESS_EQUAL")
				|| typeLookAhead.equals("GREATER_THAN") || typeLookAhead.equals("GREATER_EQUAL")) {
			token_name.add(tokenLookAhead);
			type_name.add(this.typeLookAhead);
			isValid = true;
		}
		// Error: Invalid operator
		else {
			// dummy code change with whatever applicable
			notthrown = false;
			newcount++;
			errparser.error_checker(26, "error.txt", newcount, tokenLookAhead);
			// this.panicmode("PERIOD", 10, 0); //dummy code
		}

		return isValid;
	}

	boolean statement(int mode) {
		System.out.println("SEMANTIC STATEMENT " + tokenLookAhead);
		boolean isValid = false;
		if (mode == 2) {
			if (tokenLookAhead.equals("if") || tokenLookAhead.equals("for") || tokenLookAhead.equals("while")) {
				this.statemode = 1;
				isValid = structuredStatement(statemode);
			} else {
				System.out.println("SEMANTIC SIMPLE STATEMENT");
				isValid = simpleStatement();
				
			}
		} else if (mode == 1) {
			if (tokenLookAhead.equals("if") || tokenLookAhead.equals("for") || tokenLookAhead.equals("body")) {
				this.statemode = 1;
				structuredStatement(statemode);
			} else {
				isValid = simpleStatement();
				
			}
		}
		
		this.printvarglob();

		
		return isValid;

	}

	boolean arrayDeclare(String exprdata) {
		boolean isValid = false;
		if (tokenLookAhead.equals("[")) {
			// only checks for expression GET BACK SINCE THERE IS FUNCTIONS TOO
			this.burstfunc();
			isValid = this.expression(0, exprdata);
		
			if (notthrown) {

		
				if (tokenLookAhead.equals("]")) {
					isValid = true;
					this.burstfunc();
				} else {
					// expected ]

					notthrown = false;
					newcount++;
					errparser.error_checker(36, "error.txt", newcount, tokenLookAhead);
				}
			}
		} else {
			// expected [
			notthrown = false;
			newcount++;
			errparser.error_checker(35, "error.txt", newcount, tokenLookAhead);
		}
		return isValid;
	}

	boolean funcDeclare(String exprdata) {
		boolean isValid = false;
		boolean hasparam = false, identifloc = false;
		boolean isExpr = true;
		String expgiv = "", typegive = "";
		boolean isitarray = false;
		int indnum = 0;
		String exprdata2 = "";
		int funcinde =this.funcind;
		int expparam = this.paramnoexpect;
		ArrayList<String> exp, exptype;
		exp = new ArrayList<String>();
		exptype = new ArrayList<String>();
		ArrayList<String> tokenLook, typeLook;
		int paramnum = 0;
		System.out.println("SHAKE1");
		if (canstarteval) {
		if (tokenLookAhead.equals("(")) {
			System.out.println("SHAKE12");
			this.burstfunc();
			if (tokenLookAhead.equals(")")) {
				//if no parameters
			}
			else {
				//if it has parameters
				System.out.println("SHAKE13");
				expressionbools es = new expressionbools();
				this.expb.add(es);
				hasparam = true;
				if (typeLookAhead.equals("STRING")) {
					this.searchedtype = "string";
					isExpr = false;
					
				}
				else if (typeLookAhead.equals("IDENTIFIER")) {
					this.search_identif(tokenLookAhead);
					indnum = this.identifindsearch;
					identifloc = this.identiffound;
					String dtyper = "";
					String varname = "";
					boolean hasval = false;
					if (identifloc) {
						int last = this.fm.size() - 1;
						varname = this.fm.get(last).getVl().get(indnum).getVariablename();
						dtyper = this.fm.get(last).getVl().get(indnum).getVartype();
						isitarray = this.fm.get(last).getVl().get(indnum).isIslist();
						hasval =  this.fm.get(last).getVl().get(indnum).isHasval();
						if (hasval) {
							if (dtyper.equals("string") || dtyper.equals("char")) {
								this.searchedtype = "string";
								isExpr = false;
							}
							else if (dtyper.equals("integer")) {
								this.searchedtype = "integer";
								isExpr = true;
							}
							else if (dtyper.equals("real")) {
								this.searchedtype = "real";
								isExpr = true;
							}
							else {
								this.searchedtype = "boolean";
								isExpr = true;
							}
						}
						else {
							notthrown = false;
							newcount++;
							System.out.println("Value of identifier is null " + varname);
							
						}
					}
					else {
						dtyper = this.varGlobal.get(indnum).getVartype();
						hasval = this.varGlobal.get(indnum).isHasval();
						varname = this.varGlobal.get(indnum).getVariablename();
						if (hasval) {
							if (dtyper.equals("string") || dtyper.equals("char")) {
								this.searchedtype = "string";
								isExpr = false;
							}
							else if (dtyper.equals("integer")) {
								this.searchedtype = "integer";
								isExpr = true;
							}
							else if (dtyper.equals("real")) {
								this.searchedtype = "real";
								isExpr = true;
							}
							else {
								this.searchedtype = "boolean";
								isExpr = true;
							}
						}
						else {
							notthrown = false;
							newcount++;
							System.out.println("Value of identifier is null " + varname);
							
						}
						
					}
				}
				else {
					this.searchedtype = "boolean";
					
					isExpr = true;
				}
				
				
				this.new_express_assign();
			}
			if (notthrown){
			isValid = this.expression(0, exprdata2);
			
			if (hasparam) {
				int exs = expb.size() - 1;
				this.expb.remove(exs);
				int dan = this.explister.size() - 1;
				expgiv = explister.get(dan).getExpr();
				typegive = explister.get(dan).getExpectedtype();
				boolean op1 = this.explister.get(dan).isIsbool(), op2 = this.explister.get(dan).isCanbereal();
				if (op1) {
					typegive = "boolean";
				}
				else if (op2) {
					typegive = "real";
				}
				else if (typegive.equals("string")) {
					
				}
				else {
					typegive = "integer";
				}
				exp.add(expgiv);
				exptype.add(typegive);
				
				this.removeexprarray();
			}
		}
			if (notthrown) {
				
				if (tokenLookAhead.equals(",")) {
					while (tokenLookAhead.equals(",") && notthrown) {
						expressionbools es = new expressionbools();
						this.expb.add(es);
						System.out.println("HOWDYDOO " + tokenLookAhead + " size" + exp.size());
						exprdata2 = "";
						
						this.burstfunc();
						if (typeLookAhead.equals("STRING")) {
							this.searchedtype = "string";
							isExpr = false;
							
						}
						else if (typeLookAhead.equals("IDENTIFIER")) {
							this.search_identif(tokenLookAhead);
							indnum = this.identifindsearch;
							identifloc = this.identiffound;
							String dtyper = "";
							String varname = "";
							boolean hasval = false;
							if (identifloc) {
								int last = this.fm.size() - 1;
								varname = this.fm.get(last).getVl().get(indnum).getVariablename();
								dtyper = this.fm.get(last).getVl().get(indnum).getVartype();
								isitarray = this.fm.get(last).getVl().get(indnum).isIslist();
								hasval =  this.fm.get(last).getVl().get(indnum).isHasval();
								if (hasval) {
									if (dtyper.equals("string") || dtyper.equals("char")) {
										this.searchedtype = "string";
										isExpr = false;
									}
									else if (dtyper.equals("integer")) {
										this.searchedtype = "integer";
										isExpr = true;
									}
									else if (dtyper.equals("real")) {
										this.searchedtype = "real";
										isExpr = true;
									}
									else {
										this.searchedtype = "boolean";
										isExpr = true;
									}
								}
								else {
									notthrown = false;
									newcount++;
									System.out.println("Value of identifier is null " + varname);
									
								}
							}
							else {
								dtyper = this.varGlobal.get(indnum).getVartype();
								hasval = this.varGlobal.get(indnum).isHasval();
								varname = this.varGlobal.get(indnum).getVariablename();
								if (hasval) {
									if (dtyper.equals("string") || dtyper.equals("char")) {
										this.searchedtype = "string";
										isExpr = false;
									}
									else if (dtyper.equals("integer")) {
										this.searchedtype = "integer";
										isExpr = true;
									}
									else if (dtyper.equals("real")) {
										this.searchedtype = "real";
										isExpr = true;
									}
									else {
										this.searchedtype = "boolean";
										isExpr = true;
									}
								}
								else {
									notthrown = false;
									newcount++;
									System.out.println("Value of identifier is null " + varname);
									
								}
								
							}
						}
						else {
							this.searchedtype = "boolean";
							
							isExpr = true;
						}
						
						if (notthrown) {
							this.new_express_assign();
							isValid = this.expression(0, exprdata2);
							if (notthrown) {
								int exs = expb.size() - 1;
								this.expb.remove(exs);
								int dan = this.explister.size() - 1;
								boolean op1 = this.explister.get(dan).isIsbool(), op2 = this.explister.get(dan).isCanbereal();
								expgiv = explister.get(dan).getExpr();
								typegive = explister.get(dan).getExpectedtype();
								if (op1) {
									typegive = "boolean";
								}
								else if (op2) {
									typegive = "real";
								}
								else if (typegive.equals("string")) {
									
								}
								else {
									typegive = "integer";
								}
								exp.add(expgiv);
								exptype.add(typegive);
								
								this.removeexprarray();
							}
						}
						

					}
				}
				if (notthrown) {
					
					if (tokenLookAhead.equals(")")) {
						
						for (int i = 0; i < exp.size(); i++) {
							System.out.println("EXPGOSU " + exp.get(i) + " TYPEGOSU" + exptype.get(i));
						}
						if (expparam == exp.size()) {
							int ln = this.fm.size() - 1;
							boolean matched = true;
							for (int i = 0; i < expparam ; i++) {
								String vtype = this.fm.get(ln).getVl().get(i).getVartype();
								String ptype = exptype.get(i);
								if (ptype.equals("string") && vtype.equals("char")) {
									int len = ptype.length();
									if (len >= 0 && len <= 1) {
										exptype.set(i, "char");
									}
									else {
										notthrown = false;
										newcount++;
										System.out.println("Char cannot be a string");
										matched = false;
										break;
									}
										
								}
								else if (ptype.equals("integer") && vtype.equals("real")) {
									exptype.set(i, "real");
								}
								else if (ptype.equals("real") && vtype.equals("integer")) {
									notthrown = false;
									newcount++;
									System.out.println("Cannot assign real to integer");
									matched = false;
									break;
								}
								else if (ptype.equals(vtype)) {
									
								}
								else {
									notthrown = false;
									newcount++;
									System.out.println("Mismatching datatype");
									matched = false;
									break;
								}
							}
							if (matched) {
								int yun = fm.size() - 1;
								tokenLook = fm.get(yun).getTokenList();
								typeLook = fm.get(yun).getTypeList();
								System.out.println("SIZE TYPE " + typeLook.size() + " SIZE TOKEN " + tokenLook.size());
								this.return_tokens(tokenLook, typeLook);
								this.peeker();
								this.compoundStatement(1);
							}
						}
						else {
							newcount++;
							notthrown = false;
							System.out.println("Parameters are not the same number with the function " + expparam + " yo " + exp.size());
						}
						if (notthrown) {
							isValid = true;
							
							this.burstfunc();
						}
						
						
					} else {
						// expected )
						notthrown = false;
						newcount++;
						errparser.error_checker(22, "error.txt", newcount, tokenLookAhead);

					}
				}

			}
		} else {
			// expected (
			notthrown = false;
			newcount++;
			errparser.error_checker(23, "error.txt", newcount, tokenLookAhead);
		}
	}//do not start eval
		else {
			if (tokenLookAhead.equals("(")) {
	    		this.backupfunc();
	    		this.burstfunc();
	    		
	    		isValid = this.expression(0, exprdata);
	    		if (notthrown) {
	    			System.out.println("WHAT IS UP CLOSING OUT " + tokenLookAhead);
	    			if (tokenLookAhead.equals(",")) {
	    				while (tokenLookAhead.equals(",") && notthrown) {
	    					this.backupfunc();
	    					this.burstfunc();
	    					isValid = this.expression(0, exprdata);
	    					
	    				}
	    			}
	    			if (notthrown) {
	    				System.out.println("IS SUPP CLOSING OUT " + tokenLookAhead + " size is " + this.errparser.get_errparselist().size());
	    				if (tokenLookAhead.equals(")")) {
	    					System.out.println("CLOSING OUT");
	    					isValid = true;
	    					this.backupfunc();
	        				this.burstfunc();
	        			}
	        			else {
	        				//expected )
	        				notthrown = false;
	        	        	newcount++;
	        				errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
	        				
	        			}
	    			}
	    			
	    		}
	    	}
	    	else {
	    		//expected (
	    		notthrown = false;
	        	newcount++;
				errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
	    	}
		}
		return false;
	}

	// <simpleStatement> ::= <assignment> | <readStatement> | <writeStatement>
	boolean simpleStatement() {
		boolean isValid = false;
		String exprdata = "";
		String tokname = tokenLookAhead;
		String toktype = "";
		int index = 0;
		boolean islocal = false;
		if (canstarteval) {
			if (typeLookAhead.equals("IDENTIFIER")) {
				//search for the data type;
				System.out.println("NUM " + hasnum + " sign " + hassign + " HI5 " + tokenLookAhead);
				//if found
				if(this.notthrown) {
					
					this.burstfunc();
					if (tokenLookAhead.equals("[")) {
						// array
						this.searchvariable(tokname);
						if (notthrown) {
							isValid = this.arrayDeclare(exprdata);
							if (notthrown) {
								
								exprdata = "";
			        			isValid = this.assignment(1);
			        			
			        		}
						}
						
						

					} else if (tokenLookAhead.equals("(")) {
						//search for func
						this.searchFunc(tokname);
						if (notthrown) {
							
							index = this.funcind;
							this.countparam(index);
							this.newfunccall(tokname, index);
							this.revertexpbool();
							isValid = this.funcDeclare(exprdata);
							this.removefunccall();
							this.paramnoexpect = 0;
						}
						
						
					} else {
						
						this.searchvariable(tokname);
						toktype = this.searchedtype;
						islocal = this.foundinlocal;
						index = this.searchind;
						isValid = this.assignment(1);
						if (notthrown) {
							if (toktype.equals("string") || toktype.equals("char")) {
								this.string_insert(tokname, toktype, islocal, index);
							}
							else {
								this.shunting_yard(tokname, toktype, islocal, index);
							}
							
		    				this.removeexprarray();
		    				
						}
						
						
					}
				}
				

			} else if (tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")) {
				isValid = this.readStatement();
			} else if (tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")) {
				isValid = this.writeStatement();
			}
		}
		//do not evaluate
		else {
			System.out.println("Simple statement parser " + tokenLookAhead);
	        if (typeLookAhead.equals("IDENTIFIER")) {
	        	String samp = tokenLookAhead;
	        	this.backupfunc();
	        	this.burstfunc();
	        	if (tokenLookAhead.equals("[")) {
	        		//array
	        		System.out.println("ARRAY GOES " + tokenLookAhead);
	        		isValid = this.arrayDeclare(exprdata);
	        		if (notthrown) {
	        			isValid = this.assignment(1);
	        		}
	        	}
	        	else if (tokenLookAhead.equals("(")) {
	        		
	        		isValid= this.funcDeclare(exprdata);
	        		System.out.println("CLEAR FUNC " + this.errparser.get_errparselist().size());
	        	}
	        	else {
	        		if (this.inloop) {
	        			if (samp.equals(this.varloop)) {
	        				notthrown = false;
	        				newcount++;
	        				System.out.println("Illegal expression in for loop" + samp);
	        			}
	        		}
	        		//System.out.println("BEFORE ASSIGN " + this.errparser.get_errparselist().size());
	            	isValid = this.assignment(1);
	            	//System.out.println("NOW ASSIGN " + this.errparser.get_errparselist().size());
	        	}
	        	
	        }
	        else if (tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")) {
	        	isValid = this.readStatement();
	        }
	        else if (tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")) {
	        	isValid = this.writeStatement();
	        }
		}
		
		

		return isValid;
	}

	boolean structuredStatement(int mode) {
		boolean isValid = false;
		if (tokenLookAhead.equals("begin")) {
			isValid = this.compoundStatement(2);
		} else if (tokenLookAhead.equals("if")) {
			isValid = this.ifStatement();
		} else if (tokenLookAhead.equals("for")) {
			isValid = this.forStatement();
		} else if (tokenLookAhead.equals("while")) {
			isValid = this.whileStatement();
		}
		

		return isValid;
	}

	boolean whileStatement() {
		boolean isValid = false;
		String exprdata = "";
		if (tokenLookAhead.equals("while")) {
			this.burstfunc();
			// expr
			isValid = this.expression(0, exprdata);
			if (notthrown) {
				if (tokenLookAhead.equals("do")) {
					this.burstfunc();
					isValid = this.compoundStatement(1);
					if (notthrown) {
						tokenStack.push(";");
						this.tokenTypeStack.push("SEMICOLON");
						peeker();
					}
				} else {
					// expected do
					notthrown = false;
					newcount++;
					errparser.error_checker(41, "error.txt", newcount, tokenLookAhead);
				}
			}

		} else {
			// expected while
			notthrown = false;
			newcount++;
			errparser.error_checker(44, "error.txt", newcount, tokenLookAhead);

		}
		return false;
	}

	// <compoundStatement> ::= begin <statement> end
	boolean compoundStatement(int mode) {
		boolean isValid = false, canstate = false, shakdated = false, wowzer = false, popsemi = false;
		if (this.canstarteval){
		
		if (sagemark) {
			canstate = true;
			sagemark = false;
		}


		if (mode == 0) {
			System.out.println("SEMANTIC ASD " + tokenLookAhead);
			this.statemode = 2;
			wowzer = true;

		}
		if (shak) {
			mode = 1;
			shakdated = true;
			shak = false;
			
		}
		if (tokenLookAhead.equals("begin")) {
			this.statemode = 2;
			System.out.println("BEGINNER " + tokenLookAhead);
			tokenPopper();
			tokenTypePopper();
			peeker();
			if (shak) {

			}

			isValid = this.statement(statemode);
			numexpr = 0;
			System.out.println("EXPR DATA " + globeexpr);
			this.globeexpr = "";
			this.revertexpbool();
			if (notthrown) {
				System.out.println("After begin " + tokenLookAhead);
				if (shak) {
					System.out.println("Kun3 " + mode);
				}
				while (tokenLookAhead.equals(";") && notthrown) {
					if (shak) {
						System.out.println("Kun2 " + mode);
					}
					tokenPopper();
					tokenTypePopper();
					peeker();

					isValid = statement(statemode);
					numexpr = 0;
					System.out.println("EXPR DATA2 " + globeexpr);
					this.globeexpr = "";
					this.revertexpbool();
					System.out.println("FOR LOOP END LETS SEE " + tokenLookAhead);
					
				}
				System.out.println("BEFORE CHECKING END SEM " + tokenLookAhead);
				
				if (notthrown) {
					if (tokenLookAhead.equals("end")) {
					System.out.println("CHEZ");
					tokenPopper();
					System.out.println("CHEZ2");
					tokenTypePopper();
					System.out.println("CHEZ3");
					peeker();
					System.out.println("CHEZ4");
					// System.out.println("Endstering " + mode + " because " +
					// this.errparser.get_errparselist().size());

					System.out.println("SEMANTIC MODE IS " + mode);
					if (mode == 0) {
						System.out.println("They call me out cuz " + tokenLookAhead);
						if (tokenLookAhead.equals(".")) {
							// System.out.println("AS IT SHOULD " + errparser.get_errparselist().size());
							isValid = true;
						} else {
							// expected dot
							System.out.println("THIS IS WHY " + tokenLookAhead);
							notthrown = false;
							newcount++;
							errparser.error_checker(16, "error.txt", newcount, tokenLookAhead);
						}
					} else if (mode == 1){
						System.out.println(
								" asdfaas " + tokenLookAhead + " " + tokenLookAhead.equals(";") + " " + sagemark);
						if (sagemark) {
							System.out.println("Sage goes here");
						}
						if (tokenLookAhead.equals(";")) {
							System.out.println("BEFORE GASS");
							isValid = true;

							this.burstfunc();
							popsemi = false;

							if (shakdated) {
								shakdated = false;
								shak = true;
							}

						} else {
							// expected semicolon
							notthrown = false;
							newcount++;
							isValid = false;
							// errparser.error_checker(7, "error.txt", newcount, tokenLookAhead);
						}
					}
				} else {
					// expected end
					System.out.println("END GOES HERE SEM " + mode + " " + tokenLookAhead);
					notthrown = false;
					newcount++;
					isValid = false;
					// errparser.error_checker(17, "error.txt", newcount, tokenLookAhead);

				}
			}
			}
		}
		// Error: Expected begin
		else {
			// dummy code change with whatever applicable
			newcount++;
			errparser.error_checker(18, "error.txt", newcount, tokenLookAhead);
			// this.panicmode("PERIOD", 10, 0); //dummy code
		}
	}
		//dont evaluate
		else {
			 if (sagemark) {
		        	canstate = true;
		        	sagemark = false;
		        }
		       
		        System.out.println("Kun6 " + mode);
		        if (mode == 0) {
		        	System.out.println("ASD " + tokenLookAhead);
		        	this.statemode = 2;
		        	wowzer = true;
		        	
		        }
		        if (shak) {
		        	mode = 1;
		        	shakdated = true;
		        	shak = false;
		        	System.out.println("Kun5 " + mode + tokenLookAhead);
		        }
		        if(tokenLookAhead.equals("begin")){
		        	this.backupfunc();
		        	
		            this.statemode = 2;
		            System.out.println("HAsa " + mode);
		            tokenPopper();
		            tokenTypePopper();
		            peeker();
		            System.out.println("DONT EVALUATE BODY " + tokenLookAhead);
		            if (shak) {
		            	System.out.println("Kun4 " + mode);
		            }
		            //System.out.println("LETS SEE HERE begin " + this.errparser.get_errparselist().size());
		            isValid = this.statement(statemode);
		            //System.out.println("LETS SEE HERE " + this.errparser.get_errparselist().size());
		            if (shak) {
		            	System.out.println("Kun3 " + mode);
		            }
		            while (tokenLookAhead.equals(";") && notthrown) {
		            	if (shak) {
		                	System.out.println("Kun2 " + mode);
		                }
		            	System.out.println("CHECK THIS OUT " + notthrown );
		            	this.backupfunc();
		            	tokenPopper();
		                tokenTypePopper();
		                peeker();
		                //System.out.println("IZA " + tokenLookAhead + " " + statemode + " size is " + this.errparser.get_errparselist().size() + " MODE " + mode + " shak " + shak);
		                isValid = statement(statemode);
		                System.out.println("CHECK THIS OUT2 " + notthrown );
		                System.out.println("FOR LOOP END LETS SEE " + tokenLookAhead);
		            }
		            System.out.println("SEMANTIC BEFORE " + tokenLookAhead + "seep" + " " + notthrown);
		            if (notthrown){ if (tokenLookAhead.equals("end")) {
		            	this.backupfunc();
		            	tokenPopper();
		                tokenTypePopper();
		                peeker();
		                System.out.println("SEMANTIC END " + tokenLookAhead);
		                //System.out.println("Endstering " + mode + " because " + this.errparser.get_errparselist().size());
		                if (shak) {
		                	System.out.println("Kun " + mode);
		                }
		                if (wowzer) {
		                	System.out.println("Wowzaaaa");
		                }
		                System.out.println("MODE IS " + mode);
		                if (mode == 0) {
		                	System.out.println("They call me out cuz " + tokenLookAhead);
		                	if (tokenLookAhead.equals(".")) {
		                		
		                		//System.out.println("AS IT SHOULD " + errparser.get_errparselist().size());
		                    	isValid = true;
		                    }
		                    else {
		                    	//expected dot
		                    	System.out.println("THIS IS WHY " + tokenLookAhead);
		                    	notthrown = false;
		                    	newcount++;
								errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
		                    }
		                }
		                else if (mode == 1) {
		                	System.out.println(" asdfaas " + tokenLookAhead + " " + tokenLookAhead.equals(";") + " " + sagemark);
		                	if (sagemark) {
		                		System.out.println("Sage goes here");
		                	}
		                	if (tokenLookAhead.equals(";")) {
		                		System.out.println("BEFORE GASS");
		                    		isValid = true;
		                    		this.backupfunc();
		                    		this.burstfunc();
		                    		popsemi = false;
		                    	
		                    	
		                    	if (shakdated) {
		                    		shakdated = false;
		                    		shak = true;
		                    	}
								
		                    }
		                    else {
		                    	//expected semicolon
		                    	notthrown = false;
		                    	newcount++;
		                    	isValid = false;
								errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
		                    }
		                }
		            }
		            else {
		            	//expected end
		            	notthrown = false;
		            	newcount++;
		            	isValid = false;
						errparser.error_checker(17, "error.txt" , newcount, tokenLookAhead);
		            	
		            }
		        }
		            
		        }
		        // Error: Expected begin
		        else {
		        	//dummy code change with whatever applicable
		        	newcount++;
					errparser.error_checker(18, "error.txt" , newcount, tokenLookAhead);
					//this.panicmode("PERIOD", 10, 0); //dummy code
		        }

		}
		
		return isValid;
	}

	// <readStatement> ::= read ( *IDENTIFIER* , *IDENTIFIER* ) | readln (
	// *IDENTIFIER* , *IDENTIFIER* )
	boolean readStatement() {
		String exprdata = "";
		boolean isValid = false, hasparam = false;
		String vartype = "";
		System.out.println("SEMANTIC READ STATEMENT " + tokenLookAhead);
		String ender = "";
		boolean flocal = false;
		int ind = 0;
		if (canstarteval) {
			if (tokenLookAhead.equals("readln")) {
				ender = "\n";
			}
			if (tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")) {
				this.burstfunc();
				if (tokenLookAhead.equals("(")) {

					
					this.burstfunc();
					if (typeLookAhead.equals("IDENTIFIER")) {
						this.search_identif(tokenLookAhead);
						
						if (notthrown) {
							ind = this.identifindsearch;
							
							flocal = this.identiffound;
							if (flocal) {
								
							}
							else {
								vartype = this.varGlobal.get(ind).getVartype();
							}
							this.burstfunc();
							System.out.println("VARIS " + vartype + " num " + ind);
						}
						
						
						
					}
					else {
						notthrown = false;
						newcount++;
						errparser.error_checker(5, "error.txt", newcount, tokenLookAhead);
					}
					if (notthrown) {

						
						if (notthrown) {

							
							if (tokenLookAhead.equals(")")) {
								Scanner sc = new Scanner(System.in);
								if (vartype.equals("string")) {
									try {
										System.out.println("CHECK STRING NOW");
										String inp = sc.nextLine();
										inp = inp + ender;
										if (flocal) {
											
										}
										else {
											this.varGlobal.get(ind).setStringval(inp);
										}
									}
									catch(Exception e) {
										notthrown = false;
										newcount++;
										System.out.println("Expected string ");
									}
								}
								else if (vartype.equals("char")) {
									try {
										System.out.println("CHECK STRING2 NOW");
										char c = sc.next().charAt(0);
										String inp = Character.toString(c);
										if (flocal) {
											
										}
										else {
											this.varGlobal.get(ind).setCharval(inp);
										}
									}
									catch(Exception e) {
										notthrown = false;
										newcount++;
										System.out.println("Expected char ");
									}
								}
								else if (vartype.equals("integer")) {
									try {
										System.out.println("CHECK STRING NOW3");
										int inp = sc.nextInt();
										if (flocal) {
											
										}
										else {
											this.varGlobal.get(ind).setIntval(inp);
										}
									}
									catch(Exception e) {
										notthrown = false;
										newcount++;
										System.out.println("Expected integer ");
									}
								}
								else if (vartype.equals("real")) {
									try {
										System.out.println("CHECK STRING NOW4");
										float inp = sc.nextFloat();
										if (flocal) {
											
										}
										else {
											this.varGlobal.get(ind).setFloatval(inp);
										}
										
									}
									catch(Exception e) {
										notthrown = false;
										newcount++;
										System.out.println("Expected integer ");
									}
								}
								else if (vartype.equals("boolean")) {
									try {
										System.out.println("CHECK STRING NOW5");
										boolean inp = sc.nextBoolean();
										if (flocal) {
											
										}
										else {
											this.varGlobal.get(ind).setBoolval(inp);
										}
										
									}
									catch(Exception e) {
										notthrown = false;
										newcount++;
										System.out.println("Expected integer ");
									}
								}
								this.burstfunc();

							} else {
								// expected )
								notthrown = false;

								newcount++;
								errparser.error_checker(22, "error.txt", newcount, tokenLookAhead);
							}
						}

					}

					
				} else {
					// expected (
					notthrown = false;
					newcount++;
					errparser.error_checker(23, "error.txt", newcount, tokenLookAhead);
				}

			}
		}
		//do not evaluate
		else {
			System.out.println("READ STATEMENT " + tokenLookAhead);
	        
	        if (tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")) {
	        	this.backupfunc();
				this.burstfunc();
				if (tokenLookAhead.equals("(")) {
					this.backupfunc();
					
					this.burstfunc();
					if (typeLookAhead.equals("IDENTIFIER")) {
						this.backupfunc();
						this.burstfunc();
					}
					else {
						notthrown = false;
						newcount++;
						errparser.error_checker(5, "error.txt", newcount, tokenLookAhead);
					}
					if (notthrown) {

						
						if (notthrown) {

							
							if (tokenLookAhead.equals(")")) {
								this.backupfunc();
								this.burstfunc();

							} else {
								// expected )
								notthrown = false;

								newcount++;
								errparser.error_checker(22, "error.txt", newcount, tokenLookAhead);
							}
						}

					}

					
				} else {
					// expected (
					notthrown = false;
					newcount++;
					errparser.error_checker(23, "error.txt", newcount, tokenLookAhead);
				}

			}
		}
		
		
		return isValid;
	}

	// <writeStatement> ::= write ( *IDENTIFIER* , *IDENTIFIER* ) | writeln (
	// *IDENTIFIER* , *IDENTIFIER* )
	boolean writeStatement() {
		boolean isValid = false;
		String exprdata ="";
		String start = "";
		String ender = "";
		boolean identifloc = false;
		int indnum = 0;
		boolean isExpr = false;
		if (canstarteval){
		if (tokenLookAhead.equals("writeln")) {
			ender = "\n";
		}
		if (tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")) {
			this.burstfunc();
			// if open parenthesis
			if (tokenLookAhead.equals("(")) {
				// if variables
				System.out.println("LOUDER 67" + tokenLookAhead);
				this.burstfunc();
				System.out.println("LOUDER 61" + tokenLookAhead);
				//GET BACK
				if (typeLookAhead.equals("STRING")) {
					this.searchedtype = "string";
					isExpr = false;
					
				}
				else if (typeLookAhead.equals("IDENTIFIER")) {
					
					this.search_identif(tokenLookAhead);
					indnum = this.identifindsearch;
					identifloc = this.identiffound;
					String dtyper = "";
					String varname = "";
					boolean hasval = false;
					System.out.println("LOUDER 6" + tokenLookAhead);
					if (identifloc) {
						
						System.out.println("PROMPTO " );
						int noa = this.fm.size() - 1;
						dtyper = this.fm.get(noa).getVl().get(indnum).getVartype();
						hasval = this.fm.get(noa).getVl().get(indnum).isHasval();
						varname = this.fm.get(noa).getVl().get(indnum).getVariablename();
						if (hasval) {
							if (dtyper.equals("string") || dtyper.equals("char")) {
								this.searchedtype = "string";
								isExpr = false;
							}
							else if (dtyper.equals("integer")) {
								this.searchedtype = "integer";
								isExpr = true;
							}
							else if (dtyper.equals("real")) {
								this.searchedtype = "real";
								isExpr = true;
							}
							else {
								this.searchedtype = "boolean";
								isExpr = true;
							}
						}
						else {
							notthrown = false;
							newcount++;
							System.out.println("Value of identifier is null " + varname);
							
						}
					}
					else {
						dtyper = this.varGlobal.get(indnum).getVartype();
						hasval = this.varGlobal.get(indnum).isHasval();
						varname = this.varGlobal.get(indnum).getVariablename();
						if (hasval) {
							if (dtyper.equals("string") || dtyper.equals("char")) {
								this.searchedtype = "string";
								isExpr = false;
							}
							else if (dtyper.equals("integer")) {
								this.searchedtype = "integer";
								isExpr = true;
							}
							else if (dtyper.equals("real")) {
								this.searchedtype = "real";
								isExpr = true;
							}
							else {
								this.searchedtype = "boolean";
								isExpr = true;
							}
						}
						else {
							notthrown = false;
							newcount++;
							System.out.println("Value of identifier is null " + varname);
							
						}
						
					}
				}
				else {
					this.searchedtype = "boolean";
					
					isExpr = true;
				}
				System.out.println("LOUDER 5" + tokenLookAhead);
				this.new_express_assign();
				isValid = this.expression(0, exprdata);
				this.revertexpbool();
				System.out.println("LOUDER 3" + tokenLookAhead);
				if (notthrown) {
					if (isExpr) {
						System.out.println("SHYU ASA");
						start = this.shunt_yard2(start);
					}
					else {
						System.out.println("SHYUMU ASA");
						start = this.insert_string_2(start);
					}
					this.removeexprarray();
				}
				
				if (notthrown) {
					
					if (tokenLookAhead.equals(",")) {

						while (tokenLookAhead.equals(",") && notthrown) {
							System.out.println("LOUDER 1" + tokenLookAhead);
							this.burstfunc();
							System.out.println("LOUDER 1" + tokenLookAhead);
							if (typeLookAhead.equals("STRING")) {
								this.searchedtype = "string";
								isExpr = false;
								
							}
							else if (typeLookAhead.equals("IDENTIFIER")) {
								this.search_identif(tokenLookAhead);
								indnum = this.identifindsearch;
								identifloc = this.identiffound;
								String dtyper = "";
								String varname = "";
								System.out.println("ANOTHER IDENTIF " + identifloc + " as " + tokenLookAhead);
								boolean hasval = false;
								if (identifloc) {
									System.out.println("PROMPTO " + indnum );
									int noa = this.fm.size() - 1;
									dtyper = this.fm.get(noa).getVl().get(indnum).getVartype();
									hasval = this.fm.get(noa).getVl().get(indnum).isHasval();
									varname = this.fm.get(noa).getVl().get(indnum).getVariablename();
									if (hasval) {
										if (dtyper.equals("string") || dtyper.equals("char")) {
											this.searchedtype = "string";
											isExpr = false;
										}
										else if (dtyper.equals("integer")) {
											this.searchedtype = "integer";
											isExpr = true;
										}
										else if (dtyper.equals("real")) {
											this.searchedtype = "real";
											isExpr = true;
										}
										else {
											this.searchedtype = "boolean";
											isExpr = true;
										}
									}
									else {
										notthrown = false;
										newcount++;
										System.out.println("Value of identifier is null " + varname);
										
									}
								}
								else {
									dtyper = this.varGlobal.get(indnum).getVartype();
									hasval = this.varGlobal.get(indnum).isHasval();
									varname = this.varGlobal.get(indnum).getVariablename();
									if (hasval) {
										if (dtyper.equals("string") || dtyper.equals("char")) {
											this.searchedtype = "string";
											isExpr = false;
										}
										else if (dtyper.equals("integer")) {
											this.searchedtype = "integer";
											isExpr = true;
										}
										else if (dtyper.equals("real")) {
											this.searchedtype = "real";
											isExpr = true;
										}
										else {
											this.searchedtype = "boolean";
											isExpr = true;
										}
									}
									else {
										notthrown = false;
										newcount++;
										System.out.println("Value of identifier is null " + varname);
										
									}
									
								}
							}
							else {
								this.searchedtype = "boolean";
								
								isExpr = true;
							}
							this.new_express_assign();
							System.out.println("JAKE " + tokenLookAhead);
							isValid = this.expression(0, exprdata);
							this.revertexpbool();
							System.out.println("CHECK MY RIDE " + tokenLookAhead + " isExpr " + isExpr + " kan " + notthrown);
							if (notthrown) {
								if (isExpr) {
									start = this.shunt_yard2(start);
								}
								else {
									start = this.insert_string_2(start);
								}
								this.removeexprarray();
							}
							System.out.println("ZOOOOOOOM " + tokenLookAhead + " asd " + notthrown);
						}
					}
					if (notthrown) {
						
						// if )
					
						if (tokenLookAhead.equals(")")) {
							start = start + ender;
							System.out.println("WEE PRINT");
							System.out.print(start);
							System.out.println("ENDO");
							this.burstfunc();
						} else {
							// expected )
							notthrown = false;
							newcount++;
							errparser.error_checker(22, "error.txt", newcount, tokenLookAhead);
						}
					}
				}
				// if it is a string, real, integer, bool

			} else {
				// expected (
				notthrown = false;
				newcount++;
				errparser.error_checker(23, "error.txt", newcount, tokenLookAhead);
			}
		} else {
			// expected write
		}
	}//Do not evaluate
		else {
		
	        if (tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	//if open parenthesis
	        	if (tokenLookAhead.equals("(")) {
	        		//if variables
	        		
	        		this.backupfunc();
	        		this.burstfunc();
	        		System.out.println("SLASHSHING " + tokenLookAhead);
	        		isValid = this.expression(0, exprdata);
	        		
	        		if (notthrown) {
	        			
	        			if (tokenLookAhead.equals(",")) {
	        				
	        				while (tokenLookAhead.equals(",") && notthrown) {
	        					this.backupfunc();
	        					this.burstfunc();
	        					isValid = this.expression(0, exprdata);
	        				
	        				}
	        			}
	        			if (notthrown) {
	        		
	        				//if )
	        				if (tokenLookAhead.equals(")")) {
	        					this.backupfunc();
	        					this.burstfunc();
	        				}
	        				else {
	        					//expected )
	        					notthrown = false;
	            	        	newcount++;
	            				errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
	        				}
	        			}
	        		}
	        		//if it is a string, real, integer, bool
	        		
	        		
	        	}
	        	else {
	        		//expected (
	        		notthrown = false;
		        	newcount++;
					errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
	        	}
	        }
	        else {
	        	//expected write
	        }
		}

		
		return isValid;
	}

	boolean functionDeclaration() {
		boolean isValid = false;
		boolean isGoing = true;
		ArrayList<String> tokenLook, typeLook;
		tokenLook = new ArrayList<String>();
		typeLook = new ArrayList<String>();
		String prevToken = "";
		String nameoffunc  ="";
		ArrayList<String> varn, vart;
		varn = new ArrayList<String>();
		vart = new ArrayList<String>();
		// required function
		
		function fn = new function();
	
		//required function
				if (tokenLookAhead.equals("function")) {
					
					this.burstfunc();
					//required identifier
					if (typeLookAhead.equals("IDENTIFIER")) {
						nameoffunc = tokenLookAhead;
						fn.setFuncname(nameoffunc);
						this.burstfunc();
						//required open parenthesis
						if (tokenLookAhead.equals("(")) {
							this.burstfunc();
							//optional variables
							if (typeLookAhead.equals("IDENTIFIER")) {
								//if went for optional variables go here
								while (typeLookAhead.equals("IDENTIFIER") && notthrown) {
									varn.add(tokenLookAhead);
									this.burstfunc();
									//required comma
									if (tokenLookAhead.equals(",")) {
										while (tokenLookAhead.equals(",") && notthrown) {
											this.burstfunc();
											if (typeLookAhead.equals("IDENTIFIER")) {
												varn.add(tokenLookAhead);
												this.burstfunc();
											} else {
												// expected identifier
												notthrown = false;
											}
										}
									}
									if (notthrown) {
										//
										if (tokenLookAhead.equals(":")) {
											this.burstfunc();
											if (typeLookAhead.equals("DATA_TYPE")) {
												for (int i = 0; i < varn.size(); i++) {
													vart.add(tokenLookAhead);
												}
												for (int i = 0; i < varn.size(); i++) {
													variableLocal vl = new variableLocal(varn.get(i), vart.get(i), 0, nameoffunc, false);
													vl.setIsnotparam(false);
													this.varLocal.add(vl);
												}
												
												vart.clear();
												varn.clear();
												this.checkLocalDupes(varLocal);
												if (notthrown) {this.burstfunc();
												// optional semicolon
												if (tokenLookAhead.equals(";")) {
													//check if identifier is next
													this.burstfunc();
													if (typeLookAhead.equals("IDENTIFIER")) {
														
													}
													else {
														//expected identifier
														notthrown = false;
											            newcount++;
											        	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
													}
												}
											}

											} else {
												// expected datatype
												notthrown = false;
									            newcount++;
									        	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
											}
										} else {
											// expected colon
											notthrown = false;
								            newcount++;
								        	errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
										}
									}

								}

							} 
							//required close parenthesis
							if (notthrown) {if (tokenLookAhead.equals(")")) {
								this.burstfunc();
								//check if after close paren :
								if (tokenLookAhead.equals(":")) {
									this.burstfunc();
									if (typeLookAhead.equals("DATA_TYPE") || typeLookAhead.equals("VOID")) {
										String functype = tokenLookAhead;
										fn.setFunctype(tokenLookAhead);
										variableLocal vl2 = new variableLocal(nameoffunc, functype, 0, nameoffunc, false);
										vl2.setIsnotparam(true);
										this.varLocal.add(vl2);
										this.checkLocalDupes(varLocal);
										if (notthrown) {
										this.burstfunc();
										if (tokenLookAhead.equals(";")) {
											isValid = true;
											this.burstfunc();
											if (tokenLookAhead.equals("var")) {
												//if var
												while (tokenLookAhead.equals("var") && notthrown) {
													isValid = this.variableDeclaration(1, nameoffunc);
												}
												
											}
											if (notthrown) {
												
												this.printvariablelocal();
												fn.setVarLocal(varLocal);
												this.varLocal.clear();
												this.compoundStatement(1);
												
												if (notthrown) {
													fn.insertbody(this.tokenbackup, this.typebackup);
													ArrayList<String> samp = fn.getTokenList();
													
													this.clearbackupfunc();
													this.functioncall.add(fn);
												}
												
											}
										}
										else {
											//expected semicolon
											notthrown = false;
								            newcount++;
								        	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
										}
									}
									}
									else {
										//invalid func type
										notthrown = false;
							            newcount++;
							        	errparser.error_checker(33, "error.txt" , newcount, tokenLookAhead);
									}
									
								}
								else {
									
									//expected colon
									
									notthrown = false;
						            newcount++;
						        	errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
								}
							}
						
							else {
								//expected close paren
								notthrown = false;
					            newcount++;
					        	errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
								
							}
							}
						} else {
							// expected open parenthesis
							notthrown = false;
				            newcount++;
				        	errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);

						}
					} else {
						// expected identifier
						notthrown = false;
			            newcount++;
			        	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
					}
				} else {
					// expected function
					notthrown = false;
		            newcount++;
		        	errparser.error_checker(32, "error.txt" , newcount, tokenLookAhead);
				}
		

		return isValid;
	}

	void print_errors() {

		ArrayList<String> errorList = errparser.get_errparselist();

		for (int i = 0; i < errorList.size(); i++) {
			System.out.println(errorList.get(i));
		}
	}

	String tokenPopper() {
		return tokenStack.pop();
	}

	String tokenTypePopper() {
		return tokenTypeStack.pop();
	}

	void peeker() {
		if (!tokenStack.empty()) { // proceed to peek at the next token
			tokenLookAhead = tokenStack.peek();
			typeLookAhead = tokenTypeStack.peek();
		}

	}

	boolean assignment(int mode) {
		boolean isValid = true;
		String exprdata = "";
		
		if (canstarteval) {
			
			if (typeLookAhead.equals("COLON_EQUALS")) {
				this.burstfunc();
				this.numexpr = 0;
				
				this.new_express_assign();
				isValid = this.expression(mode, exprdata);
				if (tokenLookAhead.equals("end")) {
					
				}
				if (notthrown) {
					this.print_explister();
					this.revertexpbool();
					
				}
				

			} else {
				// expected colon equals
				if (mode == 1) {
					notthrown = false;
					newcount++;
					errparser.error_checker(43, "error.txt", newcount, tokenLookAhead);
				}

			}
		}
		//do not evaluate
		else {
			
	    	
	        
	      
	        if (typeLookAhead.equals("COLON_EQUALS")) {
	        	this.backupfunc();
	        	this.burstfunc();
	        	isValid = this.expression(mode, exprdata);
	        	if (tokenLookAhead.equals("end")) {
	        		
	        	}
	        	
	        }
	        else {
	        	//expected colon equals
	        	if (mode == 1) {
	        		notthrown = false;
	            	newcount++;
	            	errparser.error_checker(43, "error.txt" , newcount, tokenLookAhead);
	        	}
	        	
	        }
		}
		
		
		return isValid;

	}
	void checkLocalDupes (ArrayList<variableLocal> vl) {
		boolean hasfound = false;
		String compa = "", compa2 = "";
	
		for (int i = 0; i < vl.size(); i++) {
			for (int j = 0; j < vl.size(); j++) {
				compa = vl.get(i).getVariablename();
				compa2 = vl.get(j).getVariablename();
				
				if (compa.equals(compa2) && i != j) {
					notthrown = false;
					hasfound = true;
					break;
				}
			}
			if (hasfound) {
				notthrown = false;
				break;
				
			}
		}
		if (notthrown) {
			
		}
		else {
			//duplicate global names found
			notthrown = false;
			newcount++;
			System.out.println("Local duplicates found");
		}
	}
	void checkDuplicates (ArrayList<variableGlobal> vg) {
		boolean hasfound = false;
		String compa = "", compa2 = "";
		
		for (int i = 0; i < vg.size(); i++) {
			for (int j = 0; j < vg.size(); j++) {
				compa = vg.get(i).getVariablename();
				compa2 = vg.get(j).getVariablename();
				
				if (compa.equals(compa2) && i != j) {
					notthrown = false;
					hasfound = true;
					break;
				}
			}
			if (hasfound) {
				notthrown = false;
				break;
				
			}
		}
		if (notthrown) {
			
		}
		else {
			//duplicate global names found
			notthrown = false;
			newcount++;
			System.out.println("Global duplicates found");
		}
	}
	void printvariableglob() {
		String varnaming = "", vartyping = "";
		boolean isitarray = false;
		
		for (int i = 0; i < this.varGlobal.size(); i++) {
			int num = i + 1;
			varnaming = varGlobal.get(i).getVariablename();
			vartyping = varGlobal.get(i).getVartype();
			isitarray = varGlobal.get(i).isIslist();
			System.out.println(num + " : " + varnaming + " " + vartyping + " islist = " + isitarray);
		}
	}
	void printvariablelocal() {
		String varnaming = "", vartyping = "";
		boolean isitarray = false;
		boolean isparam = false;
		for (int i = 0; i < this.varLocal.size(); i++) {
			int num = i + 1;
			varnaming = varLocal.get(i).getVariablename();
			vartyping = varLocal.get(i).getVartype();
			isitarray = varLocal.get(i).isIslist();
			isparam = varLocal.get(i).isIsnotparam();
			String namefunc = varLocal.get(i).getFromfunc();
			System.out.println(num + " : " + varnaming + " " + vartyping + " islist = " + isitarray + " isitnotparam = " + isparam + " from function "  + namefunc);
		}
	}
	void insert_value_local(String identif) {
		
	}
	void insert_value_global(String identif) {
		int ind = 0;
		String datatype = "";
		
		boolean hasnolocal = true, hasfound = false, dontstop = true;
		boolean isitarray = false;
		String varnaming = "", vartyping = "";
		//search for function variables
		//search for global
		if (hasnolocal && dontstop) {
			for (int i = 0; i < this.varGlobal.size(); i++) {
				int num = i + 1;
				varnaming = varGlobal.get(i).getVariablename();
				vartyping = varGlobal.get(i).getVartype();
				isitarray = varGlobal.get(i).isIslist();
				datatype = varGlobal.get(i).getVartype();
				if (identif.equals(varnaming)) {
					hasfound = true;
					ind = i;
					break;
				}
			}
		}
		if (hasfound) {
		
		}
		else {
			
			//error: not found variable exception
			notthrown = false;
			newcount++;
			System.out.println("Not found variable");
		}
	}
	void searchvariable(String identif) {
		int ind = 0;
		String datatype = "";
		int fnum = fm.size() - 1;
		boolean hasnolocal = true, hasfound = false, dontstop = true;
		boolean isitarray = false, locall = false;
		String varnaming = "", vartyping = "";
		//search for function variables
		//search for global
		
		if (fnum >=0){
			int sam = fm.get(fnum).getVl().size();
			
			for (int i = 0; i < sam; i++) {
			int num = i + 1;
			varnaming = fm.get(fnum).getVl().get(i).getVariablename();
			vartyping = fm.get(fnum).getVl().get(i).getVartype();
			isitarray = fm.get(fnum).getVl().get(i).isIslist();
			datatype = fm.get(fnum).getVl().get(i).getVartype();
			
			if (identif.equals(varnaming)) {
				hasnolocal = false;
				hasfound = true;
				ind = i;
				locall = true;
				this.set_AssignData(vartyping, isitarray);
				break;
			}
		}
	}
		if (hasnolocal && dontstop) {
			for (int i = 0; i < this.varGlobal.size(); i++) {
				int num = i + 1;
				varnaming = varGlobal.get(i).getVariablename();
				vartyping = varGlobal.get(i).getVartype();
				isitarray = varGlobal.get(i).isIslist();
				datatype = varGlobal.get(i).getVartype();
				if (identif.equals(varnaming)) {
					hasfound = true;
					ind = i;
					this.set_AssignData(vartyping, isitarray);
					break;
				}
			}
		}
		if (hasfound) {
			this.searchedtype = datatype;
			this.foundinlocal = locall;
			this.searchind = ind;
		}
		else {
			
			//error: not found variable exception
			notthrown = false;
			newcount++;
			System.out.println("Not found variable");
		}
		
	}
	void set_AssignData(String type, boolean isarray) {
		this.assigndata = type;
		this.isarraytype = isarray;
	}
	void set_exprcall(String tok, String exprdata) {
		
		String exp = "";
		if (this.numexpr == 0) {
			exprdata = exprdata + tok; 
		}
		else {
			exprdata = exprdata + " " + tok; 
		}
		exp = exprdata;
		
	}
	void checkexpress(int mode, String tokenLookAhead) {
		int num = this.explister.size() - 1, numexpr = this.numexpr;
		int sepnum = 0;
		String type = this.explister.get(num).getExpectedtype();
		boolean hasnum = this.hasnum, hassign = this.hassign, hasopenparen = this.hasopenparen, hascloseparen = this.hascloseparen;
		boolean isnotfuncparam = true;
		if (expb.size() > 0) {
		
			sepnum = expb.size() - 1;
			hasnum = expb.get(sepnum).isHasnum();
			hassign = expb.get(sepnum).isHassign();
		
			hasopenparen = expb.get(sepnum).isHasopenparen();
			hascloseparen = expb.get(sepnum).isHascloseparen();
			numexpr = expb.get(sepnum).getNumexpr();
			isnotfuncparam = false;
		}
		if (mode == 0) {
			//num function 
						
			//check if there is a sign
			if (!(hasnum) && hassign) {
				
				if (hascloseparen) {
					//if after )
					notthrown = false;
					newcount++;
					if (type.equals("String") || type.equals("char")) {
						notthrown = false;
						newcount++;
						System.out.println("Char cannot be followed by a parenthesis");
					}
					else {
						notthrown = false;
						newcount++;
						System.out.println("Num cannot be after ) " + tokenLookAhead);
					}
					
				}
				else {
					
					if (isnotfuncparam) {
						this.hasnum = true;
						this.hassign = false;
						this.hasopenparen = false;
					}
					else {
						numexpr = numexpr + 1;
						sepnum = expb.size() - 1;
						expb.get(sepnum).setHasnum(true);
						expb.get(sepnum).setHassign(false);
					
						expb.get(sepnum).setHasopenparen(false);
						
						expb.get(sepnum).setNumexpr(numexpr);
					}
					
				}
				
				
			} else {

				// exception expected number or true expression
				notthrown = false;
				newcount++;
				if (type.equals("String") || type.equals("char")) {
				
					System.out.println("Excess " + type + " " + tokenLookAhead);
				}
				else {
					
					System.out.println("Excess number " + tokenLookAhead);
				}
				
			

			}
		}
		else if (mode == 1) {
			
			//operator functions 
		
			//check if there is a num already
			if (hasnum && !(hassign)) {
			
					if (type.equals("string")) {
						if (tokenLookAhead.equals("+")) {
							if (isnotfuncparam) {
								this.hasnum = false;
								this.hassign = true;
							}
							else {
								numexpr = numexpr + 1;
								sepnum = expb.size() - 1;
								expb.get(sepnum).setHasnum(false);
								expb.get(sepnum).setHassign(true);
								
								
								expb.get(sepnum).setNumexpr(numexpr);
							}
						}
						else {
							notthrown = false;
							newcount++;
							System.out.println(type + " should not be followed by " + tokenLookAhead);
						}
					
					}
					else {
						
						if (isnotfuncparam) {
							this.hasnum = false;
							this.hassign = true;
						}
						else {
							numexpr = numexpr + 1;
							sepnum = expb.size() - 1;
							expb.get(sepnum).setHasnum(false);
							expb.get(sepnum).setHassign(true);
							
							
							expb.get(sepnum).setNumexpr(numexpr);
						}
					}
					
					
				
			} else {
				if (!(type.equals("string"))) {
					
					//if after (
					
					if (hasopenparen) {
						notthrown = false;
						newcount++;
						System.out.println("Operator should not be after operator " + tokenLookAhead);
					}
					//if after )
					else if (hascloseparen) {
						
						
						if (isnotfuncparam) {
							this.hascloseparen = false;
						}
						else {
							numexpr = numexpr + 1;
							sepnum = expb.size() - 1;
							expb.get(sepnum).setHascloseparen(false);
							
							
							
							expb.get(sepnum).setNumexpr(numexpr);
						}
						
					}
					else {
						// exception expected number or true expression
						notthrown = false;
						newcount++;
						System.out.println("Excess sign " + tokenLookAhead);
					}
				}
				else {
					notthrown = false;
					newcount++;
					System.out.println(type + " operator should not be after operator " + tokenLookAhead);
				}
				
				

			}
		}
		else if (mode == 2) {
			//after ( function 
			System.out.println("Open paren check " + tokenLookAhead);
			if (!(type.equals("string"))) {
				if (hasnum && !(hassign)) {
					if (numexpr == 0) {
						//if it is at the very start
						
						if (isnotfuncparam) {
							this.hasnum = false;
							this.hassign = true;
							this.hasopenparen = true;
						}
						else {
							numexpr = numexpr + 1;
							sepnum = expb.size() - 1;
							expb.get(sepnum).setHasnum(false);
							expb.get(sepnum).setHassign(true);
							expb.get(sepnum).setHasopenparen(true);
							
							expb.get(sepnum).setNumexpr(numexpr);
						}
					}
					else {
						//it should not be followed by a num
						notthrown = false; newcount++;
						System.out.println("Open paren cannot be followed after a num " + tokenLookAhead);
					}
				}
				else if (hassign && !(hasnum)) {
					//if after sign or ( and not )
					if (hascloseparen) {
						
						//open parenthesis should not be followed by )
						notthrown = false; newcount++;
						System.out.println("Open paren cannot be followed after close paren " + tokenLookAhead);
					}
					else {
						//if it is not followed by a )
						
						if (isnotfuncparam) {
							this.hasnum = false;
							this.hassign = true;
							this.hasopenparen = true;
						}
						else {
							numexpr = numexpr + 1;
							sepnum = expb.size() - 1;
							expb.get(sepnum).setHasnum(false);
							expb.get(sepnum).setHassign(true);
							
							expb.get(sepnum).setHasopenparen(true);
							expb.get(sepnum).setNumexpr(numexpr);
						}
					}
				}
				else {

					// exception expected number or true expression
					notthrown = false;
					newcount++;
					System.out.println("Anonymous after (");

				}
			}
			else {
				notthrown = false;
				newcount++;
				System.out.println(tokenLookAhead + " should not be on " + type + " expression");
			}
			
		}
		else if (mode == 3) {
			// during ) function
			System.out.println("During )" + tokenLookAhead);
			if (!(type.equals("string"))) {
				if (hasnum && !(hassign)) {
					//after a num
					
					if (isnotfuncparam) {
						this.hasnum = false;
						this.hassign = true;
						this.hascloseparen = true;
					}
					else {
						numexpr = numexpr + 1;
						sepnum = expb.size() - 1;
						expb.get(sepnum).setHasnum(false);
						expb.get(sepnum).setHassign(true);
						expb.get(sepnum).setHascloseparen(true);
						
						expb.get(sepnum).setNumexpr(numexpr);
					}
				}
				else if (!(hasnum) && hassign) {
					//if after (
					System.out.println("Symbol after close paren " + hascloseparen + " open " + hasopenparen);
					if (hasopenparen) {
						//cannot be after (
						notthrown = false;
						newcount++;
						System.out.println("Close paren cannot be after ( " + tokenLookAhead);
					}
					//if after )
					else if (hascloseparen) {
						if (isnotfuncparam) {
							this.hasnum = false;
							this.hassign = true;
							this.hascloseparen = true;
						}
						else {
							numexpr = numexpr + 1;
							sepnum = expb.size() - 1;
							expb.get(sepnum).setHasnum(false);
							expb.get(sepnum).setHassign(true);
							expb.get(sepnum).setHascloseparen(true);
							
							expb.get(sepnum).setNumexpr(numexpr);
						}
					}
					else {
						//cannot be after operator
						notthrown = false;
						newcount++;
						System.out.println("Close parenthesis cannot be after operator " + tokenLookAhead);
					}
				}
				else {

					// exception expected number or true expression
					notthrown = false;
					newcount++;
					System.out.println("Anonymous )");

				}
			}
			else {
				notthrown = false;
				newcount++;
				System.out.println(tokenLookAhead + " should not be on " + type + " expression");
			}
			
		}
		
	}
	void confirm_valuetype(boolean isarray, String tokenLookAhead, String typeLookAhead) {
		//validates if the data type are the same
		int expnum = this.explister.size() - 1;
		String datassign = explister.get(expnum).getExpectedtype();
		String exprorder = "exporder ";
		System.out.println("CONFIRMING token " + tokenLookAhead + " type " + typeLookAhead + " data " + datassign);
		if (notthrown) {
			//if it is integer
			if (datassign.equals("integer")) {
				//if it is a list
				if (isarray) {
					if (this.isarraytype) {
						
						
					}
					else {
						//error data type array mismatch
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch var is an array " + tokenLookAhead);
					}
				}
				//if it is not a list
				else {
					System.out.println("Tokshu " + tokenLookAhead);
					if (this.isarraytype) {
						//error data type array mismatch variable is not an array
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch variable is not an array " + tokenLookAhead);
					}
					else {
						if (typeLookAhead.equals("IDENTIFIER")) {
							
							//search for type
							System.out.println("IDENTIFIER for integer " + tokenLookAhead);
							int indnum = 0;
							boolean identifloc = false;
							this.search_identif(tokenLookAhead);
							indnum = this.identifindsearch;
							identifloc = this.identiffound;
							
							if (identifloc) {
								int lam = this.fm.size() - 1;
								String dattype = this.fm.get(lam).getVl().get(indnum).getVartype();
								if (dattype.equals("integer")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										int val = this.fm.get(lam).getVl().get(indnum).getIntval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatching identifier " + tokenLookAhead);
								}
							}
							else {
								String dattype = this.varGlobal.get(indnum).getVartype();
								if (dattype.equals("integer")) {
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										int val = this.varGlobal.get(indnum).getIntval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatching identifier " + tokenLookAhead);
								}
							}
							
						}
						else if (typeLookAhead.equals("INTEGER")) {
							System.out.println("Confirmed to be an integer " + tokenLookAhead);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
						}
						else if (tokenLookAhead.equals("+") || tokenLookAhead.equals("-") || 
								tokenLookAhead.equals("*") || tokenLookAhead.equals("/") || 
								tokenLookAhead.equals("(") || tokenLookAhead.equals(")")) {
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
						}
						else if (tokenLookAhead.equals(">") || tokenLookAhead.equals("<>") || tokenLookAhead.equals("<")
								|| tokenLookAhead.equals("=") || tokenLookAhead.equals(">=")
								|| tokenLookAhead.equals("<=") || tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
							//no comparison operators
							notthrown = false;
							newcount++;
							System.out.println("No comparison for integer " + tokenLookAhead);
						}
						else {
							//data type mismatch
							notthrown = false;
							newcount++;
							System.out.println("Data type mismatch " + tokenLookAhead);
						}
					}
				}
			}
			//if it is a real
			else if (datassign.equals("real")) {
				//if it is a list
				if (isarray) {
					if (this.isarraytype) {
						
						
					}
					else {
						//error data type array mismatch
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch var is an array " + tokenLookAhead);
					}
				}
				//if it is not a list
				else {
					if (this.isarraytype) {
						//error data type array mismatch variable is not an array
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch variable is not an array " + tokenLookAhead);
					}
					else {
						if (typeLookAhead.equals("IDENTIFIER")) {
							//search for type
							System.out.println("SHOUAN " + tokenLookAhead);
							int indnum = 0;
							boolean identifloc = false;
							this.search_identif(tokenLookAhead);
							indnum = this.identifindsearch;
							identifloc = this.identiffound;
							
							if (identifloc) {
								int lam = this.fm.size() - 1;
								String dattype = this.fm.get(lam).getVl().get(indnum).getVartype();
								if (dattype.equals("integer")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										int val = this.fm.get(lam).getVl().get(indnum).getIntval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer " + tokenLookAhead);
									}
								}
								else if (dattype.equals("real")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										float val = this.fm.get(lam).getVl().get(indnum).getFloatval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatching identifier " + tokenLookAhead);
								}
							}
							else {
								System.out.println("GLOBAL WORLD " + tokenLookAhead);
								String dattype = this.varGlobal.get(indnum).getVartype();
								if (dattype.equals("integer")) {
									
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									
									if (valhas) {
										
										int explistn = explister.size() - 1;
										int val = this.varGlobal.get(indnum).getIntval();
										
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										
										 explister.get(explistn).setExpr(newexpres);
									}
									
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer for real " + tokenLookAhead);
									}
								}
								else if (dattype.equals("real")) {
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										float val = this.varGlobal.get(indnum).getFloatval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									
									else {
										//no value of float
										notthrown = false;
										newcount++;
										System.out.println("No value of real " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatching identifier");
								}
							}
						}
						else if (typeLookAhead.equals("INTEGER")) {
							System.out.println("Confirmed to be an integer " + tokenLookAhead);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
						}
						else if (typeLookAhead.equals("REAL")) {
							System.out.println("Confirmed to be a real " + tokenLookAhead);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
						}
						else if (tokenLookAhead.equals("+") || tokenLookAhead.equals("-") || 
								tokenLookAhead.equals("*") || tokenLookAhead.equals("/") || 
								tokenLookAhead.equals("(") || tokenLookAhead.equals(")")) {
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
						}
						else if (tokenLookAhead.equals(">") || tokenLookAhead.equals("<>") || tokenLookAhead.equals("<")
								|| tokenLookAhead.equals("=") || tokenLookAhead.equals(">=")
								|| tokenLookAhead.equals("<=") || tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
							//no comparison operators
							notthrown = false;
							newcount++;
							System.out.println("No comparison for real " + tokenLookAhead);
						}
						else {
							//data type mismatch
							notthrown = false;
							newcount++;
							System.out.println("Data type mismatch " + tokenLookAhead);
						}
					}
				}
			}
			//if it is a boolean
			else if (datassign.equals("boolean")) {
				//if it is a list
				if (isarray) {
					if (this.isarraytype) {
						
						
					}
					else {
						//error data type array mismatch
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch var is an array " + tokenLookAhead);
					}
				}
				//if it is not a list
				else {
					if (this.isarraytype) {
						//error data type array mismatch variable is not an array
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch variable is not an array " + tokenLookAhead);
					}
					else {
						if (typeLookAhead.equals("IDENTIFIER")) {
							//search for type
							int indnum = 0;
							boolean identifloc = false;
							this.search_identif(tokenLookAhead);
							indnum = this.identifindsearch;
							identifloc = this.identiffound;
							
							if (identifloc) {
								int lam = this.fm.size() - 1;
								String dattype = this.fm.get(lam).getVl().get(indnum).getVartype();
								if (dattype.equals("integer")) {
									
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										int val = this.fm.get(lam).getVl().get(indnum).getIntval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer " + tokenLookAhead);
									}
								}
								else if (dattype.equals("real")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										float val = this.fm.get(lam).getVl().get(indnum).getFloatval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of real " + tokenLookAhead);
									}
								}
								else if (dattype.equals("boolean")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										boolean val = this.fm.get(lam).getVl().get(indnum).isBoolval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of boolean " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatched Identifer");
								}
							}
							else {
								System.out.println("GLOBAL WORLD " + tokenLookAhead);
								String dattype = this.varGlobal.get(indnum).getVartype();
								if (dattype.equals("integer")) {
									
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									
									if (valhas) {
										
										int explistn = explister.size() - 1;
										int val = this.varGlobal.get(indnum).getIntval();
										
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										
										 explister.get(explistn).setExpr(newexpres);
									}
									
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of integer for real " + tokenLookAhead);
									}
								}
								else if (dattype.equals("real")) {
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										float val = this.varGlobal.get(indnum).getFloatval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
										 explister.get(explistn).setCanbereal(true);
									}
									
									else {
										//no value of float
										notthrown = false;
										newcount++;
										System.out.println("No value of real " + tokenLookAhead);
									}
								}
								else if (dattype.equals("boolean")) {
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										boolean val = this.varGlobal.get(indnum).isBoolval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val + " ";
										 explister.get(explistn).setExpr(newexpres);
										 explister.get(explistn).setIsbool(true);
									}
									
									else {
										//no value of float
										notthrown = false;
										newcount++;
										System.out.println("No value of boolean " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatched Identifer");
								}
							}
						}
						else if (typeLookAhead.equals("INTEGER")) {
							System.out.println("Confirmed to be an integer " + tokenLookAhead);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
						}
						else if (typeLookAhead.equals("REAL")) {
							System.out.println("Confirmed to be a real " + tokenLookAhead);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
							 explister.get(explistn).setCanbereal(true);
						}
						else if (tokenLookAhead.equals("+") || tokenLookAhead.equals("-") || 
								tokenLookAhead.equals("*") || tokenLookAhead.equals("/") || 
								tokenLookAhead.equals("(") || tokenLookAhead.equals(")")) {
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
							 if (tokenLookAhead.equals("/")) {
								 explister.get(explistn).setCanbereal(true);
							 }
						}
						else if (tokenLookAhead.equals(">") || tokenLookAhead.equals("<")
								 || tokenLookAhead.equals(">=")
								|| tokenLookAhead.equals("<=") ) {
							//no comparison operators
							int explistn = this.explister.size() - 1;
							explister.get(explistn).setIsbool(true);
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							explister.get(explistn).setExpr(newexpres);
							explister.get(explistn).setIsbool(true);
						}
						else if (tokenLookAhead.equals("<>")) {
							int explistn = this.explister.size() - 1;
							explister.get(explistn).setIsbool(true);
							String expresscurr = explister.get(explistn).getExpr();
							String newsign = "!=";
							String newexpres = expresscurr + newsign + " ";
							explister.get(explistn).setExpr(newexpres);
							explister.get(explistn).setIsbool(true);
							
						}
						else if (tokenLookAhead.equals("=")) {
							int explistn = this.explister.size() - 1;
							explister.get(explistn).setIsbool(true);
							String expresscurr = explister.get(explistn).getExpr();
							String newsign = "==";
							String newexpres = expresscurr + newsign + " ";
							explister.get(explistn).setExpr(newexpres);
							explister.get(explistn).setIsbool(true);
						}
						else if (tokenLookAhead.equals("true") || tokenLookAhead.equals("false")) {
							System.out.println("Confirmed to be an boolean " + tokenLookAhead);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + tokenLookAhead + " ";
							 explister.get(explistn).setExpr(newexpres);
							 explister.get(explistn).setIsbool(true);
						}
						else if (tokenLookAhead.equals("and:")) {
							int explistn = this.explister.size() - 1;
							explister.get(explistn).setIsbool(true);
							String expresscurr = explister.get(explistn).getExpr();
							String newsign = "&&";
							String newexpres = expresscurr + newsign + " ";
							explister.get(explistn).setExpr(newexpres);
							explister.get(explistn).setIsbool(true);
						}
						else if (tokenLookAhead.equals("or:")) {
							int explistn = this.explister.size() - 1;
							explister.get(explistn).setIsbool(true);
							String expresscurr = explister.get(explistn).getExpr();
							String newsign = "||";
							String newexpres = expresscurr + newsign + " ";
							explister.get(explistn).setExpr(newexpres);
							explister.get(explistn).setIsbool(true);
						}
						else if (tokenLookAhead.equals("not:")) {
							int explistn = this.explister.size() - 1;
							explister.get(explistn).setIsbool(true);
							String expresscurr = explister.get(explistn).getExpr();
							String newsign = "not";
							String newexpres = expresscurr + newsign + " ";
							explister.get(explistn).setExpr(newexpres);
							explister.get(explistn).setIsbool(true);
						}
						else {
							//data type mismatch
							notthrown = false;
							newcount++;
							System.out.println("Data type mismatch " + tokenLookAhead);
						}
					}
				}
			}
			
			//if it is a string
			else if (datassign.equals("string")) {
				System.out.println("ZAAP STRING" + tokenLookAhead + " " + typeLookAhead);
				if (isarray) {
					if (this.isarraytype) {
						
						
					}
					else {
						//error data type array mismatch
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch var is an array " + tokenLookAhead);
					}
				}
				//if it is not a list
				else {
					if (this.isarraytype) {
						//error data type array mismatch variable is not an array
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch variable is not an array " + tokenLookAhead);
					}
					else {
						if (typeLookAhead.equals("IDENTIFIER")) { 
							//search for type
							System.out.println("Searching for string " + tokenLookAhead);
							int indnum = 0;
							boolean identifloc = false;
							this.search_identif(tokenLookAhead);
							indnum = this.identifindsearch;
							identifloc = this.identiffound;
							
							if (identifloc) {
								int lam = this.fm.size() - 1;
								String dattype = this.fm.get(lam).getVl().get(indnum).getVartype();
								if (dattype.equals("string")) {
									
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										String val = this.fm.get(lam).getVl().get(indnum).getStringval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val;
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of string " + tokenLookAhead);
									}
								}
								else if (dattype.equals("char")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										String val = this.fm.get(lam).getVl().get(indnum).getCharval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val;
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of string " + tokenLookAhead);
									}
								}
								else {
									notthrown = false;
									newcount++;
									System.out.println("Mismatched data type " + tokenLookAhead);
								}
							}
							else {
								String dattype = this.varGlobal.get(indnum).getVartype();
								if (dattype.equals("string")) {
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										String val = this.varGlobal.get(indnum).getStringval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val;
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of string " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatching identifier " + tokenLookAhead);
								}
							}
						}
						else if (typeLookAhead.equals("STRING")) {
							int len = tokenLookAhead.length();
							String toknam = this.tokenLookAhead.substring(1, len - 1);
							System.out.println("Confirmed to be a string " + tokenLookAhead + " sus " + len);
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							String newexpres = expresscurr + toknam;
							 explister.get(explistn).setExpr(newexpres);
						}
						
						else if (tokenLookAhead.equals("+")) {
							int explistn = this.explister.size() - 1;
							String expresscurr = explister.get(explistn).getExpr();
							
							 
						}
						else if (tokenLookAhead.equals("-") || 
								tokenLookAhead.equals("*") || tokenLookAhead.equals("/") || 
								tokenLookAhead.equals("(") || tokenLookAhead.equals(")")) {
							//no comparison operators
							notthrown = false;
							newcount++;
							System.out.println("Invalid operators for string " + tokenLookAhead);
						}
						else if (tokenLookAhead.equals(">") || tokenLookAhead.equals("<>") || tokenLookAhead.equals("<")
								|| tokenLookAhead.equals("=") || tokenLookAhead.equals(">=")
								|| tokenLookAhead.equals("<=") || tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
							//no comparison operators
							notthrown = false;
							newcount++;
							System.out.println("Invalid operators for string "  + tokenLookAhead);
						}
						else {
							//data type mismatch
							notthrown = false;
							newcount++;
							System.out.println("Cannot be declared in string function " + tokenLookAhead);
						}
					}
				}
				
			}
			else if (datassign.equals("char")) {
				if (isarray) {
					if (this.isarraytype) {
						
					}
					else {
						//error data type array mismatch
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch var is an array " + tokenLookAhead);
					}
				}
				//if it is not a list
				else {
					if (this.isarraytype) {
						//error data type array mismatch variable is not an array
						notthrown = false;
						newcount++;
						System.out.println("error data type array mismatch variable is not an array " + tokenLookAhead);
					}
					else {
						if (typeLookAhead.equals("IDENTIFIER")) {
							//search for type
							System.out.println("IDENTIFIER for char " + tokenLookAhead);
							int indnum = 0;
							boolean identifloc = false;
							this.search_identif(tokenLookAhead);
							indnum = this.identifindsearch;
							identifloc = this.identiffound;
							
							if (identifloc) {
								int lam = this.fm.size() - 1;
								String dattype = this.fm.get(lam).getVl().get(indnum).getVartype();
								if (dattype.equals("string")) {
									
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										String val = this.fm.get(lam).getVl().get(indnum).getStringval();
										String expresscurr = explister.get(explistn).getExpr();
										int len = val.length();
										if (len == 0 || len == 1) {
											String newexpres = expresscurr + val;
											 explister.get(explistn).setExpr(newexpres);
										}
										else {
											notthrown = false;
											newcount++;
											System.out.println("Character should only be 1 character");
										}
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of string " + tokenLookAhead);
									}
								}
								else if (dattype.equals("char")) {
									boolean valhas =this.fm.get(lam).getVl().get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										String val = this.fm.get(lam).getVl().get(indnum).getCharval();
										String expresscurr = explister.get(explistn).getExpr();
										String newexpres = expresscurr + val;
										 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of string " + tokenLookAhead);
									}
								}
								else {
									notthrown = false;
									newcount++;
									System.out.println("Mismatched data type " + tokenLookAhead);
								}
							}
							else {
								String dattype = this.varGlobal.get(indnum).getVartype();
								if (dattype.equals("string")) {
									boolean valhas = this.varGlobal.get(indnum).isHasval();
									if (valhas) {
										int explistn = explister.size() - 1;
										String val = varGlobal.get(indnum).getStringval();
										String expresscurr = explister.get(explistn).getExpr();
										int len = val.length();
										if (len == 0 || len == 1) {
											String newexpres = expresscurr + val;
											 explister.get(explistn).setExpr(newexpres);
										}
										else {
											notthrown = false;
											newcount++;
											System.out.println("Character should only be 1 character");
										}
										
									}
									else if (dattype.equals("char")) {
										boolean valhas2 = this.varGlobal.get(indnum).isHasval();
										if (valhas2) {
											int explistn = explister.size() - 1;
											String val = this.varGlobal.get(indnum).getCharval();
											String expresscurr = explister.get(explistn).getExpr();
											String newexpres = expresscurr + val;
											 explister.get(explistn).setExpr(newexpres);
									}
									else {
										//no value of integer
										notthrown = false;
										newcount++;
										System.out.println("No value of c " + tokenLookAhead);
									}
								}
								else {
									//mismatching identifier
									notthrown = false;
									newcount++;
									System.out.println("Mismatching identifier " + tokenLookAhead);
								}
							}
							
						}
						}
						else if (typeLookAhead.equals("STRING")) {
							System.out.println("Confirmed to be a string " + tokenLookAhead + "zap");
							int len = tokenLookAhead.length();
							
							if (len <= 3 && len >= 2) {
								String toknam = this.tokenLookAhead.substring(1, len - 1);
								int explistn = this.explister.size() - 1;
								String expresscurr = explister.get(explistn).getExpr();
								String newexpres = expresscurr + toknam;
								 explister.get(explistn).setExpr(newexpres);
							}
							else {
								notthrown = false;
								newcount++;
								System.out.println("Char should have only 1 character " + datassign + " " + len);
							}
							
						}
						
						else if (tokenLookAhead.equals("+")) {
							notthrown = false;
							newcount++;
							System.out.println("Char should not have any operators " + tokenLookAhead);
						}
						else if (tokenLookAhead.equals("-") || 
								tokenLookAhead.equals("*") || tokenLookAhead.equals("/") || 
								tokenLookAhead.equals("(") || tokenLookAhead.equals(")")) {
							//no comparison operators
							notthrown = false;
							newcount++;
							System.out.println("Invalid operators for char " + tokenLookAhead);
						}
						else if (tokenLookAhead.equals(">") || tokenLookAhead.equals("<>") || tokenLookAhead.equals("<")
								|| tokenLookAhead.equals("=") || tokenLookAhead.equals(">=")
								|| tokenLookAhead.equals("<=") || tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
							//no comparison operators
							notthrown = false;
							newcount++;
							System.out.println("Invalid operators for char "  + tokenLookAhead);
						}
						else {
							//data type mismatch
							notthrown = false;
							newcount++;
							System.out.println("Cannot be declared in char function " + tokenLookAhead);
						}
					}
				}
			}
			
			
		}
	}
	void new_express_assign() {
		exprcall++;
		this.exprorder_real = this.exprorder + " " + this.exprcall;
		explist ex = new explist(this.searchedtype, "");
		this.explister.add(ex);
	}
	void shunting_yard(String token, String type, boolean islocal, int index) {
		System.out.println("HANGYAKU");
		try
		{
			int num = this.explister.size() - 1;
			
		String exp = this.explister.get(num).getExpr();
		System.out.println("JUN " + num);
		System.out.println("here is the exp you ordered " + exp + " type " + type + " islocal " + islocal + " ind " + index);
		BigDecimal result = null;
    	Expression expression = new Expression(exp);
    	result = expression.setPrecision(33).eval();
    	System.out.println("Shunting result " + result);
    	if (type.equals("integer")) {
    		int val = result.intValue();
    		if (islocal) {
    			int num2 = this.fm.size() - 1;
    			this.fm.get(num2).getVl().get(index).setHasval(true);
    			this.fm.get(num2).getVl().get(index).setIntval(val);
    			System.out.println("GASOLINA " + this.fm.get(num2).getVl().get(index).getIntval());
    		}
    		else {
    			this.varGlobal.get(index).setHasval(true);
    			this.varGlobal.get(index).setIntval(val);
    		}
    	}
    	else if (type.equals("real")) {
    		float val = result.floatValue();
    		if (islocal) {
    			
    		}
    		else {
    			
    			this.varGlobal.get(index).setHasval(true);
    			this.varGlobal.get(index).setFloatval(val);
    		}
    	}
    	else if (type.equals("boolean")) {
    		int val = result.intValue();
    		boolean booln = false;
    		if (val == 1) {
    			booln = true;
    		}
    		if (islocal) {
    			
    		}
    		else {
    			this.varGlobal.get(index).setHasval(true);
    			this.varGlobal.get(index).setBoolval(booln);
    		}
    		
    	}
    	else if (type.equals("string")) {
    		
    	}
    	else if (type.equals("char")) {
    		
    	}
	}catch (ExpressionException f) {
		newcount++;
		notthrown = false;
		System.out.println("Invalid boolean expression");
	}
    	
	}
	void compareoper(String op) {
		
	}
	void removeexprarray() {
		int num = this.explister.size() - 1;
		
		this.explister.remove(num);
	}
	void print_explister() {
		//printing of expression
		for (int i = 0; i < this.explister.size(); i++) {
			String exp = explister.get(i).getExpr();
			System.out.println("Expression is " + exp);
		}
	}
	
	void clearlocvariable() {
		
	}
	void printvarglob()
	{
		System.out.println("variables and their values");
		for (int i = 0; i < varGlobal.size(); i++) {
			String varname = varGlobal.get(i).getVariablename();
			String vartype = varGlobal.get(i).getVartype();
			boolean hasval = varGlobal.get(i).isHasval();
			String varval = "";
			if (hasval) {
				if (vartype.equals("integer")) {
					varval = varval + varGlobal.get(i).getIntval();
				} else if (vartype.equals("real")) {
					varval = varval + varGlobal.get(i).getFloatval();
				} else if (vartype.equals("string")) {
					varval = varval + varGlobal.get(i).getStringval();
				} else if (vartype.equals("char")) {
					varval = varval + varGlobal.get(i).getCharval();
				}
				else if (vartype.equals("boolean")) {
					varval = varval + varGlobal.get(i).isBoolval();
				}
				System.out.println("var " + varname + " type " + vartype + " value " + varval);
			}
			
			
		}
		
	}
	void check_string() {
		
	}
	void string_insert(String token, String type, boolean islocal, int index) {
		int num = this.explister.size() - 1;
		String exp = this.explister.get(num).getExpr();
		
		if (type.equals("string")) {
    		this.varGlobal.get(index).setHasval(true);
			this.varGlobal.get(index).setStringval(exp);
    	}
    	else if (type.equals("char")) {
    		this.varGlobal.get(index).setHasval(true);
			this.varGlobal.get(index).setCharval(exp);
    	}
	}
	void check_char() {
		
	}
	void search_identif(String identif) {
		int ind = 0;
		String datatype = "";
		boolean isonlocal = false;
		boolean hasnolocal = true, hasfound = false, dontstop = true;
		boolean isitarray = false;
		String varnaming = "", vartyping = "";
		int sizeloc = this.fm.size() - 1;
		
		//search for function variables
		if (sizeloc >= 0){
			int sizeloc2 = this.fm.get(sizeloc).getVl().size();
			System.out.println("SEARCHIMAS");
			for (int i = 0; i < sizeloc2; i++) {
			varnaming = this.fm.get(sizeloc).getVl().get(i).getVariablename();
			vartyping = this.fm.get(sizeloc).getVl().get(i).getVartype();
			isitarray = this.fm.get(sizeloc).getVl().get(i).isIslist();
			if (identif.equals(varnaming)) {
				System.out.println("IMAS FOUND " + identif);
				hasnolocal = false;
				ind = i;
				hasfound = true;
				isonlocal = true;
				this.set_AssignData(vartyping, isitarray);
				break;
			}
		}
	}
		//search for global
		if (hasnolocal && dontstop) {
			for (int i = 0; i < this.varGlobal.size(); i++) {
				int num = i + 1;
				varnaming = varGlobal.get(i).getVariablename();
				vartyping = varGlobal.get(i).getVartype();
				isitarray = varGlobal.get(i).isIslist();
				datatype = varGlobal.get(i).getVartype();
				if (identif.equals(varnaming)) {
					System.out.println("Var " + varnaming + " identif " + identif + " i " + i);
					hasfound = true;
					ind = i;
					this.set_AssignData(vartyping, isitarray);
					break;
				}
			}
		}
		if (hasfound) {
			this.identifindsearch = ind;
			this.identiffound = isonlocal;
		}
		else {
			
			//error: not found variable exception
			notthrown = false;
			newcount++;
			System.out.println("Not found variable");
		}
	}
	boolean shunt_yard3(String exp) {
		boolean res = false;
		try
		{
			int num = this.explister.size() - 1;
			System.out.println("JUN " + num);
		

		BigDecimal result = null;
    	Expression expression = new Expression(exp);
    	result = expression.setPrecision(33).eval();
    	boolean op1 = false, op2 = false, op3 = false;
    	op1 = this.explister.get(num).isIsbool();
    	op2 = this.explister.get(num).isCanbereal();
    	System.out.println("OPS " + op1 + " OPS2 " + op2 + " resz " + exp);
    	if (op1) {
    		int val = result.intValue();
    		boolean booln = false;
    		if (val == 1) {
    			
    			booln = true;
    		}
    		System.out.println("zapcall" + booln + " expr " +  exp);
    		res = booln;
    		
    		
    	}
    	else {
    		newcount++;
    		notthrown = false;
    		System.out.println("Condition is not boolean");
    	}
    	
    	
    	System.out.println("Shunting result " + result);
    	
	}catch (ExpressionException f) {
		newcount++;
		notthrown = false;
		System.out.println("Invalid boolean expression");
	}
		return res;
	}
	String shunt_yard2(String start) {
		String res = "";
		System.out.println("SHUN 2.0");
		try
		{
			int num = this.explister.size() - 1;
			System.out.println("JUN " + num);
		String exp = this.explister.get(num).getExpr();
		System.out.println("JUN " + num);

		BigDecimal result = null;
    	Expression expression = new Expression(exp);
    	result = expression.setPrecision(33).eval();
    	boolean op1 = false, op2 = false, op3 = false;
    	op1 = this.explister.get(num).isIsbool();
    	op2 = this.explister.get(num).isCanbereal();
    	System.out.println("OPS " + op1 + " OPS2 " + op2 + " resz " + exp);
    	if (op1) {
    		int val = result.intValue();
    		boolean booln = false;
    		if (val == 1) {
    			
    			booln = true;
    		}
    		System.out.println("zapcall" + booln);
    		res = start + "" +  booln;
    		
    		
    	}
    	else if (op2) {
    		float val = result.floatValue();
    		System.out.println("zapcall" + val + "hun");
    		res = start + "" +  val;
    	}
    	else {
    		int val = result.intValue();
    		res = start + "" +  val;
    	}
    	
    	System.out.println("Shunting result " + result);
    	
	}catch (ExpressionException f) {
		newcount++;
		notthrown = false;
		System.out.println("Invalid boolean expression");
	}
		return res;
	}
	String insert_string_2(String start) {
		String res = "";
		int num = this.explister.size() - 1;
		String exp = this.explister.get(num).getExpr();
		
		res = start + exp;
		return res;
	}
	void backupfunc() {
		this.tokenbackup.add(tokenLookAhead);
		this.typebackup.add(typeLookAhead);
	}
	void clearbackupfunc() {
		this.tokenbackup.clear();
		this.typebackup.clear();
	}
	void return_tokens(ArrayList<String> tokname, ArrayList<String> typename) {

		for(int i = tokname.size()-1; i >= 0; i--){
            tokenStack.push(tokname.get(i));
            tokenTypeStack.push(typename.get(i));
        }
      
	}
	void searchFunc(String funcname) {
		boolean hasfound = false;
		int ind = 0;
		for (int i = 0; i < this.functioncall.size(); i++) {
			String funcname2 = functioncall.get(i).getFuncname();
			if (funcname2.equals(funcname)) {
				hasfound = true;
				ind = i;
				break;
			}
		}
		if (hasfound) {
			this.funcind = ind;
			
		}
		else {
			notthrown = false;
			newcount++;
			System.out.println("Function name not found " + functioncall.size());
		}
	}
	void removefunccall() {
		int num = fm.size() - 1;
		fm.remove(num);
	}
	void newfunccall(String funcname, int index) {
		this.funcno2 = funcno2 + 1;
		this.funcinst = funcinst + " " + funcno2;
		
		functionmove fm3 = new functionmove();
		fm3.setVl(this.functioncall.get(index).getVarLocal());
		fm3.setFuncinst(funcinst);
		fm3.setTokenList(this.functioncall.get(index).getTokenList());
		fm3.setTypeList(this.functioncall.get(index).getTypeList());
		fm3.setFunctype(this.functioncall.get(index).getFunctype());
		this.fm.add(fm3);
	}
	void countparam(int index) {
		int sizer = this.functioncall.get(index).getVarLocal().size();
		boolean isparam = false;
		System.out.println("COUNTING PARAMETERS " + sizer);
		for (int i = 0; i < sizer; i++) {
			int num = i + 1;
			
			isparam = this.functioncall.get(index).getVarLocal().get(i).isIsnotparam();
			isparam = !(isparam);
			System.out.println(" aga " + isparam);
			if (isparam) {
				this.paramnoexpect = paramnoexpect + 1;
			}
			
		}
	}
	void evalfunc(String funcname, int index)
	{
		int rind = 0;
		int expnum = this.explister.size() - 1;
		int samp = this.fm.size() - 1;
		String datassign = explister.get(expnum).getExpectedtype();
		String funcsign = this.fm.get(samp).getFunctype();
		int varsize = this.fm.get(samp).getVl().size();
		for (int i = 0; i < varsize; i++) {
			String sus = this.fm.get(samp).getVl().get(i).getVariablename();
			if (sus.equals(funcname)) {
				rind = i;
				break;
			}
		}
		if (datassign.equals("integer")) {
			System.out.println("FUNC " + funcsign);
			if (funcsign.equals("integer")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					int val = this.fm.get(samp).getVl().get(rind).getIntval();
					String exp = this.explister.get(expnum).getExpr() + " ";
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function integer " + funcname);
				}
			}
			else {
				newcount++;
				notthrown = false;
				System.out.println("Mismatched assignment integer " + funcname);
			}
		}
		else if (datassign.equals("real")) {
			if (funcsign.equals("integer")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					int val = this.fm.get(samp).getVl().get(rind).getIntval();
					String exp = this.explister.get(rind).getExpr() + " ";
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function real " + funcname);
				}
			}
			if (funcsign.equals("real")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					float val = this.fm.get(samp).getVl().get(rind).getFloatval();
					String exp = this.explister.get(expnum).getExpr() + " ";
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function real " + funcname);
				}
			}
			else {
				newcount++;
				notthrown = false;
				System.out.println("Mismatched assignment real " + funcname);
			}
		}
		else if (datassign.equals("boolean")) {
			if (funcsign.equals("integer")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					int val = this.fm.get(samp).getVl().get(rind).getIntval();
					String exp = this.explister.get(expnum).getExpr() + " ";
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function real " + funcname);
				}
			}
			if (funcsign.equals("real")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					float val = this.fm.get(samp).getVl().get(rind).getFloatval();
					String exp = this.explister.get(expnum).getExpr() + " ";
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function real " + funcname);
				}
			}
			if (funcsign.equals("boolean")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					boolean val = this.fm.get(samp).getVl().get(rind).isBoolval();
					String exp = this.explister.get(expnum).getExpr() + " ";
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function real " + funcname);
				}
			}
			else {
				newcount++;
				notthrown = false;
				System.out.println("Mismatched assignment boolean ");
			}
		}
		else if (datassign.equals("string")) {
			if (funcsign.equals("string")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					String val = this.fm.get(samp).getVl().get(rind).getStringval();
					String exp = this.explister.get(expnum).getExpr() + val;
					this.explister.get(expnum).setExpr(exp);
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function String " + funcname);
				}
			}
			else {
				newcount++;
				notthrown = false;
				System.out.println("Mismatched assignment string");
			}
		}
		else if (datassign.equals("char")) {
			if (funcsign.equals("string")) {
				boolean hasval = this.fm.get(samp).getVl().get(rind).isHasval();
				if (hasval) {
					String val = this.fm.get(samp).getVl().get(rind).getStringval();
					int len = val.length();
					if (len == 0 || len == 1) {
						String exp = this.explister.get(expnum).getExpr() + val;
						this.explister.get(expnum).setExpr(exp);
					}
					else {
						newcount++;
						notthrown = false;
						System.out.println("Character should have one letter only " + funcname);
					}
					
				}
				else {
					newcount++;
					notthrown = false;
					System.out.println("No value assigned for function String " + funcname);
				}
			}
			else {
				newcount++;
				notthrown = false;
				System.out.println("Mismatched assignment char");
			}
		}
	}
	}
