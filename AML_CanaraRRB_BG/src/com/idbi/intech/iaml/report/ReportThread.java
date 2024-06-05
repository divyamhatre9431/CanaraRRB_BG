package com.idbi.intech.iaml.report;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import com.idbi.intech.aml.misc.AMLXLCreator;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ReportThread implements Runnable {
	CallableStatement stmt = null;
	Statement stmtQuery = null;
	ResultSet rs = null;
	
	String reportReqNo = "";
	String startDate = "";
	String endDate = "";
	String reportCode = "";
	private static Connection connection = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public ReportThread(String reportReqNo,String startDate,String endDate,String reportCode) {
		this.reportReqNo = reportReqNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.reportCode = reportCode;
	}

	public void executeReport(String reportReqNo,String startDate,String endDate,String reportCode) {
		
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Started execution for : " + reportReqNo +"-" +reportCode);
			
			Properties reportProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/ReportProp.properties");
			reportProp.load(is);
			is.close();
			
			String path = reportProp.getProperty("REPORT");
			String fpath = path+""+reportReqNo+".xls";
			ArrayList<String> head = new ArrayList<String>();
			ArrayList<ArrayList<String>> datavalue = new ArrayList<ArrayList<String>>();
			ArrayList<String> row = null;
			AMLXLCreator create= new AMLXLCreator();
			
			String reportHeader = reportProp.getProperty(reportCode+"_HDR");
			String reportPl = reportProp.getProperty(reportCode+"_PL");
			String reportSql = reportProp.getProperty(reportCode+"_SQL");
			String[] reportHeaderArr = reportHeader.split("~");
			
			stmt = connection.prepareCall("call "+ reportPl+"('"+startDate+"','"+endDate+"')");
			stmt.execute();
						
			for(String header : reportHeaderArr)
			{
				head.add(header);
			}
			
			stmtQuery = connection.createStatement();
			rs = stmtQuery.executeQuery(reportSql);
			
			while(rs.next())
			{
				int hdrSize = reportHeaderArr.length;
				row = new ArrayList<String>();
				
				for(int i=1; i<=hdrSize; i++)
				{
					row.add(rs.getString(i));
				}
				
				datavalue.add(row);
			}			
			
			create.createExcel(fpath, reportReqNo, head, datavalue);
						
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + reportReqNo +"-" +reportCode + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtQuery != null) {
					stmtQuery.close();
					stmtQuery = null;
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
	

	@Override
	public void run() {
		makeConnection();
		executeReport(reportReqNo,startDate,endDate,reportCode);
	}

}
