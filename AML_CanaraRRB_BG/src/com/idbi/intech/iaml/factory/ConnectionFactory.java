package com.idbi.intech.iaml.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.idbi.intech.aml.util.AESAlgorithmBG;

public class ConnectionFactory {
	
	public static Connection makeConnectionAMLBPM(String connectionUrl,
			String username, String password) throws SQLException {
		Connection connectionAML = null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			connectionAML = DriverManager.getConnection(connectionUrl,
					username, password);
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connectionAML;
	}
	
	/*public static Connection makeConnectionAMLLive() throws SQLException {
		Connection connection = null;
		String connCred=null,strConnection=null,ip=null,port=null,dbname=null,username=null,password=null;
		try {
			FileReader fr = new FileReader(new File(
					"D:\\AML\\AML_PROCESS\\AML_PASS.txt"));
			BufferedReader br = new BufferedReader(fr);
			AML_PasswordBaseEncryption obj = new AML_PasswordBaseEncryption();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			while ((connCred = br.readLine()) != null) {
				String pass_info[] = obj.decrypt(connCred).split("~");
				
				strConnection = pass_info[0];
				ip = pass_info[1];
				port = pass_info[2];
				dbname = pass_info[3];
				username = pass_info[4];
				password = pass_info[5];
			}
			
			br.close();
			fr.close();
			
			connection = DriverManager.getConnection(strConnection + ip
					+ port + dbname, username, password);
			
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::: "
					+ ip + " " + port);
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}*/
	
	
	public static Connection makeConnectionAMLLiveThread() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			//@SuppressWarnings("static-access")
			//String decryptedValue = new AESAlgorithmBG().readFile();
			//String val[] = decryptedValue.split("~");
			
			//String userName = val[0];
			//String userPass = val[1];
			
			//connection = DriverManager.getConnection("jdbc:oracle:thin:@10.144.18.122:1521:oraaml","AMLIDBI","Eagleeye_2018");
			//connection = DriverManager.getConnection("jdbc:oracle:thin:@10.168.250.69:1621:oraaml","AMLIDBI","amlidbi");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.192.21.240:1521:aml","pnbaml","pnbaml321");
			//connection = DriverManager.getConnection("jdbc:oracle:thin:@10.150.51.9:1571:amldb","amlnsdlprd","amlnsdl");
			//connection = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = 10.150.51.5)(PORT = 1571)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = amldb)))","amlnsdlprd","amlnsdl");
			
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection makeConnectionFINLiveThread() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.168.49.105:1521:ibnkfin","cia","cia");
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection makeConnectioniBUSLiveThread() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.144.18.161:1521:oraaml","ibus","ibus");
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection makeConnectionEKYCLiveThread() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			/*connection = DriverManager.getConnection("jdbc:oracle:thin:@10.168.49.105:1521:ibnkfin","ekyc","ekyc");*/
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.168.49.105:1521:ibnkfin","cia","cia");
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	public static String testConnection(String userName , String pass){
		
		@SuppressWarnings("unused")
		Connection connection = null;
		String result = "NA";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.144.18.122:1521:oraaml", userName, pass);
			//connection = DriverManager.getConnection("jdbc:oracle:thin:@10.144.18.161:1521:oraaml","AMLUAT","amluat");
			result = "success";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return result;
	}

