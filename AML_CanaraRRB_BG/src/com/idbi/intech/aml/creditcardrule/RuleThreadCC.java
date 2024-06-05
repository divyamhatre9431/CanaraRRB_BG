package com.idbi.intech.aml.creditcardrule;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class RuleThreadCC implements Runnable{

	Statement stmt = null;
	String procedure = "";
	private static Connection connection = null;
	
	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	
	public RuleThreadCC(String procedure){
		this.procedure = procedure;
	}

	public void executeRule(String procedure) {
		try {
			stmt = connection.createStatement();
			
			long startTime = System.currentTimeMillis();
			
			System.out.println("Started execution for : " + procedure);
			System.out.flush();
			
			stmt.execute("call "+procedure);
			
			long stopTime = System.currentTimeMillis(); 

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure
					+ " Total Time elapsed : " + (elapsedTime / 1000)
					+ " Seconds");
			System.out.flush();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	

	@Override
	public void run() {
		makeConnection();
		executeRule(procedure);
	}
}
