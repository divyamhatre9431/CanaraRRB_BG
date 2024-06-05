package com.idbi.intech.iaml.screening;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class FileBaseWatchListDataInsertion {
	static Connection connection = null;
	static PreparedStatement stmt = null;
	static ResultSet rs = null;
	
	//static String fileDir = "D:\\AML\\WATCHLIST_DATA";

	public static void processWatchListData() throws IOException {
		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		
		try {
		File folder = new File(amlProp.getProperty("fileBaseWatchList"));
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && FilenameUtils
					.getExtension(amlProp.getProperty("fileBaseWatchList") + listOfFiles[i].getName()).equalsIgnoreCase("log")) {

				Boolean fileIsPresent = checkedFileIsAlredyInserted(listOfFiles[i].getName());
				if (Boolean.TRUE.equals(fileIsPresent)) {
					String filePath = amlProp.getProperty("fileBaseWatchList") + listOfFiles[i].getName();
					boolean processFlg=insertWatchListData(filePath);
					
					if(processFlg)
					{   
						File srcFile = new File(filePath);
						File destDir = new File(amlProp.getProperty("fileBaseWatchListBKP") + listOfFiles[i].getName());
						Path src = srcFile.toPath();
						Files.move(src, destDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean insertWatchListData(String filePath) {
		String lineText = null;
		BufferedReader lineReader = null;
		File file = null;
        boolean processFlg=false;
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			file = new File(filePath);
			String[] data = null;
			lineReader = new BufferedReader(new FileReader(file));

			while ((lineText = lineReader.readLine()) != null) {
				try {
					String noSpaceLineText = lineText.trim();
					data = noSpaceLineText.split("-------");
					RequestJson requestJson = new RequestJson();
					requestJson.setReqString(lineText);
					System.out.println("File Name :"+data[0]);
					System.out.println("Request :"+data[1]);
					System.out.println("Response :"+data[2]);
					System.out.println("Response Time:"+data[3]);
					stmt = connection.prepareStatement("insert into FILE_SCREEN_ALERTS values (?,?,?,?,?,to_date(?,'dd-mm-yy hh24:mi:ss'))");
					stmt.setString(1, genAlertId());
					stmt.setString(2, data[0]);
					stmt.setString(3, "CC");
					stmt.setString(4, data[1]);
					stmt.setString(5, data[2]);
					stmt.setString(6, data[3]);
					stmt.executeUpdate();
					
				} catch (Exception e) {
					e.printStackTrace();

				}
				processFlg=true;
			}
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt = null;
			}
			if (lineReader != null) {
				try {
					lineReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return processFlg;
	}

	private static String genAlertId() {
		String dateAsString = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		long randNumFirst = generateRandomDigits(2);
		return "ALT" + "/" + dateAsString + "/" + randNumFirst;
	}
	
	static int generateRandomDigits(int n) {
		int m = (int) Math.pow(10, n - 1);
		return m + new Random().nextInt(9 * m);
	}

	private static Boolean checkedFileIsAlredyInserted(String fileName) {
		Boolean flg = true;
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			String checkSQL = "SELECT COUNT(1) FROM FILE_SCREEN_ALERTS WHERE FILE_NAME='" + fileName + "'";

			stmt = connection.prepareStatement(checkSQL);
			rs = stmt.executeQuery();

			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {

					flg = false;

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt = null;
			}
			if (rs != null) {
				rs = null;
			}
		}
		return flg;
	}

	public static void main(String[] args) throws IOException {
		processWatchListData();
	}
}
