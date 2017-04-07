/*
 * 유니캐스팅
 * 여러 클라이언트가 각각의 포트를 두며 한 서버로 통신
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
	Thread thread;	// 서버 운영 쓰레드
 
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("가동");
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
	
	// 서버 가동
	public void startServer(){
		
		// 버튼 비활성화
		bt_start.setEnabled(false);
		
		try {
			// 서버 준비
			port=Integer.parseInt(t_port.getText());
			server=new ServerSocket(port);
			
			area.append("서버 생성\n");
			
			// server.accept();	는 쓰레드가 해야 할 작업, 메인 쓰레드X
			
			// 서버를 가동할 쓰레드 메모리에 올리기
			thread=new Thread(){
				public void run() {
				
					try {
						/*-------------------------------------------------------*/
						/* 현재는 한 클라이언트의 접속만 감지
						// 클라이언트의 접속을 감지
						Socket socket=server.accept();
						// 접속자의 ip 구하기
						String ip=socket.getInetAddress().getHostAddress();
						area.append("ip : "+ip+" 접속자 발견\n");
						*/
						/*-------------------------------------------------------*/
						// 현재는 모든 사람이 한마디씩만 하고 나가게 됨
						// 각 클라이언트를 인스턴스로 만들어 각각의 정보를 가지고 있게 구현 -> 쓰레드(각각 독립적으로 수행됨)
						/*
						while(true){
							// 클라이언트의 접속을 감지
							Socket socket=server.accept();
							// 접속자의 ip 구하기
							String ip=socket.getInetAddress().getHostAddress();
							area.append("ip : "+ip+" 접속자 발견\n");
							
							// 대화를 나눌 스트림
							BufferedReader buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
							BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
							
							String msg=buffr.readLine();	// 클라이언트의 응답이 없으면 무한 대기 상태
							area.append(msg+"\n");
						}
						*/
						while(true){
							// 클라이언트의 접속을 감지(접속만 수행, 대화는 Thread가 수행)
							Socket socket=server.accept();
							// 접속자의 ip 구하기
							String ip=socket.getInetAddress().getHostAddress();
							area.append("ip : "+ip+" 접속자 발견\n");
							
							// 접속자마자 아바타를 생성해주고, 대화를 나눌 수 있도록 세팅
							Avatar avatar=new Avatar(socket, area);
							avatar.start();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			// 쓰레드 동작
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