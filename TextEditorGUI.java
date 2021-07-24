// Java Program to create a text editor using java
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class TextEditorGUI extends JFrame implements ActionListener {

	JTextArea t;
	JFrame f;

	// Constructor
	public TextEditorGUI()
	{
		// Create a frame
		f = new JFrame("PASCAL SCANNER");

		try {
			// Set metal look and feel
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

			// Set theme to ocean
			MetalLookAndFeel.setCurrentTheme(new OceanTheme());
		}
		catch (Exception e) {
		}

		// Text component
		t = new JTextArea();

		// Create a menubar
		JMenuBar mb = new JMenuBar();

		// Create amenu for menu
		JMenu m1 = new JMenu("File");

		// Create menu items
		JMenuItem mi1 = new JMenuItem("New");
		JMenuItem mi2 = new JMenuItem("Open");
		JMenuItem mi3 = new JMenuItem("Save");

		// Add action listener
		mi1.addActionListener(this);
		mi2.addActionListener(this);
		mi3.addActionListener(this);

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
		f.add(t);
		f.setSize(500, 500);
		f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// If a button is pressed
	public void actionPerformed(ActionEvent e){
		String s = e.getActionCommand();

		if (s.equals("cut")) {
			t.cut();
		}
		else if (s.equals("copy")) {
			t.copy();
		}
		else if (s.equals("paste")) {
			t.paste();
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
					w.write(t.getText());

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
					t.setText(sl);
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
			t.setText("");
		}
        else if(s.equals("Run")) {
            System.out.println("running");

            File inputFile = new File("inputfile.pas");
            File outputFile = new File("outputfile.tok");
            File errorFile = new File("error.txt");
            Scanner2 scanner = new Scanner2();

            try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
                String line;
                int lineNum = 1;
                while((line = br.readLine())!= null){
                    scanner.read_line(inputFile, lineNum);
                    String oneLine = scanner.get_line();
                    ArrayList<String> lexemesPerLine = scanner.get_lexeme(oneLine);
                    
                    for(int i = 0; i < lexemesPerLine.size(); i++){
                        System.out.print(scanner.console_dump(oneLine, lexemesPerLine.get(i)));
                        scanner.file_dump(outputFile, scanner.console_dump(oneLine, lexemesPerLine.get(i)));
                    }
                    
                    lineNum++;
                }
            } catch (IOException error) {
                // TODO Auto-generated catch block
                error.printStackTrace();
            }

        }
	}

}
