package termProject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;


/*�ܼ� ��Ƽä�� ���� ���α׷�*/
public class Server {
	public static final int DEFAULT = 0;
	public static final int LOGINCHECK = 1;
	public static final int CREATEACCOUNT = 2;
	public static final int REDUNTCHECK = 3;
	public static final int RESULT = 4;

	static 	String url = "jdbc:mysql://127.0.0.1/member?useUnicode=true&characterEncoding=euckr&serverTimezone=UTC&&useSSL=false&user=root&password=12345";
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	Scanner sc = new Scanner(System.in);

	ServerSocket serverSocket = null;

	Socket socket = null;

	HashMap<String,ServerRecThread> game;
	HashMap<String,ServerRecThread> online;

	String hey;
	String what;
	String col;
	String username;
	String nickname;
	String name;
	String passwd;
	String email;
	String birthdate;

	String[] playerData = new String[3];
	String[] namecheck = new String[2];
	String[] resultData = new String[3];
	String[] loginData = new String[3];
	String[] accountData = new String[6];
	int mode = DEFAULT;
	int check_name = 0;
	int check_nick = 0;
	int check = 0;

	Server(){
		//data = new HashMap<String,HashMap<String, ServerRecThread>>();
		//Collections.synchronizedMap(data); //�ؽ��� ����ȭ ����.
		new HashMap<String, ServerRecThread>();
		online = new HashMap<String, ServerRecThread>();
		Collections.synchronizedMap(online); //�ؽ��� ����ȭ ����.

		//data.put("on", online);

	}//������----

