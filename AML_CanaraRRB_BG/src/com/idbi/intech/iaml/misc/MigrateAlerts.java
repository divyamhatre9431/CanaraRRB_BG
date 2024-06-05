package com.idbi.intech.iaml.misc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class MigrateAlerts {

	/**
	 * @param args
	 */

	private static Connection connection;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void getOldAlerts() {
		Statement stmt = null;
		Statement stmtFirst = null;
		Statement stmtAcid = null;
		Statement stmtRule = null;
		Statement stmtCust = null;
		ResultSet rs = null;
		ResultSet rsFirst = null;
		ResultSet rsCust = null;
		Statement stmtInsert = null;
		Statement stmtInsertAgg = null;
		Statement stmtInsertmas = null;
		Statement stmtInsertcom = null;

		ArrayList<String> listAlertId = new ArrayList<String>();
		try {
			stmt = connection.createStatement();
			stmtAcid = connection.createStatement();
			stmtRule = connection.createStatement();
			stmtInsert = connection.createStatement();
			stmtInsertAgg = connection.createStatement();
			stmtInsertmas = connection.createStatement();
			stmtInsertcom = connection.createStatement();
			stmtFirst = connection.createStatement();
			stmtCust = connection.createStatement();
			
			rsCust = stmtCust
			.executeQuery("select distinct cust_id from aml_rule_ticket");
			while (rsCust.next()) {
				String custId=rsCust.getString(1);

			rsFirst = stmtFirst
					.executeQuery("select a.alert_id||'~'||alert_sr_id||'~'||alert_sub_sr_id||'~'||cust_id||'~'||nvl(cust_account_no,'-')||'~'||last_change_user_id||'~'||rule_start_date||'~'||rule_end_date from p_aml_alert_details_closed a,p_aml_alert_master@amlv1 b where a.alert_id=b.alert_id and cust_id='"+custId+"' and b.category <> 'SWIFT' and a.alert_id not in('AL-TXN-271211-00570','AL-TXN-270112-00694','AL-TXN-060112-00631','AL-TXN-270112-00695','AL-TXN-270112-00696','AL-TXN-270112-00697')");
			while (rsFirst.next()) {
				String alertId = rsFirst.getString(1);

				String acid = "";
				String branch = "";
				String iba = "";
				String alertDescription = "";
				String sAmt = "";
				String endAmt = "";
				String noOfDays = "";
				String noOftxns = "";
				String noOfaccts = "";
				String category = "";
				String suspId = "";
				String tickId = "";
				String tranId = "";
				String partTran = "";
				String tranDate = "";
				String ddTime = "";

				String alertInfo[] = alertId.split("~");

				if (alertInfo[4].equals("-")) {
					acid = null;
					rs = stmtAcid
							.executeQuery("select primary_sol_id from aml_cust_master where cust_id =lpad('"
									+ alertInfo[3].trim() + "','9',' ')");
					while (rs.next()) {
						branch = rs.getString(1);
					}
				} else {
					rs = stmtAcid
							.executeQuery("select cust_acid from aml_ac_master where cust_ac_no ='"
									+ alertInfo[4].trim() + "'");
					while (rs.next()) {
						acid = rs.getString(1);
					}
					rs = stmtAcid
							.executeQuery("select primary_sol_id from aml_cust_master where cust_id =lpad('"
									+ alertInfo[3].trim() + "','9',' ')");
					while (rs.next()) {
						branch = rs.getString(1);
					}
				}

				rs = stmtRule
						.executeQuery("select iba_map,alert_description,start_amt,end_amt,no_of_days,no_of_txns,no_of_accounts,category  from rule_map_test where alert_id='"
								+ alertInfo[0] + "'");
				while (rs.next()) {
					iba = rs.getString(1);
					alertDescription = rs.getString(2);
					sAmt = rs.getString(3);
					endAmt = rs.getString(4);
					noOfDays = rs.getString(5);
					noOftxns = rs.getString(6);
					noOfaccts = rs.getString(7);
					category = rs.getString(8);
				}

				rs = stmtRule
						.executeQuery("select sup_userid from aml_user_master where user_id = '"
								+ alertInfo[5] + "'");
				while (rs.next()) {
					suspId = rs.getString(1);
				}

				rs = stmtRule
						.executeQuery("SELECT    'AML'|| TO_CHAR (sysdate, 'ddmmyyhh24miss')|| migrationseq.NEXTVAl,TO_CHAR (generated_time, 'ddmmyyhh24miss') from p_aml_alert_details_closed where alert_id ='"
								+ alertInfo[0]
								+ "' and alert_sr_id='"
								+ alertInfo[1]
								+ "' and alert_sub_sr_id='"
								+ alertInfo[2] + "'");
				while (rs.next()) {
					tickId = rs.getString(1);
					ddTime = rs.getString(2);
				}

				stmtInsert
						.executeUpdate("insert into aml_rule_ticket_arch values( '"
								+ tickId
								+ "','"
								+ acid
								+ "','"
								+ alertInfo[3]
								+ "','"
								+ branch
								+ "','"
								+ category
								+ "','C',(select generated_time from p_aml_alert_details_closed where alert_id ='"
								+ alertInfo[0]
								+ "' and alert_sr_id='"
								+ alertInfo[1]
								+ "' and alert_sub_sr_id='"
								+ alertInfo[2]
								+ "'),(select last_change_date from p_aml_alert_details_closed where alert_id ='"
								+ alertInfo[0]
								+ "' and alert_sr_id='"
								+ alertInfo[1]
								+ "' and alert_sub_sr_id='"
								+ alertInfo[2]
								+ "'),'"
								+ alertInfo[5]
								+ "','"
								+ suspId
								+ "','"
								+ alertInfo[6]
								+ "','"
								+ alertInfo[7]
								+ "','NO_DATA','"
								+ iba
								+ "','"
								+ sAmt
								+ "','"
								+ endAmt
								+ "','"
								+ noOftxns
								+ "','"
								+ noOfDays
								+ "','"
								+ noOfaccts
								+ "','"
								+ alertDescription + "','0','F','0')");

				rs = stmtRule
						.executeQuery("select tran_id,to_char(tran_date,'dd-mon-yyyy'),part_tran_srl_num from aml_agg_alert_dtls where alert_id ='"
								+ alertInfo[0]
								+ "' and alert_sr_id='"
								+ alertInfo[1]
								+ "' and alert_sub_sr_id='"
								+ alertInfo[2] + "'");
				while (rs.next()) {
					tranId = rs.getString(1);
					tranDate = rs.getString(2);
					partTran = rs.getString(3);
				}

				stmtInsertAgg
						.executeUpdate("insert into aml_rule_ticket_agg_temp values ('"
								+ tickId
								+ "','"
								+ tranId
								+ "','"
								+ tranDate
								+ "','"
								+ partTran
								+ "',(select generated_time from p_aml_alert_details_closed where alert_id ='"
								+ alertInfo[0]
								+ "' and alert_sr_id='"
								+ alertInfo[1]
								+ "' and alert_sub_sr_id='"
								+ alertInfo[2] + "'))");

				ArrayList<String> comment = new ArrayList<String>();

				rs = stmtRule
						.executeQuery("select nvl(alert_comment,'-'),comment_by,to_char(comment_date,'dd-mon-yyyy hh24:mi:ss'),(select sup_userid from aml_user_master where user_id=a.comment_by),(select role_id from aml_user_master where user_id=a.comment_by),to_char(sysdate,'ddmmyyhh24miss')||migratecommentseq.nextval from aml_alert_comment a where alert_id ='"
								+ alertInfo[0]
								+ "' and alert_sr_id='"
								+ alertInfo[1]
								+ "' and alert_sub_sr_id='"
								+ alertInfo[2] + "'");
				while (rs.next()) {
					String aComment = rs.getString(1);
					String aCommentby = rs.getString(2);
					String tDate = rs.getString(3);
					String uId = rs.getString(4);
					String rId = rs.getString(5);
					String commentId = rs.getString(6);

					comment.add(commentId + "~" + aCommentby + "~" +  aComment.replace("~", "") + "~" + tDate
									+ "~" + rId + "~" + uId);
				}

				for (String comm : comment) {
					String c[] = comm.split("~");
					stmtInsertmas
							.executeUpdate("insert into aml_rule_ticket_comment_temp values('"
									+ c[0]
									+ "','"
									+ c[1]
									+ "','"
									+ c[2].replace("'", " ")
									+ "',to_date('"
									+ c[3]
									+ "','dd-mon-yy hh24:mi:ss'),'"
									+ c[4] + "','" + c[5] + "')");

					stmtInsertmas
							.executeUpdate("insert into aml_rule_ticket_commaster_temp values('"
									+ c[0] + "','" + tickId + "')");
				}

				connection.commit();

			}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		makeConnection();
		new MigrateAlerts().getOldAlerts();

	}

}
