package com.idbi.intech.aml.bg_process;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.misc.EodBodReport;

public class AmlMis implements Runnable {

	private static Connection connection = null;
	Statement stmtCheck;
	ResultSet rs;
	static AmlMis process_run = new AmlMis();

	public String checkCreateThread() {

		String flg = "Y";
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
			stmtCheck = connection.createStatement();

			rs = stmtCheck
					.executeQuery("select to_char(sysdate,'hh24') from dual");
			while (rs.next()) {
				flg = rs.getString(1);
			}

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

	public static void stopService(String serviceName) throws IOException,
			InterruptedException {
		String executeCmd = "cmd /c net stop \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Stopped");
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
			Thread t = new Thread(new AmlMis());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	@Override
	public void run() {
		while (true) {
			if (checkCreateThread().equals("16")) {

				new EodBodReport().generateMisReport();
			}
			try {
				Thread.sleep(1000 * 60 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	


	public static void main(String[] args) {
		AmlMis aObj = new AmlMis();
		Thread thread = new Thread(aObj);
		thread.start();
	}
	
}
