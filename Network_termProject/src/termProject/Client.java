package termProject;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Client extends JFrame{
	static boolean gameReq = false;
	static boolean chatmode = false;
	static int chatState = 0;
	static String name;
	private JTextField textField;
	private JTextField textField_1;
	int buttonCnt = 0;
	String[] check = new String[2];
	ChatingRoom frame = new ChatingRoom();
	//0 : 로그온 안된상태, 1 : 로그온된상태, 2: 방입장완료 (대화가능),
	
	//3 : 상대방이 1:1대화요청한 상태 ,
	//5 : req_fileSend (상대방이 현재사용자에게 파일전송을 수락요청한 상태)
	String ID;
	String PassWord;
	static BufferedReader in;
	static PrintWriter out;
	static String message;
	Socket socket;
	WaitingRoom waitingRoom;
	Main m;
	//GUI
	public Client() {

		m = new Main();
		
	}
	public String[] getMsgParse(String msg)
	{

		String[] tmpArr = msg.split("\\|");   
		return tmpArr;
	}

	public void run () throws Exception
	{      
		try
		{//서버연결
			
			socket = new Socket(InetAddress.getLocalHost(), 9997); //소켓 객체 생성        
			System.out.println("서버와 연결이 되었습니다......");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}

		catch(Exception e){
			System.out.println("예외[MultiClient class]:"+e);
		}

		String msg = null;
		String[] msgArr;
		int n = 0;
		while (true)
		{
			System.out.println(++n);
			msg = in.readLine();
			System.out.println(msg);
			msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
			while (true)
			{   //receiver

				if(msg.startsWith("logon#yes"))
				{ //로그온 시도 (대화명)
					chatState = 1; //사용자 상태를 로그온 된 상태로 변경    
					m.setVisible(false);
				    m.dispose();
				    
					frame.setVisible(true);//chating room으로 입장
					frame.setSize(916, 800);
				    frame.setResizable(false);
				    frame.setLocation(360, 20);
					//frame.dispose();
				    
				    
					name = msgArr[0];
					System.out.println("로그인!");
					
				   
					out.println("ENTRANCE|"+name);
					chatState = 2;
					
					break;
				}

				else if(msg.startsWith("logon#no"))
				{ 
					LoginUI.warnFrame.setVisible(true);
					LoginUI.warnLabel.setText("Login failed.");
					break;
				}
				else if(msg.startsWith("logon#noID"))
				{ 
					LoginUI.warnFrame.setVisible(true);
					LoginUI.warnLabel.setText("Login failed");
					break;
				}
					
				else if(msg.startsWith("say")){ //대화내용
					//say|아이디|대화내용                   
					frame.say(msgArr[0], msgArr[1]);
					break;

				}

				else if (msg.startsWith("UPDATE"))
				{
					frame.update(msgArr[0]);
					break;
				}
				else if(msg.startsWith("show")){ //서버에서전달하고자하는 메시지
					//show|메시지내용    
					//msgArr[0]내용의 팝업창 띄우기
					frame.show(msgArr[0]);
					break;
				}

				else if(msg.startsWith("R_PvP")){ //해당 사용자에게 1:1대화 요청
					//R_PvP|출력내용  
					chatState = 3;//sender한테 알려줌여 1:1요청이 들어왔다는걸 
					chatmode=true; //Sender에게 1:1요청이 들어왔다는것을 알려주기 위함
					frame.yesorno(msgArr[0]);
					//msgArr[0] 메세지 팝업창으로 띄우고 버튼 두개 수락 거절 만들어서 액션리스너 만들기 tnru
					//y버튼 누르면 MultiClient.gameReq = true;
					//n버튼 누르면 MultiClient.gameReq = false;
					if ( Client.gameReq == true)
					{//초대 받은 사람 
						out.println("PvPchat|" + name + "|yes");
						break;
					}
					else 
					{
						out.println("PvPchat|" + name +"|no");
						break;
					}
				}
				else if(msg.startsWith("GAME_CAHT")){ //해당 사용자에게 1:1대화 요청
					waitingRoom.say(msgArr[0]);
					break;
				}   
				else if (msg.startsWith("INVITE"))
				{
					//System.out.println("여긴가,,,");
					if (chatmode)
					{
						out.println("PvPchat|no");
						break;
					}
					else 
					{
						System.out.println("예 게임하쟈");
						out.println("PvPchat|yes");
						chatmode= true;
						chatState = 3;//게임모드다 이말이야
						break;
					}
				}
				else if (msg.startsWith("GAMEMODE"))
	            {//GAMEMODE|내이름|내가이긴횟수|내가진횟수|상대방이름|상대방이긴횟수|상대방진횟수
	               System.out.println(" 클라이언트 게임모드임");
	               frame.setVisible(false);
	               String rate1 = null;
	               String rate2 = null;
	               System.out.println(msgArr[0]+ msgArr[1]+ msgArr[2]);
	               System.out.println(msgArr[3]+ msgArr[4]+ msgArr[5]);
	               rate1 = msgArr[1];
	               rate1 = rate1.concat("승/");
	               rate1 = rate1.concat(msgArr[2]+"패");
	               rate2 = msgArr[4];
	               rate2 = rate2.concat("승/");
	               rate2 = rate2.concat(msgArr[5]+"패");
	               waitingRoom = new WaitingRoom(msgArr[0], rate1, msgArr[3], rate2);
	               waitingRoom.setVisible(true);	              
	               break;
	               //둘다 수락했으니까 게임방 만들어조!
	               //게임서버 실행
	               //게임 gui열기 
	               //여기서 받은 것들은 게임서버로 out해주기 
	            }
				//게임gui에서 타자를 치면 거기서 바로 pvp_say를 서버에 보내주기 
				else if (msg.startsWith(" "))
				{
					out.println("PLAYER|"+msgArr[0]+"|"+msgArr[1]);
				}
				
			}

			/* */           
		}//받는 while
	} //큰while
	//run
	public void chat ()
	{
		out.println();
	}
	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//client.setVisible(true);
		client.run();

	}//main()-------
}