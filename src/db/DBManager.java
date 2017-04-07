/*
 * 1. 데이터베이스 계정 정보를 DB연동이 필요한 클래스에 중복해서 기재하지 않아도 됨!
 * 2. 인스턴스의 갯수를 한 개만 두고 사용(SingleTon)
 * -> 어플리케이션 가동 중 생성되는 Connection 객체를 하나로 통일
 * */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	
	static private DBManager instance;
	
	Connection con;
	
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	private String user="batman";
	private String password="1234";
	
	private DBManager(){
		/*
		 * 1. driver 로드
		 * 2. 접속
		 * 3. 쿼리문 수행
		 * 4. 접속 종료
		 * */
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url, user, password);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public DBManager getInstance() {
		if(instance==null){
			instance=new DBManager();
		}
		return instance;
	}

	public Connection getConnection() {
		return con;
	}

	public void disConnect(Connection con){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
