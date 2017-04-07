/*
 * ���ϰ� ��Ʈ���� ������ 1���� �� ���, �����ڸ��� ���ϰ� ��Ʈ���� Ż���Ͽ� ��Ż���� ������! -> ���ϰ� ��Ʈ���� �������� ����!
 * �ذ�å) �� ����ڸ��� �ڽŸ��� ���ϰ� ��Ʈ���� �ʿ��ϹǷ�, �����ڸ��� �ν��Ͻ��� �����Ͽ� �� �ν��Ͻ� ���� ���ϰ� ��Ʈ���� ��������!
 * -> ������ ������ ���۵� �ʿ��ϹǷ� ������� ����!!!
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
		
		// ��ȭ�� ���� ��Ʈ��
		try {
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���(������ ���ڸ��� ������ ��!)
	public void listen(){
		String msg=null;
		try {
			msg=buffr.readLine();
			area.append(msg+"\n");
			send(msg);					// ���ڸ��� ��ٷ� ������
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���ϱ�
	public void send(String msg){
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ���� ������� ���Ӿ��� ��� ���ؾ� �ϹǷ� ���ѷ��� �ֱ�
	// ���α׷��� ����ɶ����� ������ Ŭ���̾�Ʈ�� �޽����� �޾ƿ��� �ٽ� ������!
	public void run() {
		while(true){
			listen();
		}
	}
}