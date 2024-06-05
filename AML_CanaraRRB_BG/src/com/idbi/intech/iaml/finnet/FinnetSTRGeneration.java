package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class FinnetSTRGeneration {
	
	private static Connection connection;
	
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
	
	
	public static List<String> getReportData(String reqNo) {
		List<String> custList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			
			String sql = "select STR_NO||'-'||CUST_ID||'-'||CUST_ACID||'-'||ACCOUNT_NO||'-'||CUST_TYPE||'-'||TRAN_CHANNEL from STR_DETAILS_FINNET where STR_NO='"+reqNo+"' and status='N' ";

			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				System.out.println("Extracted Customer Details : " + rs.getString(1));
				custList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return custList;
	}
	
	public static void updateProcessStatus(String reqNo,String status) throws SQLException
	{
		PreparedStatement pstmtUpdateIntoAml = null;
		
		try
		{
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update STR_DETAILS_FINNET set STATUS=? where STR_NO=? ");
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.setString(2, reqNo);
			
			pstmtUpdateIntoAml.execute();
			connection.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmtUpdateIntoAml != null) {
				pstmtUpdateIntoAml.close();
				pstmtUpdateIntoAml = null;
			}
		}
	}
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws SQLException
	{
		makeConnection();
		
		// taking input from the user
		Scanner in = new Scanner(System.in);

		System.out.print("Enter STR No : ");
		String reqNo = in.nextLine();
		
		List<String> reportList = getReportData(reqNo);
		System.out.println("reportList : " + reportList.size());
		
		updateProcessStatus(reqNo,"P");
		
		for(String data : reportList)
		{
			String[] dataArr = data.split("-");
			String strNo = dataArr[0];
			String custId = dataArr[1];
			String custAcid = dataArr[2];
			String accountNo = dataArr[3];
			String custType = dataArr[4];
			String tranChannel = dataArr[5];
			String reptRefNo = dataArr[6];
			String fromDate = dataArr[7];
			String toDate = dataArr[8];
			
			new Thread(new FinnetThread(strNo, custId, custAcid, accountNo, custType, tranChannel,reptRefNo,fromDate,toDate)).start();
		}
			
	}

	
}
