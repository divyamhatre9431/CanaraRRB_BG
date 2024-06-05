package com.idbi.intech.iaml.walkin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import com.idbi.intech.aml.misc.AMLXLCreator;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class WalkinAlertRule {
	
	private static Connection connection = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	public void generateAlert(String fromDate,String toDate,String noTimes){
		Statement stmt=null;
		Statement stmtAlert = null;
		ResultSet rs = null;
		ResultSet rsAlert = null;
		String walkinName = "";
		String walkinId = "";
		String walkinDob = "";
		String ticketId = "";
		String data = "";
		String totAmt = "";

		ArrayList<String> row = null;
		ArrayList<ArrayList<String>> datavalue = new ArrayList<ArrayList<String>>();
		try{
			stmt = connection.createStatement();
			stmtAlert = connection.createStatement();
			rs = stmt.executeQuery("select walkin_name,to_char(walkin_dob,'dd-mon-yy'),walkin_id,sum(tot_cash_amt) from aml_walkin where to_date(rcre_time,'dd-mon-yy') between '"+fromDate+"' and '"+toDate+"' and tot_cash_amt between 40000 and 50000 group by walkin_name,walkin_dob,walkin_id having count(1) >="+noTimes);
			while(rs.next()){
				walkinName=rs.getString(1);
				walkinId=rs.getString(2);
				walkinDob=rs.getString(3);
				totAmt = rs.getString(4);
				
				rsAlert = stmtAlert.executeQuery("select 'AML'||to_char(sysdate,'ddmmyy')||lpad(walkinseq.nextval,4,0) from dual");
				while(rsAlert.next()){
					ticketId=rsAlert.getString(1);
				}
				
				row = new ArrayList<String>();
				
				data = ticketId+"~"+walkinName+"~"+walkinDob+"~"+walkinId+"~"+convertAmt(totAmt)+"~"+fromDate+"~"+toDate;
				for (String info : data.split("~")) {
					row.add(info);
				}
				datavalue.add(row);
				
				
				stmtAlert.executeUpdate("insert into aml_walkin_ticket values('"+ticketId+"','"+walkinName+"','"+walkinId+"','"+walkinDob+"','"+fromDate+"','"+toDate+"',sysdate,'N','"+totAmt+"')");
			}
			
			connection.commit();	
			
			generateReport(datavalue);
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(stmt!=null){
					stmt.close();
					stmt=null;
				}
				if(stmtAlert!=null){
					stmtAlert.close();
					stmtAlert=null;
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public void generateReport(ArrayList<ArrayList<String>> arrData) {
		Statement stmt = null;
		ResultSet rs = null;
		String data = "";
		String reportName = "";
		String rId = "";
		ArrayList<String> arrTo = new ArrayList<String>();
		ArrayList<ArrayList<String>> datavalue = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<String> arrHead = new ArrayList<String>();
		try {
			arrHead.add("Sr. No.");
			arrHead.add("Walkin Name");
			arrHead.add("Walkin Id");
			arrHead.add("Walkin DOB");
			arrHead.add("Total Amount");
			arrHead.add("From Date");
			arrHead.add("To Date");
			

			stmt = connection.createStatement();
			rs = stmt.executeQuery("select lpad(walkrptseg.nextval,6,0) from dual");
			while(rs.next()){
				rId=rs.getString(1);
			}

			reportName = "ReportWalkin"+rId;

			

			AMLXLCreator amlXl = new AMLXLCreator();
			amlXl.createExcel("D://AML//WalkinAlert//" + reportName + ".xls",
					"Walkin Alert Details", arrHead, arrData);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public String convertAmt(String amount) {
		String cntAmt = "";
		String fAmt = "";
		int i = 0;
		boolean flg = true;
		String decimal = "";
		boolean negFlg = false;

		if (amount.contains(".")) {
			decimal = amount.substring(amount.indexOf("."));
			amount = amount.substring(0, amount.indexOf("."));
		}

		if (amount.startsWith("-")) {
			amount = amount.substring(1);
			negFlg = true;
		}

		char digit[] = amount.toCharArray();

		for (int j = (digit.length - 1); j >= 0; j--) {
			if (i == 3) {
				cntAmt += ",";
				flg = false;
				i = 0;
			}
			if (i == 2 && flg == false) {
				cntAmt += "," + (String.valueOf(digit[j]));
				i = 0;
			} else {
				cntAmt += (String.valueOf(digit[j]));
			}
			i++;
		}

		char fdigit[] = cntAmt.toCharArray();

		for (int j = (fdigit.length - 1); j >= 0; j--) {
			fAmt += String.valueOf(fdigit[j]);
		}

		if (decimal != null) {
			fAmt += decimal;
		}

		if (negFlg) {
			fAmt = "-" + fAmt;
		}

		return fAmt;
	}
	
	public static void main(String args[]){
		makeConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
		System.out.println("Please enter the from date");
		String fromDate = br.readLine();
		System.out.println("Please enter the to date");
		String toDate = br.readLine();
		System.out.println("Please no. of times");
		String times = br.readLine();
		
		new WalkinAlertRule().generateAlert(fromDate, toDate, times);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

}
