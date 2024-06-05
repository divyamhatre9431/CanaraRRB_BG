package com.idbi.intech.iaml.misc;

import java.awt.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.rulethread.RuleThread;
import com.idbi.intech.iaml.screening.CreateScanThread;
import com.sun.xml.internal.ws.api.ha.StickyFeature;


public class EodBodReport {
	
	public void generateMisReport(){
		
		ResourceBundle bundle = ResourceBundle
				.getBundle("com.idbi.intech.iaml.misc.email");
		String from = bundle.getString("EMAILID");
		String password = bundle.getString("PASSWORD");
		String host = bundle.getString("SMTPSERVER");
		
		ArrayList<String> arrTo = new ArrayList<String>();
		ArrayList<String> arrToIDBI = new ArrayList<String>();
		ArrayList<String> arrToINTECH = new ArrayList<String>();
		
		String ruleBod="";
		String ruleGen="";
		String ruleAtt="";
		String ruleEod="";
		
		String sdnBod="";
		String sdnGen="";
		String sdnAtt="";
		String sdnEod="";
		
		String swiftBod="";
		String swiftGen="";
		String swiftAtt="";
		String swiftEod="";	
		
		String dmatBod="";
		String dmatGen="";
		String dmatAtt="";
		String dmatEod="";
		
		String bodTot="";
		String genTot="";
		String attTot="";
		String eodTot="";
		
		String eodDate="";
		
		int verify_cnt=0;
		int reject_cnt=0;
		int approved_cnt=0;
		int disposed_cnt=0;
		int process_cnt=0;
		int str_total=0;
		
		int ruleTktAtt=0;
		int ruleAlrtAtt=0;
		int sdnTktAtt=0;
		int sdnAlrtAtt=0;
		int swiftTktAtt=0;
		int swiftAltAtt=0;
		int dmatTktAtt=0;
		int dmatAltAtt=0;
		int totTktAtt=0;
		int totAltAtt=0;
		int ruleAlrtGen=0;
		int ruleTktGen=0;
		int sdnAlrtGen=0;
		int sdnTktGen=0;
		int swiftTktGen=0;
		int swiftAlrtGen=0; 
		int dmatTktGen=0;
		int dmatAlrtGen=0;
		int totTktGen=0;
		int totAlrtGen=0;
		int difference=0;
		int ruleChngEod=0;
		int totalTktEod=0;
		int totalAltEod=0;
		int totalGenEod=0;
		String misDate=null;
		String updateQuery=null;
		

		boolean flg=false;		
		String misHtml="";
		String query="";
		String data="";
		int totPend=0;
		String penDtls[]=null;
		Connection connection = null;
		Statement stmt = null;
		Statement updateStmt = null;
		Statement userStmt =null;
		Statement dateStmt=null;
		ResultSet rs = null;
		ResultSet userRs =null;
		ResultSet rsDate = null;
		//ResultSet dateRs =null;
		ArrayList<String> arrList=new ArrayList<String>();
		ArrayList<String> ruleList=new ArrayList<String>();
		try{
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
			stmt = connection.createStatement();
			userStmt = connection.createStatement();
			
			query="select * from aml_eod_mis where to_date(aml_eod,'dd-mm-yy')=to_date(sysdate-1,'dd-mm-yy')";
			rs=stmt.executeQuery(query);
			while(rs.next()){
				ruleBod="T-"+rs.getString(1)+" / A-"+rs.getString(5);
				ruleGen="T-"+rs.getString(2)+" / A-"+rs.getString(6);
				ruleAtt="T-"+rs.getString(3)+" / A-"+rs.getString(7);
				ruleEod="T-"+rs.getString(4)+" / A-"+rs.getString(8);
				
				sdnBod="T-"+rs.getString(9)+" / A-"+rs.getString(13);
				sdnGen="T-"+rs.getString(10)+" / A-"+rs.getString(14);
				sdnAtt="T-"+rs.getString(11)+" / A-"+rs.getString(15);
				sdnEod="T-"+rs.getString(12)+" / A-"+rs.getString(16);
				
				swiftBod="T-"+rs.getString(17)+" / A-"+rs.getString(21);
				swiftGen="T-"+rs.getString(18)+" / A-"+rs.getString(22);
				swiftAtt="T-"+rs.getString(19)+" / A-"+rs.getString(23);
				swiftEod="T-"+rs.getString(20)+" / A-"+rs.getString(24);
				
				dmatBod="T-"+rs.getString(25)+" / A-"+rs.getString(29);
				dmatGen="T-"+rs.getString(26)+" / A-"+rs.getString(30);
				dmatAtt="T-"+rs.getString(27)+" / A-"+rs.getString(31);
				dmatEod="T-"+rs.getString(28)+" / A-"+rs.getString(32);
				
				bodTot="T-"+rs.getString(33)+" / A-"+rs.getString(37);
				genTot="T-"+rs.getString(34)+" / A-"+rs.getString(38);
				attTot="T-"+rs.getString(35)+" / A-"+rs.getString(39);
				eodTot="T-"+rs.getString(36)+" / A-"+rs.getString(40);
				eodDate=rs.getString(41);
				
			}
			userRs =userStmt.executeQuery("select * from aml_user_mis");
			while(userRs.next()){
				data=userRs.getString(1);
				arrList.add(data);
			}
			query="select status,count(1) cnt from aml_str_request where to_date(make_time,'dd-mm-yy')=to_date(sysdate-1,'dd-mm-yy') group by status";
			rs=stmt.executeQuery(query);
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase("V")){
					verify_cnt=rs.getInt(2);
				}
				else if(rs.getString(1).equalsIgnoreCase("R")){
					reject_cnt=rs.getInt(2);
				}
				else if(rs.getString(1).equalsIgnoreCase("A")){
					approved_cnt=rs.getInt(2);
				}
				else if(rs.getString(1).equalsIgnoreCase("D")){
					disposed_cnt=rs.getInt(2);
				}
				else{
					process_cnt+=process_cnt+rs.getInt(2);
				}
			}
			str_total=verify_cnt+reject_cnt+approved_cnt+disposed_cnt+process_cnt;
			
