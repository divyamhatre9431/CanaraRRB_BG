package com.idbi.intech.iaml.rulethread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.MissingResourceException;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AlertsDistribution implements Runnable {
	private static Connection connectionMs = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	static AlertsDistribution processRun = new AlertsDistribution();

	public static void makeMSSQLConnection() {
		try {
			connectionMs = ConnectionFactory.makeMSSQLConnection();
			if (connectionMs != null) {
				System.out.println("MSSQL DB Connected.");
			}
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void distributeAlerts() {
		int ticketCnt = 0;
		int userCnt = 0;

		// String userId=null;
		try {
			stmtCheck = connectionMs.createStatement();

			rs = stmtCheck
					.executeQuery("select count(*) from aml_rule_ticket where USER_ID = 'NA' and TICKET_STATUS='O'");
			while (rs.next()) {
				ticketCnt = rs.getInt(1);
			}

			if (ticketCnt > 0) {
				rs = stmtCheck
						.executeQuery("select count(*) from aml_user_master where active_flg='Y' and role_id='LEVEL1'");
				while (rs.next()) {
					userCnt = rs.getInt(1);
				}
			}

			if (userCnt > 0) {
				rs = stmtCheck
						.executeQuery("select user_id from aml_user_master where active_flg='Y' and role_id='LEVEL1'");

				ArrayList<String> userIdList = new ArrayList<>();
				while (rs.next()) {
					userIdList.add(rs.getString(1));
					System.out.println(userIdList);
				}

				int distAlert = ticketCnt / userCnt;

				int rem = ticketCnt % userCnt;

				for (String userId : userIdList) {

					int distAlerts = distAlert;

					if (rem > 0) {
						distAlerts += 1;
						rem -= 1;
					}
					// String updateQuery="update aml_rule_ticket set USER_ID=? where
					// TICKET_STATUS='O' and USER_ID='NA' and ROWNUM=?";
					String updateQuery = "update aml_rule_ticket set ZONE='KGB',LOCK_STATUS='N',user_id=? where ticket_id in"
							+ "(select  distinct top(?) ticket_id  from aml_rule_ticket"
							+ " where TICKET_STATUS ='O' and user_id='NA') and user_id='NA' and TICKET_STATUS='O'";
					PreparedStatement stmtUpdate = connectionMs.prepareStatement(updateQuery);

					stmtUpdate.setString(1, userId);
					stmtUpdate.setInt(2, distAlerts);
					System.out.println(userId);
					stmtUpdate.executeUpdate();

					connectionMs.commit();
				}

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {

			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
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

	public void start() {
		try {
			makeMSSQLConnection();
			Thread t = new Thread(new AlertsDistribution());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				distributeAlerts();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			processRun.start();
		} else {
			processRun.stop();
		}
	}

	public void stop() {

	}

	public static void main(String[] args) {

		makeMSSQLConnection();
		Thread t = new Thread(new AlertsDistribution());
		t.start();

	}

}
