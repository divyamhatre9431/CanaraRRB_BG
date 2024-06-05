package com.idbi.intech.iaml.swift;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class SplitSWIFTMessage implements Runnable {

	private static Connection con_aml = null;
	static SplitSWIFTMessage swObj = null;
	private static SplitSWIFTMessage process_run = new SplitSWIFTMessage();

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
	public void processMessageList(String in_out) {
		PreparedStatement pstmt_message = null;
		PreparedStatement pstmt_get = null;
		PreparedStatement pstmt_fld = null;
		PreparedStatement pstmtInsertTXN = null;
		PreparedStatement pstmtUpdateInfo = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs = null;
		String msg1 = "", msg2 = "", msg3 = "";
		String msgType = "";
		String msgId = "";
		ByteArrayInputStream bai = null;
		ArrayList<String> l_Fld = null;
		try {
			pstmt_message = con_aml
					.prepareStatement("select msg_id,msg_info1,msg_info2,msg_info3 from AML_SWIFT_MSG_INFO where PROCESS_STATUS = '2' and msg_type = ?");
			pstmt_fld = con_aml
					.prepareStatement("select fld_no from aml_swift_flds where msg_no = ? and in_out_flg = ?");
			pstmtInsertTXN = con_aml
					.prepareStatement("insert into aml_swift_in_msg_txn values(?,?,?,sysdate,?,?)");
			pstmtUpdateInfo = con_aml
					.prepareStatement("update AML_SWIFT_MSG_INFO set PROCESS_STATUS = ? where msg_id = ?");
			pstmt_get = con_aml
					.prepareStatement("select distinct(msg_no) from (select distinct(msg_no) as msg_no from aml_swift_flds) a,aml_swift_msg_info b where a.msg_no=b.msg_type and b.process_status = '2' and swift_type = ?");
			pstmt_get.setString(1, in_out);
			rs = pstmt_get.executeQuery();
			while (rs.next()) {
				msgType = rs.getString(1);

				l_Fld = new ArrayList<String>();

				pstmt_fld.setString(1, msgType);
				pstmt_fld.setString(2, in_out);
				rs2 = pstmt_fld.executeQuery();
				while (rs2.next()) {
					l_Fld.add(rs2.getString(1));
				}

				pstmt_message.setString(1, msgType);
				rs1 = pstmt_message.executeQuery();
				while (rs1.next()) {
					msgId = rs1.getString(1);
					Clob clob = rs1.getClob(2);
					Clob clob1 = rs1.getClob(3);
					Clob clob2 = rs1.getClob(4);
					if (clob != null) {
						if ((int) clob.length() > 0) {
							msg1 = clob.getSubString(1, (int) clob.length());
						}
					}
					if (clob1 != null) {
						if ((int) clob1.length() > 0) {
							msg2 = clob1.getSubString(1, (int) clob1.length());
						}
					}
					if (clob2 != null) {
						if ((int) clob2.length() > 0) {
							msg3 = clob2.getSubString(1, (int) clob2.length());
						}
					}

					String msgStruct = msg1 + msg2 + msg3;

					for (String fld_no : l_Fld) {
						int index = msgStruct
								.indexOf(":" + fld_no.trim() + ":");
						if (index > 0) {
							String fld_val = msgStruct.substring(index);
							char cData[] = fld_val.toCharArray();
							StringBuilder sb = new StringBuilder();
							for (int i = fld_no.trim().length() + 2; i < cData.length; i++) {
								sb.append(cData[i]);
								if (cData[i] == ':') {
									int end1 = i + 3;
									int end2 = i + 4;

									if (cData[end1] == ':'
											|| cData[end2] == ':') {
										break;
									}

								}
							}
							String val_tmp = sb.toString();
							String val = val_tmp.substring(0,
									val_tmp.length() - 1);
							if (val.endsWith("-}{5")) {
								val = val.substring(0, val_tmp.length() - 5);
							}

							bai = new ByteArrayInputStream(val.toString()
									.getBytes());
							pstmtInsertTXN.setString(1, msgId);
							pstmtInsertTXN.setString(2, fld_no.trim());
							pstmtInsertTXN.setAsciiStream(3, bai,
									bai.available());
							pstmtInsertTXN.setString(4, msgType);
							pstmtInsertTXN.setString(5, "");
							pstmtInsertTXN.executeUpdate();
						}
						pstmtUpdateInfo.setString(1, "3");
						pstmtUpdateInfo.setString(2, msgId);
						pstmtUpdateInfo.executeUpdate();
					}
				}
			}
			con_aml.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt_message != null) {
					pstmt_message.close();
					pstmt_message = null;
				}
				if (pstmt_fld != null) {
					pstmt_fld.close();
					pstmt_fld = null;
				}
				if (pstmtUpdateInfo != null) {
					pstmtUpdateInfo.close();
					pstmtUpdateInfo = null;
				}
				if (pstmtInsertTXN != null) {
					pstmtInsertTXN.close();
					pstmtInsertTXN = null;
				}
				if (pstmt_get != null) {
					pstmt_get.close();
					pstmt_get = null;
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		Thread t = new Thread(new SplitSWIFTMessage());
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
		swObj = new SplitSWIFTMessage();
		while (true) {
			swObj.processMessageList("I");
			swObj.processMessageList("O");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
