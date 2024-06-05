package com.idbi.intech.iaml.riskrating;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.rulethread.RuleThread;

public class RiskRatingProcess implements Runnable {

	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	static RiskRatingProcess process_run = new RiskRatingProcess();

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	
	public List<String> unprocessedRiskRequest() {
		
		List<String> riskList = new ArrayList<String>();
		
		try {
			stmt = connection.createStatement();
			rs=stmt.executeQuery("select RISK_REQ_NO||'~'||START_DATE||'~'||END_DATE||'~'||RULE_LIST from risk_rating_request where status='N' ");
			
			while(rs.next()){
				String riskNo = rs.getString(1);
				riskList.add(riskNo);
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
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return riskList;
	}
	
	
	public void updateRiskReqStatus(String riskNo) {
		try {
			
			stmt = connection.createStatement();
			stmt.executeUpdate("update risk_rating_request set status='Y' where RISK_REQ_NO='"+riskNo+"' ");
			connection.commit();
			
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
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	
	@Override
	public void run() {
		while (true) {
			List<String> riskList = unprocessedRiskRequest();
			
			if(riskList.size() > 0)
			{
				for(String riskDetails : riskList)
				{
					String riskDetailsArr[] = riskDetails.split("~");
					boolean flg = true;
					
					String riskReqNo = riskDetailsArr[0];
					String startDate = riskDetailsArr[1];
					String endDate = riskDetailsArr[2];
					String ruleList = riskDetailsArr[3];
					
					if(ruleList.contains(","))
					{
						String[] ruleListArr = ruleList.split(",");
						
						for(int i=0; i<ruleListArr.length; i++)
						{
							try
							{
								String rId = ruleListArr[i];
								String procedure = rId + "('" + riskReqNo + "','" + startDate + "','"+ endDate + "')";
								//new Thread(new RuleThread(procedure)).start();
							}
							catch(Exception e)
							{
								flg = false;
								e.printStackTrace();
							}
						}
					}
					else
					{
						try
						{
							String rId = ruleList;
							String procedure = rId + "('" + riskReqNo + "','" + startDate + "','"+ endDate + "')";
							//new Thread(new RuleThread(procedure)).start();
						}
						catch(Exception e)
						{
							flg = false;
							e.printStackTrace();
						}
					}
										
					if(flg)
					{
						updateRiskReqStatus(riskReqNo);
					}
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static void main(String[] args) {
		makeConnection();
		Thread t = new Thread(new RiskRatingProcess());
		t.start();
	}

}