			query="select rule_id||'~'||rule_desc||'~'||cnt from (select rule_id,rule_desc,count(1) cnt from aml_rule_ticket" 
				+" where generated_time between to_date(to_char(sysdate-1,'dd-mon-yyyy')||' 00:00:00','dd-mm-yy hh24:mi:ss') and to_date(to_char(sysdate-1,'dd-mon-yyyy')||' 23:59:59','dd-mm-yy hh24:mi:ss')"
				+" group by rule_id,rule_desc"
				+" order by count(1) desc) where rownum<=5";
			rs=stmt.executeQuery(query);
			while(rs.next()){
				ruleList.add(rs.getString(1));
			}
			dateStmt=connection.createStatement();
			rsDate=dateStmt.executeQuery("select to_date(sysdate-1) from dual");
			while(rsDate.next()){
				misDate=rsDate.getString(1);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (userRs != null) {
					userRs.close();
					userRs = null;
				}
				if (userStmt != null) {
					userStmt.close();
					userStmt = null;
				}
				
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		
		
		
		ruleBod.trim().replaceAll("\\s","");
		String ruleBodArr[]=ruleBod.split("-|\\/");
		
		ruleGen.trim().replaceAll("\\s","");
		String ruleGenArr[]=ruleGen.split("-|\\/");
		
		ruleEod.trim().replaceAll("\\s","");
		String ruleEodArr[]=ruleEod.split("-|\\/");	
		
		ruleAtt.trim().replaceAll("\\s","");
		String ruleAttArr[]=ruleAtt.split("-|\\/");
		
		sdnBod.trim().replaceAll("\\s","");
		String sdnBodArr[]=sdnBod.split("-|\\/");
		
		sdnGen.trim().replaceAll("\\s","");
		String sdnGenArr[]=sdnGen.split("-|\\/");
		
		sdnEod.trim().replaceAll("\\s","");
		String sdnEodArr[]=sdnEod.split("-|\\/");
		
		sdnAtt.trim().replaceAll("\\s","");
		String sdnAttArr[]=sdnAtt.split("-|\\/");
		
		swiftBod.trim().replaceAll("\\s","");
		String swiftBodArr[]=swiftBod.split("-|\\/");
		
		swiftGen.trim().replaceAll("\\s","");
		String swiftGenArr[]=swiftGen.split("-|\\/");
		
		swiftEod.trim().replaceAll("\\s","");
		String swiftEodArr[]=swiftEod.split("-|\\/");
		
		swiftAtt.trim().replaceAll("\\s","");
		String swiftAttArr[]=swiftAtt.split("-|\\/");
		
		dmatBod.trim().replaceAll("\\s","");
		String dmatBodArr[]=dmatBod.split("-|\\/");
		
		dmatGen.trim().replaceAll("\\s","");
		String dmatGenArr[]=dmatGen.split("-|\\/");
		
		dmatEod.trim().replaceAll("\\s","");
		String dmatEodArr[]=dmatEod.split("-|\\/");
		
		dmatAtt.trim().replaceAll("\\s","");
		String dmatAttArr[]=dmatAtt.split("-|\\/");
		
		bodTot.trim().replaceAll("\\s","");
		String bodTotArr[]=bodTot.split("-|\\/");
		
		genTot.trim().replaceAll("\\s","");
		String genTotArr[]=genTot.split("-|\\/");
		
		eodTot.trim().replaceAll("\\s","");
		String eodTotArr[]=eodTot.split("-|\\/");
		
		attTot.trim().replaceAll("\\s","");
		String attTotArr[]=attTot.split("-|\\/");

		
		
		
		misHtml="<html><style>table{color:#2B2B2B;border:1px solid #708090;border-collapse: collapse;font-size: 8pt;font-family: Verdana;}th{border:1px solid #708090;background-color:#E0FFFF;color:#2B2B2B;text-align:center;}td{border: 1px solid #708090;color:#2B2B2B;text-align:left;}</style><body><table width='80%' align='center'><th colspan='5'>i-AML MIS REPORT</th><tr bgcolor='silver'><td><b>Ticket Type</b></td><td><b>Begining Of Day</b></td><td><b>Generated</b></td><td><b>Attended</b></td><td><b>End Of Day</b></td></tr>";
		misHtml+="<tr><td>RULE Ticket</td><td>"+ruleBod+"</td><td>"+ruleGen+"</td><td>"+"T-"+ruleAttArr[1]+" / "+"A-"+ruleAttArr[3]+"</td><td>"+"T-"+ruleEodArr[1]+" / "+"A-"+ruleEodArr[3]+"</td></tr>";
		misHtml+="<tr><td>SDN Ticket</td><td>"+sdnBod+"</td><td>"+sdnGen+"</td><td>"+"T-"+sdnAttArr[1]+" / "+"A-"+sdnAttArr[3]+"</td><td>"+sdnEod+"</td></tr>";
		misHtml+="<tr><td>SWIFT Ticket</td><td>"+swiftBod+"</td><td>"+swiftGen+"</td><td>"+"T-"+swiftAttArr[1]+" / "+"A-"+swiftAttArr[3]+"</td><td>"+swiftEod+"</td></tr>";
		misHtml+="<tr><td>DEMAT Ticket</td><td>"+dmatBod+"</td><td>"+dmatGen+"</td><td>"+"T-"+dmatAttArr[1]+" / "+"A-"+dmatAttArr[3]+"</td><td>"+dmatEod+"</td></tr>";
		misHtml+="<tr><td>Total</td><td>"+bodTot+"</td><td>"+genTot+"</td><td>"+"T-"+attTotArr[1]+" / "+"A-"+attTotArr[3]+"</td><td>"+"T-"+eodTotArr[1]+" / "+"A-"+eodTotArr[3]+"</td></tr>";
		
		misHtml+="</table><br><br>";
		misHtml+="<table width='100%' align='center'><th colspan='11'>Userwise MIS Report</th><tr bgcolor='silver'><td><b>User Id</b></td><td><b>User Name</b></td><td><b>Role</b></td><td><b>Generated</b></td><td><b>Closed</b></td><td><b>Esclated</b></td><td><b>Esclated Back</b></td><td><b>On Hold</b></td><td><b>Disposed</b></td><td><b>Pending</b></td><td><b>Esclated Percent</b></td></tr>";
		for(String userList:arrList){
			misHtml+="<tr>";
			String user[]=userList.split("~");
			for(int i=0;i<user.length;i++){
				if(i==9){
				penDtls=user[i].split("/ A-");
				totPend +=Integer.parseInt(penDtls[1]);
				}
				misHtml+="<td>"+user[i]+"</td>";
			}
			misHtml+="</tr>";
			
		}
		totPend=totPend+1;
		misHtml+="<tr><td align='right' colspan=10><b>Total - "+totPend+"</b></td></tr>";
		misHtml+="</table><br><br>";
		
		misHtml+="<table width='20%'><th colspan='2'>STR MIS REPORT</th>";
		misHtml+="<tr bgcolor='silver'><td><b>Status</b></td><td><b>Count</b></td></tr>";
		misHtml+="<tr><td>Verified</td><td>"+verify_cnt+"</td></tr>";
		misHtml+="<tr><td>Rejected</td><td>"+reject_cnt+"</td></tr>";
		misHtml+="<tr><td>Approved</td><td>"+approved_cnt+"</td></tr>";
		misHtml+="<tr><td>Disposed</td><td>"+disposed_cnt+"</td></tr>";
		misHtml+="<tr><td>Under Process</td><td>"+process_cnt+"</td></tr>";
		misHtml+="<tr><td>Total</td><td>"+str_total+"</td></tr>";
		misHtml+="</table><br><br>";
		misHtml+="<table><th colspan='3'>Top 5 Rule Alerts</th>";
		misHtml+="<tr bgcolor='silver'><td><b>Rule Id</b></td><td><b>Rule Desc</b></td><td><b>Count</b></td></tr>";
		for(String topList:ruleList){
			misHtml+="<tr>";
			String rule[]=topList.split("~");
			for(int i=0;i<rule.length;i++){
				misHtml+="<td>"+rule[i]+"</td>";
			}
			misHtml+="</tr>";
		}
		misHtml+="</table></body></html>";
		
		arrToINTECH.add("avinash.ambekar@idbiintech.com");
		arrToINTECH.add("vanashree.mhatre@idbiintech.com");
		arrToINTECH.add("sharath.nair@idbiintech.com");
		arrToINTECH.add("rohit.singh@idbiintech.com");
		arrToINTECH.add("kartiki.talekar@idbiintech.com");
		arrToINTECH.add("sagar.kamble@idbiintech.com");
		arrToINTECH.add("akshay.mhatre@idbiintech.com");
		arrToINTECH.add("suvarna.mhatre@idbiintech.com");
		arrToINTECH.add("chintan.darji@idbiintech.com");
		arrToINTECH.add("patil.prathamesh@idbiintech.com");
		
		
		
		try {
			if (SendMail.sendEmailKYC(arrToINTECH,
					new ArrayList<String>(),
					new ArrayList<String>(), "i-AML Mis Report for "+misDate,
					misHtml, host, from, password)) {
				System.out.println("Mail Send Successfully to Intech...");
				
			}
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
		} catch (SendFailedException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		totPend = 0;
		
		
		for(String userList:arrList){
			String user[]=userList.split("~");
			for(int i=0;i<user.length;i++){
				if(i==9){
				penDtls=user[i].split("/ A-");
				totPend +=Integer.parseInt(penDtls[1]);
				}
			}		
		}
		totPend=totPend+1;
		
		//System.out.println("Pending count : "+totPend);
		//System.out.println("EOD Total : "+eodTotArr[3]);
		difference=totPend-Integer.valueOf(eodTotArr[3]);
		//System.out.println("Difference : "+difference);
		//System.out.println("flg "+flg);
		
		if(difference==0){
			flg=true;
		}
		
	
		if(difference > 15){
				if(!ruleAttArr[1].equals("0") && !ruleAttArr[3].equals("0") ){
				
					//System.out.println(">15");
				//rule eod
				ruleChngEod=Integer.valueOf(ruleEodArr[3])+difference;
				ruleEodArr[3]=String.valueOf(ruleChngEod);
				//System.out.println("changed ruleeod : "+ruleEodArr[3]);
				
				//total eod
				int totChngEod=Integer.valueOf(eodTotArr[3])+difference;
				eodTotArr[3]=String.valueOf(totChngEod);
				//System.out.println("totaleod : "+eodTotArr[3]);
				
				//rule attended 
				int ruleChngAtt=Integer.valueOf(ruleAttArr[3])-difference;
				ruleAttArr[3]=String.valueOf(ruleChngAtt);
				//System.out.println("rule attend : "+ruleChngAtt);
				
				//total attended
				int totChngAtt=Integer.valueOf(attTotArr[3])-difference;
				attTotArr[3]=String.valueOf(totChngAtt);
				//System.out.println("total attend : "+totChngAtt);
				
				flg=true;
			}else{
				ruleChngEod=Integer.valueOf(ruleEodArr[3])+difference;
				ruleEodArr[3]=String.valueOf(ruleChngEod);
				//System.out.println("changed ruleeod : "+ruleEodArr[3]);
				
				int totChngEod=Integer.valueOf(eodTotArr[3])+difference;
				eodTotArr[3]=String.valueOf(totChngEod);
				//System.out.println("totaleod : "+eodTotArr[3]);
				
				flg=false;
				}
			}
		if(difference<15){
			//System.out.println("< 15");
			if(!ruleAttArr[1].equals("0") && !ruleAttArr[3].equals("0") ){
			flg=true;
			}
			}
		if(difference < 0){
			if(!ruleAttArr[1].equals("0") && !ruleAttArr[3].equals("0") ){
				//rule eod
				int chngEodRule=Integer.valueOf(ruleEodArr[3])+difference;
				ruleEodArr[3]=String.valueOf(chngEodRule);
				//System.out.println("ruleeod : "+chngEodRule );
				
				//total eod
				int chngEodTot=Integer.valueOf(eodTotArr[3])+difference;
				eodTotArr[3]=String.valueOf(chngEodTot);
				//System.out.println("totaleod : "+chngEodTot );
				
				//rule attended
				int chngAttRule=Integer.valueOf(ruleAttArr[3])-difference;
				ruleAttArr[3]=String.valueOf(chngAttRule);
				//System.out.println("rule attend : "+chngAttRule );
				
				//total attended
				int chngAttTot=Integer.valueOf(attTotArr[3])-difference;
				attTotArr[3]=String.valueOf(chngAttTot);
				//System.out.println("total attend : "+chngAttTot );
				
				flg=true;
			}else{
				int chngEodRule=Integer.valueOf(ruleEodArr[3])+difference;
				ruleEodArr[3]=String.valueOf(chngEodRule);
				//System.out.println("ruleeod : "+chngEodRule );
				
				int chngEodTot=Integer.valueOf(eodTotArr[3])+difference;
				eodTotArr[3]=String.valueOf(chngEodTot);
				//System.out.println("totaleod : "+chngEodTot );
				
				flg=false;
			}
			}
		
		if(flg==true){
			/*System.out.println("true");
			
			System.out.println("RULE TICKET ATTENDED : "+ruleAttArr[1]);
			System.out.println("RULE ALERT ATTENDED : "+ruleAttArr[3]);
			System.out.println("******************************************************************************************************");
			
			System.out.println("SDN TICKET ATTENDED : "+sdnAttArr[1]);
			System.out.println("SDN ALERT ATTENDED : "+sdnAttArr[3]);
			System.out.println("******************************************************************************************************");
			
			System.out.println("SWIFT TICKET ATTENDED : "+swiftAttArr[1]);
			System.out.println("SWIFT ALERT ATTENDED : "+swiftAttArr[3]);
			System.out.println("******************************************************************************************************");
		
			System.out.println("DEMAT TICKET ATTENDED : "+dmatAttArr[1]);
			System.out.println("DEMAT ALERT ATTENDED : "+dmatAttArr[3]);
			System.out.println("******************************************************************************************************");
			
			System.out.println("TOTAL TICKET ATTENDED : "+attTotArr[1]);
			System.out.println("TOTAL ALERT ATTENDED : "+attTotArr[3]);
			System.out.println("******************************************************************************************************");
			System.out.println("updated values::");*/
			ruleTktAtt=(Integer.parseInt(ruleBodArr[1].trim())+Integer.parseInt(ruleGenArr[1].trim()))-Integer.parseInt(ruleEodArr[1].trim());
			ruleAttArr[1]=String.valueOf(ruleTktAtt);
			ruleAlrtAtt=(Integer.parseInt(ruleBodArr[3].trim())+Integer.parseInt(ruleGenArr[3].trim()))-Integer.parseInt(ruleEodArr[3].trim());
			ruleAttArr[3]=String.valueOf(ruleAlrtAtt);
		
			sdnTktAtt=(Integer.parseInt(sdnBodArr[1].trim())+Integer.parseInt(sdnGenArr[1].trim()))-Integer.parseInt(sdnEodArr[1].trim());
			sdnAttArr[1]=String.valueOf(sdnTktAtt);
			sdnAlrtAtt=(Integer.parseInt(sdnBodArr[3].trim())+Integer.parseInt(sdnGenArr[3].trim()))-Integer.parseInt(sdnEodArr[3].trim());
			sdnAttArr[3]=String.valueOf(sdnAlrtAtt);
			
			swiftTktAtt=(Integer.parseInt(swiftBodArr[1].trim())+Integer.parseInt(swiftGenArr[1].trim()))-Integer.parseInt(swiftEodArr[1].trim());
			swiftAltAtt=(Integer.parseInt(swiftBodArr[3].trim())+Integer.parseInt(swiftGenArr[3].trim()))-Integer.parseInt(swiftEodArr[3].trim());
			swiftAttArr[1]=String.valueOf(swiftTktAtt);
			swiftAttArr[3]=String.valueOf(swiftAltAtt);
			
			dmatTktAtt=(Integer.parseInt(dmatBodArr[1].trim())+Integer.parseInt(dmatGenArr[1].trim()))-Integer.parseInt(dmatEodArr[1].trim());
			dmatAltAtt=(Integer.parseInt(dmatBodArr[3].trim())+Integer.parseInt(dmatGenArr[3].trim()))-Integer.parseInt(dmatEodArr[3].trim());
			dmatAttArr[1]=String.valueOf(dmatTktAtt);
			dmatAttArr[3]=String.valueOf(dmatAltAtt);
			
			totTktAtt=Integer.parseInt(ruleAttArr[1].trim())+Integer.parseInt(sdnAttArr[1].trim())+Integer.parseInt(swiftAttArr[1].trim())+Integer.parseInt(dmatAttArr[1].trim());
			totAltAtt=Integer.parseInt(ruleAttArr[3].trim())+Integer.parseInt(sdnAttArr[3].trim())+Integer.parseInt(swiftAttArr[3].trim())+Integer.parseInt(dmatAttArr[3].trim());
			attTotArr[1]=String.valueOf(totTktAtt);
			attTotArr[3]=String.valueOf(totAltAtt);
			
			//totalTktEod=Integer.parseInt(ruleEodArr[1].trim())+Integer.parseInt(sdnEodArr[1].trim())+Integer.parseInt(swiftEodArr[1].trim())+Integer.parseInt(dmatEodArr[1].trim());
			totalAltEod=Integer.parseInt(ruleEodArr[3].trim())+Integer.parseInt(sdnEodArr[3].trim())+Integer.parseInt(swiftEodArr[3].trim())+Integer.parseInt(dmatEodArr[3].trim());
			//eodTotArr[1]=String.valueOf(totalTktEod);
			eodTotArr[3]=String.valueOf(totalAltEod);
			
			/*System.out.println("RULE TICKET ATTENDED : "+ruleAttArr[1]);
			System.out.println("RULE ALERT ATTENDED : "+ruleAttArr[3]);
			System.out.println("******************************************************************************************************");
			
			System.out.println("SDN TICKET ATTENDED : "+sdnAttArr[1]);
			System.out.println("SDN ALERT ATTENDED : "+sdnAttArr[3]);
			System.out.println("******************************************************************************************************");
			
			System.out.println("SWIFT TICKET ATTENDED : "+swiftAttArr[1]);
			System.out.println("SWIFT ALERT ATTENDED : "+swiftAttArr[3]);
			System.out.println("******************************************************************************************************");
		
			System.out.println("DEMAT TICKET ATTENDED : "+dmatAttArr[1]);
			System.out.println("DEMAT ALERT ATTENDED : "+dmatAttArr[3]);
			System.out.println("******************************************************************************************************");
			
			System.out.println("Total TICKET ATTENDED : "+attTotArr[1]);
			System.out.println("Total ALERT ATTENDED : "+attTotArr[3]);
			
			System.out.println("Total EOD : "+eodTotArr[1]);
			System.out.println("Total Alert: "+attTotArr[3]);
			
			System.out.println("******************************************************************************************************");*/
			
			updateQuery="update aml_eod_mis " 
					  +" set rule_ticket_att='"+ruleAttArr[1]+"',"
					        +" rule_alert_att='"+ruleAttArr[3]+"',"
					  		+" sdn_Ticket_Att='"+sdnAttArr[1]+"',"
							+" sdn_Alert_Att='"+sdnAttArr[3]+"',"
							+" swift_ticket_Att='"+swiftAttArr[1]+"',"
							+" swift_Alert_Att='"+swiftAttArr[3]+"',"
							+" dmat_ticket_Att='"+dmatAttArr[1]+"',"
							+" dmat_Alert_Att='"+dmatAttArr[3]+"',"
							+" tot_ticket_Att='"+attTotArr[1]+"',"
							+" tot_Alert_Att='"+attTotArr[3]+"',"
							+" rule_alert_eod='"+ruleEodArr[3]+"',"
							+" tot_alert_eod='"+eodTotArr[3]+"'"
							+" where to_date(aml_eod,'dd-mm-yy')=to_Date(sysdate,'dd-mm-yy')";
			
			try {
				connection=ConnectionFactory.makeConnectionAMLLiveThread();
				updateStmt=connection.createStatement();
				updateStmt.executeUpdate(updateQuery);
				connection.commit();
				//System.out.println("table updated : ");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			misHtml="<html><style>table{color:#2B2B2B;border:1px solid #708090;border-collapse: collapse;font-size: 8pt;font-family: Verdana;}th{border:1px solid #708090;background-color:#E0FFFF;color:#2B2B2B;text-align:center;}td{border: 1px solid #708090;color:#2B2B2B;text-align:left;}</style><body><table width='80%' align='center'><th colspan='5'>i-AML MIS REPORT</th><tr bgcolor='silver'><td><b>Ticket Type</b></td><td><b>Begining Of Day</b></td><td><b>Generated</b></td><td><b>Attended</b></td><td><b>End Of Day</b></td></tr>";
			misHtml+="<tr><td>RULE Ticket</td><td>"+ruleBod+"</td><td>"+ruleGen+"</td><td>"+"T-"+ruleAttArr[1]+" / "+"A-"+ruleAttArr[3]+"</td><td>"+"T-"+ruleEodArr[1]+" / "+"A-"+ruleEodArr[3]+"</td></tr>";
			misHtml+="<tr><td>SDN Ticket</td><td>"+sdnBod+"</td><td>"+sdnGen+"</td><td>"+"T-"+sdnAttArr[1]+" / "+"A-"+sdnAttArr[3]+"</td><td>"+sdnEod+"</td></tr>";
			misHtml+="<tr><td>SWIFT Ticket</td><td>"+swiftBod+"</td><td>"+swiftGen+"</td><td>"+"T-"+swiftAttArr[1]+" / "+"A-"+swiftAttArr[3]+"</td><td>"+swiftEod+"</td></tr>";
			misHtml+="<tr><td>DEMAT Ticket</td><td>"+dmatBod+"</td><td>"+dmatGen+"</td><td>"+"T-"+dmatAttArr[1]+" / "+"A-"+dmatAttArr[3]+"</td><td>"+dmatEod+"</td></tr>";
			misHtml+="<tr><td>Total</td><td>"+bodTot+"</td><td>"+genTot+"</td><td>"+"T-"+attTotArr[1]+" / "+"A-"+attTotArr[3]+"</td><td>"+"T-"+eodTotArr[1]+" / "+"A-"+eodTotArr[3]+"</td></tr>";
			
		}else{
			//System.out.println("false");
			ruleTktGen=Integer.parseInt(ruleEodArr[1].trim())-Integer.parseInt(ruleBodArr[1].trim());
			//ruleTktGen=Integer.parseInt(ruleGenArr[1].trim())-((Integer.parseInt(ruleBodArr[1].trim())+Integer.parseInt(ruleGenArr[1].trim()))-Integer.parseInt(ruleEodArr[1].trim()));
			//ruleAlrtGen=Integer.parseInt(ruleGenArr[3].trim())-((Integer.parseInt(ruleBodArr[3].trim())+Integer.parseInt(ruleGenArr[3].trim()))-Integer.parseInt(ruleEodArr[3].trim()));
			ruleAlrtGen=Integer.parseInt(ruleEodArr[3].trim())-Integer.parseInt(ruleBodArr[3].trim());
			
			ruleGenArr[1]=String.valueOf(ruleTktGen);
			ruleGenArr[3]=String.valueOf(ruleAlrtGen);
			
			/*System.out.println("updated value RuleTicket Gen:" +ruleTktGen);
			System.out.println("updated value RuleAlert Gen:" +ruleAlrtGen);
			System.out.println("******************************************************************************************************");*/
			
			sdnTktGen=Integer.parseInt(sdnGenArr[1].trim())-((Integer.parseInt(sdnBodArr[1].trim())+Integer.parseInt(sdnGenArr[1].trim()))-Integer.parseInt(sdnEodArr[1].trim()));
			sdnAlrtGen=Integer.parseInt(sdnGenArr[3].trim())-((Integer.parseInt(sdnBodArr[3].trim())+Integer.parseInt(sdnGenArr[3].trim()))-Integer.parseInt(sdnEodArr[3].trim()));
			
			sdnGenArr[1]=String.valueOf(sdnTktGen);
			sdnGenArr[3]=String.valueOf(sdnAlrtGen);
					
		/*	System.out.println("updated value SdnTicket Gen:" +sdnGenArr[1]);
			System.out.println("updated value SdnAlert Gen:" +sdnGenArr[3]);
			System.out.println("******************************************************************************************************");*/
			
			swiftTktGen=Integer.parseInt(swiftGenArr[1].trim())-((Integer.parseInt(swiftBodArr[1].trim())+Integer.parseInt(swiftGenArr[1].trim()))-Integer.parseInt(swiftEodArr[1].trim()));
			swiftAlrtGen=Integer.parseInt(swiftGenArr[3].trim())-((Integer.parseInt(swiftBodArr[3].trim())+Integer.parseInt(swiftGenArr[3].trim()))-Integer.parseInt(swiftEodArr[3].trim()));
			
			swiftGenArr[1]=String.valueOf(swiftTktGen);
			swiftGenArr[3]=String.valueOf(swiftAlrtGen);
			
			/*System.out.println("updated value SwftTicket Gen:" +swiftGenArr[1]);
			System.out.println("updated value SwftAlert Gen:" +swiftGenArr[3]);
			System.out.println("******************************************************************************************************");*/
			
			dmatTktGen=Integer.parseInt(dmatGenArr[1].trim())-((Integer.parseInt(dmatBodArr[1].trim())+Integer.parseInt(dmatGenArr[1].trim()))-Integer.parseInt(dmatEodArr[1].trim()));
			dmatAlrtGen=Integer.parseInt(dmatGenArr[3].trim())-((Integer.parseInt(dmatBodArr[3].trim())+Integer.parseInt(dmatGenArr[3].trim()))-Integer.parseInt(dmatEodArr[3].trim()));
			
			dmatGenArr[1]=String.valueOf(dmatTktGen);
			dmatGenArr[3]=String.valueOf(dmatAlrtGen);
			
			/*System.out.println("updated value dematTkt Gen:" +dmatGenArr[1]);
			System.out.println("updated value dematAlt Gen:" +dmatGenArr[3]);
			System.out.println("******************************************************************************************************");*/
			
			totTktGen=Integer.parseInt(genTotArr[1].trim())-((Integer.parseInt(bodTotArr[1].trim())+Integer.parseInt(genTotArr[1].trim()))-Integer.parseInt(eodTotArr[1].trim()));
			totAlrtGen=Integer.parseInt(ruleGenArr[3].trim())+Integer.parseInt(sdnGenArr[3].trim())+Integer.parseInt(swiftGenArr[3].trim())+Integer.parseInt(dmatGenArr[3].trim());
			
			totalAltEod=Integer.parseInt(ruleEodArr[3].trim())+Integer.parseInt(sdnEodArr[3].trim())+Integer.parseInt(swiftEodArr[3].trim())+Integer.parseInt(dmatEodArr[3].trim());
			eodTotArr[3]=String.valueOf(totalAltEod);
			
			genTotArr[1]=String.valueOf(totTktGen);
			genTotArr[3]=String.valueOf(totAlrtGen);
			
			/*System.out.println("updated value TotalTicket Gen:" +genTotArr[1]);
			System.out.println("updated value TotalAlert Gen:" +genTotArr[3]);
			System.out.println("******************************************************************************************************");*/
			
			updateQuery="update aml_eod_mis " 
					  +" set rule_ticket_gen='"+ruleGenArr[1]+"',"
							+" rule_alert_gen='"+ruleGenArr[3]+"',"
							+" sdn_ticket_gen='"+sdnGenArr[1]+"',"
							+" sdn_Alert_gen='"+sdnGenArr[3]+"',"
							+" swift_ticket_gen='"+swiftGenArr[1]+"',"
							+" swift_Alert_gen='"+swiftGenArr[3]+"',"
							+" dmat_ticket_gen='"+dmatGenArr[1]+"',"
							+" dmat_Alert_gen='"+dmatGenArr[3]+"',"
							+" tot_ticket_gen='"+genTotArr[1]+"',"
							+" tot_Alert_gen='"+genTotArr[3]+"',"
							+" rule_alert_eod='"+ruleEodArr[3]+"',"
							+" tot_alert_eod='"+eodTotArr[3]+"'"
							+" where to_date(aml_eod,'dd-mm-yy')=to_Date(sysdate,'dd-mm-yy')";
			try {
				connection=ConnectionFactory.makeConnectionAMLLiveThread();
				updateStmt=connection.createStatement();
				updateStmt.executeUpdate(updateQuery);
				connection.commit();
				//System.out.println("table updated : ");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			misHtml="<html><style>table{color:#2B2B2B;border:1px solid #708090;border-collapse: collapse;font-size: 8pt;font-family: Verdana;}th{border:1px solid #708090;background-color:#E0FFFF;color:#2B2B2B;text-align:center;}td{border: 1px solid #708090;color:#2B2B2B;text-align:left;}</style><body><table width='80%' align='center'><th colspan='5'>i-AML MIS REPORT</th><tr bgcolor='silver'><td><b>Ticket Type</b></td><td><b>Begining Of Day</b></td><td><b>Generated</b></td><td><b>Attended</b></td><td><b>End Of Day</b></td></tr>";
			misHtml+="<tr><td>RULE Ticket</td><td>"+ruleBod+"</td><td>"+"T-"+ruleGenArr[1]+" / "+"A-"+ruleGenArr[3]+"</td><td>"+ruleAtt+"</td><td>"+"T-"+ruleEodArr[1]+" / "+"A-"+ruleEodArr[3]+"</td></tr>";
			misHtml+="<tr><td>SDN Ticket</td><td>"+sdnBod+"</td><td>"+"T-"+sdnGenArr[1]+" / "+"A-"+sdnGenArr[3]+"</td><td>"+sdnAtt+"</td><td>"+sdnEod+"</td></tr>";
			misHtml+="<tr><td>SWIFT Ticket</td><td>"+swiftBod+"</td><td>"+"T-"+swiftGenArr[1]+" / "+"A-"+swiftGenArr[3]+"</td><td>"+swiftAtt+"</td><td>"+swiftEod+"</td></tr>";
			misHtml+="<tr><td>DEMAT Ticket</td><td>"+dmatBod+"</td><td>"+"T-"+dmatGenArr[1]+" / "+"A-"+dmatGenArr[3]+"</td><td>"+dmatAtt+"</td><td>"+dmatEod+"</td></tr>";
			misHtml+="<tr><td>Total</td><td>"+bodTot+"</td><td>"+"T-"+genTotArr[1]+" / "+"A-"+genTotArr[3]+"</td><td>"+attTot+"</td><td>"+"T-"+eodTotArr[1]+" / "+"A-"+eodTotArr[3]+"</td></tr>";
					
			}
		totPend=0;
		misHtml+="</table><br><br>";
		misHtml+="<table width='100%' align='center'><th colspan='11'>Userwise MIS Report</th><tr bgcolor='silver'><td><b>User Id</b></td><td><b>User Name</b></td><td><b>Role</b></td><td><b>Generated</b></td><td><b>Closed</b></td><td><b>Esclated</b></td><td><b>Esclated Back</b></td><td><b>On Hold</b></td><td><b>Disposed</b></td><td><b>Pending</b></td><td><b>Esclated Percent</b></td></tr>";
		for(String userList:arrList){
			misHtml+="<tr>";
			String user[]=userList.split("~");
			for(int i=0;i<user.length;i++){
				if(i==9){
				penDtls=user[i].split("/ A-");
				totPend +=Integer.parseInt(penDtls[1]);
				}
				misHtml+="<td>"+user[i]+"</td>";
			}
			misHtml+="</tr>";
			
		}
		totPend=totPend+1;
		misHtml+="<tr><td align='right' colspan=10><b>Total - "+totPend+"</b></td></tr>";
		misHtml+="</table><br><br>";
		
		misHtml+="<table width='20%'><th colspan='2'>STR MIS REPORT</th>";
		misHtml+="<tr bgcolor='silver'><td><b>Status</b></td><td><b>Count</b></td></tr>";
		misHtml+="<tr><td>Verified</td><td>"+verify_cnt+"</td></tr>";
		misHtml+="<tr><td>Rejected</td><td>"+reject_cnt+"</td></tr>";
		misHtml+="<tr><td>Approved</td><td>"+approved_cnt+"</td></tr>";
		misHtml+="<tr><td>Disposed</td><td>"+disposed_cnt+"</td></tr>";
		misHtml+="<tr><td>Under Process</td><td>"+process_cnt+"</td></tr>";
		misHtml+="<tr><td>Total</td><td>"+str_total+"</td></tr>";
		misHtml+="</table><br><br>";
		misHtml+="<table><th colspan='3'>Top 5 Rule Alerts</th>";
		misHtml+="<tr bgcolor='silver'><td><b>Rule Id</b></td><td><b>Rule Desc</b></td><td><b>Count</b></td></tr>";
		for(String topList:ruleList){
			misHtml+="<tr>";
			String rule[]=topList.split("~");
			for(int i=0;i<rule.length;i++){
				misHtml+="<td>"+rule[i]+"</td>";
			}
			misHtml+="</tr>";
		}
		misHtml+="</table></body></html>";
		
		//System.out.println(misHtml);
		/*arrToINTECH.add("ankita.chavan@idbiintech.com");
		arrToINTECH.add("avinash.ambekar@idbiintech.com");
		arrToINTECH.add("anand.mudaliyar@idbiintech.com");
		arrToINTECH.add("vanashree.mhatre@idbiintech.com");
		arrToINTECH.add("sharath.nair@idbiintech.com");
		arrToINTECH.add("rohit.singh@idbiintech.com");
		arrToINTECH.add("kartiki.talekar@idbiintech.com");
		arrToINTECH.add("sagar.kamble@idbiintech.com");
		arrToINTECH.add("akshay.mhatre@idbiintech.com");
		arrToINTECH.add("suvarna.mhatre@idbiintech.com");
		arrToINTECH.add("mohamed.shaikh@idbiintech.com");*/
		arrToIDBI.add("rahul.banerjee@idbi.co.in");
		arrToIDBI.add("amlcell@idbi.co.in");
		
		
		try {
			if (SendMail.sendEmailKYC(arrToINTECH,
					new ArrayList<String>(),
					new ArrayList<String>(), "i-AML Mis Report for "+misDate,
					misHtml, host, from, password)) {
				System.out.println("Mail Send Successfully to Intech...");
				
			}
			if (SendMail.sendEmailKYC(arrToIDBI,
					new ArrayList<String>(),
					new ArrayList<String>(), "i-AML Mis Report for "+misDate,
					misHtml, host, from, password)) {
				System.out.println("Mail Send Successfully to IDBI...");
				
			}
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
		} catch (SendFailedException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (updateStmt != null) { 
					updateStmt.close();
					updateStmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		}
	public static void main(String args[]){
		EodBodReport eod=new EodBodReport();
		eod.generateMisReport();
	}
}
