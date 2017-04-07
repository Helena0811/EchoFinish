package echo.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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
	
	ServerSocket server;	// 접속 감지용 소켓
	
	Thread thread;			// 서버 가동용 쓰레드
	
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north=new JPanel();
		t_port=new JTextField(Integer.toString(port),15);
		bt_start=new JButton("접속");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		// 윈도우 창이 여러개 생성되므로 위치를 위해 setBounds()이용
		setBounds(600, 100, 300, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	// 접속 버튼을 누르면 서버 생성 및 연결
	public void startServer(){
		
		// 서버 가동중에 또 다시 버튼을 눌러 여러 쓰레드가 실행되는 것을 방지
		bt_start.setEnabled(false);
		
		// Checked Exception		문법상 오류가 없지만 에러 발생 가능성이 있는 예외, compile time에 고려
		// RunTime Exception	강요하지 않는 예외, runtime 시 고려
		// 런타임 예외, 컴파일 전에 강제 예외 처리가 되지 않음. 방치됨!\
		/*
		 * 예외 종류
		 * 1. Checked Exception
		 * 예외 처리 강요
		 * 2. RunTime Exception(=Unchecked Exception)
		 * 예외 처리를 강요하지 X
		 * */
		try {
			port=Integer.parseInt(t_port.getText());
			server=new ServerSocket(port);	// 서버 생성
			
			area.append("서버 준비 완료\n");
			
			// 가동(소켓 반환됨)
			Socket socket=server.accept();
			area.append("서버 가동중\n");
			
			// 클라이언트는 대화를 나누기 위해 접속한 것이므로, 접속이 되는 순간 스트림을 얻자
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			String data;
			
			while(true){
				data=buffr.readLine();	// 클라이언트의 메시지 받기
				area.append("클라이언트 : "+data+"\n");
				buffw.write(data+"\n");	// 보내기
				buffw.flush();			// 버퍼 비우기
			}
		
			/*
			 * 실행부라 불리는 메인 쓰레드는 절대 무한 루프나 대기, 지연 상태에 빠져서는 안됨!!!
			 * 실행부는 사용자들의 이벤트 감지 및 프로그램 운영이 목적
			 * 무한 루프나 대기 상태에 빠지면 본연의 역할을 할 수 없음
			 * 
			 * ex) 스마트폰 개발 분야에서는 이러한 코드는 이미 컴파일 타임에서 에러가 발생함
			 * */
			
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "포트 번호에 제대로 된 숫자를 넣어주세요.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * 실행부라 불리는 메인 쓰레드는 절대 무한 루프나 대기, 지연 상태에 빠져서는 안됨!!!
	 * 실행부는 사용자들의 이벤트 감지 및 프로그램 운영이 목적
	 * 무한 루프나 대기 상태에 빠지면 본연의 역할을 할 수 없음
	 * 
	 * ex) 스마트폰 개발 분야에서는 이러한 코드는 이미 컴파일 타임에서 에러가 발생함
	 * */
	public void actionPerformed(ActionEvent e) {

		// 내부 익명 클래스로 쓰레드 구현
		thread=new Thread(){
			public void run() {
				startServer();
			}
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}

}
