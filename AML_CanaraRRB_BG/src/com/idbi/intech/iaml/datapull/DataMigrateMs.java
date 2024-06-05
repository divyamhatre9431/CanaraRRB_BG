package com.idbi.intech.iaml.datapull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class DataMigrateMs implements Runnable {
	private static Connection connectionMs = null;
	String procedure = "";
	Statement stmt = null;

	public static void makeMSSQLConnection() {
		try {
			connectionMs = ConnectionFactory.makeMSSQLConnection();
			/*
			 * if(connectionMs!=null) { System.out.println("MSSQL DB Connected."); }
			 */
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public DataMigrateMs(String procedure) {
		this.procedure = procedure;
	}

	public void executeProc(String procedure) {
		try {
			stmt = connectionMs.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			 stmt.execute("{call dbo." + procedure+"}");
			 
			 

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
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
		makeMSSQLConnection();
		executeProc(procedure);
	}

}
