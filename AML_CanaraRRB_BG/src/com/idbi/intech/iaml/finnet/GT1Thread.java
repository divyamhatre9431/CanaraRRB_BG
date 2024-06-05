package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GT1Thread implements Runnable {
	
	private static Connection connection;

	String requestId = "";
	String batchNo = "";
	String fromDate = "";
	String toDate = "";
	String reportType = "";
	String procedure = "";
	
	public GT1Thread(String requestId, String batchNo,
			String fromDate, String toDate, String reportType, String procedure) {
		this.requestId = requestId;
		this.batchNo = batchNo;
		this.batchNo = batchNo;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.reportType = reportType;
		this.procedure = procedure;
	}
	
	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("AML connection established successfully...");
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	
	public void executeProc(String procedure) {
		
		Statement stmt = null;
		
		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure+"('"+requestId+"')");

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
		
		try
		{
			makeConnection();
			executeProc(procedure);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
