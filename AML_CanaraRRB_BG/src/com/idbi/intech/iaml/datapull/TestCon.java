package com.idbi.intech.iaml.datapull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestCon {

	public static Connection makeMSSQLConnection() throws SQLException {
		Connection connection = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			connection = DriverManager.getConnection(
					"jdbc:sqlserver://203.112.157.124:1433;databaseName=kagbiamldb;integratedSecurity=false", "sa",
					"Intech_2020");
			
			if (connection!=null) {
				System.out.println("Connected...");
			}else {
				System.out.println("Connected Failed...");
			}
			
		} catch (SQLException e) {
			System.out.println("SqlDAOFactory : makeConnection : Exception : " + e);
			throw new SQLException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static void main(String args[]) throws SQLException {
		TestCon test=new TestCon();
		test.makeMSSQLConnection();
	}
}
