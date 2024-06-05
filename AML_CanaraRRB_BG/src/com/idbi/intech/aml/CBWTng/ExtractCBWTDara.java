package com.idbi.intech.aml.CBWTng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ExtractCBWTDara {

	private static Connection connection = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void extractData(String fromData, String toDate) {
		CallableStatement cStmt = null;
		try {
			cStmt = connection.prepareCall("call cbwt_generation('" + fromData
					+ "','" + toDate + "')");
			cStmt.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (cStmt != null) {
					cStmt.close();
					cStmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		makeConnection();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.println("Please enter the from date (DD-MON-YY)");
			String fromDate = br.readLine();
			System.out.println("Please enter the to date (DD-MON-YY)");
			String toDate = br.readLine();

			new ExtractCBWTDara().extractData(fromDate, toDate);
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
