package com.idbi.intech.iaml.screening;

/*
 * Remove the HARDCODE name and person before moving to production
 * Also change the aml_cust_master_sdn to aml_cust_master.
 *  
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.StringTokenizer;

import oracle.net.aso.n;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.tool.FuzzyMatch;

public class AMLCustomerScreeningBA implements Runnable {
	private static final String RESOURCE = "resource";
	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtUp = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	DecimalFormat df = new DecimalFormat("###.##");

	String id;
	String custName;
	String sol;
	String custCntry;
	String custDob;
	String custPass;
	String commCtry;
	double threshold;
	String pcOld = "";
	String ccOld = "";
	Set<String> hsSusp = null;

	private static AMLCustomerScreeningBA process_run = new AMLCustomerScreeningBA();
	@SuppressWarnings("unused")
	private String nlDob;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public double scanLogic(Set<String> NList, Set<String> cust,
			double threshold) {
		ArrayList<Double> arrRes = new ArrayList<Double>();
		double main_result = 0.0;

		for (String cName : cust) {

			double result = 0.0;
			double temp_result = 0.0;

			@SuppressWarnings("unused")
			int k = 0;

			for (String neg : NList) {

				temp_result = FuzzyMatch.compare(neg.toUpperCase(),
						cName.toUpperCase()) * 100;

				// System.out.println(temp_result + " " + neg.toUpperCase() +
				// " "
				// + cName.toUpperCase());

				if (temp_result >= 0) {
					if (temp_result > result)
						result = temp_result;
				}
			}

			if (result > 0) {
				arrRes.add(result);
			}
		}

		// System.out.println(arrRes);

		for (double main_res : arrRes) {
			// System.out.println("Main Result :: " + main_result);
			main_result = main_res + main_result;
		}

		// System.out.println(cust.size() + " --- " + main_result);

		main_result = main_result / cust.size();
		return main_result;
	}

	@SuppressWarnings("unused")
	public void scanCustomerBase(String fullFlg) {
		Statement pstmt_nl = null;
		ResultSet rs3 = null;
		try {
			String cName = "";
			String cPCountry = "";
			String cCCountry = "";
			String cId = "";
			String cDob = "";
			String cNo = "";
			double threshold = 0;
			String sol = "";
			int i = 0;
			String nl_query = "";

			stmt = connection.createStatement();
			stmtUp = connection.createStatement();
			pstmt_nl = connection.createStatement();

			rs = stmt
					.executeQuery("select nvl(VALUE,'0') from p_aml_general where name='SDN_FUZZY_MATCH_PERCENTAGE'");
			while (rs.next()) {
				threshold = Double.parseDouble(rs.getString(1));
			}

			String sql = "SELECT a.cust_id, cust_name,(SELECT ref_desc FROM aml_rct WHERE ref_rec_type = '03' AND ref_code = a.cust_perm_cntry_code) AS perm_ctry,(SELECT ref_desc FROM aml_rct WHERE ref_rec_type = '03' AND ref_code = a.cust_commu_cntry_code) AS commu_ctry,nvl(to_char(cust_dob,'dd-mon-yyyy'),'-'),primary_sol_id,cust_pan_no||'#'||nvl(cust_passport_no,'-')||'#'||nvl(nat_id_card_num,'-') FROM aml_cust_master a,aml_kyc b WHERE a.cust_id = b.cust_id";

			if (fullFlg.equals("N"))
				sql += " and scan_flg = 'Y'";

			sql += " and cust_del_flg = 'N'";

			if (fullFlg.equals("N"))
				sql += " and rownum<2000";

			// System.out.println(sql);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				cId = rs.getString(1);
				cName = rs.getString(2) == null ? "-" : rs.getString(2);
				cPCountry = rs.getString(3) == null ? "NA" : rs.getString(3);
				cCCountry = rs.getString(4) == null ? "NA" : rs.getString(4);
				cDob = rs.getString(5) == null ? "NA" : rs.getString(5);
				sol = rs.getString(6) == null ? "NA" : rs.getString(6);
				cNo = rs.getString(7) == null ? "NA" : rs.getString(7);

				if ((!cCCountry.equals(ccOld)) && !cCCountry.equals("NA")) {
					System.out.println("ccOld " + ccOld + " -  " + cCCountry);
					ccOld = cCCountry;

					hsSusp = new HashSet<String>();

					nl_query = "SELECT TRIM (NAME)||'~'||a.person_id FROM aml_wc_name a,aml_wc_list z,aml_wc_description1_list l";

					if (fullFlg.equals("Y"))
						nl_query += ",aml_wc_master c";

					if (!cCCountry.equalsIgnoreCase("NA")) {
						nl_query += ",aml_wc_country d WHERE a.person_id = d.person_id and d.person_id=z.person_id and upper(desc_1) = upper(RECORD_DESC) and DESCRIPTION1_ID not in ('1','2')";

						if (fullFlg.equals("Y"))
							nl_query += " and d.person_id=c.person_id and to_date(aml_date,'dd-mon-yy')=to_date(sysdate,'dd-mon-yy')";
					}
					
					nl_query += " union ";
					nl_query += "SELECT TRIM (NAME)||'~'||a.person_id FROM aml_wc_alias a,aml_wc_list z,aml_wc_description1_list l";

					if (fullFlg.equals("Y"))
						nl_query += ",aml_wc_master c";

					if (!cCCountry.equalsIgnoreCase("NA")) {
						nl_query += ",aml_wc_country d WHERE a.person_id = d.person_id and d.person_id=z.person_id and upper(desc_1) = upper(RECORD_DESC) and DESCRIPTION1_ID not in ('1','2')";

						if (fullFlg.equals("Y"))
							nl_query += " and d.person_id=c.person_id and to_date(aml_date,'dd-mon-yy')=to_date(sysdate,'dd-mon-yy')";
							}
						//System.out.println(nl_query);

						rs3 = pstmt_nl.executeQuery(nl_query);
						while (rs3.next()) {
							hsSusp.add(rs3.getString(1));
						}
					

					System.out.println("Susp  refreshed!!!");
				}

				getCustomerScanned(cId, cName, sol, cPCountry, cDob, cNo,
						cCCountry, threshold, fullFlg, hsSusp);

				if (fullFlg.equals("N"))
					stmtUp.executeUpdate("update aml_cust_master set scan_flg = 'C' where cust_id  = '"
							+ cId + "'");

				// System.out.println("Customer Id :: " + (++i) + " :: " + cId);

				connection.commit();
			}
			// System.out.println("Scanning Completed......");
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
				if (pstmt_nl != null) {
					pstmt_nl.close();
					pstmt_nl = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void getCustomerScanned(String id, String custName, String sol,
			String custCntry, String custDob, String custPass, String commCtry,
			double threshold, String fullFlg, Set<String> hsSusp) {
		Statement stmt_flds = null;
		Statement stmtMsg = null;
		Statement stmt = null;
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmt_phenotic = null;
		PreparedStatement pstmt_process_stat = null;
		PreparedStatement pstmtSubAlert = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs4 = null;
		ResultSet rsSeq = null;
		ResultSet rsDist = null;
		String alert_id = "";
		String nl_query = "";
		String nl_id = "";
		String nl_name = "";
		@SuppressWarnings("unused")
		String phono = "";
		String role = "";
		String display_result = "";
		String name = "";
		@SuppressWarnings("unused")
		String cntryFlg = "";
		String dobFlg = "";
		String passFlg = "";
		Set<String> al_susp = null;
		StringTokenizer st = null;
		Set<String> nl_Set = null;
		@SuppressWarnings("unused")
		String country = "";
		int marks = 0;
		try {
			stmt = connection.createStatement();
			stmtMsg = connection.createStatement();

			// System.out.println(hsSusp.size());

			pstmt_sdn_alert = connection
					.prepareStatement("insert into aml_rule_ticket  values"
							+ "(?,'',?,?,'Wl','O',sysdate,sysdate,'NA','NA',to_date(sysdate,'dd/mm/yy'),to_date(sysdate,'dd/mm/yy'),'NO DATA','WLS001',?,'0','0','0','0',?,?,'W','0')");
			pstmtSubAlert = connection
					.prepareStatement("insert into AML_SDNSCANALERTDETAILS values (?,?,?,?,?,?,?)");
			pstmt_phenotic = connection
					.prepareStatement("select decode(count(*),'1','Y','N') from (select soundex(?) as wl from dual) a,(select soundex(?) as tkn from dual) b where a.wl=b.tkn");
			stmt_flds = connection.createStatement();

			al_susp = new HashSet<String>();

			if (custName != null) {

				// System.out.println(custName);
				Set<String> al_Set = null;

				// System.out.println(nl_query);

				// System.out.println(hsSusp);

				for (String nlList : hsSusp) {
					String nlTmp[] = nlList.split("~");

					nl_name = nlTmp[0];
					nl_id = nlTmp[1];

					nl_Set = new HashSet<String>();

					st = new StringTokenizer(nl_name, " ");
					String negToken = "";
					while (st.hasMoreTokens()) {
						negToken = st.nextToken();
						if (negToken != null) {
							nl_Set.add(negToken);
						}
					}

					al_Set = new HashSet<String>();

					st = new StringTokenizer(custName, " ");
					String custToken = "";
					while (st.hasMoreTokens()) {
						custToken = st.nextToken();
						if (custToken != null) {
							al_Set.add(custToken);
						}
					}

					int lenNl = 0;

					// System.out.println(nl_Set);

					for (String nltmp : nl_Set) {
						lenNl += nltmp.length();
					}

					// to check whether the String has
					// qualified
					// length

					double lenCust = 0;

					double percentMatch = 0;

					// System.out.println("data :: "+al_Set);

					for (String lenSt : al_Set) {
						lenCust += lenSt.length();
					}

					// System.out.println(lenCust);
					// System.out.println(lenNl);

					// System.out.println((lenCust / lenNl));

					// Calculating percent of match
					percentMatch = (lenCust / lenNl) * 100;

					// System.out.println(tSet);
					// Area for changing the logic the

					// percentMatch = 85;

					// System.out.println("percent :: " + percentMatch);

					// System.out.println(percentMatch >= 70
					// && percentMatch <= 120);
					//System.out.println(percentMatch + " : percentMatch");
					if (percentMatch >= 70 && percentMatch <= 120) {

						double result = 0;

						// System.out.println("Negative :: " + nl_Set);
						// System.out.println("Name :: " + al_Set);

						result = scanLogic(nl_Set, al_Set, threshold);

						// System.out.println(result);

						pstmt_phenotic.setString(1, nl_name);
						pstmt_phenotic.setString(2, name.trim());

						rs4 = pstmt_phenotic.executeQuery();
						while (rs4.next()) {
							phono = rs4.getString(1);
						}

						// System.out.println("Result ::: "+result);

						// System.out.println("Result :: " + result);
						//System.out.println("result : threshold "+ result +" : "+threshold);
						if (result >= threshold) {
							// To check the parameter match
							marks = 2;

							nlDob = "";

							rs4 = stmt
									.executeQuery("select decode(count(person_id),0,'N','Y') as cnt from aml_wc_date where person_id ='"
											+ nl_id
											+ "' and (day||'-'||month||'-'||year)='"
											+ custDob + "' ");
							while (rs4.next()) {
								dobFlg = rs4.getString("cnt");
								marks += 1;
							}

							 //End Of DOB Match 

							// ID Match 

							passFlg = "N";

							for (String custidproof : custPass.split("#")) {
								if (passFlg.equals("N")) {
									rs4 = stmt
											.executeQuery("select decode(count(1),0,'N','Y') from aml_wc_id where person_id = '"
													+ nl_id
													+ "' and idnum = '"
													+ custidproof + "'");
									while (rs4.next()) {
										passFlg = rs4.getString(1);
									}
								}
							}

							if (passFlg.equals("Y"))
								marks += 1;

							// End Of Id Scan 

							display_result = df.format(result);
							name = "";
							//if (marks > 1) {
								al_susp.add(nl_id + "~"
										+ custName.trim().toUpperCase() + "~"
										+ display_result + "~" + nl_name.trim()
										+ "~" + dobFlg + "~" + passFlg);
							//}
						}

					}

				}
			}

			if (al_susp.size() > 0) {
				// creating sequence
				rsSeq = stmt
						.executeQuery("SELECT 'AML'|| TO_CHAR (SYSDATE, 'DDMMYYHHMISS')|| LPAD (ticket_no.NEXTVAL, 5, 0) FROM DUAL");
				while (rsSeq.next()) {
					alert_id = rsSeq.getString(1);
				}

				// System.out.println(alert_id);
				// distribution of alerts
				rsDist = stmt
						.executeQuery("select nvl(sdn_distribution,value) from p_aml_general where name='DEFAULT_USER'");
				while (rsDist.next()) {
					role = rsDist.getString(1);
				}

				pstmt_sdn_alert.setString(1, alert_id);
				pstmt_sdn_alert.setString(2, id);
				pstmt_sdn_alert.setString(3, sol);
				pstmt_sdn_alert.setString(4, String.valueOf(threshold));
				pstmt_sdn_alert.setString(5,
						"Customer screening against Dow Jones watch list (Threshold :- "
								+ threshold + "%)");
				pstmt_sdn_alert.setString(6, String.valueOf(al_susp.size()));

				pstmt_sdn_alert.executeUpdate();

				for (String result : al_susp) {
					 //System.out.println(result);
					String temp[] = result.split("~");
					pstmtSubAlert.setString(1, alert_id);
					pstmtSubAlert.setString(2, temp[2]);
					pstmtSubAlert.setString(3, temp[0]);
					pstmtSubAlert.setString(4, temp[4]);
					pstmtSubAlert.setString(5, "Y");
					pstmtSubAlert.setString(6, "O");
					pstmtSubAlert.setString(7, temp[5]);

					pstmtSubAlert.executeUpdate();
				}
			}

			connection.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt_flds != null) {
					stmt_flds.close();
					stmt_flds = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsDist != null) {
					rsDist.close();
					rsDist = null;
				}
				if (rsSeq != null) {
					rsSeq.close();
					rsSeq = null;
				}
				if (pstmt_sdn_alert != null) {
					pstmt_sdn_alert.close();
					pstmt_sdn_alert = null;
				}
				if (pstmt_phenotic != null) {
					pstmt_phenotic.close();
					pstmt_phenotic = null;
				}
				if (pstmt_process_stat != null) {
					pstmt_process_stat.close();
					pstmt_process_stat = null;
				}
				if (pstmtSubAlert != null) {
					pstmtSubAlert.close();
					pstmtSubAlert = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs4 != null) {
					rs4.close();
					rs4 = null;
				}
				if (stmtMsg != null) {
					stmtMsg.close();
					stmtMsg = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public String checkCreateThread() {
		int cnt = 0;
		String flg = "N";
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
	
	public void run() {
		while (true) {
			if (checkCreateThread().equals("Y")) {
			scanCustomerBase("N");
			 //scanCustomerBase("Y");
			}
			try {
				Thread.sleep(1000*6);
			} catch (InterruptedException e) {
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
			process_run.start();
		} else {
			process_run.stop();
		}
	}

	public void start() {
		try {
			makeConnection();
			Thread t = new Thread(new AMLCustomerScreeningBA());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public static void main(String args[]) {
		makeConnection();
		Thread t = new Thread(new AMLCustomerScreeningBA());
		t.start();
	}

}
