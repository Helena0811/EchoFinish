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
	
	Socket socket;	// ��ȭ�� ����, Stream�� �ʿ���
	String ip;
	
	BufferedReader buffr;
	BufferedWriter buffw;
	
	// ������� ���� ������ ��� ����� ������
	ClientThread ct;
	
	public ClientMain() {
		
		manager=DBManager.getInstance();
		
		p_north=new JPanel();
		choice=new Choice();
		t_port=new JTextField(Integer.toString(port),10);
		bt_connect=new JButton("����");
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
				  * ������ �̿� ���ϸ�
				  * String msg=t_input.getText();
				  * send(msg);
				  * 
				  * // ������
				  * ct.send(t_input.getText());
				  * // �Է��� �۾� �����
				  * t_input.setText("");
				  * 
				  * // ���� ġ�� �ٽ� �޾ƿ��� ���� �ƴ϶� ������ �޾ƿ� 
				  * listen();
				  * 
				 */
				 
				 // ������(ClientThread�� ����)
				 ct.send(t_input.getText());
				 
				 // �Է��� �۾� �����
				 t_input.setText("");
	 
			 }
			}
		});
		
		setBounds(950, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	// �����ͺ��̽� ��������
	public void loadIP(){
		Connection con=manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select * from chat order by chat_id asc";
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			// rs�� ������ ����Ǹ� ����� ���̹Ƿ� DB���� �о�� ������ collection framework�̳� DTO�� ��Ƴ���
			// ���ڵ� �� �Ǹ��� �̸�, IP�ּҸ� ���� DTO, ���ڵ� ��ü�� list
			// rs�� ��� �����͸� dto�� �ű�� ���� = Mapping(ex) MyBatis, Hybernate)
			while(rs.next()){
				// ���ڵ� �ϳ�
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
			// ���� ����
			manager.disConnect(con);
		}
	}
	
	// ������ ���� �õ�
	public void connect(){
		// ip�� port�� ������ �̸� socket�� �غ��س��� ��!
		// ���� ���� �� ������ �߻���
		try {
			port=Integer.parseInt(t_port.getText());
			socket=new Socket(ip, port);
			
			// ��ȭ�� ������ ���� ��Ʈ�� ���
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// ClientThread�� �ǽð� û�� ����
			// ���� ���Ǵ� socket�� ���� �޽����� ����� area�� �μ��� �ֱ�
			ct=new ClientThread(socket, area);
			
			// ClientThread ����
			ct.start();
			
			// ������ �޼ҵ嵵 �ܺ� ������(����)�� ClientThread�� ����
			ct.send("�ȳ�?");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// �̸��� �����ϸ� ip ���
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int index=ch.getSelectedIndex();
		Chat chat=list.get(index);
		
		this.setTitle(chat.getIp());
		
		ip=chat.getIp();	// ������� ip�� ����
	}
	
	// ���� ��ư�� ������ ����
	public void actionPerformed(ActionEvent e) {
		connect();
		
	}
	
	public static void main(String[] args) {
		new ClientMain();

	}
}
