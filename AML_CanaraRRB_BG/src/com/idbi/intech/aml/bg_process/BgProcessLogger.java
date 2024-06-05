package com.idbi.intech.aml.bg_process;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class BgProcessLogger {

	/**
	 * 
	 * @param fileName
	 * @param formattedText
	 * @param e
	 */
	public static void writeToFileException(String logFileName,String fileName , String methodName, Exception e) {

		
		FileOutputStream fo = null;
		//PrintWriter pw = null;
		OutputStreamWriter osw = null;
		
		String formattedText = formatText(fileName, methodName, "Exception ::"+e.toString());
		
		try {

			// fo = new FileOutputStream("user_logs/" + fileName, true);
//			fo = new FileOutputStream(fileName, true);
//			pw = new PrintWriter(fo);
//			pw.print(formattedText);
//			pw.print("Exception Thrown: ");
//			e.printStackTrace(pw);
//			pw.flush();
//			pw.close();
			
			ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
			String LOG_DIR = rb.getString("LOG_DIR");
			fo = new FileOutputStream(LOG_DIR + logFileName, true);
			osw = new OutputStreamWriter(fo);
			osw.write(formattedText);
			osw.flush();
			osw.close();
			fo.close();

		} catch (Exception z) {
			System.out.println("Failed to write to log file: " + fileName
					+ " - " + z.getMessage());
		} finally {
//			if (pw != null) {
//				pw.close();
//			}
		}
		
	}
	
	/**
	 * 
	 * @param fileName
	 * @param formattedText
	 */
	public static void writeToFile(String logFileName,String fileName ,String methodName,String textToWrite) {

		
		FileOutputStream fo = null;
		OutputStreamWriter osw = null;

		String formattedText = formatText(fileName, methodName, textToWrite);
		
		try {
			//Get log directory from AMLProp property file.
			ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
			String LOG_DIR = rb.getString("LOG_DIR");
			fo = new FileOutputStream(LOG_DIR + logFileName, true);
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
	public static String generateLogFileName(String userName) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.US);
		String dateString = formatter.format(new Date());

		Random random = new Random();
		random.setSeed(new Date().getTime());

		int sequence = random.nextInt();
		String fileName = "BGP" + dateString + "_" + userName + "_"
				+ new Integer(sequence) + ".log";

		return fileName;
	}
	
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

}
