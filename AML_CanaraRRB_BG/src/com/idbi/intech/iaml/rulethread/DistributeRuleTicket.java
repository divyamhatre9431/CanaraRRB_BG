package com.idbi.intech.iaml.rulethread;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class DistributeRuleTicket implements Runnable {

	private static Connection connection = null;
	
	private static DistributeRuleTicket process_run = new DistributeRuleTicket();

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void distributeAlerts(String user,Connection connection) {
		Statement stmt = null;
		ResultSet rs = null;
		Statement sTicket = null;
		CallableStatement callStmt = null;
		ResultSet rsTicket = null;
		String userId = "";
		String customerId = "";
		String customerVert = "";
		try {
			//System.out.println(user);
			stmt = connection.createStatement();
			sTicket = connection.createStatement();
			callStmt = connection.prepareCall("call update_dynamic_rulewise_cnt(?,?,?,?,?)");

			String query = "select a.cust_id,'NO DATA' cust_vertical_code from aml_rule_ticket a,aml_cust_master b where a.cust_id = b.cust_id and ticket_status in ('O','H') and user_id='"
					+ user + "' "
					+ "union all "
					+"select a.cust_id,'NO DATA' cust_vertical_code from aml_rule_ticket a where ticket_status in ('O','H') and user_id='"+user+"' and cust_id like 'AML%'";
			
			//System.out.println("query : "+query);
			rsTicket = sTicket.executeQuery(query);
			
			while (rsTicket.next()) {
				customerId = rsTicket.getString(1);
				customerVert = rsTicket.getString(2);
				String query2 = "select nvl(rule_distribution('"
						+ customerId
						+ "'),value) from p_aml_general where name='DEFAULT_USER'";
				
				//System.out.println("query2 : "+query2);
				
				rs = stmt.executeQuery(query2);
				while (rs.next()) {
					userId = rs.getString(1);
				}

				stmt.executeUpdate("update aml_rule_ticket set user_id='"
						+ userId + "',vertical='"+customerVert+"' where cust_id = '" + customerId
						+ "' and ticket_status = 'O' and user_id='" + user
						+ "'");
				
				callStmt.setString(1, userId);
				callStmt.setString(2, customerId);
				callStmt.setString(3, "O");
				callStmt.setString(4, "O");
				callStmt.setString(5, userId);
				
				
				callStmt.execute();
				
				connection.commit();
			}			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (sTicket != null) {
					sTicket.close();
					sTicket = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rsTicket != null) {
					rsTicket.close();
					rsTicket = null;
				}
				if (callStmt != null) {
					callStmt.close();
					callStmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	//Watch List Alert Distribution
	public void distributeNsAlerts(String user, Connection connection) {
		Statement stmt = null;
		//Statement nsstmt=null;
		ResultSet rs = null;
		Statement sTicket = null;
		ResultSet rsTicket = null;
		PreparedStatement pstmtUserIdUpdate = null;
		String userId = "";
		DistributeDataBean bean = null;
		ArrayList<DistributeDataBean> listBean = new ArrayList<DistributeDataBean>();
		
		try {
			stmt = connection.createStatement();
			//nsstmt=connection.createStatement();
			sTicket = connection.createStatement();
			//System.out.println("user is "+user);
			pstmtUserIdUpdate = connection.prepareStatement(
					"update ns_alert_master set user_id=? where cust_id=? and alert_status='O' and user_id=?");

			rsTicket = sTicket.executeQuery(
					"select a.cust_id from ns_alert_master a,aml_cust_master b where  a.cust_id = b.cust_id and alert_status ='O' and user_id='"
							+ user + "'");
			while (rsTicket.next()) {
				bean = new DistributeDataBean(rsTicket.getString("cust_id"), null);
				if (bean.getCustId() != null) {
					listBean.add(bean);
				}
			}

			for (DistributeDataBean beanData : listBean) {
				//System.out.println("cust is "+beanData.getCustId());

				rs = stmt.executeQuery("select nvl(rule_distribution('" + beanData.getCustId()
						+ "'),value) from p_aml_general where name='DEFAULT_USER'");
				while (rs.next()) {
					userId = rs.getString(1);
				}
				//System.out.println("distributed user is "+beanData.getCustId());
				
				pstmtUserIdUpdate.setString(1, userId);
				pstmtUserIdUpdate.setString(2, beanData.getCustId());
				pstmtUserIdUpdate.setString(3, user);
				
				pstmtUserIdUpdate.executeUpdate();
				
				/*nsstmt.executeUpdate("update ns_alert_master set user_id='"
						+ userId + "' where cust_id = '" + beanData.getCustId()
						+ "' and alert_status = 'O' and user_id='" + user + "'");*/

				connection.commit();
				
			}
			if(rs!=null) {
				rs.close();
				rs=null;
			}
			
			if(stmt!=null) {
				stmt.close();
				stmt=null;
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage()+" "+ex.getSQLState());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (sTicket != null) {
					sTicket.close();
					sTicket = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rsTicket != null) {
					rsTicket.close();
					rsTicket = null;
				}
				if(pstmtUserIdUpdate!=null) {
					pstmtUserIdUpdate.close();
					pstmtUserIdUpdate=null;
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage()+" "+ex.getSQLState());
			}
		}
	}


	public String checkCreateThread() {
		int cnt = 0;
		String flg = "N";
		Statement stmtCheck=null;
		ResultSet rs=null;
		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck
					.executeQuery("select count(distinct(exe_flg)) from aml_core_process where active_flg='Y'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}

			if (cnt == 1) {
				rs = stmtCheck
						.executeQuery("select distinct(exe_flg) from aml_core_process where active_flg='Y'");
				while (rs.next()) {
					flg = rs.getString(1);
				}
			}
			// System.out.println(flg);
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
		return flg;
	}

	@Override
	public void run() {
		while (true) {
			if (checkCreateThread().equals("Y")) {
			distributeAlerts("NA",connection);
			//distributeNsAlerts("SYSTEM", connection);
			try {
				Thread.sleep(1000 * 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			Thread tObj = new Thread(new DistributeRuleTicket());
			tObj.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public static void main(String args[]) {
		makeConnection();
		Thread tObj = new Thread(new DistributeRuleTicket());
		tObj.start();

	}

}
