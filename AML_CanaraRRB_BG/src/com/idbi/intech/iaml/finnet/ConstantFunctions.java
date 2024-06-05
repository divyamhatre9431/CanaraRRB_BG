package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ConstantFunctions 
{
	
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
	
	
	public static RequestDataBean getRequestData(String fileType) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		RequestDataBean reqObj = new RequestDataBean();

		try {
			
			makeConnection();
			
			sql = "select a.REQUEST_ID,a.REG_REPORT_TYPE,a.REG_MONTH||a.REG_YEAR batch_no,a.FROM_DT,a.END_DT,b.FILE_TYPE from reg_request_dtls a, reg_request_process b "
					+ "where a.request_id = b.request_id "
					+ "and a.STEP_NUMBER in ('0','1') "
					+ "and b.status='N' "
					//+ "and REG_REPORT_TYPE= '"+reportType+"'"
					+ "and FILE_TYPE= '"+fileType+"'";
			
			System.out.println("sql : "+sql);

			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				reqObj.setBatchNo(rs.getString("batch_no"));
				reqObj.setEndDate(rs.getString("END_DT"));
				reqObj.setFileType(rs.getString("FILE_TYPE"));
				reqObj.setFromDate(rs.getString("FROM_DT"));
				reqObj.setRequestId(rs.getString("REQUEST_ID"));
				reqObj.setStrNo(rs.getString("REQUEST_ID"));
				reqObj.setReportType(rs.getString("REG_REPORT_TYPE"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return reqObj;
	}
	
	
	public static List<StrReqDataBean> getStrRequestData() {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		List<StrReqDataBean> strList = new ArrayList<StrReqDataBean>();

		try {
			makeConnection();
			
			sql = "select * from str_details_finnet where status='N' ";
			System.out.println("sql : "+sql);

			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				StrReqDataBean reqObj = new StrReqDataBean();
				//reqObj.setBatchNo(rs.getString("batch_no"));
				//reqObj.setEndDate(rs.getString("END_DT"));
				//reqObj.setFromDate(rs.getString("FROM_DT"));
				reqObj.setStrNo(rs.getString("STR_NO"));
				reqObj.setReportType("STR");
				reqObj.setTxnChannel(rs.getString("TRAN_CHANNEL"));
				strList.add(reqObj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return strList;
	}
	
	
	public static StrReqDataBean getStrTxnData() {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		StrReqDataBean reqObj = new StrReqDataBean();

		try {
			makeConnection();
			
			sql = "select * from aml_transaction_master_f where str_no='N' ";
			System.out.println("sql : "+sql);

			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				reqObj.setBatchNo(rs.getString("batch_no"));
				reqObj.setEndDate(rs.getString("END_DT"));
				reqObj.setFromDate(rs.getString("FROM_DT"));
				reqObj.setStrNo(rs.getString("STR_NO"));
				reqObj.setReportType("STR");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return reqObj;
	}
	
	
	public static void updateThreadStatus(String fileType,String status) throws SQLException
	{
		PreparedStatement pstmtUpdateIntoAml = null;
		
		try
		{
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update STR_REQUEST_THREADS set STATUS=? where FILE_TYPE=? ");
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.setString(2, fileType);
			
			pstmtUpdateIntoAml.execute();
			connection.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmtUpdateIntoAml != null) {
				pstmtUpdateIntoAml.close();
				pstmtUpdateIntoAml = null;
			}
		}
	}
	
	
	public static String getThreadStatus(String fileType) throws SQLException
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String status = "";
		
		try
		{
			pstmt = connection.prepareStatement(
					"select status from STR_REQUEST_THREADS where FILE_TYPE=? ");
			pstmt.setString(1, fileType);
			
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				status = rs.getString(1);
			}
			
			connection.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		}
		
		return status;
	}
	
	
	public static String checkStatus(String fileType) {
		
		Statement stmtCheck = null;
		ResultSet rs = null;
		int cnt = 0;
		String flg = "N";
		
		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck
					.executeQuery("select count(distinct(status)) from str_request_threads where active_flg='Y' ");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}
			rs.close();

			if (cnt == 1) {
				rs = stmtCheck
						.executeQuery("select distinct(status) from str_request_threads where active_flg='Y'  ");
				while (rs.next()) {
					flg = rs.getString(1);
				}
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
	
	
	public static void updateMainStatus(String strNo,String status) throws SQLException
	{
		PreparedStatement pstmtUpdateIntoAml = null;
		
		
		
		
		try
		{
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			// System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String procedure = amlProp.getProperty("FINNET_STR_CLEAN");
			
			
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update str_details_finnet set STATUS=? where str_no =? ");
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.setString(2, strNo);
			
			pstmtUpdateIntoAml.execute();
			connection.commit();
			
			//executeProc(procedure, strNo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmtUpdateIntoAml != null) {
				pstmtUpdateIntoAml.close();
				pstmtUpdateIntoAml = null;
			}
		}
	}
	
	
	public static void executeProc(String procedure,String requestId) {
		
		Statement stmt = null;
		
		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure+"('"+requestId+"')");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure
					+ " Total Time elapsed : " + (elapsedTime / 1000)
					+ " Seconds");
			System.out.flush();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	
	public static String checkStrStatus(String strNo) {
		
		Statement stmtCheck = null;
		ResultSet rs = null;
		String flg = "N";
		
		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck
					.executeQuery("select status from str_details_finnet where str_no='"+strNo+"' ");
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
	
	
	public static void updateStrReq(String strNo) throws SQLException
	{
		PreparedStatement pstmtUpdateIntoAml = null;
		
		PreparedStatement pstmtUpdateStrReq = null;
		
		
		try
		{
		
			
			
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update pnbfinnet.REG_REQUEST_DTLS set step_number = ? where request_id= ? ");
			pstmtUpdateIntoAml.setString(1, "2");
			pstmtUpdateIntoAml.setString(2, strNo);
			pstmtUpdateIntoAml.execute();
			connection.commit();
			pstmtUpdateStrReq = connection.prepareStatement(
					"update pnbfinnet.str_REQUEST_DTLS set REQ_STATUS = ? where req_id= ?");
			pstmtUpdateStrReq.setString(1, "2");
			pstmtUpdateStrReq.setString(2, strNo);
			pstmtUpdateStrReq.execute();
			connection.commit();
			
			//executeProc(procedure, strNo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmtUpdateIntoAml != null) {
				pstmtUpdateIntoAml.close();
				pstmtUpdateIntoAml = null;
			}
			if (pstmtUpdateStrReq != null) {
				pstmtUpdateStrReq.close();
				pstmtUpdateStrReq = null;
			}
		}
	}
	

}
