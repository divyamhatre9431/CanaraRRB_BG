package com.idbi.intech.aml.bg_process;

public class AMLConstants {

	public static final String NEW = "N";

	public static final String NONE = "-";

	public static final String SYSTEM = "SYSTEM";

	public static final String CTRExcelReportAmount = "10000000";

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	public static final String YES = "Y";

	public static final String SUSPECIOUS = "S";

	public static final String SWIFT = "SWIFT";

	public static final String PENDING = "P";
	public static final String REJECTED = "R";
	public static final String CLOSED = "C";

	// Cash Txn Report Threshold
	public static final String CASHTHRESHOLD = "200000";
	public static final String ACTIVE = "A";

	// XML Fragmentation - 14 MB
	public static final long XML_FRAGMENT_SIZE = 14680064;
	
	//@Author - Hardik - addition start	
	//aml property file name
	public static final String AML_PROPERTIES = "AMLProp";
	
	//ccr xml directory location
	public static final String CCR_XML_DIR_LOC = "CCR_XML_DIR";
	
	//ccr xml header tags
	public static final String REPORTTYPE = "ReportType";
	public static final String REPORTFORMATTYPE = "ReportFormatType";
	public static final String DATASTRUCTUREVERSION = "DataStructureVersion";
	public static final String GENERATIONUTILITYVERSION = "GenerationUtilityVersion";
	public static  final String DATASOURCE = "DataSource";
	public static final String REPORTINGENTITYNAME = "ReportingEntityName";
	public static final String REPORTINGENTITYCATEGORY = "ReportingEntityCategory";
	public static final String REREGISTRATIONNUMBER = "RERegistrationNumber";
	public static final String FIUREID = "FIUREID";
	public static final String PONAME = "POName";
	public static final String PODESIGNATION = "PODesignation";
	public static final String POADDRESS = "Address";
	public static final String POCITY = "City";
	public static final String POSTATECODE = "StateCode";
	public static final String POPINCODE = "PinCode";
	public static final String POCOUNTRYCODE = "CountryCode";
	public static final String POTELEPHONE = "Telephone";
	public static final String POMOBILE = "Mobile";
	public static final String POFAX = "Fax";
	public static final String POEMAIL = "POEmail";
	public static final String BATCHNUMBER = "BatchNumber";
	public static final String BATCHDATE = "BatchDate";
	public static final String MONTHOFREPORT = "MonthOfReport";
	public static final String YEAROFREPORT = "YearOfReport";
	public static final String OPERATIONALMODE = "OperationalMode";
	public static final String BATCHTYPE = "BatchType";
	public static final String ORIGINALBATCHID = "OriginalBatchID";
	public static final String REASONOFREVISION = "ReasonOfRevision";
	public static final String PKICERTIFICATENUM = "PKICertificateNum";
	
	//ccr xml report tags
	public static final String REPORT_START_TAG = "<Report>";
	public static final String REPORT_END_TAG = "</Report>";
	public static final String REPORT = "report";
	public static final String REPORTSERIALNUM = "ReportSerialNum";
	public static final String ORIGINALREPORTSERIALNUM = "OriginalreportserialNum";
	public static final String BRANCHREFNUMTYPE = "BranchRefNumType";
	public static final String BRANCHREFNUM = "BranchRefNum";
	public static final String BRANCHNAME = "BranchName";
	public static final String ADDRESS = "Address";
	public static final String CITY = "City";
	public static final String STATECODE = "StateCode";
	public static final String PINCODE = "PinCode";
	public static final String COUNTRYCODE = "CountryCode";
	public static final String TELEPHONE = "Telephone";
	public static final String MOBILE = "Mobile";
	public static final String FAX = "Fax";
	public static final String BRANCHEMAIL = "BranchEmail";
	public static final String INR1000NOTECOUNT = "INR1000NoteCount";
	public static final String INR500NOTECOUNT = "INR500NoteCount"; 
	public static final String INR100NOTECOUNT = "INR100NoteCount";
	public static final String INR50NOTECOUNT = "INR50NoteCount";
	public static final String INR20NOTECOUNT = "INR20NoteCount";
	public static final String INR10NOTECOUNT = "INR10NoteCount";
	public static final String INR5NOTECOUNT = "INR5NoteCount";
	public static final String FICNVALUE = "FICNValue";
	public static final String DATEOFTENDERING = "DateOfTendering";
	public static final String CASHTENDERED = "CashTendered";
	public static final String DATEOFDETECTION = "DateOfDetection";
	public static final String DETECTEDAT = "DetectedAt";
	public static final String POLICEINFORMED = "PoliceInformed";
	public static final String POLICEREPORTDETAIL = "PoliceReportDetail";
	public static final String TENDERINGPERSON = "TenderingPerson";
	public static final String ACCOUNTHOLDER = "AccountHolder";
	public static final String ACCOUNTNUMBER = "AccountNumber";
	public static final String PRIORITYRATING = "PriorityRating";
	public static final String INCIDENTREMARKS = "IncidentRemarks";
	
	// ccr xml report-transaction's tags
	public static final String TRANSACTIONDETAILS = "TransactionDetails";
	public static final String DENOMINATION = "Denomination"; 
	public static final String CURRENCYSERIALNO = "CurrencySerialNum";
	public static final String CURRENCYREMARKS = "CurrencyRemarks";
	
	//ccr xml sequences
	public static final String TXNMASTERSEQUENCE = "AML_CCR_XML_REP_TXN_MASTER_SEQ";
	public static final String HEADERMASTERSEQUENCE = "AML_CCR_HEADER_MASTER_SEQ";
	public static final String CCRFILESEQ = "CCR_FILE_SEQ";
	//@Author - Hardik - addition end
}
