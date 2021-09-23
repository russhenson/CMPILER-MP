import javax.swing.text.html.HTMLEditorKit.Parser;

import semanticanalyzer.semanticanalyzer;
import semanticanalyzer.*;
import java.io.*;
import java.util.*;

public class Parser2 {
	private boolean shak = false, sagemark = false;
	private int errornum = 0;
    private Stack<String> tokenStack, tokenStack2;
    private Stack<String> tokenTypeStack, tokenTypeStack2;
    private ErrorParser errparser;
    private String tokenLookAhead;
    private String typeLookAhead;
    private ArrayList<String> token_name, type_name, errorlistinterp;
    private int newcount = 0;
    private int statemode = 0;
    private boolean shouldpopsemi = true;
    private String indic = "";
    private boolean notthrown = true;
    private int instamod = 0;
    private boolean cangostruct = true;
    private ArrayList<String> tokenn, typee;
    
    public Parser2(ArrayList<String> tokens, ArrayList<String> tokenType, int counter) {
        tokenn = tokens;
        typee = tokenType;
    	this.tokenStack = new Stack<>();
        this.tokenTypeStack = new Stack<>();
        
        token_name = new ArrayList<String>();
        type_name = new ArrayList<String>();
        this.newcount = counter;
        errparser = new ErrorParser();
        for(int i = tokens.size()-1; i >= 0; i--){
            tokenStack.push(tokens.get(i));
            tokenTypeStack.push(tokenType.get(i));
        }
      
       
        tokenLookAhead = tokenStack.peek();
        typeLookAhead = tokenTypeStack.peek();
        
    }

    ArrayList<String> get_errparselist() {
    	return this.errparser.get_errparselist();
    }

    int getcounter() {
    	return newcount;
    }

    String tokenPopper() {
        return tokenStack.pop();
    }

    String tokenTypePopper() {
        return tokenTypeStack.pop();
    }

