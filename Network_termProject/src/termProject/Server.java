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


/*콘솔 멀티채팅 서버 프로그램*/
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
		//Collections.synchronizedMap(data); //해쉬맵 동기화 설정.
		new HashMap<String, ServerRecThread>();
		online = new HashMap<String, ServerRecThread>();
		Collections.synchronizedMap(online); //해쉬맵 동기화 설정.

		//data.put("on", online);

	}//생성자----

	/**서버 시작 */
	public void init(){
		try{
			serverSocket = new ServerSocket(9997); //9999포트로 서버소켓 객체생성.
			//fromGame = new ServerSocket(9998);
			System.out.println("##서버가 시작되었습니다.");

			while(true){ //서버가 실행되는 동안 클라이언트들의 접속을 기다림.
				socket = serverSocket.accept(); //클라이언트의 접속을 기다리다가 접속이 되면 Socket객체를 생성.
				//gameSocket = fromGame.accept();
				System.out.println(socket.getInetAddress()+":"+socket.getPort()); //클라이언트 정보 (ip, 포트) 출력

				Thread msr = new ServerRecThread(socket); //쓰레드 생성.
				msr.start(); //쓰레드 시동.
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
			System.out.println("예외:"+e);
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
				System.out.println("예외:"+e);
			}
		}
	}
	/** 대기실에 있는 클라이언트들에게 메시지를 전달. */
	public void sendAllWaitMsg(String msg){
		//HashMap<String, ServerRecThread> one  = data.get("on");//on이라는 key를 가진 해시맵을 one 해시맵에 저장 == 지금 접속한 사용자 다 저장      
		Iterator<String> it_hash = online.keySet().iterator(); //key를 다 받아와서 iterator 객체로 바꿔주는거임
		while(it_hash.hasNext()){//키가 존재할때까지       
			try
			{
				//System.out.println(it_hash);
				ServerRecThread st = online.get(it_hash.next()); //it_hash.next()키를 가진 스레드 저장 
				if(!st.chatMode)//게임모드가 아닌사람에게만
				{
					st.out.println(msg);
				}              
			}
			catch(Exception e)
			{
				System.out.println("예외:"+e);
			}
		}
	}//sendAllWaitMsg()-----------

	/**1:1 게임방 대화*/
	public void sendPvPMsg(String fromName, String toName, String msg){
		//fromname|toname|대화내용
		ServerRecThread st1 = online.get(fromName);
		ServerRecThread st2 = online.get(toName); //it_hash.next()키를 가진 스레드 저장 
		st1.out.println("GAME_CAHT|" + msg);
		st2.out.println("GAME_CAHT|" + msg);

	}


	//sendPvPMsg()-----------

	//main메서드
	public static void main(String[] args) 
	{
		Server ms = new Server(); //서버객체 생성.
		ms.init();//실행.
	}//main()------  

	////////////////////////////////////////////////////////////////////////
	//----// 내부 클래스 //--------//

	// 클라이언트로부터 읽어온 메시지를 다른 클라이언트(socket)에 보내는 역할을 하는 메서드

	class ServerRecThread extends Thread {

		Socket socket;

		String name=""; //이름 저장
		String loc="";  //지역 저장
		String toNameTmp = null;//1:1대화 상대  
		String fileServerIP; //파일서버 아이피 저장
		String filePath; //파일 서버에서 전송할 파일 패스 저장.
		boolean chatMode = false; //1:1 게임모드 여부
		int state = 0;
		private BufferedReader in;
		private PrintWriter out;

		//생성자.
		public ServerRecThread(Socket socket){
			this.socket = socket;
			try
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
			}
			catch(Exception e)
			{
				System.out.println("ServerRecThread 생성자 예외:"+e);
			}
		}// -------------------------------------생성자

		/**접속된 유저리스트  문자열로 반환*/    
		public String showUserList(){
			StringBuilder output = new StringBuilder();
			Iterator it = online.keySet().iterator(); //접속중인 유저 아이디들의 집합
			while(it.hasNext()){ //반복하면서 사용자이름을 StringBuilder에 추가
				try{      
					String key= (String) it.next();
					output.append(key +" ");                  
				}catch(Exception e){
					System.out.println("예외:"+e);
				}
			}//while--------
			return output.toString();
		}//showUserList()-----------

		/**메시지 파서 */    
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

				Class.forName("com.mysql.jdbc.Driver"); // JDBC 드라이버 로드
				System.out.println("드라이버 연결 성공!" + mode);

				conn = DriverManager.getConnection(url);
				System.out.println("데이터베이스 연결 성공!");

				stmt = conn.createStatement();

				String useXproject = "use member";
				stmt.executeUpdate(useXproject);

				String search1 = "select * from user where username like '" + me +"';";
				//username으로 받은 데이터가 rs에 저장되고 그 내용을 확인!
				rs = stmt.executeQuery(search1);

				if(rs.next()) {
					my_win = rs.getString("win");
					my_lose = rs.getString("lose");
				}
				rate = my_win;
				rate = rate.concat("승/");
				rate = rate.concat(my_lose+"패");

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

				Class.forName("com.mysql.jdbc.Driver"); // JDBC 드라이버 로드
				System.out.println("드라이버 연결 성공!" + mode);

				conn = DriverManager.getConnection(url);
				System.out.println("데이터베이스 연결 성공!");

				stmt = conn.createStatement();

				String useXproject = "use member";
				stmt.executeUpdate(useXproject);

				if(mode == LOGINCHECK) {
					col = "username";
					what = username;
					//username으로 검색하기
					String search = "select * from user where username like '" + loginData[1] +"';";
					//username으로 받은 데이터가 rs에 저장되고 그 내용을 확인!
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
		public void run(){ //쓰레드를 사용하기 위해서 run()메서드 재정의
			HashMap<String, ServerRecThread> clientMap=null;   //현재 클라이언트가 저장되어있는 해쉬맵       
			try{  

				while(in!=null){ //입력스트림이 null이 아니면 반복.
					String msg = in.readLine(); //입력스트림을 통해 읽어온 문자열을 msg에 할당. 
					System.out.println(msg);
					String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
					name = msgArr[0]; //넘어온 대화명은 전역변수 name에 저장
					//메세지 처리 ----------------------------------------------

					if (msg.startsWith("LOGINCHECK")) // 아이디 패스워드 입력하고 로그인버튼 누르면 R_LOGIN서버에 보냄 로그인 할 수 있는지 체크
					{
						mode = LOGINCHECK;
						loginData = msg.split("\\|");
						command();

						//디비랑 연동했는데 아이디 패스워드가 다르면 
						//out.println("logon#no|");
					}

					else if(msg.startsWith("CREATEACCOUNT"))
					{
						mode = CREATEACCOUNT;
						accountData = msg.split("\\|");
						command();
					}

					else if(msg.startsWith("ENTRANCE"))
					{ //그룹입장을 시도
						//ENTRANCE|name
						online.put(name, this);
						sendAllWaitMsg("say|"+ name +"|님이 입장하셨습니다.");      
						sendAllWaitMsg("UPDATE|"+showUserList());
					}
					else if (msg.startsWith("ALLCHAT"))
					{//ALLCHAT|name|text
						sendAllWaitMsg("say|"+msgArr[0]+"|" +msgArr[1]);
					}
					else if(msg.startsWith("GAME_CAHT")){ //해당 사용자에게 1:1대화 요청
						out.println("GAME_CHAT|"+msgArr[0]);
						break;
					} 
					else if(msg.startsWith("INVITE")){ //명령어 전송
						//INVITE|name|toname|result            
						String name = msgArr[0]; 
						String toName = msgArr[1].trim();
						if (!online.get(name).chatMode)
						{
							out.println("show|"+toName +"님께 게임신청을 합니다. "); //show == 서버에서 해당 클라이언트에게 보내는 메세지
							if(online.containsKey(toName) && !online.get(toName).chatMode)//유저체크
							{ 
								//req_PvPchat|신청자|응답자|메시지 .... 취소
								//req_PvPchat|메시지 .... 로 변경
								online.get(toName).out.println("R_PvP| "+ name + "에게 게임초대가 왔습니다.");//name으로 부터 게임신청왔다고 toname한테 말함  
								toNameTmp = toName;
								online.get(toNameTmp).toNameTmp = name;
							}
							else//상대방이 게임모드일때
							{
								out.println("show| 해당 유저가 존재하지않거나 상대방이 1:1대화를 할수없는 상태입니다.");
								online.remove(name);
								sendAllWaitMsg("say|" + name+"|님이 나가셨습니다.");
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
					else if(msg.startsWith("PvPchat")){ //1:1대화신청 수락결과에 대한 처리
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
							online.get(toNameTmp).out.println("show|"+name+" 님께서 게임을 거절하셨습니다.");
						}                      

					}
					else if(msg.startsWith("PVP_say")){ //대화내용 전송
						if(chatMode)
						{
							//PVP_say|fromname|toname|대화내용
							System.out.println(name +"|" + msgArr[1]  + "[" +name +"]"+ msgArr[2]);
							sendPvPMsg(name, msgArr[1], "[" +name +"]"+ msgArr[2]);
							//출력스트림으로 보낸다.
						}      
					}
					else if(msg.startsWith("EXIT_ROOM"))//게임나갈때
					{   
						if(chatMode){
							//gameOut();
							chatMode = false; //1:1대화모드 해제
							online.get(toNameTmp).chatMode=false; //상대방도 1:1대화모드 해제							
							sendAllWaitMsg("UPDATE|" + showUserList());
						}
						break;
					}         
					else if(msg.startsWith("EXIT_SERVER")){ //종료 
						online.remove(name);
						System.out.print(name);
						sendAllWaitMsg("say|" + name+"|님이 나가셨습니다.");
						sendAllWaitMsg("UPDATE|" + showUserList());
						out.println("");
						break;
					}
					else if(msg.startsWith("PLAYER"))
					{
						playerData = msg.split("\\|");

					}
					//------------------------------------------------- 메세지 처리


				}//while()---------
			}catch(Exception e){
				System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
				//e.printStackTrace();
			}finally{
				//예외가 발생할때 퇴장. 해쉬맵에서 해당 데이터 제거.
				//보통 종료하거나 나가면 java.net.SocketException: 예외발생
				if(clientMap!=null)
				{
					clientMap.remove(name);
					sendAllWaitMsg("<" + name + ">" + "님이 퇴장하셨습니다.");

				}              
			}
		}//run()------------
	}//class MultiServerRec-------------
	//////////////////////////////////////////////////////////////////////
}