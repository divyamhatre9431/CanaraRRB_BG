package com.idbi.intech.iaml.rulethread;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class RuleThread implements Runnable {
	CallableStatement callStmt=null;
	Statement stmt = null;
	String procedure = "";
	String cbsDt = "";
	private static Connection connectionOra = null;
	
	public static void makeConnection() {
		try {
			connectionOra = ConnectionFactory.makeConnectionAMLLive();
		} catch (Exception sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	
	public RuleThread(String procedure,String cbsDt){
		this.procedure = procedure;
		this.cbsDt = cbsDt;
	}

	public void executeRule(String procedure,String cbsDt) {
		try {
			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure +" - "+cbsDt);
			System.out.flush();
			
			callStmt = connectionOra.prepareCall("{call "+procedure+"(?)}");
			callStmt.setString(1, cbsDt);
			callStmt.execute();
			
			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure
					+ " Total Time elapsed : " + (elapsedTime / 1000)
					+ " Seconds");
			System.out.flush();
			connectionOra.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (callStmt != null) {
					callStmt.close();
					callStmt = null;
				}
				if (connectionOra != null) {
					connectionOra.close();
					connectionOra = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	

	@Override
	public void run() {
		makeConnection();
		executeRule(procedure, cbsDt);
	}

}
