package com.idbi.intech.iaml.regulatory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenerateSTRFile implements Runnable{
	private static Connection connAml=null;
	private static Properties amlProp=null;
	private static String stateList[] = null;
	private static GenerateSTRFile strProcess =new GenerateSTRFile();
	
	public static void makeConnectionAml() {
		try {
			connAml=ConnectionFactory.makeConnectionAMLLive();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ArrayList<String> getStrListForGeneration() {
		Statement stmt=null;
		ResultSet rs=null;
		ArrayList<String> strList=null;
		try {
			strList=new ArrayList<String>();
			stmt=connAml.createStatement();
			rs=stmt.executeQuery("select concat(REQ_ID,'~', MONTH(update_dt),'~',YEAR(update_dt)) from REG_REQUEST with(nolock) where REQ_STATUS='V' and reg_report_type='STR'");
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
	
	public void extractSTRDetails(String requestId,String month,String year,Properties amlProp) {
		CallableStatement callStmt=null;
		Statement stmt=null;
		ResultSet rs=null;
		String seqNo="";
		try {
			
			stmt=connAml.createStatement();
			rs=stmt.executeQuery("select FORMAT(GETDATE(),'ddMMyy')+RIGHT(REPLICATE(0,5)+ CONVERT (varchar,(next value for strseqno)),5) as SeqNo");
			while(rs.next()) {
				seqNo=rs.getString(1);
			}
			
			System.out.println("Batch Number for Request Id:: "+requestId+" is "+seqNo);
			
			System.out.println("Processing Transaction Details");
			callStmt=connAml.prepareCall("{call stp_str_transaction('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			System.out.println("Transaction Details Completed");
			
			System.out.println("Processing Branch Details");
			callStmt=connAml.prepareCall("{call stp_str_branch('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			System.out.println("Branch Details Completed");
			
			System.out.println("Processing Bizz Details");
			callStmt=connAml.prepareCall("{call stp_str_accounts('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			System.out.println("Bizz Details Completed");
			
			System.out.println("Processing Person Details");
			callStmt=connAml.prepareCall("{call stp_str_persondtls('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			callStmt.close();
			System.out.println("Person Details Completed");
			
			System.out.println("Processing Control file Details");
			callStmt=connAml.prepareCall("{call stp_str_ctrlfile('"+seqNo+"','"+requestId+"')}");
			callStmt.execute();
			System.out.println("Control file Details Completed");
			
			System.out.println("Creating STR Xml file for Request Id:: "+requestId+" with Batch Number:: "+seqNo+" in month ::"+month+" and Year::"+year);
			
			createXmlFile(requestId, seqNo,month,year,amlProp.getProperty("STRDIR"));
			System.out.println("File Created successfully for Request Id:: "+requestId+" with Batch Number:: "+seqNo+" in month ::"+month+" and Year::"+year);
			
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
	
	public void createXmlFile(String requestId,String seqNo,String month,String year,String filePath) {
		StringBuffer sb=null;
		
		Statement poStmt=null;
		ResultSet poRs=null;
		
		Statement reqStmt=null;
		ResultSet reqRs=null;
		
		Statement bizzStmt=null;
		ResultSet bizzRs=null;
		
		Statement branchStmt=null;
		ResultSet branchRs=null;
		ResultSet branchRsCount=null;
		
		Statement personStmt=null;
		ResultSet personRs=null;
		
		Statement txnStmt=null;
		ResultSet txnRs=null;
		
		Statement acProcess=null;
		Statement updateRegRequest=null;
		
		String bankname="";
		String bankCategory="";
		String fiureid="";
		String poName="",poDesgination="",poAddress="",poCity="",poState="",poCountry="",poPin="",poTel="",poMobile="",poFax="",poEmail="",poRegNo="";
		String batchDate="",gos="",doi="",suspCrime="",suspCmplxTxn="",suspEcoRational="",suspTerrorism="",attemptTxn="",leaInformed="",sourceAlert="",leaDetails="",priorityRate="",reportCoverage="",additionalDocs="";
		String branchNo="",bizzNum="";
		
		int i=1;
		File file=null;
		FileOutputStream writeXml=null;
		String finalXml="";
		String filename="";
		
		try {
			
			poStmt=connAml.createStatement();
			reqStmt=connAml.createStatement();
			bizzStmt=connAml.createStatement();
			branchStmt=connAml.createStatement();
			personStmt=connAml.createStatement();
			txnStmt=connAml.createStatement();
			acProcess=connAml.createStatement();
			updateRegRequest=connAml.createStatement();
			
			poRs=poStmt.executeQuery("select COMP_NAME_OF_BANK,COMP_CATEGORY,FIUREID,PO_NAME,PO_DESIGNATION,PO_ADDRESS,PO_CITY,PO_STATE,PO_COUNTRY,PO_PIN,PO_TEL,PO_MOBILE,PO_FAX,PO_EMAIL,PO_REG_NUMBER from PO_DETAILS with(nolock) where PO_STATUS='A' and PO_IS_ACTIVE='Y'");
			while(poRs.next()) {
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
			
			
			reqRs=reqStmt.executeQuery("select FORMAT(UPDATE_DT,'yyyy-MM-dd') UPDATE_DT,GOS,DOI,SUSP_PROCD_CRIME,SUSP_CMPLX_TXN,SUSP_ECO_RATIONAL,SUSP_FIN_TERRORISM,ATTEMPED_TXN,LEA_INFORMED,SOURCE_OF_ALERT,LEA_DETAILS,PRIORITY_RATING,REPORT_COVERAGE,ADDITIONAL_DOCS from REG_REQUEST with(nolock) where REQ_ID='"+requestId+"'");
			while(reqRs.next()) {
				batchDate=reqRs.getString("UPDATE_DT");
				gos=reqRs.getString("GOS");
				doi=reqRs.getString("DOI");
				suspCrime=reqRs.getString("SUSP_PROCD_CRIME");
				suspCmplxTxn=reqRs.getString("SUSP_CMPLX_TXN");
				suspEcoRational=reqRs.getString("SUSP_ECO_RATIONAL");
				suspTerrorism=reqRs.getString("SUSP_FIN_TERRORISM");
				attemptTxn=reqRs.getString("ATTEMPED_TXN");
				leaInformed=reqRs.getString("LEA_INFORMED");
				sourceAlert=reqRs.getString("SOURCE_OF_ALERT");
				leaDetails=reqRs.getString("LEA_DETAILS");
				reportCoverage=reqRs.getString("REPORT_COVERAGE");
				additionalDocs=reqRs.getString("ADDITIONAL_DOCS");
				priorityRate=reqRs.getString("PRIORITY_RATING");
			}
			
			sb=new StringBuffer();
			filename="STR_ARF_"+requestId+"_"+seqNo+".xml";
			System.out.println("Creating file:: "+filename);
			
			file=new File(filePath+filename);
			
			writeXml=new FileOutputStream(file);
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sb.append("<Batch>\n");
			sb.append("<ReportType>STR</ReportType>\n");
			sb.append("<ReportFormatType>ARF</ReportFormatType>\n");
			
			//BatchHeader
			sb.append("<BatchHeader>\n");
			sb.append("<DataStructureVersion>2</DataStructureVersion>\n");
			sb.append("<GenerationUtilityVersion></GenerationUtilityVersion>\n");
			sb.append("<DataSource>xml</DataSource>\n");
			sb.append("</BatchHeader>\n");
			
			//ReportingEntity
			sb.append("<ReportingEntity>");
			sb.append("<ReportingEntityName>"+bankname+"</ReportingEntityName>");
			sb.append("<ReportingEntityCategory>"+bankCategory+"</ReportingEntityCategory>");
			sb.append("<RERegistrationNumber>"+poRegNo+"</RERegistrationNumber>");
			sb.append("<FIUREID>"+fiureid+"</FIUREID>");
			sb.append("</ReportingEntity>\n");
			
			//PrincipalOfficer
			sb.append("<PrincipalOfficer>");
			sb.append("<POName>"+poName+"</POName>");
			sb.append("<PODesignation>"+poDesgination+"</PODesignation>");
			sb.append("<POAddress>");
			sb.append("<Address>"+poAddress+"</Address>");
			sb.append("<City>"+poCity+"</City>");
			sb.append("<StateCode>"+poState+"</StateCode>");
			sb.append("<PinCode>"+poPin+"</PinCode>");
			sb.append("<CountryCode>"+poCountry+"</CountryCode>");
			sb.append("</POAddress>");
			sb.append("<POPhone>");
			sb.append("<Telephone>"+poTel+"</Telephone>");
			sb.append("<Mobile>"+poMobile+"</Mobile>");
			sb.append("<Fax>"+poFax+"</Fax>");
			sb.append("</POPhone>");
			sb.append("<POEmail>"+poEmail+"</POEmail>");
			sb.append("</PrincipalOfficer>\n");
			
			//BatchDetails
			sb.append("<BatchDetails>");
			sb.append("<BatchNumber>"+seqNo+"</BatchNumber>");
			sb.append("<BatchDate>"+batchDate+"</BatchDate>");
			sb.append("<MonthOfReport>"+month+"</MonthOfReport>");
			sb.append("<YearOfReport>"+year+"</YearOfReport>");
			sb.append("<OperationalMode>P</OperationalMode>");
			sb.append("<BatchType>N</BatchType>");
			sb.append("<OriginalBatchID>0</OriginalBatchID>");
			sb.append("<ReasonOfRevision>N</ReasonOfRevision>");
			sb.append("<PKICertificateNum></PKICertificateNum>");
			sb.append("</BatchDetails>\n");
			
			//Report
			sb.append("<Report>\n");
			sb.append("<ReportSerialNum>"+i+"</ReportSerialNum>\n");
			sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>\n");
			//sb.append("<MainPersonName></MainPersonName>");
			
			//SuspicionDetails
			sb.append("<SuspicionDetails>\n");
			sb.append("<SourceOfAlert>"+sourceAlert+"</SourceOfAlert>\n");
			//sb.append("<AlertIndicator></AlertIndicator>\n");
			sb.append("<SuspicionDueToProceedsOfCrime>"+suspCrime+"</SuspicionDueToProceedsOfCrime>\n");
			sb.append("<SuspicionDueToComplexTrans>"+suspCmplxTxn+"</SuspicionDueToComplexTrans>\n");
			sb.append("<SuspicionDueToNoEcoRationale>"+suspEcoRational+"</SuspicionDueToNoEcoRationale>\n");
			sb.append("<SuspicionOfFinancingOfTerrorism>"+suspTerrorism+"</SuspicionOfFinancingOfTerrorism>\n");
			sb.append("<AttemptedTransaction>"+attemptTxn+"</AttemptedTransaction>\n");
			sb.append("<GroundsOfSuspicion>"+gos+"</GroundsOfSuspicion>\n");
			sb.append("<DetailsOfInvestigation>"+doi+"</DetailsOfInvestigation>\n");
			sb.append("<LEAInformed>"+leaInformed+"</LEAInformed>\n");
			//sb.append("<LEADetails></LEADetails>\n");
			sb.append("<PriorityRating>"+priorityRate+"</PriorityRating>\n");
			sb.append("<ReportCoverage>"+reportCoverage+"</ReportCoverage>\n");
			sb.append("<AdditionalDocuments>"+additionalDocs+"</AdditionalDocuments>\n");
			sb.append("</SuspicionDetails>\n");
			
			bizzRs=bizzStmt.executeQuery("select BIZZ_NO,BRANCH_NO,BIZZ_HLDR_NAME,BIZZ_TYPE,BIZZ_HLDR_TYPE,BIZZ_OPN_DT,RISK_RATE,BIZZ_STATUS,CUST_NAME,CUST_ID,"
					+ " (select case when count(1)>0 then 'N' else 'Y' end from REG_TRANSACTION with(nolock) where SEQ_NO=rb.SEQ_NO and REQ_ID=rb.REQ_ID and BIZZ_NO=rb.BIZZ_NO) TXNFLG "
					+ " from REG_BIZZDTLS rb with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and PROCESS_FLG='N'");
			
			while(bizzRs.next()) {
				branchNo=bizzRs.getString("BRANCH_NO");
				bizzNum=bizzRs.getString("BIZZ_NO");
				
				acProcess.executeUpdate("update REG_BIZZDTLS set PROCESS_FLG='P' where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"' ");
				
				//Account
				sb.append("<Account>");
				sb.append("<AccountDetails>");
				sb.append("<AccountNumber>"+bizzNum+"</AccountNumber>");
				sb.append("<AccountType>"+bizzRs.getString("BIZZ_TYPE")+"</AccountType>");
				sb.append("<HolderName>"+bizzRs.getString("BIZZ_HLDR_NAME")+"</HolderName>");
				sb.append("<AccountHolderType>"+bizzRs.getString("BIZZ_HLDR_TYPE")+"</AccountHolderType>");
				sb.append("<AccountStatus>"+bizzRs.getString("BIZZ_STATUS")+"</AccountStatus>");
				sb.append("<DateOfOpening>"+bizzRs.getString("BIZZ_OPN_DT")+"</DateOfOpening>");
				sb.append("<RiskRating>"+bizzRs.getString("RISK_RATE")+"</RiskRating>");
				sb.append("<CumulativeCreditTurnover>0</CumulativeCreditTurnover>");
				sb.append("<CumulativeDebitTurnover>0</CumulativeDebitTurnover>");
				sb.append("<CumulativeCashDepositTurnover>0</CumulativeCashDepositTurnover>");
				sb.append("<CumulativeCashWithdrawalTurnover>0</CumulativeCashWithdrawalTurnover>");
				sb.append("<NoTransactionsTobeReported>"+bizzRs.getString("TXNFLG")+"</NoTransactionsTobeReported>");
				sb.append("</AccountDetails>");
				
				//Branch				
				branchRs=branchStmt.executeQuery("select DISTINCT BRANCH_NAME,BRANCH_ADDRESS,BRANCH_CITY,BRANCH_STATE,BRANCH_PINCODE,BRANCH_TELEPONE,BRANCH_FAX,BRANCH_EMAIL from REG_BRANCH with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BRANCH_NO='"+branchNo+"'");
				
				while(branchRs.next()) {
					
					sb.append("<Branch>");
					sb.append("<BranchRefNumType>S</BranchRefNumType>");
					sb.append("<BranchRefNum>"+branchNo+"</BranchRefNum>");
					sb.append("<BranchDetails>");
					sb.append("<BranchName>"+branchRs.getString("BRANCH_NAME")+"</BranchName>");
					sb.append("<BranchAddress>");
					sb.append("<Address>");
					sb.append(branchRs.getString("BRANCH_ADDRESS").length() <= 15 ? (branchNo+":"+branchRs
							.getString("BRANCH_NAME")+":"+branchRs.getString("BRANCH_STATE")+":"+branchRs.getString("BRANCH_PINCODE"))
							: branchRs.getString("BRANCH_ADDRESS"));
					sb.append("</Address>");
					sb.append("<City>");
					sb.append(branchRs.getString("BRANCH_CITY")==null?"":branchRs.getString("BRANCH_CITY"));
					sb.append("</City>");
					sb.append("<StateCode>"+getStateCode(branchRs.getString("BRANCH_STATE"))+"</StateCode>");
					sb.append("<PinCode>"+branchRs.getString("BRANCH_PINCODE")+"</PinCode>");
					sb.append("<CountryCode>IN</CountryCode>");
					sb.append("</BranchAddress>");
					sb.append("<BranchPhone>");
					sb.append("<Telephone></Telephone>");
//					sb.append("<Telephone>");
//					sb.append(branchRs.getString("BRANCH_TELEPONE")==null?"":branchRs.getString("BRANCH_TELEPONE"));
//					sb.append("</Telephone>");
					sb.append("<Mobile></Mobile>");
					sb.append("<Fax></Fax>");
					sb.append("</BranchPhone>");
					sb.append("<BranchEmail></BranchEmail>");
//					sb.append("<BranchEmail>");
//					sb.append(branchRs.getString("BRANCH_EMAIL")==null?"":branchRs.getString("BRANCH_EMAIL"));
//					sb.append("</BranchEmail>");
					sb.append("</BranchDetails>");
					sb.append("</Branch>");
				}
				
				//PersonDetails
				personRs=personStmt.executeQuery("select RELATION_FLG,CUSTOMER_NAME,CUST_ID,COMMU_ADDR,COMMU_CITY,COMMU_STATE,COMMU_PIN,COMMU_CNTRY,COMMU_TEL,COMMU_MOBILE,COMMU_EMAIL,OCCUPATION,DATE_OF_BIRTH,CUST_GENDER,NATIONALITY,TYPE_OF_ID,ID_NO,PAN_NO,CONST_TYPE,NATURE_OF_BIZZ,DATE_OF_ICORP,CUST_TYPE from REG_INDLPE with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"'");
				
				while(personRs.next()) {
					
					sb.append("<PersonDetails>");
					sb.append("<PersonName>"+personRs.getString("CUSTOMER_NAME")+"</PersonName>");
					sb.append("<CustomerId>"+personRs.getString("CUST_ID")+"</CustomerId>");
					sb.append("<RelationFlag>"+personRs.getString("RELATION_FLG")+"</RelationFlag>");
					sb.append("<CommunicationAddress>");
					sb.append("<Address>"+personRs.getString("COMMU_ADDR")+"</Address>");
					sb.append("<City>");
					sb.append(personRs.getString("COMMU_CITY")==null?"":personRs.getString("COMMU_CITY"));
					sb.append("</City>");
					sb.append("<StateCode>"+getStateCode(personRs.getString("COMMU_STATE"))+"</StateCode>");
					sb.append("<PinCode>");
					sb.append(personRs.getString("COMMU_PIN")==null?"":personRs.getString("COMMU_PIN"));
					sb.append("</PinCode>");
					sb.append("<CountryCode>"+personRs.getString("COMMU_CNTRY")+"</CountryCode>");
					sb.append("</CommunicationAddress>");
					sb.append("<Phone>");
					sb.append("<Telephone>");
					sb.append(personRs.getString("COMMU_TEL")==null?"":personRs.getString("COMMU_TEL"));
					sb.append("</Telephone>");
					sb.append("<Mobile>"+personRs.getString("COMMU_MOBILE")+"</Mobile>");
					sb.append("<Fax></Fax>");
					sb.append("</Phone>");
					sb.append("<Email>");
					sb.append(personRs.getString("COMMU_EMAIL")==null?"":personRs.getString("COMMU_EMAIL"));
					sb.append("</Email>");
					
					if(personRs.getString("PAN_NO")==null||personRs.getString("PAN_NO").equals("PANNOTREQD")||personRs.getString("PAN_NO").equals("PANINVALID")||personRs.getString("PAN_NO").equals("FORM60")||personRs.getString("PAN_NO").equals("PANNOTAVBL")){
						sb.append("<PAN></PAN>");
					}else{
						sb.append("<PAN>");
						sb.append(personRs.getString("PAN_NO") == null ? "" : personRs.getString("PAN_NO"));
						sb.append("</PAN>");
					}
					//sb.append("<UIN></UIN>");
					
					if(personRs.getString("CUST_TYPE").equals("I")) {
						sb.append("<Individual>");
						sb.append("<Gender>"+personRs.getString("CUST_GENDER")+"</Gender>");
						sb.append("<DateOfBirth>");
						sb.append(personRs.getString("DATE_OF_BIRTH")==null?"":personRs.getString("DATE_OF_BIRTH"));
						sb.append("</DateOfBirth>");
						sb.append("<IdentificationType>"+personRs.getString("TYPE_OF_ID")+"</IdentificationType>");
						sb.append("<IdentificationNumber>"+personRs.getString("ID_NO")+"</IdentificationNumber>");
						//sb.append("<IssuingAuthority></IssuingAuthority>");
						//sb.append("<PlaceOfIssue></PlaceOfIssue>");
						sb.append("<Nationality>"+personRs.getString("NATIONALITY")+"</Nationality>");
						//sb.append("<PlaceOfWork></PlaceOfWork>");
						//sb.append("<FatherOrSpouse></FatherOrSpouse>");
						sb.append("<Occupation>"+personRs.getString("OCCUPATION")+"</Occupation>");
						sb.append("</Individual>");
					}else {
						sb.append("<LegalPerson>");
						sb.append("<ConstitutionType>"+personRs.getString("CONST_TYPE")+"</ConstitutionType>");
						sb.append("<RegistrationNumber></RegistrationNumber>");
						sb.append("<DateOfIncorporation>"+personRs.getString("DATE_OF_ICORP")+"</DateOfIncorporation>");
						sb.append("<CountryCode>"+personRs.getString("COMMU_CNTRY")+"</CountryCode>");
						sb.append("<NatureOfBusiness>"+personRs.getString("NATURE_OF_BIZZ")+"</NatureOfBusiness>");
						sb.append("</LegalPerson>");
					}
					sb.append("</PersonDetails>");
					
				}
				
				
				//Transaction
				txnRs=txnStmt.executeQuery("select TRAN_ID,TRAN_DATE,TRAN_TYPE,CR_DB_FLG,AMOUNT,TRAN_CRNCY from REG_TRANSACTION with(nolock) where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"'");
				
				while(txnRs.next()) {
					sb.append("<Transaction>");
					sb.append("<DateOfTransaction>"+txnRs.getString("TRAN_DATE")+"</DateOfTransaction>");
					sb.append("<TransactionID>"+txnRs.getString("TRAN_ID")+"</TransactionID>");
					sb.append("<TransactionMode>"+txnRs.getString("TRAN_TYPE")+"</TransactionMode>");
					sb.append("<DebitCredit>"+txnRs.getString("CR_DB_FLG")+"</DebitCredit>");
					sb.append("<Amount>"+txnRs.getString("AMOUNT")+"</Amount>");
					sb.append("<Currency>"+txnRs.getString("TRAN_CRNCY")+"</Currency>");	
					sb.append("</Transaction>");
				}
				sb.append("</Account>\n");
				
				acProcess.executeUpdate("update REG_BIZZDTLS set PROCESS_FLG='Y' where SEQ_NO='"+seqNo+"' and REQ_ID='"+requestId+"' and BIZZ_NO='"+bizzNum+"' ");
				
			}
			
			sb.append("</Report>\n");
			sb.append("</Batch>");
			finalXml=sb.toString().replace("&", " &amp; ");
			String strxml= new String(finalXml.getBytes("iso-8859-1"),"UTF-8");
			writeXml.write(strxml.getBytes());
			writeXml.close();
			
			updateRegRequest.executeUpdate("update REG_REQUEST set  REQ_STATUS='X' ,FILE_NAME='"+filename+"' where REQ_ID='"+requestId+"'");
			
			connAml.commit();
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(poStmt!=null) {
					poStmt.close();
					poStmt=null;
				}
				if(reqStmt!=null) {
					reqStmt.close();
					reqStmt=null;
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
				if(updateRegRequest!=null) {
					updateRegRequest.close();
					updateRegRequest=null;
				}
				if(poRs!=null) {
					poRs.close();
					poRs=null;
				}
				if(reqRs!=null) {
					reqRs.close();
					reqRs=null;
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
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

	public static void main(String args[]) {
		makeConnectionAml();
		Thread threadObj =new Thread(new GenerateSTRFile());
		threadObj.start();
	
	}

	@Override
	public void run() {
		//System.out.println("here");
		ArrayList<String> reqidList =null;	
		while(true) {
			reqidList=getStrListForGeneration();
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
					System.out.println("Generating STR Xml file for :: "+reqData[0]);
					
					extractSTRDetails(reqData[0],reqData[1],reqData[2],amlProp);
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
			Thread threadObj= new Thread(new GenerateSTRFile());
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
			strProcess.start();
		}else {
			strProcess.stop();
		}
	}
}
