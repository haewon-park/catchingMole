package termProject;

import java.awt.event.*;
import javax.swing.*;

import java.awt.*;

public class Main extends JFrame {

   public Main() {
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setSize(916, 800);
      setResizable(false);
      setLocation(360, 20);
      getContentPane().setLayout(null);
      setVisible(true);

      JButton loginButton = new JButton("login", new ImageIcon(Main.class.getResource("loginbutton.jpg")));
      loginButton.setBorderPainted(false);
      loginButton.setContentAreaFilled(false);
      loginButton.setFocusPainted(false);
      loginButton.setBounds(500, 650, 242, 116);
      getContentPane().add(loginButton);

      JButton ruleButton = new JButton("rule", new ImageIcon(Main.class.getResource("rulebutton.jpg")));
      ruleButton.setBorderPainted(false);
      ruleButton.setContentAreaFilled(false);
      ruleButton.setFocusPainted(false);
      ruleButton.setBounds(150, 650, 243, 116);
      getContentPane().add(ruleButton);

      JLabel mainBG = new JLabel("");
      mainBG.setBounds(0, 0, 916, 800);
      getContentPane().add(mainBG);
      mainBG.setIcon(new ImageIcon(Main.class.getResource("first.jpg")));

      loginButton.addActionListener(new ButtonAction());
      ruleButton.addActionListener(new ButtonAction());             
   }

      

   public static void main(String[] args) {
      Main main = new Main();
      //main.setVisible(true);

   }

   class ButtonAction implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent e) {
         // TODO Auto-generated method stub

         JButton myButton = (JButton) e.getSource();
         String temp = myButton.getText();

         if (temp.equals("login")) {
            LoginUI frame = new LoginUI();
            frame.setSize(916,800);
            frame.setVisible(true);
         }
         
         else if(temp.equals("rule")) {
            RuleUI frame2 = new RuleUI();
            frame2.setSize(916,800);
            frame2.setVisible(true);
         }
      }
   }
}