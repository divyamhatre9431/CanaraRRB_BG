package com.idbi.intech.iaml.swift;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FileFTPGet implements Runnable {

	private String ip = "";
	private String userName = "";
	private String password = "";
	private String directory = "";
	private String backUpDirectory = "";
	private String backupServer = "";
	private FTPClient client = null;
	public static FileFTPGet process_run = new FileFTPGet();

	public FileFTPGet() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.iaml.swift.aml_ftp");
			ip = bundle.getString("SWF_SERVERIP");
			userName = bundle.getString("SWF_SERVERUSERID");
			password = bundle.getString("SWF_SERVERPWD");
			directory = bundle.getString("SWF_DESTINDIR");
			backUpDirectory = bundle.getString("SWF_PRESENTDIR");
			client = new FTPClient();
			client.connect(ip);
			if (client.isConnected()) {
				if (!client.login(userName, password)) {
					System.out.println("Error Invalid username or password");

					System.err.println("Exception ");
				}
				client.changeWorkingDirectory(directory);
			} else {
				System.out.println("Error in ftp connection");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		try {
			while (1 == 1) {
				FTPFile[] files = client.listFiles();

				System.out.println("---------------------------------------------");
				System.out.println("Total no. of file found :: " + files.length);
				System.out.println("---------------------------------------------");

				boolean flag = false;
				for (int i = files.length - 1; i >= 0; i--) {
					String fileId = "";

					System.out.println("Started processing of file :: " + files[i].getName());

					InputStream is = client.retrieveFileStream(files[i].getName());
					BufferedInputStream bis = new BufferedInputStream(is);
					byte[] bytes = new byte[(int) files[i].getSize()];
					bis.read(bytes);

					System.out.println("No of bytes :: " + bytes.length);
					String message = Arrays.toString(bytes);
					// Ensuring that size of the file is equal to no of bytes
					if (bytes.length == files[i].getSize()) {
						// System.out
						// .println("Y$$$$$$$$$$$$$$$$$$$$$$$in the if condition");
						File file = new File(backUpDirectory + files[i].getName());
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(bytes);
						fos.flush();
						fos.close();
						System.out.println("File writing complete");
						client.deleteFile(files[i].getName());
						System.out.println(files[i].getName() + " Processing over");
						System.out
								.println("---------------------------------------------------------");
						System.out.println("File" + files[i] + "deleted.");
					} else {
						System.out.println("run");
					}
					is.close();
					bis.close();
					if (!client.completePendingCommand()) {
						client.logout();
						client.disconnect();
						System.out.println("File transfer failed.");

						System.err.println("Exception ");
					}
				}
				Thread.sleep(1000*60);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		FileFTPGet ffg = new FileFTPGet();
		Thread t = new Thread(ffg);
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
			Thread t_upload = new Thread(process_run);
			t_upload.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}
	
	
	
}
