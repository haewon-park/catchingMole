package termProject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginUI extends JFrame {
	public static final int DEFAULT = 0;
	public static final int LOGINCHECK = 1;
	public static final int CREATEACCOUNT = 2;
	public static final int REDUNTCHECK = 3;
	
	public static final int port_num = 9999;
	
	Scanner in;
    PrintWriter out;
	
	String hey;
	String what;
	String col;
	String username;
	String nickname;
	String name;
	String passwd;
	String email;
	String birthdate;
	String namecheck;
	int check = 0;
	int mode = DEFAULT;
	
	int checkboxnum;
	int adminLog = 0;
	static 	String url = "jdbc:mysql://127.0.0.1/member?serverTimezone=UTC&&useSSL=false&user=root&password=12345";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	Scanner sc = new Scanner(System.in);
	
	static JFrame warnFrame = new JFrame();
	static JLabel warnLabel = new JLabel();
	JButton warnBtn = new JButton("Got it");
	
	JFrame createFrame = new JFrame("Create Account");
	JPanel createPanel = new JPanel();

	JButton [] newBtn = new JButton[4];
	JLabel [] newLabel = new JLabel[7];
	JTextField [] newField = new JTextField[7];

	JPasswordField pwField = new JPasswordField();
	JTextField idField = new JTextField();
	
	Dimension dim_Frame = new Dimension(400,800);
	Dimension dim_Label = new Dimension(300,30);
	Dimension dim_Field = new Dimension(300,40);
	Dimension dim_Button = new Dimension(300,70);
	Dimension dim_SButton = new Dimension(150,70);
	
	static LoginUI login = null;
	/**
	 * Launch the application.
	 * @throws IOException 
	 */

	/**
	 * Create the frame.
	 */
	public LoginUI() {
		
		newLabel[0] = new JLabel("UserName");
		newField[0] = new JTextField();
		newLabel[1] = new JLabel("Password(should be longer than 7)");
		newField[1] = new JTextField();
		newLabel[2] = new JLabel("Rewrite Password");
		newField[2] = new JTextField();
		newLabel[3] = new JLabel("Name");
		newField[3] = new JTextField();
		newLabel[4] = new JLabel("Nickname");
		newField[4] = new JTextField();
		newLabel[5] = new JLabel("Birth Date (ex.19981925)");
		newField[5] = new JTextField();
		newLabel[6] = new JLabel("E-mail");
		newField[6] = new JTextField();
		newBtn[0] = new JButton("Save");
		newBtn[1] = new JButton("Cancel");
		newBtn[2] = new JButton("중복확인");
		
		newLabel[0].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newLabel[1].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newLabel[2].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newLabel[3].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newLabel[4].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newLabel[5].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newLabel[6].setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 12));
		newField[0].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		newField[1].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		newField[2].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		newField[3].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		newField[4].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		newField[5].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		newField[6].setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 12));
		
		warnFrame.setSize(400, 200);
		warnFrame.setLocation(0, 0);
		warnFrame.setLayout(null);
		warnFrame.setResizable(false);
		warnFrame.setVisible(false);
		
		warnLabel.setLocation(10, 10);
		warnLabel.setSize(380, 100);
		warnLabel.setFont(new Font("배달의민족 한나는 열한살", Font.PLAIN, 15));

		warnBtn.setSize(100, 50);
		warnBtn.setLocation(150, 100);
		warnBtn.addActionListener(new ButtonAction());
		
		
		warnFrame.add(warnLabel);
		warnFrame.add(warnBtn);

		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(950,800);
    	setResizable(false);
    	setLocation(360,20);
    	setVisible(true);
		getContentPane().setLayout(null);
		
		idField.setSize(350, 50);
		idField.setLocation(350, 315);
		idField.setFont(new Font("배달의민족 한나는 열한살", Font.BOLD, 40));
		add(idField);
		pwField.setSize(350,50);
		pwField.setLocation(350, 433);
		pwField.setFont(new Font("궁서",Font.BOLD, 40));
		add(pwField);
		
		JButton loginButton = new JButton("Log in",new ImageIcon(LoginUI.class.getResource("loginbutton.jpg")));
		
    	loginButton.setBorderPainted(false);
    	loginButton.setContentAreaFilled(false);
    	loginButton.setFocusPainted(false);
    	loginButton.setBounds(500, 650, 242, 116);
    	getContentPane().add(loginButton);
    	
    	JButton joinButton = new JButton("Create Account",new ImageIcon(LoginUI.class.getResource("joinbutton.jpg")));
    	
    	joinButton.setBorderPainted(false);
		joinButton.setContentAreaFilled(false);
		joinButton.setFocusPainted(false);
		joinButton.setBounds(150, 650, 243, 116);
		getContentPane().add(joinButton);
		
		JLabel mainBG = new JLabel("");
		mainBG.setBounds(0, 0, 950, 800);
		getContentPane().add(mainBG);
		mainBG.setIcon(new ImageIcon(LoginUI.class.getResource("login.jpg")));
		
		loginButton.addActionListener(new ButtonAction());
		joinButton.addActionListener(new ButtonAction());
		newBtn[0].addActionListener(new ButtonAction());
		newBtn[1].addActionListener(new ButtonAction());
		
	}
	class ButtonAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton myButton = (JButton)e.getSource();
			String temp = myButton.getText();

			warnFrame.setVisible(false);

			if(temp.equals("Log in")) {
				username = idField.getText();
				passwd = new String(pwField.getPassword());
				idField.setText("");
				pwField.setText("");

				System.out.println("Login 정보 " + username + " " + passwd);
				Client.out.println("LOGINCHECK|" + username +"|"+passwd);
				
				//login.setVisible(false);
				
			}
			
			else if(temp.equals("Create Account")) {
				mode = CREATEACCOUNT;
				
				createPanel.setLayout(null);
				createFrame.setSize(dim_Frame);
				createFrame.add(createPanel);
				createFrame.repaint();
				createFrame.setVisible(true);
				
				
				int x = 50, y = 10;

				for(int i =  0; i< newLabel.length ; i++) {
					newLabel[i].setLocation(x, y);
					newLabel[i].setSize(dim_Label);
					createPanel.add(newLabel[i]);
					y += 30;
					newField[i].setLocation(x, y);
					newField[i].setSize(dim_Field);
					createPanel.add(newField[i]);
					y += 40;
					

				}

				for(int i = 0; i < newBtn.length -2 ; i++) {
					newBtn[i].setLocation(x, y);
					newBtn[i].setSize(dim_SButton);
					newBtn[i].addActionListener(this);
					createPanel.add(newBtn[i]);
					x += 150;
				}
				
			}


			else if(temp.equals("Save")) {				
				int admission = 1;
				String warnMessage = "";
				
				if(!newField[1].getText().equals(newField[2].getText()) ) {
					warnMessage = "Rewrite Password.";
					admission = 0;
				}
				else if(newField[1].getText().length() < 8) {
					warnMessage = "password too short.";
					admission = 0;

				}

				else {
					for(int i = 0; i < newField.length; i++) {
						if(newField[i].getText().length()==0) {
							warnMessage = warnMessage + " " + newField[i].getText(); 
							admission = 0;

						}
					}
					if(admission == 0) warnMessage = "All data should be filled.";
				}
				if(admission == 0) {
					warnLabel.setText(warnMessage);
					warnFrame.setVisible(true);
				}
				else {
					System.out.println("new account added");
					username = newField[0].getText();
					passwd = newField[1].getText();
					name = newField[3].getText();
					nickname = newField[4].getText();
					birthdate = newField[5].getText();
					email = newField[6].getText();
					Client.out.println("CREATEACCOUNT|"+username+"|"+passwd+"|"+name+"|"+nickname+"|"+birthdate+"|"+email);

					warnLabel.setText("Account created! Login with your Account!");
					warnFrame.setVisible(true);
					createFrame.remove(createPanel);
					createFrame.revalidate();
					createFrame.repaint();
					
					mode = DEFAULT;
				
				}
			}
			
			else if(temp.equals("Got it")){
				warnFrame.setVisible(false);
				createFrame.setVisible(false);
				
			}
			else if(temp.equals("Cancel")) {
				remove(createFrame);
				createFrame.setVisible(false);
				
				for(int i = 0; i < newField.length; i++) {
					newField[i].setText("");
				}
				
				mode = DEFAULT;
			}
			
		}
	}
	
	public static void main(String[] args) throws IOException {
	 	
    	login = new LoginUI(); //클라이언트 선언하고 
        login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //끄면 닫는겨 
        login.setVisible(true);// 보이게 해주고 
	}

}

