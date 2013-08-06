/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gizmoe.demoui;

import com.sun.xml.internal.ws.util.StringUtils;
import gizmoe.TaskDagResolver.ResolveDag;
import gizmoe.capabilities.DemoBaseCapability;
import gizmoe.taskdag.MyDag;
import gizmoe.taskexecutor.CapabilitySpawner;
import gizmoe.taskexecutor.TaskExecutor;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.transaction.Status;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.commons.io.FileUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 *
 * @author upsham
 */
public class DemoUi extends javax.swing.JFrame {

    private int error = 0;
    private String taskRunning = null;
    private long seconds;
    private BatteryUI Battery;

    /**
     * Creates new form DemoUi
     */
    public DemoUi() {
        initComponents();
        jLabel3.setVisible(false);
        jScrollPane2.setVisible(false);
        jTextPane1.setVisible(false);
        this.setTitle("GizmoE Task Demo UI");
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }
    

    public void errorEncountered(String handle) {
        error = 1;
        String text = null;
        if (handle.equals("emailNotFound")) {
            text = ("The person was not listed in advisor directory (email wasn't found)");
            jLabel3.setText(text);
            changeIcon("Wrong.png", text);
        }else if (handle.equals("notAvailable")) {
            text = ("Advisor is currently listed as busy in the calendar!");
            jLabel3.setText(text);
            changeIcon("Wrong.png", text);
        }else if (handle.equals("photoNotFound")) {
            text = ("Advisor's photo was not found!");
            jLabel3.setText(text);
            changeIcon("Exclamation.png", text);
            error = 3;
        }
        
        try {
            StyledDocument doc = jTextPane1.getStyledDocument();

            //  Define a keyword attribute

            SimpleAttributeSet keyWord = new SimpleAttributeSet();
            StyleConstants.setForeground(keyWord, Color.RED);
            StyleConstants.setBackground(keyWord, Color.YELLOW);
            StyleConstants.setBold(keyWord, true);
            text = text + "\n";
            doc.insertString(doc.getLength(), text, keyWord);
            jTextPane1.setCaretPosition(jTextPane1.getCaretPosition() + text.length());
        } catch (BadLocationException ex) {
            System.out.println("Text Pane Exception");
            //Logger.getLogger(DemoUi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void doBatteryUI(long current, long total) {
        if (total > 0) {
            float percent = ((float) (total - current)) / total;
            percent = percent * 100;
            if (percent > 55) {
                changeBatteryIcon("Battery Full.png");
            } else if (percent > 14) {
                changeBatteryIcon("Battery Medium.png");
            } else if (percent > 0) {
                changeBatteryIcon("Critical Energy.png");
            } else {
                changeBatteryIcon("Battery Empty.png");
                Battery.kill();
            }
        } else {
            changeBatteryIcon("Battery Empty.png");
            Battery.kill();
        }
    }

    private void changeBatteryIcon(String name){
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(name));
        jLabel5.setHorizontalAlignment(JLabel.CENTER);
        jLabel5.setIcon(imageIcon);
        jLabel5.setHorizontalTextPosition(JLabel.CENTER);
        jLabel5.setVerticalTextPosition(JLabel.BOTTOM);
        jLabel5.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public synchronized void setArea(String text) {
        try {
            String labelText = text.split("::")[1];
            if(labelText.contains("will run out in")){
                Battery = new BatteryUI(seconds, this);
                Thread t = new Thread(Battery);
                t.start();
            }else if(labelText.contains("thread for BatteryMonitor")){
                Battery.kill();
            }
            if (labelText.endsWith(" ")) {
                labelText = labelText.substring(0, labelText.length() - 1);
            }
            if(!labelText.contains("Killing the thread for")){
                jLabel3.setText(labelText + "...");
            }
            StyledDocument doc = jTextPane1.getStyledDocument();

            //  Define a keyword attribute

            SimpleAttributeSet keyWord = new SimpleAttributeSet();
            StyleConstants.setForeground(keyWord, Color.BLACK);
            StyleConstants.setBackground(keyWord, Color.WHITE);
            StyleConstants.setBold(keyWord, false);

            //  Add some text
            text = text + ".\n";
            doc.insertString(doc.getLength(), text, keyWord);
            jTextPane1.setCaretPosition(jTextPane1.getCaretPosition() + text.length());
        } catch (BadLocationException ex) {
            System.out.println("Text Pane Exception");
            //Logger.getLogger(DemoUi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void lowBattery(){
        error = 2;
        try {
            StyledDocument doc = jTextPane1.getStyledDocument();

            //  Define a keyword attribute

            SimpleAttributeSet keyWord = new SimpleAttributeSet();
            String text = "Battery has run out! Killing everything else and returning to home base...";
            changeIcon("low.png", text);
            StyleConstants.setForeground(keyWord, Color.RED);
            StyleConstants.setBackground(keyWord, Color.WHITE);
            StyleConstants.setBold(keyWord, true);

            //  Add some text
            text =  text+"\n";
            doc.insertString(doc.getLength(),text, keyWord);
            jTextPane1.setCaretPosition(jTextPane1.getCaretPosition() + text.length());
        } catch (BadLocationException ex) {
            System.out.println("Text Pane Exception");
            //Logger.getLogger(DemoUi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taskfinished() {
        try {
            StyledDocument doc = jTextPane1.getStyledDocument();

            //  Define a keyword attribute

            SimpleAttributeSet keyWord = new SimpleAttributeSet();
            StyleConstants.setItalic(keyWord, true);
            StyleConstants.setBackground(keyWord, Color.WHITE);
            String text;
            if (error==1) {
                StyleConstants.setForeground(keyWord, Color.RED);
                text = "Task " + taskRunning + " has ended with critical errors!";
            }else if (error == 2){
                StyleConstants.setForeground(keyWord, Color.YELLOW);
                StyleConstants.setBackground(keyWord, Color.BLACK);
                text = "Task " + taskRunning + " was terminated due to battery constraints!";
            }else if(error == 3){
                StyleConstants.setForeground(keyWord, Color.RED);
                text = "Task " + taskRunning + " has ended with non-critical errors!";
            }else {
                jLabel3.setText("Task " + taskRunning + " was completed successfully...");
                changeIcon("Right.png", "Task " + taskRunning + " was completed successfully...");
                StyleConstants.setForeground(keyWord, Color.GREEN);
                text = "Task " + taskRunning + " was completed successfully!";
            }

            //  Add some text
            doc.insertString(doc.getLength(), text, keyWord);
            jTextPane1.setCaretPosition(jTextPane1.getCaretPosition() + text.length());
        } catch (BadLocationException ex) {
            System.out.println("Text Pane Exception");
            //Logger.getLogger(DemoUi.class.getName()).log(Level.SEVERE, null, ex);
        }
        jComboBox1.setEnabled(true);
        jButton1.setEnabled(true);
    }

    public String prompt(String prompt, Boolean startingCap, String type) {
        String line = null;
        error = 0;
        Boolean invalid;
        do {
            invalid = false;
            try {
                if (startingCap) {
                    changeIcon("user.png", "Need overall task input....");
                    line = JOptionPane.showInputDialog(this, "Overall Task Input is required.\nPlease enter input '" + prompt + "' of type '" + type + "': ", "Overall Task Input", JOptionPane.QUESTION_MESSAGE);
                } else {
                    changeIcon("user.png", "Need user input....");
                    line = JOptionPane.showInputDialog(this, prompt, "Task Internal User Input", JOptionPane.QUESTION_MESSAGE);
                }
                if (line == null) {
                    JOptionPane.showMessageDialog(this, "Canceling is not an option! Input is required at this point.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    invalid = true;
                } else if (line.equals("") || line.equals("\n") || line.equals("\t") || line.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "You entered an empty string! Please enter valid input only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    invalid = true;
                } else if (type.equals("int")) {
                    int i = Integer.parseInt(line);
                    if (i < 0) {
                        JOptionPane.showMessageDialog(this, "Invalid input! Please enter a positive integer only!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        invalid = true;
                    }
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please enter an integer only!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                invalid = true;
            }
        } while (invalid);
        if(prompt.contains("Battery")){
            seconds = Long.parseLong(line);
        }
        changeIcon("loading.gif", "Loading....");
        return line;
    }

    private void changeIcon(String name) {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(name));
        jLabel3.setHorizontalAlignment(JLabel.CENTER);
        jLabel3.setIcon(imageIcon);
        jLabel3.setHorizontalTextPosition(JLabel.CENTER);
        jLabel3.setVerticalTextPosition(JLabel.BOTTOM);
        jLabel3.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void changeIcon(String name, String labelString) {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(name));
        jLabel3.setHorizontalAlignment(JLabel.CENTER);
        jLabel3.setIcon(imageIcon);
        jLabel3.setText(labelString);
        jLabel3.setHorizontalTextPosition(JLabel.CENTER);
        jLabel3.setVerticalTextPosition(JLabel.BOTTOM);
        jLabel3.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Select the Task that you would like to run:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MeetAdvisor", "TryMeetingAdvisors", "MeetAdvisorWithBattery", "TryMeetingAdvisorsWithBattery" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Execute");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Loading...");

        jTextPane1.setEditable(false);
        jTextPane1.setFocusable(false);
        jScrollPane2.setViewportView(jTextPane1);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Battery Available: ");

        jLabel5.setText("Loading...");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(32, 32, 32)
                .add(jLabel1)
                .add(18, 18, 18)
                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton1)
                .add(49, 49, 49))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(71, 71, 71)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 646, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap(87, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(39, 39, 39)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 237, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel2)
                .add(22, 22, 22)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jButton1.setEnabled(false);
        jScrollPane2.setVisible(true);
        jTextPane1.setVisible(true);
        jLabel5.setVisible(false);
        jComboBox1.setEnabled(false);
        this.pack();
        Battery = null;
        jTextPane1.setText("");
        MyDag testdag = ResolveDag.TaskDagResolver(jComboBox1.getSelectedItem().toString());
        taskRunning = jComboBox1.getSelectedItem().toString();
        TaskExecutor te = new TaskExecutor();
        te.setForm(this);
        te.callback(testdag);
        CapabilitySpawner cs = new CapabilitySpawner();
        cs.setForm(this);
        Thread t1 = new Thread(cs);
        Thread t2 = new Thread(te);
        changeIcon("loading.gif", "Loading...");
        t1.start();
        t2.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        jLabel3.setHorizontalTextPosition(JLabel.CENTER);
        jLabel3.setHorizontalAlignment(JLabel.CENTER);
        if(jComboBox1.getSelectedItem().toString().equalsIgnoreCase("MeetAdvisorWithBattery")){
            long time = DemoBaseCapability.seconds*5-1;
            jLabel4.setText("Max Battery Required: "+time+" seconds.");
            jLabel4.setVisible(true);
        }else if(jComboBox1.getSelectedItem().toString().equalsIgnoreCase("TryMeetingAdvisorsWithBattery")){
            long time = DemoBaseCapability.seconds*9-2;
            jLabel4.setText("Approximate One-Fail Battery Required: "+time+" seconds.");
            jLabel4.setVisible(true);
        }else{
            jLabel4.setVisible(false);
        }
        this.pack();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(DemoUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(DemoUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(DemoUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(DemoUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DemoUi().setVisible(true);
            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
