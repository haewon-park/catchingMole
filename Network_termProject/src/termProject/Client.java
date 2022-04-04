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
	//0 : �α׿� �ȵȻ���, 1 : �α׿µȻ���, 2: ������Ϸ� (��ȭ����),
	
	//3 : ������ 1:1��ȭ��û�� ���� ,
	//5 : req_fileSend (������ �������ڿ��� ���������� ������û�� ����)
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
		{//��������
			
			socket = new Socket(InetAddress.getLocalHost(), 9997); //���� ��ü ����        
			System.out.println("������ ������ �Ǿ����ϴ�......");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}

		catch(Exception e){
			System.out.println("����[MultiClient class]:"+e);
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
				{ //�α׿� �õ� (��ȭ��)
					chatState = 1; //����� ���¸� �α׿� �� ���·� ����    
					m.setVisible(false);
				    m.dispose();
				    
					frame.setVisible(true);//chating room���� ����
					frame.setSize(916, 800);
				    frame.setResizable(false);
				    frame.setLocation(360, 20);
					//frame.dispose();
				    
				    
					name = msgArr[0];
					System.out.println("�α���!");
					
				   
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
					
				else if(msg.startsWith("say")){ //��ȭ����
					//say|���̵�|��ȭ����                   
					frame.say(msgArr[0], msgArr[1]);
					break;

				}

				else if (msg.startsWith("UPDATE"))
				{
					frame.update(msgArr[0]);
					break;
				}
				else if(msg.startsWith("show")){ //�������������ϰ����ϴ� �޽���
					//show|�޽�������    
					//msgArr[0]������ �˾�â ����
					frame.show(msgArr[0]);
					break;
				}

				else if(msg.startsWith("R_PvP")){ //�ش� ����ڿ��� 1:1��ȭ ��û
					//R_PvP|��³���  
					chatState = 3;//sender���� �˷��ܿ� 1:1��û�� ���Դٴ°� 
					chatmode=true; //Sender���� 1:1��û�� ���Դٴ°��� �˷��ֱ� ����
					frame.yesorno(msgArr[0]);
					//msgArr[0] �޼��� �˾�â���� ���� ��ư �ΰ� ���� ���� ���� �׼Ǹ����� ����� tnru
					//y��ư ������ MultiClient.gameReq = true;
					//n��ư ������ MultiClient.gameReq = false;
					if ( Client.gameReq == true)
					{//�ʴ� ���� ��� 
						out.println("PvPchat|" + name + "|yes");
						break;
					}
					else 
					{
						out.println("PvPchat|" + name +"|no");
						break;
					}
				}
				else if(msg.startsWith("GAME_CAHT")){ //�ش� ����ڿ��� 1:1��ȭ ��û
					waitingRoom.say(msgArr[0]);
					break;
				}   
				else if (msg.startsWith("INVITE"))
				{
					//System.out.println("���䰡,,,");
					if (chatmode)
					{
						out.println("PvPchat|no");
						break;
					}
					else 
					{
						System.out.println("�� ��������");
						out.println("PvPchat|yes");
						chatmode= true;
						chatState = 3;//���Ӹ��� �̸��̾�
						break;
					}
				}
				else if (msg.startsWith("GAMEMODE"))
	            {//GAMEMODE|���̸�|�����̱�Ƚ��|������Ƚ��|�����̸�|�����̱�Ƚ��|������Ƚ��
	               System.out.println(" Ŭ���̾�Ʈ ���Ӹ����");
	               frame.setVisible(false);
	               String rate1 = null;
	               String rate2 = null;
	               System.out.println(msgArr[0]+ msgArr[1]+ msgArr[2]);
	               System.out.println(msgArr[3]+ msgArr[4]+ msgArr[5]);
	               rate1 = msgArr[1];
	               rate1 = rate1.concat("��/");
	               rate1 = rate1.concat(msgArr[2]+"��");
	               rate2 = msgArr[4];
	               rate2 = rate2.concat("��/");
	               rate2 = rate2.concat(msgArr[5]+"��");
	               waitingRoom = new WaitingRoom(msgArr[0], rate1, msgArr[3], rate2);
	               waitingRoom.setVisible(true);	              
	               break;
	               //�Ѵ� ���������ϱ� ���ӹ� �������!
	               //���Ӽ��� ����
	               //���� gui���� 
	               //���⼭ ���� �͵��� ���Ӽ����� out���ֱ� 
	            }
				//����gui���� Ÿ�ڸ� ġ�� �ű⼭ �ٷ� pvp_say�� ������ �����ֱ� 
				else if (msg.startsWith(" "))
				{
					out.println("PLAYER|"+msgArr[0]+"|"+msgArr[1]);
				}
				
			}

			/* */           
		}//�޴� while
	} //ūwhile
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