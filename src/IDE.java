import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
/*
 * Created by JFormDesigner on Fri Sep 17 10:43:25 SGT 2021
 */

public class IDE extends JFrame {
    private IDE id = null;
    public IDE() {
        initComponents();
        this.id = this;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        scrollPane1 = new JScrollPane();
        txtCode = new JTextArea();
        scrollPane2 = new JScrollPane();
        txtError = new JTextArea();
        scrollPane3 = new JScrollPane();
        txtOutput = new JTextArea();
        brnRun = new JButton();

        //======== this ========
        var contentPane = getContentPane();

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(txtCode);
        }

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(txtError);
        }

        //======== scrollPane3 ========
        {
            scrollPane3.setViewportView(txtOutput);
        }

        //---- brnRun ----
        brnRun.setText("Run");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 418, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(brnRun, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addComponent(scrollPane3)
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(scrollPane1)
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(brnRun)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        brnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                txtOutput.setText("");
                txtError.setText("");
                String text = txtCode.getText();
                BufferedWriter wr;
                try { wr = new BufferedWriter(new FileWriter("code.txt"));
                    wr.write(text);
                    wr.close();
                } catch (IOException ex) {
                    System.out.println("error");
                }
                ArrayList<Token> tokens = Scanner.c("code.txt").tokens;
                Parser p = Parser.c(tokens);
                AST tree = p.run_parser();

                Interpreter i = Interpreter.c(tree);
                i.ide = id;
                i.clear_log();
                i.walk_tree(tree);

                try (BufferedReader br = new BufferedReader(new FileReader("output.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                            txtOutput.append(line + "\n");
                        }
                    } catch (Exception ex){ ex.printStackTrace(); }

                try (BufferedReader br = new BufferedReader(new FileReader("error.txt"))){
                    String line;
                    while ((line = br.readLine()) != null){
                        txtError.append(line + "\n");
                    }
                }catch (Exception ex){ ex.printStackTrace(); }
            }
        });
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JScrollPane scrollPane1;
    private JTextArea txtCode;
    private JScrollPane scrollPane2;
    private JTextArea txtError;
    private JScrollPane scrollPane3;
    public JTextArea txtOutput;
    private JButton brnRun;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
