package com.idbi.intech.aml.ccrxmlprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.net.ftp.FTPClient;

import com.idbi.intech.aml.bg_process.AMLConstants;
import com.idbi.intech.aml.util.InfoLogger;
import com.idbi.intech.aml.util.InfoLoggerDebug;
import com.idbi.intech.aml.util.InfoLoggerExceptions;
import com.idbi.intech.aml.util.InfoLoggerNormal;
import com.idbi.intech.aml.util.InfoLoggerTrace;
import com.idbi.intech.aml.util.InfoLoggerVerbose;
import com.idbi.intech.aml.util.SendEmail;
import com.idbi.intech.aml.util.User;


public class CCRXMLProcess {
	private static Connection connection = null;
	static String logFileName = InfoLogger.generateLogFileName(CCRXMLProcess.class.getSimpleName(), "AMLBGProcess");
	static User user = new User();
	static int logLevel = 0;
	static InfoLogger log = null;	
	static ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
	static String mailText = null;
	
	public static void main(String args[]) throws SQLException{
		
		logLevel = user.getLogLevel();

		if (logLevel == 0) {
			user.setInfoLogger(new InfoLoggerExceptions());
		} else if (logLevel == 1) {
			user.setInfoLogger(new InfoLoggerNormal());
		} else if (logLevel == 2) {
			user.setInfoLogger(new InfoLoggerTrace());
		} else if (logLevel == 3) {
			user.setInfoLogger(new InfoLoggerDebug());
		} else if (logLevel == 4) {
			user.setInfoLogger(new InfoLoggerVerbose());
		} else {
			user.setInfoLogger(new InfoLoggerVerbose());
		}

		log = user.getInfoLogger();
		log.setLogFileName(logFileName);
		log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "main", "Exception(0) Text");
		log.logNormalText(CCRXMLProcess.class.getSimpleName(), "main", "Normal(1) Text");
		log.logTraceText(CCRXMLProcess.class.getSimpleName(), "main", "Trace(2) Text");
		log.logDebugText(CCRXMLProcess.class.getSimpleName(), "main", "Debug(3) Text");
		log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "main", "Verbose(4) Text");
		log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "main", "Start");
		
		processCCRXML();
		//SendCCRFileStatusEmail("Demo mail for CCR.");
		
		log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "Main", "END");
		/*SendCCRFileStatusEmail("CCR file named 2158.xml for the month of January is generated. Please Check.");*/
	}

	private static void processCCRXML(){
		
		ResourceBundle rb = ResourceBundle.getBundle("AMLProp");		
		/**
		 * FTP interface
		 */
		
		ResourceBundle ftpConnectionProperties = 
				ResourceBundle.getBundle("com.idbi.intech.aml.live.connect.IConnect_APP");
		
		String serverIP = ftpConnectionProperties.getString("CCR_FTP_SERVER_IP");
		String userId = ftpConnectionProperties.getString("CCR_FTP_USER") ;			
		String password = ftpConnectionProperties.getString("CCR_FTP_PWD");
		String directoryLocation = ftpConnectionProperties.getString("CCR_FTP_SOURCE");		
		String serverLocationToTransferFilesFromFTP = 
			rb.getString("CCRXML_BACKUP");
		String destinationDirectory = rb.getString("CCRXML");
		
		FTPClient ftpClient = FTPConnectionManage.getFTPConnection(
				serverIP,userId,password,directoryLocation, log);
		
		FTPConnectionManage.transferFTPFilesToServerLocation(ftpClient, serverLocationToTransferFilesFromFTP, log);
		FTPConnectionManage.disconnectFTP(ftpClient, log);
		ArrayList<String> xmlFilesT = getFilesFromDirectories(serverLocationToTransferFilesFromFTP);
		
		/**
		 * FTP interface ends
		 */
		
		//get xml files from directory
		//String ccrXMLDir = rb.getString("CCR_XML_DIR");
		
		/**
		 * get files from FTP location
		 */
		
		//get all xml files from local folder
		//ArrayList<String> xmlFiles = getFilesFromDirectories(ccrXMLDir);		
		
		
		//set initial information header
		ArrayList<String> fileWiseDistinctHeader = fileWiseDistXmlHeader();
		//set repetitive information header
		ArrayList<String> reportHeader = setXMLFileHeader();
		//set Transaction Header
		//ArrayList<String> transactionHeader = setTransactionHeader();
		
		
		try{
			
			/*String destinationDirectory = rb.getString("CCRXML");			
			//process files
			processFiles(xmlFiles,reportHeader,fileWiseDistinctHeader,
					destinationDirectory);*/		
		
			//FTP files processing
			processFiles(xmlFilesT,reportHeader,fileWiseDistinctHeader,
					destinationDirectory);	
		
		}finally{
			try {				
				if(connection != null){					
					connection.close();			
				}
			} catch (SQLException e) {				
				log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processCCRXML", e.getMessage());
			}
		}	
	} 
	
	/*private static ArrayList<String> setTransactionHeader() {
		ArrayList<String> reportWiseTransactionHeader = new ArrayList<String>(3);
		reportWiseTransactionHeader.add("<Denomination>");
		reportWiseTransactionHeader.add("<CurrencySerialNum>");
		reportWiseTransactionHeader.add("<CurrencyRemarks>");
		return reportWiseTransactionHeader; 
	}*/

	//set repetitive information header 
	private static ArrayList<String> fileWiseDistXmlHeader() {
		
		ArrayList<String> fileWiseDistinctHeader = new ArrayList<String>(29);
		fileWiseDistinctHeader.add("<ReportType>");
		fileWiseDistinctHeader.add("<ReportFormatType>");
		fileWiseDistinctHeader.add("<DataStructureVersion>");
		fileWiseDistinctHeader.add("<GenerationUtilityVersion>");
		fileWiseDistinctHeader.add("<DataSource>");
		fileWiseDistinctHeader.add("<ReportingEntityName>");
		fileWiseDistinctHeader.add("<ReportingEntityCategory>");		
		fileWiseDistinctHeader.add("<RERegistrationNumber>");
		fileWiseDistinctHeader.add("<FIUREID>");
		fileWiseDistinctHeader.add("<POName>");
		fileWiseDistinctHeader.add("<PODesignation>");
		fileWiseDistinctHeader.add("<Address>");
		fileWiseDistinctHeader.add("<City>");
		fileWiseDistinctHeader.add("<StateCode>");
		fileWiseDistinctHeader.add("<PinCode>");		
		fileWiseDistinctHeader.add("<CountryCode>");
		fileWiseDistinctHeader.add("<Telephone>");
		fileWiseDistinctHeader.add("<Mobile>");
		fileWiseDistinctHeader.add("<Fax>");
		fileWiseDistinctHeader.add("<POEmail>");
		fileWiseDistinctHeader.add("<BatchNumber>");
		fileWiseDistinctHeader.add("<BatchDate>");
		fileWiseDistinctHeader.add("<MonthOfReport>");		
		fileWiseDistinctHeader.add("<YearOfReport>");
		fileWiseDistinctHeader.add("<OperationalMode>");
		fileWiseDistinctHeader.add("<BatchType>");
		fileWiseDistinctHeader.add("<OriginalBatchID>");
		fileWiseDistinctHeader.add("<ReasonOfRevision>");
		fileWiseDistinctHeader.add("<PKICertificateNum>");		
		return fileWiseDistinctHeader;
	}

	//set initial information header
	private static ArrayList<String> setXMLFileHeader() {
		
		ArrayList<String> reportHeader = new ArrayList<String>(33);		
		reportHeader.add("<ReportSerialNum>");
		reportHeader.add("<OriginalReportSerialNum>");
		reportHeader.add("<BranchRefNumType>");
		reportHeader.add("<BranchRefNum>");
		reportHeader.add("<BranchName>");
		reportHeader.add("<Address>");
		reportHeader.add("<City>");
		reportHeader.add("<StateCode>");
		reportHeader.add("<PinCode>");
		reportHeader.add("<CountryCode>");
		reportHeader.add("<Telephone>");
		reportHeader.add("<Mobile>");
		reportHeader.add("<Fax>");
		reportHeader.add("<BranchEmail>");
		reportHeader.add("<INR1000NoteCount>");
		reportHeader.add("<INR500NoteCount>");		
		reportHeader.add("<INR100NoteCount>");
		reportHeader.add("<INR50NoteCount>");
		reportHeader.add("<INR20NoteCount>");
		reportHeader.add("<INR10NoteCount>");
		reportHeader.add("<INR5NoteCount>");
		reportHeader.add("<FICNValue>");
		reportHeader.add("<DateOfTendering>");
		reportHeader.add("<CashTendered>");
		reportHeader.add("<DateOfDetection>");
		reportHeader.add("<DetectedAt>");
		reportHeader.add("<PoliceInformed>");		
		reportHeader.add("<PoliceReportDetail>");
		reportHeader.add("<TenderingPerson>");
		reportHeader.add("<AccountHolder>");
		reportHeader.add("<AccountNumber>");
		reportHeader.add("<PriorityRating>");		
		reportHeader.add("<IncidentRemarks>");
		//reportHeader.add("<Denomination>");
		//reportHeader.add("<CurrencySerialNum>");
		//reportHeader.add("<CurrencyRemarks>");			
		return reportHeader;
	}

	//get data from xml file and set it in arrays 
	private static void processFiles(ArrayList<String> xmlFiles, 
						ArrayList<String> reportHeader, 
						ArrayList<String> fileWiseDistinctDetails,
						String destinationDirectory) {
		
		try {			
			//connection = DAOFactory.makeConnectionAMLLive();
			connection = makeDbConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles", "connection error"+ e.getMessage());
			SendCCRFileStatusEmail("Database is not available.");
			return;
		}
		
		//Using XMLEventReader Iterator		
		XMLInputFactory factory = XMLInputFactory.newInstance();		
		XMLEventReader r;
		XMLEvent event;		 
		XMLEvent eventNext;		
		
		//file wise insertion		
		//System.out.println("Total number of files need to be processed "+xmlFiles.size());
		log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles", "Total number of files need to be processed "+xmlFiles.size());
		
		for(String xmlFile : xmlFiles){
			//log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles", "File "+xmlFile+" processing has been started.");
			int i = 0;
			int j = 0;
			boolean headerNotFilledFlag = true;
			ArrayList<String> capturedData = new ArrayList<String>();
			ArrayList<String> fileWiseCommonData = new ArrayList<String>(fileWiseDistinctDetails.size());
			List<ArrayList<String>> capturedReportDataFromXML = new ArrayList<ArrayList<String>>();
			//log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles", "File: "+xmlFile+" processing starts.");
			
			try {
				r = factory.createXMLEventReader(xmlFile,new FileInputStream(xmlFile));
				
				while(r.hasNext()) {				
					
					event = r.nextEvent();				
					
					if(headerNotFilledFlag){
						
						while(!event.toString().equalsIgnoreCase("</PKICertificateNum>")){
							
							if(event.toString().equalsIgnoreCase(fileWiseDistinctDetails.get(j))){
								eventNext = r.nextEvent();								
								if(eventNext.toString().startsWith("</")){
									fileWiseCommonData.add("");							
								}else{
									fileWiseCommonData.add(eventNext.toString());
									j++;
								}								
								
							}
							event = r.nextEvent();
						}
						headerNotFilledFlag = false;
					}
					
					
					if(event.toString().equalsIgnoreCase(reportHeader.get(i))){
						
						eventNext = r.nextEvent();
						
						if(eventNext.toString().startsWith("</")){
							capturedData.add("NA");							
						}else{
							capturedData.add(eventNext.toString());
							i++;
						}												
						
					}
					
					if(event.toString().equalsIgnoreCase("</ReportSummary>")){												
						capturedReportDataFromXML.add(capturedData);						
						capturedData = new ArrayList<String>();
						i=0;
					}
										
				}
					
			} catch (Exception e) {
				//System.out.println("File "+xmlFile+" parse error. Further processing is aborted.");
				log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles", 
						"File "+xmlFile+" parse error. Further processing is aborted." + e.getMessage());
				mailText = "File "+xmlFile+" parse error. Further processing of file has been aborted.";
				SendCCRFileStatusEmail(mailText);
				continue;
			}			
			
			try{
				//log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles", "File: "+xmlFile+" processing ends.");
				Long ccrId = insertDataInDataBase(xmlFile,fileWiseCommonData,capturedReportDataFromXML);
				//System.out.println("File "+xmlFile+" insertion successful.");
				log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles",
						"File "+xmlFile+" insertion successful.");				
				
				copyFile(
						new File(xmlFile), 
						new File(destinationDirectory + ccrId.toString()));
				
				connection.commit();
								
				
				mailText = "CCR file has been processed. Please check and verify it.";
										
				
			}catch(Exception e){
				
				mailText = "CCR file processing error. Please contact your system administrator.";
				
				
				log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles",
						"File "+xmlFile+" insertion failed."+ e.getMessage());
				try {
					connection.rollback();
					
				} catch (SQLException e1) {					
					log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "processFiles",
							"Connection rollback error.");
				}
			}
			
			SendCCRFileStatusEmail(mailText);
		}
	}
	
	private static void SendCCRFileStatusEmail(String mailText) {
		
		ArrayList<String> toList = new ArrayList<String>();	
		ArrayList<String> ccList = new ArrayList<String>();	
		ArrayList<String> bccList = new ArrayList<String>();	
		String smtpServer = null;
		String userName = null;
		String password = null;		
		toList.add(rb.getString("CCR_EMAIL_TO"));						
		ccList.add(rb.getString("CCR_EMAIL_CC"));				
		smtpServer = rb.getString("SMTPSERVER");
		userName = rb.getString("EMAILID");
		password = rb.getString("PASSWORD");
		mailText += "<br/>This is an auto generated mail from i_AML system.";
		
		try {
			SendEmail.sendEmailNew(toList, ccList, bccList, "CCR file processing update.",
					mailText, smtpServer,userName, password);
		} catch (Exception e) {			
			log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "SendCCRFileStatusEmail", "Email sending error.");
		}
		
	}

	//inserting file wise data in xml file
	private static Long insertDataInDataBase(String fileName,ArrayList<String> capturedHeaderDataFromXML,
		List<ArrayList<String>> capturedReportDataFromXML) throws Exception {		
		
		log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "insertDataInDataBase",
				"File "+fileName+" insertion starts.");
		
		//System.out.println("File "+fileName+" insertion starts.");
		
		Long ccrId = getNextSeqValue(AMLConstants.CCRFILESEQ);
		
		if( ccrId > 0 ){
			insertHeaderData(ccrId,capturedHeaderDataFromXML);
			insertBranchData(ccrId,capturedReportDataFromXML);
		}
		
		//insert report data in detail table
		if(!insertReportData(ccrId,capturedReportDataFromXML)){
			//System.out.println("File "+fileName+" insertion error.");
			
			log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "insertDataInDataBase",
					"File "+fileName+" insertion error.");
		}
		
		return ccrId;
	}
	
	private static void insertBranchData(Long ccrId,
			List<ArrayList<String>> capturedReportDataFromXML) throws SQLException {
		
		StringBuilder sb = new StringBuilder("");		
		PreparedStatement psBranch = null;
		PreparedStatement psCtrlFileMap = null;		
		int j = 1;				
		
		String insertControlMap = "INSERT INTO P_AML_CCR_CONTROLMAP " +
				"(LAST_CCR_DATE, LAST_CCR_STATUS, LAST_CCR_FROM_DATE, " +
				"LAST_CCR_TO_DATE, LAST_CCR_ID, CCR_GENERATED_BY) " +
				"VALUES (sysdate, ?, sysdate, sysdate, ?, ?)";		
		
		try{	
			psCtrlFileMap = connection.prepareStatement(insertControlMap);
			psCtrlFileMap.setString(1, AMLConstants.ACTIVE);
			psCtrlFileMap.setLong(2, ccrId);
			psCtrlFileMap.setString(3, "SYSUSR");
			psCtrlFileMap.execute();
		
		
		for(int i = 1 ; i < 16 ; i++){
			sb.append("?,");
		}		
		sb.deleteCharAt(sb.length() - 1);
		
		String insertBranchStrSql = "insert into P_AML_CCR_BRANCH_FILE " +
				"( RECORD_TYPE, LINE_NUMBER,NAME_OF_BRANCH, BRANCH_REF_NO, UID_FIU," +
				"BRANCH_ADD1,BRANCH_ADD2, BRANCH_ADD3, BRANCH_ADD4, BRANCH_ADD5," +
				"BRANCH_PIN, BRANCH_TELEPHONE, BRANCH_FAX,BRANCH_EMAIL,CCR_ID) " +
				"values (" + sb.toString()+") ";		
		
						
			psBranch = connection.prepareStatement(insertBranchStrSql);
			
			for(ArrayList<String> branch: capturedReportDataFromXML){		
				
				psBranch.setString(1, "BRC");
				psBranch.setInt(2, j++);
				psBranch.setString(3, branch.get(4));
				psBranch.setString(4, branch.get(3));
				psBranch.setString(5, "XXXXXXXXXX");
				psBranch.setString(6, branch.get(5));
				psBranch.setString(7, null);
				psBranch.setString(8, null);
				psBranch.setString(9, branch.get(6));
				psBranch.setString(10, branch.get(7));
				psBranch.setLong(11, Long.parseLong(branch.get(8)));
				psBranch.setString(12, branch.get(10));
				psBranch.setString(13, branch.get(12));
				psBranch.setString(14, branch.get(13));				
				psBranch.setLong(15,ccrId);				
				psBranch.executeUpdate();
				//connection.commit();
			}
			
		}catch(Exception e){
			//e.printStackTrace();
			log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "insertBranchData",
					"Insertion error.");
			connection.rollback();
		}finally{
			try{
				if(psCtrlFileMap != null){
					psCtrlFileMap.close();
				}
				if(psBranch != null){
					psBranch.close();
				}
			}catch(SQLException e){
				log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "insertBranchData",
				"Insertion error.");
			}
		}		
	}

	private static boolean insertReportData(
			Long ccrId, List<ArrayList<String>> capturedReportDataFromXML)
			throws Exception
	{		
		
		//System.out.println("Report data insertion starts.");
		PreparedStatement ps = null;
		PreparedStatement updatePs = null;
		StringBuilder sb = new StringBuilder("");
		int j = 1;	
		Long grandTotal = 0l;
		boolean cntryFlag = false;
		boolean refFlag = false;
		boolean skipOrigiSerialFlag = false;
		boolean skipFicnVal = false;
		boolean skipPoliceRepDtl = false;
		long numberOfCounterFeit = 0l;
		long totalValueOfCounterFeit = 0l;
		boolean result = false;
		
		for(int i = 1 ; i < 37 ; i++){		
			if (i == 26 || i == 28 || i == 35) {
				sb.append("to_date(?,'yyyy-MM-dd'),");
			} else {
				sb.append("?,");
			}
		}		
		sb.deleteCharAt(sb.length() - 1);
		
		
		
		/*String strAMLCCRDetails = "insert into P_AML_CCR_DETAILS (DICBN_NUMBER,SOL_ID,DICBN_AUTHORITY_NAME, "
			+ "DICBN_DATE, DICBN_MONTH,DICBN_YEAR,DICBN_CCR_DT,STATE,SYATEM_DATE,CCR_BRANCH,"
			+ "CCR_REF_NUM,CCR_FID_IND,CCR_ADDRS,CCR_ROAD,CCR_LOCALITY,CCR_CITY,CCR_STATE,CCR_TEL,"
			+ "CCR_FAX,CCR_EMAIL,CCR_CSH_DEP,CCR_DT_TENDERING,CCR_DT_DETECTION,CCR_DETECTED_AT1,"
			+ "CCR_DETECTED_AT2,CCR_DETECTED_AT3,CCR_DETECTED_AT4,CCR_DETECTED_AT5,CCR_POLICE_INFO,"
			+ "CCR_DET_FIR,CCR_ADD_INFO,CCR_NM_TEN_PER,CCR_NM_ACC_HOLDER,CCR_NM_ACC_NO,CCR_NM,"
			+ "CCR_DESIGNATION,DICBN_RSFIVE,DICBN_TEN,DICBN_RSTWENTY,DICBN_RSFIFTY,DICBN_RSHUNDRED,"
			+ "DICBN_RSFIVEHUNDRED,DICBN_THAUSAND,DICBN_RSFIVE_AMT,DICBN_RSTEN_AMT,DICBN_RSTWENTY_AMT,"
			+ "DICBN_RSFIFTY_AMT,DICBN_RSHUNDRED_AMT,DICBN_RSFIVEHUNDRED_AMT,DICBN_RSTHAUSAND_AMT,"
			+ "DICBN_GTOTAL,PIN_CODE) values (" + sb.toString() + ")";*/	
		
		String strAMLCCRDetails = "insert into P_AML_CCR_DETAILS(DICBN_NUMBER," +
			"SOL_ID,CCR_BRANCH,CCR_ADDRS,CCR_CITY,CCR_STATE,PIN_CODE," +
			"CCR_TEL,BRNCH_MOB,CCR_FAX,CCR_EMAIL,DICBN_THAUSAND,DICBN_RSTHAUSAND_AMT," +
			"DICBN_RSFIVEHUNDRED,DICBN_RSFIVEHUNDRED_AMT,DICBN_RSHUNDRED,DICBN_RSHUNDRED_AMT," +
			"DICBN_RSFIFTY,DICBN_RSFIFTY_AMT," +
			"DICBN_RSTWENTY,DICBN_RSTWENTY_AMT," +
			"DICBN_TEN,DICBN_RSTEN_AMT," +
			"DICBN_RSFIVE,DICBN_RSFIVE_AMT," +
			"CCR_DT_TENDERING," +
			"CCR_CSH_DEP,CCR_DT_DETECTION,CCR_DETECTED_AT1,CCR_POLICE_INFO," +
			"CCR_NM_TEN_PER,CCR_NM_ACC_HOLDER,CCR_NM_ACC_NO," +
			"DICBN_GTOTAL,SYATEM_DATE,CCR_ID)" +
			" values (" + sb.toString() + ")";		
		
		try {
			ps = connection.prepareStatement(strAMLCCRDetails);		
			
			for(ArrayList<String> reportData : capturedReportDataFromXML){				
				j = 1;
				cntryFlag = false;
				refFlag = false;
				skipOrigiSerialFlag = false;
				skipFicnVal = false;
				skipPoliceRepDtl = false;
				grandTotal = 0l;
				numberOfCounterFeit = numberOfCounterFeit + 1;
				for(String rowData : reportData){
					if(rowData == null){
						break;
					}else{
						if(j == 1){							
							ps.setLong(j++, Long.parseLong(rowData));						
						}else if(j==7){							
							ps.setLong(j++, Long.parseLong(rowData));							
						}else if(!skipOrigiSerialFlag && j == 2){
							skipOrigiSerialFlag = true;							
							continue;
						}else if(!refFlag && j == 2){
							refFlag = true;							
							continue;
						}else if(!cntryFlag && j == 8){
							cntryFlag = true;							
							continue;
						}else if(j == 12){
							grandTotal += ( Long.parseLong(rowData) * 1000l);
							ps.setLong(j++, Long.parseLong(rowData));							
							ps.setLong(j++, Long.parseLong(rowData) * 1000l);							
						}else if(j == 14){
							grandTotal += ( Long.parseLong(rowData) * 500l);
							ps.setLong(j++, Long.parseLong(rowData));
							ps.setLong(j++, Long.parseLong(rowData) * 500l);
						}else if(j == 16){
							grandTotal += ( Long.parseLong(rowData) * 100l);
							ps.setLong(j++, Long.parseLong(rowData));							
							ps.setLong(j++, Long.parseLong(rowData) * 100l);							
						}else if(j == 18){
							grandTotal += ( Long.parseLong(rowData) * 50l);
							ps.setLong(j++, Long.parseLong(rowData));
							ps.setLong(j++, Long.parseLong(rowData) * 50l);
						}else if(j == 20){
							grandTotal += ( Long.parseLong(rowData) * 20l);
							ps.setLong(j++, Long.parseLong(rowData));
							ps.setLong(j++, Long.parseLong(rowData) * 20l);
						}else if(j == 22){
							grandTotal += ( Long.parseLong(rowData) * 10l);
							ps.setLong(j++, Long.parseLong(rowData));
							ps.setLong(j++, Long.parseLong(rowData) * 10l);
						}else if (j == 24){
							grandTotal += ( Long.parseLong(rowData) * 5l);
							ps.setLong(j++, Long.parseLong(rowData));
							ps.setLong(j++, Long.parseLong(rowData) * 5l);							
						}else if(!skipFicnVal && j == 26){
							skipFicnVal = true;							
							continue;
						}else if(j == 27){							
							ps.setLong(j++, Long.parseLong(rowData));							
						}else if(!skipPoliceRepDtl && j == 31){
							skipPoliceRepDtl = true;							
							continue;
						}else if(j == 34){
							break;
						}else{					
							ps.setString(j++, rowData);							
						}
					}
					
				}
				ps.setLong(34,grandTotal);
				ps.setString(35,new SimpleDateFormat("yyyy-MM-dd").format(java.util.Calendar.getInstance().getTime()));
				ps.setLong(36,ccrId);
				ps.executeUpdate();
				//connection.commit();
				totalValueOfCounterFeit = totalValueOfCounterFeit + grandTotal;
			}			
			
			//insert into control file
			updatePs = connection.prepareStatement("update P_AML_CCR_CONTROL_FILE set " +
					"TOTAL_VALUE_OF_COUNTERFEIT=?," +
					"NUMBER_OF_COUNTERFIT=? " +
					"where " +
					"SERIAL_NUMBER_OF_REPORT = ?");
			updatePs.setLong(1, totalValueOfCounterFeit);
			updatePs.setLong(2, numberOfCounterFeit);
			updatePs.setLong(3, ccrId);
			updatePs.executeUpdate();
			//connection.commit();
				
		} finally{
			try{
				if(ps != null){
					ps.close();
				}
				if(updatePs != null){
					updatePs.close();
				}
			}catch(SQLException e){
				log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "insertReportData",
				"update error.");
			}
		}
		
		result = true;
		return result;
	}

	private static void insertHeaderData(long ccrId,ArrayList<String> capturedHeaderDataFromXML) throws SQLException {
				
		PreparedStatement headerDataPs = null;
		StringBuilder sb = new StringBuilder("");			
		
		for(int i = 1 ; i < 27 ; i++){
			if(i == 4){
				sb.append("to_date(?,'dd-mm-yyyy'),");
			}else{
				sb.append("?,");
			}
		}
		
		sb.deleteCharAt(sb.length()-1);	
		
		String strAMLCCCRHeaderMaster = "insert into P_AML_CCR_CONTROL_FILE (SERIAL_NUMBER_OF_REPORT,REPORT_NAME," +
				"RECORD_TYPE, REPORT_DATE,UNIQ_CODE_REPORTING_ENTITY,REPORTING_ENTITY_NAME, REPORTING_ENTITY_CATEGORY," +
				"UID_FIU, PO_NAME, PO_DESIGNATION, PO_ADD1, PO_ADD2,PO_ADD3, PO_ADD4, PO_ADD5, PO_PINCODE," +
				"PO_TELEPHONE, PO_FAX, PO_EMAIL, REPORT_TYPE, REASON_FOR_REPLACEMENT, SERIAL_NUMBER, "+
				"OPERATION_MODE, DATASTRUCTURE_VERSION, NUMBER_OF_COUNTERFIT, TOTAL_VALUE_OF_COUNTERFEIT ) " +
				"values ("+sb.toString()+")";
		
		try{
			
			headerDataPs = connection.prepareStatement(strAMLCCCRHeaderMaster);
			headerDataPs.setLong(1, ccrId);			
			headerDataPs.setString(2, "CCR");
			headerDataPs.setString(3, "CTL");
			headerDataPs.setString(4, new SimpleDateFormat("dd/MM/yyyy").format(java.util.Calendar.getInstance().getTime()) );
			headerDataPs.setString(5, rb.getString("BSRCode"));
			headerDataPs.setString(6, capturedHeaderDataFromXML.get(5));			
			headerDataPs.setString(7, capturedHeaderDataFromXML.get(6));
			headerDataPs.setString(8, capturedHeaderDataFromXML.get(8));
			headerDataPs.setString(9, capturedHeaderDataFromXML.get(9));
			headerDataPs.setString(10, capturedHeaderDataFromXML.get(10));
			headerDataPs.setString(11, capturedHeaderDataFromXML.get(11));			
			headerDataPs.setString(12, null);
			headerDataPs.setString(13, null);			
			headerDataPs.setString(14, capturedHeaderDataFromXML.get(12));
			headerDataPs.setString(15, capturedHeaderDataFromXML.get(13));
			/*try{
				headerDataPs.setLong(16,Long.parseLong(capturedHeaderDataFromXML.get(14)));
			}catch(Exception e){
				headerDataPs.setLong(16, new Long(0));
			}*/
			headerDataPs.setString(16, rb.getString("Pin"));			
			headerDataPs.setString(17, capturedHeaderDataFromXML.get(16));
			headerDataPs.setString(18, capturedHeaderDataFromXML.get(18));
			headerDataPs.setString(19, capturedHeaderDataFromXML.get(19));
			
			headerDataPs.setString(20, capturedHeaderDataFromXML.get(0));
			headerDataPs.setString(21, capturedHeaderDataFromXML.get(27));
			headerDataPs.setLong(22, new Long(0));
			headerDataPs.setString(23, capturedHeaderDataFromXML.get(24));
			headerDataPs.setString(24, capturedHeaderDataFromXML.get(2));
			
			//calculate it at last
			headerDataPs.setLong(25, new Long(0));
			headerDataPs.setLong(26, new Long(0));
			
			
			headerDataPs.executeUpdate();
			//connection.commit();
			
		}catch(Exception e){
			log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "insertReportData",
			"insert error.");
			connection.rollback();
		}finally{
			try {
				if(headerDataPs != null){
					headerDataPs.close();
				}
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}	
	}
	
	//get next sequence value based on sequence name
	private static Long getNextSeqValue(String seqName) {
		Long seqNextVal = new Long(0);		
		Statement stmt = null;
		ResultSet rs = null;
		String nextSequenceSql = "select "+seqName+".NEXTVAL from DUAL";
		
		try{
			stmt = connection.prepareStatement(nextSequenceSql);
			rs = stmt.executeQuery(nextSequenceSql);			
			if (rs != null) {
				rs.next();
				seqNextVal = Long.parseLong(rs.getString(1));
				rs.close();
			}			
			if(stmt != null){
				stmt.close();
			}			
		}catch(SQLException e){
			seqNextVal = new Long(0);
			log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "getNextSeqValue",
			"sequence error.");
		}
		return seqNextVal;
	}

	//get xml files from directory
	private static ArrayList<String> getFilesFromDirectories(String directory) {
		
		ArrayList<String> xmlFiles = new ArrayList<String>();
		File dir = new File(directory);
		if (dir.listFiles() != null) {
			for (File file : dir.listFiles()) {
				if (file.getName().endsWith((".xml"))) {
					xmlFiles.add(directory+file.getName());
				}
			}
		}
		return xmlFiles;
	}
	
	//copies all the files in the directory from source to destinaton
	public static void copyDirectory(File sourceDir, File destDir) throws IOException {
		//log.logVerboseText("GenerateCCRFiles", "copyDirectory", "Start");
		File[] children = sourceDir.listFiles();

		if (!destDir.exists()) {
			destDir.mkdir();
		}

		for (File sourceChild : children) {
			// System.out.println(sourceChild.getName());
			String name = sourceChild.getName();

			if (name.lastIndexOf(".") == -1) {
				continue;
			}
			String fileType = name.substring(name.lastIndexOf(".") + 1, name.length());

			if (fileType.equalsIgnoreCase("txt")) {
				String str[] = name.split("\\.");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
				String dateString = formatter.format(new Date());
				File destChild = new File(destDir, str[0] + dateString + "." + fileType);

				if (sourceChild.isDirectory()) {

					copyDirectory(sourceChild, destChild);
				} else {
					copyFile(sourceChild, destChild);
				}
			}
		}
		//log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "copyDirectory", "End");
	}
	
	// copy file from source to destination
	private static void copyFile(File source, File dest) throws IOException {
		//log.logVerboseText("GenerateCCRFiles", "copyFile", "Start");
		//log.logVerboseText("GenerateCCRFiles", "copyFile", "Source :" + source);
		//log.logVerboseText("GenerateCCRFiles", "copyFile", "Destination :" + dest);
		if (!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			//log.logVerboseText(CCRXMLProcess.class.getSimpleName(), "copyFile", "End");
		}finally {
			in.close();
			out.close();
		}
	}
	
	public static Connection makeDbConnection() throws SQLException {
		Connection connectionAML = null;
		//UAT/DEV
		//String strConnection = "jdbc:oracle:thin:@10.144.136.92:1521:oraaml";
		//production
		String strConnection = "jdbc:oracle:thin:@10.144.18.161:1521:oraaml";
		String username = "amlidbi";
		String password = "amlidbi";
		
		try {
			// Make connection			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//connectionAML = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);			
			connectionAML = DriverManager.getConnection(strConnection,username,password);

		} catch (Exception e) {
			log.logExceptionText(CCRXMLProcess.class.getSimpleName(), "makeDbConnection",
			"connection error.");	
		}
		return connectionAML;
	}
}
