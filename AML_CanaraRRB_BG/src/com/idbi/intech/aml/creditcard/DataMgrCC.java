package com.idbi.intech.aml.creditcard;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class DataMgrCC implements Runnable{

	private static Connection connection = null;
	String procedure = "";
	Statement stmt = null;

	
	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	public DataMgrCC(String procedure) {
		this.procedure = procedure;
	}
	
	

	public void executeProc(String procedure) {
		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure);

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure
					+ " Total Time elapsed : " + (elapsedTime / 1000)
					+ " Seconds");
			System.out.flush();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
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
		executeProc(procedure);
	}
}
