/*
 * 소켓과 스트림을 서버에 1개만 둔 결과, 접속자마다 소켓과 스트림을 탈취하여 쟁탈전이 벌어짐! -> 소켓과 스트림이 유지되지 않음!
 * 해결책) 각 사용자마다 자신만의 소켓과 스트림이 필요하므로, 접속자마다 인스턴스를 생성하여 그 인스턴스 내에 소켓과 스트림을 보관하자!
 * -> 별도의 독립된 동작도 필요하므로 쓰레드로 정의!!!
 * */
package uni.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class Avatar extends Thread{
	Socket socket;
	JTextArea area;
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public Avatar(Socket socket, JTextArea area) {
		this.socket=socket;
		this.area=area;
		
		// 대화를 나눌 스트림
		try {
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 듣기(서버는 듣자마자 보내야 함!)
	public void listen(){
		String msg=null;
		try {
			msg=buffr.readLine();
			area.append(msg+"\n");
			send(msg);					// 듣자마자 곧바로 보내기
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 말하기
	public void send(String msg){
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 서버 쓰레드는 끊임없이 듣고 말해야 하므로 무한루프 주기
	// 프로그램이 종료될때까지 끝없이 클라이언트의 메시지를 받아오고 다시 보낸다!
	public void run() {
		while(true){
			listen();
		}
	}
}
