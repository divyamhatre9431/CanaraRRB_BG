package com.idbi.intech.iaml.datapull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class DataMigrateOra implements Runnable {
	private static Connection connectionOra = null;
	String procedure = "";
	Statement stmt = null;

	public static void makeOracleConnection() {
		try {
			connectionOra = ConnectionFactory.makeConnectionAMLLive();
			if(connectionOra!=null) {
			System.out.println("Oracle DB Connected.");
			}
			
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public DataMigrateOra(String procedure) {
		this.procedure = procedure;
	}

	public void executeProc(String procedure) {
		try {
			stmt = connectionOra.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + "call " + procedure+"();");

			stmt.execute("{call " + procedure+"()}");

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
		makeOracleConnection();
		executeProc(procedure);
	}

}
