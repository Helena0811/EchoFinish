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
	
	ServerSocket server;	// ���� ������ ����
	
	Thread thread;			// ���� ������ ������
	
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north=new JPanel();
		t_port=new JTextField(Integer.toString(port),15);
		bt_start=new JButton("����");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		// ������ â�� ������ �����ǹǷ� ��ġ�� ���� setBounds()�̿�
		setBounds(600, 100, 300, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	// ���� ��ư�� ������ ���� ���� �� ����
	public void startServer(){
		
		// ���� �����߿� �� �ٽ� ��ư�� ���� ���� �����尡 ����Ǵ� ���� ����
		bt_start.setEnabled(false);
		
		// Checked Exception		������ ������ ������ ���� �߻� ���ɼ��� �ִ� ����, compile time�� ���
		// RunTime Exception	�������� �ʴ� ����, runtime �� ���
		// ��Ÿ�� ����, ������ ���� ���� ���� ó���� ���� ����. ��ġ��!\
		/*
		 * ���� ����
		 * 1. Checked Exception
		 * ���� ó�� ����
		 * 2. RunTime Exception(=Unchecked Exception)
		 * ���� ó���� �������� X
		 * */
		try {
			port=Integer.parseInt(t_port.getText());
			server=new ServerSocket(port);	// ���� ����
			
			area.append("���� �غ� �Ϸ�\n");
			
			// ����(���� ��ȯ��)
			Socket socket=server.accept();
			area.append("���� ������\n");
			
			// Ŭ���̾�Ʈ�� ��ȭ�� ������ ���� ������ ���̹Ƿ�, ������ �Ǵ� ���� ��Ʈ���� ����
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			String data;
			
			while(true){
				data=buffr.readLine();	// Ŭ���̾�Ʈ�� �޽��� �ޱ�
				area.append("Ŭ���̾�Ʈ : "+data+"\n");
				buffw.write(data+"\n");	// ������
				buffw.flush();			// ���� ����
			}
		
			/*
			 * ����ζ� �Ҹ��� ���� ������� ���� ���� ������ ���, ���� ���¿� �������� �ȵ�!!!
			 * ����δ� ����ڵ��� �̺�Ʈ ���� �� ���α׷� ��� ����
			 * ���� ������ ��� ���¿� ������ ������ ������ �� �� ����
			 * 
			 * ex) ����Ʈ�� ���� �о߿����� �̷��� �ڵ�� �̹� ������ Ÿ�ӿ��� ������ �߻���
			 * */
			
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "��Ʈ ��ȣ�� ����� �� ���ڸ� �־��ּ���.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * ����ζ� �Ҹ��� ���� ������� ���� ���� ������ ���, ���� ���¿� �������� �ȵ�!!!
	 * ����δ� ����ڵ��� �̺�Ʈ ���� �� ���α׷� ��� ����
	 * ���� ������ ��� ���¿� ������ ������ ������ �� �� ����
	 * 
	 * ex) ����Ʈ�� ���� �о߿����� �̷��� �ڵ�� �̹� ������ Ÿ�ӿ��� ������ �߻���
	 * */
	public void actionPerformed(ActionEvent e) {

		// ���� �͸� Ŭ������ ������ ����
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