	public static Connection makeConnectionAMLUATThread() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.144.118.20:1521:oms","amluat","amluat");
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection makeConnectionAMLLive() throws SQLException {
		Connection connection = null;
		String connCred = null, strConnection = null, ip = null, port = null, dbname = null, username = null,
				password = null;
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			//System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			connCred = amlProp.getProperty("DBDETAILS");
			// AML_PasswordBaseEncryption obj = new AML_PasswordBaseEncryption();
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// String pass_info[] = obj.decrypt(connCred).split("~");
			String pass_info[] = connCred.split("~");
			strConnection = pass_info[0];
			ip = pass_info[1];
			port = pass_info[2];
			dbname = pass_info[3];
			username = pass_info[4];
			password = pass_info[5];
			System.out.println("connCred : "+strConnection + ip + port + dbname);
			connection = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);
			//System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::: ");
			//System.out.println("IP::"+ ip + " : "+port);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection makeConnectionNSAMLLive() throws SQLException {
		Connection connection = null;
		String connCred = null, strConnection = null, ip = null, port = null, dbname = null, username = null,
				password = null;
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			//System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			connCred = amlProp.getProperty("NSDBDETAILS");
			// AML_PasswordBaseEncryption obj = new AML_PasswordBaseEncryption();
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// String pass_info[] = obj.decrypt(connCred).split("~");
			String pass_info[] = connCred.split("~");
			strConnection = pass_info[0];
			ip = pass_info[1];
			port = pass_info[2];
			dbname = pass_info[3];
			username = pass_info[4];
			password = pass_info[5];
			//System.out.println("connCred : "+strConnection + ip + port + dbname);
			connection = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);
			//System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::: ");
			//System.out.println("IP::"+ ip + " : "+port);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection makeConnectionAMLNSLive() throws SQLException {
		Connection connection = null;
		String connCred = null, strConnection = null, ip = null, port = null, dbname = null, username = null,
				password = null;
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			//System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			connCred = amlProp.getProperty("DBDETAILS");
			// AML_PasswordBaseEncryption obj = new AML_PasswordBaseEncryption();
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// String pass_info[] = obj.decrypt(connCred).split("~");
			String pass_info[] = connCred.split("~");
			strConnection = pass_info[0];
			ip = pass_info[1];
			port = pass_info[2];
			dbname = pass_info[3];
			username = pass_info[4];
			password = pass_info[5];
			//System.out.println("connCred : "+strConnection + ip + port + dbname);
			connection = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);
			//System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::: ");
			//System.out.println("IP::"+ ip + " : "+port);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/*public static Connection makeConnectionAMLLive() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			connection = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = 203.112.157.161)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = orclpdb)))","pnbaml","pnbaml");
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}*/
	public static Connection makeConnectionEDWLive() throws SQLException {
		Connection connection = null;
		String connCred = null, strConnection = null, ip = null, port = null, dbname = null, username = null,
				password = null;
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			//System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			connCred = amlProp.getProperty("EDWDBDETAILS");
			// AML_PasswordBaseEncryption obj = new AML_PasswordBaseEncryption();
			Class.forName("com.ibm.db2.jcc.DB2Driver");

			// String pass_info[] = obj.decrypt(connCred).split("~");
			String pass_info[] = connCred.split("~");
			strConnection = pass_info[0];
			ip = pass_info[1];
			port = pass_info[2];
			dbname = pass_info[3];
			username = pass_info[4];
			password = pass_info[5];
			//System.out.println("connCred : "+strConnection + ip + port + dbname);
			connection = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);
			//System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::: ");
			//System.out.println("IP::"+ ip + " : "+port);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	//MSSQL Connection 
	public static Connection makeMSSQLConnection() throws SQLException {
		Connection connection = null;
		String connCred = null, strConnection = null, ip = null, port = null, dbname = null, username = null,
				password = null;
		try {
			/*
			 * Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			 * 
			 * connection = DriverManager.getConnection(
			 * "jdbc:sqlserver://203.112.157.124:1433;databaseName=kagbiamldb;integratedSecurity=false"
			 * ,"sa","Intech_2020");
			 */
			
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			connCred = amlProp.getProperty("MSDBDETAILS");
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			// String pass_info[] = obj.decrypt(connCred).split("~");
			String pass_info[] = connCred.split("~");
			strConnection = pass_info[0];
			username = pass_info[1];
			password = pass_info[2];
			System.out.println("MS conn : "+strConnection);
			connection = DriverManager.getConnection(strConnection , username, password);
			connection.setAutoCommit(false);
			
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : "
					+ e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
}