	/**���� ���� */
	public void init(){
		try{
			serverSocket = new ServerSocket(9997); //9999��Ʈ�� �������� ��ü����.
			//fromGame = new ServerSocket(9998);
			System.out.println("##������ ���۵Ǿ����ϴ�.");

			while(true){ //������ ����Ǵ� ���� Ŭ���̾�Ʈ���� ������ ��ٸ�.
				socket = serverSocket.accept(); //Ŭ���̾�Ʈ�� ������ ��ٸ��ٰ� ������ �Ǹ� Socket��ü�� ����.
				//gameSocket = fromGame.accept();
				System.out.println(socket.getInetAddress()+":"+socket.getPort()); //Ŭ���̾�Ʈ ���� (ip, ��Ʈ) ���

				Thread msr = new ServerRecThread(socket); //������ ����.
				msr.start(); //������ �õ�.
			}      

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void gameIn(String me, String you){

		try{
			ServerRecThread st1 = online.get(me);
			ServerRecThread st2 = online.get(you);
			online.remove(me);
			online.remove(you);
			game.put(me, st1);
			game.put(you, st2);
		}              
		catch(Exception e){
			System.out.println("����:"+e);
		}
	}

	public void gameOut(){
		Iterator<String> it_hash = game.keySet().iterator();
		while(it_hash.hasNext()){
			try{
				ServerRecThread st = game.get(it_hash.next());
				String key = it_hash.next();
				if (st.chatMode)
				{	
					game.remove(key);
					online.put(key, st);
				}

			}              
			catch(Exception e){
				System.out.println("����:"+e);
			}
		}
	}
	/** ���ǿ� �ִ� Ŭ���̾�Ʈ�鿡�� �޽����� ����. */
	public void sendAllWaitMsg(String msg){
		//HashMap<String, ServerRecThread> one  = data.get("on");//on�̶�� key�� ���� �ؽø��� one �ؽøʿ� ���� == ���� ������ ����� �� ����      
		Iterator<String> it_hash = online.keySet().iterator(); //key�� �� �޾ƿͼ� iterator ��ü�� �ٲ��ִ°���
		while(it_hash.hasNext()){//Ű�� �����Ҷ�����       
			try
			{
				//System.out.println(it_hash);
				ServerRecThread st = online.get(it_hash.next()); //it_hash.next()Ű�� ���� ������ ���� 
				if(!st.chatMode)//���Ӹ�尡 �ƴѻ�����Ը�
				{
					st.out.println(msg);
				}              
			}
			catch(Exception e)
			{
				System.out.println("����:"+e);
			}
		}
	}//sendAllWaitMsg()-----------

	/**1:1 ���ӹ� ��ȭ*/
	public void sendPvPMsg(String fromName, String toName, String msg){
		//fromname|toname|��ȭ����
		ServerRecThread st1 = online.get(fromName);
		ServerRecThread st2 = online.get(toName); //it_hash.next()Ű�� ���� ������ ���� 
		st1.out.println("GAME_CAHT|" + msg);
		st2.out.println("GAME_CAHT|" + msg);

	}


	//sendPvPMsg()-----------

	//main�޼���
	public static void main(String[] args) 
	{
		Server ms = new Server(); //������ü ����.
		ms.init();//����.
	}//main()------  

	////////////////////////////////////////////////////////////////////////
	//----// ���� Ŭ���� //--------//

	// Ŭ���̾�Ʈ�κ��� �о�� �޽����� �ٸ� Ŭ���̾�Ʈ(socket)�� ������ ������ �ϴ� �޼���

	class ServerRecThread extends Thread {

		Socket socket;

		String name=""; //�̸� ����
		String loc="";  //���� ����
		String toNameTmp = null;//1:1��ȭ ���  
		String fileServerIP; //���ϼ��� ������ ����
		String filePath; //���� �������� ������ ���� �н� ����.
		boolean chatMode = false; //1:1 ���Ӹ�� ����
		int state = 0;
		private BufferedReader in;
		private PrintWriter out;

		//������.
		public ServerRecThread(Socket socket){
			this.socket = socket;
			try
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
			}
			catch(Exception e)
			{
				System.out.println("ServerRecThread ������ ����:"+e);
			}
		}// -------------------------------------������

		/**���ӵ� ��������Ʈ  ���ڿ��� ��ȯ*/    
		public String showUserList(){
			StringBuilder output = new StringBuilder();
			Iterator it = online.keySet().iterator(); //�������� ���� ���̵���� ����
			while(it.hasNext()){ //�ݺ��ϸ鼭 ������̸��� StringBuilder�� �߰�
				try{      
					String key= (String) it.next();
					output.append(key +" ");                  
				}catch(Exception e){
					System.out.println("����:"+e);
				}
			}//while--------
			return output.toString();
		}//showUserList()-----------

		/**�޽��� �ļ� */    
		public String[] getMsgParse(String msg)
		{
			String[] tmpArr = msg.split("\\|");        
			return tmpArr;
		}

		public String getEncrypt(String temp)
		{
			try
			{
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(temp.getBytes("UTF-8"));
				StringBuffer hexString = new StringBuffer();

				for(int i = 0; i < hash.length; i++)
				{
					String hex = Integer.toHexString(0xff & hash[i]);
					if(hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}

				return hexString.toString();
			}
			catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}

		}
		public  String getRate(String me) throws Exception
		{
			String rate = null; 
			try {
				String my_win = null;
				String my_lose = null;
				int you_win = 0;
				int you_lose = 0;

				Class.forName("com.mysql.jdbc.Driver"); // JDBC ����̹� �ε�
				System.out.println("����̹� ���� ����!" + mode);

				conn = DriverManager.getConnection(url);
				System.out.println("�����ͺ��̽� ���� ����!");

				stmt = conn.createStatement();

				String useXproject = "use member";
				stmt.executeUpdate(useXproject);

				String search1 = "select * from user where username like '" + me +"';";
				//username���� ���� �����Ͱ� rs�� ����ǰ� �� ������ Ȯ��!
				rs = stmt.executeQuery(search1);

				if(rs.next()) {
					my_win = rs.getString("win");
					my_lose = rs.getString("lose");
				}
				rate = my_win;
				rate = rate.concat("��/");
				rate = rate.concat(my_lose+"��");

			}catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException e) {
				e.printStackTrace(); 
			}

			return rate;
		}


		public void command() {
			try {

				Class.forName("com.mysql.jdbc.Driver"); // JDBC ����̹� �ε�
				System.out.println("����̹� ���� ����!" + mode);

				conn = DriverManager.getConnection(url);
				System.out.println("�����ͺ��̽� ���� ����!");

				stmt = conn.createStatement();

				String useXproject = "use member";
				stmt.executeUpdate(useXproject);

				if(mode == LOGINCHECK) {
					col = "username";
					what = username;
					//username���� �˻��ϱ�
					String search = "select * from user where username like '" + loginData[1] +"';";
					//username���� ���� �����Ͱ� rs�� ����ǰ� �� ������ Ȯ��!
					rs = stmt.executeQuery(search);

					if(rs.next()) {
						String data = rs.getString("nickname")+","+rs.getString("win")+","+rs.getString("lose");
						System.out.println(data);

						String encrypt = getEncrypt(loginData[2]);
						if(encrypt.equals(rs.getString("passwd"))){
							out.println("logon#yes|" + rs.getString("nickname"));

						}
						else {
							System.out.println("login failed");
							out.println("logon#noPW");
						}
						SimpleDateFormat f = new SimpleDateFormat("hh:mm");
						String Update = "update user set lastTime = '" + f.format(new Date()) + "' where username = '"+ loginData[1]+"' ; ";
						stmt.executeUpdate(Update);
						System.out.println(Update);

					}
					else {
						System.out.println("login failed");
						out.println("logon#noID");

					}
				}

				else if(mode == CREATEACCOUNT) {

					String encrypt = getEncrypt(accountData[2]);

					SimpleDateFormat f = new SimpleDateFormat("hh:mm");
					String adduser = "insert into user(username, passwd, name, nickname, birthdate, email, lastTime, win, lose) "
							+ "values('"+ accountData[1] + "', '" + encrypt + "', '"
							+ accountData[3] + "', '" + accountData[4] +"', '" + accountData[5] + "', '" + accountData[6] + "', '" + f.format(new Date()) +  "', '" + 0 + "', '" + 0 + "');";
					System.out.println(adduser);
					stmt.executeUpdate(adduser);

				}
				else if(mode == RESULT)
				{
					String Update = "update user set win = win + 1"  
							+ " where username = '"+ resultData[1] + "' ; ";
					stmt.executeUpdate(Update);
					System.out.println(Update);

					Update = "update user set lose = lose + 1" + " where username = '"+ resultData[2] + " ; ";
					stmt.executeUpdate(Update);
					System.out.println(Update);
				}


			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException e) {
				e.printStackTrace(); 
			}
		}


		@Override
		public void run(){ //�����带 ����ϱ� ���ؼ� run()�޼��� ������
			HashMap<String, ServerRecThread> clientMap=null;   //���� Ŭ���̾�Ʈ�� ����Ǿ��ִ� �ؽ���       
			try{  

				while(in!=null){ //�Է½�Ʈ���� null�� �ƴϸ� �ݺ�.
					String msg = in.readLine(); //�Է½�Ʈ���� ���� �о�� ���ڿ��� msg�� �Ҵ�. 
					System.out.println(msg);
					String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
					name = msgArr[0]; //�Ѿ�� ��ȭ���� �������� name�� ����
					//�޼��� ó�� ----------------------------------------------

					if (msg.startsWith("LOGINCHECK")) // ���̵� �н����� �Է��ϰ� �α��ι�ư ������ R_LOGIN������ ���� �α��� �� �� �ִ��� üũ
					{
						mode = LOGINCHECK;
						loginData = msg.split("\\|");
						command();

						//���� �����ߴµ� ���̵� �н����尡 �ٸ��� 
						//out.println("logon#no|");
					}

					else if(msg.startsWith("CREATEACCOUNT"))
					{
						mode = CREATEACCOUNT;
						accountData = msg.split("\\|");
						command();
					}

					else if(msg.startsWith("ENTRANCE"))
					{ //�׷������� �õ�
						//ENTRANCE|name
						online.put(name, this);
						sendAllWaitMsg("say|"+ name +"|���� �����ϼ̽��ϴ�.");      
						sendAllWaitMsg("UPDATE|"+showUserList());
					}
					else if (msg.startsWith("ALLCHAT"))
					{//ALLCHAT|name|text
						sendAllWaitMsg("say|"+msgArr[0]+"|" +msgArr[1]);
					}
					else if(msg.startsWith("GAME_CAHT")){ //�ش� ����ڿ��� 1:1��ȭ ��û
						out.println("GAME_CHAT|"+msgArr[0]);
						break;
					} 
					else if(msg.startsWith("INVITE")){ //��ɾ� ����
						//INVITE|name|toname|result            
						String name = msgArr[0]; 
						String toName = msgArr[1].trim();
						if (!online.get(name).chatMode)
						{
							out.println("show|"+toName +"�Բ� ���ӽ�û�� �մϴ�. "); //show == �������� �ش� Ŭ���̾�Ʈ���� ������ �޼���
							if(online.containsKey(toName) && !online.get(toName).chatMode)//����üũ
							{ 
								//req_PvPchat|��û��|������|�޽��� .... ���
								//req_PvPchat|�޽��� .... �� ����
								online.get(toName).out.println("R_PvP| "+ name + "���� �����ʴ밡 �Խ��ϴ�.");//name���� ���� ���ӽ�û�Դٰ� toname���� ����  
								toNameTmp = toName;
								online.get(toNameTmp).toNameTmp = name;
							}
							else//������ ���Ӹ���϶�
							{
								out.println("show| �ش� ������ ���������ʰų� ������ 1:1��ȭ�� �Ҽ����� �����Դϴ�.");
								online.remove(name);
								sendAllWaitMsg("say|" + name+"|���� �����̽��ϴ�.");
								sendAllWaitMsg("UPDATE|" + showUserList());
								out.println("");
							}

						}
					}
					else if (msg.startsWith("RESULT")) 
					{
						resultData = msg.split("\\|");
						command();
					}
					else if(msg.startsWith("PvPchat")){ //1:1��ȭ��û ��������� ���� ó��
						//PvPchat|name|result                       
						String result = msgArr[1];                             
						if(result.equals("yes")){                              
							//chatMode = true;
							System.out.println(toNameTmp);
							online.get(name).chatMode=true;
							online.get(toNameTmp).chatMode = true;
							//System.out.println(toNameTmp);
							//gameIn(toNameTmp, name);
							sendAllWaitMsg("UPDATE|" + showUserList());
									

						}
						else /*(r.equals("no"))*/
						{
							online.get(toNameTmp).out.println("show|"+name+" �Բ��� ������ �����ϼ̽��ϴ�.");
						}                      

					}
					else if(msg.startsWith("PVP_say")){ //��ȭ���� ����
						if(chatMode)
						{
							//PVP_say|fromname|toname|��ȭ����
							System.out.println(name +"|" + msgArr[1]  + "[" +name +"]"+ msgArr[2]);
							sendPvPMsg(name, msgArr[1], "[" +name +"]"+ msgArr[2]);
							//��½�Ʈ������ ������.
						}      
					}
					else if(msg.startsWith("EXIT_ROOM"))//���ӳ�����
					{   
						if(chatMode){
							//gameOut();
							chatMode = false; //1:1��ȭ��� ����
							online.get(toNameTmp).chatMode=false; //���浵 1:1��ȭ��� ����							
							sendAllWaitMsg("UPDATE|" + showUserList());
						}
						break;
					}         
					else if(msg.startsWith("EXIT_SERVER")){ //���� 
						online.remove(name);
						System.out.print(name);
						sendAllWaitMsg("say|" + name+"|���� �����̽��ϴ�.");
						sendAllWaitMsg("UPDATE|" + showUserList());
						out.println("");
						break;
					}
					else if(msg.startsWith("PLAYER"))
					{
						playerData = msg.split("\\|");

					}
					//------------------------------------------------- �޼��� ó��


				}//while()---------
			}catch(Exception e){
				System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
				//e.printStackTrace();
			}finally{
				//���ܰ� �߻��Ҷ� ����. �ؽ��ʿ��� �ش� ������ ����.
				//���� �����ϰų� ������ java.net.SocketException: ���ܹ߻�
				if(clientMap!=null)
				{
					clientMap.remove(name);
					sendAllWaitMsg("<" + name + ">" + "���� �����ϼ̽��ϴ�.");

				}              
			}
		}//run()------------
	}//class MultiServerRec-------------
	//////////////////////////////////////////////////////////////////////
}