    void peeker() {
    	if(!tokenStack.empty()){ // proceed to peek at the next token
            tokenLookAhead = tokenStack.peek();
            typeLookAhead = tokenTypeStack.peek();
        }

    }
    boolean start () {
    	boolean isValid = false;
    	isValid = program();
    	return isValid;
    }
    // <program> ::= program *IDENTIFIER* ;
    boolean program() {
        boolean isValid = false,  hasfunc = false;
        
        // Check if the first token is "program"
        if(tokenLookAhead.equals("program")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();
            
            // Checks the program name if it's valid
            if(typeLookAhead.equals("IDENTIFIER")){ 
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                // Check if it ends with semi colon
                if(typeLookAhead.equals("SEMICOLON")){
                    System.out.println("Valid program heading"); // No error
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();
                    isValid = true;
                    if (tokenLookAhead.equals("var") || tokenLookAhead.equals("const")) {
                    	//if starts with var
                    	while ((tokenLookAhead.equals("var") || tokenLookAhead.equals("const")) && notthrown) {
                    		isValid = this.variableDeclaration();
                    	}
                    	
                    }
                    if (notthrown) {
                    	if (tokenLookAhead.equals("function")) {
                        	//if starts with function
                    		hasfunc = true;
                    		while (tokenLookAhead.equals("function") && notthrown) {
                    			isValid = this.functionDeclaration();
                    		}
                        	
                        }
                    }
                    
                    if (notthrown) {
                    	
                    	
                    	isValid = this.compoundStatement(0);
                    	System.out.println("Check the weather " + notthrown + " and " + tokenLookAhead + " size " + this.errparser.get_errparselist().size());
                    	
                    	int num = this.errparser.get_errparselist().size();
                    	if (num == 0) {
                    		
                    		semanticanalyzer sem4 = new semanticanalyzer(this.tokenn, this.typee, newcount);
                    		
                    		
                    	}
                    }

                }
                else {
                    System.out.println("Missing a semicolon"); // Missing a semicolon
                    // get the error message from error.txt
                    isValid = false;
                    notthrown = false;
                    newcount++;
                	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
                	//panicmode("IDENTIFIER", 2, 0);
                   
                }

            }
            else {
                System.out.println("Expected ID"); // Missing or invalid name
                // get the error message from error.txt
                newcount++;
                notthrown = false;
            	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
            	//panic
            	//panicmode("IDENTIFIER", 1, 0);
                
            }
       }
       // get the error message from error.txt
        else {
        	notthrown = false;
        	newcount++;
        	errparser.error_checker(8, "error.txt" , newcount, tokenLookAhead);
        	//panicmode("program", 0, 1);
        }

       return isValid;

    }
    void burstfunc() {
    	this.tokenPopper();
    	this.tokenTypePopper();
    	peeker();
    }
    void panicmode(String type, int mode, int submode) {
    	boolean cango = true, colongo = true, identgo = true, commago = true;
    	if (mode == 0) {
    		//tokens
    		while (cango) {
    			System.out.println("Token " + this.tokenLookAhead + " type " + this.typeLookAhead);
        		this.tokenPopper();
            	this.tokenTypePopper();
            	peeker();
            	if (this.tokenLookAhead.equals(type)) {
            		cango = false;
            		//System.out.println("Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
            		if (submode == 1) {
            			//program
            			program();
            		}
            		else if (submode == 2) {
            			//variable
            			variableDeclaration();
            		}
            	}
        	}
    		
    	}
    	else if (mode == 1) {
    		//types for program
    		while (cango) {
    			//System.out.println("Token " + this.tokenLookAhead + " type " +  this.typeLookAhead);
        		this.tokenPopper();
            	this.tokenTypePopper();
            	
            	peeker();
            	if (this.typeLookAhead.equals(type)) {
            		//System.out.println(type + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
            		while(colongo) {
            			this.tokenPopper();
                    	this.tokenTypePopper();
                    	peeker();
                    	if (this.typeLookAhead.equals("SEMICOLON")) {
                    		//System.out.println("SEMICOLON " + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
                    		colongo = false;
                    		cango = false;
                    	}
                    	else {
                    		newcount++;
                         	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
                    	}
            		}
            	}
            	//if it is still not an identifier
        	}
    	}
		else if (mode == 2) {
			// missing semicolon
			while (cango) {
				System.out.println("Token " + this.tokenLookAhead + " type " + this.typeLookAhead);
				this.tokenPopper();
				this.tokenTypePopper();
				
				peeker();
				if (this.typeLookAhead.equals(type)) {
					cango = false;
					System.out.println(type + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
					
				}
				else {
					newcount++;
                 	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
				}
			}
		}
		else if (mode == 3) {
			//WRONG IDENTIFIER
			
			while (cango) {
				System.out.println("Token " + this.tokenLookAhead + " type " +  this.typeLookAhead);
        		this.tokenPopper();
            	this.tokenTypePopper();
            	peeker();
            	// if it is an identifier
            	if (this.typeLookAhead.equals(type)) {
					cango = false;
					//System.out.println(type + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
					while (identgo) {
						burstfunc();
		            	
		            	//if it's a comma
						//System.out.println("COMMA " + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
		            	if (typeLookAhead.equals("COMMA")) {
		            		
		            		//if it's an identifier
		            		burstfunc();
		            		//System.out.println("CHECKER " + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
		            		if (typeLookAhead.equals("IDENTIFIER")) {
		            			//System.out.println("BAM");
		            		}
		            		else {
		            			boolean notidentif = true;
		            			while(notidentif) {
		            				burstfunc();
		            				if (typeLookAhead.equals("IDENTIFIER")) {
		            					notidentif = false;
				            		}
		            			}
		            		}
		            	}
		            	else if (typeLookAhead.equals("COLON")) {
		            		//colon days
		            		//System.out.println("COLON GO");
		            		// if it's a datatype 
		            		burstfunc();
		            		//System.out.println("COLON GO AZA " + typeLookAhead);
			            	//datatype checking
			            	if (typeLookAhead.equals("DATA_TYPE")) {
			            		//System.out.println("datatype GO");
			            		// if it's a semicolon
			            		burstfunc();
			            		//if it's a semicolon
			            		if (typeLookAhead.equals("SEMICOLON")) {
			            			System.out.println("smicolon GO");
			            			//if it's an identifier
			            			burstfunc();
			            			if (typeLookAhead.equals("IDENTIFIER")) {
			            				System.out.println("idnt");
			            			}
			            			else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
										identgo = false;
									}
			            			else {
			            				newcount++;
		                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
			            				boolean identifgo2 = true;
			            				while (identifgo2) {
			            					
			            					if (typeLookAhead.equals("IDENTIFIER")) {
			            						System.out.println("shashing");
					            				identifgo2 = false;
					            			}
			            					else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
												identgo = false;
												identifgo2 = false;
											}
			            					else {
			            						newcount++;
				                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
			            					}
			            				}
			            			}
			            		}
			            		//if its not a correct semicolon
			            		else {
			            			newcount++;
	                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
			            			boolean semicolzer = true;
			            			while (semicolzer) {
			            				burstfunc();
			            				if (typeLookAhead.equals("SEMICOLON")) {
			            					semicolzer = false;
			            					//System.out.println("smicolon GO");
					            			//if it's an identifier
					            			burstfunc();
					            			if (typeLookAhead.equals("IDENTIFIER")) {
					            				System.out.println("idnt");
					            			}
					            			else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
												identgo = false;
											}
					            			else {
					            				newcount++;
				                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
					            				boolean identifgo2 = true;
					            				while (identifgo2) {
					            					
					            					if (typeLookAhead.equals("IDENTIFIER")) {
					            						System.out.println("shashing");
							            				identifgo2 = false;
							            			}
					            					else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
														identgo = false;
														identifgo2 = false;
													}
					            					else {
					            						newcount++;
						                        		errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
					            					}
					            				}
					            			}
			            				}
			            				else {
			            					newcount++;
			                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
			            				}
			            			}
			            			
			            		}
				            	//up until here error
			            	}
			            	//if it is not a datatype
			            	else {
			            		newcount++;
                        		errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
                        		boolean datatypergo = true;
                        		while (datatypergo) {
                        			burstfunc();
                        			if (typeLookAhead.equals("DATA_TYPE")) {
                        				datatypergo = false;
                        				burstfunc();
                        				if (typeLookAhead.equals("SEMICOLON")) {
        			            			//System.out.println("smicolon GO");
        			            			//if it's an identifier
        			            			burstfunc();
        			            			if (typeLookAhead.equals("IDENTIFIER")) {
        			            				//System.out.println("idnt");
        			            			}
        			            			else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
        										identgo = false;
        									}
        			            			else {
        			            				newcount++;
        		                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
        			            				boolean identifgo2 = true;
        			            				while (identifgo2) {
        			            					burstfunc();
        			            					if (typeLookAhead.equals("IDENTIFIER")) {
        			            						System.out.println("shashing");
        					            				identifgo2 = false;
        					            			}
        			            					else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
        												identgo = false;
        												identifgo2 = false;
        											}
        			            					else {
        			            						newcount++;
        				                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
        			            					}
        			            				}
        			            			}
        			            		}
        			            		//if its not a correct semicolon
        			            		else {
        			            			newcount++;
        	                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
        			            			boolean semicolzer = true;
        			            			while (semicolzer) {
        			            				burstfunc();
        			            				if (typeLookAhead.equals("SEMICOLON")) {
        			            					semicolzer = false;
        			            					//System.out.println("smicolon GO");
        					            			//if it's an identifier
        					            			burstfunc();
        					            			if (typeLookAhead.equals("IDENTIFIER")) {
        					            				System.out.println("idnt");
        					            			}
        					            			else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
        												identgo = false;
        											}
        					            			else {
        					            				newcount++;
        				                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
        					            				boolean identifgo2 = true;
        					            				while (identifgo2) {
        					            					burstfunc();
        					            					if (typeLookAhead.equals("IDENTIFIER")) {
        					            						System.out.println("shashing");
        							            				identifgo2 = false;
        							            			}
        					            					else if (tokenLookAhead.equals("function") || tokenLookAhead.equals("begin")) {
        														identgo = false;
        														identifgo2 = false;
        													}
        					            					else {
        					            						newcount++;
        						                        		errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
        					            					}
        					            				}
        					            			}
        			            				}
        			            				else {
        			            					newcount++;
        			                        		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
        			            				}
        			            			}
        			            			
        			            		}
                        			}
                        			else {
                        				newcount++;
                                		errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
                        			}
                        		}
			            	}
		            	}
		            	else {
		            		//if it's not a comma
		            		newcount++;
                    		errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
                    		
		            	}
						
					}
					
				}
            	//if it is not an identifier
				else {
					newcount++;
                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
				}
            	
			}
		}
		else if (mode == 4) {
			//wrong comma
			while (cango) {
				
				burstfunc();
				System.out.println("Token " + tokenLookAhead + " TYPE " + typeLookAhead);
				//if it's a comma
				if (typeLookAhead.equals(type)) {
					
					commago = true;
					while (commago) {
						System.out.println("YUSEI " + typeLookAhead + " " + tokenLookAhead);
						burstfunc();
						//if identifier
						if (typeLookAhead.equals("IDENTIFIER")) {
							System.out.println("HASD");
							
							boolean commaidgo = true;
							while (commaidgo) {
								System.out.println("YUSEI2" + typeLookAhead + " " + tokenLookAhead);
								burstfunc();
								if (typeLookAhead.equals("COMMA")) {
									System.out.println("ASDF");
								
									//if it goes for comma
									commaidgo = false;
									commago = false;
									System.out.println("ZAP Token " + tokenLookAhead + " TYPE " + typeLookAhead);
								}
								else if (typeLookAhead.equals("COLON")) {
									System.out.println("Hwe");
									//if it goes for colon
									commaidgo = false;
									boolean commacolgo = true;
									
									while (commacolgo) {
										burstfunc();
										//if it is a datatype
										if (typeLookAhead.equals("DATA_TYPE")) {
											System.out.println("aqerhD");
											commacolgo = false;
											boolean commadata = true;
											while (commadata) {
												burstfunc();
												//if it is a semicolon
												if (typeLookAhead.equals("SEMICOLON")) {
													System.out.println("Cuz");
													commadata = false;
													boolean commasemi = true;
													while (commasemi) {
														System.out.println("eat " + typeLookAhead + " " + tokenLookAhead);
														burstfunc();
														//if it is another identifier
														if (typeLookAhead.equals("IDENTIFIER")) {
															System.out.println("when");
															commasemi = false;
															boolean anothercomaid = true;
															while (anothercomaid) {
																burstfunc();
																//if it is a comma 
																if (typeLookAhead.equals("COMMA")) {
																	anothercomaid = false;
																	commago = false;
																}
																//if it is not a comma
																else {
																	newcount++;
												                 	errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
																}
															}
														}
														//if it is a function or begin
														else if (tokenLookAhead.equals("begin") || tokenLookAhead.equals("function")) {
															System.out.println("HUASD");
															commasemi = false;
															commago = false;
															cango = false;
															
														}
														//if not
														else {
															newcount++;
										                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
														}
													}
												}
												//if it is not a semicolon
												else {
													newcount++;
								                 	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
												}
											}
										}
										//if it is not a datatype
										else {
											newcount++;
						                 	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
										}
									}
									
								}
								else {
									newcount++;
				                 	errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
								}
							}
						}
						else {
							newcount++;
		                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
						}
					}
				}
				else if (typeLookAhead.equals("COLON")) {
					System.out.println("ROAT");
					boolean smurf = true;
					while (smurf) {
						
						burstfunc();
						//if datatype
						if (typeLookAhead.equals("DATA_TYPE")) {
							System.out.println("DATA FOUND");
							smurf = false;
							boolean smurf2 = true;
							while (smurf2) {
								burstfunc();
								//if semicolon
								if (typeLookAhead.equals("SEMICOLON")) {
									System.out.println("CHEKC");
									smurf2 = false;
									boolean smurf3 = true;
									while (smurf3) {
										burstfunc();
										//if identifier
										if (typeLookAhead.equals("IDENTIFIER")) {
											smurf3 = false;
										}
										//if begin or function
										else if (tokenLookAhead.equals("begin") || tokenLookAhead.equals("function")) {
											
											smurf3 = false;
											cango = false;
										}
										//if neither
										else {
											;
											newcount++;
						                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
										}
									}
									
								}
								//if not
								else {
									newcount++;
				                 	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
								}
							}
						}
						//if not
						else {
							newcount++;
		                 	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
						}
					}
					
				}
				else {
					newcount++;
                 	errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
				}
				//if it's not a comma
			}
		}
		else if (mode == 5) {
			//wrong datatype
			boolean datawrong = true;
			boolean dasn = false;
			while (datawrong) {
				System.out.println("PIZZA");
				this.burstfunc();
				//if it is a semicolon
				if (typeLookAhead.equals(type)) {
					boolean datatysemi = true;
					while (datatysemi) {
						burstfunc();
						System.out.println("WHACK");
						//if semicolon
						if(typeLookAhead.equals("SEMICOLON")) {
							
							datatysemi = false;
							while (cango) {
								burstfunc();
								System.out.println("MOLE");
								//System.out.println("TOKEN " + tokenLookAhead + " TYPE " + typeLookAhead);
								if (tokenLookAhead.equals("begin") || tokenLookAhead.equals("function")) {
									cango = false;
									dasn = true;
									System.out.println("HELLFIRE");
									
								}
								else if(typeLookAhead.equals("IDENTIFIER")) {
									
									cango = false;
									
								}
								//if neither of these
								else {
									newcount++;
				                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
								}
							}
						}
						//if not
						else {
							System.out.println("TOKEN " + tokenLookAhead  + " semis");
							newcount++;
							errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
						}
					}
					if (dasn) {
						
						datawrong = false;
					}
					else {
						cango = true;
					}
					System.out.println("DATA " + datawrong);	
					while (cango) {
						burstfunc();
						System.out.println("TOKEN " + tokenLookAhead + " TYPE " + typeLookAhead);
						//if it is a comma
						if (tokenLookAhead.equals("COMMA")) {
							
							boolean dataiden = true;
							while (dataiden) {
								//if it is an identifier
								burstfunc();
								if (typeLookAhead.equals("IDENTIFIER")) {
									dataiden = false;
									
								}
								//if it is not
								else {
									newcount++;
				                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
								}
							}
							
						}
						//if it is a colon
						else if (typeLookAhead.equals("COLON")) {
							boolean datacomms = true;
							while (datacomms) {
								burstfunc();
								//if datatype
								if (typeLookAhead.equals("DATA_TYPE")) {
									datacomms = false;
									boolean datatypeself = true;
									while (datatypeself) {
										burstfunc();
										//if it is a semicolon
										if (typeLookAhead.equals("SEMICOLON")) {
											boolean datasemis = true;
											while (datasemis) {
												burstfunc();
												//if it is an identifier
												if (typeLookAhead.equals("IDENTIFIER")) {
													datasemis = false;
												}
												else if (tokenLookAhead.equals("begin") || tokenLookAhead.equals("function")) {
													datasemis = false;
													cango = false;
												}
												//if it is not
												else {
													newcount++;
								                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
												}
											}
										}
										//if it is not
										else {
											newcount++;
						                 	errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
										}
									}
								}
								//if not
								else {
									newcount++;
				                 	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
								}
							}
							
						}
						//if it is not
						else {
							newcount++;
		                 	errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
						}
					}
					
					//up until here
				}
				//if it is not a datatype
				else {
					newcount++;
                 	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
				}
			}
		}
		else if (mode == 6) {
			boolean semicanf = true, dasemi = true;
			//if it is a semicolon
			while (semicanf) {
				//if it is a semicolon
				System.out.println(type);
				burstfunc();
				System.out.println(typeLookAhead);
				if (typeLookAhead.equals(type)) {
					System.out.println("JUMP");
					boolean semiident = true;
					while (semiident) {
						burstfunc();
						System.out.println("MOLE");
						//System.out.println("TOKEN " + tokenLookAhead + " TYPE " + typeLookAhead);
						if (tokenLookAhead.equals("begin") || tokenLookAhead.equals("function")) {
							cango = false;
							dasemi = true;
							semiident = false;
							semicanf = false;
							System.out.println("HELLFIRE");
							
						}
						else if(typeLookAhead.equals("IDENTIFIER")) {
							semiident = false;
							cango = false;
							semicanf = false;
							
						}
						//if neither of these
						else {
							newcount++;
		                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
						}
						
					}
				}
				//if it is not
				else {
					newcount++;
                 	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
				}
				
			}
			if (dasemi) {
				cango = false;
				semicanf = false;
			}
			else {
				cango = true;
			}
			while (cango) {
				burstfunc();
				System.out.println("TOKEN " + tokenLookAhead + " TYPE " + typeLookAhead);
				//if it is a comma
				if (tokenLookAhead.equals("COMMA")) {
					
					boolean dataiden2 = true;
					while (dataiden2) {
						//if it is an identifier
						burstfunc();
						if (typeLookAhead.equals("IDENTIFIER")) {
							dataiden2 = false;
							
						}
						//if it is not
						else {
							newcount++;
		                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
						}
					}
					
				}
				//if it is a colon
				else if (typeLookAhead.equals("COLON")) {
					boolean datacomms2 = true;
					while (datacomms2) {
						burstfunc();
						//if datatype
						if (typeLookAhead.equals("DATA_TYPE")) {
							datacomms2 = false;
							boolean datatypeself2 = true;
							while (datatypeself2) {
								burstfunc();
								//if it is a semicolon
								if (typeLookAhead.equals("SEMICOLON")) {
									boolean datasemis2 = true;
									while (datasemis2) {
										burstfunc();
										//if it is an identifier
										if (typeLookAhead.equals("IDENTIFIER")) {
											datasemis2 = false;
										}
										else if (tokenLookAhead.equals("begin") || tokenLookAhead.equals("function")) {
											datasemis2 = false;
											cango = false;
										}
										//if it is not
										else {
											newcount++;
						                 	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
										}
									}
								}
								//if it is not
								else {
									newcount++;
				                 	errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
								}
							}
						}
						//if not
						else {
							newcount++;
		                 	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
						}
					}
					
				}
				//if it is not
				else {
					newcount++;
                 	errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
				}
				//up until here
			}
			
		}
		else if(mode == 7) {
			//if it is not a dot after begin
			System.out.println("UMAE DISCARD");
			newcount++;
			errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
			
		}
    	
    	
    }
    
    // Syntax: l-value := r-value ;
    boolean assignment(int mode){ 
        boolean isValid = true;
        
    		System.out.println("Has p " + tokenLookAhead);
    	
        
        System.out.println("Going " + tokenLookAhead);
        if (typeLookAhead.equals("COLON_EQUALS")) {
        	this.burstfunc();
        	isValid = this.expression(mode);
        	if (tokenLookAhead.equals("end")) {
        		System.out.println("BANG");
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
        
        return isValid;
        
    }
    boolean chaincomma() {
    	boolean isValid = false;
    	return isValid;
    }
    boolean chainIdentifier() {
    	boolean isValid = false;
    	
			
			
			if (tokenLookAhead.equals(",")) {
				while (tokenLookAhead.equals(",") && notthrown) {
					this.burstfunc();
					if (tokenLookAhead.equals("IDENTIFIER")) {
						this.burstfunc();
					} 
					else {
						//expected identifier
						notthrown = false;
			            newcount++;
			        	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
					}
					
				}
				//when not comma anymore
				if (notthrown) {
					if (tokenLookAhead.equals(":")) {
						this.burstfunc();
						if (typeLookAhead.equals("DATA_TYPE")) {
							this.burstfunc(); 
							if (tokenLookAhead.equals(";")) {
								this.burstfunc();
								System.out.println("Good line");
								isValid = true;
							}
							else {
								//expected semicolon
								notthrown = false;
					            newcount++;
					        	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
							}
						}
						else {
							//expected data type
							System.out.println("UMAE 4");
							notthrown = false;
							notthrown = false;
				            newcount++;
				        	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
						}
					}
					else {
						//expected colon
						notthrown = false;
						notthrown = false;
			            newcount++;
			        	errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
					}
				}
				/*if (notthrown) {
					if (tokenLookAhead.equals(":")) {
						
					}
					else {
						//expected colon
					}
				}*/
			}
			else {
				//expected comma
			}
			
			//see if identycheck is false if it showed an error
		
    	return isValid;
    }
    boolean variableDeclaration(){
    	boolean isGoing = true;
        boolean isValid = false;
        int pastnum = 0;
		boolean colonPopped = false;
		boolean startAgain = true;
		boolean hasarray = false;

        // Check if the first token is "var"
		//GO BACK ARRAY SHOULD ONLY ACCEPT INTEGERS
		if (tokenLookAhead.equals("var")) {
			this.burstfunc();
			//required identifier
			if (typeLookAhead.equals("IDENTIFIER")) {
				//keep looking for identifier
				
				while (typeLookAhead.equals("IDENTIFIER") && notthrown) {
					System.out.println("QUICK CHECK " + tokenLookAhead);
					this.burstfunc();
					//optional comma
					if (tokenLookAhead.equals(",")) {
						while (tokenLookAhead.equals(",") && notthrown) {
							this.burstfunc();
							if (typeLookAhead.equals("IDENTIFIER")) {
								this.burstfunc();
								
							}
							else {
								//expected identifier
								notthrown = false;
								newcount++;
					        	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
							}
						}
					}
					
					if (notthrown) {
						//required colon
						if (tokenLookAhead.equals(":")) {
							//required datatype
							this.burstfunc();
							if (tokenLookAhead.equals("array")) {
								hasarray = true;
								this.burstfunc();
								//if open brackets after array
								if (tokenLookAhead.equals("[")) {
									this.burstfunc();
									//if integer or identifier
									if (typeLookAhead.equals("INTEGER")) {
										if (tokenLookAhead.equals("1")) {
											
										}
										else {
											notthrown = false;
											newcount++;
											errparser.error_checker(46, "error.txt" , newcount, tokenLookAhead);
										}
										if (notthrown) {
										this.burstfunc();
										//if up until data type
										if (typeLookAhead.equals("UP_UNTIL")) {
											this.burstfunc();
											//if identifier or integer
											if (typeLookAhead.equals("INTEGER")) {
												int numf = Integer.parseInt(tokenLookAhead);
												if (numf >= 1) {
													
												}
												else {
													notthrown = false;
										            newcount++;
										        	errparser.error_checker(47, "error.txt" , newcount, tokenLookAhead);
												}
												if (notthrown){
												this.burstfunc();
												//expected close bracket
												if (tokenLookAhead.equals("]")) {
													this.burstfunc();
													if (tokenLookAhead.equals("of")) {
														this.burstfunc();
														
														
													}
													else {
														//expected of
														notthrown = false;
											            newcount++;
											        	errparser.error_checker(34, "error.txt" , newcount, tokenLookAhead);
													}
												}
												else {
													//expected close bracket
													notthrown = false;
										            newcount++;
										        	errparser.error_checker(36, "error.txt" , newcount, tokenLookAhead);
												}
											}
											}
											else {
												//expected integer
												notthrown = false;
									            newcount++;
									        	errparser.error_checker(37, "error.txt" , newcount, tokenLookAhead);
											}
										}
										else {
											//expected up_until
											notthrown = false;
								            newcount++;
								        	errparser.error_checker(38, "error.txt" , newcount, tokenLookAhead);
								        	
										}
									}
										
									}
									else {
										//expected integer
										notthrown = false;
							            newcount++;
							        	errparser.error_checker(37, "error.txt" , newcount, tokenLookAhead);
									}
								}
								else {
									//expected open bracket
									notthrown = false;
						            newcount++;
						        	errparser.error_checker(35, "error.txt" , newcount, tokenLookAhead);
								}
								
							}
							System.out.println("SHang");
							if (typeLookAhead.equals("DATA_TYPE") && !(tokenLookAhead.equals("array"))) {
								System.out.println("SHang2");
								this.burstfunc();
								hasarray = false;
								//required semicolon
								if (tokenLookAhead.equals(";")) {
									//revert to check if it is an identifier still
									
									this.burstfunc();
									
								}
								else {
									//expected semicolon
									notthrown = false;
						            newcount++;
						        	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
								}
							}
							else {
								//expected data type
								if (hasarray && tokenLookAhead.equals("array")) {
									System.out.println("UMAE 2");
									hasarray = false;
									//repeated array
									notthrown = false;
									notthrown = false;
						            newcount++;
						        	errparser.error_checker(39, "error.txt" , newcount, tokenLookAhead);
								}
								else {
									System.out.println("UMAE 3");
									notthrown = false;
									notthrown = false;
						            newcount++;
						        	errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
								}
								
					        	
							}
						}
						else {
							//expected colon
							notthrown = false;
				            newcount++;
				        	errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
						}
					}
					
				}
				
				
				
			}
			else {
				//expected identifier
				notthrown = false;
	            newcount++;
	        	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
			}
		}
		else if (tokenLookAhead.equals("const")) {
			this.burstfunc();
			if (typeLookAhead.equals("IDENTIFIER")) {
				while (typeLookAhead.equals("IDENTIFIER") && notthrown) {
					this.burstfunc();
					if (tokenLookAhead.equals("=")) {
						this.burstfunc();
						if (typeLookAhead.equals("INTEGER") || typeLookAhead.equals("REAL") || typeLookAhead.equals("STRING") || tokenLookAhead.equals("true") || tokenLookAhead.equals("false")) {
							this.burstfunc();
							if (tokenLookAhead.equals(";")) {
								this.burstfunc();
							}
							else {
								notthrown = false;
					            newcount++;
					        	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
							}
						}
						else {
							notthrown = false;
				            newcount++;
				        	errparser.error_checker(49, "error.txt" , newcount, tokenLookAhead);
						}
					}
					else {
						notthrown = false;
			            newcount++;
			        	errparser.error_checker(48, "error.txt" , newcount, tokenLookAhead);
					}
				}
				
			}
			else {
				notthrown = false;
	            newcount++;
	        	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
			}
		}
		else {
			//expected var
			
			notthrown = false;
            newcount++;
        	errparser.error_checker(13, "error.txt" , newcount, tokenLookAhead);
			
		}
        /*if(tokenLookAhead.equals("var")){
            tokenPopper();
            tokenTypePopper();
            peeker();
            
            while(startAgain) {
				while(isGoing){
					if(typeLookAhead.equals("IDENTIFIER")){
						tokenPopper();
						tokenTypePopper();
						peeker();

						if(typeLookAhead.equals("COMMA")){
							tokenPopper();
							tokenTypePopper();
							peeker();

							if(!typeLookAhead.equals("IDENTIFIER")){
								newcount++;
								errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
								isValid = false;
								isGoing = true;
							}
						}
						else if(typeLookAhead.equals("COLON")){
							tokenPopper();
							tokenTypePopper();
							peeker();
							isGoing = false;
							colonPopped = true;

						}
						else {
							// Error Handling
							isValid = false;

							if(typeLookAhead.equals("DATA_TYPE")){
								colonPopped = true;
								isGoing = false;

								// Missing a ":"
								newcount++;
								errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
							}
							else if(typeLookAhead.equals("IDENTIFIER")){
								// Missing a ","
								newcount++;
								errparser.error_checker(14, "error.txt" , newcount, tokenLookAhead);
								isGoing = true;
							}

						}
					}

					else if(typeLookAhead.equals("COMMA")){
						tokenPopper();
						tokenTypePopper();
						peeker();

						newcount++;
						errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
						isValid = false;
						isGoing = true;
					}
					else if(typeLookAhead.equals("COLON")){
						tokenPopper();
						tokenTypePopper();
						peeker();

						newcount++;
						errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
						isValid = false;
						isGoing = false;
						colonPopped = true;
					}
						
				}

				if(colonPopped && typeLookAhead.equals("DATA_TYPE")){
					tokenPopper();
					tokenTypePopper();
					peeker();

					if(typeLookAhead.equals("SEMICOLON")){
						tokenPopper();
						tokenTypePopper();
						peeker();

						if(typeLookAhead.equals("IDENTIFIER")){
							startAgain = true;
							isGoing = true;
						}
						else {
							startAgain = false;
							isValid = true;
							//System.out.println("Valid Variable Declaration");
						}
						
					}
					else if(typeLookAhead.equals("IDENTIFIER")){
						// no semicolon on previous line, but there's more declaration
						startAgain = true;
						isGoing = true;
						newcount++;
						errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
					}
					else {
						startAgain = false;
						newcount++;
						errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
					}

				}
				else if(colonPopped && !typeLookAhead.equals("DATA_TYPE")) {
					startAgain = false;
					newcount++;
					errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
				}
			}

        }*/
		// var not found
        
        return isValid;
    }
    
    void clearlists() {
    	token_name.clear();
    	type_name.clear();
    }
    
    boolean forStatement(){
        boolean isValid = false;
        // Check if the first token is "for"
        System.out.println("FOR STATEMENT");
        if (tokenLookAhead.equals("for")) {
        	//if assignment
        	this.burstfunc();
        	System.out.println("IS IT ASSIGN " + tokenLookAhead);
        	if (typeLookAhead.equals("IDENTIFIER")) {
        		this.burstfunc();
        		//start
            	isValid = this.assignment(1);
            	
            	if (notthrown) {
            	
            		System.out.println("IS IT TO " + tokenLookAhead);
            		if (tokenLookAhead.equals("to") || tokenLookAhead.equals("downto")) {
            			this.burstfunc();
            			isValid = this.expression(0);
            			if (notthrown) {
            				
            				if (tokenLookAhead.equals("do")) {
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
       

        

        return isValid;
    }

    // <ifStatement> ::= <ifThen> | <ifThenElse>
    boolean ifStatement(){
        boolean isValid = false;
        boolean cancont = true;
        //GO BACK TO HERE SOMETIME VERY IMPORTANT
        this.burstfunc();
        if (tokenLookAhead.equals("(")) {
        	this.burstfunc();
        	isValid = this.expression(0);
            if (notthrown) {
            	//if )
            	if (tokenLookAhead.equals(")")) {
            		this.burstfunc();
            		if (tokenLookAhead.equals("then")) {
            			
            			
                    	
                    	this.burstfunc();
                    	System.out.println("Go here " + tokenLookAhead);
                    	
                    	int num = 1;
                    	isValid = this.compoundStatement(2);
                    	System.out.println(tokenLookAhead + " jitters " + this.errparser.get_errparselist().size());
                    	if (notthrown) {
                    		if (tokenLookAhead.equals("else")) {
                        		while (tokenLookAhead.equals("else") && notthrown && cancont) {
                        			this.burstfunc();
                        			if (tokenLookAhead.equals("if")) {
                        				this.burstfunc();
                        				if (tokenLookAhead.equals("(")) {
                        					this.burstfunc();
                        		        	isValid = this.expression(0);
                        		        	if (notthrown) {
                        		        		if (tokenLookAhead.equals(")")) {
                        		        			this.burstfunc();
                        		        			if (tokenLookAhead.equals("then")) {
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
        
        
        //old code
        /*System.out.println("ifStatement function called. " + tokenLookAhead + " " + typeLookAhead);

        if(ifThen() || ifThenElse()){
            isValid = true;
        }*/

        return isValid;
    }

    void returntokens() {
    	//returning of tokens
    	for(int i = token_name.size()-1; i >= 0; i--){
    		this.tokenTypeStack.push(type_name.get(i));
    		this.tokenStack.push(token_name.get(i));
    	}
    	clearlists();
    }
    
    // <ifThen> ::= if <expression> then <compoundStatement>
    boolean ifThen() {
        boolean isValid = false;
       
        System.out.println("ifThen function called.");

        if(tokenLookAhead.equals("if")){
            token_name.add(this.tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
            	token_name.add(this.tokenLookAhead);
                type_name.add(this.typeLookAhead);
            	token_name.add(this.tokenLookAhead);
                type_name.add(this.typeLookAhead);
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(expression(0)){
                	
                    if(typeLookAhead.equals("CLOSE_PAREN")){
                    	token_name.add(this.tokenLookAhead);
                        type_name.add(this.typeLookAhead);
                    	token_name.add(this.tokenLookAhead);
                        type_name.add(this.typeLookAhead);
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                
                        if(tokenLookAhead.equals("then")){
                        	token_name.add(this.tokenLookAhead);
                            type_name.add(this.typeLookAhead);
                        	token_name.add(this.tokenLookAhead);
                            type_name.add(this.typeLookAhead);
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
                            //if(compoundStatement
                            if(compoundStatement(1) && !tokenLookAhead.equals("else")){
                            	token_name.add(this.tokenLookAhead);
                                type_name.add(this.typeLookAhead);
                                System.out.println("Valid if-then statement");
                                isValid = true;
                                clearlists();
                            }
                            else {
                            	//there is an else
                            	//function for returning of stack
                            	this.returntokens();
                            }
                        }
                        // Error: Missing a then
                        else {
                        	//dummy code change with whatever applicable
                        	System.out.println("UMAEKA if");
                        	newcount++;
    						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
    						this.panicmode("PERIOD", 10, 0); //dummy code
                        }
                    }
                    // Error: Missing a )
                    else {
                    	//dummy code change with whatever applicable
                    	System.out.println("UMAEKA if1");
                    	newcount++;
						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
						this.panicmode("PERIOD", 10, 0); //dummy code
                    }
                }
            }
            // Error: Missing a (
            else {
            	//dummy code change with whatever applicable
            	newcount++;
            	System.out.println("UMAEKA if2");
				errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
				this.panicmode("PERIOD", 10, 0); //dummy code
            }
        }
        // Error: Missing if
        else {
        	//dummy code change with whatever applicable
        	System.out.println("UMAEKA if3");
        	newcount++;
			errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
			this.panicmode("PERIOD", 10, 0); //dummy code
        }
        
        

        return isValid;
    }
    
    // <ifThenElse> ::= if <expression> then <compoundStatement> else <compoundStatement>
    boolean ifThenElse() {
        boolean isValid = false;

        System.out.println("ifThenElse function called.");

        if(tokenLookAhead.equals("if")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(expression(0)){

                    if(typeLookAhead.equals("CLOSE_PAREN")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                    

                        if(tokenLookAhead.equals("then")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(compoundStatement(1)){

                                if(tokenLookAhead.equals("else")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();

                                    if(compoundStatement(1)){
                                        isValid = true;
                                        System.out.println("Valid if-then-else statement");
                                    }
                                }
                                // Error: Missing an "else"
                                else {
                                	//dummy code change with whatever applicable
                                	newcount++;
            						errparser.error_checker(19, "error.txt" , newcount, tokenLookAhead);
                                }
                                
                            }
                        }
                        // Error: Missing a then
                        else {
                        	//dummy code change with whatever applicable
                        	newcount++;
    						errparser.error_checker(20, "error.txt" , newcount, tokenLookAhead);
    						//this.panicmode("PERIOD", 10, 0); //dummy code
                        }
                    }
                    // Error: Missing a )
                    else {
                    	//dummy code change with whatever applicable
                    	System.out.println("UMAEKA if5");
                    	newcount++;
						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
						//this.panicmode("PERIOD", 10, 0); //dummy code
                    }
                }
            }
            // Error: Missing a (
            else {
            	//dummy code change with whatever applicable
            	newcount++;
				errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
				//this.panicmode("PERIOD", 10, 0); //dummy code
            }
        }
        // Error: Missing if
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }
        
        return isValid;
    }

    // <expression> ::= <simpleExpression> | <relationalExpression>
    boolean expression(int mode) {
    	String beftoken, beftype;
        Boolean isValid = false;
        isValid = this.simpleExpression(mode);
        System.out.println("TENZ WAS HERE " + tokenLookAhead);
        if (this.tokenLookAhead.equals(">") || this.tokenLookAhead.equals("<>") || this.tokenLookAhead.equals("<")  || this.tokenLookAhead.equals("=")  || this.tokenLookAhead.equals(">=")  || this.tokenLookAhead.equals("<=") || tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
        	this.burstfunc();
        	isValid = this.simpleExpression(mode);
        	System.out.println("AND: SO " + tokenLookAhead);
        	
        }
        //old code
        /*System.out.println("expression function called.");

        if(simpleExpression() || relationalExpression()){
            isValid = true;
        }*/

        return isValid;
    }

    // <relationalExpression> ::= <simpleExpression> <relationalOperator> <simpleExpression>
    boolean relationalExpression(int mode) {
        Boolean isValid = false;
        System.out.println("RELATIONAL EXP CALLED " + tokenLookAhead);
        if (tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
        	
        	this.burstfunc();
        }
        isValid = this.factor(mode);
        /*while (tokenLookAhead.equals("and:") || tokenLookAhead.equals("or:")) {
        	this.burstfunc();
        	isValid = this.factor(mode);
        }*/
        //old code
        /*
        if(simpleExpression()){
            if(relationalOperator()){
                if(simpleExpression()){
                    isValid = true;
                }
            }
        }
*/
        return isValid;
    }

    // <simpleExpression> ::= <term> | <term> <addingOperator> <term>
    boolean simpleExpression(int mode) {
        boolean isValid = false;
        System.out.println("simpleExpression function called. " + tokenLookAhead);
       
        if (tokenLookAhead.equals("+") || tokenLookAhead.equals("-")) {
        	
        	this.burstfunc();
        }
        isValid = this.term(mode);
        while (tokenLookAhead.equals("+") || tokenLookAhead.equals("-")) {
        	this.burstfunc();
        	isValid = this.term(mode);
        }
		//old code
        /*
        if(term()){

            if(addingOperator()){

                if(term()){
                    isValid = true;
                }

            }
            else 
                isValid = true; // just the term is acceptable
            
        }*/

        return isValid;
    }

    // <term> ::= <factor> | <factor> <multiOperator> <factor>
    boolean term(int mode){
        boolean isValid = false;

        System.out.println("term function called. " + tokenLookAhead);
        isValid = this.factor(mode);

        while (tokenLookAhead.equals("*") || tokenLookAhead.equals("/")) {
        	this.burstfunc();
        	isValid = this.factor(mode);
        }
        //old code
/*
        if(factor()){

            if(multiOperator()){
            	
                if(factor()){
                    isValid = true;
                }
            }
            else 
                isValid = true;
        }
*/

        return isValid;
    }

    // <factor> ::= *IDENTIFIER* | *INTEGER* | <expressionParen>
    boolean factor(int mode) {
        boolean isValid = false;

        System.out.println("factor function called. " + tokenLookAhead);
        if (tokenLookAhead.equals("not:")) {
        	this.burstfunc();
        	if (typeLookAhead.equals("OPEN_PAREN")) {
        		burstfunc();
            	isValid = expression(mode);
            	if (notthrown) {
            		if (typeLookAhead.equals("CLOSE_PAREN")) {
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
        	this.burstfunc();
        	System.out.println("WHEEZE SOUND " + tokenLookAhead);
        }
        else if (typeLookAhead.equals("IDENTIFIER")) {
        	
        	this.burstfunc();
        	System.out.println("U TRY THIS " + tokenLookAhead);
        	//if array
        	if (tokenLookAhead.equals("[")) {
        		
        	
        		isValid =this.arrayDeclare();
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
        		
        		isValid = this.funcDeclare();
        		System.out.println("NOW WHAT " + tokenLookAhead);
        	}
        	else {
        		
        	}
        }
        else if (typeLookAhead.equals("OPEN_PAREN")) {
        	burstfunc();
        	isValid = expression(mode);
        	if (notthrown) {
        		if (typeLookAhead.equals("CLOSE_PAREN")) {
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
      

        return isValid;
    }

    // <expressionParen> ::= ( expression )
    boolean expressionParen() {
        boolean isValid = false;

        if(typeLookAhead.equals("OPEN_PAREN")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            if(expression(0)){
                if(typeLookAhead.equals("CLOSE_PAREN")){
                	token_name.add(tokenLookAhead);
                    type_name.add(this.typeLookAhead);
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    isValid = true;
                }
                // Error: Missing )
                
                else {
                	this.returntokens();
                
                    	//dummy code change with whatever applicable
                    	newcount++;
						errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
						//this.panicmode("PERIOD", 10, 0); //dummy code
                    
                }
            }
        }
        // Error: Missing a (
        else {
        	
        	this.returntokens();
        	
            	//dummy code change with whatever applicable
            	newcount++;
				errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
				//this.panicmode("PERIOD", 10, 0); //dummy code
            
        }

        return isValid;
    }

    // <addingOperator> ::= *PLUS* | *MINUS*
    boolean addingOperator(){
        boolean isValid = false;

        if(typeLookAhead.equals("PLUS") || typeLookAhead.equals("MINUS")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }
        // Error: Expected a + or -
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(24, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }

        return isValid;
    }

    // <multiOperator> ::= *MULTIPLY* | *DIVIDE*
    boolean multiOperator(){
        boolean isValid = false;

        if(typeLookAhead.equals("MULTIPLY") || typeLookAhead.equals("DIVIDE")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }
        // Error: Expected a * or /
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(25, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }
            
        return isValid;
    }

    // <relationalOperator> ::= *NOT_EQUAL* | *LESS_THAN* | *LESS_EQUAL* | *GREATER_EQUAL* | *GREATER_THAN*
    boolean relationalOperator() {
        boolean isValid = false;

        if( typeLookAhead.equals("NOT_EQUAL") || 
            typeLookAhead.equals("LESS_THAN") || 
            typeLookAhead.equals("LESS_EQUAL") ||
            typeLookAhead.equals("GREATER_THAN") ||
            typeLookAhead.equals("GREATER_EQUAL")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            isValid = true;
        }
        // Error: Invalid operator
        else {
        	//dummy code change with whatever applicable
        	notthrown = false;
        	newcount++;
			errparser.error_checker(26, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }

        return isValid;
    }

    // <statement> ::= <simpleStatement> | <structuredStatement>
    boolean statement(int mode) { 
    	System.out.println("ZAP " + tokenLookAhead);
        boolean isValid = false;
        if (mode == 2) {
        	if (tokenLookAhead.equals("if") || tokenLookAhead.equals("for") || tokenLookAhead.equals("while")) {
        		this.statemode = 1;
        		isValid = structuredStatement(statemode);
        	}
        	else {
        		isValid = simpleStatement();
        		System.out.println("CLEARED 1 simp " + this.errparser.get_errparselist().size());
        	}
        }
        else if (mode == 1) {
        	if (tokenLookAhead.equals("if") || tokenLookAhead.equals("for") || tokenLookAhead.equals("body")) {
        		this.statemode = 1;
        		structuredStatement(statemode);
        	}
        	else {
        		isValid = simpleStatement();
        		System.out.println("CLEARED simp " + this.errparser.get_errparselist().size());
        	}
        }
        
        //old code
        /*if(simpleStatement() | structuredStatement(mode))
            isValid = true;
        
        System.out.println("Gas " + isValid);*/
        return isValid;
		
    }
    boolean arrayDeclare() {
    	boolean isValid = false;
    	if (tokenLookAhead.equals("[")) {
    		//only checks for expression GET BACK SINCE THERE IS FUNCTIONS TOO
    		this.burstfunc();
    		isValid = this.expression(0);
    		System.out.println("arrays2 " + tokenLookAhead);
    		if (notthrown) {
    		
    			System.out.println("arraysa " + tokenLookAhead);
    			if (tokenLookAhead.equals("]")) {
    				isValid = true;
    				this.burstfunc(); 
    			}
    			else {
    				//expected ]
    				
    				notthrown = false;
    	        	newcount++;
    				errparser.error_checker(36, "error.txt" , newcount, tokenLookAhead);
    			}
    		}
    	}
    	else {
    		//expected [
    		notthrown = false;
        	newcount++;
			errparser.error_checker(35, "error.txt" , newcount, tokenLookAhead);
    	}
    	return isValid;
    }
    boolean funcDeclare() {
    	boolean isValid = false;
    	if (tokenLookAhead.equals("(")) {
    		
    		this.burstfunc();
    		
    		isValid = this.expression(0);
    		if (notthrown) {
    			System.out.println("WHAT IS UP CLOSING OUT " + tokenLookAhead);
    			if (tokenLookAhead.equals(",")) {
    				while (tokenLookAhead.equals(",") && notthrown) {
    					this.burstfunc();
    					isValid = this.expression(0);
    					
    				}
    			}
    			if (notthrown) {
    				System.out.println("IS SUPP CLOSING OUT " + tokenLookAhead + " size is " + this.errparser.get_errparselist().size());
    				if (tokenLookAhead.equals(")")) {
    					System.out.println("CLOSING OUT");
    					isValid = true;
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
    	return false;
    }
    // <simpleStatement> ::= <assignment> | <readStatement> | <writeStatement>
    boolean simpleStatement() {
        boolean isValid = false;
        System.out.println("Simple statement parser " + tokenLookAhead);
        if (typeLookAhead.equals("IDENTIFIER")) {
        	this.burstfunc();
        	if (tokenLookAhead.equals("[")) {
        		//array
        		System.out.println("ARRAY GOES " + tokenLookAhead);
        		isValid = this.arrayDeclare();
        		if (notthrown) {
        			isValid = this.assignment(1);
        		}
        	}
        	else if (tokenLookAhead.equals("(")) {
        		
        		isValid= this.funcDeclare();
        		System.out.println("CLEAR FUNC " + this.errparser.get_errparselist().size());
        	}
        	else {
        		System.out.println("BEFORE ASSIGN " + this.errparser.get_errparselist().size());
            	isValid = this.assignment(1);
            	System.out.println("NOW ASSIGN " + this.errparser.get_errparselist().size());
        	}
        	
        }
        else if (tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")) {
        	isValid = this.readStatement();
        }
        else if (tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")) {
        	isValid = this.writeStatement();
        }
        //old code
        /*if(assignment() | readStatement() | writeStatement())
            isValid = true;*/

        return isValid;
    }
    
    boolean simpleSubStatement() {
    	boolean isValid = false;
    	if (simpleStatement()) {
    		if (statement(1)) {
    			isValid = true;
    		}
    	}
    	
    	return isValid;
    }
    
    // <structuredStatement> ::= <compoundStatement> | <ifStatement> | <whileStatement> | forStatement
    boolean structuredStatement(int mode) {
        boolean isValid = false;
        if (tokenLookAhead.equals("begin")) {
        	isValid = this.compoundStatement(2);
        }
        else if (tokenLookAhead.equals("if")) {
        	isValid = this.ifStatement();
        }
        else if (tokenLookAhead.equals("for")) {
        	isValid = this.forStatement();
        }
        else if (tokenLookAhead.equals("while")) {
        	isValid = this.whileStatement();
        }
        /*if (!(this.cangostruct)) {
        	System.out.println("HA");
        	cangostruct = true;
        }
        else {
        	if(compoundStatement(mode) | ifStatement() /* | whileStatement() */ //| forStatement()  )
                //isValid = true;
        //}
        

        return isValid;
    }
    boolean whileStatement() {
    	boolean isValid = false;
    	
    	if (tokenLookAhead.equals("while")) {
    		this.burstfunc();
    		//expr
    		isValid = this.expression(0);
    		if (notthrown) {
    			if (tokenLookAhead.equals("do")) {
    				this.burstfunc();
    				isValid = this.compoundStatement(1);
    				if (notthrown) {
    					tokenStack.push(";");
                		this.tokenTypeStack.push("SEMICOLON");
                		peeker();
    				}
    			}
    			else {
    				//expected do
    				notthrown = false;
                	newcount++;
					errparser.error_checker(41, "error.txt" , newcount, tokenLookAhead);
    			}
    		}
    		
    	}
    	else {
    		//expected while
    		notthrown = false;
        	newcount++;
			errparser.error_checker(44, "error.txt" , newcount, tokenLookAhead);
    		
    	}
    	return false;
    }

    // <compoundStatement> ::= begin <statement> end
    boolean compoundStatement(int mode) {
        boolean isValid = false, canstate = false, shakdated = false, wowzer = false, popsemi = false;
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
            this.statemode = 2;
            System.out.println("HAsa " + mode);
            tokenPopper();
            tokenTypePopper();
            peeker();
            if (shak) {
            	System.out.println("Kun4 " + mode);
            }
            System.out.println("LETS SEE HERE begin " + this.errparser.get_errparselist().size());
            isValid = this.statement(statemode);
            System.out.println("LETS SEE HERE " + this.errparser.get_errparselist().size());
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
                System.out.println("IZA " + tokenLookAhead + " " + statemode + " size is " + this.errparser.get_errparselist().size() + " MODE " + mode + " shak " + shak);
                isValid = statement(statemode);
                System.out.println("FOR LOOP END LETS SEE " + tokenLookAhead);
            }
            if (notthrown) {
            if (tokenLookAhead.equals("end")) {
            	tokenPopper();
                tokenTypePopper();
                peeker();
                System.out.println("Endstering " + mode + " because " + this.errparser.get_errparselist().size());
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
                		System.out.println("AS IT SHOULD " + errparser.get_errparselist().size());
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
                else {
                	System.out.println("Last token is " + tokenLookAhead);
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

        return isValid;
    }

    // <readStatement> ::= read ( *IDENTIFIER* , *IDENTIFIER* ) | readln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean readStatement() {
        boolean isValid = false, hasparam = false;
        System.out.println("READ STATEMENT " + tokenLookAhead);
        
        if (tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")) {
			this.burstfunc();
			if (tokenLookAhead.equals("(")) {

				
				this.burstfunc();
				if (typeLookAhead.equals("IDENTIFIER")) {
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

        return isValid;
    }

    // <writeStatement> ::= write ( *IDENTIFIER* , *IDENTIFIER* ) | writeln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean writeStatement() {
        boolean isValid = false;
        System.out.println("INITIATING WRITE");
        if (tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")) {
        	this.burstfunc();
        	//if open parenthesis
        	if (tokenLookAhead.equals("(")) {
        		//if variables
        		this.burstfunc();
        		isValid = this.expression(0);
        		
        		if (notthrown) {
        			System.out.println("WRITE HAS SOMETHIN " + tokenLookAhead);
        			if (tokenLookAhead.equals(",")) {
        				System.out.println("WRITE HAS A COMMA");
        				while (tokenLookAhead.equals(",") && notthrown) {
        					this.burstfunc();
        					System.out.println("SRATHC " + tokenLookAhead);
        					isValid = this.expression(0);
        					System.out.println("CHECKERS " + tokenLookAhead);
        				}
        			}
        			if (notthrown) {
        				System.out.println("DOES WRITE HAVE ) " + tokenLookAhead);
        				//if )
        				if (tokenLookAhead.equals(")")) {
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
       
        return isValid;
    }
    
    boolean functionDeclaration(){
        boolean isValid = false;
        boolean isGoing = true;
        String prevToken = "";
        //required function
		if (tokenLookAhead.equals("function")) {
			this.burstfunc();
			//required identifier
			if (typeLookAhead.equals("IDENTIFIER")) {
				this.burstfunc();
				//required open parenthesis
				if (tokenLookAhead.equals("(")) {
					this.burstfunc();
					//optional variables
					if (typeLookAhead.equals("IDENTIFIER")) {
						//if went for optional variables go here
						while (typeLookAhead.equals("IDENTIFIER") && notthrown) {
							this.burstfunc();
							//required comma
							if (tokenLookAhead.equals(",")) {
								while (tokenLookAhead.equals(",") && notthrown) {
									this.burstfunc();
									if (typeLookAhead.equals("IDENTIFIER")) {
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
										this.burstfunc();
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
					if (tokenLookAhead.equals(")")) {
						this.burstfunc();
						//check if after close paren :
						if (tokenLookAhead.equals(":")) {
							this.burstfunc();
							if (typeLookAhead.equals("DATA_TYPE") || typeLookAhead.equals("VOID")) {
								this.burstfunc();
								if (tokenLookAhead.equals(";")) {
									isValid = true;
									this.burstfunc();
									if (tokenLookAhead.equals("var") || tokenLookAhead.equals("const")) {
										//if var
										while ((tokenLookAhead.equals("var") || tokenLookAhead.equals("const")) && notthrown) {
											isValid = this.variableDeclaration();
										}
										
									}
									if (notthrown) {
										this.compoundStatement(1);
									}
								}
								else {
									//expected semicolon
									notthrown = false;
						            newcount++;
						        	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
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

    void print_errors(){

        ArrayList<String> errorList = errparser.get_errparselist();

        for(int i = 0; i < errorList.size(); i++){
            System.out.println(errorList.get(i));
        }
    }

    

}
