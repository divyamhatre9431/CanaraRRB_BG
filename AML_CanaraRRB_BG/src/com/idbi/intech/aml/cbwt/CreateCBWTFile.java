package com.idbi.intech.aml.cbwt;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//import com.idbi.intech.aml.STRWI.YesNo;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreateCBWTFile {
	private static Connection con_aml = null;

	public static void makeConnection() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public ArrayList<String> getDateRange(){
		ArrayList<String> dateArr=new ArrayList<String>();
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt =con_aml.createStatement();
			rs=stmt.executeQuery("select to_char(last_day(add_months(sysdate,-2))+1,'DD-MON-YYYY')||'~'||to_char(last_day(add_months(sysdate,-2))+10,'DD-MON-YYYY') daterange from dual"
								+" union" 
								+" select to_char(last_day(add_months(sysdate,-2))+11,'DD-MON-YYYY')||'~'||to_char(last_day(add_months(sysdate,-2))+20,'DD-MON-YYYY') daterange from dual"
								+" union"
								+" select to_char(last_day(add_months(sysdate,-2))+21,'DD-MON-YYYY')||'~'||to_char(last_day(add_months(sysdate,-1)),'DD-MON-YYYY') daterange from dual");
			while(rs.next()){
				dateArr.add(rs.getString(1));
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return dateArr;
	}
	public void createXMLFile(String query,String startDate,String endDate,String fileName) {
		StringBuffer sb = new StringBuffer();

		ResultSet rs = null;
		Statement stmt = null;
		ResultSet subrs = null;
		Statement substmt = null;
		Statement insrtstmt=null;
		String reportSrlNum = "";
		String txnsol = "";
		String state = "ZZ";
		String btch_num="";
		String btch_date="";
		String mnth_report="";
		String year_report="";
		String insert_query="";
		String riskRating="";
		
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
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Batch><ReportType>EFT</ReportType><ReportFormatType>TRF</ReportFormatType><BatchHeader><DataStructureVersion>2</DataStructureVersion><GenerationUtilityVersion>00000</GenerationUtilityVersion><DataSource>xml</DataSource></BatchHeader><ReportingEntity><ReportingEntityName>Industrial Development Bank Of India Limited</ReportingEntityName><ReportingEntityCategory>BAPVT</ReportingEntityCategory><RERegistrationNumber>00000000000</RERegistrationNumber><FIUREID>BASCB00075</FIUREID></ReportingEntity><PrincipalOfficer><POName>R. Ramesh</POName><PODesignation>General Manager</PODesignation><POAddress><Address>IDBI Bank Ltd,AMLCell,Sarju House,Plot No 90,</Address><City>MUMBAI</City><StateCode>MH</StateCode><PinCode>Pin</PinCode><CountryCode>IN</CountryCode></POAddress><POPhone><Telephone>022 66700700</Telephone><Mobile>022 66700700</Mobile><Fax>91-022-66700520</Fax></POPhone><POEmail>r_ramesh@idbi.co.in</POEmail></PrincipalOfficer><BatchDetails>");
			
			stmt =con_aml.createStatement();
			insrtstmt =con_aml.createStatement();
			rs = stmt.executeQuery("select lpad(cbwtbatchnum.nextval,8,'0'),to_char(sysdate,'YYYY-MM-DD'),to_char(add_months(sysdate,-1),'MM'),to_char(sysdate,'YYYY')  from dual");
			while(rs.next()){
				sb.append("<BatchNumber>"+rs.getString(1)+"</BatchNumber><BatchDate>"+rs.getString(2)+"</BatchDate><MonthOfReport>"+rs.getString(3)+"</MonthOfReport><YearOfReport>"+rs.getString(4)+"</YearOfReport><OperationalMode>P</OperationalMode><BatchType>N</BatchType><OriginalBatchID>0</OriginalBatchID><ReasonOfRevision>N</ReasonOfRevision><PKICertificateNum>0000000000</PKICertificateNum></BatchDetails>");
				btch_num=rs.getString(1);
				btch_date=rs.getString(2);
				mnth_report=rs.getString(3);
				year_report=rs.getString(4);
			}
			substmt = con_aml.createStatement();
			// purchase other than currency
			query+=" and tran_date between '"+startDate+"' and '"+endDate+"'";
			rs = stmt
					.executeQuery(query);

			while (rs.next()) {
				subrs = substmt
						.executeQuery("select lpad(cbwtreportnum.nextval,8,'0') from dual");
				while (subrs.next()) {
					reportSrlNum = subrs.getString(1);
				}
				
				insert_query="insert into aml_cbwt_tran_file" 
				+" (batch_num,batch_date,month_of_report,year_of_report,report_srl_num,org_report_srl_num,main_person_name,"
				+"tran_date,tran_time,tran_ref_num,tran_type,instrmnt_type,tran_inst_name,tran_inst_ref_num,tran_state_code,"
				+"tran_cntry_code,payment_instrmnt_num,payment_instrmnt_issue_num,instrmnt_ref_num,instrmnt_cntry_code,"
				+"amount_rupees,amount_foreign_curr,curr_of_tran,purpose_of_tran,purpose_code,risk_rating,cust_name,cust_id,occupation,"
				+"date_of_birth,gender,nationality,identificationtype,identificationnum,issuingauthority,placeofissue,pan,uin,"
				+"address,city,statecode,pincode,countrycode,ac_no,ac_inst_name,ac_inst_refnum,related_inst_name,inst_relation_flag,"
				+"related_inst_refnum,remarks,inward_outward_flg)"
				+"values('"+btch_num+"','"+btch_date+"','"+mnth_report+"','"+year_report+"','"+reportSrlNum+"','0',null,"
				+"'"+rs.getString(1)+"','"+rs.getString(2)+"','"+rs.getString(3)+"','"+rs.getString(4)+"','"+rs.getString(5)+"','"+rs.getString(6).replace("'"," ")+"','"+rs.getString(7)+"','"+rs.getString(8)+"',"
				+"'"+rs.getString(9)+"',null,null,null,'XX',"
				+"'"+rs.getString(10)+"','"+rs.getString(11)+"','"+rs.getString(12)+"','"+rs.getString(13).replace("'"," ")+"','"+rs.getString(14)+"','"+rs.getString(15)+"','"+rs.getString(16).replace("'"," ")+"','"+rs.getString(17)+"','"+rs.getString(18)+"',"
				+"'"+rs.getString(19)+"','"+rs.getString(20)+"','"+rs.getString(21)+"','Z',null,null,null,'"+rs.getString(22)+"',null,"
				+"'"+rs.getString(23).replace("'"," ")+"','"+rs.getString(24)+"','"+rs.getString(25)+"','"+rs.getString(26)+"','"+rs.getString(27)+"',"
				+"'"+rs.getString(30)+"','IDBI BANK LTD','"+rs.getString(31)+"',null,'X',null,null,'"+rs.getString(34)+"')";
				
				
				insrtstmt.executeUpdate(insert_query);
				
				sb.append("<Report>");
				sb.append("<ReportSerialNum>" + reportSrlNum
						+ "</ReportSerialNum>");
				sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>");
				sb.append("<MainPersonName></MainPersonName>");
				sb.append("<Transaction>");
				sb.append("<TransactionDate>" + rs.getString(1)
						+ "</TransactionDate>");
				sb.append("<TransactionTime>" + rs.getString(2)
						+ "</TransactionTime>");
				sb.append("<TransactionRefNum>" + rs.getString(3)
						+ "</TransactionRefNum>");
				sb.append("<TransactionType>" + rs.getString(4)
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
				sb.append("<AmountForeignCurrency>" + rs.getString(11)
						+ "</AmountForeignCurrency>");
				sb.append("<CurrencyOfTransaction>" + rs.getString(12)
						+ "</CurrencyOfTransaction>");
				sb.append("<PurposeOfTransaction>" + rs.getString(13)
						+ "</PurposeOfTransaction>");
				sb.append("<PurposeCode>" + rs.getString(14) + "</PurposeCode>");
				riskRating=rs.getString(15);
				sb.append("<RiskRating>" + rs.getString(15)+ "</RiskRating>");
				sb.append("<CustomerDetails>");
				sb.append("<CustomerName>" + rs.getString(16)
						+ "</CustomerName>");
				sb.append("<CustomerId>" + rs.getString(17) + "</CustomerId>");
				sb.append("<Occupation>" + rs.getString(18) + "</Occupation>");
				sb.append("<DateOfBirth>" + rs.getString(19) + "</DateOfBirth>");
				sb.append("<Gender>" + rs.getString(20) + "</Gender>");
				sb.append("<Nationality>" + rs.getString(21) + "</Nationality>");
				sb.append("<IdentificationType>Z</IdentificationType>");
				sb.append("<IdentificationNumber></IdentificationNumber>");
				sb.append("<IssuingAuthority></IssuingAuthority>");
				sb.append("<PlaceOfIssue></PlaceOfIssue>");
				sb.append("<PAN>" + rs.getString(22) + "</PAN>");
				sb.append("<UIN></UIN>");
				sb.append("<CustomerAddress>");
				sb.append("<Address>" + rs.getString(23) + "</Address>");
				sb.append("<City>" + rs.getString(24) + "</City>");
				
				state = rs.getString(25);
				
				if(!statecode.contains(state))
					state="ZZ";
					
				sb.append("<StateCode>" + state + "</StateCode>");
				sb.append("<PinCode>" + rs.getString(26) + "</PinCode>");
				sb.append("<CountryCode>" + rs.getString(27) + "</CountryCode>");
				sb.append("</CustomerAddress>");
				sb.append("<Phone>");
				sb.append("<Telephone>" + rs.getString(28) + "</Telephone>");
				sb.append("<Mobile></Mobile>");
				sb.append("<Fax></Fax>");
				sb.append("</Phone>");
				sb.append("<Email>" + rs.getString(29) + "</Email>");
				sb.append("</CustomerDetails>");
				sb.append("<AccountNumber>" + rs.getString(30)
						+ "</AccountNumber>");
				sb.append("<AccountWithInstitutionName>IDBI BANK LTD</AccountWithInstitutionName>");
				txnsol = rs.getString(31);
				sb.append("<AccountWithInstitutionRefNum>" + txnsol
						+ "</AccountWithInstitutionRefNum>");
				sb.append("<RelatedInstitutionName></RelatedInstitutionName>");
				sb.append("<InstitutionRelationFlag>X</InstitutionRelationFlag>");
				sb.append("<RelatedInstitutionRefNum></RelatedInstitutionRefNum>");
				sb.append("<Remarks></Remarks>");
				sb.append("</Transaction>");
				sb.append("<Branch>");
				sb.append("<InstitutionName>IDBI BANK LTD</InstitutionName>");
				sb.append("<InstitutionBranchName>"+rs.getString(32)+"</InstitutionBranchName>");
				sb.append("<InstitutionRefNum>"+txnsol+"</InstitutionRefNum>");
				sb.append("<ReportingRole>B</ReportingRole>");

				subrs = substmt
						.executeQuery("select bank_identifier from brbic@live_fin a,sol@live_fin b where a.branch_code=b.br_code and paysys_id='SWIFT' and sol_id='"
								+ txnsol + "' and rownum<2");
				while (subrs.next()) {
					sb.append("<BIC>" + subrs.getString(1) + "</BIC>");
				}

				sb.append("<BranchAddress>");

				subrs = substmt
						.executeQuery("select addr_1||' '||addr_2,city_code,nvl(decode(state_code,'NA','ZZ',state_code),'ZZ'),pin_code from sol@live_fin  where  sol_id='"
								+ txnsol + "'");
				while (subrs.next()) {
					state = subrs.getString(3);
					
					if(!statecode.contains(state))
						state="ZZ";
					
					sb.append("<Address>" + subrs.getString(1) + "</Address>");
					sb.append("<City>" + subrs.getString(2) + "</City>");
					sb.append("<StateCode>" + state
							+ "</StateCode>");
					sb.append("<PinCode>" + subrs.getString(4) + "</PinCode>");
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
				sb.append("<PaymentInstrument>");
				sb.append("<InstrumentRefNum>000000</InstrumentRefNum>");
				sb.append("<IssueInstitutionRefNum></IssueInstitutionRefNum>");
				sb.append("<InstrumentIssueInstitutionName></InstrumentIssueInstitutionName>");
				sb.append("<InstrumentHolderName></InstrumentHolderName>");
				sb.append("<RelationshipBeginningDate>"+rs.getString(33)+"</RelationshipBeginningDate>");
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
				sb.append("</RelatedPersons>");
				sb.append("</Report>");
				con_aml.commit();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		sb.append("</Batch>");
		String finalXml = sb.toString().replaceAll("[!#$%&'()*+;\\^_{|}~`\\[\\]]*", "");
		try{
			FileWriter writeXml= new FileWriter("D:/EFT_FILES/JUN/EFT_"+fileName+".xml");
			writeXml.write(finalXml);
			writeXml.close();
			System.out.println(fileName+"::File Writing Completed...");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		makeConnection();
		CreateCBWTFile cbwt=new CreateCBWTFile();
		//Addition of Query
		ArrayList<String> queryList = new ArrayList<String>();
		String inwardPocn="select to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
			+ "to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
			+ "pst.tran_id TransactionRefNum,"
			+ "'P' TransactionType,"
			+ "decode (fbr.dflt_reg_sub_type,'TT','E','CHQ','L','SIGHT','Z','USANC','Z','ORG','Z','TC','B','COLL','Z','IMADV','E','DD','C') InstrumentType,"
			+ "(select distinct sol_desc from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2) transactionInsitutionName,"
			+ "(select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2)  transactionInsitutionRefNum,"
			+ "nvl((select distinct a.state_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2),'XX') transactionStateCode,"
			+ "nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2),'IN') transactionCountry_Code,"
			+ "round(amt_home_crncy,0) AmountRupees,"
			+ "round(amt_purchsd,0) AmountForeignCurrency,"
			+ "crncy_purchsd CurrencyOfTransaction,"
			+ "(select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
			+ "pst.purpose_of_rem PurposeCode,"
			+ "nvl((select (case when free_code_1 like 'RL_1'then 'T3' "
			+ " when free_code_1 like 'RL1%'then 'T3' "
			+ " when free_code_1 like 'RL_2'then 'T2' "
			+ " when free_code_1 like 'RL2%'then 'T2' "
			+ " when free_code_1 like 'RL_3'then 'T1' "
			+ " when free_code_1 like 'RL3%'then 'T1' "
			+ " else 'XX' end) from cem@live_fin where cust_id=fbm.party_code),'XX') riskRating,"
			+ " nvl((select cust_name from cmg@live_fin where cust_id=fbm.party_code),'XX') customerName,"
			+ " nvl((select cust_id from cmg@live_fin where cust_id=fbm.party_code),'XX') customerId,"
			+ " nvl((select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code),0) Occupation,"
			+ " nvl((select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') DateOfBirth,"
			+ " nvl((select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code),'X') Gender,"
			+ " nvl((select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code),'IN') Nationality,"
			+ " nvl((select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code),'XXXX') pan,"
			+ " nvl((select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code),'XXXX') address,"
			+ " nvl((select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code),'XX') city,"
			+ " nvl((select decode((select count(1) from sol@live_fin where state_code = a.cust_comu_state_code),0,'ZZ',cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code),'ZZ') state,"
			+ " nvl((select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code),00) pincode,"
			+ " nvl((select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code),'ZZ') countryCode,"
			+ " nvl((select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code),'00000') phone,"
			+ " nvl((select email_id from cmg@live_fin where cust_id=fbm.party_code),'NA') email,"
			+ " (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
			+ "  (select sol_id from gam@live_fin where acid=fbh.oper_acid) accountinstitutionRefNum, "
			+ "	(select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@live_fin where acid=fbh.oper_acid)) institutionName,"
			+ " nvl((select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') relatioshipBeginningDate,"
			+ " 'I' tran_type from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr "
			+ " where fbh.event_acid in ( select acid"
			+ "       from gam@live_fin"
			+ "       where gam.sol_id = '096'"
			+ "       and gam.gl_sub_head_code in ('31200', '52110')"
			+ "       and gam.acct_crncy_code = pst.crncy_purchsd)"
			+"  and pst.bill_ref_num = fbm.bill_id"
			+ " and pst.sol_id = fbm.sol_id"
			+ " and fbm.bill_id = fbh.bill_id"
			+ " and fbm.sol_id = fbh.sol_id"
			+ " and fbh.vfd_bod_date = pst.tran_date"
			+ " and fbh.tran_id = pst.tran_id"
			+ " and fbm.reg_type = fbr.reg_type"
			+ " and bill_func in ('R','P')"
			+ " and fbm.ps_ind = 'Y'"
			+ " and pst.purpose_of_rem like 'P%'"
			+ " and amt_home_crncy > 500000"
			+ " and pst.crncy_purchsd <> 'INR'"
			+ " and pst.entity_cre_flg = 'Y'"
			+ " and inward_outward_ind = 'I'"
			+ " and fbm.reg_type not in ('IREC','FCNI')";
		
		/*String inwardPcn="select  to_char(fbh.vfd_bod_date,'YYYY-MM-DD') transactionDate,"
        +" to_char(fbh.vfd_sys_date,'HH24:MM:SS') transactionTime,"
        +" pst.tran_id transactionRefNum," 
        +" 'P' transactionType," 
        +" 'A' instrumentType,"
        +" (select distinct sol_desc from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id) transactionInsitutionName,"
        +" (select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id)  transactionInsitutionRefNum,"
        +" (select distinct a.state_code from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id) transactionStateCode,"       
        +" nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id),'IN') transactionCountry_Code,"
        +" round(amt_home_crncy,0) amountRupees,"
        +" round(amt_purchsd,0) amountForeignCurrency,"
        +" crncy_purchsd currencyOfTransaction,"
        +" (select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  purposeOfTransaction,"
        +" pst.purpose_of_rem purposeCode,"
        +"(select (case when free_code_1 like 'RL_1'then 'T3'"
        +"   when free_code_1 like 'RL1%'then 'T3'"
        +"    when free_code_1 like 'RL_2'then 'T2'" 
        +"    when free_code_1 like 'RL2%'then 'T2'"
        +"    when free_code_1 like 'RL_3'then 'T1'"
        +"    when free_code_1 like 'RL3%'then 'T1'"
        +"   else 'XX' end) from cem@live_fin where cust_id=fbm.party_code) riskRating,"
        +" (select cust_name from cmg@live_fin where cust_id=fbm.party_code) customerName,"   
        +" (select cust_id from cmg@live_fin where cust_id=fbm.party_code) customerId,"
        +" (select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code) Occupation,"
        +" (select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code) DateOfBirth,"
        +" (select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code) Gender,"
        +" (select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code) Nationality,"
        +" (select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code) pan,"
        +" (select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code) address,"
        +" (select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code) city,"
        +" (select decode((select count(1) from sol@live_fin where state_code=a.cust_comu_state_code),0,'ZZ',a.cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code) state,"
        +" (select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code) pincode,"
        +" (select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code) countryCode,"
        +" (select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code) phone,"
        +" (select email_id from cmg@live_fin where cust_id=fbm.party_code) email,"
        +" (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
        +" (select sol_id from gam@live_fin where acid=fbh.oper_acid) sol_id,"
        +" (select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@live_fin where acid=fbh.oper_acid)) sol_desc,"
        +" (select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code) relatioshipBeginningDate"
        +" from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr"
        +" where fbh.event_acid in ( select acid"
        +"                from gam@live_fin"
        +"               where gam.gl_sub_head_code = '50500' "
        +"              and gam.acct_crncy_code = pst.crncy_purchsd)"  
        +" and pst.bill_ref_num = fbm.bill_id"
        +" and pst.sol_id = fbm.sol_id"
        +" and fbm.bill_id = fbh.bill_id"
        +" and fbm.sol_id = fbh.sol_id"
        +" and fbh.vfd_bod_date = pst.tran_date"
        +" and fbh.tran_id = pst.tran_id"
        +" and fbm.reg_type = fbr.reg_type"
        +" and bill_func in ('R','P')"
        +" and fbm.ps_ind = 'Y'"
        +" and pst.purpose_of_rem like 'P%'"
        +" and amt_home_crncy > 500000"
        +" and pst.crncy_purchsd <> 'INR'"
        +" and pst.entity_cre_flg = 'Y'"
        +" and inward_outward_ind = 'I'"
        +" and fbm.reg_type = 'FCNI'";*/
		
		String inwardRocn="select  to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
        +" to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
        +" pst.tran_id TransactionRefNum," 
        +" 'R' TransactionType," 
        +" decode (fbr.dflt_reg_sub_type,'TT','E','CHQ','L','SIGHT','Z','USANC','Z','ORG','Z','TC','B','COLL','Z','IMADV','E','DD','C') InstrumentType,"
        +" (select distinct sol_desc from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id  and rownum<2) transactionInsitutionName,"
        +" (select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id  and rownum<2)  transactionInsitutionRefNum,"
        +" (select distinct a.state_code from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id  and rownum<2) transactionStateCode,"       
        +" nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b" 
        +" where a.br_code=b.br_code"
        +" and sol_id =fbm.sol_id  and rownum<2),'IN') transactionCountry_Code,"
        +" round(amt_home_crncy,0) AmountRupees,"
        +" round(amt_sold,0) AmountForeignCurrency,"
        +" crncy_sold CurrencyOfTransaction,"
        +" (select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
        +" pst.purpose_of_rem PurposeCode,"
        + "nvl((select (case when free_code_1 like 'RL_1'then 'T3' "
		+ " when free_code_1 like 'RL1%'then 'T3' "
		+ " when free_code_1 like 'RL_2'then 'T2' "
		+ " when free_code_1 like 'RL2%'then 'T2' "
		+ " when free_code_1 like 'RL_3'then 'T1' "
		+ " when free_code_1 like 'RL3%'then 'T1' "
		+ " else 'XX' end) from cem@live_fin where cust_id=fbm.party_code),'XX') riskRating,"
		+ " nvl((select cust_name from cmg@live_fin where cust_id=fbm.party_code),'XX') customerName,"
		+ " nvl((select cust_id from cmg@live_fin where cust_id=fbm.party_code),'XX') customerId,"
		+ " nvl((select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code),0) Occupation,"
		+ " nvl((select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') DateOfBirth,"
		+ " nvl((select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code),'X') Gender,"
		+ " nvl((select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code),'IN') Nationality,"
		+ " nvl((select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code),'XXXX') pan,"
		+ " nvl((select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code),'XXXX') address,"
		+ " nvl((select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code),'XX') city,"
		+ " nvl((select decode((select count(1) from sol@live_fin where state_code = a.cust_comu_state_code),0,'ZZ',cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code),'ZZ') state,"
		+ " nvl((select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code),00) pincode,"
		+ " nvl((select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code),'ZZ') countryCode,"
		+ " nvl((select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code),'00000') phone,"
		+ " nvl((select email_id from cmg@live_fin where cust_id=fbm.party_code),'NA') email,"
		+ " (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
		+ "  (select sol_id from gam@live_fin where acid=fbh.oper_acid) accountinstitutionRefNum, "
		+ "	(select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@live_fin where acid=fbh.oper_acid)) institutionName,"
		+ " nvl((select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') relatioshipBeginningDate,"
        +" 'I' tran_type from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr"
        +" where fbh.event_acid in ( select acid"
        +"              from gam@live_fin"
        +"              where gam.sol_id='096'" 
        +"              and gam.gl_sub_head_code in ('31200', '52110')" 
        +"              and gam.acct_crncy_code = pst.crncy_sold)" 
        +" and pst.bill_ref_num = fbm.bill_id"
        +" and pst.sol_id = fbm.sol_id"
        +" and fbm.bill_id = fbh.bill_id"
        +" and fbm.sol_id = fbh.sol_id"
        +" and fbh.vfd_bod_date = pst.tran_date"
        +" and fbh.tran_id = pst.tran_id"
        +" and fbm.reg_type = fbr.reg_type" 
        +" and bill_func in ('R','P')"
        +" and fbm.ps_ind = 'Y'"
        +" and pst.purpose_of_rem like 'S%'"
        +" and amt_home_crncy > 500000"
        +" and pst.crncy_sold <> 'INR'"
        +" and pst.entity_cre_flg = 'Y'"
        +" and inward_outward_ind = 'I'"
        +" and fbm.reg_type not in ('IREC','FCNI')";
		
		/*String inwardRcn="select  to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
	        +" to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
	        +" pst.tran_id TransactionRefNum," 
	        +" 'R' TransactionType," 
	        +"  'A' instrumentType,"
	        +" (select distinct sol_desc from sol@live_fin a,bct@live_fin b" 
	        +" where a.br_code=b.br_code"
	        +" and sol_id =fbm.sol_id) transactionInsitutionName,"
	        +" (select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b" 
	        +" where a.br_code=b.br_code"
	        +" and sol_id =fbm.sol_id)  transactionInsitutionRefNum,"
	        +" (select distinct a.state_code from sol@live_fin a,bct@live_fin b" 
	        +" where a.br_code=b.br_code"
	        +" and sol_id =fbm.sol_id) transactionStateCode,"       
	        +" nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b" 
	        +" where a.br_code=b.br_code"
	        +" and sol_id =fbm.sol_id),'IN') transactionCountry_Code,"
	        +" round(amt_home_crncy,0) AmountRupees,"
	        +" round(amt_sold,0) AmountForeignCurrency,"
	        +" crncy_sold CurrencyOfTransaction,"
	        +" (select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
	        +" pst.purpose_of_rem PurposeCode,"
	        +" (select (case when free_code_1 like 'RL_1'then 'T3'"
	        +"     when free_code_1 like 'RL1%'then 'T3'"
	        +"    when free_code_1 like 'RL_2'then 'T2'"  
	        +"  when free_code_1 like 'RL2%'then 'T2'"
	        +"  when free_code_1 like 'RL_3'then 'T1'"
	        +"  when free_code_1 like 'RL3%'then 'T1'"
	        +"  else 'XX' end) from cem@live_fin where cust_id=fbm.party_code) riskRating,"
	        +" (select cust_name from cmg@live_fin where cust_id=fbm.party_code) customerName,"   
	        +" (select cust_id from cmg@live_fin where cust_id=fbm.party_code) customerId,"
	        +" (select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code) Occupation,"
	        +" (select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code) DateOfBirth,"
	        +" (select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code) Gender,"
	        +" (select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code) Nationality,"
	        +" (select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code) pan,"
	        +" (select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code) address,"
	        +" (select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code) city,"
	        +" (select decode((select count(1) from sol@live_fin where state_code=a.cust_comu_state_code),0,'ZZ',a.cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code) state,"
	        +" (select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code) pincode,"
	        +" (select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code) countryCode,"
	        +" (select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code) phone,"
	        +" (select email_id from cmg@live_fin where cust_id=fbm.party_code) email,"
	        +" (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
	        +" (select sol_id from gam@live_fin where acid=fbh.oper_acid) sol_id,"
	        +" (select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@live_fin where acid=fbh.oper_acid)) sol_desc,"
	        +" (select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code) relatioshipBeginningDate"
	        +" from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr"
	        +" where fbh.event_acid in ( select acid"
	        +"              from gam@live_fin"
	        +"              where gam.gl_sub_head_code in ('31200', '52110')" 
	        +"              and gam.acct_crncy_code = pst.crncy_sold)" 
	        +" and pst.bill_ref_num = fbm.bill_id"
	        +" and pst.sol_id = fbm.sol_id"
	        +" and fbm.bill_id = fbh.bill_id"
	        +" and fbm.sol_id = fbh.sol_id"
	        +" and fbh.vfd_bod_date = pst.tran_date"
	        +" and fbh.tran_id = pst.tran_id"
	        +" and fbm.reg_type = fbr.reg_type" 
	        +" and bill_func in ('R','P')"
	        +" and fbm.ps_ind = 'Y'"
	        +" and pst.purpose_of_rem like 'S%'"
	        +" and amt_home_crncy > 500000"
	        +" and pst.crncy_sold <> 'INR'"
	        +" and pst.entity_cre_flg = 'Y'"
	        +" and inward_outward_ind = 'I'"
	        +" and fbm.reg_type = 'FCNI'";*/
		
		String outwardPocn="select to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
			+ "to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
			+ "pst.tran_id TransactionRefNum,"
			+ "'P' TransactionType,"
			+ "decode (fbr.dflt_reg_sub_type,'TT','E','CHQ','L','SIGHT','Z','USANC','Z','ORG','Z','TC','B','COLL','Z','IMADV','E','DD','C') InstrumentType,"
			+ "(select distinct sol_desc from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2) transactionInsitutionName,"
			+ "(select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2)  transactionInsitutionRefNum,"
			+ "(select distinct a.state_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2) transactionStateCode,"
			+ "nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2),'IN') transactionCountry_Code,"
			+ "round(amt_home_crncy,0) AmountRupees,"
			+ "round(amt_purchsd,0) AmountForeignCurrency,"
			+ "crncy_purchsd CurrencyOfTransaction,"
			+ "(select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
			+ "pst.purpose_of_rem PurposeCode,"
			+ "nvl((select (case when free_code_1 like 'RL_1'then 'T3' "
			+ " when free_code_1 like 'RL1%'then 'T3' "
			+ " when free_code_1 like 'RL_2'then 'T2' "
			+ " when free_code_1 like 'RL2%'then 'T2' "
			+ " when free_code_1 like 'RL_3'then 'T1' "
			+ " when free_code_1 like 'RL3%'then 'T1' "
			+ " else 'XX' end) from cem@live_fin where cust_id=fbm.party_code),'XX') riskRating,"
			+ " nvl((select cust_name from cmg@live_fin where cust_id=fbm.party_code),'XX') customerName,"
			+ " nvl((select cust_id from cmg@live_fin where cust_id=fbm.party_code),'XX') customerId,"
			+ " nvl((select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code),0) Occupation,"
			+ " nvl((select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') DateOfBirth,"
			+ " nvl((select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code),'X') Gender,"
			+ " nvl((select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code),'IN') Nationality,"
			+ " nvl((select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code),'XXXX') pan,"
			+ " nvl((select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code),'XXXX') address,"
			+ " nvl((select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code),'XX') city,"
			+ " nvl((select decode((select count(1) from sol@live_fin where state_code = a.cust_comu_state_code),0,'ZZ',cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code),'ZZ') state,"
			+ " nvl((select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code),00) pincode,"
			+ " nvl((select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code),'ZZ') countryCode,"
			+ " nvl((select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code),'00000') phone,"
			+ " nvl((select email_id from cmg@live_fin where cust_id=fbm.party_code),'NA') email,"
			+ " (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
			+ "  (select sol_id from gam@live_fin where acid=fbh.oper_acid) accountinstitutionRefNum, "
			+ "	(select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@live_fin where acid=fbh.oper_acid)) institutionName,"
			+ " nvl((select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') relatioshipBeginningDate,"
			+ "  'O' tran_type from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr "
			+ " where fbh.event_acid in ( select acid"
			+ "       from gam@live_fin"
			+ "       where gam.sol_id = '096'"
			+ "       and gam.gl_sub_head_code in ('31200', '52110')"
			+ "       and gam.acct_crncy_code = pst.crncy_purchsd)"
			+"  and pst.bill_ref_num = fbm.bill_id"
			+ " and pst.sol_id = fbm.sol_id"
			+ " and fbm.bill_id = fbh.bill_id"
			+ " and fbm.sol_id = fbh.sol_id"
			+ " and fbh.vfd_bod_date = pst.tran_date"
			+ " and fbh.tran_id = pst.tran_id"
			+ " and fbm.reg_type = fbr.reg_type"
			+ " and bill_func in ('R','P')"
			+ " and fbm.ps_ind = 'Y'"
			+ " and pst.purpose_of_rem like 'P%'"
			+ " and amt_home_crncy > 500000"
			+ " and pst.crncy_purchsd <> 'INR'"
			+ " and pst.entity_cre_flg = 'Y'"
			+ " and inward_outward_ind = 'O'"
			+ " and fbm.reg_type not in ('OREC','FCNO')";
		
		/*String outwardPcn="select to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
			+ "to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
			+ "pst.tran_id TransactionRefNum,"
			+ "'P' TransactionType,"
			+ "'A' instrumentType,"
			+ "(select distinct sol_desc from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id) transactionInsitutionName,"
			+ "(select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id)  transactionInsitutionRefNum,"
			+ "(select distinct a.state_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id) transactionStateCode,"
			+ "nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id),'IN') transactionCountry_Code,"
			+ "round(amt_home_crncy,0) AmountRupees,"
			+ "round(amt_purchsd,0) AmountForeignCurrency,"
			+ "crncy_purchsd CurrencyOfTransaction,"
			+ "(select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
			+ "pst.purpose_of_rem PurposeCode,"
			+ "(select (case when free_code_1 like 'RL_1'then 'T3' "
			+ " when free_code_1 like 'RL1%'then 'T3' "
			+ " when free_code_1 like 'RL_2'then 'T2' "
			+ " when free_code_1 like 'RL2%'then 'T2' "
			+ " when free_code_1 like 'RL_3'then 'T1' "
			+ " when free_code_1 like 'RL3%'then 'T1' "
			+ " else 'XX' end) from cem@live_fin where cust_id=fbm.party_code) riskRating,"
			+ " (select cust_name from cmg@live_fin where cust_id=fbm.party_code) customerName,"
			+ "  (select cust_id from cmg@live_fin where cust_id=fbm.party_code) customerId,"
			+ "  (select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code) Occupation,"
			+ "  (select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code) DateOfBirth,"
			+ "  (select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code) Gender,"
			+ "  (select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code) Nationality,"
			+ "  (select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code) pan,"
			+ "  (select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code) address,"
			+ "  (select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code) city,"
			+ "  (select decode((select count(1) from sol@live_fin where state_code = a.cust_comu_state_code),0,'ZZ',cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code) state,"
			+ "  (select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code) pincode,"
			+ "  (select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code) countryCode,"
			+ "  (select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code) phone,"
			+ "  (select email_id from cmg@live_fin where cust_id=fbm.party_code) email,"
			+ "  (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
			+ "  (select sol_id from gam@live_fin where acid=fbh.oper_acid) accountinstitutionRefNum, "
			+ "	(select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@daily_mis where acid=fbh.oper_acid)) institutionName,"
			+ " (select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@daily_mis where cust_id=fbm.party_code) relatioshipBeginningDate"
			+ " from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr "
			+ " where fbh.event_acid in ( select acid"
			+ "       from gam@live_fin"
			+ "       where gam.gl_sub_head_code in ('31200', '52110')"
			+ "       and gam.acct_crncy_code = pst.crncy_purchsd)"
			+"  and pst.bill_ref_num = fbm.bill_id"
			+ " and pst.sol_id = fbm.sol_id"
			+ " and fbm.bill_id = fbh.bill_id"
			+ " and fbm.sol_id = fbh.sol_id"
			+ " and fbh.vfd_bod_date = pst.tran_date"
			+ " and fbh.tran_id = pst.tran_id"
			+ " and fbm.reg_type = fbr.reg_type"
			+ " and bill_func in ('R','P')"
			+ " and fbm.ps_ind = 'Y'"
			+ " and pst.purpose_of_rem like 'P%'"
			+ " and amt_home_crncy > 500000"
			+ " and pst.crncy_purchsd <> 'INR'"
			+ " and pst.entity_cre_flg = 'Y'"
			+ " and inward_outward_ind = 'O'"
			+ " and fbm.reg_type ='FCNO'";*/
		
		String outwardRocn="select to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
			+ "to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
			+ "pst.tran_id TransactionRefNum,"
			+ "'P' TransactionType,"
			+ "decode (fbr.dflt_reg_sub_type,'TT','E','CHQ','L','SIGHT','Z','USANC','Z','ORG','Z','TC','B','COLL','Z','IMADV','E','DD','C') InstrumentType,"
			+ "(select distinct sol_desc from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2) transactionInsitutionName,"
			+ "(select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2)  transactionInsitutionRefNum,"
			+ "(select distinct a.state_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2) transactionStateCode,"
			+ "nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id  and rownum<2),'IN') transactionCountry_Code,"
			+ "round(amt_home_crncy,0) AmountRupees,"
			+ "round(amt_purchsd,0) AmountForeignCurrency,"
			+ "crncy_purchsd CurrencyOfTransaction,"
			+ "(select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
			+ "pst.purpose_of_rem PurposeCode,"
			+ "nvl((select (case when free_code_1 like 'RL_1'then 'T3' "
			+ " when free_code_1 like 'RL1%'then 'T3' "
			+ " when free_code_1 like 'RL_2'then 'T2' "
			+ " when free_code_1 like 'RL2%'then 'T2' "
			+ " when free_code_1 like 'RL_3'then 'T1' "
			+ " when free_code_1 like 'RL3%'then 'T1' "
			+ " else 'XX' end) from cem@live_fin where cust_id=fbm.party_code),'XX') riskRating,"
			+ " nvl((select cust_name from cmg@live_fin where cust_id=fbm.party_code),'XX') customerName,"
			+ " nvl((select cust_id from cmg@live_fin where cust_id=fbm.party_code),'XX') customerId,"
			+ " nvl((select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code),0) Occupation,"
			+ " nvl((select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') DateOfBirth,"
			+ " nvl((select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code),'X') Gender,"
			+ " nvl((select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code),'IN') Nationality,"
			+ " nvl((select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code),'XXXX') pan,"
			+ " nvl((select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code),'XXXX') address,"
			+ " nvl((select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code),'XX') city,"
			+ " nvl((select decode((select count(1) from sol@live_fin where state_code = a.cust_comu_state_code),0,'ZZ',cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code),'ZZ') state,"
			+ " nvl((select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code),00) pincode,"
			+ " nvl((select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code),'ZZ') countryCode,"
			+ " nvl((select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code),'00000') phone,"
			+ " nvl((select email_id from cmg@live_fin where cust_id=fbm.party_code),'NA') email,"
			+ " (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
			+ "  (select sol_id from gam@live_fin where acid=fbh.oper_acid) accountinstitutionRefNum, "
			+ "	(select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@live_fin where acid=fbh.oper_acid)) institutionName,"
			+ " nvl((select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code),'0001-01-01') relatioshipBeginningDate,"
			+ " 'O' tran_type from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr "
			+ " where fbh.event_acid in ( select acid"
			+ "       from gam@live_fin"
			+ "       where gam.sol_id = '096'"
			+ "       and gam.gl_sub_head_code in ('31200', '52110')"
			+ "       and gam.acct_crncy_code = pst.crncy_purchsd)"
			+"  and pst.bill_ref_num = fbm.bill_id"
			+ " and pst.sol_id = fbm.sol_id"
			+ " and fbm.bill_id = fbh.bill_id"
			+ " and fbm.sol_id = fbh.sol_id"
			+ " and fbh.vfd_bod_date = pst.tran_date"
			+ " and fbh.tran_id = pst.tran_id"
			+ " and fbm.reg_type = fbr.reg_type"
			+ " and bill_func in ('R','P')"
			+ " and fbm.ps_ind = 'Y'"
			+ " and pst.purpose_of_rem like 'S%'"
			+ " and amt_home_crncy > 500000"
			+ " and pst.crncy_purchsd <> 'INR'"
			+ " and pst.entity_cre_flg = 'Y'"
			+ " and inward_outward_ind = 'O'"
			+ " and fbm.reg_type not in ('OREC','FCNO')";
		
		/*String outwardRcn="select to_char(fbh.vfd_bod_date,'YYYY-MM-DD') TransactionDate,"
			+ "to_char(fbh.vfd_sys_date,'HH24:MM:SS') TransactionTime,"
			+ "pst.tran_id TransactionRefNum,"
			+ "'P' TransactionType,"
			+ "'A' instrumentType,"
			+ "(select distinct sol_desc from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id) transactionInsitutionName,"
			+ "(select distinct a.micr_centre_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id)  transactionInsitutionRefNum,"
			+ "(select distinct a.state_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id) transactionStateCode,"
			+ "nvl((select distinct b.cntry_code from sol@live_fin a,bct@live_fin b "
			+ "where a.br_code=b.br_code "
			+ "and sol_id =fbm.sol_id),'IN') transactionCountry_Code,"
			+ "round(amt_home_crncy,0) AmountRupees,"
			+ "round(amt_purchsd,0) AmountForeignCurrency,"
			+ "crncy_purchsd CurrencyOfTransaction,"
			+ "(select ref_desc from rct@live_fin where ref_rec_type = '81' and ref_code = pst.purpose_of_rem and del_flg != 'Y')  PurposeOfTransaction,"
			+ "pst.purpose_of_rem PurposeCode,"
			+ "(select (case when free_code_1 like 'RL_1'then 'T3' "
			+ " when free_code_1 like 'RL1%'then 'T3' "
			+ " when free_code_1 like 'RL_2'then 'T2' "
			+ " when free_code_1 like 'RL2%'then 'T2' "
			+ " when free_code_1 like 'RL_3'then 'T1' "
			+ " when free_code_1 like 'RL3%'then 'T1' "
			+ " else 'XX' end) from cem@live_fin where cust_id=fbm.party_code) riskRating,"
			+ " (select cust_name from cmg@live_fin where cust_id=fbm.party_code) customerName,"
			+ "  (select cust_id from cmg@live_fin where cust_id=fbm.party_code) customerId,"
			+ "  (select cust_occp_code from cmg@live_fin where cust_id=fbm.party_code) Occupation,"
			+ "  (select nvl(to_char(date_of_birth,'YYYY-MM-DD'),'0001-01-01') from cmg@live_fin where cust_id=fbm.party_code) DateOfBirth,"
			+ "  (select nvl(decode(cust_sex,'O','X',cust_sex),'X') from cmg@live_fin where cust_id=fbm.party_code) Gender,"
			+ "  (select nvl(cust_perm_cntry_code,'IN') from cmg@live_fin where cust_id=fbm.party_code) Nationality,"
			+ "  (select pan_gir_num from cmg@live_fin where cust_id=fbm.party_code) pan,"
			+ "  (select nvl(cust_comu_addr1||' '|| cust_comu_addr2,'XXXX') from cmg@live_fin where cust_id=fbm.party_code) address,"
			+ "  (select cust_comu_city_code from cmg@live_fin where cust_id=fbm.party_code) city,"
			+ "  (select decode((select count(1) from sol@live_fin where state_code = a.cust_comu_state_code),0,'ZZ',cust_comu_state_code) from cmg@live_fin a where cust_id=fbm.party_code) state,"
			+ "  (select cust_comu_pin_code from cmg@live_fin where cust_id=fbm.party_code) pincode,"
			+ "  (select cust_comu_cntry_code from cmg@live_fin where cust_id=fbm.party_code) countryCode,"
			+ "  (select cust_comu_phone_num_1 from cmg@live_fin where cust_id=fbm.party_code) phone,"
			+ "  (select email_id from cmg@live_fin where cust_id=fbm.party_code) email,"
			+ "  (select foracid from gam@live_fin where acid=fbh.oper_acid) accountNumber,"
			+ "  (select sol_id from gam@live_fin where acid=fbh.oper_acid) accountinstitutionRefNum, "
			+ "	(select sol_desc from sol@live_fin where sol_id=(select sol_id from gam@daily_mis where acid=fbh.oper_acid)) institutionName,"
			+ " (select nvl(to_char(cust_opn_date,'YYYY-MM-DD'),'0001-01-01') from cmg@daily_mis where cust_id=fbm.party_code) relatioshipBeginningDate"
			+ " from pst@live_fin pst, fbm@live_fin fbm, fbh@live_fin fbh, fbr@live_fin fbr "
			+ " where fbh.event_acid in ( select acid"
			+ "       from gam@live_fin"
			+ "       where gam.gl_sub_head_code in ('31200', '52110')"
			+ "       and gam.acct_crncy_code = pst.crncy_purchsd)"
			+"  and pst.bill_ref_num = fbm.bill_id"
			+ " and pst.sol_id = fbm.sol_id"
			+ " and fbm.bill_id = fbh.bill_id"
			+ " and fbm.sol_id = fbh.sol_id"
			+ " and fbh.vfd_bod_date = pst.tran_date"
			+ " and fbh.tran_id = pst.tran_id"
			+ " and fbm.reg_type = fbr.reg_type"
			+ " and bill_func in ('R','P')"
			+ " and fbm.ps_ind = 'Y'"
			+ " and pst.purpose_of_rem like 'S%'"
			+ " and amt_home_crncy > 500000"
			+ " and pst.crncy_purchsd <> 'INR'"
			+ " and pst.entity_cre_flg = 'Y'"
			+ " and inward_outward_ind = 'O'"
			+ " and fbm.reg_type='FCNO'";*/
		
		String WCCQuery = "SELECT "
 +" to_char(A.TRANSACTIONDATE,'yyyy-mm-dd'),  "
 +"  decode(A.TRANSACTIONTIME,'NA','00:00:00',A.TRANSACTIONTIME), A.TRANSACTIONREFNO, A.TRANSACTIONTYPE, " 
 +"  A.INSTRUMENTTYPE, A.TRANSACTIONINSTITUTIONNAME, A.TRANSACTIONINSTITUTIONREFNUM,  "
 +"  A.TRANSACTIONSTATECODE, A.TRANSACTIONCOUNTRYCODE, "
 +"  A.AMOUNTRUPEES, A.AMOUNTFOREIGNCURRENCY, A.CURRENCYOFTRANSACTION,  "
 +"  A.PURPOSEOFTRANSACTION, A.PURPOSECODE, decode(A.RISKRATING,'T3(High Risk)','T3',A.RISKRATING),  "
 +" A.CUSTOMERNAME, A.CUSTOMERID, A.OCCUPATION,  "
 +"  A.DATEOFBIRTH, A.GENDER, decode(A.NATIONALITY,'INDIAN','IN',A.NATIONALITY),  "
 +"  A.PAN, "
 +"  A.ADDRESS, A.CITY, A.STATECODE,  "
 +"  A.PINCODE, A.COUNTRYCODE, A.TELEPHONE,  "
 +"  A.EMAIL,  "
 +"  A.ACCOUNTNUMBER, (select branch_id from aml_ac_master where cust_Ac_no =A.ACCOUNTNUMBER ) "
 +" FROM AML_CBWT_WCC A"; 
		
		//queryList.add(WCCQuery);
		
		queryList.add(inwardPocn);
		//queryList.add(inwardPcn);
		queryList.add(inwardRocn);
		//queryList.add(inwardRcn);
		queryList.add(outwardPocn);
		//queryList.add(outwardPcn);
		queryList.add(outwardRocn);
		//queryList.add(outwardRcn);
		
		//Getting Date Range
		ArrayList<String> dateRangeList = new ArrayList<String>();
		dateRangeList=cbwt.getDateRange();
		int num=0;
		char chr='@';
		String filename="";
		for(String mainQuery:queryList){
			chr++;
			for(String data:dateRangeList){
				String date[]=data.split("~");
				num++;
				filename=chr+""+num;
				System.out.println("For Date::"+date[0]+"~"+date[1]);
				System.out.println("File Writing in Process...");
				cbwt.createXMLFile(mainQuery,date[0],date[1],filename);
			}
			num=0;
		}
	}
}
