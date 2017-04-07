/*
 * Ŭ���̾�Ʈ ������ �ǽð� û�븦 �����ϰ� �ϴ� �ܺ� ������
 * ���� �����尡 �ƴ� ������ ���� �����带 �̿��Ͽ� �����带 Loop������ ����
 * 
 *  ������ �޴� ���� ��� ����
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
	
	//listen�� send�� �����ϰ� �Ϸ��� �ش� socket�� area�� �ʿ�
	public ClientThread(Socket socket, JTextArea area) {
		this.socket=socket;
		this.area=area;
		
		try {
			// ��Ʈ��
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ������(���ϱ�)
	public void send(String msg){
		try {
			buffw.write(msg);
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	// �ޱ�(���)
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
