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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;


public class ReadSWIFTFiles implements Runnable {

	private static Connection con_aml = null;
	private ArrayList<String> arrMsg = null;
	private String extension = "";
	static ReadSWIFTFiles readObj = null;
	final ResourceBundle bundle_path = ResourceBundle
			.getBundle("com.idbi.intech.iaml.swift.SWIFT_Path");
	final String srcFile = bundle_path.getString("FILE_SRC_IN");
	final String destFile = bundle_path.getString("FILE_DEST_IN");
	static ReadSWIFTFiles process_run = new ReadSWIFTFiles();
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

	public void insertMsg(String src, String dest, String fname, String in_out,
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
			strBuild.append("select distinct(msg_no) from aml_swift_flds where ");
			if (in_out.equals("I"))
				strBuild.append("in_out_flg = 'I'");
			else
				strBuild.append("in_out_flg = 'O'");

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
			//pstmt_fname = con_aml
			//		.prepareStatement("insert into aml_swift_msg_info_print(filename,status,LOC_FOR,upload_time) values(?,?,?,sysdate)");

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

				//pstmt_fname.setString(1, fname);
				//pstmt_fname.setString(2, "N");
				//pstmt_fname.setString(3, loc_for);
				//pstmt_fname.executeUpdate();

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
					|| extension.equalsIgnoreCase(".inn")|| extension.equalsIgnoreCase(".txt"))
				filname.add(element);
		}
		return filname;
	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		Thread t = new Thread(new ReadSWIFTFiles());
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
		ArrayList<String> arrFiles = null;
		System.out.println("iAML Reading SWIFT files......");
		ReadSWIFTFiles rObj = new ReadSWIFTFiles();
		try {
			pstmt_dist = con_aml.createStatement();
			String user = "";
			while (true) {
				arrFiles = rObj.getFileNamesDir(srcFile);
				System.out.println(arrFiles);
				for (String name : arrFiles) {
					rs_dist = pstmt_dist
							.executeQuery("select value from p_aml_general where name = 'DEFAULT_USER'");
					while (rs_dist.next()) {
						user = rs_dist.getString(1);
					}
					rObj.insertMsg(srcFile, destFile, name, "I", "F",
							user);
					rObj.deleteFile(srcFile, name);
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

