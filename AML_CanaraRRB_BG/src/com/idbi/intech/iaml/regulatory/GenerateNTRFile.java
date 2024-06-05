package com.idbi.intech.iaml.regulatory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenerateNTRFile implements Runnable{
	private static int srl=1;
	private static Connection connAml=null;
	private static Properties amlProp=null;
	private static String stateList[] = null;
	private static GenerateNTRFile ntrProcess =new GenerateNTRFile();
	
	public static void makeConnectionAml() {
		try {
			connAml=ConnectionFactory.makeConnectionAMLLive();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ArrayList<String> getNtrListForGeneration() {
		Statement stmt=null;
		ResultSet rs=null;
		ArrayList<String> strList=null;
		try {
			strList=new ArrayList<String>();
			stmt=connAml.createStatement();
			rs=stmt.executeQuery("select concat(REQ_ID,'~',format(convert(date,FROM_DT,105),'MM'),'~',format(convert(date,FROM_DT,105),'yyyy')) from REG_REQUEST with(nolock) where REQ_STATUS='V' and reg_report_type='NTR'");
			while(rs.next()) {
				strList.add(rs.getString(1));
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(stmt!=null) {
					stmt.close();
					stmt=null;
				}
				
				if(rs!=null) {
					rs.close();
					rs=null;
				}
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return strList;
	}
	
	public int getBizzCount(String requestId,String seqNo) {
		Statement stmt=null;
		ResultSet rs=null;
		int bizzCnt=0;
		try {
			stmt=connAml.createStatement();
			rs=stmt.executeQuery("select count(distinct cust_id) from NTR_BIZZDTLS with(nolock) where REQ_ID='"+requestId+"' and SEQ_NO='"+seqNo+"' and RECORD_TYPE='NTR'");
			while(rs.next()) {
				bizzCnt= rs.getInt(1);
			}
			System.out.println("Total Report Count:: "+bizzCnt);
		}catch(SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(stmt!=null) {
					stmt.close();
					stmt=null;
				}
				
				if(rs!=null) {
					rs.close();
					rs=null;
				}
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return bizzCnt;
	}
	
	public void extractCTRDetails(String requestId,String month,String year,Properties amlProp) {
		CallableStatement callStmt=null;
		Statement stmt=null;
		ResultSet rs=null;
		String seqNo="";
		try {
			
			stmt=connAml.createStatement();
			rs=stmt.executeQuery("select FORMAT(GETDATE(),'ddMMyy') as SeqNo");
			while(rs.next()) {
				seqNo=rs.getString(1);
			}
			
			System.out.println("Batch Number for Request Id:: "+requestId+" is "+seqNo);
			
			System.out.println("Processing NTR Data Details");
			callStmt=connAml.prepareCall("{call dbo.stp_ntr_data('"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			connAml.commit();
			System.out.println("NTR Data Extraction Completed");
			
			System.out.println("Processing Transaction Details");
			callStmt=connAml.prepareCall("{call dbo.stp_ntr_transaction('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			connAml.commit();
			System.out.println("Transaction Details Completed");
			
			System.out.println("Processing Branch Details");
			callStmt=connAml.prepareCall("{call dbo.stp_ntr_branch('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			connAml.commit();
			System.out.println("Branch Details Completed");
			
			System.out.println("Processing Accounts Details");
			callStmt=connAml.prepareCall("{call dbo.stp_ntr_accounts('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			connAml.commit();
			System.out.println("Accounts Details Completed");
			
			System.out.println("Processing Person Details");
			callStmt=connAml.prepareCall("{call dbo.stp_ntr_persondtls('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			connAml.commit();
			System.out.println("Person Details Completed");
			
			System.out.println("Processing Control file Details");
			callStmt=connAml.prepareCall("{call dbo.stp_ntr_ctrlfile('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			connAml.commit();
			System.out.println("Control file Details Completed");
			
			int bizzCount=getBizzCount(requestId, seqNo);
			int fileCount = 1;
			System.out.println("Creating NTR Xml file for Request Id:: "+requestId+" with Batch Number:: "+seqNo);
			for(int cnt=1;cnt<=bizzCount;) {
				createXmlFile(requestId, seqNo,fileCount,month,year,amlProp.getProperty("NTRDIR"));
				cnt+=1000;
				System.out.println("Count Processed:: "+cnt);
				fileCount++;
			}
			
			if(bizzCount > 0)
			{
				compressAndUpdateFileName(requestId,amlProp.getProperty("NTRDIR"));
			}
			
			System.out.println("File Created successfully for Request Id:: "+requestId+" with Batch Number:: "+seqNo);
			
		}catch(SQLException ex) {
			ex.printStackTrace();
		}finally {
			try {
				if(callStmt!=null) {
					callStmt.close();
					callStmt=null;
				}
				
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String getStateCode(String stateName) {
		String stateCode="XX";
		for(String state:stateList) {
			String stateVal[] = state.split("-");
			if(stateVal[1].equals(stateName)) {
				stateCode=stateVal[0];
			}
		}
		return stateCode;
	}
	
	public void createXmlFile(String requestId,String seqNo,int fileCount,String month,String year,String filePath) {
		StringBuffer sb=null;
		
		Statement poStmt=null;
		ResultSet poRs=null;
		
		Statement bizzStmt=null;
		ResultSet bizzRs=null;
		
		Statement branchStmt=null;
		ResultSet branchRs=null;
		
		Statement personStmt=null;
		ResultSet personRs=null;
		
		Statement txnStmt=null;
		ResultSet txnRs=null;
		
		Statement acProcess=null;
		Statement updateRegRequest=null;
		
		Statement custStmt=null;
		ResultSet custRs=null;
		
		String bankname="";
		String bankCategory="";
		String fiureid="";
		String poName="",poDesgination="",poAddress="",poCity="",poState="",poCountry="",poPin="",poTel="",poMobile="",poFax="",poEmail="",poRegNo="";
		String batchDate="";
		String branchNo="",bizzNum="",custId="";
		
		int i=srl;
		File file=null;
		FileOutputStream writeXml=null;
		String finalXml="";
		String filename="";
		
		try {
			
			poStmt=connAml.createStatement();
			bizzStmt=connAml.createStatement();
			branchStmt=connAml.createStatement();
			personStmt=connAml.createStatement();
			txnStmt=connAml.createStatement();
			acProcess=connAml.createStatement();
			updateRegRequest=connAml.createStatement();
			custStmt=connAml.createStatement();
			
			poRs=poStmt.executeQuery("select FORMAT(GETDATE(),'yyyy-MM-dd') UPDATE_DT,COMP_NAME_OF_BANK,COMP_CATEGORY,FIUREID,PO_NAME,PO_DESIGNATION,PO_ADDRESS,PO_CITY,PO_STATE,PO_COUNTRY,PO_PIN,PO_TEL,PO_MOBILE,PO_FAX,PO_EMAIL,PO_REG_NUMBER from PO_DETAILS with(nolock) where PO_STATUS='A' and PO_IS_ACTIVE='Y'");
			while(poRs.next()) {
				batchDate=poRs.getString("UPDATE_DT");
				bankname=poRs.getString("COMP_NAME_OF_BANK");
				bankCategory=poRs.getString("COMP_CATEGORY");
				fiureid=poRs.getString("FIUREID");
				poName=poRs.getString("PO_NAME");
				poDesgination=poRs.getString("PO_DESIGNATION");
				poAddress=poRs.getString("PO_ADDRESS");
				poCity=poRs.getString("PO_CITY");
				poState=poRs.getString("PO_STATE");
				poCountry=poRs.getString("PO_COUNTRY");
				poPin=poRs.getString("PO_PIN");
				poTel=poRs.getString("PO_TEL");
				poMobile=poRs.getString("PO_MOBILE");
				poFax=poRs.getString("PO_FAX");
				poEmail=poRs.getString("PO_EMAIL");
				poRegNo=poRs.getString("PO_REG_NUMBER");
			}
			
			
			sb=new StringBuffer();
			filename="NTR_ARF_"+requestId+"_"+seqNo+"_"+fileCount++ +".xml";
			System.out.println("Creating file:: "+filename);
			
			file=new File(filePath+filename);
			
			writeXml=new FileOutputStream(file);
			
			boolean poFlg =true;
			
			custRs=custStmt.executeQuery("SELECT TOP(1000) * FROM (SELECT DISTINCT CUST_NAME,CUST_ID "
					+ " from NTR_BIZZDTLS with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and PROCESS_FLG='N') cb");
			
			while(custRs.next()) {
				if(poFlg) {
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					sb.append("<Batch>\n");
					sb.append("<ReportType>NTR</ReportType>\n");
					sb.append("<ReportFormatType>ARF</ReportFormatType>\n");
					
					//BatchHeader
					sb.append("<BatchHeader>\n");
					sb.append("<DataStructureVersion>2</DataStructureVersion>\n");
					sb.append("<GenerationUtilityVersion></GenerationUtilityVersion>\n");
					sb.append("<DataSource>xml</DataSource>\n");
					sb.append("</BatchHeader>\n");
					
					//ReportingEntity
					sb.append("<ReportingEntity>\n");
					sb.append("<ReportingEntityName>"+bankname+"</ReportingEntityName>\n");
					sb.append("<ReportingEntityCategory>"+bankCategory+"</ReportingEntityCategory>\n");
					sb.append("<RERegistrationNumber>"+poRegNo+"</RERegistrationNumber>\n");
					sb.append("<FIUREID>"+fiureid+"</FIUREID>\n");
					sb.append("</ReportingEntity>\n");
					
					//PrincipalOfficer
					sb.append("<PrincipalOfficer>\n");
					sb.append("<POName>"+poName+"</POName>\n");
					sb.append("<PODesignation>"+poDesgination+"</PODesignation>\n");
					sb.append("<POAddress>\n");
					sb.append("<Address>"+poAddress+"</Address>\n");
					sb.append("<City>"+poCity+"</City>\n");
					sb.append("<StateCode>"+poState+"</StateCode>\n");
					sb.append("<PinCode>"+poPin+"</PinCode>\n");
					sb.append("<CountryCode>"+poCountry+"</CountryCode>\n");
					sb.append("</POAddress>\n");
					sb.append("<POPhone>\n");
					sb.append("<Telephone>"+poTel+"</Telephone>\n");
					if(poMobile != null)
					{
						poMobile = poMobile.length() == 10 || poMobile.length() == 12 ? poMobile : "";
						sb.append("<Mobile>"+poMobile+"</Mobile>\n");
					}
					else
					{
						sb.append("<Mobile></Mobile>\n");
					}
					sb.append("<Fax>"+poFax+"</Fax>\n");
					sb.append("</POPhone>\n");
					sb.append("<POEmail>"+poEmail+"</POEmail>\n");
					sb.append("</PrincipalOfficer>\n");
					
					//BatchDetails
					sb.append("<BatchDetails>\n");
					sb.append("<BatchNumber>"+seqNo+"</BatchNumber>\n");
					sb.append("<BatchDate>"+batchDate+"</BatchDate>\n");
					sb.append("<MonthOfReport>"+month+"</MonthOfReport>\n");
					sb.append("<YearOfReport>"+year+"</YearOfReport>\n");
					sb.append("<OperationalMode>P</OperationalMode>\n");
					sb.append("<BatchType>N</BatchType>\n");
					sb.append("<OriginalBatchID>0</OriginalBatchID>\n");
					sb.append("<ReasonOfRevision>N</ReasonOfRevision>\n");
					sb.append("<PKICertificateNum></PKICertificateNum>\n");
					sb.append("</BatchDetails>\n");
				}
				poFlg=false;
				
				custId = custRs.getString("CUST_ID");
				
				//Report
				sb.append("<Report>\n");
				sb.append("<ReportSerialNum>"+i+"</ReportSerialNum>\n");
				sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>\n");
				sb.append("<MainPersonName>"
						+ (custRs.getString("CUST_NAME").length() > 75 ? custRs
								.getString("CUST_NAME").substring(0, 75)
								: custRs.getString("CUST_NAME"))
						+ "</MainPersonName>\n");
				
				bizzRs=bizzStmt.executeQuery("SELECT BIZZ_NO,BRANCH_NO,BIZZ_HLDR_NAME,BIZZ_TYPE,BIZZ_HLDR_TYPE,BIZZ_OPN_DT,RISK_RATE,BIZZ_STATUS,CUST_NAME,CUST_ID,"
						+ " (select case when count(1)>0 then 'N' else 'Y' end from NTR_TRANSACTION with(nolock) where SEQ_NO=rb.SEQ_NO and REQ_ID=rb.REQ_ID and BIZZ_NO=rb.BIZZ_NO) TXNFLG "
						+ " from NTR_BIZZDTLS rb with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and CUST_ID='"+custId+"'");
				
				while(bizzRs.next()) {
					branchNo=bizzRs.getString("BRANCH_NO");
					bizzNum=bizzRs.getString("BIZZ_NO");
					
					acProcess.executeUpdate("UPDATE NTR_BIZZDTLS set PROCESS_FLG='P' where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"' ");
					
					//Account
					sb.append("<Account>\n");
					sb.append("<AccountDetails>\n");
					sb.append("<AccountNumber>"+bizzNum+"</AccountNumber>\n");
					sb.append("<AccountType>"+bizzRs.getString("BIZZ_TYPE")+"</AccountType>\n");
					sb.append("<HolderName>"+bizzRs.getString("BIZZ_HLDR_NAME")+"</HolderName>\n");
					sb.append("<AccountHolderType>"+bizzRs.getString("BIZZ_HLDR_TYPE")+"</AccountHolderType>\n");
					sb.append("<AccountStatus>"+bizzRs.getString("BIZZ_STATUS")+"</AccountStatus>\n");
					sb.append("<DateOfOpening>"+bizzRs.getString("BIZZ_OPN_DT")+"</DateOfOpening>\n");
					sb.append("<RiskRating>"+bizzRs.getString("RISK_RATE")+"</RiskRating>\n");
					sb.append("<CumulativeCreditTurnover>0</CumulativeCreditTurnover>\n");
					sb.append("<CumulativeDebitTurnover>0</CumulativeDebitTurnover>\n");
					sb.append("<CumulativeCashDepositTurnover>0</CumulativeCashDepositTurnover>\n");
					sb.append("<CumulativeCashWithdrawalTurnover>0</CumulativeCashWithdrawalTurnover>\n");
					sb.append("<NoTransactionsTobeReported>"+bizzRs.getString("TXNFLG")+"</NoTransactionsTobeReported>\n");
					sb.append("</AccountDetails>\n");
					
					//Branch				
					branchRs=branchStmt.executeQuery("SELECT DISTINCT BRANCH_NAME,BRANCH_ADDRESS,BRANCH_CITY,BRANCH_STATE,BRANCH_PINCODE,BRANCH_TELEPONE,BRANCH_FAX,BRANCH_EMAIL from NTR_BRANCH with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BRANCH_NO='"+branchNo+"'");
					
					while(branchRs.next()) {
						sb.append("<Branch>\n");
						sb.append("<BranchRefNumType>S</BranchRefNumType>\n");
						sb.append("<BranchRefNum>"+branchNo+"</BranchRefNum>\n");
						sb.append("<BranchDetails>\n");
						sb.append("<BranchName>"+branchRs.getString("BRANCH_NAME")+"</BranchName>\n");
						sb.append("<BranchAddress>\n");
						sb.append("<Address>");
						sb.append(branchRs.getString("BRANCH_ADDRESS")==null?(branchNo+":"+branchRs
								.getString("BRANCH_NAME")+":"+branchRs.getString("BRANCH_STATE")+":"+branchRs.getString("BRANCH_PINCODE")):branchRs.getString("BRANCH_ADDRESS").length() <= 15 ? (branchNo+":"+branchRs
								.getString("BRANCH_NAME")+":"+branchRs.getString("BRANCH_STATE")+":"+branchRs.getString("BRANCH_PINCODE"))
								: branchRs.getString("BRANCH_ADDRESS"));
						sb.append("</Address>\n");
						sb.append("<City>");
						sb.append(branchRs.getString("BRANCH_CITY")==null?"":branchRs.getString("BRANCH_CITY"));
						sb.append("</City>\n");
						sb.append("<StateCode>"+getStateCode(branchRs.getString("BRANCH_STATE"))+"</StateCode>\n");
						sb.append("<PinCode>"+branchRs.getString("BRANCH_PINCODE")+"</PinCode>\n");
						sb.append("<CountryCode>IN</CountryCode>\n");
						sb.append("</BranchAddress>\n");
						sb.append("<BranchPhone>\n");
						sb.append("<Telephone></Telephone>\n");
//						sb.append("<Telephone>");
//						sb.append(branchRs.getString("BRANCH_TELEPONE")==null?"":branchRs.getString("BRANCH_TELEPONE"));
//						sb.append("</Telephone>");
						sb.append("<Mobile></Mobile>\n");
						sb.append("<Fax></Fax>\n");
						sb.append("</BranchPhone>\n");
						sb.append("<BranchEmail></BranchEmail>\n");
//						sb.append("<BranchEmail>");
//						sb.append(branchRs.getString("BRANCH_EMAIL")==null?"":branchRs.getString("BRANCH_EMAIL"));
//						sb.append("</BranchEmail>");
						sb.append("</BranchDetails>\n");
						sb.append("</Branch>\n");
					}
					
					//PersonDetails
					personRs=personStmt.executeQuery("SELECT RELATION_FLG,CUSTOMER_NAME,CUST_ID,COMMU_ADDR,COMMU_CITY,COMMU_STATE,COMMU_PIN,COMMU_CNTRY,COMMU_TEL,COMMU_MOBILE,COMMU_EMAIL,OCCUPATION,DATE_OF_BIRTH,CUST_GENDER,NATIONALITY,TYPE_OF_ID,ID_NO,PAN_NO,CONST_TYPE,NATURE_OF_BIZZ,DATE_OF_ICORP,CUST_TYPE from NTR_INDLPE with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"'");
					
					while(personRs.next()) {
						
						sb.append("<PersonDetails>\n");
						sb.append("<PersonName>"+personRs.getString("CUSTOMER_NAME")+"</PersonName>\n");
						sb.append("<CustomerId>"+personRs.getString("CUST_ID")+"</CustomerId>\n");
						sb.append("<RelationFlag>"+personRs.getString("RELATION_FLG")+"</RelationFlag>\n");
						sb.append("<CommunicationAddress>\n");
						sb.append("<Address>");
						sb.append(personRs.getString("COMMU_ADDR").length() <= 15 ? (personRs.getString("CUSTOMER_NAME")+":"+personRs
								.getString("COMMU_CITY")+":"+personRs.getString("COMMU_STATE")+":"+personRs.getString("COMMU_PIN")+":")
								: personRs.getString("COMMU_ADDR"));
						sb.append("</Address>\n");
						sb.append("<City>");
						sb.append(personRs.getString("COMMU_CITY")==null?"":personRs.getString("COMMU_CITY"));
						sb.append("</City>\n");
						sb.append("<StateCode>"+getStateCode(personRs.getString("COMMU_STATE"))+"</StateCode>\n");
						sb.append("<PinCode>");
						sb.append(personRs.getString("COMMU_PIN")==null?"":personRs.getString("COMMU_PIN"));
						sb.append("</PinCode>\n");
						sb.append("<CountryCode>"+personRs.getString("COMMU_CNTRY")+"</CountryCode>\n");
						sb.append("</CommunicationAddress>\n");
						sb.append("<Phone>\n");
						sb.append("<Telephone>");
						sb.append(personRs.getString("COMMU_TEL")==null?"":personRs.getString("COMMU_TEL"));
						sb.append("</Telephone>\n");
						/*sb.append("<Mobile>");
						sb.append(personRs.getString("COMMU_MOBILE")==null?"":personRs.getString("COMMU_MOBILE"));
						sb.append("</Mobile>\n");*/
						String pMobile= personRs.getString("COMMU_MOBILE");
						if(pMobile != null)
						{
							pMobile = pMobile.length() == 10 || pMobile.length() == 12 ? pMobile : "";
							sb.append("<Mobile>"+pMobile+"</Mobile>\n");
						}
						else
						{
							sb.append("<Mobile></Mobile>\n");
						}
						sb.append("<Fax></Fax>\n");
						sb.append("</Phone>\n");
						sb.append("<Email>");
						sb.append(personRs.getString("COMMU_EMAIL")==null?"":personRs.getString("COMMU_EMAIL"));
						sb.append("</Email>\n");
						
						if(personRs.getString("PAN_NO")==null||personRs.getString("PAN_NO").equals("PANNOTREQD")||personRs.getString("PAN_NO").equals("PANINVALID")||personRs.getString("PAN_NO").equals("FORM60")||personRs.getString("PAN_NO").equals("PANNOTAVBL")
								||personRs.getString("PAN_NO").equals("FORM 61")||personRs.getString("PAN_NO").equals("FORM 60")||personRs.getString("PAN_NO").equals("FORM 60")){
							sb.append("<PAN></PAN>\n");
						}else{
							if(personRs.getString("PAN_NO").length() == 10)
							{
								sb.append("<PAN>");
								sb.append(personRs.getString("PAN_NO") == null ? "" : personRs.getString("PAN_NO"));
								sb.append("</PAN>\n");
							}
							else
							{
								sb.append("<PAN></PAN>\n");
							}
						}
						//sb.append("<UIN></UIN>");
						
						if(personRs.getString("CUST_TYPE").equals("I")) {
							sb.append("<Individual>\n");
							String gender = personRs.getString("CUST_GENDER");
							if(gender != null)
							{
								if(gender.equals("M") || gender.equals("F"))
								{
									sb.append("<Gender>"+gender+"</Gender>\n");
								}
								else
								{
									sb.append("<Gender>X</Gender>\n");
								}
							}
							else
							{
								sb.append("<Gender></Gender>\n");
							}
							sb.append("<DateOfBirth>");
							sb.append(personRs.getString("DATE_OF_BIRTH")==null?"1900-01-01":personRs.getString("DATE_OF_BIRTH"));
							sb.append("</DateOfBirth>\n");
							sb.append("<IdentificationType>"+personRs.getString("TYPE_OF_ID")+"</IdentificationType>\n");
							sb.append("<IdentificationNumber>"+personRs.getString("ID_NO")+"</IdentificationNumber>\n");
							//sb.append("<IssuingAuthority></IssuingAuthority>");
							//sb.append("<PlaceOfIssue></PlaceOfIssue>");
							sb.append("<Nationality>"+personRs.getString("NATIONALITY")+"</Nationality>\n");
							//sb.append("<PlaceOfWork></PlaceOfWork>");
							//sb.append("<FatherOrSpouse></FatherOrSpouse>");
							sb.append("<Occupation>"+personRs.getString("OCCUPATION")+"</Occupation>\n");
							sb.append("</Individual>\n");
						}else {
							sb.append("<LegalPerson>\n");
							sb.append("<ConstitutionType>"+personRs.getString("CONST_TYPE")+"</ConstitutionType>\n");
							sb.append("<RegistrationNumber></RegistrationNumber>\n");
							sb.append("<DateOfIncorporation>"+personRs.getString("DATE_OF_ICORP")+"</DateOfIncorporation>\n");
							sb.append("<CountryCode>"+personRs.getString("COMMU_CNTRY")+"</CountryCode>\n");
							sb.append("<NatureOfBusiness>"+personRs.getString("NATURE_OF_BIZZ")+"</NatureOfBusiness>\n");
							sb.append("</LegalPerson>\n");
						}
						sb.append("</PersonDetails>\n");
						
					}
					
					//Transaction
					txnRs=txnStmt.executeQuery("select TRAN_ID,TRAN_DATE,TRAN_TYPE,CR_DB_FLG,CAST(AMOUNT as INT) AMOUNT,TRAN_CRNCY from NTR_TRANSACTION with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"'");
					
					while(txnRs.next()) {
						sb.append("<Transaction>\n");
						sb.append("<DateOfTransaction>"+txnRs.getString("TRAN_DATE")+"</DateOfTransaction>\n");
						sb.append("<TransactionID>"+txnRs.getString("TRAN_ID")+"</TransactionID>\n");
						
						String txnMode =  txnRs.getString("TRAN_TYPE") == null ? "X" : txnRs.getString("TRAN_TYPE");
						
						if(txnMode.equals("A") || txnMode.equals("B") || txnMode.equals("C") || txnMode.equals("D") || txnMode.equals("E") || txnMode.equals("F") || txnMode.equals("G") || txnMode.equals("S") || txnMode.equals("Z"))
						{
							sb.append("<TransactionMode>"+txnMode+"</TransactionMode>\n");
						}
						else
						{
							sb.append("<TransactionMode>X</TransactionMode>\n");
						}
						sb.append("<DebitCredit>"+txnRs.getString("CR_DB_FLG")+"</DebitCredit>\n");
						sb.append("<Amount>"+txnRs.getString("AMOUNT")+"</Amount>\n");
						sb.append("<Currency>"+txnRs.getString("TRAN_CRNCY")+"</Currency>\n");	
						sb.append("</Transaction>\n");
					}
					sb.append("</Account>\n");
					acProcess.executeUpdate("update NTR_BIZZDTLS set PROCESS_FLG='Y',FILE_NAME='"+filename+"' where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"'");
				}
				sb.append("</Report>\n");
				i++;
			}
			sb.append("</Batch>");
			finalXml=sb.toString().replace("&", " &amp; ");
			String strxml= new String(finalXml.getBytes("iso-8859-1"),"UTF-8");
			writeXml.write(strxml.getBytes());
			writeXml.close();
			
			updateRegRequest.executeUpdate("update REG_REQUEST set  REQ_STATUS='X' where REQ_ID='"+requestId+"'");
			
			connAml.commit();
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(poStmt!=null) {
					poStmt.close();
					poStmt=null;
				}
				if(acProcess!=null) {
					acProcess.close();
					acProcess=null;
				}
				if(branchStmt!=null) {
					branchStmt.close();
					branchStmt=null;
				}
				if(bizzStmt!=null) {
					bizzStmt.close();
					bizzStmt=null;
				}
				if(personStmt!=null) {
					personStmt.close();
					personStmt=null;
				}
				if(txnStmt!=null) {
					txnStmt.close();
					txnStmt=null;
				}
				if(custStmt!=null) {
					custStmt.close();
					custStmt=null;
				}
				if(updateRegRequest!=null) {
					updateRegRequest.close();
					updateRegRequest=null;
				}
				if(poRs!=null) {
					poRs.close();
					poRs=null;
				}
				if(branchRs!=null) {
					branchRs.close();
					branchRs=null;
				}
				if(bizzRs!=null) {
					bizzRs.close();
					bizzRs=null;
				}
				if(personRs!=null) {
					personRs.close();
					personRs=null;
				}
				if(txnRs!=null) {
					txnRs.close();
					txnRs=null;
				}
				if(custRs!=null) {
					custRs.close();
					custRs=null;
				}
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		srl = i;
	}
	
	public void compressAndUpdateFileName(String requestId,String filePath) throws SQLException
	{
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String> fileList = new ArrayList<String>();
		
		try
		{
			String sql = "select distinct file_name from NTR_BIZZDTLS where req_id='"+requestId+"'";
			stmt = connAml.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				fileList.add(filePath+""+rs.getString(1).trim());
			}
			
			if(fileList.size() > 0)
			{
				String[] fileArr = new String[fileList.size()];
				fileList.toArray(fileArr);
				
				FilesCompresser.compress(filePath+""+requestId, fileArr);
				
				pstmt = connAml.prepareStatement("update REG_REQUEST set file_name='"+requestId+"' where req_id='"+requestId+"' and reg_report_type='NTR'");
				pstmt.executeUpdate();
				
				connAml.commit();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(pstmt != null)
			{
				pstmt.close();
			}
		}
	}

	public static void main(String args[]) {
		makeConnectionAml();
		Thread threadObj =new Thread(new GenerateNTRFile());
		threadObj.start();
	}

	@Override
	public void run() {
		ArrayList<String> reqidList =null;	
		while(true) {
			reqidList=getNtrListForGeneration();
			if(reqidList.size()>0) {
				amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				System.out.println("Current Directory::"+dir);
				InputStream is;
				try {
				is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				is.close();
				stateList = amlProp.getProperty("STATE").split(",");
				for(String reqDetails:reqidList) {
					String reqData[]=reqDetails.split("~");
					System.out.println("--------------------------------------------------------------------------------------------------");
					System.out.println("Generating NTR Xml file for :: "+reqData[0]);
					extractCTRDetails(reqData[0],reqData[1],reqData[2],amlProp);
				}
				Thread.sleep(100000 * 6);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			}
		}
	}
	
	public void start() {
		try {
			makeConnectionAml();
			Thread threadObj= new Thread(new GenerateNTRFile());
			threadObj.start();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void stop() {
		
	}
	
	public static void windowsService(String args[]) {
		String cmd="start";
		if(args.length>0) {
			cmd=args[0];
		}
		
		if("start".equals(cmd)) {
			ntrProcess.start();
		}else {
			ntrProcess.stop();
		}
	}
}
