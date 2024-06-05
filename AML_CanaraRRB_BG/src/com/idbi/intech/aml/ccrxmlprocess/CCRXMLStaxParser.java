package com.idbi.intech.aml.ccrxmlprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.idbi.intech.aml.bg_process.AMLConstants;

public class CCRXMLStaxParser {	
	
	private static Connection connection = null;
	private static Statement stmt = null;
	private static PreparedStatement headerPstmt = null;
	private static PreparedStatement txnPstmt = null;
	private static PreparedStatement repDtlPstmt = null;
	
	//process files present in source folder
	public static void processXMLFiles(List<String> xmlFiles){				
		
		List<CCRReport> ccrReports = null;
		CCRReport ccrReport = null;
		CCRReportHeader ccrReportHeader = null;		
		XMLEvent xmlEvent = null;
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		InputStream inputStream = null;
		XMLEventReader xmlEventReader = null;
		boolean iterateTillEndElemnet;		
		//flag to check if <Report> tag has been parsed or not
		boolean reportStart = false;
		//abort file processing if some error in parsing 
		boolean abort = false;
		
		//file wise processing
		for(String xmlFile : xmlFiles){		
			
			ccrReport = new CCRReport();			
			
			System.out.println("File "+xmlFile+" processing starts.");
			
			try{			
				xmlInputFactory = XMLInputFactory.newInstance();
				inputStream = new FileInputStream(xmlFile);
				xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);	
				iterateTillEndElemnet = true;				
				
				while(xmlEventReader.hasNext()){
					
					//process initial tags of an xml file - read it and set 
					//it in CCRReportHeader bean
					if(null == ccrReportHeader){
						ccrReportHeader = processHeaderTags(xmlEventReader,xmlEvent);	
						ccrReports = new ArrayList<CCRReport>();
					}				
					
					//Process Report tags of an xml file - read from it and set it in
					//CCRReport bean
					 
					while(xmlEventReader.hasNext() && iterateTillEndElemnet){
						
						xmlEvent = xmlEventReader.nextEvent();					
						
						if(xmlEvent.toString().equalsIgnoreCase(AMLConstants.REPORT_START_TAG)){						
							reportStart = true;
						}					
						
						if(xmlEvent.toString().equalsIgnoreCase(AMLConstants.REPORT_END_TAG)){							
							ccrReports.add(ccrReport);
							ccrReport = new CCRReport();
							iterateTillEndElemnet = false;
							reportStart = false;
						}else{							
						
							if(reportStart){							
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REPORTSERIALNUM)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setReportSerialNo(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.ORIGINALREPORTSERIALNUM)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setOriginalReportSerialNum(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BRANCHREFNUMTYPE)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setBranchRefNumType(xmlEvent.asCharacters().getData());
									continue;
							    }						
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BRANCHREFNUM)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setBranchRefNum(xmlEvent.asCharacters().getData());
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BRANCHNAME)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setBranchName(xmlEvent.asCharacters().getData());
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.ADDRESS)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setAddress(xmlEvent.asCharacters().getData());
									continue;
							    }						
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.CITY)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setCity(xmlEvent.asCharacters().getData());							 
									}
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.STATECODE)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setStateCode(xmlEvent.asCharacters().getData());
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.PINCODE)) {								
									xmlEvent = xmlEventReader.nextEvent();
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setPinCode(xmlEvent.asCharacters().getData());							 
									}
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.COUNTRYCODE)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setCountryCode(xmlEvent.asCharacters().getData());
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.TELEPHONE)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setTelephone(xmlEvent.asCharacters().getData());
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.MOBILE)) {								
									xmlEvent = xmlEventReader.nextEvent();
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setMobile(xmlEvent.asCharacters().getData());							 
									}
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.FAX)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setFax(xmlEvent.asCharacters().getData());							 
									}
									continue;
							    }
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BRANCHEMAIL)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setBranchEmail(xmlEvent.asCharacters().getData());
									continue;
								}							
							
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR1000NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR1000NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR500NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR500NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR100NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR100NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR50NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR50NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR20NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR20NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR10NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR10NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INR5NOTECOUNT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setiNR5NoteCount(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.FICNVALUE)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									ccrReport.setfICNValue(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.DATEOFTENDERING)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									ccrReport.setDateOfTendering(xmlEvent.asCharacters().getData());
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.CASHTENDERED)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setCashTendered(new BigInteger(xmlEvent.asCharacters().getData()));
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.DATEOFDETECTION)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									ccrReport.setDateOfDetection(xmlEvent.asCharacters().getData());
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.DETECTEDAT)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setDetectedAt(xmlEvent.asCharacters().getData());
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.POLICEINFORMED)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setPoliceInformed(xmlEvent.asCharacters().getData());
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.POLICEREPORTDETAIL)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setPoliceReportDetail(xmlEvent.asCharacters().getData());							 
									}
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.TENDERINGPERSON)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setTenderingPerson(xmlEvent.asCharacters().getData());					 
									}
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.ACCOUNTHOLDER)) {								
									xmlEvent = xmlEventReader.nextEvent();
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setAccountHolder(xmlEvent.asCharacters().getData());					 
									}
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.ACCOUNTNUMBER)) {								
									xmlEvent = xmlEventReader.nextEvent();
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setAccountNumber(xmlEvent.asCharacters().getData());
									}
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.PRIORITYRATING)) {								
									xmlEvent = xmlEventReader.nextEvent();
									ccrReport.setPriorityRating(xmlEvent.asCharacters().getData());
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.INCIDENTREMARKS)) {								
									xmlEvent = xmlEventReader.nextEvent();
									if(xmlEvent.toString().startsWith("</")){
										ccrReport.setIncidentRemarks("NA");
									}else{
										ccrReport.setIncidentRemarks(xmlEvent.asCharacters().getData());
									}
									continue;
								}
								
								if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.TRANSACTIONDETAILS)) {								
									xmlEvent = xmlEventReader.nextEvent();									
									
									if(!xmlEvent.toString().startsWith("</")){								
										if(null == ccrReport.getCcrReportTransactions()){
											ccrReport.setCcrReportTransactions(new ArrayList<CCRReportTransaction>(5));
											ccrReport.getCcrReportTransactions().add(processTransactionDetails(xmlEventReader,xmlEvent));
										}else{
											ccrReport.getCcrReportTransactions().add(processTransactionDetails(xmlEventReader,xmlEvent));
										}
									}									
								}								
							}
						}
					}
					iterateTillEndElemnet = true;										
				}		
				
			}catch(FileNotFoundException e) {
			      e.printStackTrace();
		    } catch(XMLStreamException e) {
		    	System.out.println("Invalid File Format. File processing aborted.");
		    	//e.printStackTrace();	
		    	abort = true;
		    }
		    
		    //if parse error then abort = true then no need to insert into database
		    //if parsing successful then insert captured beans into database
		    if(!abort){	    	
		    	boolean result = insertCapturedBeansInToDataBase(ccrReportHeader,ccrReports);
			    if(result){
			    	System.out.println("File "+xmlFile+" processing has been completed successfully.");	
			    }else{
			    	System.out.println("File "+xmlFile+" database insertion error.");
			    }
			    ccrReportHeader = null;
			    
		    }		    
		}
		
		if(connection != null){
			try {
				connection.close();
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
	}
	
	//process header tags of an xml file
	private static CCRReportHeader processHeaderTags(XMLEventReader xmlEventReader,
			XMLEvent xmlEvent) throws XMLStreamException {
		
		xmlEvent = xmlEventReader.nextEvent();
		
		CCRReportHeader ccrReportHeader = new CCRReportHeader();
		while(!xmlEvent.toString().equalsIgnoreCase("</BatchDetails>")){
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REPORTTYPE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setReportType(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REPORTFORMATTYPE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setReportFormatType(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.DATASTRUCTUREVERSION)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setDataStructureVersion(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.GENERATIONUTILITYVERSION)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setGenerationUtilityVersion(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.DATASOURCE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setDataSource(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REPORTINGENTITYNAME)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setReportingEntityName(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REPORTINGENTITYCATEGORY)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setReportingEntityCategory(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REREGISTRATIONNUMBER)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setReRegistrationNumber(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.FIUREID)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setFiureid(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.PONAME)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setPoName(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.PODESIGNATION)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setPoDesignation(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.ADDRESS)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setAddress(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.CITY)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setCity(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.STATECODE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setStateCode(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.PINCODE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setPinCode(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.COUNTRYCODE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setCountryCode(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.TELEPHONE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setTelephone(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.MOBILE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setMobile(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.FAX)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setFax(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.POEMAIL)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setPoEmail(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BATCHNUMBER)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setBatchNumber(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BATCHDATE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setBatchDate(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.MONTHOFREPORT)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setMonthOfReport(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.YEAROFREPORT)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setYearOfReport(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.OPERATIONALMODE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setOperationalMode(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.BATCHTYPE)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setBatchType(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.ORIGINALBATCHID)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setOriginalBatchID(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.REASONOFREVISION)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setReasonOfRevision(xmlEvent.asCharacters().getData());																	        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.PKICERTIFICATENUM)) {								
				xmlEvent = xmlEventReader.nextEvent();				
				ccrReportHeader.setPkiCertificateNum(xmlEvent.asCharacters().getData());																	        
			}
			
			xmlEvent = xmlEventReader.nextEvent();			
		}
		
		return ccrReportHeader;
	}	
	
	private static CCRReportTransaction processTransactionDetails(XMLEventReader xmlEventReader,XMLEvent xmlEvent) throws XMLStreamException{
		CCRReportTransaction ccrReportTransaction = new CCRReportTransaction();
		while(!xmlEvent.toString().equalsIgnoreCase("</TransactionDetails>")){	
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.DENOMINATION)) {								
				xmlEvent = xmlEventReader.nextEvent();
				ccrReportTransaction.setDenomination(xmlEvent.asCharacters().getData());									        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.CURRENCYSERIALNO)) {								
				xmlEvent = xmlEventReader.nextEvent();
				ccrReportTransaction.setCurrencySerialNo(xmlEvent.asCharacters().getData());					        
			}
			
			if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(AMLConstants.CURRENCYREMARKS)) {								
				xmlEvent = xmlEventReader.nextEvent();
				if(xmlEvent.toString().startsWith("</")){
					ccrReportTransaction.setCurrencyRemarks("NA");
				}else{
					ccrReportTransaction.setCurrencyRemarks(xmlEvent.asCharacters().getData());	
				}									        
			}
			
			xmlEvent = xmlEventReader.nextEvent();
		}		
		return ccrReportTransaction;		
	}
	
	private static boolean insertCapturedBeansInToDataBase(
			CCRReportHeader ccrReportHeader, List<CCRReport> ccrReports) {
		
		boolean result = true;	
		connection = makeDbConnection();
		
		if(connection != null){
			Long headerId =  insertHeaderBeanIntoDataBase(ccrReportHeader);			
			insertReportBeanIntoDataBase(headerId,ccrReports);
			try {
				connection.commit();
			} catch (SQLException e) {
				System.out.println("Committing error.");
				e.printStackTrace();
			}
		}else{
			result = false;
		}		
		return result;
	}	
	private static void insertReportBeanIntoDataBase(Long headerId,List<CCRReport> ccrReport) {
		
		StringBuilder txnStr = new StringBuilder("");
		StringBuilder repStr = new StringBuilder("");		
		Long txnId = new Long(0);		
		
		for(int i = 1 ; i < 5 ; i++){
			txnStr.append("?,");
		}
		
		txnStr.deleteCharAt(txnStr.length()-1);
		
		String strAMLCCCRHeaderMaster = "insert into AML_CCR_XML_REP_TXN_MASTER(" +
		"TXN_ID,DENOM,CURR_SERIAL_NO,CURR_RMRKS) values ("+txnStr.toString()+")";
		
		for(int i = 1 ; i < 36 ; i++){
			if(i== 24 || i == 26){
				repStr.append("to_date(?,'yyyy-mm-dd'),");
			}else{
				repStr.append("?,");
			}			
		}
		
		repStr.deleteCharAt(repStr.length()-1);
		
		String strAMLCCCReportDetails = "insert into AML_CCR_XML_REP_DTLS(" +
		"REP_HEADER_ID,REP_SERIAL_NO,ORG_REP_SERIAL_NO,BR_REF_NUM_TYPE," +
		"BR_REF_NUM,BR_NAME,BR_ADDRESS,BR_CITY,BR_STATE_CODE,BR_PIN_CODE," +
		"BR_COUNTRY_CODE,BR_TEL,BR_MOBILE,BR_FAX,BR_EMAIL,REP_SUM_1000_CNT," +
		"REP_SUM_500_CNT,REP_SUM_100_CNT,REP_SUM_50_CNT,REP_SUM_20_CNT,REP_SUM_10_CNT," +
		"REP_SUM_5_CNT,FICN_VAL,TD_DT,TD_CASH,DETECT_DT,DETECT_AT,PL_INFO," +
		"PL_REP_DTL,TD_PERSON,ACC_HOLDER,ACC_NUM,PR_RATING,INC_RMRKS,TXN_ID) " +
		"values ("+repStr.toString()+")";
			
		try{			
			txnPstmt = connection.prepareStatement(strAMLCCCRHeaderMaster);
			repDtlPstmt = connection.prepareStatement(strAMLCCCReportDetails);
			
			for(CCRReport report : ccrReport){
				txnId = insertEachReportBeanInDataBase(txnId,report,headerId);
				if(txnId != 0){
					insertEachReportTxnBeansInDataBase(txnId,report.getCcrReportTransactions());
				}				
			}			
			txnPstmt.close();
			repDtlPstmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	private static Long insertEachReportBeanInDataBase(
			Long txnId,CCRReport ccrRep, Long headerId) {		
		txnId = getNextSeqValue(AMLConstants.TXNMASTERSEQUENCE);
		try{
			repDtlPstmt.setLong(1, headerId);
			repDtlPstmt.setInt(2, Integer.parseInt(ccrRep.getReportSerialNo().toString()));
			repDtlPstmt.setInt(3, Integer.parseInt(ccrRep.getOriginalReportSerialNum().toString()));
			repDtlPstmt.setString(4, ccrRep.getBranchRefNumType());
			repDtlPstmt.setString(5, ccrRep.getBranchRefNum());
			repDtlPstmt.setString(6, ccrRep.getBranchName());
			repDtlPstmt.setString(7, ccrRep.getAddress());
			repDtlPstmt.setString(8, ccrRep.getCity());
			repDtlPstmt.setString(9, ccrRep.getStateCode());
			repDtlPstmt.setString(10, ccrRep.getPinCode());
			repDtlPstmt.setString(11, ccrRep.getCountryCode());
			repDtlPstmt.setString(12, ccrRep.getTelephone());
			repDtlPstmt.setString(13, ccrRep.getMobile());
			repDtlPstmt.setString(14, ccrRep.getFax());
			repDtlPstmt.setString(15, ccrRep.getBranchEmail());
			repDtlPstmt.setInt(16, Integer.parseInt(ccrRep.getiNR1000NoteCount().toString()));		
			repDtlPstmt.setInt(17, Integer.parseInt(ccrRep.getiNR500NoteCount().toString()));
			repDtlPstmt.setInt(18, Integer.parseInt(ccrRep.getiNR100NoteCount().toString()));
			repDtlPstmt.setInt(19, Integer.parseInt(ccrRep.getiNR50NoteCount().toString()));
			repDtlPstmt.setInt(20, Integer.parseInt(ccrRep.getiNR20NoteCount().toString()));
			repDtlPstmt.setInt(21, Integer.parseInt(ccrRep.getiNR10NoteCount().toString()));
			repDtlPstmt.setInt(22, Integer.parseInt(ccrRep.getiNR5NoteCount().toString()));
			repDtlPstmt.setInt(23, Integer.parseInt(ccrRep.getfICNValue().toString()));
			repDtlPstmt.setString(24, ccrRep.getDateOfTendering());
			repDtlPstmt.setInt(25, Integer.parseInt(ccrRep.getCashTendered().toString()));
			repDtlPstmt.setString(26, ccrRep.getDateOfDetection());
			repDtlPstmt.setString(27, ccrRep.getDetectedAt());
			repDtlPstmt.setString(28, ccrRep.getPoliceInformed());
			repDtlPstmt.setString(29, ccrRep.getPoliceReportDetail());
			repDtlPstmt.setString(30, ccrRep.getTenderingPerson());
			repDtlPstmt.setString(31, ccrRep.getAccountHolder());
			repDtlPstmt.setString(32, ccrRep.getAccountNumber());
			repDtlPstmt.setString(33,ccrRep.getPriorityRating());
			repDtlPstmt.setString(34, ccrRep.getIncidentRemarks());
			repDtlPstmt.setLong(35, txnId);			
			repDtlPstmt.executeUpdate();
			//connection.commit();			
		}catch(SQLException e){
			e.printStackTrace();
			txnId = new Long(0);
		}
		return txnId;
	}

	private static void insertEachReportTxnBeansInDataBase(Long txnId,
			List<CCRReportTransaction> ccrReportTransactions) {
		
		for(CCRReportTransaction ccrReportTransaction : ccrReportTransactions){			
			try {
				txnPstmt.setLong(1, txnId);			
				txnPstmt.setString(2, ccrReportTransaction.getDenomination());			
				txnPstmt.setString(3, ccrReportTransaction.getCurrencySerialNo());
				txnPstmt.setString(4, ccrReportTransaction.getCurrencyRemarks());
				txnPstmt.executeUpdate();
				//connection.commit();				
				
			} catch (SQLException e) {
				e.printStackTrace();				
			}
		}		
	}

	//get next sequence value based on sequence name
	private static Long getNextSeqValue(String seqName) {
		Long seqNextVal = new Long(0);
		String nextSequenceSql = "select "+seqName+".NEXTVAL from DUAL";
		
		ResultSet rs = null;
		
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
			e.printStackTrace();
		}
		return seqNextVal;
	}
	
	public static Connection makeDbConnection(){
		
		if(null == connection ){
			//UAT/DEV
			String strConnection = "jdbc:oracle:thin:@10.144.136.92:1521:oraaml";
			//production
			//String strConnection = "jdbc:oracle:thin:@10.144.18.161:1521:oraaml";
			String username = "amlidbi";
			String password = "amlidbi";
			
			try {
				// Make connection			
				Class.forName("oracle.jdbc.driver.OracleDriver");
				//connectionAML = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);			
				connection = DriverManager.getConnection(strConnection,username,password);

			} catch (Exception e) {
				e.printStackTrace();			
			}
		}
		
		return connection;
	}
	
	private static Long insertHeaderBeanIntoDataBase(CCRReportHeader ccrReportHeader) {
		
		Long repHeaderId = new Long(0);		
		StringBuilder sb = new StringBuilder("");
		
		for(int i = 1 ; i < 31 ; i++){
			sb.append("?,");
		}
		
		sb.deleteCharAt(sb.length()-1);		
	
		String strAMLCCCRHeaderMaster = "insert into AML_CCR_XML_HEADER_MASTER(REP_HEADER_ID,REP_TYPE," +
		"REP_FORMAT_TYPE,DATA_STRUCT_VER,GENERATION_UTILITY_VER,DATA_SRC,REP_ENTITY_NAME," +
		"REP_ENTITY_CATEGORY,RE_REG_NUM,FIUREID,PO_NAME,PO_DESIGNATION,PO_ADDRESS," +
		"PO_CITY,PO_STATE,PO_PIN_CODE,PO_COUNTRY_CODE,PO_TEL,PO_MOB,PO_FAX,PO_EMAIL," +
		"BT_NUMBER,BT_DATE,BT_REP_MNTH,BT_REP_YR,BT_OP_MODE," +
		"BT_TYPE,BT_ORIGINAL_ID,BT_REASON_REVISION,BT_PKI_CERT_NO) values ("+sb.toString()+")";
		
		try{
			
			headerPstmt = connection.prepareStatement(strAMLCCCRHeaderMaster);
				
			//30 header fields are required to be captured to enter in AML_CCR_HEADER_MASTER table
			
			//get latest sequence number
			repHeaderId = getNextSeqValue(AMLConstants.HEADERMASTERSEQUENCE);
			headerPstmt.setLong(1, repHeaderId);		
			
			headerPstmt.setString(2, ccrReportHeader.getReportType());
			headerPstmt.setString(3,ccrReportHeader.getReportType());
			headerPstmt.setString(4,ccrReportHeader.getDataStructureVersion());
			headerPstmt.setString(5,ccrReportHeader.getGenerationUtilityVersion());
			headerPstmt.setString(6,ccrReportHeader.getDataSource());
			headerPstmt.setString(7,ccrReportHeader.getReportingEntityName());
			headerPstmt.setString(8,ccrReportHeader.getReportingEntityCategory());
			headerPstmt.setString(9,ccrReportHeader.getReRegistrationNumber());
			headerPstmt.setString(10,ccrReportHeader.getFiureid());
			headerPstmt.setString(11,ccrReportHeader.getPoName());			
			headerPstmt.setString(12,ccrReportHeader.getPoDesignation());
			headerPstmt.setString(13,ccrReportHeader.getAddress());
			headerPstmt.setString(14,ccrReportHeader.getCity());
			headerPstmt.setString(15,ccrReportHeader.getStateCode());
			headerPstmt.setString(16,ccrReportHeader.getPinCode());
			headerPstmt.setString(17,ccrReportHeader.getCountryCode());
			headerPstmt.setString(18,ccrReportHeader.getTelephone());
			headerPstmt.setString(19,ccrReportHeader.getMobile());
			headerPstmt.setString(20,ccrReportHeader.getFax());
			headerPstmt.setString(21,ccrReportHeader.getPoEmail());
			headerPstmt.setString(22,ccrReportHeader.getBatchNumber());
			headerPstmt.setString(23,ccrReportHeader.getBatchDate());
			headerPstmt.setString(24,ccrReportHeader.getMonthOfReport());
			headerPstmt.setString(25,ccrReportHeader.getYearOfReport());
			headerPstmt.setString(26,ccrReportHeader.getOperationalMode());
			headerPstmt.setString(27,ccrReportHeader.getBatchType());
			headerPstmt.setString(28,ccrReportHeader.getOriginalBatchID());
			headerPstmt.setString(29,ccrReportHeader.getReasonOfRevision());
			headerPstmt.setString(30,ccrReportHeader.getPkiCertificateNum());			
			headerPstmt.executeUpdate();						
			headerPstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return repHeaderId;
	}
	
	public static void main(String[] args) {
		ResourceBundle rb = ResourceBundle.getBundle(AMLConstants.AML_PROPERTIES);
		
		//read from source directory
		String ccrXMLDir = rb.getString(AMLConstants.CCR_XML_DIR_LOC);		
		ArrayList<String> xmlFiles = getFilesFromDirectory(ccrXMLDir);
		if(null != xmlFiles && xmlFiles.size() > 0){
			//process files
			processXMLFiles(xmlFiles);
		}else{
			System.out.println("Folder empty. No file to process.");
		}		
	}
	
	//get xml files from directory
	private static ArrayList<String> getFilesFromDirectory(String directory) {
		
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
}

