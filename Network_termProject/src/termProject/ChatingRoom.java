package termProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;

public class ChatingRoom extends JFrame implements WindowListener {

   private JPanel contentPane;
   private JTable table;
   private JTextField textField;
   JTextArea textArea_1;
   static String msg = "s";
   String msgTemp = "s";
   JPanel panel_2;
   Vector <String> list= new Vector<String>();
   Vector <Vector <String>> LIST = new Vector <Vector <String>>();
   DefaultTableModel model;
   String toname = "";
   int x = 0;
   int y = 0;
   /**
    * Launch the application.
    */
   public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               ChatingRoom frame = new ChatingRoom();
               //frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * Create the frame.
    */
   public ChatingRoom() {
      setForeground(new Color(204, 0, 0));
      setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
      setBackground(new Color(0, 0, 51));
      setTitle("대기방");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 950, 800);
      contentPane = new JPanel();
      contentPane.setBackground(new Color(0x00285b));
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      JPanel panel = new JPanel();

      panel.setBorder(new LineBorder(new Color(249, 208, 3), 4));
      panel.setBounds(0, 12, 654, 729);
      contentPane.add(panel);
      panel.setLayout(null);

      Image img = new ImageIcon("이미지주소").getImage();
      textArea_1 = new JTextArea();
      textArea_1.setBounds(5, 5, 645, 650);
      textArea_1.setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 17));
      panel.add(textArea_1);

      textField = new JTextField();
      textField.setBounds(5, 656, 645, 68);
      textField.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 17));
      panel.add(textField);
      textField.setColumns(10);

      textField.addActionListener(new ActionListener() 
      {
         public void actionPerformed(ActionEvent e) {
            String name = Client.name;
            msg = msgTemp;
            msg = "ALLCHAT|" + name + "|" +textField.getText(); 
            Client.out.println(msg);
            textField.setText("");
         }
      });

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new LineBorder(new Color(0xf9d003), 4));
      panel_1.setBackground(new Color(0x00285b));
      panel_1.setBounds(654, 12, 264, 729);
      contentPane.add(panel_1);
      panel_1.setLayout(null);

      JLabel lblNewLabel = new JLabel("<접속중인 유저>");
      lblNewLabel.setForeground(new Color(255, 255, 255));
      lblNewLabel.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 17));
      lblNewLabel.setBounds(76, 23, 116, 18);
      panel_1.add(lblNewLabel);

      panel_2 = new JPanel();
      panel_2.setBackground(new Color(0x00285b));
      panel_2.setBounds(4, 59, 256, 666);
      panel_1.add(panel_2);;

      addWindowListener(new WindowAdapter() {   // 내부 무명클래스로서 
         @Override
         public void windowClosing(WindowEvent e) {  // 이벤트프로그램
            Client.out.println("EXIT_SERVER|"+Client.name);   
            System.exit(0);
            
         }

      });
   }

   public void say (String name, String input)
   {
      textArea_1.append("<"+ name + ">"+ input +"\n");
   }
   public int changeMsg()
   {
      if ( msg.equals(msgTemp))
         return 0;
      else 
         return 1;

   }
   
   public void update (String user)
   {
      int y = 0;
      System.out.println(user);
      String[] temp = user.split(" ");
      int size = temp.length;
      System.out.println(size);
      panel_2.removeAll();
      //pack();
      
      for(int i = 0 ; i < size ; i++)
      {
         add(temp[i], y);
         y = y+76;
      }

   }
   public void add(String user, int y)
   {
      JPanel p1 = new JPanel();
      JLabel l1 = new JLabel(user);
      l1.setBounds(14, 32, 62, 18);
      l1.setBackground(new Color(0x00285b));
      l1.setForeground(new Color(255, 255, 255));
      l1.setHorizontalAlignment(SwingConstants.RIGHT);
      l1.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 17));
      ImageIcon originIcon = new ImageIcon(ChatingRoom.class.getResource("invitebutton.png"));  
      Image originImg = originIcon.getImage(); 
      Image changedImg= originImg.getScaledInstance(105, 27, Image.SCALE_SMOOTH );
      ImageIcon Icon = new ImageIcon(changedImg);
   
      JButton b1 = new JButton(Icon);
      b1.setBorderPainted(false);
      b1.setContentAreaFilled(false);
      b1.setFocusPainted(false);
      b1.setBounds(14, 32, 105, 27);
      //b1.setSize(105, 27);
      b1.addActionListener(new ActionListener(){ //익명클래스로 리스너 작성
         public void actionPerformed(ActionEvent e){
            JButton b1 = (JButton) e.getSource();
            toname = user;
            System.out.println("INVITE|" + Client.name+"|"+toname);
            Client.out.println("INVITE|" + Client.name+"|"+toname);
            //INVITE|name|toname|result   
         }
      });
      p1.add(l1);
      p1.add(b1);
      p1.setBounds(0, y, 256, 76);
      p1.setBackground(new Color(0x00285b));
      panel_2.add(p1);
      revalidate(); 
      repaint();
   }
   
   
   public void show (String msg)
   {
      JOptionPane.showMessageDialog(this, msg);
   }
   public void yesorno(String msg)
   {
      int result = JOptionPane.showConfirmDialog(this, msg, "초대장 후후", JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION)
         Client.gameReq = true;
      else 
         Client.gameReq = false;

   }

   @Override
   public void windowActivated(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowClosed(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowClosing(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowDeactivated(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowDeiconified(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowIconified(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowOpened(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

}