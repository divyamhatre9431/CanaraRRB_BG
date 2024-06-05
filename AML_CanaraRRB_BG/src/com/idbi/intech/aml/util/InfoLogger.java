/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : InfoLogger.java
 * Created By : Jignesh Kansara
 * 
 * Modification History
 * 
 * 11-07-2011	Jignesh Kansara		Initial version
 */

package com.idbi.intech.aml.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public abstract class InfoLogger {

	String fileName = null;

	/**
	 * Constructor for InfoLogger.
	 */
	public InfoLogger() {
		super();
	}

	public String getLogFileName() {
		return this.fileName;
	}

	public void setLogFileName(String file) {
		this.fileName = file;
	}

	public abstract void logNormalText(String currentClass, String method,
			String aText);

	public abstract void logExceptionText(String currentClass, String method,
			String aText);
	
	public abstract void logExceptionText(String currentClass, String method,
			String aText, Exception e);

	/**
	 * 
	 * @param currentClass
	 * @param method
	 * @param aText
	 */
	public abstract void logVerboseText(String currentClass, String method,
			String aText);

	/**
	 * 
	 * @param currentClass
	 * @param method
	 * @param aText
	 */
	public abstract void logTraceText(String currentClass, String method,
			String aText);

	/**
	 * 
	 * @param currentClass
	 * @param method
	 * @param aText
	 */
	public abstract void logDebugText(String currentClass, String method,
			String aText);

	static String formatText(String currentClass, String method, String aText) {
		String result = new String();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS",
				Locale.US);
		String dateString = formatter.format(new Date());
		result = dateString + ":" + currentClass + "::" + method + "  ("
				+ aText + ")\n";
		return result;
	}
	static String formatText(String type, String currentClass, String method, String aText) {
		String result = new String();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS",
				Locale.US);
		String dateString = formatter.format(new Date());
		result = dateString + ":" + type + "::" + currentClass + "::" + method + "  ("
				+ aText + ")\n";
		return result;
	}

	static void writeToFile(String fileName, String formattedText, Exception e) {

		FileOutputStream fo = null;
		PrintWriter pw = null;

		try {

			// fo = new FileOutputStream("user_logs/" + fileName, true);
			fo = new FileOutputStream(fileName, true);
			pw = new PrintWriter(fo);
			pw.print(formattedText);
			pw.print("Exception Thrown: ");
			e.printStackTrace(pw);
			pw.flush();
			pw.close();

		} catch (Exception z) {
			System.out.println("Failed to write to log file: " + fileName
					+ " - " + z.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * 
	 * @param fileName
	 * @param formattedText
	 */
	static void writeToFile(String fileName, String formattedText) {

		FileOutputStream fo = null;
		OutputStreamWriter osw = null;

		try {
			//Get log directory from AMLProp property file.
			ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
			String LOG_DIR = rb.getString("LOG_DIR");
			fo = new FileOutputStream(LOG_DIR + fileName, true);
			osw = new OutputStreamWriter(fo);
			osw.write(formattedText);
			osw.flush();
			osw.close();
			fo.close();
		} catch (Exception e) {
			System.out.println("Failed to write to log file: " + fileName
					+ " - " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param userName
	 * @return
	 */
	public static String generateLogFileName(String userName,String projectName) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.US);
		String dateString = formatter.format(new Date());

		Random random = new Random();
		random.setSeed(new Date().getTime());

		int sequence = random.nextInt();
		String fileName = projectName + dateString + "_" + userName + "_"
				+ new Integer(sequence) + ".log";

		return fileName;
	}
}
