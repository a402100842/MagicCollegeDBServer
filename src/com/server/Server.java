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
		System.out.println(server+"������������......");
		while(true){ 
			socket = server.accept();
			Client client = new Client(socket);	
			clientList.add(client);
			System.out.println(socket+"��������");
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
		
		//������
		public String url = "jdbc:mysql://172.18.159.197:3306/magic";//
		// MySQL����ʱ���û���
		public String user = "1a781";
		// MySQL����ʱ������
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
			    System.out.println(socket+"�ر�����");
		    }
			
		}
		
		
		private boolean loginCheck(){
			
			try {
		         Class.forName("com.mysql.jdbc.Driver"); 
		     	} catch (Exception e) {
		     		e.printStackTrace();
		     	}
		     try {
		    	// ������������
					Class.forName("com.mysql.jdbc.Driver");
					
					Connection connection = (Connection)DriverManager.getConnection(url, user, password1);
					
					if(!connection.isClosed())
						System.out.println("Succeeded connecting to the Database!");
					// statement����ִ��SQL���
					Statement statement = (Statement)connection.createStatement();
					
					// Ҫִ�е�SQL���
					String sql = "select password from user where name = '"+name+"'";
					// ִ��SQL��䲢���ؽ����
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
								// �ر�����
								connection.close();
							  return true;
							
						}	
					}
					// �رս����
					rs.close();
					
					statement.close();
					// �ر�����
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
					// ������������
					Class.forName("com.mysql.jdbc.Driver");
					
					Connection connection = (Connection)DriverManager.getConnection(url, user, password1);
					
					if(!connection.isClosed())
						System.out.println("Succeeded connecting to the Database!");
					
					Statement statement = (Statement)connection.createStatement();
					
					// Ҫִ�е�SQL���
					String sql = "select * from user where name = '"+name+"'";
					// ִ��SQL��䲢���ؽ����
					ResultSet rs = statement.executeQuery(sql);
					if(rs.next()){	
							rs.close();
							statement.close();
							// �ر�����
							connection.close();
							return false;	
							
					}
					else{
						register(name,password,statement);
						// �رս����
						rs.close();
						
						statement.close();
						// �ر�����
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
