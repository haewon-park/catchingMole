//레전드
package termProject;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GameClient extends JFrame {
   private JPanel rootPanel = null;
   private JPanel topPanel = null;
   private JLabel timeLabel = null;
   private JLabel scoreLabel = null;
   private JPanel gamePanel = null;
   private JButton[][] buttons = null;

   private Color foreground = Color.WHITE;
   private Color background = new Color(0x00285b);
   private Color labelBorderColor = Color.WHITE;

   private ExecutorService timeThread = null;
   private final int MaxTimeout = 30;
   private long timeOutStarts = 0;

   private ExecutorService receiver = null;
   private Socket socket = null;
   private PrintWriter out = null;
   private BufferedReader in = null;
   private Scanner input = null;

   private int openedId = -1; // 지금 열려있는 칸 번호 (c * (r + 1))

   public static void main(String[] args) throws IOException {
      GameClient frame = new GameClient();
      frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      frame.setVisible(true);
   }

   public GameClient(Socket socket) throws IOException {
      timeLabel = new JLabel();
      timeLabel.setForeground(foreground);
      timeLabel.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 50));
      scoreLabel = new JLabel();
      scoreLabel.setForeground(foreground);
      scoreLabel.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 50));

      topPanel = new JPanel(new BorderLayout());
      topPanel.setBorder(new EmptyBorder(30, 0, 30, 0)); //
      topPanel.setBackground(background);
      topPanel.add(timeLabel, BorderLayout.WEST);
      topPanel.add(scoreLabel, BorderLayout.EAST);

      int row = 4;
      int column = 4;
      buttons = new JButton[row][column];
      ImageIcon dudu = new ImageIcon(GameClient.class.getResource("enabled.jpg"));
      Image duduimg = dudu.getImage();
      Image dd = duduimg.getScaledInstance(224,180, Image.SCALE_SMOOTH);
      for (int r = 0; r < row; r++) {
         for (int c = 0; c < column; c++) {
            buttons[r][c] = new JButton(Integer.toString(4 * r + c),
                  new ImageIcon(dd));
            buttons[r][c].setBorderPainted(false);
            buttons[r][c].setFocusPainted(false);
            buttons[r][c].setOpaque(false);
            buttons[r][c].setEnabled(false);
         }
      }

      gamePanel = new JPanel(new GridLayout(row, column));
      for (int r = 0; r < row; r++) {
         for (int c = 0; c < column; c++) {
            gamePanel.add(buttons[r][c]);
         }
      }

      rootPanel = new JPanel(new BorderLayout());
      rootPanel.setBorder(new EmptyBorder(0, 30, 30, 30));
      rootPanel.setBackground(background);
      rootPanel.add(topPanel, BorderLayout.NORTH);
      rootPanel.add(gamePanel, BorderLayout.CENTER);

      this.setContentPane(rootPanel);
      this.setSize(916, 800);
      this.setResizable(false);
      this.setLocation(360, 20);

      try {
         InetAddress local;
         local = InetAddress.getLocalHost();
         String ip = local.getHostAddress();
         System.out.println(ip);
         socket = new Socket(ip, 9999);
         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         out = new PrintWriter(socket.getOutputStream());
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(0);
      }

      if (socket != null) {
         try {
            out = new PrintWriter(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());
            receiver = Executors.newSingleThreadExecutor();
            receiver.execute(new Runnable() {
               @Override
               public void run() {
                  JFrame WinnerFrame = new JFrame();
                  JLabel winner;

                  while (true) {
                     String command = in.nextLine();
                     System.out.println(command);
                     switch (command) {
                     case "start":

                        timeThread = Executors.newSingleThreadExecutor();
                        timeThread.execute(new Runnable() {
                           @Override
                           public void run() {
                              startTimeout();
                              while (true) {
                                 int remainingSec = (int) (System.currentTimeMillis() - timeOutStarts)
                                       / 1000;

                                 if ((MaxTimeout - remainingSec) <= 0) {
                                    out.println("finish");
                                    out.flush();
                                    timeLabel.setText("남은 시간 : " + 0);// 남은 시간이 0일 때, 게임 finish
                                 } else
                                    timeLabel.setText(
                                          "남은 시간 : " + Integer.toString(MaxTimeout - remainingSec));
                                 try {
                                    Thread.sleep(100);
                                 } catch (InterruptedException e) {
                                    e.printStackTrace();
                                 }
                              }
                           }
                        });
                        break;

                     case "newopen": // 새로운 칸이 열렸을 때
                        int r = in.nextInt(); // 메인서버에서 랜덤 숫자 돌려서 보내주세영!
                        int c = in.nextInt(); // 얘도여!
                        openedId = 4 * r + c;
                        System.out.println(openedId);
                        buttons[r][c].setEnabled(true);
                        buttons[r][c].addActionListener(new ButtonAction());
                        break;

                     case "oppclk": // 상대가 눌렀을 때
                        command = "newopen";
                        break;

                     case "finish": // 남은시간이 0일 때 서버가 stop을 보내면 멈추는 것도 추가해주세여! 멈춰야해여!
                        int win = in.nextInt(); // 이기면 1, 지면 0
                        if (win == 1) {
                           winner = new JLabel(new ImageIcon(GameClient.class.getResource("win.jpg")));
                           WinnerFrame.add(winner);
                           WinnerFrame.setSize(550, 150);
                           WinnerFrame.setLocation(520, 400);
                           WinnerFrame.setVisible(true);
                           WinnerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        }

                        else {
                           winner = new JLabel(new ImageIcon(GameClient.class.getResource("lose.jpg")));
                           WinnerFrame.add(winner);
                           WinnerFrame.setSize(600, 150);
                           WinnerFrame.setLocation(520, 400);
                           WinnerFrame.setVisible(true);
                           WinnerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        }
                        break;
                        
                     case "renewopen": 
                        r = in.nextInt(); // 메인서버에서 랜덤 숫자 돌려서 보내주세영!
                        c = in.nextInt(); // 얘도여!
                        openedId = 4 * r + c;
                        System.out.println("re " + openedId);
                        buttons[r][c].setEnabled(true);
                        buttons[r][c].addActionListener(new ButtonAction());
                        out.println("end");
                        out.flush();
                        break;
                        
                     default:
                        System.out.println("Unknown: " + command);
                        break;
                     }
                  }
               }
            });
         }

         catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private GameClient() throws IOException {
      this(null);
   }

   private void startTimeout() {
      timeOutStarts = System.currentTimeMillis();
   }

   class ButtonAction implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         JButton myButton = (JButton) e.getSource();
         String temp = myButton.getText();

         if (temp.equals(Integer.toString(openedId))) {
            out.println("iclick");
            out.flush();

            int c = openedId % 4;
            int r = openedId / 4;

            buttons[r][c].setEnabled(false);
         }
      }
   }

}