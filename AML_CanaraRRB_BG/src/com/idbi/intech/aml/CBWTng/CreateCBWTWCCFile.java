package com.idbi.intech.aml.CBWTng;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreateCBWTWCCFile {
	private static Connection con_aml = null;

	public static void makeConnection() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void createXMLFile(String fileName) {
		StringBuffer sb = new StringBuffer();

		ResultSet rs = null;
		ResultSet RptNumRs = null;
		Statement stmt = null;
		Statement StmtNumRs = null;
		ResultSet subrs = null;
		Statement substmt = null;
		String reportSrlNum = "";
		String txnsol = "";
		String state = "ZZ";
		String query="";
		
		ArrayList<String> statecode = new ArrayList<String>();
		
		statecode.add("AN");
		statecode.add("AP");
		statecode.add("AR");
		statecode.add("AS");
		statecode.add("BR");
		statecode.add("CH");
		statecode.add("CG");
		statecode.add("DN");
		statecode.add("DD");
		statecode.add("DL");
		statecode.add("GA");
		statecode.add("GJ");
		statecode.add("HR");
		statecode.add("HP");
		statecode.add("JK");
		statecode.add("JH");
		statecode.add("KA");
		statecode.add("KL");
		statecode.add("LD");
		statecode.add("MP");
		statecode.add("MH");
		statecode.add("MN");
		statecode.add("ML");
		statecode.add("MZ");
		statecode.add("NL");
		statecode.add("OR");
		statecode.add("PY");
		statecode.add("PB");
		statecode.add("RJ");
		statecode.add("SK");
		statecode.add("TN");
		statecode.add("TR");
		statecode.add("UP");
		statecode.add("UA");
		statecode.add("WB");
		statecode.add("XX");
		statecode.add("ZZ");
		
				
		try {

			// Initial Tag
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Batch><ReportType>EFT</ReportType><ReportFormatType>TRF</ReportFormatType><BatchHeader><DataStructureVersion>2</DataStructureVersion><GenerationUtilityVersion>00000</GenerationUtilityVersion><DataSource>xml</DataSource></BatchHeader><ReportingEntity><ReportingEntityName>IDBI BANK LTD</ReportingEntityName><ReportingEntityCategory>BAPUB</ReportingEntityCategory><RERegistrationNumber>22</RERegistrationNumber><FIUREID>BASCB00075</FIUREID></ReportingEntity><PrincipalOfficer><POName>Varughese A.S.</POName><PODesignation>General Manager</PODesignation><POAddress><Address>IDBI Bank Ltd,AMLCell,Annex Building, Plot no.39,40 and 41, Sector 11, CBD Belapur</Address><City>NAVI MUMBAI</City><StateCode>MH</StateCode><PinCode>400614</PinCode><CountryCode>IN</CountryCode></POAddress><POPhone><Telephone>022 66700697</Telephone><Mobile>918494941779</Mobile><Fax>91-022-66700520</Fax></POPhone><POEmail>a_varughese@idbi.co.in</POEmail></PrincipalOfficer><BatchDetails>");
			
			stmt =con_aml.createStatement();
			StmtNumRs = con_aml.createStatement();
			rs = stmt.executeQuery("select lpad(cbwtbatchnum.nextval,8,'0'),to_char(sysdate,'YYYY-MM-DD'),to_char(add_months(sysdate,-1),'MM'),to_char(add_months(sysdate,-1),'YYYY') "  
					+" from dual");
			while(rs.next()){
				sb.append("<BatchNumber>"+rs.getString(1)+"</BatchNumber><BatchDate>"+rs.getString(2)+"</BatchDate><MonthOfReport>"+rs.getString(3)+"</MonthOfReport><YearOfReport>"+rs.getString(4)+"</YearOfReport><OperationalMode>P</OperationalMode><BatchType>N</BatchType><OriginalBatchID>0</OriginalBatchID><ReasonOfRevision>N</ReasonOfRevision><PKICertificateNum>0000000000</PKICertificateNum></BatchDetails>");
			}
			substmt = con_aml.createStatement();
			// purchase other than currency
			//query+=" and tran_date between '"+startDate+"' and '"+endDate+"'";
			RptNumRs = StmtNumRs
					.executeQuery("select distinct reportserialnum from aml_cbwt_wcc order by to_number(reportserialnum)");
			while(RptNumRs.next()){
				reportSrlNum = RptNumRs.getString(1);
				String branchSol = "";
				sb.append("<Report>");
				sb.append("<ReportSerialNum>" + reportSrlNum
						+ "</ReportSerialNum>");
				sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>");
				sb.append("<MainPersonName></MainPersonName>");
				query="SELECT "
				 +" to_char(A.TRANSACTIONDATE,'yyyy-mm-dd'),  "
				 +"  decode(A.TRANSACTIONTIME,'NA','00:00:00',A.TRANSACTIONTIME), A.TRANSACTIONREFNO, A.TRANSACTIONTYPE, " 
				 +"  A.INSTRUMENTTYPE, A.TRANSACTIONINSTITUTIONNAME, A.TRANSACTIONINSTITUTIONREFNUM,  "
				 +"  A.TRANSACTIONSTATECODE, A.TRANSACTIONCOUNTRYCODE, "
				 +"  a.amountrupees, A.AMOUNTFOREIGNCURRENCY, A.CURRENCYOFTRANSACTION,  "
				 +"  A.PURPOSEOFTRANSACTION, A.PURPOSECODE, decode(A.RISKRATING,'T1(High Risk)','T1','T2(Medium Risk)','T2','T3(Low Risk)','T3','XX'),  "
				 +" A.CUSTOMERNAME, A.CUSTOMERID, A.OCCUPATION,  "
				 +"  to_char(to_date(decode(a.dateofbirth,'NA','01-jan-01',a.dateofbirth),'dd-mm-yy'),'yyyy-mm-dd'), A.GENDER, decode(A.NATIONALITY,'INDIAN','IN',A.NATIONALITY),  "
				 +"  A.PAN, "
				 +"  A.ADDRESS, A.CITY, A.STATECODE,  "
				 +"  A.PINCODE, A.COUNTRYCODE, A.TELEPHONE,  "
				 +"  A.EMAIL,  "
				 +"  A.ACCOUNTNUMBER, (select branch_id from aml_ac_master where cust_Ac_no =A.ACCOUNTNUMBER ) "
				 +" FROM AML_CBWT_WCC A " 
				 + " where REPORTSERIALNUM = '"+reportSrlNum+"'";
				rs=stmt.executeQuery(query);
				
				while(rs.next()){
					String tranType = rs.getString(4);
				sb.append("<Transaction>");
				sb.append("<TransactionDate>" + rs.getString(1)
						+ "</TransactionDate>");
				sb.append("<TransactionTime>" + rs.getString(2)
						+ "</TransactionTime>");
				sb.append("<TransactionRefNum>" + rs.getString(3)
						+ "</TransactionRefNum>");
				sb.append("<TransactionType>" + tranType
						+ "</TransactionType>");
				sb.append("<InstrumentType>" + rs.getString(5)
						+ "</InstrumentType>");
				sb.append("<TransactionInstitutionName>" + rs.getString(6)
						+ "</TransactionInstitutionName>");
				sb.append("<TransactionInstitutionRefNum>" + rs.getString(7)
						+ "</TransactionInstitutionRefNum>");
				sb.append("<TransactionStateCode>" + rs.getString(8)
						+ "</TransactionStateCode>");
				sb.append("<TransactionCountryCode>" + rs.getString(9)
						+ "</TransactionCountryCode>");
				sb.append("<PaymentInstrumentNum></PaymentInstrumentNum>");
				sb.append("<PaymentInstrumentIssueInstitutionName></PaymentInstrumentIssueInstitutionName>");
				sb.append("<InstrumentIssueInstitutionRefNum></InstrumentIssueInstitutionRefNum>");
				sb.append("<InstrumentCountryCode>XX</InstrumentCountryCode>");
				sb.append("<AmountRupees>" + rs.getString(10)
						+ "</AmountRupees>");
				sb.append("<AmountForeignCurrency>0"
						+ "</AmountForeignCurrency>");
				sb.append("<CurrencyOfTransaction>XXX"
						+ "</CurrencyOfTransaction>");
				sb.append("<PurposeOfTransaction>" + rs.getString(13)
						+ "</PurposeOfTransaction>");
				sb.append("<PurposeCode>XXXXX</PurposeCode>");
				sb.append("<RiskRating>" + rs.getString(15) + "</RiskRating>");
				sb.append("<CustomerDetails>");
				sb.append("<CustomerName>" + rs.getString(16)
						+ "</CustomerName>");
				sb.append("<CustomerId>" + rs.getString(17) + "</CustomerId>");
				sb.append("<Occupation>" + rs.getString(18) + "</Occupation>");
				if(rs.getString(19) != null){
					sb.append("<DateOfBirth>" + rs.getString(19) + "</DateOfBirth>");
				}
				if(rs.getString(20) != null && !rs.getString(20).equalsIgnoreCase("NA")){
					sb.append("<Gender>" + rs.getString(20) + "</Gender>");
				}else{
					sb.append("<Gender>X</Gender>");
				}
				sb.append("<Nationality>" + rs.getString(21) + "</Nationality>");
				sb.append("<IdentificationType>Z</IdentificationType>");
				sb.append("<IdentificationNumber></IdentificationNumber>");
				sb.append("<IssuingAuthority></IssuingAuthority>");
				sb.append("<PlaceOfIssue></PlaceOfIssue>");
				sb.append("<PAN>" + rs.getString(22) + "</PAN>");
				sb.append("<UIN></UIN>");
				sb.append("<CustomerAddress>");
				sb.append("<Address>" + rs.getString(23) + "</Address>");
				sb.append("<City>" + (rs.getString(24) == null ? "" : rs.getString(24)) + "</City>");
				
				state = rs.getString(25);
				
				if(!statecode.contains(state))
					state="ZZ";
					
				sb.append("<StateCode>" + state + "</StateCode>");
				sb.append("<PinCode>" + (rs.getString(26) == null ? "" : rs.getString(26)) + "</PinCode>");
				sb.append("<CountryCode>" + rs.getString(27) + "</CountryCode>");
				sb.append("</CustomerAddress>");
				sb.append("<Phone>");
				sb.append("<Telephone>" + (rs.getString(28) == null ? "" : rs.getString(28)) + "</Telephone>");
				sb.append("<Mobile></Mobile>");
				sb.append("<Fax></Fax>");
				sb.append("</Phone>");
				sb.append("<Email>" + (rs.getString(29) == null ? "" : rs.getString(29)) + "</Email>");
				sb.append("</CustomerDetails>");
				sb.append("<AccountNumber>" + rs.getString(30)
						+ "</AccountNumber>");
				sb.append("<AccountWithInstitutionName>IDBI BANK LTD</AccountWithInstitutionName>");
					txnsol = rs.getString(31);
				if(tranType.equals("P"))
					branchSol = txnsol;
				sb.append("<AccountWithInstitutionRefNum>" + txnsol
						+ "</AccountWithInstitutionRefNum>");
				sb.append("<RelatedInstitutionName></RelatedInstitutionName>");
				sb.append("<InstitutionRelationFlag>X</InstitutionRelationFlag>");
				sb.append("<RelatedInstitutionRefNum></RelatedInstitutionRefNum>");
				sb.append("<Remarks></Remarks>");
				sb.append("</Transaction>");
				}
				
				
					sb.append("<Branch>");
					sb.append("<InstitutionName>IDBI BANK LTD</InstitutionName>");
					sb.append("<InstitutionBranchName></InstitutionBranchName>");
					//sb.append("<InstitutionBranchName>"+rs.getString(32)+"</InstitutionBranchName>");
					sb.append("<InstitutionRefNum>"+branchSol+"</InstitutionRefNum>");
					sb.append("<ReportingRole>B</ReportingRole>");
	
					subrs = substmt
							.executeQuery("select bank_identifier from brbic@live_fin a,sol@live_fin b where a.branch_code=b.br_code and paysys_id='SWIFT' and sol_id='"
									+ branchSol + "' and rownum<2");
					while (subrs.next()) {
						sb.append("<BIC>" + subrs.getString(1) + "</BIC>");
					}
	
					sb.append("<BranchAddress>");
	
					subrs = substmt
							.executeQuery("select addr_1||' '||addr_2,city_code,nvl(decode(state_code,'NA','ZZ',state_code),'ZZ'),pin_code from sol@live_fin  where  sol_id='"
									+ branchSol + "'");
					while (subrs.next()) {
						state = subrs.getString(3);
						
						if(!statecode.contains(state))
							state="ZZ";
						
						sb.append("<Address>" + subrs.getString(1) + "</Address>");
						sb.append("<City>" + subrs.getString(2) + "</City>");
						sb.append("<StateCode>" + state
								+ "</StateCode>");
						sb.append("<PinCode>" + (subrs.getString(4) == null ? "" : subrs.getString(4)) + "</PinCode>");
						sb.append("<CountryCode>IN</CountryCode>");
					}
	
					sb.append("</BranchAddress>");
					
					sb.append("<Phone>");
					sb.append("<Telephone></Telephone>");
					sb.append("<Mobile></Mobile>");
					sb.append("<Fax></Fax>");
					sb.append("</Phone>");
					sb.append("<Email></Email>");
					sb.append("<Remarks></Remarks>");
					sb.append("</Branch>");
				/*sb.append("<PaymentInstrument>");
				sb.append("<InstrumentRefNum>000000</InstrumentRefNum>");
				sb.append("<IssueInstitutionRefNum></IssueInstitutionRefNum>");
				sb.append("<InstrumentIssueInstitutionName></InstrumentIssueInstitutionName>");
				sb.append("<InstrumentHolderName></InstrumentHolderName>");
				//sb.append("<RelationshipBeginningDate>"+rs.getString(33)+"</RelationshipBeginningDate>");
				//sb.append("<RelationshipBeginningDate></RelationshipBeginningDate>");
				sb.append("<CumulativePurchaseTurnover>00000</CumulativePurchaseTurnover>");
				sb.append("<Remarks></Remarks>");
				sb.append("</PaymentInstrument>");
				sb.append("<RelatedPersons>");
				sb.append("<PersonName>XXXX</PersonName>");
				sb.append("<CustomerId></CustomerId>");
				sb.append("<RelationFlag>X</RelationFlag>");
				sb.append("<CommunicationAddress>");
				sb.append("<Address>XXXX</Address>");
				sb.append("<City>XX</City>");
				sb.append("<StateCode>XX</StateCode>");
				sb.append("<PinCode></PinCode>");
				sb.append("<CountryCode>XX</CountryCode>");
				sb.append("</CommunicationAddress>");
				sb.append("<Phone>");
				sb.append("<Telephone></Telephone>");
				sb.append("<Mobile></Mobile>");
				sb.append("<Fax></Fax>");
				sb.append("</Phone>");
				sb.append("<Email></Email>");
				sb.append("<SecondAddress>");
				sb.append("<Address>XXXX</Address>");
				sb.append("<City>XX</City>");
				sb.append("<StateCode>XX</StateCode>");
				sb.append("<PinCode></PinCode>");
				sb.append("<CountryCode>XX</CountryCode>");
				sb.append("</SecondAddress>");
				sb.append("<PAN></PAN>");
				sb.append("<UIN></UIN>");
				sb.append("<Individual>");
				sb.append("<Gender>X</Gender>");
				sb.append("<DateOfBirth>0001-01-01</DateOfBirth>");
				sb.append("<IdentificationType>Z</IdentificationType>");
				sb.append("<IdentificationNumber></IdentificationNumber>");
				sb.append("<IssuingAuthority></IssuingAuthority>");
				sb.append("<PlaceOfIssue></PlaceOfIssue>");
				sb.append("<Nationality>XX</Nationality>");
				sb.append("<PlaceOfWork></PlaceOfWork>");
				sb.append("<FatherOrSpouse></FatherOrSpouse>");
				sb.append("<Occupation></Occupation>");
				sb.append("</Individual>");
				sb.append("</RelatedPersons>");*/
				
				
				sb.append("</Report>");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		sb.append("</Batch>");
		String finalXml = sb.toString().replaceAll("[!#$%&'()*+;\\^_{|}~`\\[\\]]*", "");
		try{
			FileWriter writeXml= new FileWriter("D:/AML/CBWTXML/"+fileName+".xml");
			writeXml.write(finalXml);
			writeXml.close();
			System.out.println(fileName+"::File Writing Completed...");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		makeConnection();
		CreateCBWTWCCFile cbwt=new CreateCBWTWCCFile();
		//Addition of Query
		
		/*String WCCQuery = "SELECT "
			 +" to_char(A.TRANSACTIONDATE,'yyyy-mm-dd'),  "
			 +"  decode(A.TRANSACTIONTIME,'NA','00:00:00',A.TRANSACTIONTIME), A.TRANSACTIONREFNO, A.TRANSACTIONTYPE, " 
			 +"  A.INSTRUMENTTYPE, A.TRANSACTIONINSTITUTIONNAME, A.TRANSACTIONINSTITUTIONREFNUM,  "
			 +"  A.TRANSACTIONSTATECODE, A.TRANSACTIONCOUNTRYCODE, "
			 +"  a.amountrupees, A.AMOUNTFOREIGNCURRENCY, A.CURRENCYOFTRANSACTION,  "
			 +"  A.PURPOSEOFTRANSACTION, A.PURPOSECODE, decode(A.RISKRATING,'T3(High Risk)','T3','T2(Medium Risk)','T2','T1(Low Risk)','T1'),  "
			 +" A.CUSTOMERNAME, A.CUSTOMERID, A.OCCUPATION,  "
			 +"  to_char(to_date(decode(a.dateofbirth,'NA','01-jan-01',a.dateofbirth),'dd-mm-yy'),'yyyy-mm-dd'), A.GENDER, decode(A.NATIONALITY,'INDIAN','IN',A.NATIONALITY),  "
			 +"  A.PAN, "
			 +"  A.ADDRESS, A.CITY, A.STATECODE,  "
			 +"  A.PINCODE, A.COUNTRYCODE, A.TELEPHONE,  "
			 +"  A.EMAIL,  "
			 +"  A.ACCOUNTNUMBER, (select branch_id from aml_ac_master where cust_Ac_no =A.ACCOUNTNUMBER ) "
			 +" FROM AML_CBWT_WCC A "; */
		Date date=new Date();
		SimpleDateFormat ft=new SimpleDateFormat("ddMMMyyyy");
		String fromDate =ft.format(date).toString().toUpperCase();
		String filename="CBWT_EXC"+fromDate;
		System.out.println("File Writing in Process...");
		cbwt.createXMLFile(filename);
	}
}
