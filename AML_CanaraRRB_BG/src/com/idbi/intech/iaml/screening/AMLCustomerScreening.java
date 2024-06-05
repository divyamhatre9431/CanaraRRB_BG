package com.idbi.intech.iaml.screening;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.tool.FuzzyMatch;

public class AMLCustomerScreening {
	private static Connection connection = null;
	DecimalFormat df = new DecimalFormat("###.##");

	public static void loadOMSDB() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public double scanLogic(Set<String> NList, Set<String> cust,
			double threshold) {
		ArrayList<Double> arrRes = new ArrayList<Double>();
		double main_result = 0.0;

		for (String cName : cust) {

			double result = 0.0;
			double temp_result = 0.0;

			int k = 0;

			for (String neg : NList) {

				temp_result = FuzzyMatch.compare(neg.toUpperCase(),
						cName.toUpperCase()) * 100;

				if (temp_result >= threshold) {
					result += temp_result;
					k++;

				}
			}

			if (result > 0) {
				arrRes.add(result / k);
			}
		}

		for (double main_res : arrRes) {
			main_result = main_res + main_result;
		}

		main_result = main_result / cust.size();
		return main_result;
	}

	public void customerScreen(String scanDate, String type) {
		Statement stmt = null;
		ResultSet rsCust = null;
		String name = "";
		String id = "";
		String passport = "";
		String dob = "";
		String country = "";
		String vertical = "";
		String sol = "";
		int cnt = 0;

		try {
			stmt = connection.createStatement();

			String customerSQL = "select cust_name,cust_id,nvl(cust_passport_no,'-') as passport,nvl(to_char(cust_dob,'DD-MON-YYYY'),'') as dob,nvl((select ref_desc from rct@dubai_live where ref_rec_type= '03' and ref_code = a.cust_perm_cntry_code),'-') as country1,nvl((select ref_desc from rct@dubai_live where ref_rec_type= '03' and ref_code = a.CUST_COMMU_CNTRY_CODE),'-') as country2,cust_vertical_code,primary_sol_id from aml_cust_master a where ";

			if (type.equals("NEW")) {
				customerSQL += "aml_upload_date='" + scanDate + "'";
			}

			if (type.equals("OLD")) {
				customerSQL += "aml_upload_date!='" + scanDate + "'";
			}
			
			//System.out.println(customerSQL);

			rsCust = stmt.executeQuery(customerSQL);

			while (rsCust.next()) {
				name = rsCust.getString("cust_name") != null ? rsCust
						.getString("cust_name").replaceAll("'", "") : ""
						.replaceAll("'", "");
				id = rsCust.getString("cust_id");
				passport = rsCust.getString("passport");
				dob = rsCust.getString("dob") == null ? "" : rsCust
						.getString("dob");
				country = rsCust.getString("country1").equals("-") ? rsCust
						.getString("country2") : rsCust.getString("country1");
				vertical = rsCust.getString("cust_vertical_code") == null ? "NA"
						: rsCust.getString("cust_vertical_code");
				sol = rsCust.getString("primary_sol_id");

				
				getCustomerScanned(id, name, vertical, sol, country, dob,
						passport);
				
				System.out.println("Scanned :: "+(cnt++)+" :: "+id);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}

	private void getCustomerScanned(String id, String custName,
			String vertical, String sol, String custCntry, String custDob,
			String custPass) {
		Statement stmt_flds = null;
		Statement stmtMsg = null;
		Statement stmt = null;
		Statement pstmt_nl = null;
		PreparedStatement pstmt_threshold = null;
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmt_phenotic = null;
		PreparedStatement pstmt_process_stat = null;
		PreparedStatement pstmtSubAlert = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
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
		String cntryFlg = "";
		String dobFlg = "";
		String passFlg = "";
		String day = "";
		String month = "";
		String year = "";
		Set<String> al_susp = null;
		double threshold = 0.0;
		StringTokenizer st = null;
		Set<String> nl_Set = null;
		try {
			pstmt_nl = connection.createStatement();
			stmt = connection.createStatement();
			stmtMsg = connection.createStatement();
			pstmt_threshold = connection
					.prepareStatement("select VALUE from p_aml_general where name='SDN_FUZZY_MATCH_PERCENTAGE'");
			pstmt_sdn_alert = connection
					.prepareStatement("insert into AML_SDN_ALERT_DETAILS  values"
							+ "(?,?,?,?,sysdate,?,sysdate,?,?,?,?,?,?)");
			pstmtSubAlert = connection
					.prepareStatement("insert into AML_SDNSCANALERTDETAILS values (?,?,?,?,?,?,?)");
			pstmt_phenotic = connection
					.prepareStatement("select decode(count(*),'1','Y','N') from (select soundex(?) as wl from dual) a,(select soundex(?) as tkn from dual) b where a.wl=b.tkn");
			stmt_flds = connection.createStatement();

			rs = pstmt_threshold.executeQuery();
			while (rs.next()) {
				threshold = Double.parseDouble(rs.getString(1));
			}

			al_susp = new HashSet<String>();

			if (custName != null) {
				Set<String> al_Set = null;

				nl_query = "select trim(name),a.person_id from aml_wc_name a,aml_wc_list b where a.person_id = b.person_id and (desc_2 in ('SANCTIONS LISTS','CORRUPTION','TERROR','FINANCIAL CRIME') or desc_1='SPECIAL INTEREST ENTITY (SIE)' or desc_3 in ('COUNTRY','SHIP','AIRCRAFT'))";
				// nl_query =
				// "select trim(name),person_id from aml_wc_name@aml_dubai where person_id = '677679'";
				// System.out.println("NL : " + nl_query);

				rs3 = pstmt_nl.executeQuery(nl_query);
				while (rs3.next()) {
					nl_name = rs3.getString(1);
					nl_id = rs3.getString(2);

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

					for (String nltmp : nl_Set) {
						lenNl += nltmp.length();
					}

					// to check whether the String has
					// qualified
					// length

					int lenCust = 0;

					int percentMatch = 0;

					for (String lenSt : al_Set) {
						lenCust += lenSt.length();
					}

					// Calculating percent of match
					percentMatch = (lenCust / lenNl) * 100;

					// System.out.println(tSet);
					// Area for changing the logic the

					// System.out.println("percent :: "+percentMatch);

					if (percentMatch >= 85 && percentMatch <= 115) {

						double result = 0;

						result = scanLogic(nl_Set, al_Set, threshold);

						if (result >= threshold) {
							pstmt_phenotic.setString(1, nl_name);
							pstmt_phenotic.setString(2, name.trim());

							rs4 = pstmt_phenotic.executeQuery();
							while (rs4.next()) {
								phono = rs4.getString(1);
							}

							rs4 = stmt
									.executeQuery("select decode(count(1),0,'N','Y')  as cntry from aml_wc_country where  person_id = '"
											+ nl_id
											+ "' and country_cd in (select country_code from aml_wc_country_list where  instr(upper(country_name),'"
											+ custCntry + "')>0)");
							while (rs4.next()) {
								cntryFlg = rs4.getString("cntry");
							}

							rs4 = stmt
									.executeQuery("select day,month,year as dob from aml_wc_date where person_id = '"
											+ nl_id
											+ "' and  trim(date_type) in ('DATE OF REGISTRATION','DATE OF BIRTH')");
							while (rs4.next()) {
								day = rs4.getString(1) == null ? "NA" : rs4
										.getString(1).trim().equals("") ? "NA"
										: rs4.getString(1);
								month = rs4.getString(2) == null ? "NA" : rs4
										.getString(2).trim().equals("") ? "NA"
										: rs4.getString(2);
								year = rs4.getString(3) == null ? "NA" : rs4
										.getString(3).trim().equals("") ? "NA"
										: rs4.getString(3);
							}

							if (custDob.contains("-")) {
								boolean d = false, m = false, y = false;
								int i = 0;
								String arrDob[] = custDob.split("-");
								for (String birth : arrDob) {
									i++;
									if (i == 1) {
										if (birth.equalsIgnoreCase(day)) {
											d = true;
										}
									}
									if (i == 2) {
										if (birth.equalsIgnoreCase(month)) {
											m = true;
										}
									}
									if (i == 3) {
										if (birth.equalsIgnoreCase(year)) {
											y = true;
										}
									}
								}

								if (d && m && y) {
									dobFlg = "Y";
								} else {
									dobFlg = "N";
								}
							} else {
								dobFlg = "N";
							}

							rs4 = stmt
									.executeQuery("select decode(count(1),0,'N','Y') from aml_wc_id where person_id = '"
											+ nl_id
											+ "' and instr(upper(idtype),'PASSPORT')>0 and trim(idnum) =(select cust_passport_no from aml_cust_master where cust_id = '"
											+ id + "')");
							while (rs4.next()) {
								passFlg = rs4.getString(1);
							}

							display_result = df.format(result);
							name = "";

							if (cntryFlg.equalsIgnoreCase("Y"))
								al_susp.add(nl_id + "~"
										+ custName.trim().toUpperCase() + "~"
										+ display_result + "~" + nl_name.trim()
										+ "~" + dobFlg + "~" + passFlg);
						}
					}

				}
			}

			if (al_susp.size() > 0) {
				// creating sequence
				rsSeq = stmt
						.executeQuery("select 'AL'||to_char(sysdate,'ddmmyyhh24miss')||lpad(sdnalert.nextval,4,0) from dual");
				while (rsSeq.next()) {
					alert_id = rsSeq.getString(1);
				}

				// distribution of alerts
				//rsDist = stmt.executeQuery("select getsdnrole('" + sol + "','"
				//		+ vertical + "') from dual");
				//while (rsDist.next()) {
					role = rsDist.getString(1);
				//}
					
				role = "RAVI";

				pstmt_sdn_alert.setString(1, alert_id);
				pstmt_sdn_alert.setString(2, "SDN Alerts");
				pstmt_sdn_alert.setString(3, "O");
				pstmt_sdn_alert.setString(4, "SDN");
				pstmt_sdn_alert.setString(5, "SYSTEM");
				pstmt_sdn_alert.setString(6, role);
				pstmt_sdn_alert.setString(7, "N");
				pstmt_sdn_alert.setString(8, "");
				pstmt_sdn_alert.setString(9, sol);
				pstmt_sdn_alert.setString(10, id);
				pstmt_sdn_alert.setString(11, vertical);

				pstmt_sdn_alert.executeUpdate();

				for (String result : al_susp) {
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
				if (pstmt_nl != null) {
					pstmt_nl.close();
					pstmt_nl = null;
				}
				if (pstmt_threshold != null) {
					pstmt_threshold.close();
					pstmt_threshold = null;
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
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
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

	public static void main(String args[]) {
		loadOMSDB();
		new AMLCustomerScreening().customerScreen("21-may-14", "NEW");
		// new AMLCustomerScreening().customerScreen("22-apr-14", "OLD");
	}

}
