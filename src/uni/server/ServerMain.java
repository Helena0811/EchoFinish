/*
 * ����ĳ����
 * ���� Ŭ���̾�Ʈ�� ������ ��Ʈ�� �θ� �� ������ ���
 * */
package uni.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener{
	JPanel p_north; 
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	ServerSocket server;
	Thread thread;	// ���� � ������
 
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("����");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		setBounds(600,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	// ���� ����
	public void startServer(){
		
		// ��ư ��Ȱ��ȭ
		bt_start.setEnabled(false);
		
		try {
			// ���� �غ�
			port=Integer.parseInt(t_port.getText());
			server=new ServerSocket(port);
			
			area.append("���� ����\n");
			
			// server.accept();	�� �����尡 �ؾ� �� �۾�, ���� ������X
			
			// ������ ������ ������ �޸𸮿� �ø���
			thread=new Thread(){
				public void run() {
				
					try {
						/*-------------------------------------------------------*/
						/* ����� �� Ŭ���̾�Ʈ�� ���Ӹ� ����
						// Ŭ���̾�Ʈ�� ������ ����
						Socket socket=server.accept();
						// �������� ip ���ϱ�
						String ip=socket.getInetAddress().getHostAddress();
						area.append("ip : "+ip+" ������ �߰�\n");
						*/
						/*-------------------------------------------------------*/
						// ����� ��� ����� �Ѹ��𾿸� �ϰ� ������ ��
						// �� Ŭ���̾�Ʈ�� �ν��Ͻ��� ����� ������ ������ ������ �ְ� ���� -> ������(���� ���������� �����)
						/*
						while(true){
							// Ŭ���̾�Ʈ�� ������ ����
							Socket socket=server.accept();
							// �������� ip ���ϱ�
							String ip=socket.getInetAddress().getHostAddress();
							area.append("ip : "+ip+" ������ �߰�\n");
							
							// ��ȭ�� ���� ��Ʈ��
							BufferedReader buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
							BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
							
							String msg=buffr.readLine();	// Ŭ���̾�Ʈ�� ������ ������ ���� ��� ����
							area.append(msg+"\n");
						}
						*/
						while(true){
							// Ŭ���̾�Ʈ�� ������ ����(���Ӹ� ����, ��ȭ�� Thread�� ����)
							Socket socket=server.accept();
							// �������� ip ���ϱ�
							String ip=socket.getInetAddress().getHostAddress();
							area.append("ip : "+ip+" ������ �߰�\n");
							
							// �����ڸ��� �ƹ�Ÿ�� �������ְ�, ��ȭ�� ���� �� �ֵ��� ����
							Avatar avatar=new Avatar(socket, area);
							avatar.start();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			// ������ ����
			thread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		startServer();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}