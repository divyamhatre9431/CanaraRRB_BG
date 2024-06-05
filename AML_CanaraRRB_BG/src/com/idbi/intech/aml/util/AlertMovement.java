package com.idbi.intech.aml.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AlertMovement {

	public void getAlertCount(String toDate, String fromDate, int percentage,
			String userId) {

		Statement stmtFetchCnt = null, stmtUpdate = null, stmt = null;
		ResultSet rsupdate = null, rs = null;
		Connection conn = null;

		int totalCount = 0, calculatedCount = 0;
		String ticketId=null,prevUserId=null;

		try {
			conn = ConnectionFactory.makeConnectionAMLLive();
			stmtFetchCnt = conn.createStatement();
			rs = stmtFetchCnt
					.executeQuery("select count(1) from aml_rule_ticket where (to_date(generated_time,'dd-mm-yy') between '"
							+ toDate
							+ "' and '"
							+ fromDate
							+ "') and ticket_status='C'");

			while (rs.next()) {
				totalCount = rs.getInt(1);

			}

			rs.close();
			stmtFetchCnt.close();

			calculatedCount = Math.round((totalCount / 100) * percentage);
			System.out.println(totalCount + " : " +calculatedCount);

			stmt = conn.createStatement();
			rsupdate = stmt
					.executeQuery("SELECT ticket_id,user_id FROM ( SELECT * FROM aml_rule_ticket ORDER BY dbms_random.VALUE ) where (to_date(generated_time,'dd-mm-yy') between '"
							+ toDate
							+ "' and '"
							+ fromDate
							+ "') and rownum <= "
							+ calculatedCount
							+ " and ticket_status='C'");

			stmtUpdate = conn.createStatement();
			while (rsupdate.next()) {
				ticketId=rsupdate.getString(1);
				prevUserId=rsupdate.getString(2);
				stmtUpdate.executeQuery("update aml_rule_ticket set USER_ID='"
						+ userId + "', ticket_status='G' where TICKET_ID='"
						+ ticketId + "'");
				System.out.println(ticketId+"~"+prevUserId);
				conn.commit();
				insertPOComment(conn,ticketId, "Agreed Tickets", userId);
			}
		

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
				if (stmtUpdate != null) {
					stmtUpdate.close();
					stmtUpdate = null;
				}
				if (stmt!= null) {
					stmt.close();
					stmt = null;
				}
				if (stmtFetchCnt != null) {
					stmtFetchCnt.close();
					stmtFetchCnt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void insertPOComment(Connection conn,String ticketId, String comment, String userId) {

		Statement stmtComment = null, stmtCommMaster = null;
		ResultSet rs = null;
		//Connection conn = null;
		String seq = null;

		try {
			//conn = ConnectionFactory.makeConnectionAMLLive();
			stmtComment = conn.createStatement();
			stmtCommMaster = conn.createStatement();
			rs = stmtComment
					.executeQuery("select to_char(sysdate,'DDMMYYHHMISS')||lpad(COMMENTSEQ.nextval,3,'0') from dual");

			while (rs.next()) {
				seq = rs.getString(1);
				stmtComment
						.executeQuery("insert into AML_RULE_TICKET_COMMENT(COMMENT_ID,USER_ID,USER_COMMENT,COMMENT_TIME,ROLE_ID,SUP_USERID,ACTION) values('"
								+ seq
								+ "','"
								+ userId
								+ "','"
								+ comment
								+ "',sysdate,'LEVEL5','XXX','')");

				stmtCommMaster
						.executeQuery("insert into AML_RULE_TICKET_COMMASTER(COMMENT_ID,TICKET_ID) values('"
								+ seq + "','" + ticketId + "')");

				conn.commit();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
//				if (conn != null) {
//					conn.close();
//					conn = null;
//				}
				if (stmtComment != null) {
					stmtComment.close();
					stmtComment = null;
				}
				if (stmtCommMaster != null) {
					stmtCommMaster.close();
					stmtCommMaster = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		AlertMovement alertmove = new AlertMovement();

		// alertmove.insertPOComment("AML18111811442273055", "comment test PO",
		// "VAR4873");

		alertmove.getAlertCount("01-apr-2019", "30-apr-2019", 2, "VAR4873");

	}
}
