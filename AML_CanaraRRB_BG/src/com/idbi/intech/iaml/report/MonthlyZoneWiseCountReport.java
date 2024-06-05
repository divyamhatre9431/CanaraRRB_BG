package com.idbi.intech.iaml.report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class MonthlyZoneWiseCountReport  {

	private static Connection connection = null;
	
	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	
	public HashMap<String, ArrayList<String>> getMonthlyZoneWiseCount() {

		HashMap<String, ArrayList<String>> data = new HashMap<>();

		Connection connection = null;
		Statement stmt = null, stmt1 = null;
		ResultSet rs = null, rs1 = null;

		String queryAML = "", query = "";
		try {
			// System.out.println("4");
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();

			query = "select distinct zone_id,substr(zone_desc,4,length(zone_desc))as zone_desc from monthly_Pending_Count_temp";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String ZoneId = rs.getString(1);
				String ZoneDesc = rs.getString(2);
				// System.out.println("ZoneId " + ZoneId);

				stmt1 = connection.createStatement();

				ArrayList<String> arrlist = new ArrayList<>();
				queryAML = "select zone_id,pending_count from monthly_Pending_Count_temp where zone_id='" + ZoneId
						+ "' order by zone_id,alert_date asc";
				rs1 = stmt1.executeQuery(queryAML);
				while (rs1.next()) {
					arrlist.add(rs1.getString(2));
				}
				data.put(ZoneDesc, arrlist);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return data;
	}

	public ArrayList<String> getMonthlyZoneWiseDays(String fromDate, String toDate) {

		ArrayList<String> arrlist = new ArrayList<>();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		try {
			System.out.println(fromDate+"  - "+toDate);
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			getExcuteProce(fromDate, toDate);
			query = "select distinct trunc(alert_date) from monthly_Pending_Count_temp order by trunc(alert_date)  asc";

			rs = stmt.executeQuery(query);

			while (rs.next()) {

				Date newDate = rs.getDate(1);

				SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM");
				String val = formatter2.format(newDate);

			//	System.out.println("date value newDate -  " + newDate + "valvalvalval " + val);

				arrlist.add(val);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return arrlist;
	}

	public void getExcuteProce(String fromDate, String toDate) {
		Connection connection = null;
		CallableStatement callstmt = null;

		try {
			// System.out.println("3");
			connection = ConnectionFactory.makeConnectionAMLLive();
			callstmt = connection.prepareCall("call getMonthlyZoneCount('" + fromDate + "','" + toDate + "')");
			callstmt.execute();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
}
