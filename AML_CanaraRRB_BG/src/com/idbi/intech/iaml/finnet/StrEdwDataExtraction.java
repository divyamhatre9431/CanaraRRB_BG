package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class StrEdwDataExtraction {
	private static Connection connectionDataWarehouse;
	
	private static void makeConnectionToDataWarehouse() {

		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connectionDataWarehouse = ConnectionFactory.makeConnectionEDWLive();
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("EDW connection established successfully...");
		} catch (SQLException | ClassNotFoundException e) {

			e.printStackTrace();
			System.err.println("Exception e:" + e);
		}
	}

	public  static String  genStrTransaction(String strNo) {

		ResultSet rsFetchCount = null;
		ResultSet rsFetchStrEdwData = null;
		int count = 1;
		boolean updateflg = false;
		Boolean flg = false;

		String vTranChannel = null;
		String vFromDate = null;
		String vToDate = null;
		String status = "N";
		String fetchStrCount = null;
		String fetchStrDataQuery = null;
		
		List<String> CustIdList = new ArrayList<>();

		String[] tranChannels = new String[7];

		fetchStrCount = "SELECT count(1)  FROM AML_TRANSACTION_MASTER_F WHERE STR_NO=?";
		fetchStrDataQuery = "select TRAN_CHANNEL,to_char(to_date(from_date,'DD-MON-RR'),'YYYYMMDD') from_date,to_char(to_date(to_date,'DD-MON-RR'),'YYYYMMDD') to_date from str_details_finnet where STR_NO=?";

		try (Connection connFetch = ConnectionFactory.makeConnectionAMLLive();
				PreparedStatement stmtFetchStr = connFetch.prepareStatement(fetchStrCount);
				PreparedStatement stmtFetchStrData = connFetch.prepareStatement(fetchStrDataQuery);) {
			stmtFetchStr.setString(1, strNo);
			rsFetchCount = stmtFetchStr.executeQuery();
			while (rsFetchCount.next()) {
				count = rsFetchCount.getInt(1);

			}

			if (count == 0) {
				Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String procedure = amlProp.getProperty("SYNC_FINNET_CUST");
				
				updateflg = updateStrDetails("N", strNo);
				if (updateflg) {

					stmtFetchStrData.setString(1, strNo);
					rsFetchStrEdwData = stmtFetchStrData.executeQuery();

					while (rsFetchStrEdwData.next()) {
						vTranChannel = rsFetchStrEdwData.getString(1);
						vFromDate = rsFetchStrEdwData.getString(2);
						vToDate = rsFetchStrEdwData.getString(3);
					}

					if (vTranChannel.contains(",")) {
						tranChannels = vTranChannel.split(",");
					} else {
						tranChannels[0] = vTranChannel;
					}

					if (tranChannels != null) {
						for (String Channels : tranChannels) {
                    System.out.println("Transaction Channels"+Channels);
							List<String> acctList = getStrAcctDtls(strNo);
							if (Channels.equalsIgnoreCase("TC1")) {
								flg = insertStrTC1DtlsTemp(acctList, strNo,vFromDate,vToDate);

							}  else if (Channels.equalsIgnoreCase("TC2")) {
								flg = insertStrTC2DtlsTemp(acctList, strNo,vFromDate,vToDate);

							} else if (Channels.equalsIgnoreCase("TS1")) {
								flg = insertStrTS1DtlsTemp(acctList, strNo,vFromDate,vToDate);

							} else if (Channels.equalsIgnoreCase("TS2")) {
								flg = insertStrTS2DtlsTemp(acctList, strNo,vFromDate,vToDate);
								
							} else if (Channels.equalsIgnoreCase("TS3")) {
								flg = insertStrTS3DtlsTemp(acctList, strNo,vFromDate,vToDate);

							} else if (Channels.equalsIgnoreCase("GT1")) {
								flg = insertStrGT1DtlsTemp(acctList, strNo,vFromDate,vToDate);

							}
							if (Boolean.TRUE.equals(flg)) {
								status="Y";
							} else {
								status="N";
							}

						}

					}
					if (status.equals("Y")) {
						CustIdList =getStrCustDtls(strNo);
						if(CustIdList.size()>0) {
							executeProc(procedure,CustIdList);
							
						}
					}

				}
			}

		}

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				if (rsFetchCount != null) {
					rsFetchCount.close();
					rsFetchCount = null;
				}
				if (rsFetchStrEdwData != null) {
					rsFetchStrEdwData.close();
					rsFetchStrEdwData = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return status;

	}
	
	public static void executeProc(String procedure, List<String> custBeanList) {

		Statement stmt = null;
		String flg = "N";
		Connection connection = null;

		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			for (String custId : custBeanList) {

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure + "('" + custId + "')");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();
			flg = "Y";
			}
		} catch (SQLException ex) {
			flg = "N";
			ex.printStackTrace();
		} catch (Exception ex) {
			flg = "N";
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
	
	public static List<String> getStrCustDtls(String strNo) {
		ResultSet rsFetchAcct = null;
		String custId = null;
		String query = "SELECT distinct tran_cust_id FROM AML_TRANSACTION_MASTER_F where STR_NO=?";
		List<String> strRequestList = new ArrayList<>();
		try (Connection connection = ConnectionFactory.makeConnectionAMLLive();
				PreparedStatement pstmtFetchAcct = connection.prepareStatement(query);) {
			pstmtFetchAcct.setString(1, strNo);
			rsFetchAcct = pstmtFetchAcct.executeQuery();
			while (rsFetchAcct.next()) {
				custId = rsFetchAcct.getString(1) == null ? "NA" : rsFetchAcct.getString(1);
				strRequestList.add(custId);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rsFetchAcct != null) {
					rsFetchAcct.close();
					rsFetchAcct = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		}
		return strRequestList;
	}

	

	public static boolean updateStrDetails(String status, String strNo) {
		boolean flg = true;
		String threadUpdate = "update STR_REQUEST_THREADS set STATUS=?";
		String strUpdate = "update str_details_finnet set status=? where str_no=?";
		try (Connection connection = ConnectionFactory.makeConnectionAMLLive();
				PreparedStatement pstmtUpdateIntoAml = connection.prepareStatement(threadUpdate);
				PreparedStatement pstmtUpdateStr = connection.prepareStatement(strUpdate);) {
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.execute();
			connection.commit();
			pstmtUpdateStr.setString(1, "P");
			pstmtUpdateStr.setString(2, strNo);
			pstmtUpdateStr.executeUpdate();
			connection.commit();

		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		return flg;
	}

	public static List<String> getStrAcctDtls(String strNo) {
		ResultSet rsFetchAcct = null;
		String custAcid = null;
		String query = "select get_acid(ACCT_NO) acid from aml_str_req_acct where req_no=?";
		List<String> strRequestList = new ArrayList<>();
		try (Connection connection = ConnectionFactory.makeConnectionAMLLive();
				PreparedStatement pstmtFetchAcct = connection.prepareStatement(query);) {
			pstmtFetchAcct.setString(1, strNo);
			rsFetchAcct = pstmtFetchAcct.executeQuery();
			while (rsFetchAcct.next()) {
				custAcid = rsFetchAcct.getString(1) == null ? "NA" : rsFetchAcct.getString(1);
				strRequestList.add(custAcid);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rsFetchAcct != null) {
					rsFetchAcct.close();
					rsFetchAcct = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		}
		return strRequestList;
	}
	
	/* Get Customer Risk Data */
	public static String getBatchNum(String strNo) {
		PreparedStatement pstmt3 = null;
		String vBatchNum = null;
		ResultSet rs3 = null;
		try (Connection conn = ConnectionFactory.makeConnectionAMLLive();) {

			String custBatchQuery = "select substr(substr(a.str_no,4,4),3,2)||substr(substr(a.str_no,4,4),1,2) batch_num from str_details_finnet a where str_no=?";
			pstmt3 = conn.prepareStatement(custBatchQuery);
			pstmt3.setString(1, strNo);
			rs3 = pstmt3.executeQuery();
			while (rs3.next()) {
				vBatchNum = rs3.getString(1);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt3 != null) {
					pstmt3.close();
					pstmt3 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return vBatchNum;

	}
	
	public static String getCustType(String custId) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String custType = "";

		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(
					"select get_cust_type('" + custId + "') from dual ");

			while (rs.next()) {
				custType = rs.getString(1);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return custType;
	}
	
	public static String getTs3Date(String Date) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String vToDate = "";

		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(
					"select To_char(to_date('"+Date+"','YYYYMMDD')-30,'YYYYMMDD')  from dual ");

			while (rs.next()) {
				vToDate = rs.getString(1);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return vToDate;
	}
	
	
	public static String checkTs3Date(String Date, String ts3ToDate) {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String vToDate = "";
		int result = 0;
		int result1= 0;

		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(
					" select  case when "
					+ " to_date('"+Date+"' ,'YYYYMMDD') >= "
					+ " trunc(To_date('"+ts3ToDate+"','YYYYMMDD')) "
					+ " then 1 else 0 end as result from dual");

			while (rs.next()) {
				result = rs.getInt("result");;
			}
			
			if (result == 1) {
				vToDate=Date;
			} else {
			
				vToDate=ts3ToDate;

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return vToDate;
	}
	public static boolean insertStrGT1DtlsTemp(List<String> acctList, String strNo, String vFromDate, String vToDate) {
		boolean flg = true;
		Statement stFetchTxnWarehouse = null;
		Connection connection = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml=null;
		int commitCounter = 0;
		makeConnectionToDataWarehouse();
		String insertTransaction ="insert into AML_TRANSACTION_MASTER_F(TRAN_ID,TRAN_DATE,PART_TRAN_SRL_NUM,DEL_FLG,CRE_DEB_FLG,"
				+ "GL_SUB_HEAD_CODE,TRAN_ACID,TRAN_AMT,TRAN_PARTICULAR,TRAN_PARTICULAR_2,TRAN_TYPE,TRAN_SUB_TYPE,"
				+ "VALUE_DATE,PSTD_DATE,VFD_DATE,RPT_CODE,REF_NUM,INSTRUMENT_TYPE,INSTRUMENT_DATE,INSTRUMENT_NUM,INSTRUMENT_ALPHA,"
				+ "TRAN_RMKS,PSTD_FLG,TRAN_CUST_ID,RATE_CODE,RATE,TRAN_CRNCY_CODE,REF_CRNCY_CODE,REF_AMT , "
				+ "BRANCH_ID,INIT_BRANCH_ID,ACCOUNT_NO,TRAN_CHANNEL,STR_NO,BATCH_NUM,REPORT_TYPE,REPORT_REF_NUM ,"
				+ "CUST_TYPE)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try  {
			 connection = ConnectionFactory.makeConnectionAMLLive();
			 pstmtIsertIntoAml = connection
					.prepareStatement(insertTransaction);
			 
			 Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String mistableName = amlProp.getProperty("FETCH_MIS_TABLE");
			
			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
		
				
				

				for (String custAcid : acctList) {
					String acctDtlsQuery = "select ft.SRC_TRAN_ID,to_char(ft.SRC_TRAN_DT,'DD-MON-RR') SRC_TRAN_DT,ft.SRC_PART_TRAN_SRL_NUM,ft.PART_TRAN_TYPE,DA.GL_SUB_HEAD_CODE, "
							+ "DA.ACID,ft.TRAN_AMT,ft.TRAN_PARTICULAR,'NA' TRAN_PARTICULAR_2,ft.TRAN_TYPE,ft.TRAN_SUB_TYPE,to_char(ft.VALUE_DATE ,'DD-MON-RR') "
							+ ",to_char(ft.PSTD_DATE ,'DD-MON-RR'),to_char(ft.VFD_DATE,'DD-MON-RR'),ft.RPT_CODE,ft.REF_NUM,ft.INSTRUMENT_TYPE,nvl(to_char(ft.INSTRUMENT_DT,'DD-MON-RR'),null),ft.INSTRUMENT_NUM "
							+ ",ft.INSTRUMENT_ALPHA,ft.TRAN_REMARKS,DA.IP_CODE,nvl(ft.REF_CRNCY_CODE,'INR') REF_CRNCY_CODE,ft.TRAN_AMT REF_AMT,DA.BRANCH_CODE,DA.ACCOUNT_NBR "
							+ ",DECODE(SUBSTR(tran_particular, 1, 4),'CDS/','ATM','ATM ','ATM','NRTG','RTGS','UPI/','UPI','NACH','NACH','IMPS','IMPS','NEFT','NEFT','GTXN') TRAN_CHANNEL,"
							+ "(dense_rank() over (order by DA.IP_CODE)) REPORT_REF_NUM "
							+ "from  "+mistableName+"  ft "
							+ "inner join MIS_TMP.DIM_ARNGMNT_CRN_ROW da on  DA.ARNGMNT_ID=FT.ARNGMNT_ID "
							+ "where TXN_CAL_ID  BETWEEN '"+vFromDate+"' AND '"+vToDate+"' "
							+ "and DA.ACID='"+custAcid+"'"
							+ " and SUBSTR(tran_particular, 1, 4) not in ('CDS/','ATM ','NRTG','UPI/','IMPS','NEFT') "
							+ " and tran_amt > 100 "
							+ " and tran_type != 'C' "
							+ " and tran_sub_type in('CI','NR','NP','CP','CR','BI','I','O','PI','MU')";
					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString(1));
						pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString(2));
						pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString(3));
						pstmtIsertIntoAml.setString(4, "N");
						pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString(4));
						pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString(5));
						pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString(6));
						pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString(7));
						pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString(8));
						pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString(9));
						pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString(10));
						pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString(11));
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString(12));
						pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString(13));
						pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString(14));
						pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString(15));
						pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString(16));
						pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString(17));
						pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString(18));
						pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString(19));
						pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString(20));
						pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString(21));
						pstmtIsertIntoAml.setString(23, "Y");
						pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString(22));
						pstmtIsertIntoAml.setString(25, "1");
						pstmtIsertIntoAml.setString(26, null);
						pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString(24));
						pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString(26));
						pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString(27));
						pstmtIsertIntoAml.setString(34, strNo);
						pstmtIsertIntoAml.setString(35, getBatchNum(strNo));
						pstmtIsertIntoAml.setString(36, "STR");
						pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString(28));
						pstmtIsertIntoAml.setString(38, getCustType(rsTxnDataWarehouse.getString(22)));
					

						pstmtIsertIntoAml.execute();

						System.out.println(commitCounter);
						if (commitCounter == 500) {
							connection.commit();
							commitCounter = 0;
						}
					}
				}

				connection.commit();
				System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
				
			
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	
	}

	public static boolean insertStrTS3DtlsTemp(List<String> acctList, String strNo, String vFromDate, String vToDate) {
		boolean flg = true;
		Statement stFetchTxnWarehouse = null;
		Connection connection = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml=null;
		int commitCounter = 0;
		String ts3ToDate = null;
		String chkTs3Date = null;
		makeConnectionToDataWarehouse();
		
		
		String insertTransaction ="insert into AML_TRANSACTION_MASTER_F(TRAN_ID,TRAN_DATE,PART_TRAN_SRL_NUM,DEL_FLG,CRE_DEB_FLG,"
				+ "GL_SUB_HEAD_CODE,TRAN_ACID,TRAN_AMT,TRAN_PARTICULAR,TRAN_PARTICULAR_2,TRAN_TYPE,TRAN_SUB_TYPE,"
				+ "VALUE_DATE,PSTD_DATE,VFD_DATE,RPT_CODE,REF_NUM,INSTRUMENT_TYPE,INSTRUMENT_DATE,INSTRUMENT_NUM,INSTRUMENT_ALPHA,"
				+ "TRAN_RMKS,PSTD_FLG,TRAN_CUST_ID,RATE_CODE,RATE,TRAN_CRNCY_CODE,REF_CRNCY_CODE,REF_AMT , "
				+ "BRANCH_ID,INIT_BRANCH_ID,ACCOUNT_NO,TRAN_CHANNEL,STR_NO,BATCH_NUM,REPORT_TYPE,REPORT_REF_NUM ,"
				+ "CUST_TYPE)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try  {
			 connection = ConnectionFactory.makeConnectionAMLLive();
			 pstmtIsertIntoAml = connection
					.prepareStatement(insertTransaction);
			 
			 Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String mistableName = amlProp.getProperty("FETCH_MIS_TABLE");
			
			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
			ts3ToDate=getTs3Date(vToDate);
			chkTs3Date=checkTs3Date(vFromDate,ts3ToDate);
				for (String custAcid : acctList) {
					String acctDtlsQuery = "select ft.SRC_TRAN_ID,to_char(ft.SRC_TRAN_DT,'DD-MON-RR') SRC_TRAN_DT,ft.SRC_PART_TRAN_SRL_NUM,ft.PART_TRAN_TYPE,DA.GL_SUB_HEAD_CODE, "
							+ "DA.ACID,ft.TRAN_AMT,ft.TRAN_PARTICULAR,'NA' TRAN_PARTICULAR_2,ft.TRAN_TYPE,ft.TRAN_SUB_TYPE,to_char(ft.VALUE_DATE ,'DD-MON-RR') "
							+ ",to_char(ft.PSTD_DATE ,'DD-MON-RR'),to_char(ft.VFD_DATE,'DD-MON-RR'),ft.RPT_CODE,ft.REF_NUM,ft.INSTRUMENT_TYPE,nvl(to_char(ft.INSTRUMENT_DT,'DD-MON-RR'),null),ft.INSTRUMENT_NUM "
							+ ",ft.INSTRUMENT_ALPHA,ft.TRAN_REMARKS,DA.IP_CODE,nvl(ft.REF_CRNCY_CODE,'INR') REF_CRNCY_CODE,ft.TRAN_AMT REF_AMT,DA.BRANCH_CODE,DA.ACCOUNT_NBR "
							+ ",DECODE(SUBSTR(tran_particular, 1, 4),'CDS/','ATM','ATM ','ATM','NRTG','RTGS','UPI/','UPI','NACH','NACH','IMPS','IMPS','NEFT','NEFT','GTXN') TRAN_CHANNEL,"
							+ "(dense_rank() over (order by DA.IP_CODE)) REPORT_REF_NUM "
							+ "from  "+mistableName+"  ft "
							+ "inner join MIS_TMP.DIM_ARNGMNT_CRN_ROW da on  DA.ARNGMNT_ID=FT.ARNGMNT_ID "
							+ "where TXN_CAL_ID  BETWEEN '"+chkTs3Date+"' AND '"+vToDate+"' "
							+ "and DA.ACID='"+custAcid+"'"
							+ " and SUBSTR(tran_particular, 1, 4) = 'UPI/'"
							+ " and tran_amt > 100 "
							+ " and tran_type != 'C' "
							+ " and tran_sub_type in('CI','NR','NP','CP','CR','BI','I','O','PI','MU')";
					System.out.println("TS3 Query"+acctDtlsQuery);
					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString(1));
						pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString(2));
						pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString(3));
						pstmtIsertIntoAml.setString(4, "N");
						pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString(4));
						pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString(5));
						pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString(6));
						pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString(7));
						pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString(8));
						pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString(9));
						pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString(10));
						pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString(11));
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString(12));
						pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString(13));
						pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString(14));
						pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString(15));
						pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString(16));
						pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString(17));
						pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString(18));
						pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString(19));
						pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString(20));
						pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString(21));
						pstmtIsertIntoAml.setString(23, "Y");
						pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString(22));
						pstmtIsertIntoAml.setString(25, "1");
						pstmtIsertIntoAml.setString(26, null);
						pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString(24));
						pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString(26));
						pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString(27));
						pstmtIsertIntoAml.setString(34, strNo);
						pstmtIsertIntoAml.setString(35, getBatchNum(strNo));
						pstmtIsertIntoAml.setString(36, "STR");
						pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString(28));
						pstmtIsertIntoAml.setString(38, getCustType(rsTxnDataWarehouse.getString(22)));
					

						pstmtIsertIntoAml.execute();

						System.out.println(commitCounter);
						if (commitCounter == 500) {
							connection.commit();
							commitCounter = 0;
						}
					}
				}

				connection.commit();
				System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
				
			
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	
	}

	public static boolean insertStrTS2DtlsTemp(List<String> acctList, String strNo, String vFromDate, String vToDate) {
		boolean flg = true;
		Statement stFetchTxnWarehouse = null;
		Connection connection = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml=null;
		int commitCounter = 0;
		makeConnectionToDataWarehouse();
		String insertTransaction ="insert into AML_TRANSACTION_MASTER_F(TRAN_ID,TRAN_DATE,PART_TRAN_SRL_NUM,DEL_FLG,CRE_DEB_FLG,"
				+ "GL_SUB_HEAD_CODE,TRAN_ACID,TRAN_AMT,TRAN_PARTICULAR,TRAN_PARTICULAR_2,TRAN_TYPE,TRAN_SUB_TYPE,"
				+ "VALUE_DATE,PSTD_DATE,VFD_DATE,RPT_CODE,REF_NUM,INSTRUMENT_TYPE,INSTRUMENT_DATE,INSTRUMENT_NUM,INSTRUMENT_ALPHA,"
				+ "TRAN_RMKS,PSTD_FLG,TRAN_CUST_ID,RATE_CODE,RATE,TRAN_CRNCY_CODE,REF_CRNCY_CODE,REF_AMT , "
				+ "BRANCH_ID,INIT_BRANCH_ID,ACCOUNT_NO,TRAN_CHANNEL,STR_NO,BATCH_NUM,REPORT_TYPE,REPORT_REF_NUM ,"
				+ "CUST_TYPE)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try  {
			 connection = ConnectionFactory.makeConnectionAMLLive();
			 pstmtIsertIntoAml = connection
					.prepareStatement(insertTransaction);
			 
			 Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String mistableName = amlProp.getProperty("FETCH_MIS_TABLE");
			
			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
		
				for (String custAcid : acctList) {
					String acctDtlsQuery = "select ft.SRC_TRAN_ID,to_char(ft.SRC_TRAN_DT,'DD-MON-RR') SRC_TRAN_DT,ft.SRC_PART_TRAN_SRL_NUM,ft.PART_TRAN_TYPE,DA.GL_SUB_HEAD_CODE, "
							+ "DA.ACID,ft.TRAN_AMT,ft.TRAN_PARTICULAR,'NA' TRAN_PARTICULAR_2,ft.TRAN_TYPE,ft.TRAN_SUB_TYPE,to_char(ft.VALUE_DATE ,'DD-MON-RR') "
							+ ",to_char(ft.PSTD_DATE ,'DD-MON-RR'),to_char(ft.VFD_DATE,'DD-MON-RR'),ft.RPT_CODE,ft.REF_NUM,ft.INSTRUMENT_TYPE,nvl(to_char(ft.INSTRUMENT_DT,'DD-MON-RR'),null),ft.INSTRUMENT_NUM "
							+ ",ft.INSTRUMENT_ALPHA,ft.TRAN_REMARKS,DA.IP_CODE,nvl(ft.REF_CRNCY_CODE,'INR') REF_CRNCY_CODE,ft.TRAN_AMT REF_AMT,DA.BRANCH_CODE,DA.ACCOUNT_NBR "
							+ ",DECODE(SUBSTR(tran_particular, 1, 4),'CDS/','ATM','ATM ','ATM','NRTG','RTGS','UPI/','UPI','NACH','NACH','IMPS','IMPS','NEFT','NEFT','GTXN') TRAN_CHANNEL,"
							+ "(dense_rank() over (order by DA.IP_CODE)) REPORT_REF_NUM "
							+ "from  "+mistableName+"  ft "
							+ "inner join MIS_TMP.DIM_ARNGMNT_CRN_ROW da on  DA.ARNGMNT_ID=FT.ARNGMNT_ID "
							+ "where TXN_CAL_ID  BETWEEN '"+vFromDate+"' AND '"+vToDate+"' "
							+ "and DA.ACID='"+custAcid+"'"
							+ " and SUBSTR(tran_particular, 1, 4) = 'IMPS' "
							+ " and tran_amt > 100 "
							+ " and tran_type != 'C' "
							+ " and tran_sub_type in('CI','NR','NP','CP','CR','BI','I','O','PI','MU')";
					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString(1));
						pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString(2));
						pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString(3));
						pstmtIsertIntoAml.setString(4, "N");
						pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString(4));
						pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString(5));
						pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString(6));
						pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString(7));
						pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString(8));
						pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString(9));
						pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString(10));
						pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString(11));
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString(12));
						pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString(13));
						pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString(14));
						pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString(15));
						pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString(16));
						pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString(17));
						pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString(18));
						pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString(19));
						pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString(20));
						pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString(21));
						pstmtIsertIntoAml.setString(23, "Y");
						pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString(22));
						pstmtIsertIntoAml.setString(25, "1");
						pstmtIsertIntoAml.setString(26, null);
						pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString(24));
						pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString(26));
						pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString(27));
						pstmtIsertIntoAml.setString(34, strNo);
						pstmtIsertIntoAml.setString(35, getBatchNum(strNo));
						pstmtIsertIntoAml.setString(36, "STR");
						pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString(28));
						pstmtIsertIntoAml.setString(38, getCustType(rsTxnDataWarehouse.getString(22)));
					

						pstmtIsertIntoAml.execute();

						System.out.println(commitCounter);
						if (commitCounter == 500) {
							connection.commit();
							commitCounter = 0;
						}
					}
				}

				connection.commit();
				System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
				
			
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	
	}

	public static boolean insertStrTS1DtlsTemp(List<String> acctList, String strNo, String vFromDate, String vToDate) {
		boolean flg = true;
		Statement stFetchTxnWarehouse = null;
		Connection connection = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml=null;
		int commitCounter = 0;
		makeConnectionToDataWarehouse();
		String insertTransaction ="insert into AML_TRANSACTION_MASTER_F(TRAN_ID,TRAN_DATE,PART_TRAN_SRL_NUM,DEL_FLG,CRE_DEB_FLG,"
				+ "GL_SUB_HEAD_CODE,TRAN_ACID,TRAN_AMT,TRAN_PARTICULAR,TRAN_PARTICULAR_2,TRAN_TYPE,TRAN_SUB_TYPE,"
				+ "VALUE_DATE,PSTD_DATE,VFD_DATE,RPT_CODE,REF_NUM,INSTRUMENT_TYPE,INSTRUMENT_DATE,INSTRUMENT_NUM,INSTRUMENT_ALPHA,"
				+ "TRAN_RMKS,PSTD_FLG,TRAN_CUST_ID,RATE_CODE,RATE,TRAN_CRNCY_CODE,REF_CRNCY_CODE,REF_AMT , "
				+ "BRANCH_ID,INIT_BRANCH_ID,ACCOUNT_NO,TRAN_CHANNEL,STR_NO,BATCH_NUM,REPORT_TYPE,REPORT_REF_NUM ,"
				+ "CUST_TYPE)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try  {
			 connection = ConnectionFactory.makeConnectionAMLLive();
			 pstmtIsertIntoAml = connection
					.prepareStatement(insertTransaction);
			 
			 Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String mistableName = amlProp.getProperty("FETCH_MIS_TABLE");
			
			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
		
				
				

				for (String custAcid : acctList) {
					String acctDtlsQuery = "select ft.SRC_TRAN_ID,to_char(ft.SRC_TRAN_DT,'DD-MON-RR') SRC_TRAN_DT,ft.SRC_PART_TRAN_SRL_NUM,ft.PART_TRAN_TYPE,DA.GL_SUB_HEAD_CODE, "
							+ "DA.ACID,ft.TRAN_AMT,ft.TRAN_PARTICULAR,'NA' TRAN_PARTICULAR_2,ft.TRAN_TYPE,ft.TRAN_SUB_TYPE,to_char(ft.VALUE_DATE ,'DD-MON-RR') "
							+ ",to_char(ft.PSTD_DATE ,'DD-MON-RR'),to_char(ft.VFD_DATE,'DD-MON-RR'),ft.RPT_CODE,ft.REF_NUM,ft.INSTRUMENT_TYPE,nvl(to_char(ft.INSTRUMENT_DT,'DD-MON-RR'),null),ft.INSTRUMENT_NUM "
							+ ",ft.INSTRUMENT_ALPHA,ft.TRAN_REMARKS,DA.IP_CODE,nvl(ft.REF_CRNCY_CODE,'INR') REF_CRNCY_CODE,ft.TRAN_AMT REF_AMT,DA.BRANCH_CODE,DA.ACCOUNT_NBR "
							+ ",DECODE(SUBSTR(tran_particular, 1, 4),'CDS/','ATM','ATM ','ATM','NRTG','RTGS','UPI/','UPI','NACH','NACH','IMPS','IMPS','NEFT','NEFT','GTXN') TRAN_CHANNEL,"
							+ "(dense_rank() over (order by DA.IP_CODE)) REPORT_REF_NUM "
							+ "from  "+mistableName+"  ft "
							+ "inner join MIS_TMP.DIM_ARNGMNT_CRN_ROW da on  DA.ARNGMNT_ID=FT.ARNGMNT_ID "
							+ "where TXN_CAL_ID  BETWEEN '"+vFromDate+"' AND '"+vToDate+"' "
							+ "and DA.ACID='"+custAcid+"'"
							+ " and SUBSTR(tran_particular, 1, 4) in ('NRTG','NEFT') "
							+ " and tran_amt > 100 "
							+ " and tran_type != 'C' "
							+ " and tran_sub_type in('CI','NR','NP','CP','CR','BI','I','O','PI','MU')";
					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString(1));
						pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString(2));
						pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString(3));
						pstmtIsertIntoAml.setString(4, "N");
						pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString(4));
						pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString(5));
						pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString(6));
						pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString(7));
						pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString(8));
						pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString(9));
						pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString(10));
						pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString(11));
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString(12));
						pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString(13));
						pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString(14));
						pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString(15));
						pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString(16));
						pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString(17));
						pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString(18));
						pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString(19));
						pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString(20));
						pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString(21));
						pstmtIsertIntoAml.setString(23, "Y");
						pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString(22));
						pstmtIsertIntoAml.setString(25, "1");
						pstmtIsertIntoAml.setString(26, null);
						pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString(24));
						pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString(26));
						pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString(27));
						pstmtIsertIntoAml.setString(34, strNo);
						pstmtIsertIntoAml.setString(35, getBatchNum(strNo));
						pstmtIsertIntoAml.setString(36, "STR");
						pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString(28));
						pstmtIsertIntoAml.setString(38, getCustType(rsTxnDataWarehouse.getString(22)));
					

						pstmtIsertIntoAml.execute();

						System.out.println(commitCounter);
						if (commitCounter == 500) {
							connection.commit();
							commitCounter = 0;
						}
					}
				}

				connection.commit();
				System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
				
			
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	
	}

	public static boolean insertStrTC2DtlsTemp(List<String> acctList, String strNo, String vFromDate, String vToDate) {
		boolean flg = true;
		Statement stFetchTxnWarehouse = null;
		Connection connection = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml=null;
		int commitCounter = 0;
		makeConnectionToDataWarehouse();
		String insertTransaction ="insert into AML_TRANSACTION_MASTER_F(TRAN_ID,TRAN_DATE,PART_TRAN_SRL_NUM,DEL_FLG,CRE_DEB_FLG,"
				+ "GL_SUB_HEAD_CODE,TRAN_ACID,TRAN_AMT,TRAN_PARTICULAR,TRAN_PARTICULAR_2,TRAN_TYPE,TRAN_SUB_TYPE,"
				+ "VALUE_DATE,PSTD_DATE,VFD_DATE,RPT_CODE,REF_NUM,INSTRUMENT_TYPE,INSTRUMENT_DATE,INSTRUMENT_NUM,INSTRUMENT_ALPHA,"
				+ "TRAN_RMKS,PSTD_FLG,TRAN_CUST_ID,RATE_CODE,RATE,TRAN_CRNCY_CODE,REF_CRNCY_CODE,REF_AMT , "
				+ "BRANCH_ID,INIT_BRANCH_ID,ACCOUNT_NO,TRAN_CHANNEL,STR_NO,BATCH_NUM,REPORT_TYPE,REPORT_REF_NUM ,"
				+ "CUST_TYPE)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try  {
			 connection = ConnectionFactory.makeConnectionAMLLive();
			 pstmtIsertIntoAml = connection
					.prepareStatement(insertTransaction);
			 
			 Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String mistableName = amlProp.getProperty("FETCH_MIS_TABLE");
			
			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
		
				
				

				for (String custAcid : acctList) {
					String acctDtlsQuery = "select ft.SRC_TRAN_ID,to_char(ft.SRC_TRAN_DT,'DD-MON-RR') SRC_TRAN_DT,ft.SRC_PART_TRAN_SRL_NUM,ft.PART_TRAN_TYPE,DA.GL_SUB_HEAD_CODE, "
							+ "DA.ACID,ft.TRAN_AMT,ft.TRAN_PARTICULAR,'NA' TRAN_PARTICULAR_2,ft.TRAN_TYPE,ft.TRAN_SUB_TYPE,to_char(ft.VALUE_DATE ,'DD-MON-RR') "
							+ ",to_char(ft.PSTD_DATE ,'DD-MON-RR'),to_char(ft.VFD_DATE,'DD-MON-RR'),ft.RPT_CODE,ft.REF_NUM,ft.INSTRUMENT_TYPE,nvl(to_char(ft.INSTRUMENT_DT,'DD-MON-RR'),null),ft.INSTRUMENT_NUM "
							+ ",ft.INSTRUMENT_ALPHA,ft.TRAN_REMARKS,DA.IP_CODE,nvl(ft.REF_CRNCY_CODE,'INR') REF_CRNCY_CODE,ft.TRAN_AMT REF_AMT,DA.BRANCH_CODE,DA.ACCOUNT_NBR "
							+ ",DECODE(SUBSTR(tran_particular, 1, 4),'CDS/','ATM','ATM ','ATM','NRTG','RTGS','UPI/','UPI','NACH','NACH','IMPS','IMPS','NEFT','NEFT','GTXN') TRAN_CHANNEL,"
							+ "(dense_rank() over (order by DA.IP_CODE)) REPORT_REF_NUM "
							+ "from  "+mistableName+"  ft "
							+ "inner join MIS_TMP.DIM_ARNGMNT_CRN_ROW da on  DA.ARNGMNT_ID=FT.ARNGMNT_ID "
							+ "where TXN_CAL_ID  BETWEEN '"+vFromDate+"' AND '"+vToDate+"' "
							+ "and DA.ACID='"+custAcid+"'"
							+ " and tran_amt > 100 "
							+ " and tran_type = 'C' "
							+ " and tran_sub_type in('CI','NR','NP','CP','CR','BI','I','O','PI','MU')";
					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString(1));
						pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString(2));
						pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString(3));
						pstmtIsertIntoAml.setString(4, "N");
						pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString(4));
						pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString(5));
						pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString(6));
						pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString(7));
						pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString(8));
						pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString(9));
						pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString(10));
						pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString(11));
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString(12));
						pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString(13));
						pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString(14));
						pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString(15));
						pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString(16));
						pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString(17));
						pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString(18));
						pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString(19));
						pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString(20));
						pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString(21));
						pstmtIsertIntoAml.setString(23, "Y");
						pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString(22));
						pstmtIsertIntoAml.setString(25, "1");
						pstmtIsertIntoAml.setString(26, null);
						pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString(24));
						pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString(26));
						pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString(27));
						pstmtIsertIntoAml.setString(34, strNo);
						pstmtIsertIntoAml.setString(35, getBatchNum(strNo));
						pstmtIsertIntoAml.setString(36, "STR");
						pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString(28));
						pstmtIsertIntoAml.setString(38, getCustType(rsTxnDataWarehouse.getString(22)));
					

						pstmtIsertIntoAml.execute();

						System.out.println(commitCounter);
						if (commitCounter == 500) {
							connection.commit();
							commitCounter = 0;
						}
					}
				}

				connection.commit();
				System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
				
			
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	
	}

	public static boolean insertStrTC1DtlsTemp(List<String> acctList, String strNo, String vFromDate, String vToDate) {
		// TODO Auto-generated method stub
		boolean flg = true;
		Statement stFetchTxnWarehouse = null;
		Connection connection = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml=null;
		int commitCounter = 0;
		makeConnectionToDataWarehouse();
		
		
		
		
		String insertTransaction ="insert into AML_TRANSACTION_MASTER_F(TRAN_ID,TRAN_DATE,PART_TRAN_SRL_NUM,DEL_FLG,CRE_DEB_FLG,"
				+ "GL_SUB_HEAD_CODE,TRAN_ACID,TRAN_AMT,TRAN_PARTICULAR,TRAN_PARTICULAR_2,TRAN_TYPE,TRAN_SUB_TYPE,"
				+ "VALUE_DATE,PSTD_DATE,VFD_DATE,RPT_CODE,REF_NUM,INSTRUMENT_TYPE,INSTRUMENT_DATE,INSTRUMENT_NUM,INSTRUMENT_ALPHA,"
				+ "TRAN_RMKS,PSTD_FLG,TRAN_CUST_ID,RATE_CODE,RATE,TRAN_CRNCY_CODE,REF_CRNCY_CODE,REF_AMT , "
				+ "BRANCH_ID,INIT_BRANCH_ID,ACCOUNT_NO,TRAN_CHANNEL,STR_NO,BATCH_NUM,REPORT_TYPE,REPORT_REF_NUM ,"
				+ "CUST_TYPE)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try  {
			 connection = ConnectionFactory.makeConnectionAMLLive();
			 pstmtIsertIntoAml = connection
					.prepareStatement(insertTransaction);
			 
			 
			 Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::"+dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				String mistableName = amlProp.getProperty("FETCH_MIS_TABLE");
			
			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
		
				
				

				for (String custAcid : acctList) {
					String acctDtlsQuery = "select ft.SRC_TRAN_ID,to_char(ft.SRC_TRAN_DT,'DD-MON-RR') SRC_TRAN_DT,ft.SRC_PART_TRAN_SRL_NUM,ft.PART_TRAN_TYPE,DA.GL_SUB_HEAD_CODE, "
							+ "DA.ACID,ft.TRAN_AMT,ft.TRAN_PARTICULAR,'NA' TRAN_PARTICULAR_2,ft.TRAN_TYPE,ft.TRAN_SUB_TYPE,to_char(ft.VALUE_DATE ,'DD-MON-RR') "
							+ ",to_char(ft.PSTD_DATE ,'DD-MON-RR'),to_char(ft.VFD_DATE,'DD-MON-RR'),ft.RPT_CODE,ft.REF_NUM,ft.INSTRUMENT_TYPE,nvl(to_char(ft.INSTRUMENT_DT,'DD-MON-RR'),null),ft.INSTRUMENT_NUM "
							+ ",ft.INSTRUMENT_ALPHA,ft.TRAN_REMARKS,DA.IP_CODE,nvl(ft.REF_CRNCY_CODE,'INR') REF_CRNCY_CODE,ft.TRAN_AMT REF_AMT,DA.BRANCH_CODE,DA.ACCOUNT_NBR "
							+ ",DECODE(SUBSTR(tran_particular, 1, 4),'CDS/','ATM','ATM ','ATM','NRTG','RTGS','UPI/','UPI','NACH','NACH','IMPS','IMPS','NEFT','NEFT','GTXN') TRAN_CHANNEL,"
							+ "(dense_rank() over (order by DA.IP_CODE)) REPORT_REF_NUM "
							+ "from  "+mistableName+"  ft "
							+ "inner join MIS_TMP.DIM_ARNGMNT_CRN_ROW da on  DA.ARNGMNT_ID=FT.ARNGMNT_ID "
							+ "where TXN_CAL_ID  BETWEEN '"+vFromDate+"' AND '"+vToDate+"' "
							+ "and DA.ACID='"+custAcid+"'"
							+ " and SUBSTR(tran_particular, 1, 4) in ('ATM ','CDS/') "
							+ " and tran_amt > 100 "
							+ " and tran_type != 'C' "
							+ " and tran_sub_type in('CI','NR','NP','CP','CR','BI','I','O','PI','MU')";
					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);
					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString(1));
						pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString(2));
						pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString(3));
						pstmtIsertIntoAml.setString(4, "N");
						pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString(4));
						pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString(5));
						pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString(6));
						pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString(7));
						pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString(8));
						pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString(9));
						pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString(10));
						pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString(11));
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString(12));
						pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString(13));
						pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString(14));
						pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString(15));
						pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString(16));
						pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString(17));
						pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString(18));
						pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString(19));
						pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString(20));
						pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString(21));
						pstmtIsertIntoAml.setString(23, "Y");
						pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString(22));
						pstmtIsertIntoAml.setString(25, "1");
						pstmtIsertIntoAml.setString(26, null);
						pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString(23));
						pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString(24));
						pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString(25));
						pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString(26));
						pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString(27));
						pstmtIsertIntoAml.setString(34, strNo);
						pstmtIsertIntoAml.setString(35, getBatchNum(strNo));
						pstmtIsertIntoAml.setString(36, "STR");
						pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString(28));
						pstmtIsertIntoAml.setString(38, getCustType(rsTxnDataWarehouse.getString(22)));
					

						pstmtIsertIntoAml.execute();

						System.out.println(commitCounter);
						if (commitCounter == 500) {
							connection.commit();
							commitCounter = 0;
						}
					}
				}

				connection.commit();
				System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
				
			
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	
	}

}
