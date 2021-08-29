import javax.swing.text.html.HTMLEditorKit.Parser;

import java.io.*;
import java.util.*;

public class Parser2 {

    private Stack<String> tokenStack;
    private Stack<String> tokenTypeStack;
    private ErrorParser errparser;
    private String tokenLookAhead;
    private String typeLookAhead;
    private ArrayList<String> token_name, type_name;
    private int newcount = 0;
    private int statemode = 0;
    private String indic = "";

    public Parser2(ArrayList<String> tokens, ArrayList<String> tokenType, int counter) {
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

    // <program> ::= program *IDENTIFIER* ;
    boolean program() {
        boolean isValid = false;
        
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

                }
                else {
                    System.out.println("Missing a semicolon"); // Missing a semicolon
                    // get the error message from error.txt
                    newcount++;
                	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
                	panicmode("IDENTIFIER", 2, 0);
                   
                }

            }
            else {
                System.out.println("Invalid ID"); // Missing or invalid name
                // get the error message from error.txt
                newcount++;
            	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
            	//panic
            	panicmode("IDENTIFIER", 1, 0);
                
            }
       }
       // get the error message from error.txt
        else {
        	newcount++;
        	errparser.error_checker(8, "error.txt" , newcount, tokenLookAhead);
        	panicmode("program", 0, 1);
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
			newcount++;
			errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
			
		}
    	
    	
    }
    
    // Syntax: l-value := r-value ;
    boolean assignment(){ 
        boolean isValid = true;
        System.out.println("HUng " + tokenLookAhead + " " + typeLookAhead);
         
        if(typeLookAhead.equals("IDENTIFIER")){ 
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("COLON_EQUALS")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                // Check the r-value
                if( typeLookAhead.equals("STRING") || 
                    typeLookAhead.equals("REAL") || 
                    typeLookAhead.equals("INTEGER")){ // supposed to be arithmetic

                    
                        tokenPopper();
                    tokenTypePopper();
                    peeker();

                    // Check if it ends with semi colon
                    if(typeLookAhead.equals("SEMICOLON")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                        System.out.println("Valid assignment"); // No error
                        isValid =  true;
                    }
                    else {
                        System.out.println("Missing a semicolon"); 
                        // Error: Missing a semicolon 
                        // get the error message from error.txt
                       
                        	//dummy code change with whatever applicable
                        	newcount++;
    						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
    						this.panicmode("PERIOD", 10, 0); //dummy code
                        
                        
                    }
                }
                else if (expression()) {
                	 tokenPopper();
                     tokenTypePopper();
                     peeker();

                     // Check if it ends with semi colon
                     if(typeLookAhead.equals("SEMICOLON")){
                         
                         tokenPopper();
                         tokenTypePopper();
                         peeker();
                         System.out.println("Valid assignment"); // No error
                         isValid =  true;
                     }
                     else {
                         System.out.println("Missing a semicolon"); 
                         // Error: Missing a semicolon 
                         // get the error message from error.txt
                         
                         	//dummy code change with whatever applicable
                         	newcount++;
     						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
     						this.panicmode("PERIOD", 10, 0); //dummy code
                         
                         
                     }
                }
                else {
                    System.out.println("Invalid assignment value"); 
                    // Error: Wrong assignment
                    // get the error message from error.txt
                  
                    	//dummy code change with whatever applicable
                    	newcount++;
						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
						this.panicmode("PERIOD", 10, 0); //dummy code
                    
                    
                }

            }
            else {
                System.out.println("Missing an assignment operator"); 
                // Error: No := operator
                // get the error message from error.txt
                
                	//dummy code change with whatever applicable
                	newcount++;
					errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
					this.panicmode("PERIOD", 10, 0); //dummy code
                
               
            }

        }
        // Error: Incorrect or Missing Identifier
        // geth the error message from error.txt
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
			this.panicmode("PERIOD", 10, 0); //dummy code
        }
        
        return isValid;
    }

    boolean variableDeclaration(){
    	boolean isGoing = false;
    	boolean iscorrect = false;

        boolean isValid = false;

        // Check if the first token is "var"
    	System.out.println("Checking " + this.tokenLookAhead);
        if(tokenLookAhead.equals("var")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();
            if (typeLookAhead.equals("IDENTIFIER")) {
            	while (typeLookAhead.equals("IDENTIFIER")) {
                	iscorrect = true;
                	isGoing = true;
                	
                    tokenPopper();
                    tokenTypePopper();
                	peeker();
                	System.out.println("Grun " + this.tokenLookAhead + " " + this.typeLookAhead);
                	while(isGoing) {
                		//if it is a comma
                		System.out.println("ZAP " + this.tokenLookAhead + " " + this.typeLookAhead);
                		if (typeLookAhead.equals("COMMA")) {
                			
                            tokenPopper();
                            tokenTypePopper();
                        	peeker();
                        	if (!(typeLookAhead.equals("IDENTIFIER"))) {
                        		iscorrect = false;
                        		newcount++;
                        		errparser.error_checker(11, "error.txt" , newcount, tokenLookAhead);
                        		return false;
                        	}
                        	else {
                        		
                                tokenPopper();
                                tokenTypePopper();
                            	peeker();
                        	}
                		}
                		//if it is a colon
                		else if (typeLookAhead.equals("COLON")) {
                			
                            tokenPopper();
                            tokenTypePopper();
                        	peeker();
                        	isGoing = false;
                		}
                		
                		else {
                			newcount++;
                			errparser.error_checker(10, "error.txt" , newcount, tokenLookAhead);
                			this.panicmode("COMMA", 4, 0);
                			return false;
                		}
                	}
                	//if it is a keyword
                	if (typeLookAhead.equals("DATA_TYPE")) {
                		
                        tokenPopper();
                        tokenTypePopper();
                		peeker();
                		// if it ends in a semicolon
                		if (typeLookAhead.equals("SEMICOLON")) {
                    		
                            tokenPopper();
                            tokenTypePopper();
                    		peeker();
                    		System.out.println("One line valid");
                    	}
                    	else {
                    		System.out.println("SEMI ERROR");
                    		newcount++;
                    		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
                    		
                    		this.panicmode("SEMICOLON", 6, 0);
                    	}
                	}
                	else {
                		System.out.println("ERROR ON DATA");
                		newcount++;
                		errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
                		this.panicmode("DATA_TYPE", 5, 0);
                	}
                	//if it ends with a semicolon
                	
                }
            }
            else {
            	errparser.error_checker(2, "error.txt" , newcount, tokenLookAhead);
            	this.panicmode("IDENTIFIER", 3, 0);
            }
            
            
            /*while(typeLookAhead.equals("SEMICOLON")){
                while(typeLookAhead.equals("COLON")){
                    if(typeLookAhead.equals("IDENTIFIER")){
                        tokenStack.pop();
                        tokenTypeStack.pop();
                        if(!tokenStack.empty()){ // proceed to peek at the next token
                            tokenLookAhead = tokenStack.peek();
                            typeLookAhead = tokenTypeStack.peek();
                        }

                        if(typeLookAhead.equals("COMMA")){
                            tokenStack.pop();
                            tokenTypeStack.pop();
                            if(!tokenStack.empty()){ // proceed to peek at the next token
                                tokenLookAhead = tokenStack.peek();
                                typeLookAhead = tokenTypeStack.peek();
                            }
                        }
                    }
                    // error for invalid identifier
                    // error if colon is not found
                }
                if(typeLookAhead.equals("DATA_TYPE")){
                    tokenStack.pop();
                    tokenTypeStack.pop();
                    if(!tokenStack.empty()){ // proceed to peek at the next token
                        tokenLookAhead = tokenStack.peek();
                        typeLookAhead = tokenTypeStack.peek();
                    }
                }

                // error if the next token is a colon,  it should be a semicolon after the data type
            }
*/
            System.out.println("Valid Variable Declaration");

            //return iscorrect;
            isValid = true;


        }
        else {
        	newcount++;
    		errparser.error_checker(13, "error.txt" , newcount, tokenLookAhead);
    		
        	this.panicmode("var", 0, 2);
        }

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
        if(tokenLookAhead.equals("for")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("IDENTIFIER")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(typeLookAhead.equals("COLON_EQUALS")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    if(typeLookAhead.equals("INTEGER")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(tokenLookAhead.equals("to")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(typeLookAhead.equals("INTEGER")){
                                
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(tokenLookAhead.equals("do")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();

                                    if(compoundStatement(1)){
                                        System.out.println("Valid for-loop");
                                        isValid = true;
                                        
                                    }
                                    // this function has error handling already no need for one here                          
                                }
                                // Error: Expected a "do"
                                else {
                                	//dummy code change with whatever applicable
                                	newcount++;
            						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
            						this.panicmode("PERIOD", 10, 0); //dummy code
                                }
                            }
                            // Error: Expected an Integer
                            else {
                            	//dummy code change with whatever applicable
                            	newcount++;
        						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
        						this.panicmode("PERIOD", 10, 0); //dummy code
                            }
                        }
                        // Error: Expected a "to"
                        else {
                        	//dummy code change with whatever applicable
                        	newcount++;
    						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
    						this.panicmode("PERIOD", 10, 0); //dummy code
                        }
                    }
                    // Error: Expected an Integer
                    else {
                    	//dummy code change with whatever applicable
                    	newcount++;
						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
						this.panicmode("PERIOD", 10, 0); //dummy code
                    }
                }
                // Error: No := operator
                else {
                	//dummy code change with whatever applicable
                	newcount++;
					errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
					this.panicmode("PERIOD", 10, 0); //dummy code
                }
            }
            // Error: Incorrect or Missing Identifier
            else {
            	//dummy code change with whatever applicable
            	newcount++;
				errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
				this.panicmode("PERIOD", 10, 0); //dummy code
            }
        }
        // Error: Expected a "for"
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
			this.panicmode("PERIOD", 10, 0); //dummy code
        }

        

        return isValid;
    }

    // <ifStatement> ::= <ifThen> | <ifThenElse>
    boolean ifStatement(){
        boolean isValid = false;

        System.out.println("ifStatement function called. " + tokenLookAhead + " " + typeLookAhead);

        if(ifThen() || ifThenElse()){
            isValid = true;
        }

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

                if(expression()){
                	
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
                        	newcount++;
    						errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
    						this.panicmode("PERIOD", 10, 0); //dummy code
                        }
                    }
                    // Error: Missing a )
                    else {
                    	//dummy code change with whatever applicable
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
				errparser.error_checker(16, "error.txt" , newcount, tokenLookAhead);
				this.panicmode("PERIOD", 10, 0); //dummy code
            }
        }
        // Error: Missing if
        else {
        	//dummy code change with whatever applicable
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

                if(expression()){

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
    boolean expression() {
        Boolean isValid = false;

        System.out.println("expression function called.");

        if(simpleExpression() || relationalExpression()){
            isValid = true;
        }

        return isValid;
    }

    // <relationalExpression> ::= <simpleExpression> <relationalOperator> <simpleExpression>
    boolean relationalExpression() {
        Boolean isValid = false;

        if(simpleExpression()){
            if(relationalOperator()){
                if(simpleExpression()){
                    isValid = true;
                }
            }
        }

        return isValid;
    }

    // <simpleExpression> ::= <term> | <term> <addingOperator> <term>
    boolean simpleExpression() {
        boolean isValid = false;

        System.out.println("simpleExpression function called.");

        if(term()){

            if(addingOperator()){

                if(term()){
                    isValid = true;
                }

            }
            else 
                isValid = true; // just the term is acceptable
            
        }

        return isValid;
    }

    // <term> ::= <factor> | <factor> <multiOperator> <factor>
    boolean term(){
        boolean isValid = false;

        System.out.println("term function called.");

        if(factor()){

            if(multiOperator()){
            	
                if(factor()){
                    isValid = true;
                }
            }
            else 
                isValid = true;
        }


        return isValid;
    }

    // <factor> ::= *IDENTIFIER* | *INTEGER* | <expressionParen>
    boolean factor() {
        boolean isValid = false;

        System.out.println("factor function called.");

        if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("INTEGER")){ 
            token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }
        else if (expressionParen()) {
        	tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
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
            if(expression()){
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
        	newcount++;
			errparser.error_checker(26, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }

        return isValid;
    }

    // <statement> ::= <simpleStatement> | <structuredStatement>
    boolean statement(int mode) { 
        boolean isValid = false;
        if(simpleStatement() | structuredStatement(mode))
            isValid = true;
            
        return isValid;
    }

    // <simpleStatement> ::= <assignment> | <readStatement> | <writeStatement>
    boolean simpleStatement() {
        boolean isValid = false;
        
        if(assignment() | readStatement() | writeStatement())
            isValid = true;

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

        if(compoundStatement(mode) | ifStatement() /* | whileStatement() */ | forStatement()  )
            isValid = true;

        return isValid;
    }

    // <compoundStatement> ::= begin <statement> end
    boolean compoundStatement(int mode) {
        boolean isValid = false, canstate = true;

        if(tokenLookAhead.equals("begin")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();
            
            if(statement(1)){
                if(tokenLookAhead.equals("end")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();
                    //if statement
                    if (mode == 1) {
                    	if(typeLookAhead.equals("SEMICOLON")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            isValid = true;
                            System.out.println("Valid compound statement");

                        }
                        // Error: Missing a semicolon
                    	else {
                    		
                            	//dummy code change with whatever applicable
                            	newcount++;
        						errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
        						//this.panicmode("PERIOD", 10, 0); //dummy code
                            
                    	}
                    }
					else if (mode == 0) {
						System.out.println("FADS");
						if (typeLookAhead.equals("PERIOD")) {
							System.out.println("ZAM2");
							

							isValid = true;
							System.out.println("Valid compound statement");

						}
						// Error: Missing a semicolon
						else {
	                    	//dummy code change with whatever applicable
	                    	newcount++;
							errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
							//this.panicmode("PERIOD", 10, 0); //dummy code
	                    }
					}
                    
                    
                }
                // Error: Expected end
                else {
                	//dummy code change with whatever applicable
                	newcount++;
					errparser.error_checker(17, "error.txt" , newcount, tokenLookAhead);
					//this.panicmode("PERIOD", 10, 0); //dummy code
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
        boolean isValid = false;
        System.out.println("READ STATEMENT");

        if(tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(typeLookAhead.equals("IDENTIFIER")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    if(typeLookAhead.equals("COMMA")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("IDENTIFIER")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(typeLookAhead.equals("CLOSE_PAREN")){
                                
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(typeLookAhead.equals("SEMICOLON")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();
            
                                    System.out.println("Valid read statement");
                                    isValid = true;
                                }
                                // Error: Missing a ;
                                else {
                                	//dummy code change with whatever applicable
                                	newcount++;
            						errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
            						//this.panicmode("PERIOD", 10, 0); //dummy code
                                }
        
                                
                            }
                            // Error: Missing )
                            else {
                            	//dummy code change with whatever applicable
                            	newcount++;
        						errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
        						//this.panicmode("PERIOD", 10, 0); //dummy code
                            }
                        }
                        // Error: Invalid Identifier
                        else {
                        	//dummy code change with whatever applicable
                        	newcount++;
    						errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
    						//this.panicmode("PERIOD", 10, 0); //dummy code
                        }

                    }
                    else if(typeLookAhead.equals("CLOSE_PAREN")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("SEMICOLON")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
    
                            System.out.println("Valid read statement");
                            isValid = true;
                        }
                        // Error: Missing a ;
                        else {
                        	//dummy code change with whatever applicable
                        	newcount++;
    						errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
    						//this.panicmode("PERIOD", 10, 0); //dummy code
                        }
                    }
                    // Error: Missing )
                    else {
                    	//dummy code change with whatever applicable
                    	newcount++;
						errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
						//this.panicmode("PERIOD", 10, 0); //dummy code
                    }
                }
                // Error: Invalid Identifier
                else {
                	//dummy code change with whatever applicable
                	newcount++;
					errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
					//this.panicmode("PERIOD", 10, 0); //dummy code
                }
            }
            // Error: Missing (
            else {
            	//dummy code change with whatever applicable
            	newcount++;
				errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
				//this.panicmode("PERIOD", 10, 0); //dummy code
            }


        }
        // Error: Expected read or readln
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(28, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }


        return isValid;
    }

    // <writeStatement> ::= write ( *IDENTIFIER* , *IDENTIFIER* ) | writeln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean writeStatement() {
        boolean isValid = false;

        if(tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("STRING")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    if(typeLookAhead.equals("COMMA")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("STRING")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(typeLookAhead.equals("CLOSE_PAREN")){
                                
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(typeLookAhead.equals("SEMICOLON")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();
            
                                    System.out.println("Valid write statement");
                                    isValid = true;
                                }
        
                                
                            }
                        }

                    }
                    else if(typeLookAhead.equals("CLOSE_PAREN")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("SEMICOLON")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
    
                            System.out.println("Valid write statement");
                            isValid = true;
                        }
                         // Error: Missing a ;
                        else {
                        	//dummy code change with whatever applicable
                        	newcount++;
    						errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
    						//this.panicmode("PERIOD", 10, 0); //dummy code
                        }
                    }
                    // Error: Missing )
                    else {
                    	//dummy code change with whatever applicable
                    	newcount++;
						errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
						//this.panicmode("PERIOD", 10, 0); //dummy code
                    }
                
                }
                // Error: Invalid Identifier or String
                else {
                	//dummy code change with whatever applicable
                	newcount++;
					errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
					//this.panicmode("PERIOD", 10, 0); //dummy code
                }

            }
            // Error: Missing (
            else {
            	//dummy code change with whatever applicable
            	newcount++;
				errparser.error_checker(23, "error.txt" , newcount, tokenLookAhead);
				//this.panicmode("PERIOD", 10, 0); //dummy code
            }

        }
        // Error: Expected write or writeln
        else {
        	//dummy code change with whatever applicable
        	newcount++;
			errparser.error_checker(29, "error.txt" , newcount, tokenLookAhead);
			//this.panicmode("PERIOD", 10, 0); //dummy code
        }

        return isValid;
    }

    boolean functionDeclaration(){
        boolean isValid = false;
        boolean isGoing = true;
        String prevToken = "";

        if(tokenLookAhead.equals("function")){
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("IDENTIFIER")){
                tokenPopper();
                tokenTypePopper();
                peeker();
                //variables in parenthesis
                if(typeLookAhead.equals("OPEN_PAREN")){
                    prevToken = "OPEN_PAREN";
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    while(isGoing){
                        if((tokenLookAhead.equals("var") || tokenLookAhead.equals("const"))
                        && (prevToken.equals("OPEN_PAREN") || (prevToken.equals("SEMICOLON")))){
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
                        }
                        if(typeLookAhead.equals("IDENTIFIER") ){
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
                                    isGoing = false;
                                }
                            }
                            else if(typeLookAhead.equals("COLON")){
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(typeLookAhead.equals("DATA_TYPE")){
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();

                                    if(typeLookAhead.equals("SEMICOLON")){
                                        prevToken = "SEMICOLON";
                                        tokenPopper();
                                        tokenTypePopper();
                                        peeker();
                                    }
                                    else if(typeLookAhead.equals("CLOSE_PAREN")){
                                        tokenPopper();
                                        tokenTypePopper();
                                        peeker();
                                        isGoing = false;
                                    }
                                    else {
                                        // Error: Missing a ";" or ")"
										isGoing = false;
                                        newcount++;
                                        errparser.error_checker(30, "error.txt" , newcount, tokenLookAhead);
                                    }
                                    
                                }
                                else {
                                    // Invalid data type
									isGoing = false;
                                    newcount++;
    						        errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
                                }
                            }
                            else {
                                // Missing a "," or ":"
								isGoing = false;
                                newcount++;
                                errparser.error_checker(31, "error.txt" , newcount, tokenLookAhead);
                            }
                            
                        }
                        else {
                            // Missing id
							isGoing = false;
                            newcount++;
                            errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
                        }
                        
                    }

                }

                if(typeLookAhead.equals("COLON")){
                    tokenPopper();
                    tokenTypePopper();
                    peeker();
                    if(typeLookAhead.equals("DATA_TYPE")){
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                        if(typeLookAhead.equals("SEMICOLON")){
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
                            if (compoundStatement(1)) {
                            	isValid = true;
    							System.out.println("Valid program declaration");
                            }
                            
                        }
                        else{
                            newcount++;
                            errparser.error_checker(27, "error.txt" , newcount, tokenLookAhead);
                        }
    
                    }
                    else {
                        newcount++;
                        errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
                    }

                }
                else {
                    newcount++;
                    errparser.error_checker(9, "error.txt" , newcount, tokenLookAhead);
                }

            }
            else {
                newcount++;
                errparser.error_checker(22, "error.txt" , newcount, tokenLookAhead);
            }
                
            
        }

        return isValid;
    }

    void print_errors(){

        ArrayList<String> errorList = errparser.get_errparselist();

        for(int i = 0; i < errorList.size(); i++){
            System.out.println(errorList.get(i));
        }
    }

    void programStructure(){
        if(program()){
            if(variableDeclaration()){
                if(functionDeclaration()){
					if(compoundStatement(0))
                    	System.out.println("No errors");
                }

            }
        }

        print_errors();
    }

}
