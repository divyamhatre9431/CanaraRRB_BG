package com.idbi.intech.iaml.swift;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.screening.AMLCustomerScreeningBA;

public class AMLSWIFTReadParseFile implements Runnable {
	private static Connection con_aml = null;
	private ArrayList<String> arrMsg = null;
	private String extension = "";
	static AMLSWIFTReadParseFile readObj = null;
	final ResourceBundle bundle_path = ResourceBundle
			.getBundle("com.idbi.intech.iaml.swift.SWIFT_Path");
	final String srcFile = bundle_path.getString("FILE_SRC_IN");
	final String destFile = bundle_path.getString("FILE_DEST_IN");
	static AMLSWIFTReadParseFile process_run = new AMLSWIFTReadParseFile();
	private ResultSet rs_dist = null;
	private Statement pstmt_dist = null;

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

	public void insertMsg(String src, String dest, String fname,
			String loc_for, String user_id) {
		PreparedStatement pstmtSeq = null;
		PreparedStatement pstmtInsertMsg = null;
		PreparedStatement pstmt_fname = null;
		Statement st_msg = null;
		ByteArrayInputStream bai = null;
		ByteArrayInputStream bai1 = null;
		File file1 = null;
		File file2 = null;
		File file = null;
		String strTemp = "";
		@SuppressWarnings("unused")
		int firstIndex = 0;
		@SuppressWarnings("unused")
		ByteArrayInputStream bai2 = null;
		ResultSet rs = null;
		String msg_id = null;
		String message = "";
		int iIncrement = 0;
		int iNoOfSplit = 0;
		int FixedSplitLen = 4000;
		InputStream is = null;
		InputStream in = null;
		OutputStream out = null;
		BufferedInputStream bis = null;
		ArrayList<String> arrStr = null;
		String msgType = null;
		ArrayList<String> alMsgNo = null;
		StringBuilder strBuild = null;
		String process = "";

		try {

			st_msg = con_aml.createStatement();
			alMsgNo = new ArrayList<String>();
			strBuild = new StringBuilder();
			strBuild.append("select distinct(msg_no) from aml_swift_flds where in_out_flg in ('I','O')");
			rs = st_msg.executeQuery(strBuild.toString());
			while (rs.next()) {
				alMsgNo.add(rs.getString(1));
			}

			arrMsg = new ArrayList<String>();
			file = new File(src + fname);
			is = new FileInputStream(file);
			bis = new BufferedInputStream(is);
			byte[] bytes = new byte[(int) file.length()];
			bis.read(bytes);
			message = new String(bytes);

			firstIndex = message.indexOf("{1:F21");
			while (message.indexOf("{1:F21", 7) != -1) {
				strTemp = message.substring(0, message.indexOf("{1:F21", 7));
				msgType = (strTemp.substring(strTemp.indexOf("{2:") + 3,
						strTemp.indexOf("{2:") + 50)).substring(1, 4);
				if (alMsgNo.contains(msgType))
					arrMsg.add(strTemp);
				message = message.substring(message.indexOf("{1:F21", 7));
				strTemp = "";
			}
			if (strTemp.equalsIgnoreCase("")) {
				msgType = (message.substring(message.indexOf("{2:") + 3,
						message.indexOf("{2:") + 50)).substring(1, 4);
				if (alMsgNo.contains(msgType))
					arrMsg.add(message);
			}

			pstmtSeq = con_aml
					.prepareStatement("select 'MSG-'||to_char(sysdate,'ddmmyy')||'-'||lpad(MSG_ID_SEQ.nextval,5,0),processdist from dual");

			pstmtInsertMsg = con_aml
					.prepareStatement("insert into AML_SWIFT_MSG_INFO(MSG_ID,MSG_INFO1,MSG_INFO2,MSG_INFO3"
							+ ",FILE_NAME,STATUS,PROCESS_STATUS,SPLIT_FLAG,"
							+ "SOL_FND_FLG,FILE_CRE_FLG,MQ_FLG,ALERT_STATUS,GENERATED_TIME,USER_ID,LOCAL_MSG,PROCESS) values(?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?)");
			// pstmt_fname = con_aml
			// .prepareStatement("insert into aml_swift_msg_info_print(filename,status,LOC_FOR,upload_time) values(?,?,?,sysdate)");

			for (int i = 0; i < arrMsg.size(); i++) {
				rs = pstmtSeq.executeQuery();
				while (rs.next()) {
					msg_id = rs.getString(1);
					process = rs.getString(2);
				}
				arrStr = new ArrayList<String>();
				long tot_Mlen = arrMsg.get(i).length();

				if (tot_Mlen <= FixedSplitLen) {
					iNoOfSplit = 1;
				} else if (tot_Mlen > FixedSplitLen
						&& tot_Mlen <= 2 * FixedSplitLen) {
					iNoOfSplit = 2;
				} else {
					iNoOfSplit = 3;
				}
				iIncrement = 0;

				if (iNoOfSplit == 1) {
					arrStr.add(arrMsg.get(i).substring(0, ((int) tot_Mlen)));
				} else {
					for (int j = 0; j < iNoOfSplit; j++) {
						if (j == iNoOfSplit - 1) {
							arrStr.add(arrMsg.get(i)
									.substring((iIncrement - 1)));
						} else {
							arrStr.add(arrMsg.get(i).substring(iIncrement,
									iIncrement + FixedSplitLen));
							iIncrement = iIncrement + FixedSplitLen + 1;
						}
					}
				}

				pstmtInsertMsg.setString(1, msg_id);
				if (arrStr.size() == 1) {
					bai = new ByteArrayInputStream(arrStr.get(0).getBytes());
					pstmtInsertMsg.setAsciiStream(2, bai, bai.available());
					pstmtInsertMsg.setString(3, "");
					pstmtInsertMsg.setString(4, "");
				}
				if (arrStr.size() == 2) {
					bai = new ByteArrayInputStream(arrStr.get(0).getBytes());
					bai1 = new ByteArrayInputStream(arrStr.get(1).getBytes());
					pstmtInsertMsg.setAsciiStream(2, bai, bai.available());
					pstmtInsertMsg.setAsciiStream(3, bai1, bai1.available());
					pstmtInsertMsg.setString(4, "");
				}
				if (arrStr.size() == 3) {
					bai = new ByteArrayInputStream(arrStr.get(0).getBytes());
					bai1 = new ByteArrayInputStream(arrStr.get(1).getBytes());
					bai2 = new ByteArrayInputStream(arrStr.get(1).getBytes());
					pstmtInsertMsg.setAsciiStream(2, bai, bai.available());
					pstmtInsertMsg.setAsciiStream(3, bai1, bai1.available());
					pstmtInsertMsg.setAsciiStream(4, bai1, bai1.available());
				}
				pstmtInsertMsg.setString(4, "");
				pstmtInsertMsg.setString(5, fname);
				pstmtInsertMsg.setString(6, "C");
				pstmtInsertMsg.setString(7, "1");
				pstmtInsertMsg.setString(8, "N");
				pstmtInsertMsg.setString(9, "N");
				pstmtInsertMsg.setString(10, "N");
				pstmtInsertMsg.setString(11, "N");
				pstmtInsertMsg.setString(12, "O");
				pstmtInsertMsg.setString(13, user_id);
				pstmtInsertMsg.setString(14, loc_for);
				pstmtInsertMsg.setString(15, process);
				pstmtInsertMsg.executeUpdate();

				// pstmt_fname.setString(1, fname);
				// pstmt_fname.setString(2, "N");
				// pstmt_fname.setString(3, loc_for);
				// pstmt_fname.executeUpdate();

			}

			file1 = new File(src + fname);
			file2 = new File(dest + fname);
			in = new FileInputStream(file1);
			out = new FileOutputStream(file2);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			con_aml.commit();

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmtInsertMsg != null) {
					pstmtInsertMsg.close();
					pstmtInsertMsg = null;
				}
				if (pstmt_fname != null) {
					pstmt_fname.close();
					pstmt_fname = null;
				}
				if (pstmtSeq != null) {
					pstmtSeq.close();
					pstmtSeq = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (in != null) {
					in.close();
					in = null;
				}
				if (out != null) {
					out.close();
					out = null;
				}
				if (bis != null) {
					bis.close();
					bis = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
				if (st_msg != null) {
					st_msg.close();
					st_msg = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void deleteFile(String src, String fname) {
		File file = new File(src + fname);
		file.delete();
	}

	public ArrayList<String> getFileNamesDir(String fname) {
		System.out.println(fname);
		ArrayList<String> filname = new ArrayList<String>();
		File file = new File(fname);
		String[] listOfFiles = file.list();
		for (String element : listOfFiles) {
			int dotPos = element.lastIndexOf(".");
			extension = element.substring(dotPos);
			if (extension.equalsIgnoreCase(".out")
					|| extension.equalsIgnoreCase(".inn")
					|| extension.equalsIgnoreCase(".txt"))
				filname.add(element);
		}
		return filname;
	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		Thread t = new Thread(new AMLSWIFTReadParseFile());
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
			Thread t = new Thread(new AMLSWIFTReadParseFile());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	// Parsing the read files
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

	// Split Message Field Wise
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
									break;
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
						con_aml.commit();
					}
				}
			}
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

	@Override
	public void run() {
		ArrayList<String> arrFiles = null;
		System.out.println("iAML Reading SWIFT files......");
		try {
			pstmt_dist = con_aml.createStatement();
			String user = "";
			while (true) {
				arrFiles = getFileNamesDir(srcFile);
				//System.out.println(arrFiles);
				for (String name : arrFiles) {
					rs_dist = pstmt_dist
							.executeQuery("select SWIFT_Distribution from dual");
					while (rs_dist.next()) {
						user = rs_dist.getString(1);
					}
					if (user == null) {
						rs_dist = pstmt_dist
								.executeQuery("select value from p_aml_general where name = 'DEFAULT_USER'");
						while (rs_dist.next()) {
							user = rs_dist.getString(1);
						}
					}
					insertMsg(srcFile, destFile, name, "F", user);
					deleteFile(srcFile, name);
					splitMessage();
					processMessageList("I");
					processMessageList("O");
				}
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt_dist != null) {
					pstmt_dist.close();
					pstmt_dist = null;
				}
				if (rs_dist != null) {
					rs_dist.close();
					rs_dist = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

}
