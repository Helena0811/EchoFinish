/*
 * 클라이언트 측에서 실시간 청취를 가능하게 하는 외부 쓰레드
 * 메인 쓰레드가 아닌 개발자 정의 쓰레드를 이용하여 쓰레드를 Loop내에서 실행
 * 
 *  보내고 받는 것이 모두 가능
 * */
package uni.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ClientThread extends Thread{
	BufferedReader buffr;
	BufferedWriter buffw;
	
	Socket socket;
	JTextArea area;
	
	//listen과 send가 가능하게 하려면 해당 socket과 area가 필요
	public ClientThread(Socket socket, JTextArea area) {
		this.socket=socket;
		this.area=area;
		
		try {
			// 스트림
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 보내기(말하기)
	public void send(String msg){
		try {
			buffw.write(msg);
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	// 받기(듣기)
	public void listen(){
		String msg=null;
		try {
			msg=buffr.readLine();
			area.append(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		
	}
	
}
