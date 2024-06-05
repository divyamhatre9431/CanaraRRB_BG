package com.idbi.intech.iaml.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class RedistributeAlert {
	private static Connection con_aml = null;
	
	public static void makeConnection() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	public void performRedistribution(String userId){
		Statement stmt = null;
		ResultSet rs = null;
		CallableStatement cStmt = null;
		int row = 0;
		try{
			stmt=con_aml.createStatement();
			cStmt=con_aml.prepareCall("call gen_userno()");
			row = stmt.executeUpdate("update aml_user_master set active_flg='N',user_no=null,distri_rule='N' where user_id ='"+userId+"'");
			if(row>0){
				cStmt.execute();
				stmt.executeUpdate("update aml_rule_ticket set user_id = 'NA' where user_id ='"+userId+"' and ticket_status in ('O','H','E')");
				con_aml.commit();
				System.out.println("Leave marked for the user successfully");
			}
			else{
				System.out.println("Please Enter Correct User Id");
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(stmt!=null){
					stmt.close();
					stmt=null;
				}
				if(rs!=null){
					rs.close();
					rs=null;
				}
				if(cStmt!=null){
					cStmt.close();
					cStmt=null;
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public void performActivate(String userId){
		Statement stmt = null;
		ResultSet rs = null;
		CallableStatement cStmt = null;
		int row = 0;
		try{
			stmt=con_aml.createStatement();
			cStmt=con_aml.prepareCall("call gen_userno()");
			row = stmt.executeUpdate("update aml_user_master set active_flg='Y',distri_rule='Y' where user_id ='"+userId+"'");
			if(row>0){
				cStmt.execute();
				System.out.println("User reactivated successfully");
			}
			else{
				System.out.println("Please Enter Correct User Id");
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(stmt!=null){
					stmt.close();
					stmt=null;
				}
				if(rs!=null){
					rs.close();
					rs=null;
				}
				if(cStmt!=null){
					cStmt.close();
					cStmt=null;
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]){
		makeConnection();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Input the user id on leave");
			String user_id=br.readLine().toUpperCase();
			System.out.println("Please select an action");
			System.out.println("1. Mark Leave");
			System.out.println("2. Reactivate After Leave");
			String action = br.readLine();
			if(action.equalsIgnoreCase("1"));
				new RedistributeAlert().performRedistribution(user_id);
			if(action.equalsIgnoreCase("2"))
				new RedistributeAlert().performActivate(user_id);
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

}
