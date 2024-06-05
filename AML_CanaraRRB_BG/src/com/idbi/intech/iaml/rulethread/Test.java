package com.idbi.intech.iaml.rulethread;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.idbi.intech.iaml.tool.FuzzyMatch;
import com.sun.org.apache.xpath.internal.operations.Neg;

public class Test {

	public static void main(String args[]) {
		// try {
		// Set<String> a1 = new HashSet<String>();
		// Set<String> a2 = new HashSet<String>();
		//
		// a1.add("ORGANIZATION");
		// a1.add("IBRAHIM");
		// a1.add("DAWOOD");
		//
		// a2.add("IBRAHIM");
		// //a2.add("ORGANIZATION");
		// a2.add("DAVOOD");
		//
		// System.out.println(new Test().scanLogic(a1, a2, 0));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		
		System.out.println(new Test().convertAmt("-10000000000"));

	}

	public static void stopService(String serviceName) throws IOException,
			InterruptedException {
		String executeCmd = "cmd /c net start \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Success");
		}

	}

	public String convertAmt(String amount) {
		String cntAmt = "";
		String fAmt = "";
		int i = 0;
		boolean flg = true;
		String decimal = "";
		boolean negFlg = false;

		if (amount.contains(".")) {
			decimal = amount.substring(amount.indexOf("."));
			amount = amount.substring(0, amount.indexOf("."));
		}
		
		if(amount.startsWith("-")){
			amount = amount.substring(1);
			negFlg = true;
		}

		char digit[] = amount.toCharArray();

		for (int j = (digit.length - 1); j >= 0; j--) {
			if (i == 3) {
				cntAmt += ",";
				flg = false;
				i = 0;
			}
			if (i == 2 && flg == false) {
				cntAmt += "," + (String.valueOf(digit[j]));
				i = 0;
			} else {
				cntAmt += (String.valueOf(digit[j]));
			}
			i++;
		}

		char fdigit[] = cntAmt.toCharArray();

		for (int j = (fdigit.length - 1); j >= 0; j--) {
			fAmt += String.valueOf(fdigit[j]);
		}

		if (decimal != null) {
			fAmt += decimal;
		}
		
		if(negFlg){
			fAmt="-"+fAmt;
		}

		return fAmt;
	}

	public String getFLDData(String MSG_ID, String fldno) throws IOException {
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		String msg = "";
		String query = "";
		String c = "";
		String d = "";
		InputStream is = null;
		PreparedStatement pstmt = null;
		try {
			conn = com.idbi.intech.iaml.factory.ConnectionFactory
					.makeConnectionAMLLiveThread();
			stmt = conn.createStatement();

			query = "select msg_id,fld_val from AML_SWIFT_IN_MSG_TXN where msg_id =? and fld_no = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, MSG_ID);
			pstmt.setString(2, fldno);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				d = rs.getString(1);
				c = rs.getString(2);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
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
		return c;
	}

	public double scanLogic(Set<String> NList, Set<String> cust,
			double threshold) {
		ArrayList<Double> arrRes = new ArrayList<Double>();
		double main_result = 0.0;

		for (String cName : cust) {

			double result = 0.0;
			double temp_result = 0.0;

			int k = 0;

			for (String neg : NList) {

				temp_result = FuzzyMatch.compare(neg.toUpperCase(),
						cName.toUpperCase()) * 100;

				//System.out.println(temp_result);

				if (temp_result >= threshold) {
					if (temp_result > result)
						result = temp_result;
				}
			}

			if (result > 0) {
				arrRes.add(result );
			}
		}

		for (double main_res : arrRes) {
			main_result = main_res + main_result;
		}

		main_result = main_result / cust.size();
		return main_result;
	}

	public int testDob() {
		String custDpart[] = new String[3];
		String nlDpart[] = new String[3];
		String nlDob = "15/1947";
		String custDob = "02/15/1947";
		String dobFlg = "";
		int marks = 0;

		if (!custDob.equalsIgnoreCase("-")) {
			custDpart = custDob.split("/");
		}

		if (!nlDob.equalsIgnoreCase("-")) {
			nlDpart = nlDob.split("/");
		}

		int arrSize = nlDpart.length - 1;

		dobFlg = "Y";

		if (custDpart.length > 0 && nlDpart.length > 0) {
			for (int i = arrSize; i >= 0; i--) {
				System.out.println(custDpart[i] + " :: " + (nlDpart[i]));
				System.out.println(!custDpart[i].equalsIgnoreCase(nlDpart[i]));
				if (!custDpart[i].equalsIgnoreCase(nlDpart[i])) {
					if (dobFlg.equals("Y"))
						dobFlg = "N";
				}
			}
		}

		System.out.println(dobFlg);

		if (dobFlg.equals("Y")) {
			marks += 1;
		}
		return marks;
	}

	public int testDob1() {
		String nlDob = "-";
		String custDob = "1957";
		int marks = 0;

		if (custDob.contains(nlDob) || custDob.equals("-")) {
			marks += 1;
		}

		return marks;

	}

	public void testBrRef() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		String bRefNo = "";
		try {
			System.out.println("Here !!!");
			conn = com.idbi.intech.iaml.factory.ConnectionFactory
					.makeConnectionAMLLiveThread();
			sql = "select distinct(B.SOL_ID) as sol,B.SOL_DESC,B.ADDR_1,B.ADDR_2,"
					+ "(select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code=b.CITY_CODE) as CITY_CODE,"
					+ "(select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code=b.STATE_CODE) as STATE_CODE,b.PIN_CODE,b.UNIFORM_BR_CODE "
					+ " from P_AML_SOL b,AML_CTR_DETAILS A where a.BRANCH_ID=b.SOL_ID";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				bRefNo = String
						.format("%-7s",
								rs.getString("UNIFORM_BR_CODE") != null
										&& rs.getString("UNIFORM_BR_CODE")
												.length() > 6 ? rs
										.getString("UNIFORM_BR_CODE") : "")
						.substring(0, 7).replace("\'", " ").replace("\"", " ");

				System.out.println("BR :: " + bRefNo + " : "
						+ rs.getString("sol"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
