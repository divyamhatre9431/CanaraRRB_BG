package com.idbi.intech.iaml.misc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class MarkLeaveNameScreenUser implements Runnable  {
	private static Connection connection = null;
	private static MarkLeaveNameScreenUser process_run = new MarkLeaveNameScreenUser();

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void parkNewAlert() {
		Statement stmt = null;
		Statement stmtnsAlert=null;
		try {
			stmt = connection.createStatement();
			stmtnsAlert = connection.createStatement();
			
			stmt.executeUpdate("update aml_rule_ticket set user_id = 'HP' where ticket_status='O' and user_id ='NA'");
			
			 /*  Name sreen alert distibution --Priyank*/
			stmtnsAlert.executeUpdate("update ns_alert_master set user_id = 'HP' where alert_status='O' and user_id ='NA'");
			
			
			connection.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				connection.rollback();
				if (stmt != null) {
					stmt.close();
					stmt = null;
					if(stmtnsAlert != null)
					{
						stmtnsAlert.close();
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public String reallocateLeaveUser() {
		Statement stmt = null;
		Statement stmtUp = null;
		PreparedStatement stmtDel = null;
		Statement stmtUserNo = null;
		ResultSet rs = null;
		ArrayList<String> arrUser = null;
		String reqId = "";
		String userId = "";
		int startCnt = 0, endCnt = 0;
		try {
			stmt = connection.createStatement();
			stmtUp = connection.createStatement();
			stmtDel = connection
					.prepareStatement("delete from aml_leave_marking where user_id  = ?");
			stmtUserNo = connection.createStatement();

			rs = stmtUserNo
					.executeQuery("select count(1) from aml_user_master where active_flg = 'H' and role_id = 'LEVEL1'");
			while (rs.next()) {
				startCnt = rs.getInt(1);
			}

			arrUser = new ArrayList<String>();

			rs = stmt
					.executeQuery("select maker_id from aml_holiday_mark a,aml_user_master b where a.maker_id=b.user_id and status = 'V' and active_flg ='H' and to_date((select max(end_date) from aml_holiday_mark where maker_id=a.maker_id),'dd-mm-yy')<to_date(sysdate,'dd-mm-yy')");
			while (rs.next()) {
				arrUser.add(rs.getString(1));
			}

			rs = stmt
					.executeQuery("select a.user_id,request_id from aml_leave_marking a,aml_user_master b where a.user_id =b.user_id and active_flg='H' and to_date((select max(end_date) from aml_leave_marking where user_id = a.user_id),'dd-mm-yy')<to_date(sysdate,'dd-mm-yy')");
			while (rs.next()) {
				userId = rs.getString(1);
				reqId = rs.getString(2);

				arrUser.add(userId);
				stmtUp.executeUpdate("update aml_user_master set sup_userid='"
						+ userId
						+ "' where user_id in (select user_id from aml_sub_leave_marking where request_id  = '"
						+ reqId + "')");
			}

			for (String user : arrUser) {
				stmt.executeUpdate("update aml_user_master set active_flg='Y',user_no=null where user_id='"
						+ user + "'");
			}

			connection.commit();

			arrUser = new ArrayList<String>();

			rs = stmt
					.executeQuery("select maker_id from aml_holiday_mark a,aml_user_master b where a.maker_id=b.user_id and status = 'V' and  active_flg ='Y' and start_date<=to_date(sysdate,'dd-mm-yy') and end_date>=to_date(sysdate,'dd-mm-yy') and verify_date = (select max( verify_date) from aml_holiday_mark where maker_id=a.maker_id)");
			while (rs.next()) {
				arrUser.add(rs.getString(1));
			}

			String userDel = "";

			rs = stmt
					.executeQuery("select user_id from aml_leave_marking where start_date<=to_date(sysdate,'dd-mm-yy') and end_date>=to_date(sysdate,'dd-mm-yy')");
			while (rs.next()) {
				userDel = rs.getString(1);

				arrUser.add(userDel);

				stmtDel.setString(1, userDel);
				stmtDel.executeUpdate();
			}

			for (String user : arrUser) {
				stmt.executeUpdate("update aml_user_master set active_flg='H',user_no=null where user_id='"
						+ user + "'");
			}

			connection.commit();

			rs = stmtUserNo
					.executeQuery("select count(1) from aml_user_master where active_flg = 'H' and role_id = 'LEVEL1'");
			while (rs.next()) {
				endCnt = rs.getInt(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtUp != null) {
					stmtUp.close();
					stmtUp = null;
				}
				if (stmtDel != null) {
					stmtDel.close();
					stmtDel = null;
				}
				if (stmtUserNo != null) {
					stmtUserNo.close();
					stmtUserNo = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return endCnt != startCnt ? "Y" : "N";
	}

	public void makeDistribution() {
		Statement stmt = null;
		Statement stmtp = null;
		Statement stmtNameScreen=null;
		Statement stmtNsAlert=null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmtNamScreen= null;
		
		ResultSet rs = null;
		ResultSet rsNsAlert=null;
		ArrayList<String> user = new ArrayList<String>();
		ArrayList<String> nsUser= new ArrayList<String>();
		try {
			stmt = connection.createStatement();
			stmtp = connection.createStatement();
			stmtNameScreen=connection.createStatement();
			stmtNsAlert=connection.createStatement();

			stmtp.executeUpdate("update aml_rule_ticket set user_id = 'NA'  where ticket_status='O' and user_id ='HP'");

			pstmt = connection
					.prepareStatement("update aml_rule_ticket set user_id = 'NA',ticket_status='O' where user_id = ? and ticket_status in ('O')");

			rs = stmt
					.executeQuery("select distinct a.user_id from aml_rule_ticket a,aml_user_master b,aml_holiday_mark c "
							+ "where a.user_id=b.user_id " 
							+ "and b.user_id=c.maker_id " 
							+ "and verify_date = (select max( verify_date) from aml_holiday_mark where maker_id=b.user_id) "
							+ "and c.flag='Y' " 
							+ "and active_flg = 'H' "
							+ "and ticket_status in ('O')");
			
			
			
			
			
			while (rs.next()) {
				user.add(rs.getString(1));
			}

			for (String id : user) {
				pstmt.setString(1, id);
				pstmt.executeUpdate();
			}
			
			 /*  Name sreen alert distibution --Priyank*/
			
			
			stmtNameScreen.executeUpdate("update ns_alert_master  set user_id = 'NA'  where alert_status ='O' and user_id ='HP'");
			
			pstmtNamScreen = connection.prepareStatement("update ns_alert_master set user_id ='NA',alert_status='O' where user_id = ? and alert_status in ('O')");
			
			rsNsAlert=stmtNsAlert.executeQuery("select distinct a.user_id from ns_alert_master a,aml_user_master b,aml_holiday_mark c "
					+ "where a.user_id=b.user_id  "
					+ "and b.user_id=c.maker_id "
					+ "and verify_date=(select max(verify_date) from aml_holiday_mark where maker_id=b.user_id ) "
					+ "and c.flag='Y' "
					+ "and active_flg='H' "
					+ "and alert_status in ('O')");
			
			
			while(rsNsAlert.next())
			{
				nsUser.add(rsNsAlert.getString(1));
			}
			
			for(String nsUserId : nsUser)
			{
				pstmtNamScreen.setString(1, nsUserId);
			}
			
			connection.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtp != null) {
					stmtp.close();
					stmtp = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if( stmtNameScreen != null)
				{
					stmtNameScreen.close();
					stmtNameScreen=null;
				}
				if(stmtNsAlert !=null)
				{
					stmtNsAlert.close();
					stmtNsAlert=null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				
				if(rsNsAlert != null)
				{
					rsNsAlert.close();
					rsNsAlert=null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			process_run.start();
		} else {
			process_run.stop();
		}
	}

	public void start() {
		try {
			makeConnection();
			Thread t = new Thread(new MarkLeaveNameScreenUser());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public void changeUserNo() {
		CallableStatement cStmt = null;
		int row = 0;
		try {
			cStmt = connection.prepareCall("call gen_userno()");
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
		Thread t = new Thread(new MarkLeaveNameScreenUser());
		t.start();
	}

	@Override
	public void run() {
		while (true) {
			parkNewAlert();
			if (reallocateLeaveUser().equals("Y")) {
				changeUserNo();
			}
			makeDistribution();
			
			System.out.println("Completed");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
	
	
	
	


