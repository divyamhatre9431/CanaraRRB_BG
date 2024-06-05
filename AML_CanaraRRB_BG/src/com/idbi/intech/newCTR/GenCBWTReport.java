package com.idbi.intech.newCTR;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenCBWTReport {
	
	private static int srl = 1;
	private static Connection conn = null;
	
	public GenCBWTReport(Connection conn) {
		this.conn = conn;
	}
	
	public static String getCurrentMonth() throws Exception {
		String sql = "select to_char(sysdate-30,'MM') as result from dual";
		
		ResultSet rs = null;
		PreparedStatement statement = null;
		String dat = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				dat = rs.getString("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		return dat;
	}
	
	public static String getCurrentYear() throws Exception {
		String sql = "select to_char(sysdate-30,'YYYY') as result from dual";
		ResultSet rs = null;
		PreparedStatement statement = null;
		String dat = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				dat = rs.getString("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		return dat;
	}
	
	public String checkReport(String date) {
		Statement stmt = null;
		ResultSet rs = null;
		String flag = "";
		try {
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("select decode(count(1),0,'N','Y') from cbwt_ctrlfile where month_of_record=to_char(to_date('"
							+ date
							+ "','dd-mm-yy'),'MM') and year_of_record=to_char(to_date('"
							+ date + "','dd-mm-yy'),'YYYY')");
			while (rs.next()) {
				flag = rs.getString(1);
			}
			System.out.println(flag);
		} catch (Exception ex) {
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return flag;
	}
	
	public String genCbwtSeq(String date) {

		Statement stmt = null;
		ResultSet rs = null;
		String ctrseq = "";
		try {
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("select to_char(to_date('"+date+"','dd-mm-yy'),'MMYY') from dual");
			while (rs.next()) {
				ctrseq = rs.getString(1);
			}
			System.out.println("CBWT SeqNo is :" + ctrseq);
		} catch (Exception ex) {
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ctrseq;
	}
	
	public void genCbwtDetails(String fromdate, String enddate, String cbwtseq,String userId) {
		ReadSWIFTTxtFiles rdtxtfile = new ReadSWIFTTxtFiles();
		CallableStatement callstmt = null;
		try {
			/*System.out.println("Uploading the SWIFT files...");
			rdtxtfile.uploadSwiftFiles();
			System.out.println("SWIFT files uploaded successfully...");*/
			
			//Generating CBWT Transaction from msg data inserted
			/*System.out.println("Generating CBWT Transaction data from file...");
			getCbwtTransactionData();
			System.out.println("CBWT Transaction data inserted successfully from file...");*/
			
			/*System.out.println("Generating CBWT Transaction data");
			callstmt = conn.prepareCall("call gen_cbwt_txn('"+fromdate+"','"+enddate+"')");
			callstmt.execute();
			System.out.println("CBWT Transaction data inserted successfully...");
			
			System.out.println("Updating CBWT Transaction data...");
			callstmt = conn.prepareCall("call cbwt_update_txn('"+fromdate+"','"+enddate+"')");
			callstmt.execute();
			System.out.println("CBWT Transaction data updated successfully...");
			
			System.out.println("Updating CBWT Inward Transaction data...");
			callstmt = conn.prepareCall("call cbwt_update_in_txn('"+fromdate+"','"+enddate+"')");
			callstmt.execute();
			System.out.println("CBWT Inward Transaction data updated successfully...");
			
			System.out.println("Updating CBWT Outward Transaction data...");
			callstmt = conn.prepareCall("call cbwt_update_out_txn('"+fromdate+"','"+enddate+"')");
			callstmt.execute();
			System.out.println("CBWT Outward Transaction data updated successfully...");
			
			System.out.println("Updating CBWT Final Transaction data...");
			callstmt = conn.prepareCall("call cbwt_update_final()");
			callstmt.execute();
			System.out.println("CBWT Final Transaction data updated successfully...");
			
			//inserting data in CBWT Core tables
			System.out.println("Generating CBWT Core table data...");
			callstmt = conn.prepareCall("call cbwt_core_gen('" + fromdate+ "','" + enddate + "','" + cbwtseq + "')");
			callstmt.execute();
			System.out.println("CBWT Core table Generated...");*/
			
			// CBWT Control Files
			System.out.println("Generating CBWT Control File...");
			callstmt = conn.prepareCall("call gen_cbwt_ctrl('" + fromdate+ "','" + enddate + "','" + cbwtseq + "','"+userId+"')");
			callstmt.execute();
			System.out.println("CBWT ControlFile Generated...");

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (callstmt != null) {
					callstmt.close();
					callstmt = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	public int getTransactionCount(){
		Statement stmt = null;
		ResultSet rs = null;
		int cnt = 0;
		try {
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("select count(1) from cbwt_transaction where process_flg='R'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}
		} catch (Exception ex) {
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return cnt;
	}
	
	public void getCbwtTransactionData(){
		Statement stmt = null;
		Statement msgDtlsStmt = null;
		Statement insertStmt = null;
		
		ResultSet rs = null;
		ResultSet rsMsgDtls = null;
		
		String msgId ="",senderBic="",receiverBic="",inOutFlg="",msgType="";
		String fldData[]=null;
		String refNo=null,txndate=null,txnCrncy=null,txnAmt=null,senderAcNo=null,senderName=null,senderAddress=null,senderCrsBic=null,recvAcNo=null,recvName=null,recvCrsBic=null,receiverAddress=null;
		try {
			stmt = conn.createStatement();
			msgDtlsStmt = conn.createStatement();
			insertStmt = conn.createStatement();
			
			stmt.executeQuery("truncate table cbwt_transaction");
			
			rs = stmt
					.executeQuery("select msg_id,sender_bic,receiver_bic,in_out_flg,msg_type from cbwt_swift_msg");
			while (rs.next()) {
				msgId=rs.getString("msg_id");
				System.out.println(msgId);
				senderBic=rs.getString("sender_bic");
				receiverBic=rs.getString("receiver_bic");
				inOutFlg=rs.getString("in_out_flg");
				msgType=rs.getString("msg_type");
				refNo="";txndate="";txnCrncy="";txnAmt="";senderAcNo="";senderName="";senderAddress="";senderCrsBic="";recvAcNo="";recvName="";recvCrsBic="";receiverAddress="";
				
				rsMsgDtls = msgDtlsStmt.executeQuery("select fld_no,fld_val from cbwt_swift_fld where msg_id='"+msgId+"'");
				while(rsMsgDtls.next()){
					String fldNo=rsMsgDtls.getString("fld_no");
					String fldVal=rsMsgDtls.getString("fld_val");

					fldVal=fldVal.replace("'","");
					
					if(fldNo.equals("20")){
						refNo=fldVal.trim();
						//System.out.println(refNo);
					}
					
					if(fldNo.equals("32A")){
						fldVal=fldVal.replace(",",".");
						txndate=fldVal.substring(0, 6);
						txnCrncy=fldVal.trim().substring(6, 9);
						txnAmt=fldVal.trim().substring(9,fldVal.length());
						System.out.println(txndate+"~"+txnCrncy+"~"+txnAmt);
					}
					
					fldData=null;
					if(fldNo.equals("50A")){
						fldData=fldVal.split("-");
						if(fldData.length>1){
							senderAcNo=fldData[0].replace("/", "");
							senderName=fldData[1];
							senderAddress="";
							for(int i=2;i<fldData.length;i++){
								senderAddress+=fldData[i];
							}
						}
						System.out.println(senderAcNo+"~"+senderName+"~"+senderAddress);
					}
					
					fldData=null;
					if(fldNo.equals("50F")){
						fldData=fldVal.split("-");
						if(fldData.length>1){
							senderAcNo=fldData[0].replace("/", "");
							senderName=fldData[1];
							senderAddress="";
							for(int i=2;i<fldData.length;i++){
								senderAddress+=fldData[i];
							}
						}
						System.out.println(senderAcNo+"~"+senderName+"~"+senderAddress);
					}
					
					fldData=null;
					if(fldNo.equals("50K")){
						fldData=fldVal.split("-");
						if(fldData.length>1){
							senderAcNo=fldData[0].replace("/", "");
							senderName=fldData[1];
							senderAddress="";
							for(int i=2;i<fldData.length;i++){
								senderAddress+=fldData[i];
							}
						}
						System.out.println(senderAcNo+"~"+senderName+"~"+senderAddress);
					}
					
					if(fldNo.equals("52A")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						senderCrsBic=fldVal.trim();
						//System.out.println(senderCrsBic);
					}
					
					if(fldNo.equals("53A")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						senderCrsBic=fldVal.trim();
						//System.out.println(senderCrsBic);
					}
					
					if(fldNo.equals("53B")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						senderCrsBic=fldVal.trim();
						//System.out.println(senderCrsBic);
					}
					
					if(fldNo.equals("53D")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						senderCrsBic=fldVal.trim();
						//System.out.println(senderCrsBic);
					}
					
					fldData=null;
					if(fldNo.equals("59")){
						fldData=fldVal.split("-");
						if(fldData.length>1){
							recvAcNo=fldData[0].replace("/", "");
							recvName=fldData[1];
							receiverAddress="";
							for(int i=2;i<fldData.length;i++){
								receiverAddress+=fldData[i];
							}
						}
						System.out.println(recvAcNo+"~"+recvName+"~"+receiverAddress);
					}
					
					fldData=null;
					if(fldNo.equals("59A")){
						fldData=fldVal.split("-");
						if(fldData.length>1){
							recvAcNo=fldData[0].replace("/", "");
							recvName=fldData[1];
							receiverAddress="";
							for(int i=2;i<fldData.length;i++){
								receiverAddress+=fldData[i];
							}
						}
						System.out.println(recvAcNo+"~"+recvName+"~"+receiverAddress);
					}
					
					fldData=null;
					if(fldNo.equals("59F")){
						fldData=fldVal.split("~");
						if(fldData.length>1){
							recvAcNo=fldData[0].replace("/", "");
							recvName=fldData[1];
							receiverAddress="";
							for(int i=2;i<fldData.length;i++){
								receiverAddress+=fldData[i];
							}
						}
						
						System.out.println(recvAcNo+"~"+recvName+"~"+receiverAddress);
					}
					
					if(fldNo.equals("54A")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						recvCrsBic=fldVal.trim();
						//System.out.println(recvCrsBic);
					}
					if(fldNo.equals("54B")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						recvCrsBic=fldVal.trim();
						//System.out.println(recvCrsBic);
					}
					if(fldNo.equals("54D")){
						fldVal=fldVal.replace("/", "");
						fldVal=fldVal.substring(1);
						recvCrsBic=fldVal.trim();
						//System.out.println(recvCrsBic);
					}
					
				}
				insertStmt.executeUpdate("insert into cbwt_transaction values('"+msgId+"','"+inOutFlg+"',to_date('"+txndate+"','yy-mm-dd'),trim('"+refNo+"'),'"+txnAmt+"','"+txnCrncy+"',trim('"+senderBic+"'),trim('"+receiverBic+"'),trim('"+senderAcNo+"'),'"+senderName+"','"+senderAddress+"','"+senderCrsBic+"',trim('"+recvAcNo+"'),'"+recvName+"','"+receiverAddress+"','"+recvCrsBic+"',null,null,null,null,'"+msgType+"',null,null,null)");
				senderAddress="";
				receiverAddress="";
				
			}
			conn.commit();
		} catch (Exception ex) {
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
				if (msgDtlsStmt != null) {
					msgDtlsStmt.close();
					msgDtlsStmt = null;
				}
				if (rsMsgDtls != null) {
					rsMsgDtls.close();
					rsMsgDtls = null;
				}
				if (insertStmt != null) {
					insertStmt.close();
					insertStmt = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	
	
	public void createXmlFile(String cbwtseq,int filecount){
		Statement stmt = null;
		Statement totStmt = null;
		Statement billStmt = null;
		Statement txnStmt = null;
		Statement branchStmt = null;
		Statement billProcess = null;
		Statement stmtCustomer = null;
		
		ResultSet rs = null;
		ResultSet rsTot = null;
		ResultSet rsBill = null;
		ResultSet rsTxn = null;
		ResultSet rsBranch = null;
		ResultSet rsCustomer = null;
		
		
		String bankName = "";
		String bankCetegory = "";
		String fiureid = "";
		String poName = "";
		String poDesg = "";
		String poAddress = "";
		String poCity = "";
		String poState = "";
		String poCountry = "";
		String poPin = "";
		String poTel = "";
		String poFax = "";
		String poMobile = "";
		String poEmail = "";
		String batchDate = "";
		String month = "";
		String year = "";
		
		String msgId ="";
		String billId = "";
		String mainPersonName = "";
		String branchBic = "";
		String senderBic = "";
		String receiverBic = "";
		String txnInstituteName = "";
		String txnCountryCode = "";
		String reportingRole ="";
		String branchStateCode="";
		String branchPinCode="";
		String inOutFlg="";
		ArrayList<String> branchList = new ArrayList<String>();
		
		ResourceBundle bundle = ResourceBundle
				.getBundle("com.idbi.intech.newCTR.ReportGen");
		String fileDir = bundle.getString("CBWTDIR");
		StringBuffer sb = null;
		String finalXml = "";
		String outfileDir = "";
		//int filecount = 1;
		String batchNum = cbwtseq;

		int i = srl;
		File file = null;
		FileOutputStream writeXml = null;
		
		try {
			stmt = conn.createStatement();
			totStmt = conn.createStatement();
			billStmt = conn.createStatement();
			txnStmt = conn.createStatement();
			branchStmt  = conn.createStatement();
			billProcess = conn.createStatement();
			stmtCustomer = conn.createStatement();
			
			rsTot = totStmt
					.executeQuery("select to_char(sysdate,'YYYY-MM-DD') batchDate,month_of_record,year_of_record from cbwt_ctrlfile where cbwt_seq_no='"
							+ cbwtseq + "'");
			while (rsTot.next()) {
				batchDate = rsTot.getString(1);
				month = rsTot.getString(2);
				year = rsTot.getString(3);
			}
			rs = stmt
					.executeQuery("select comp_name_of_bank,bank_cetegory,fiureid,po_name,po_designation,po_address,po_city,po_state,po_country,po_pin,po_tel,po_mobile,po_fax,po_email from po_details");
			while (rs.next()) {
				bankName = rs.getString("comp_name_of_bank");
				bankCetegory = rs.getString("bank_cetegory");
				fiureid = rs.getString("fiureid");
				poName = rs.getString("po_name");
				poDesg = rs.getString("po_designation");
				poAddress = rs.getString("po_address");
				poCity = rs.getString("po_city");
				poState = rs.getString("po_state");
				poCountry = rs.getString("po_country");
				poPin = rs.getString("po_pin");
				poTel = rs.getString("po_tel");
				poMobile = rs.getString("po_mobile");
				poFax = rs.getString("po_fax");
				poEmail = rs.getString("po_email");
			}
			sb = new StringBuffer();
			outfileDir = fileDir + "CBWT_EFT" + batchNum + "_" + filecount++
					+ ".xml";
			file = new File(outfileDir);
			writeXml = new FileOutputStream(file);

			boolean poFlg = true;

			//IDBI Bank
			rsBill = billStmt
					.executeQuery("select * from (select msg_id,trim(sender_bic) sender_bic,trim(receiver_bic) receiver_bic,decode(in_out_flg,'I',receiver_name,sender_name) person_name,in_out_flg,process_flg from cbwt_transaction where process_flg='R' ) where process_flg='R' and rownum<2001");
			
			//Allahabad Bank
			//rsBill = billStmt
			//		.executeQuery("select msg_id,sender_bic,receiver_bic,case when in_out_flg='I' then nvl(person_name,receiver_bic) else nvl(person_name,sender_bic) end person_name, in_out_flg,process_flg from (select msg_id,trim(sender_bic) sender_bic,trim(receiver_bic) receiver_bic,decode(in_out_flg,'I',receiver_name,sender_name) person_name,in_out_flg,process_flg from cbwt_transaction where process_flg='R') where process_flg='R'and rownum<2001");
			while (rsBill.next()) {
				
				if (poFlg) {
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					sb.append("<Batch>\n");
					sb.append("<ReportType>EFT</ReportType>\n");
					sb.append("<ReportFormatType>TRF</ReportFormatType>\n");
					sb.append("<BatchHeader>\n");
					sb.append("<DataStructureVersion>2</DataStructureVersion>\n");
					sb.append("<GenerationUtilityVersion></GenerationUtilityVersion>\n");
					sb.append("<DataSource>xml</DataSource>\n");
					sb.append("</BatchHeader>\n");
					sb.append("<ReportingEntity>\n");
					sb.append("<ReportingEntityName>" + bankName
							+ "</ReportingEntityName>\n");
					sb.append("<ReportingEntityCategory>" + bankCetegory
							+ "</ReportingEntityCategory>\n");
					sb.append("<RERegistrationNumber>22</RERegistrationNumber>\n");
					sb.append("<FIUREID>" + fiureid + "</FIUREID>\n");
					sb.append("</ReportingEntity>\n");
					sb.append("<PrincipalOfficer>\n");
					sb.append("<POName>" + poName + "</POName>\n");
					sb.append("<PODesignation>" + poDesg + "</PODesignation>\n");
					sb.append("<POAddress>\n");
					sb.append("<Address>" + poAddress + "</Address>\n");
					sb.append("<City>" + poCity + "</City>\n");
					sb.append("<StateCode>" + poState + "</StateCode>\n");
					sb.append("<PinCode>" + poPin + "</PinCode>\n");
					sb.append("<CountryCode>" + poCountry + "</CountryCode>\n");
					sb.append("</POAddress>\n");
					sb.append("<POPhone>\n");
					sb.append("<Telephone>" + poTel + "</Telephone>\n");
					sb.append("<Mobile>" + poMobile + "</Mobile>\n");
					sb.append("<Fax>" + poFax + "</Fax>\n");
					sb.append("</POPhone>\n");
					sb.append("<POEmail>" + poEmail + "</POEmail>\n");
					sb.append("</PrincipalOfficer>\n");
					sb.append("<BatchDetails>\n");
					sb.append("<BatchNumber>" + batchNum + "</BatchNumber>\n");
					sb.append("<BatchDate>" + batchDate + "</BatchDate>\n");
					sb.append("<MonthOfReport>" + month + "</MonthOfReport>\n");
					sb.append("<YearOfReport>" + year + "</YearOfReport>\n");
					sb.append("<OperationalMode>P</OperationalMode>\n");
					sb.append("<BatchType>N</BatchType>\n");
					sb.append("<OriginalBatchID>0</OriginalBatchID>\n");
					sb.append("<ReasonOfRevision>N</ReasonOfRevision>\n");
					sb.append("<PKICertificateNum></PKICertificateNum>\n");
					sb.append("</BatchDetails>\n");
				}

				poFlg = false;
				
				msgId = rsBill.getString(1);
				//billId = rsBill.getString(2);
				senderBic = rsBill.getString(2);
				receiverBic = rsBill.getString(3);
				mainPersonName = rsBill.getString(4);
				inOutFlg = rsBill.getString(5);
				
				sb.append("<Report>\n");
				sb.append("<ReportSerialNum>" + i + "</ReportSerialNum>\n");
				sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>\n");
				sb.append("<MainPersonName>"+mainPersonName+"</MainPersonName>\n");
				
				//System.out.println(msgId);
				
				if(inOutFlg.equals("I")){
				rsTxn = txnStmt.executeQuery("select to_char(tran_date,'YYYY-MM-DD') tran_date,tran_ref_no,tran_type,instrument_type,tran_inst_name,tran_inst_ref_no,tran_state_code,tran_cntry_code,round(amount_inr,0) amount_inr,round(amount_fc,0) amount_fc,tran_crncy,bank_bic,purpose_code,purpose_desc,cust_id,account_no,related_inst_no,cust_name,cust_address,cust_state_code,cust_cntry_code,reporting_role,br_pincode,ordering_bic" 
							+" from cbwt_inward_sender_new where msg_id='"+msgId+"'"
							+" union all"
							+" select to_char(tran_date,'YYYY-MM-DD') tran_date,tran_ref_no,tran_type,instrument_type,tran_inst_name,tran_inst_ref_no,tran_state_code,tran_cntry_code,round(amount_inr,0) amount_inr,round(amount_fc,0) amount_fc ,tran_crncy,bank_bic,purpose_code,purpose_desc,cust_id,account_no,related_inst_no,cust_name,cust_address,cust_state_code,cust_cntry_code,reporting_role,br_pincode,ordering_bic"
							+" from cbwt_inward_receiver_new where msg_id='"+msgId+"'");
				}else{
					rsTxn = txnStmt.executeQuery("select to_char(tran_date,'YYYY-MM-DD') tran_date,tran_ref_no,tran_type,instrument_type,tran_inst_name,tran_inst_ref_no,tran_state_code,tran_cntry_code,round(amount_inr,0) amount_inr,round(amount_fc,0) amount_fc,tran_crncy,bank_bic,purpose_code,purpose_desc,cust_id,account_no,related_inst_no,cust_name,cust_address,cust_state_code,cust_cntry_code,reporting_role,br_pincode,ordering_bic" 
							+" from cbwt_outward_sender_new where msg_id='"+msgId+"'"
							+" union all"
							+" select to_char(tran_date,'YYYY-MM-DD') tran_date,tran_ref_no,tran_type,instrument_type,tran_inst_name,tran_inst_ref_no,tran_state_code,tran_cntry_code,round(amount_inr,0) amount_inr,round(amount_fc,0) amount_fc ,tran_crncy,bank_bic,purpose_code,purpose_desc,cust_id,account_no,related_inst_no,cust_name,cust_address,cust_state_code,cust_cntry_code,reporting_role,br_pincode,ordering_bic"
							+" from cbwt_outward_receiver_new where msg_id='"+msgId+"'");
				}
				
				while(rsTxn.next()){
					//System.out.println("Inside::"+msgId);
				sb.append("<Transaction>\n");
				sb.append("<TransactionDate>"+rsTxn.getString("tran_date")+"</TransactionDate>\n");
				sb.append("<TransactionTime>00:00:00</TransactionTime>\n");
				sb.append("<TransactionRefNum>"+rsTxn.getString("tran_ref_no")+"</TransactionRefNum>\n");
				sb.append("<TransactionType>"+rsTxn.getString("tran_type")+"</TransactionType>\n");
				sb.append("<InstrumentType>E</InstrumentType>\n");
				
				branchBic=rsTxn.getString("ordering_bic");
				//System.out.println(branchBic);
				reportingRole=rsTxn.getString("reporting_role");
				branchStateCode=rsTxn.getString("tran_state_code");
				branchPinCode=rsTxn.getString("br_pincode");
				branchList.add(branchBic.trim()+"~"+reportingRole+"~"+branchStateCode+"~"+branchPinCode);
				rsBranch = branchStmt.executeQuery("select ic_bank_name,ic_branch_addrs,ic_city_name,ic_country_code from bank_bic_details where ic_id_code=trim('"+branchBic+"')");
				while(rsBranch.next()){
					txnInstituteName = rsBranch.getString("ic_bank_name");
					txnCountryCode =  rsBranch.getString("ic_country_code");
				}
				
				//System.out.println(txnInstituteName);
				rsBranch.close();
				rsBranch = null;
				
				sb.append("<TransactionInstitutionName>"+rsTxn.getString("tran_inst_name")+"</TransactionInstitutionName>\n");
				sb.append("<TransactionInstitutionRefNum>"+rsTxn.getString("tran_inst_ref_no")+"</TransactionInstitutionRefNum>\n");
				sb.append("<TransactionStateCode>"+rsTxn.getString("tran_state_code")+"</TransactionStateCode>\n");
				sb.append("<TransactionCountryCode>"+rsTxn.getString("tran_cntry_code")+"</TransactionCountryCode>\n");
				sb.append("<PaymentInstrumentNum></PaymentInstrumentNum>\n");
				sb.append("<PaymentInstrumentIssueInstitutionName></PaymentInstrumentIssueInstitutionName>\n");
				sb.append("<InstrumentIssueInstitutionRefNum></InstrumentIssueInstitutionRefNum>\n");
				sb.append("<InstrumentCountryCode>"+rsTxn.getString("tran_cntry_code")+"</InstrumentCountryCode>\n");
				sb.append("<AmountRupees>"+rsTxn.getString("amount_inr")+"</AmountRupees>\n");
				sb.append("<AmountForeignCurrency>"+rsTxn.getString("amount_fc")+"</AmountForeignCurrency>\n");
				sb.append("<CurrencyOfTransaction>"+rsTxn.getString("tran_crncy")+"</CurrencyOfTransaction>\n");
				sb.append("<PurposeOfTransaction>"+rsTxn.getString("purpose_desc")+"</PurposeOfTransaction>\n");
				sb.append("<PurposeCode>"+rsTxn.getString("purpose_code")+"</PurposeCode>\n");
				sb.append("<RiskRating>XX</RiskRating>\n");
				
				String custId = rsTxn.getString("cust_id") == null ? "NA"
						: rsTxn.getString("cust_id");
				
				String acNo = rsTxn.getString("account_no");
				if(acNo!=null){
					acNo=acNo.replace("/","");
					if(acNo.length()>20){
						acNo=acNo.substring(0, 19);
					}
				}
				
				if (!custId.equals("NA")) {
					
					int checkCust = 0;
					rsCustomer = stmtCustomer
							.executeQuery("select count(1) from aml_cust_master where cust_id=lpad(trim('"+ custId + "'),9,' ')");
					while (rsCustomer.next()) {
						checkCust = rsCustomer.getInt(1);
					}

					rsCustomer.close();
					if (checkCust > 0) {
						rsCustomer = stmtCustomer.executeQuery("select cust_id,cust_name,(select ref_desc from aml_rct where ref_rec_type='21' and ref_code=cust_occp_code) occupation," + 
								"nvl(decode(cust_sex,'O','X',cust_sex),'X') gender," + 
								"nvl(to_char(cust_dob,'YYYY-MM-DD'),null) dateofbirth," + 
								"nvl(cust_commu_cntry_code,'XX') nationality," + 
								"decode(cust_pan_no,'FORM60',null,'PANNOTAVBL',null,'PANNOTREQD',null,'PANINVALID',null,cust_pan_no) cust_pan_no," + 
								"(cust_commu_addr1||''||cust_commu_addr2||(select ref_desc from aml_rct where ref_rec_type='01' and ref_code=cust_commu_city_code)||cust_commu_state_code||cust_commu_cntry_code) cust_address," + 
								"(select ref_desc from aml_rct where ref_rec_type='01' and ref_code=cust_commu_city_code) city," + 
								"nvl(decode(cust_commu_state_code,'.','XX','TEL','TG','UT','UA','ND','DL','HARYA','HR','UTK','UA',cust_commu_state_code),'XX') state," + 
								"nvl(cust_commu_pin_code,'') pincode,nvl(cust_commu_cntry_code,'XX') cntrycode " + 
								"from aml_cust_master where cust_id=lpad(trim('"+ custId + "'),9,' ')");
						while (rsCustomer.next()) {
							sb.append("<CustomerDetails>\n");
							sb.append("<CustomerName>"+rsCustomer.getString("cust_name")+"</CustomerName>\n");
							sb.append("<CustomerId>");
							sb.append(rsCustomer.getString("cust_id")==null?"":rsCustomer.getString("cust_id"));
							sb.append("</CustomerId>\n");
							sb.append("<Occupation>");
							sb.append(rsCustomer.getString("occupation")==null?"":rsCustomer.getString("occupation"));
							sb.append("</Occupation>\n");
							if(rsCustomer.getString("dateofbirth")!=null){
								sb.append("<DateOfBirth>");
								sb.append(rsCustomer.getString("dateofbirth")==null?"":rsCustomer.getString("dateofbirth"));
								sb.append("</DateOfBirth>\n");
							}
							sb.append("<Gender>");
							sb.append(rsCustomer.getString("gender")==null?"X":rsCustomer.getString("gender"));
							sb.append("</Gender>\n");
							sb.append("<Nationality>");
							sb.append(rsCustomer.getString("nationality")==null?"X":rsCustomer.getString("nationality"));
							sb.append("</Nationality>\n");
							if(rsCustomer.getString("cust_pan_no")!=null){
								
								sb.append("<IdentificationType>");
								sb.append(rsCustomer.getString("cust_pan_no")==null?"":"C");
								sb.append("</IdentificationType>\n");
								sb.append("<IdentificationNumber>");
								sb.append(rsCustomer.getString("cust_pan_no")==null?"":rsCustomer.getString("cust_pan_no"));
								sb.append("</IdentificationNumber>\n");
								sb.append("<IssuingAuthority></IssuingAuthority>\n");
								sb.append("<PlaceOfIssue></PlaceOfIssue>\n");
								sb.append("<PAN>");
								sb.append(rsCustomer.getString("cust_pan_no")==null?"":rsCustomer.getString("cust_pan_no"));
								sb.append("</PAN>\n");
								sb.append("<UIN></UIN>\n");
							}
							sb.append("<CustomerAddress>\n");
							sb.append("<Address>"+rsCustomer.getString("cust_address")+"</Address>\n");
							sb.append("<City>");
							sb.append(rsCustomer.getString("city")==null?"":rsCustomer.getString("city"));
							sb.append("</City>\n");
							sb.append("<StateCode>");
							//sb.append(checkState().contains(rsCustomer.getString("state"))?rsCustomer.getString("state"):"XX");
							sb.append(rsCustomer.getString("state").length()==3?"XX":checkState().contains(rsCustomer.getString("state"))?rsCustomer.getString("state"):"XX");
							sb.append("</StateCode>\n");
							sb.append("<PinCode>");
							sb.append(rsCustomer.getString("pincode")==null?"":rsCustomer.getString("pincode"));
							sb.append("</PinCode>\n");
							sb.append("<CountryCode>");
							sb.append(checkCountry().contains(rsCustomer.getString("cntrycode"))?rsCustomer.getString("cntrycode"):"XX");
							sb.append("</CountryCode>\n");
							sb.append("</CustomerAddress>\n");
							sb.append("<Phone>\n");
							sb.append("<Telephone></Telephone>\n");
							sb.append("<Mobile></Mobile>\n");
							sb.append("<Fax></Fax>\n");
							sb.append("</Phone>\n");
							sb.append("<Email></Email>\n");
							sb.append("</CustomerDetails>\n");
						}
					}else {
						sb.append("<CustomerDetails>\n");
						sb.append("<CustomerName>"+rsTxn.getString("cust_name")+"</CustomerName>\n");
						sb.append("<CustomerId>");
						sb.append(rsTxn.getString("cust_id")==null?"":rsTxn.getString("cust_id"));
						sb.append("</CustomerId>\n");
//						sb.append("<Occupation></Occupation>\n");
//						sb.append("<DateOfBirth></DateOfBirth>\n");
//						sb.append("<Gender></Gender>\n");
//						sb.append("<Nationality></Nationality>\n");
//						sb.append("<IdentificationType>Z</IdentificationType>\n");
//						sb.append("<IdentificationNumber></IdentificationNumber>\n");
//						sb.append("<IssuingAuthority></IssuingAuthority>\n");
//						sb.append("<PlaceOfIssue></PlaceOfIssue>\n");
//						sb.append("<PAN></PAN>\n");
//						sb.append("<UIN></UIN>\n");
						sb.append("<CustomerAddress>\n");
						sb.append("<Address>");
						sb.append(rsTxn.getString("cust_address")==null?(branchBic+" "+txnInstituteName+" "+acNo):rsTxn.getString("cust_address").trim().length()<15?(branchBic+" "+txnInstituteName+" "+acNo):rsTxn.getString("cust_address"));
						sb.append("</Address>\n");
						sb.append("<City></City>\n");
						sb.append("<StateCode>XX</StateCode>\n");
						sb.append("<PinCode></PinCode>\n");
						sb.append("<CountryCode>");
						sb.append((checkCountry().contains(rsTxn.getString("cust_cntry_code"))?rsTxn.getString("cust_cntry_code"):"XX"));
						sb.append("</CountryCode>\n");
						sb.append("</CustomerAddress>\n");
						sb.append("<Phone>\n");
						sb.append("<Telephone></Telephone>\n");
						sb.append("<Mobile></Mobile>\n");
						sb.append("<Fax></Fax>\n");
						sb.append("</Phone>\n");
						sb.append("<Email></Email>\n");
						sb.append("</CustomerDetails>\n");
					}
				
				} else {
					sb.append("<CustomerDetails>\n");
					sb.append("<CustomerName>"+rsTxn.getString("cust_name")+"</CustomerName>\n");
					sb.append("<CustomerId>");
					sb.append(rsTxn.getString("cust_id")==null?"":rsTxn.getString("cust_id"));
					sb.append("</CustomerId>\n");
//					sb.append("<Occupation></Occupation>\n");
//					sb.append("<DateOfBirth></DateOfBirth>\n");
//					sb.append("<Gender></Gender>\n");
//					sb.append("<Nationality></Nationality>\n");
//					sb.append("<IdentificationType>Z</IdentificationType>\n");
//					sb.append("<IdentificationNumber></IdentificationNumber>\n");
//					sb.append("<IssuingAuthority></IssuingAuthority>\n");
//					sb.append("<PlaceOfIssue></PlaceOfIssue>\n");
//					sb.append("<PAN></PAN>\n");
//					sb.append("<UIN></UIN>\n");
					sb.append("<CustomerAddress>\n");
					sb.append("<Address>");
					sb.append(rsTxn.getString("cust_address")==null?(branchBic+" "+txnInstituteName+" "+acNo):rsTxn.getString("cust_address").trim().length()<15?(branchBic+" "+txnInstituteName+" "+acNo):rsTxn.getString("cust_address"));
					sb.append("</Address>\n");
					sb.append("<City></City>\n");
					sb.append("<StateCode>XX</StateCode>\n");
					sb.append("<PinCode></PinCode>\n");
					sb.append("<CountryCode>");
					sb.append((checkCountry().contains(rsTxn.getString("cust_cntry_code"))?rsTxn.getString("cust_cntry_code"):"XX"));
					sb.append("</CountryCode>\n");
					sb.append("</CustomerAddress>\n");
					sb.append("<Phone>\n");
					sb.append("<Telephone></Telephone>\n");
					sb.append("<Mobile></Mobile>\n");
					sb.append("<Fax></Fax>\n");
					sb.append("</Phone>\n");
					sb.append("<Email></Email>\n");
					sb.append("</CustomerDetails>\n");
				}
				
				sb.append("<AccountNumber>");
				sb.append(acNo==null?"":acNo);
				sb.append("</AccountNumber>\n");
				sb.append("<AccountWithInstitutionName>"+txnInstituteName+"</AccountWithInstitutionName>\n");
				sb.append("<AccountWithInstitutionRefNum>"+branchBic+"</AccountWithInstitutionRefNum>\n");
				sb.append("<RelatedInstitutionName></RelatedInstitutionName>\n");
				sb.append("<InstitutionRelationFlag>X</InstitutionRelationFlag>\n");
				sb.append("<RelatedInstitutionRefNum></RelatedInstitutionRefNum>\n");
				sb.append("<Remarks></Remarks>\n");
				
				sb.append("</Transaction>\n");
				
				}
				
				branchBic="";
				reportingRole="";
				
				for(String branchDtls : branchList){	
					//System.out.println(branchDtls);
					String branch[]=branchDtls.split("~");
					rsBranch = branchStmt.executeQuery("select ic_bank_name,ic_branch_addrs,ic_city_name,ic_country_code from bank_bic_details where ic_id_code=trim('"+branch[0]+"')");
				
					while(rsBranch.next()){		
						sb.append("<Branch>\n");
						sb.append("<InstitutionName>");
						sb.append(rsBranch.getString("ic_bank_name").length()>79?rsBranch.getString("ic_bank_name").substring(0, 79):rsBranch.getString("ic_bank_name"));
						sb.append("</InstitutionName>\n");
						sb.append("<InstitutionBranchName></InstitutionBranchName>\n");
						sb.append("<InstitutionRefNum>"+branch[0].trim()+"</InstitutionRefNum>\n");
						sb.append("<ReportingRole>"+branch[1]+"</ReportingRole>\n");
						sb.append("<BIC>"+branch[0].trim()+"</BIC>\n");
						sb.append("<BranchAddress>\n");
						sb.append("<Address>"+rsBranch.getString("ic_branch_addrs")+" "+rsBranch.getString("ic_city_name")+" "+rsBranch.getString("ic_country_code")+"</Address>\n");
						sb.append("<City>"+rsBranch.getString("ic_city_name")+"</City>\n");
						sb.append("<StateCode>"+branch[2].trim()+"</StateCode>\n");
						sb.append("<PinCode>");
						sb.append(branch[3].trim()==null?"":branch[3].trim().equalsIgnoreCase("null")?"":branch[3].trim());
						sb.append("</PinCode>\n");
						sb.append("<CountryCode>"+rsBranch.getString("ic_country_code")+"</CountryCode>\n");
						sb.append("</BranchAddress>\n");
						sb.append("<Phone>\n");
						sb.append("<Telephone></Telephone>\n");
						sb.append("<Mobile></Mobile>\n");
						sb.append("<Fax></Fax>\n");
						sb.append("</Phone>\n");
						sb.append("<Email></Email>\n");
						sb.append("<Remarks></Remarks>\n");
					
						sb.append("</Branch>\n");
					}
					
				}
				branchList.clear();
				
				
				sb.append("</Report>\n");
				i++;
				
				billProcess.executeUpdate("update cbwt_transaction set process_flg = 'Y' where msg_id = '"+msgId+"'");
			}
			conn.commit();
			
			sb.append("</Batch>");
			finalXml = sb.toString().replace("&", " &amp; ");
			String xml = new String(finalXml.getBytes("iso-8859-1"), "UTF-8");
			writeXml.write(xml.getBytes());
			writeXml.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try{
			if(rs!=null){
				rs.close();
				rs=null;
			}
			if(rsBill!=null){
				rsBill.close();
				rsBill=null;
			}
			if(rsTot!=null){
				rsTot.close();
				rsTot=null;
			}
			if(rsBranch!=null){
				rsBranch.close();
				rsBranch=null;
			}
			if(stmt!=null){
				stmt.close();
				stmt=null;
			}
			if(totStmt!=null){
				totStmt.close();
				totStmt=null;
			}
			if(branchStmt!=null){
				branchStmt.close();
				branchStmt=null;
			}
			if(txnStmt!=null){
				txnStmt.close();
				txnStmt=null;
			}
			if(billStmt!=null){
				billStmt.close();
				billStmt=null;
			}
			if(billProcess!=null){
				billProcess.close();
				billProcess=null;
			}
			if(stmtCustomer!=null){
				stmtCustomer.close();
				stmtCustomer=null;
			}
			if(rsCustomer!=null){
				rsCustomer.close();
				rsCustomer=null;
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		srl = i;
	
	}
	
	public ArrayList<String> checkCountry() {
		ArrayList<String> arrctry = new ArrayList<String>();

		arrctry.add("AF");
		arrctry.add("AX");
		arrctry.add("AL");
		arrctry.add("DZ");
		arrctry.add("AS");
		arrctry.add("AD");
		arrctry.add("AO");
		arrctry.add("AI");
		arrctry.add("AQ");
		arrctry.add("AG");
		arrctry.add("AR");
		arrctry.add("AM");
		arrctry.add("AW");
		arrctry.add("AU");
		arrctry.add("AT");
		arrctry.add("AZ");
		arrctry.add("BS");
		arrctry.add("BH");
		arrctry.add("BD");
		arrctry.add("BB");
		arrctry.add("BY");
		arrctry.add("BE");
		arrctry.add("BZ");
		arrctry.add("BJ");
		arrctry.add("BM");
		arrctry.add("BT");
		arrctry.add("BO");
		arrctry.add("BA");
		arrctry.add("BW");
		arrctry.add("BV");
		arrctry.add("BR");
		arrctry.add("IO");
		arrctry.add("BN");
		arrctry.add("BG");
		arrctry.add("BF");
		arrctry.add("BI");
		arrctry.add("KH");
		arrctry.add("CM");
		arrctry.add("CA");
		arrctry.add("CV");
		arrctry.add("KY");
		arrctry.add("CF");
		arrctry.add("TD");
		arrctry.add("CL");
		arrctry.add("CN");
		arrctry.add("CX");
		arrctry.add("CC");
		arrctry.add("CO");
		arrctry.add("KM");
		arrctry.add("CG");
		arrctry.add("CD");
		arrctry.add("CK");
		arrctry.add("CR");
		arrctry.add("CI");
		arrctry.add("HR");
		arrctry.add("CU");
		arrctry.add("CY");
		arrctry.add("CZ");
		arrctry.add("DK");
		arrctry.add("DJ");
		arrctry.add("DM");
		arrctry.add("DO");
		arrctry.add("EC");
		arrctry.add("EG");
		arrctry.add("SV");
		arrctry.add("GQ");
		arrctry.add("ER");
		arrctry.add("EE");
		arrctry.add("ET");
		arrctry.add("FK");
		arrctry.add("FO");
		arrctry.add("FJ");
		arrctry.add("FI");
		arrctry.add("FR");
		arrctry.add("GF");
		arrctry.add("PF");
		arrctry.add("TF");
		arrctry.add("GA");
		arrctry.add("GM");
		arrctry.add("GE");
		arrctry.add("DE");
		arrctry.add("GH");
		arrctry.add("GI");
		arrctry.add("GR");
		arrctry.add("GL");
		arrctry.add("GD");
		arrctry.add("GP");
		arrctry.add("GU");
		arrctry.add("GT");
		arrctry.add("GG");
		arrctry.add("GN");
		arrctry.add("GW");
		arrctry.add("GY");
		arrctry.add("HT");
		arrctry.add("HM");
		arrctry.add("VA");
		arrctry.add("HN");
		arrctry.add("HK");
		arrctry.add("HU");
		arrctry.add("IS");
		arrctry.add("IN");
		arrctry.add("ID");
		arrctry.add("IR");
		arrctry.add("IQ");
		arrctry.add("IE");
		arrctry.add("IM");
		arrctry.add("IL");
		arrctry.add("IT");
		arrctry.add("JM");
		arrctry.add("JP");
		arrctry.add("JE");
		arrctry.add("JO");
		arrctry.add("KZ");
		arrctry.add("KE");
		arrctry.add("KI");
		arrctry.add("KP");
		arrctry.add("KR");
		arrctry.add("KW");
		arrctry.add("KG");
		arrctry.add("LA");
		arrctry.add("LV");
		arrctry.add("LB");
		arrctry.add("LS");
		arrctry.add("LR");
		arrctry.add("LY");
		arrctry.add("LI");
		arrctry.add("LT");
		arrctry.add("LU");
		arrctry.add("MO");
		arrctry.add("MK");
		arrctry.add("MG");
		arrctry.add("MW");
		arrctry.add("MY");
		arrctry.add("MV");
		arrctry.add("ML");
		arrctry.add("MT");
		arrctry.add("MH");
		arrctry.add("MQ");
		arrctry.add("MR");
		arrctry.add("MU");
		arrctry.add("YT");
		arrctry.add("MX");
		arrctry.add("FM");
		arrctry.add("MD");
		arrctry.add("MC");
		arrctry.add("MN");
		arrctry.add("ME");
		arrctry.add("MS");
		arrctry.add("MA");
		arrctry.add("MZ");
		arrctry.add("MM");
		arrctry.add("NA");
		arrctry.add("NR");
		arrctry.add("NP");
		arrctry.add("NL");
		arrctry.add("AN");
		arrctry.add("NC");
		arrctry.add("NZ");
		arrctry.add("NI");
		arrctry.add("NE");
		arrctry.add("NG");
		arrctry.add("NU");
		arrctry.add("NF");
		arrctry.add("MP");
		arrctry.add("NO");
		arrctry.add("OM");
		arrctry.add("PK");
		arrctry.add("PW");
		arrctry.add("PS");
		arrctry.add("PA");
		arrctry.add("PG");
		arrctry.add("PY");
		arrctry.add("PE");
		arrctry.add("PH");
		arrctry.add("PN");
		arrctry.add("PL");
		arrctry.add("PT");
		arrctry.add("PR");
		arrctry.add("QA");
		arrctry.add("RE");
		arrctry.add("RO");
		arrctry.add("RU");
		arrctry.add("RW");
		arrctry.add("BL");
		arrctry.add("SH");
		arrctry.add("KN");
		arrctry.add("LC");
		arrctry.add("MF");
		arrctry.add("PM");
		arrctry.add("VC");
		arrctry.add("WS");
		arrctry.add("SM");
		arrctry.add("ST");
		arrctry.add("SA");
		arrctry.add("SN");
		arrctry.add("RS");
		arrctry.add("SC");
		arrctry.add("SL");
		arrctry.add("SG");
		arrctry.add("SK");
		arrctry.add("SI");
		arrctry.add("SB");
		arrctry.add("SO");
		arrctry.add("ZA");
		arrctry.add("GS");
		arrctry.add("ES");
		arrctry.add("LK");
		arrctry.add("SD");
		arrctry.add("SR");
		arrctry.add("SJ");
		arrctry.add("SZ");
		arrctry.add("SE");
		arrctry.add("CH");
		arrctry.add("SY");
		arrctry.add("TW");
		arrctry.add("TJ");
		arrctry.add("TZ");
		arrctry.add("TH");
		arrctry.add("TL");
		arrctry.add("TG");
		arrctry.add("TK");
		arrctry.add("TO");
		arrctry.add("TT");
		arrctry.add("TN");
		arrctry.add("TR");
		arrctry.add("TM");
		arrctry.add("TC");
		arrctry.add("TV");
		arrctry.add("UG");
		arrctry.add("UA");
		arrctry.add("AE");
		arrctry.add("GB");
		arrctry.add("US");
		arrctry.add("UM");
		arrctry.add("UY");
		arrctry.add("UZ");
		arrctry.add("VU");
		arrctry.add("VE");
		arrctry.add("VN");
		arrctry.add("VG");
		arrctry.add("VI");
		arrctry.add("WF");
		arrctry.add("EH");
		arrctry.add("YE");
		arrctry.add("ZM");
		arrctry.add("ZW");
		arrctry.add("XX");
		arrctry.add("ZZ");

		return arrctry;

	}
	
	public ArrayList<String> checkState() {
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
		//statecode.add("UK");
		statecode.add("TG");
		statecode.add("XX");
		statecode.add("ZZ");

		return statecode;
	}

}
