package com.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class Server {
	public ServerSocket server = null;
	public  Socket socket;
	
	ArrayList<Client> clientList = new ArrayList<Client>();
	
	public Server() throws IOException{
		server = new ServerSocket(6000);
		System.out.println(server+"服务器已启动......");
		while(true){ 
			socket = server.accept();
			Client client = new Client(socket);	
			clientList.add(client);
			System.out.println(socket+"建立连接");
			client.start();
		} 
		
	}
	class Client extends Thread{
		private Socket client;
		private BufferedReader in;
		private PrintWriter out;
		
		private int id;
		private String name;
		private String password;
		private String type;
		private String msg;
		private String sendMsg;
		
		//主机号
		public String url = "jdbc:mysql://172.18.159.197:3306/magic";//
		// MySQL配置时的用户名
		public String user = "1a781";
		// MySQL配置时的密码
		public String password1 = "r4idn3";
		
		Client(Socket socket){
			client = socket;
			try{ 
				in=new BufferedReader(new InputStreamReader(client.getInputStream())); 
				out=new PrintWriter(client.getOutputStream());
			}catch(IOException e){ 
					e.printStackTrace();
			}
		}
		
		public void run(){
			
			try {
				msg="";
				sendMsg="";
				id = 0;
				
				while(true){
					    msg=in.readLine();
					
						String s[] = msg.split(":");
					
						if(s.length==3){
							type = s[0]; name = s[1]; password = s[2];
							if(type.equals("L")){
								if(loginCheck()){
									sendMsg = "L:1:"+id;
									send(sendMsg);
								}
								else{
									sendMsg = "L:0:"+id;
									send(sendMsg);
								
								}
								
							
							}
							else{
								if(regCheck()){
									sendMsg = "R:1:"+id;
									send(sendMsg);
								
								}
								else{
									sendMsg = "R:0:"+id;
									send(sendMsg);
								
								}
							
							}
						}
					}
				
			} catch (Exception e) {
				try {
					socket.close();
					in.close();
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			    clientList.remove(this);
			    System.out.println(socket+"关闭连接");
		    }
			
		}
		
		
		private boolean loginCheck(){
			
			try {
		         Class.forName("com.mysql.jdbc.Driver"); 
		     	} catch (Exception e) {
		     		e.printStackTrace();
		     	}
		     try {
		    	// 加载驱动程序
					Class.forName("com.mysql.jdbc.Driver");
					
					Connection connection = (Connection)DriverManager.getConnection(url, user, password1);
					
					if(!connection.isClosed())
						System.out.println("Succeeded connecting to the Database!");
					// statement用来执行SQL语句
					Statement statement = (Statement)connection.createStatement();
					
					// 要执行的SQL语句
					String sql = "select password from user where name = '"+name+"'";
					// 执行SQL语句并返回结果集
					ResultSet rs = statement.executeQuery(sql);
					while(rs.next()){
						
						if(rs.getString("password").equals(password)){
											
							  
							  
							  String sql1 = "select id from user where name = '"+name+"'";
							  
							  ResultSet rs1 = statement.executeQuery(sql1);
								while(rs1.next()){
													
									id=rs1.getInt("id");
									
								}	
								rs1.close();
								rs.close();
								statement.close();
								// 关闭连接
								connection.close();
							  return true;
							
						}	
					}
					// 关闭结果集
					rs.close();
					
					statement.close();
					// 关闭连接
					connection.close();
					
					return false;
					
		     		}catch(ClassNotFoundException e) {
		     			System.out.println("Sorry,can`t find the Driver!");
		     			e.printStackTrace();
		     		} catch(SQLException e) {
		     			e.printStackTrace();
		     		} catch(Exception e) {
		     			e.printStackTrace();
		     		}
			return false;
		}
		private boolean regCheck(){

			try {
		         Class.forName("com.mysql.jdbc.Driver"); 
		     	} catch (Exception e) {
		     		e.printStackTrace();
		     	}
		     try {
					// 加载驱动程序
					Class.forName("com.mysql.jdbc.Driver");
					
					Connection connection = (Connection)DriverManager.getConnection(url, user, password1);
					
					if(!connection.isClosed())
						System.out.println("Succeeded connecting to the Database!");
					
					Statement statement = (Statement)connection.createStatement();
					
					// 要执行的SQL语句
					String sql = "select * from user where name = '"+name+"'";
					// 执行SQL语句并返回结果集
					ResultSet rs = statement.executeQuery(sql);
					if(rs.next()){	
							rs.close();
							statement.close();
							// 关闭连接
							connection.close();
							return false;	
							
					}
					else{
						register(name,password,statement);
						// 关闭结果集
						rs.close();
						
						statement.close();
						// 关闭连接
						connection.close();
						return true;
					}
					
					
		     		} catch(ClassNotFoundException e) {
		     			System.out.println("Sorry,can`t find the Driver!");
		     			e.printStackTrace();
		     		} catch(SQLException e) {
		     			e.printStackTrace();
		     		} catch(Exception e) {
		     			e.printStackTrace();
		     		}
				
			return false;
		}
		public void send(String str) throws IOException{
			out.println(str); 
			out.flush(); 
		}
		
		public synchronized void register(String nam,String pw,Statement statement) throws SQLException{
			
			String sql1 = "insert into user(name,password) values('"+nam+"','"+pw+"')";
			statement.executeUpdate(sql1);
			
		}
		
	}
	public static void main(String[] args)throws IOException{
	//	new Server();
		new Server().server.close();
		
	}
	

}
