package com.idbi.intech.iaml.swift;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ParseFile implements Runnable {

	private static Connection con_aml = null;
	static ParseFile pfObj = null;
	public static ParseFile process_run = new ParseFile();

	protected static void LoadDatabaseAml() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLive();
			con_aml.setAutoCommit(false);
		} catch (SQLException sqlExp) {
			System.exit(0);
		}
		System.out
				.println("===========================================================");
	}

	@SuppressWarnings("unused")
	private void splitMessage() {
		PreparedStatement pstmt_get = null;
		PreparedStatement pstmt_date = null;
		PreparedStatement pstmt_insert = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String msg = "";
		String msg_id = "";
		String strRef = "";
		String sw_type = "";
		String msgBlock2 = "";
		String msgType = "";
		String sender_bank = "";
		String send_date = "";
		String msgBlock1 = "";
		String recv_bank = "";
		String sender_coun = "";
		String sender_reg = "";
		String recv_coun = "";
		String recv_reg = "";
		String sol = "";
		String msg_priority = "";
		String mir = "";
		String send_time = "";
		String recv_time = "";
		String net_stat = "";
		String date_time = "";
		try {
			pstmt_date = con_aml
					.prepareStatement("select to_char(sysdate,'yymmdd') from dual");
			pstmt_get = con_aml
					.prepareStatement("select msg_id,msg_info1||msg_info2||msg_info3 from AML_SWIFT_MSG_INFO where PROCESS_STATUS = '1'");
			pstmt_insert = con_aml
					.prepareStatement("update AML_SWIFT_MSG_INFO set MUR=?,SEND_BANK_ID=?,SOL=?,"
							+ "SENDER_COUNTRY=?,SENDER_REGION=?,RECV_BANK_ID=?,RECV_COUNTRY=?,"
							+ "RECV_REGION=?,SPLIT_FLAG=?,PROCESS_STATUS=?,MSG_PRIORITY=?,MSG_DATE=?,MIR=?,SEND_TIME=?,RECV_TIME=?,SWIFT_TYPE=?,NET_STATUS=?,MSG_TYPE=? where MSG_ID=?");

			rs = pstmt_get.executeQuery();
			while (rs.next()) {

				msg_id = rs.getString(1);
				Clob clob = rs.getClob(2);
				if (clob != null) {
					if ((int) clob.length() > 0) {
						msg = clob.getSubString(1, (int) clob.length());
					}
				}

				strRef = msg.substring(msg.indexOf("{108:") + 5,
						msg.indexOf("{108:") + 11);

				sw_type = msg.substring(msg.indexOf("{2:") + 3,
						msg.indexOf("{2:") + 4);

				if (sw_type.equals("I")) {
					rs1 = pstmt_date.executeQuery();
					while (rs1.next()) {
						send_date = rs1.getString(1);
					}

					// information of sender
					msgBlock1 = msg.substring(msg.indexOf("{1:") + 3,
							msg.indexOf("1:") + 27);
					sender_bank = msgBlock1.substring(3, 3 + 8);
					sender_bank = sender_bank + msgBlock1.substring(12, 12 + 3);
					sender_coun = sender_bank.substring(4, 6);
					sender_reg = sender_bank.substring(6, 6 + 2);
					net_stat = msgBlock1.substring(0, 0 + 3);
					if (net_stat.equals("F21")) {
						net_stat = "Network Ack";
					} else if (net_stat.equals("F01")) {
						net_stat = "Financial Message";
					}

					// information of receiver
					msgBlock2 = msg.substring(msg.indexOf("{2:") + 3,
							msg.indexOf("{2:") + 21);
					msgType = msgBlock2.substring(1, 4);
					recv_bank = msgBlock2.substring(4, 4 + 11);
					recv_coun = recv_bank.substring(4, 6);
					recv_reg = recv_bank.substring(6, 6 + 2);
					sol = recv_bank.substring(8, 8 + 3);
					msg_priority = msgBlock2.substring(12, 12 + 1);
					mir = "";
					send_time = "";
					recv_time = "";
				}

				else if (sw_type.equals("O")) {
					// information of sender
					// System.out.println("inside here");
					msgBlock2 = msg.substring(msg.indexOf("{2:") + 3,
							msg.indexOf("{2:") + 50);
					msgType = msgBlock2.substring(1, 4);
					sender_bank = msgBlock2.substring(14, 22);
					sender_bank = sender_bank + msgBlock2.substring(23, 26);
					send_date = msgBlock2.substring(36, 42);
					sender_coun = sender_bank.substring(4, 6);
					sender_reg = sender_bank.substring(6, 6 + 2);
					sol = sender_bank.substring(8, 8 + 3);
					msg_priority = msgBlock2.substring(46, 47);
					mir = msgBlock2.substring(8, 8 + 28);
					send_time = msgBlock2.substring(4, 4 + 4);
					recv_time = msgBlock2.substring(42, 42 + 4);

					// information of receiver
					msgBlock1 = msg.substring(msg.indexOf("{1:") + 3,
							msg.indexOf("1:") + 27);
					recv_bank = msgBlock1.substring(3, 3 + 8);
					recv_bank = recv_bank + msgBlock1.substring(12, 12 + 3);
					recv_coun = recv_bank.substring(4, 6);
					recv_reg = recv_bank.substring(6, 6 + 2);
					net_stat = msgBlock1.substring(0, 0 + 3);
					if (net_stat.equals("F21")) {
						net_stat = "Network Ack";
					} else if (net_stat.equals("F01")) {
						net_stat = "Financial Message";
					}
				}

				pstmt_insert.setString(1, strRef);
				pstmt_insert.setString(2, sender_bank);
				pstmt_insert.setString(3, sol);
				pstmt_insert.setString(4, sender_coun);
				pstmt_insert.setString(5, sender_reg);
				pstmt_insert.setString(6, recv_bank);
				pstmt_insert.setString(7, recv_coun);
				pstmt_insert.setString(8, recv_reg);
				pstmt_insert.setString(9, "Y");
				pstmt_insert.setString(10, "2");
				pstmt_insert.setString(11, msg_priority);
				pstmt_insert.setString(12, send_date);
				pstmt_insert.setString(13, mir);
				pstmt_insert.setString(14, send_time);
				pstmt_insert.setString(15, recv_time);
				pstmt_insert.setString(16, sw_type.equals("I") ? "O" : "I");
				pstmt_insert.setString(17, net_stat);
				pstmt_insert.setString(18, msgType);
				pstmt_insert.setString(19, msg_id);

				pstmt_insert.executeUpdate();

				con_aml.commit();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt_get != null) {
					pstmt_get.close();
					pstmt_get = null;
				}
				if (pstmt_date != null) {
					pstmt_date.close();
					pstmt_date = null;
				}
				if (pstmt_insert != null) {
					pstmt_insert.close();
					pstmt_insert = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		Thread t = new Thread(new ParseFile());
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
			LoadDatabaseAml();
			Thread t_upload = new Thread(process_run);
			t_upload.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	@Override
	public void run() {
		pfObj = new ParseFile();
		while (true) {
			pfObj.splitMessage();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
