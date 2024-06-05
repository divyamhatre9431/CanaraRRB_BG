package com.idbi.intech.iaml.screening;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreateScanThread implements Runnable {

	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtUp = null;
	Statement stmtCheck = null;
	ResultSet rs = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public String checkCreateThread() {
		int cnt = 0;
		String flg = "N";
		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck
					.executeQuery("select count(distinct(exe_flg)) from aml_core_process where active_flg='Y'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}

			if (cnt == 1) {
				rs = stmtCheck
						.executeQuery("select distinct(exe_flg) from aml_core_process where active_flg='Y'");
				while (rs.next()) {
					flg = rs.getString(1);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public void createThread() {
		try {
			String cName = "";
			String cPCountry = "";
			String cCCountry = "";
			String cId = "";
			String cDob = "";
			String cNo = "";
			double threshold = 0;
			String sol = "";
			int i= 0;

			stmt = connection.createStatement();
			stmtUp = connection.createStatement();

			rs = stmt
					.executeQuery("select nvl(VALUE,'0') from p_aml_general where name='SDN_FUZZY_MATCH_PERCENTAGE'");
			while (rs.next()) {
				threshold = Double.parseDouble(rs.getString(1));
			}

			rs = stmt
					.executeQuery("SELECT a.cust_id, cust_name,(SELECT ref_desc FROM aml_rct WHERE ref_rec_type = '03' AND ref_code = a.cust_perm_cntry_code) AS perm_ctry,(SELECT ref_desc FROM aml_rct WHERE ref_rec_type = '03' AND ref_code = a.cust_commu_cntry_code) AS commu_ctry,nvl(to_char(cust_dob,'MM/DD/YYYY'),'-'),primary_sol_id,id_number FROM aml_cust_master a,aml_kyc b WHERE a.cust_id = b.cust_id and scan_flg = 'Y' and cust_del_flg = 'N'");
			while (rs.next()) {
				cId = rs.getString(1);
				cName = rs.getString(2)== null ?"-":rs.getString(2);
				cPCountry = rs.getString(3)==null?"NA":rs.getString(3);
				cCCountry = rs.getString(4)==null?"NA":rs.getString(4);
				cDob = rs.getString(5)==null?"NA":rs.getString(5);
				sol = rs.getString(6)==null?"NA":rs.getString(5);
				cNo = rs.getString(7)==null?"NA":rs.getString(7);

				stmtUp.executeUpdate("update aml_cust_master set scan_flg = 'N' where cust_id  = '"
						+ cId + "'");
				
				System.out.println("Customer Id :: "+(++i) + " :: "+ cId);
				
				connection.commit();
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
	}

	public void updateBizz() {
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_bizz_tab set OPE_FLG_SDN='Y' where OPE_FLG_SDN='N'");
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
	
	public static void main(String args[]){
		makeConnection();
		Thread t = new Thread();
		t.start();
	}

	@Override
	public void run() {
		while (true) {
			if (checkCreateThread().equals("Y")) {
				updateBizz();
				createThread();
			}
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
