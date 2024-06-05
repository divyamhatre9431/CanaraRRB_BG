package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GT1DataExtractionService {

	private static Connection connection;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("AML connection established successfully...");
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	

	public static void main(String[] args) throws IOException, SQLException {
		
		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		//System.out.println("Current Directory::"+dir);
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		String procedure = amlProp.getProperty("GT1_PL");
		
		try
		{
			makeConnection();
			
			RequestDataBean reqObj = ConstantFunctions.getRequestData("GT1");
			String reportType = reqObj.getReportType();
			String fromDate = reqObj.getFromDate();
			String toDate = reqObj.getEndDate();
			//String strNo = reqObj.getRequestId();
			String requestId = reqObj.getRequestId();
			String batchNo = reqObj.getBatchNo();
			//String fileType = reqObj.getFileType();
			
			new Thread(new GT1Thread(requestId, batchNo, fromDate, toDate, reportType,procedure)).start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
