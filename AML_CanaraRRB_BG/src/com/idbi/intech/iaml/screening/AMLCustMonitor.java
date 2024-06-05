package com.idbi.intech.iaml.screening;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AMLCustMonitor implements Runnable {
	private static Connection connection = null;
	private static AMLCustMonitor process_run = new AMLCustMonitor();

	private static void createConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void pullDailyCust() {
		Statement stmt = null;
		Statement stmtMax = null;
		Statement stmtTrunc = null;
		PreparedStatement stmtInsert = null;
		ResultSet rs = null;
		String pstdTym = null;
		String status = "N";
		String proceedFlg = "N";
		int row = 0;
		CallableStatement callStmt = null;
		try {
			stmt = connection.createStatement();
			stmtTrunc = connection.createStatement();
			stmtMax = connection.createStatement();

			callStmt = connection.prepareCall("call amlscancheck()");
			stmtInsert = connection.prepareStatement("insert into aml_cust_sdn values (?,?,?)");

			rs = stmt
					.executeQuery("select decode(count(1),'0','N','Y') as now from aml_stop where to_number(to_char(sysdate,'hh24')) between to_number(end_time) and to_number(init_time)");
			while (rs.next()) {
				proceedFlg = rs.getString("now");
			}

			// System.out.println(proceedFlg);

			if (proceedFlg.equals("Y")) {
				rs = stmt
						.executeQuery("select to_char(maxpstdtym,'dd-mm-yy hh24:mi:ss') as maxpstd,status from "
								+ "aml_lastpstd");
				while (rs.next()) {
					pstdTym = rs.getString("maxpstd");
					status = rs.getString("status");
				}



			System.out.println(pstdTym);


				if (pstdTym != null && status.equals("Y")) {
					System.out.println("Upload Started...");
					stmt.executeUpdate("update aml_lastpstd set status = 'N'");
					connection.commit();

					stmtTrunc.executeUpdate("truncate table aml_cust_sdn");

					rs = stmt
							.executeQuery("select table_key,to_char(audit_date,'dd-mon-yy hh24:mi:ss'),'C' from adt@live_fin where table_name='CMG' and audit_date > to_date('"
									+ pstdTym
									+ "','dd-mm-yy hh24:mi:ss') and func_code='A'");
					while(rs.next()){
						row++;
						
						stmtInsert.setString(1,rs.getString(1));
						stmtInsert.setString(2,rs.getString(2));
						stmtInsert.setString(3,rs.getString(3));
						
						stmtInsert.executeUpdate();
					}
					
					connection.commit();
					
					if (row > 0) {
						callStmt.execute();
						stmtMax.executeUpdate("update aml_lastpstd set maxpstdtym=(select max(to_date(audit_date,'dd-mm-yy hh24:mi:ss')) from aml_cust_sdn)");
						connection.commit();
					}
					
					stmt.executeUpdate("update aml_lastpstd set status = 'Y'");
					connection.commit();
				}
			}
		} catch (SQLException ex) {
			System.out.println("Oracle Unavailable " + ex.getMessage());
			ex.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e) {
				System.out.println("Oracle Unavailable " + e.getMessage());
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtMax != null) {
					stmtMax.close();
					stmtMax = null;
				}
				if (stmtTrunc != null) {
					stmtTrunc.close();
					stmtTrunc = null;
				}
				if (callStmt != null) {
					callStmt.close();
					callStmt = null;
				}
				if (stmtInsert != null) {
					stmtInsert.close();
					stmtInsert = null;
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

	public static void main(String args[]) {
		createConnection();
		Thread t = new Thread(new AMLCustMonitor());
		t.start();
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
			createConnection();
			Thread t = new Thread(new AMLCustMonitor());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {
	}

	@Override
	public void run() {
		while (true) {
			new AMLCustMonitor().pullDailyCust();
			try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
