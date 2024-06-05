package com.idbi.intech.iaml.report;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import com.idbi.intech.aml.misc.AMLXLCreator;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class MonthlyReportRequest implements Runnable {

	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	CallableStatement cstmt = null;
	Statement stmtQuery = null;

	String reportReqNo = "";
	String startDate = "";
	String endDate = "";
	String reportCode = "";
	static ReportRequestProcess process_run = new ReportRequestProcess();

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public List<String> unprocessedReportRequest() {

		List<String> reportList = new ArrayList<String>();

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(
					"select REPORT_REQ_NO||'~'||START_DATE||'~'||END_DATE||'~'||REPORT_CODE from REPORT_REQUEST_TAB where REPORT_STATUS='N' ");

			while (rs.next()) {
				String riskNo = rs.getString(1);
				reportList.add(riskNo);
			}

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

		return reportList;
	}

	public void updateReportReqStatus(String reportReqNo) {
		try {

			stmt = connection.createStatement();
			stmt.executeUpdate(
					"update REPORT_REQUEST_TAB set REPORT_STATUS='Y',REQ_EXE_DATE=sysdate where REPORT_REQ_NO='"
							+ reportReqNo + "' ");
			connection.commit();

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

	@Override
	public void run() {
		while (true) {
			List<String> reportList = unprocessedReportRequest();

			/*
			 * String startDate = "01-mar-2022"; String endDate = "30-mar-2022"; reportReqNo
			 * ="112234568";
			 * 
			 * try { System.out.println( "reportReqNo : " + reportReqNo + "-" + startDate +
			 * "-" + endDate + "-" + reportCode);
			 * 
			 * executeReport(reportReqNo, startDate, endDate, reportCode); } catch
			 * (Exception e) {
			 * 
			 * e.printStackTrace(); }
			 */

			if (reportList.size() > 0) {
				for (String reportDetails : reportList) {
					String reportDetailsArr[] = reportDetails.split("~");
					boolean flg = true;

					String reportReqNo = reportDetailsArr[0];
					String startDate = reportDetailsArr[1];
					String endDate = reportDetailsArr[2];
					String reportCode = reportDetailsArr[3];

					try {
						System.out.println(
								"reportReqNo : " + reportReqNo + "-" + startDate + "-" + endDate + "-" + reportCode);

						executeReport(reportReqNo, startDate, endDate, reportCode);
					} catch (Exception e) {
						flg = false;
						e.printStackTrace();
					}

					if (flg) {
						updateReportReqStatus(reportReqNo);
					}
				}
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		makeConnection();
		Thread t = new Thread(new MonthlyReportRequest());
		t.start();
	}

	public void executeReport(String reportReqNo, String startDate, String endDate, String reportCode) {

		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Started execution for : " + reportReqNo + "-" + reportCode);

			Properties reportProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/ReportProp.properties");
			reportProp.load(is);
			is.close();

			String path = reportProp.getProperty("REPORT");
			String fpath = path + "" + reportReqNo + ".xls";

			ArrayList<String> head = new ArrayList<String>();
			ArrayList<ArrayList<String>> datavalue = new ArrayList<ArrayList<String>>();
			ArrayList<String> row = null;
			AMLXLCreator create = new AMLXLCreator();

			MonthlyZoneWiseCountReport zoneWise = new MonthlyZoneWiseCountReport();
			ArrayList<String> arrList = zoneWise.getMonthlyZoneWiseDays(startDate, endDate);
			HashMap<String, ArrayList<String>> hm = zoneWise.getMonthlyZoneWiseCount();

			head.add("ZONE NAME");
			for (String header : arrList) {
				head.add(header);
			}
			Set<String> keySet = hm.keySet();
			for (String key : keySet) {
				row = new ArrayList<String>();
				row.add(key);
				for (String report : hm.get(key)) {
					row.add(report);
				}
				datavalue.add(row);
			}

			create.createExcel(fpath, reportReqNo, head, datavalue);

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + reportReqNo + "-" + " Total Time elapsed : "
					+ (elapsedTime / 1000) + " Seconds");
			System.out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
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

}
