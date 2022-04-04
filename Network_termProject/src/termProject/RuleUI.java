package termProject;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class RuleUI extends JFrame{
   private JPanel contentPane;

   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               RuleUI frame = new RuleUI();
               frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }
   
   public RuleUI() {
      setVisible(true);
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(916, 800);
      setLocation(360,25);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      JButton rulebutton = new JButton(new ImageIcon(RuleUI.class.getResource("exit.png")));
      rulebutton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            dispose();
         }
      });
      rulebutton.setBorderPainted(false);
      rulebutton.setContentAreaFilled(false);
      rulebutton.setFocusPainted(false);
      rulebutton.setBounds(700, 40, 207, 127);
      contentPane.add(rulebutton);
      
      JLabel lblNewLabel = new JLabel("");
      lblNewLabel.setIcon(new ImageIcon(RuleUI.class.getResource("rule.jpg")));
      lblNewLabel.setBounds(0, 0, 916, 800);
      contentPane.add(lblNewLabel);
   }
}