package termProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
   public static void main(String[] args) throws Exception {
      ServerSocket listener1 = null;

      ExecutorService pool = Executors.newFixedThreadPool(500); // 2개의 thread를 만들 수 있는 메모리 준비
      try {
         listener1 = new ServerSocket(9999); // 서버 소켓 생성

         Socket player1 = null, player2 = null;

         while (true) {
            if (player1 == null) {
               player1 = listener1.accept();

               try {
                  PrintWriter writer1 = new PrintWriter(player1.getOutputStream());
                  writer1.println("wait");
                  writer1.flush();
               } catch (IOException e) {
                  e.printStackTrace();
               }

            } else {
               player2 = listener1.accept();
               try {
                  PrintWriter writer1 = new PrintWriter(player1.getOutputStream());
                  writer1.println("start");
                  writer1.flush();

                  PrintWriter writer2 = new PrintWriter(player2.getOutputStream());
                  writer2.println("start");
                  writer2.flush();

               } catch (IOException e) {
                  e.printStackTrace();
               }

               pool.execute(new Handler(player1, player2)); // listener가 client를 accept하면 해당 client의 thread 생성 후

               player1 = player2 = null;
            }
         } // 실행
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            if (listener1 != null) // 서버 소켓이 있다면
               listener1.close(); // 서버 소켓 닫기
         } catch (IOException e) {
         }
      }
   }

   private static class Handler implements Runnable {
      private Socket socket1;
      private Scanner in1;
      private PrintWriter out1;

      private Socket socket2;
      private Scanner in2;
      private PrintWriter out2;

      private int score1 = 0, score2 = 0;

      public Handler(Socket socket1, Socket socket2) {
         this.socket1 = socket1;
         this.socket2 = socket2;
      }

      public void run() {
         while (true) {
            try {
               in1 = new Scanner(socket1.getInputStream());
               out1 = new PrintWriter(socket1.getOutputStream(), true); // 출력 스트림 생성

               in2 = new Scanner(socket2.getInputStream()); // 입력 스트림 생성
               out2 = new PrintWriter(socket2.getOutputStream(), true); // 출력 스트림 생성

            } catch (IOException e1) {
               e1.printStackTrace();
            } // 입력 스트림 생성

            int r = (int) (Math.random() * 3);
            int c = (int) (Math.random() * 3);

            out1.println("newopen");
            out1.flush();
            out2.println("newopen");
            out2.flush();
            out1.println(r);
            out1.flush();
            out2.println(r);
            out2.flush();
            out1.println(c);
            out1.flush();
            out2.println(c);
            out2.flush();

            String input1 = in1.nextLine();
            String input2 = in2.nextLine();
            if (input1.equals("iclick") || input2.equals("iclick")) {
               if (input1.equals("iclick") == true) {
                  score1++;
                  out1.println("oppclk");
                  out2.println("oppclk");
                  out1.flush();
                  out2.flush();
                  continue;
               }

               else if (input2.equals("iclick") == true) {
                  score2++;
                  out1.println("oppclk");
                  out2.println("oppclk");
                  out1.flush();
                  out2.flush();
                  continue;
               }
            }

            if (input1.equals("finish") && input2.equals("finish")) {
               if (score1 > score2) {
                  out1.println("finish");
                  out2.println("finish");
                  out1.println(1);
                  out2.println(0);
                  out1.flush();
                  out2.flush();
               }

               else if (score1 < score2) {
                  out1.println("finish");
                  out2.println("finish");
                  out1.println(0);
                  out2.println(1);
                  out1.flush();
                  out2.flush();
               }

               else if (input1.equals("end") || input2.equals("end")) {
                  if (score1 > score2) {
                     out1.println("finish");
                     out2.println("finish");
                     out1.println(1);
                     out2.println(0);
                     out1.flush();
                     out2.flush();
                  }

                  else if (score1 < score2) {
                     out1.println("finish");
                     out2.println("finish");
                     out1.println(0);
                     out2.println(1);
                     out1.flush();
                     out2.flush();
                  }
               }

               else {
                  r = (int) Math.random() * 4;
                  c = (int) Math.random() * 4;
                  out1.println("renewopen");
                  out2.println("renewopen");
                  out1.println(r);
                  out2.println(r);
                  out1.println(c);
                  out2.println(c);
                  out1.flush();
                  out2.flush();

               }
            }

            /// client 접속 해제 시
            try {
               socket1.close();
               socket2.close();
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }
   }
}