package com.idbi.intech.iaml.screening;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AML_WLScan_Customer_New implements Runnable {
	private static Connection con_aml;
	private static AML_WLScan_Customer_New obj = new AML_WLScan_Customer_New();

	private static void LoadDatabaseAml() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLive();
			con_aml.setAutoCommit(false);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private static void performScan(String scanDate, String type) {
		Statement stmt = null;
		Statement stmtCust = null;
		Statement stmtScan = null;
		Statement stmtCountry = null;
		ResultSet rsSol = null;
		ResultSet rsCust = null;
		ResultSet rsScan = null;
		ResultSet rsSeq = null;
		ResultSet rsDOB = null;
		ResultSet rsCountry = null;
		String sol = "";
		String name = "";
		String id = "";
		String passport = "";
		String dob = "";
		String country = "";
		String alert_id = "";
		String role = "";
		String vertical = "";
		String percent = "";
		String personId = "";
		String passFlg = "";
		String dobFlg = "";
		String cntryCode = "-";
		try {
			stmtCust = con_aml.createStatement();
			stmtScan = con_aml.createStatement();
			stmt = con_aml.createStatement();
			stmtCountry = con_aml.createStatement();

			String customerSQL = "select cust_name,cust_id,nvl(cust_passport_no,'-') as passport,nvl(to_char(cust_dob,'DD-MON-YYYY'),'') as dob,nvl(cust_perm_cntry_code,'-') as country1,nvl(cust_commu_cntry_code,'-') as country2,cust_vertical_code,primary_sol_id from aml_cust_master where ";

			if (type.equals("NEW")) {
				customerSQL += "scan_flg = 'Y'";
			}

			if (type.equals("OLD")) {
				customerSQL += "scan_flg = 'N'";
			}

			// customerSQL += "white_list='N'";

			// System.out.println(customerSQL);

			rsCust = stmtCust.executeQuery(customerSQL);

			while (rsCust.next()) {
				name = rsCust.getString("cust_name") != null ? rsCust
						.getString("cust_name").replaceAll("'", "") : ""
						.replaceAll("'", "");
				id = rsCust.getString("cust_id");
				passport = rsCust.getString("passport");
				dob = rsCust.getString("dob");
				country = rsCust.getString("country1").equals("-") ? rsCust
						.getString("country2") : rsCust.getString("country1");
				vertical = rsCust.getString("cust_vertical_code") == null ? "NA"
						: rsCust.getString("cust_vertical_code");
				sol = rsCust.getString("primary_sol_id");

				// Scanning the customer

				String sql = "select person_id,percent from (select a.person_id, utl_match.jaro_winkler_similarity(trim(UPPER(name)), '"
						+ name.toUpperCase()
						+ "') as percent from aml_wc_name a,aml_wc_master b where a.person_id=b.person_id";

				if (!country.equals("-")) {
					if ((country.toUpperCase()).equals("U.A.E")
							|| (country.toUpperCase()).equals("UAE")
							|| (country.toUpperCase()).equals("U.A.E."))
						country = "United Arab Emirates";

					rsCountry = stmtCountry
							.executeQuery("select country_code from aml_wc_country_list where country_name='"
									+ country.toUpperCase() + "'");
					while (rsCountry.next()) {
						cntryCode = rsCountry.getString(1);
					}

					if (!cntryCode.equals("-"))
						sql += " and a.person_id in (select person_id from aml_wc_country where country_code = '"
								+ cntryCode.toUpperCase() + "')";
				}

				if (type.equals("OLD"))
					sql += " and aml_date='" + scanDate + "'";

				sql += ") where percent >= (select value from p_aml_general where name = 'SDN_FUZZY_MATCH_PERCENTAGE')";

				// System.out.println(sql);

				// setting flag for main alert entry
				boolean flg = true;

				rsScan = stmt.executeQuery(sql);
				while (rsScan.next()) {
					if (flg) {
						// getting scan data from ResultSet
						personId = rsScan.getString("person_id");
						percent = rsScan.getString("percent");

						// creating sequence
						rsSeq = stmt
								.executeQuery("select 'AL'||to_char(sysdate,'ddmmyyhh24miss')||lpad(sdnalert.nextval,4,0) from dual");
						while (rsSeq.next()) {
							alert_id = rsSeq.getString(1);
						}

						
						// inserting main alert

						stmt.executeUpdate("insert into AML_SDN_ALERT_DETAILS"
								+ "(ALERT_ID,ALERT_DESC,ALERT_STATUS,CATEGORY,GENERATED_TIME,LAST_CHANGE_USER_ID,"
								+ "LAST_CHANGE_DATE,user_ID,"
								+ "PRIMARY_SOL_ID,CUST_ID,VERTICAL) "
								+ "values('"
								+ alert_id
								+ "','SDN Alert','O','SDN',sysdate,'SYSTEM',sysdate,'UAT1'"
								+  ",'" + sol + "','" + id
								+ "','" + vertical + "')");

						// reseting flg for inserting duplicate alert
						flg = false;
					}

					// scanning for passport
					/*rsPass = stmt
							.executeQuery("select decode(count(pass_num),0,'N','Y') as cnt from aml_wc_passport where person_id='"
									+ personId
									+ "' and pass_num='"
									+ passport
									+ "'");
					while (rsPass.next()) {
						passFlg = rsPass.getString("cnt");
					}*/
					
					passFlg = "N";

					// scanning for DOB
					rsDOB = stmt
							.executeQuery("select decode(count(person_id),0,'N','Y') as cnt from aml_wc_date where person_id ='"
									+ personId + "' and (day||'-'||month||'-'||year)='" + dob + "' ");
					while (rsDOB.next()) {
						dobFlg = rsDOB.getString("cnt");
					}

					// inserting sub alerts
					stmt.executeUpdate("insert into AML_SDNSCANALERTDETAILS(ALERT_ID,MATCH_PERCENT,PERSON_ID,DOB_MATCH,CTRY_MATCH,SUB_ALERT_STATUS,PASS_FLG) values('"
							+ alert_id
							+ "','"
							+ percent
							+ "','"
							+ personId
							+ "','" + dobFlg + "','Y','O','" + passFlg + "')");

				}
				con_aml.commit();

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtCust != null) {
					stmtCust.close();
					stmtCust = null;
				}
				if (stmtScan != null) {
					stmtScan.close();
					stmtScan = null;
				}
				if (rsSol != null) {
					rsSol.close();
					rsSol = null;
				}
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
				if (rsScan != null) {
					rsScan.close();
					rsScan = null;
				}
				if (rsSeq != null) {
					rsSeq.close();
					rsSeq = null;
				}
				if (rsDOB != null) {
					rsDOB.close();
					rsDOB = null;
				}
				if (stmtCountry != null) {
					stmtCountry.close();
					stmtCountry = null;
				}
				if (rsCountry != null) {
					rsCountry.close();
					rsCountry = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String getReadyFlg(int opt) {
		PreparedStatement pstmt_flg = null;
		ResultSet rs = null;
		String strFlg = "";
		String strQuery = "";
		try {
			if (opt == 1)
				strQuery = "select ope_flg_sdn from aml_bizz_tab where ope_flg_sdn='Y'";
			else if (opt == 2)
				strQuery = "update aml_bizz_tab set ope_flg_sdn = 'Z' where ope_flg_sdn='Y'";
			pstmt_flg = con_aml.prepareStatement(strQuery);
			if (opt == 1) {
				rs = pstmt_flg.executeQuery();
				while (rs.next()) {
					strFlg = rs.getString(1);
				}
			} else if (opt == 2) {
				pstmt_flg.executeUpdate();
				con_aml.commit();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt_flg != null) {
					pstmt_flg.close();
					pstmt_flg = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return strFlg;
	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		Thread t = new Thread(obj);
		t.start();
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			obj.start();
		} else {
			obj.stop();
		}
	}

	public void start() {
		try {
			LoadDatabaseAml();
			Thread t_upload = new Thread(obj);
			t_upload.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	@Override
	public void run() {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con_aml.createStatement();
			while (true) {
				if (getReadyFlg(1).equals("Y")) {
					rs = stmt
							.executeQuery("select to_char(sysdate,'dd-mon-yy') from dual");
					while (rs.next()) {
						String dt = rs.getString(1);
						System.out.println("Scan Started..... " + dt);

						long startTime = System.currentTimeMillis();
						
						System.out.println("Scan started for new record....");
						performScan(dt, "NEW");
						System.out.println("Scan started for old record....");
						performScan(dt, "OLD");
						getReadyFlg(2);

						long stopTime = System.currentTimeMillis();

						long elapsedTime = stopTime - startTime;
						System.out.println("Total Time elapsed : "
								+ elapsedTime + " ms");

						System.out.println("Scan Completed..... " + dt);
					}
				}
				try {
					Thread.sleep(1000 * 300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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

	}
	
	
}
