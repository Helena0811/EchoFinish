package echo.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DBManager;

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_north;
	Choice choice;
	JTextField t_port;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	JTextField t_input;
	
	int port=7777;
	
	DBManager manager;
	
	ArrayList<Chat> list=new ArrayList<Chat>();
	
	Socket socket;	// 대화용 소켓, Stream도 필요함
	String ip;
	
	BufferedReader buffr;
	BufferedWriter buffw;
	
	// 실행부인 메인 쓰레드 대신 실행될 쓰레드
	ClientThread ct;
	
	public ClientMain() {
		
		manager=DBManager.getInstance();
		
		p_north=new JPanel();
		choice=new Choice();
		t_port=new JTextField(Integer.toString(port),10);
		bt_connect=new JButton("연결");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		t_input=new JTextField(20);
		
		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north,BorderLayout.NORTH);
		add(scroll);
		add(t_input,BorderLayout.SOUTH);
		
		loadIP();
		
		for(int i=0; i<list.size(); i++){
			choice.add(list.get(i).getName());
		}
		bt_connect.addActionListener(this);
		choice.addItemListener(this);
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
			 int key=e.getKeyCode();
			 if(key==KeyEvent.VK_ENTER){
				 /*
				  * 쓰레드 이용 안하면
				  * String msg=t_input.getText();
				  * send(msg);
				  * 
				  * // 보내기
				  * ct.send(t_input.getText());
				  * // 입력한 글씨 지우기
				  * t_input.setText("");
				  * 
				  * // 엔터 치면 다시 받아오는 것이 아니라 언제나 받아옴 
				  * listen();
				  * 
				 */
				 
				 // 보내기(ClientThread가 수행)
				 ct.send(t_input.getText());
				 
				 // 입력한 글씨 지우기
				 t_input.setText("");
	 
			 }
			}
		});
		
		setBounds(950, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	// 데이터베이스 가져오기
	public void loadIP(){
		Connection con=manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select * from chat order by chat_id asc";
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			// rs는 접속이 종료되면 사라질 것이므로 DB에서 읽어온 정보를 collection framework이나 DTO로 담아놓기
			// 레코드 한 건마다 이름, IP주소를 가진 DTO, 레코드 전체는 list
			// rs의 모든 데이터를 dto로 옮기는 과정 = Mapping(ex) MyBatis, Hybernate)
			while(rs.next()){
				// 레코드 하나
				Chat dto=new Chat();
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// 접속 끊기
			manager.disConnect(con);
		}
	}
	
	// 서버에 접속 시도
	public void connect(){
		// ip와 port를 가지고 미리 socket을 준비해놔야 함!
		// 소켓 생성 시 접속이 발생함
		try {
			port=Integer.parseInt(t_port.getText());
			socket=new Socket(ip, port);
			
			// 대화를 나누기 전에 스트림 얻기
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// ClientThread가 실시간 청취 시작
			// 현재 사용되는 socket과 받은 메시지를 출력할 area를 인수로 넣기
			ct=new ClientThread(socket, area);
			
			// ClientThread 실행
			ct.start();
			
			// 보내는 메소드도 외부 쓰레드(동생)인 ClientThread가 수행
			ct.send("안녕?");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 이름을 선택하면 ip 출력
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int index=ch.getSelectedIndex();
		Chat chat=list.get(index);
		
		this.setTitle(chat.getIp());
		
		ip=chat.getIp();	// 멤버변수 ip에 대입
	}
	
	// 접속 버튼을 누르면 접속
	public void actionPerformed(ActionEvent e) {
		connect();
		
	}
	
	public static void main(String[] args) {
		new ClientMain();

	}
}
