package com.idbi.intech.iaml.swift;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

public class TestMessage {
	
	static Connection con_aml = null;
	
	protected static void LoadDatabaseAml() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLive();
			con_aml.setAutoCommit(false);
		} catch (SQLException sqlExp) {
			System.exit(0);
		}
		System.out.println("===========================================================");
	}
	
	void getDtls(){
		try{
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		
		ResultSet rs = null;
		String sql = "select  MSG_INFO1,MSG_INFO2,MSG_INFO3 from aml_swift_msg_info where MSG_ID = 'MSG-211113-07536'";
		rs = con_aml.createStatement().executeQuery(sql);

		if (rs != null && rs.next()) {
			Clob clob = rs.getClob(1);
			Clob clob1 = rs.getClob(2);
			Clob clob2 = rs.getClob(3);
			if (clob != null) {
				if ((int) clob.length() > 0) {
					msg1 = clob.getSubString(1, (int) clob.length());
				}
			}
			if (clob1 != null) {
				if ((int) clob1.length() > 0) {
					msg2 = clob1.getSubString(1, (int) clob1.length());
				}
			}
			if (clob2 != null) {
				if ((int) clob2.length() > 0) {
					msg3 = clob2.getSubString(1, (int) clob2.length());
				}
			}
			
			String message = msg1+msg2+msg3;
			
			System.out.println(msg1+msg2+msg3);
			
		}}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		LoadDatabaseAml();
		new TestMessage().getDtls();
	}

}
