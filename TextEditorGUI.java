// Java Program to create a text editor using java
// Source: https://www.geeksforgeeks.org/java-swing-create-a-simple-text-editor/
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.io.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;
class TextEditorGUI extends JFrame implements ActionListener {
	//int counter = 0;
	JTextArea sourceCodeTextArea, outputBox, errorBox;
	JFrame f;
	JPanel panel, topPanel, bottomPanel;
	JScrollPane outputScrollPane, errorScrollPane, sourceCodeScrollPane;
	JScrollBar scrollBar;

	BufferedWriter bWriter;
    FileWriter fileW;

	// Constructor
	public TextEditorGUI()
	{
		// Create a frame
		f = new JFrame("PASCAL PARSER");

		panel = new JPanel(new BorderLayout());
		topPanel = new JPanel();
		bottomPanel = new JPanel(new BorderLayout());	

		try {
			// Set metal look and feel
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

			// Set theme to ocean
			MetalLookAndFeel.setCurrentTheme(new OceanTheme());
		}
		catch (Exception e) {
		}

		// Text component
		sourceCodeTextArea = new JTextArea();
		outputBox = new JTextArea();
		errorBox = new JTextArea();

		sourceCodeScrollPane = new JScrollPane(sourceCodeTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		outputScrollPane = new JScrollPane(outputBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		errorScrollPane = new JScrollPane(errorBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		outputBox.setEditable(false);
		errorBox.setEditable(false);

		sourceCodeTextArea.setBorder(new LineBorder(Color.GREEN, 1));
		outputBox.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		errorBox.setBorder(new LineBorder(Color.red, 1));

		//topPanel.add(sourceCodeTextArea);
		topPanel.add(sourceCodeScrollPane);
		bottomPanel.add(outputScrollPane);
		bottomPanel.add(errorScrollPane, BorderLayout.EAST);

		sourceCodeScrollPane.setPreferredSize(new Dimension(1000, 350));
		sourceCodeTextArea.setMinimumSize(new Dimension(1000, 350));
		outputBox.setMinimumSize(new Dimension(500, 300));
		errorScrollPane.setPreferredSize(new Dimension(500, 280));
		errorBox.setMinimumSize(new Dimension(500, 280));

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(bottomPanel, BorderLayout.CENTER);

		// Add padding on all textareas
		sourceCodeTextArea.setBorder(BorderFactory.createCompoundBorder(sourceCodeTextArea.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		outputBox.setBorder(BorderFactory.createCompoundBorder(outputBox.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		errorBox.setBorder(BorderFactory.createCompoundBorder(errorBox.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Create a menubar
		JMenuBar mb = new JMenuBar();

		// Create amenu for menu
		JMenu m1 = new JMenu("File");

		// Create menu items
		JMenuItem mi0 = new JMenuItem("Reset");
		JMenuItem mi1 = new JMenuItem("New");
		JMenuItem mi2 = new JMenuItem("Open");
		JMenuItem mi3 = new JMenuItem("Save");

		// Add action listener
		mi0.addActionListener(this);
		mi1.addActionListener(this);
		mi2.addActionListener(this);
		mi3.addActionListener(this);

		m1.add(mi0);
		m1.add(mi1);
		m1.add(mi2);
		m1.add(mi3);

		// Create amenu for menu
		JMenu m2 = new JMenu("Edit");

		// Create menu items
		JMenuItem mi4 = new JMenuItem("cut");
		JMenuItem mi5 = new JMenuItem("copy");
		JMenuItem mi6 = new JMenuItem("paste");

		// Add action listener
		mi4.addActionListener(this);
		mi5.addActionListener(this);
		mi6.addActionListener(this);

		m2.add(mi4);
		m2.add(mi5);
		m2.add(mi6);

		JMenuItem m3 = new JMenuItem("Run");

		m3.addActionListener(this);

		mb.add(m1);
		mb.add(m2);
		mb.add(m3);

		f.setJMenuBar(mb);
		f.add(panel);
		f.setMinimumSize(new Dimension(1000, 700));
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// If a button is pressed
	public void actionPerformed(ActionEvent e)
	{
		String s = e.getActionCommand();

		if (s.equals("cut")) {
			sourceCodeTextArea.cut();
		}
		else if (s.equals("copy")) {
			sourceCodeTextArea.copy();
		}
		else if (s.equals("paste")) {
			sourceCodeTextArea.paste();
		}
		else if (s.equals("Reset")) {
			outputBox.setText("");
			errorBox.setText("");
			File inputFile = new File("inputfile.pas");
			File outputFile = new File("outputfile.tok");

			// reset input and output files
			try {
				this.fileW = new FileWriter(inputFile);
            	this.bWriter = new BufferedWriter(fileW);
				bWriter.write(outputBox.getText());

				this.fileW = new FileWriter(outputFile);
				this.bWriter = new BufferedWriter(fileW);
				bWriter.write(outputBox.getText());

				bWriter.close(); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (s.equals("Save")) {
			// Create an object of JFileChooser class
			JFileChooser j = new JFileChooser("f:");

			// Invoke the showsSaveDialog function to show the save dialog
			int r = j.showSaveDialog(null);

			if (r == JFileChooser.APPROVE_OPTION) {

				// Set the label to the path of the selected directory
				File fi = new File(j.getSelectedFile().getAbsolutePath());

				try {
					// Create a file writer
					FileWriter wr = new FileWriter(fi, false);

					// Create buffered writer to write
					BufferedWriter w = new BufferedWriter(wr);

					// Write
					w.write(sourceCodeTextArea.getText());

					w.flush();
					w.close();
				}
				catch (Exception evt) {
					JOptionPane.showMessageDialog(f, evt.getMessage());
				}
			}
			// If the user cancelled the operation
			else
				JOptionPane.showMessageDialog(f, "the user cancelled the operation");
		}
		else if (s.equals("Open")) {
			// Create an object of JFileChooser class
			JFileChooser j = new JFileChooser("f:");

			// Invoke the showsOpenDialog function to show the save dialog
			int r = j.showOpenDialog(null);

			// If the user selects a file
			if (r == JFileChooser.APPROVE_OPTION) {
				// Set the label to the path of the selected directory
				File fi = new File(j.getSelectedFile().getAbsolutePath());

				try {
					// String
					String s1 = "", sl = "";

					// File reader
					FileReader fr = new FileReader(fi);

					// Buffered reader
					BufferedReader br = new BufferedReader(fr);

					// Initialize sl
					sl = br.readLine();

					// Take the input from the file
					while ((s1 = br.readLine()) != null) {
						sl = sl + "\n" + s1;
					}

					// Set the text
					sourceCodeTextArea.setText(sl);
				}
				catch (Exception evt) {
					JOptionPane.showMessageDialog(f, evt.getMessage());
				}
			}
			// If the user cancelled the operation
			else
				JOptionPane.showMessageDialog(f, "the user cancelled the operation");
		}
		else if (s.equals("New")) {
            sourceCodeTextArea.setText("");
			outputBox.setText("");
			errorBox.setText("");
			File inputFile = new File("inputfile.pas");
			File outputFile = new File("outputfile.tok");

			// reset input and output files
			try {
				this.fileW = new FileWriter(inputFile);
            	this.bWriter = new BufferedWriter(fileW);
				bWriter.write(sourceCodeTextArea.getText());

				this.fileW = new FileWriter(outputFile);
				this.bWriter = new BufferedWriter(fileW);
				bWriter.write(outputBox.getText());

				bWriter.close(); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        }
		else if (s.equals("Run")) {
			String combi = "";
            ArrayList<String> errlist;
            File inputFile = new File("inputfile.pas");
            File outputFile = new File("outputfile.tok");
            File errorFile = new File("error.txt");
            Scanner2 scanner = new Scanner2();
            int counter = 0;

			// write to inputfile.pas
			try {
				this.fileW = new FileWriter(inputFile);
            	this.bWriter = new BufferedWriter(fileW);
				bWriter.write(sourceCodeTextArea.getText());
				bWriter.close(); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
                String line;
                int lineNum = 1;
                boolean isnotcomm = true;
				boolean isString = false;
				boolean isFirstQuote = false;
                while((line = br.readLine())!= null){
                    scanner.read_line(inputFile, lineNum);
                    String oneLine = scanner.get_line();
                    ArrayList<String> lexemesPerLine = scanner.get_lexeme(oneLine);
                    for(int i = 0; i < lexemesPerLine.size(); i++){
                        System.out.print(scanner.console_dump(oneLine, lexemesPerLine.get(i), isnotcomm, isString));
                        scanner.file_dump(outputFile, scanner.console_dump(oneLine, lexemesPerLine.get(i), isnotcomm, isString));
                        String result = scanner.get_tokenresult();
                        if (result.equals("ERROR")) {
                        	counter++;
                        	scanner.lex_error(lexemesPerLine.get(i), "error.txt", counter);
                        }
                        //use this else if when having the string with spaces inside the bracket such as {this is a comment} with the 4 texts of code being considered as comments
                        else if (result.equals("COMMENT") || result.equals("OPEN_CURLY_BRACE") || result.equals("CLOSE_CURLY_BRACE")) {
                        	//deletes last line
                    		scanner.delete_line("outputfile.tok");
                    		
                        	String snip = lexemesPerLine.get(i);
                        	int curlind = 0;
                        	isnotcomm = false;
                        	if (snip.contains("}")) {
                        		
                        		//from here
                        		int len = snip.length();
                        		
                        		for (int j = 0; j < len; j++) {
                        			String letty = Character.toString(snip.charAt(j));
                        			if (letty.equals("}")) {
                        				curlind = j;
                        				break;
                        			}
                        		}
                        		int max = curlind + 1;
                        		String before = snip.substring(0, max), after = "";
                        		String newoutput = combi + before + "\t" + result + "\n";
                        		combi = "";
                        		scanner.file_dump(outputFile, newoutput);
                        		//to here delete if needed
                        		isnotcomm = true;
                        		//comment this one as well if needed
                        		//if it is not the last index
                        		if (curlind != (len - 1)) {
                        			after = snip.substring(max, len);
                        			scanner.file_dump(outputFile, scanner.console_dump(oneLine, after, isnotcomm, isString));
                        		}
                        	}
                        	else {
                        		combi = combi + snip + " ";
                        	}
                        }
						else if(result.equals("DOUBLE_QUOTE") || result.equals("STRING")){
							String snip = lexemesPerLine.get(i);
							if(!isFirstQuote && snip.contains("\"")){
								isString = true;
								isFirstQuote = true;
							}
							else if(isFirstQuote && snip.contains("\"")){
								isString = false;
								isFirstQuote = false;
							}
						}
                        
                    }
                    
                    
                    lineNum++;
                }

                errlist = scanner.acquire_errorlist();
                
                System.out.println("Printing the errors ");
                errorBox.setText("");
                for (int i = 0; i < errlist.size(); i++ ) {
                	System.out.println(errlist.get(i));
                	errorBox.append(errlist.get(i));
                }
               
                
                
            } catch (IOException error) {
                // TODO Auto-generated catch block
                error.printStackTrace();
            }

			// print output to output textarea
			try {
				outputBox.read(new InputStreamReader(
					getClass().getResourceAsStream("outputfile.tok")), null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//printing the errors 
			

        }
	}
	

}
