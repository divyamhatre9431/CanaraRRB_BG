package com.idbi.intech.iaml.swift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.idbi.intech.iaml.factory.AML_PasswordBaseEncryption;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class MessageScanOut implements Runnable {
	Statement stmt = null;
	Statement stmtUpdate = null;
	ResultSet rsInfo = null;
	ArrayList<String> arrMsg = null;
	private static Connection con_aml = null;
	static String url = "";
	static String user = "";
	static String password = "";
	protected static String connCred = "";
	private static Thread tMain = null;
	private static MessageScanOut process_run = new MessageScanOut();

	public static void LoadDatabaseAml() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
			con_aml.setAutoCommit(false);
			System.out.println("Connection started");
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			// System.exit(0);
		}
	}

	public ArrayList<String> getMessage() {
		try {
			arrMsg = new ArrayList<String>();
			stmt = con_aml.createStatement();
			stmtUpdate = con_aml.createStatement();

			String msgId = "";
			String msgType = "";

			rsInfo = stmt
					.executeQuery("select msg_id,msg_type from aml_swift_msg_info where process_status = '3' and process <> 'R' and swift_type = 'O' and rownum<31 order by generated_time asc");
			while (rsInfo.next()) {
				msgId = rsInfo.getString(1);
				msgType = rsInfo.getString(2);
				arrMsg.add(msgId + "~" + msgType);
				stmtUpdate
						.executeUpdate("update aml_swift_msg_info set process = 'R' where msg_id='"
								+ msgId + "'");
			}
			con_aml.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsInfo != null) {
					rsInfo.close();
					rsInfo = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return arrMsg;
	}

	public static void getDBCredentails() {
		try {
			AML_PasswordBaseEncryption obj = new AML_PasswordBaseEncryption();
			FileReader fr = new FileReader(new File(
					"D:\\AML\\AML_PROCESS\\AML_PASS.txt"));
			BufferedReader br = new BufferedReader(fr);
			while ((connCred = br.readLine()) != null) {
				String pass_info[] = obj.decrypt(connCred).split("~");
				url = pass_info[0] + pass_info[1] + pass_info[2] + pass_info[3];
				user = pass_info[4];
				password = pass_info[5];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String args[]) {
		//getDBCredentails();
		LoadDatabaseAml();
		MessageScanOut obj = new MessageScanOut();
		tMain = new Thread(obj);
		tMain.start();
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
		getDBCredentails();
		LoadDatabaseAml();
		tMain = new Thread(process_run);
		tMain.start();
	}

	public void stop() {

	}

	@Override
	public void run() {
		while (true) {
			for (String info : getMessage()) {
				MsgScan scanObj = new MsgScan(info,"jdbc:oracle:thin:@10.144.136.225:1522:amlobc", "amlidbi", "amlidbi");
				Thread tChild = new Thread(scanObj);
				tChild.start();
			}
			try {
				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}