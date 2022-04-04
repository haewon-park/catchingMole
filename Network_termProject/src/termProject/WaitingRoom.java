package termProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class WaitingRoom extends JFrame {
   private JPanel contentPane;
   private JTextField textField;
   JLabel p1name;
   JLabel p1rate;
   JLabel p2name;
   JLabel p2rate;
   JButton p1start;
   JButton p1bye;
   JButton p2start;
   JButton p2bye;
   JTextArea textArea;
   //GamePanel gp;
   int flag = 0;
   String msg;
   String player = null;
   Socket s1 = null;
   static GameClient gameclient;
   /**
    * Launch the application.
    */
   public void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               String p1n = "p1name";
               String p1r = "p1rate";
               String p2n = "p2name";
               String p2r = "p2rate";
               WaitingRoom frame = new WaitingRoom(p1n, p1r, p2n, p2r);
               frame.setVisible(true);
         
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * Create the frame.
    * @throws Exception 
    */
   public WaitingRoom (String p1n, String p1r, String p2n, String p2r) throws Exception
   {

       
      setVisible(true);
      setResizable(false);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 916, 800);
      contentPane = new JPanel();
      contentPane.setBackground(new Color(0x00285b));
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);


      //패널 생성하면서 여기에 게임 클라이언트추가하고 setVisible(false)로 해놓기 
      JPanel gamePanel = new JPanel();
      gamePanel.setBorder(new LineBorder(Color.ORANGE, 3));
      gamePanel.setBackground(Color.BLACK);
      gamePanel.setBounds(193, 12, 515, 600);
      contentPane.add(gamePanel);
      gamePanel.setLayout(null);


      JPanel p1 = new JPanel();
      p1.setBorder(new LineBorder(new Color(255, 200, 0), 7, true));
      p1.setBounds(9, 12, 178, 138);
      p1.setBackground(new Color(0x00285b));
      contentPane.add(p1);
      p1.setLayout(null);

      p1name = new JLabel(p2n);
      p1name.setForeground(Color.WHITE);
      p1name.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 22));
      p1name.setHorizontalAlignment(SwingConstants.CENTER);
      p1name.setBounds(42, 36, 109, 41);
      p1.add(p1name);

      p1rate = new JLabel(p2r);

      p1rate.setForeground(Color.WHITE);
      p1rate.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 18));
      p1rate.setHorizontalAlignment(SwingConstants.CENTER);
      p1rate.setBounds(18, 90, 151, 18);
      p1.add(p1rate);

      JPanel p2 = new JPanel();
      p2.setBorder(new LineBorder(Color.ORANGE, 7));
      p2.setBounds(715, 12, 178, 138);
      p2.setBackground(new Color(0x00285b));
      contentPane.add(p2);
      p2.setLayout(null);

      p2name = new JLabel(p1n);
      p2name.setForeground(Color.WHITE);
      p2name.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 22));
      p2name.setHorizontalAlignment(SwingConstants.CENTER);
      p2name.setBounds(35, 36, 107, 41);
      p2.add(p2name);

      p2rate = new JLabel(p1r);
      p2rate.setForeground(Color.WHITE);
      p2rate.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 18));
      p2rate.setHorizontalAlignment(SwingConstants.CENTER);
      p2rate.setBounds(60, 89, 62, 18);
      p2.add(p2rate);

      JPanel chatPanel = new JPanel();
      chatPanel.setBorder(new LineBorder(new Color(255, 200, 0), 6));
      chatPanel.setBounds(193, 611, 515, 145);
      contentPane.add(chatPanel);
      chatPanel.setLayout(null);

      textField = new JTextField();
      textField.setForeground(new Color(0, 0, 0));
      textField.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 16));
      textField.setBounds(5, 99, 505, 40);
      chatPanel.add(textField);
      textField.setColumns(10);
      textField.addActionListener(new ActionListener() 
      {
         public void actionPerformed(ActionEvent e) {
            msg = "PVP_say|" + p1name.getText() + "|" +p2name.getText() + "|" +textField.getText(); 
            System.out.println(msg);
            Client.out.println(msg);
            textField.setText("");
         }
      });

      textArea = new JTextArea();
      textArea.setForeground(Color.WHITE);
      textArea.setBackground(Color.BLACK);
      textArea.setEditable(false);
      textArea.setBounds(5, 3, 505, 95);
      chatPanel.add(textArea);

      ImageIcon originIcon = new ImageIcon(ChatingRoom.class.getResource("ready.png"));  
      Image originImg = originIcon.getImage(); 
      Image changedImg= originImg.getScaledInstance(178, 58, Image.SCALE_SMOOTH );
      ImageIcon Icon = new ImageIcon(changedImg);

      //먼저 버튼 누르면 p1 -> READY|p1|p2
      p1start = new JButton(Icon);

      p1start.setBorderPainted(false);
      p1start.setContentAreaFilled(false);
      p1start.setFocusPainted(false);
      
      p1start.setVisible(true);
      p1start.setBounds(9, 575, 178, 58);
      contentPane.add(p1start);
      p1start.addActionListener(new ActionListener() {
         @SuppressWarnings("static-access")
         public void actionPerformed(ActionEvent arg0) {
            try {
               gameclient = new GameClient(s1);
               gameclient.main(null);
               System.out.println("클릭");
            } catch (IOException e) {
               // TODO Auto-generated catch block
               System.out.println("안들어와여");
            }
         }
      });
      ImageIcon oI = new ImageIcon(ChatingRoom.class.getResource("exit2.png"));  
      Image img = oI.getImage(); 
      Image changed= img.getScaledInstance(178, 58, Image.SCALE_SMOOTH );
      ImageIcon ic = new ImageIcon(changed);


      p1bye = new JButton(ic);
      p1bye.setBorderPainted(false);
      p1bye.setContentAreaFilled(false);
      p1bye.setFocusPainted(false);
      p1bye.setBounds(9, 645, 178, 58);
      contentPane.add(p1bye);
      p1bye.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            //다시 채팅창 띄우고 게임대기창 끄고
            //서버에 다시 인바이트해주기 p1이랑 p2       
            //client.out.println("update|");
            Client.out.println("EXIT_ROOM");
            dispose();
         }
      });

   }
   public void say (String msg)
   {
      textArea.append(msg + "\n");
   }
}
//둘다 수락했으니까 게임방 만들어조!