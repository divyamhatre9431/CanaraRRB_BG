package com.idbi.intech.aml.branchctr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreateBranchCTR {
	private static Connection connection;

	public static void makeConnection() {
		try {
			//connection = ConnectionFactory.makeConnectionAMLLive();
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	public void genCtrDetails(String fromdate, String enddate, String ctrseq) {

		CallableStatement callstmt = null;
		try {
			callstmt = connection.prepareCall("call gen_ctr_details('" + fromdate
					+ "','" + enddate + "')");
			callstmt.execute();
			System.out.println("CTR Detail Generated...");

			// CTR Transaction
			System.out.println("Generating CTR Transaction File...");
			callstmt = connection.prepareCall("call gen_ctr_tran('" + ctrseq + "')");
			callstmt.execute();
			System.out.println("CTR Transaction Generated...");

			// // CTR Branch
			// System.out.println("Generating CTR Branch File...");
			// callstmt = connection.prepareCall("call gen_ctr_branch('" +
			// fromdate
			// + "','" + enddate + "','" + ctrseq + "')");
			// callstmt.execute();
			// System.out.println("CTR Branch Generated...");
			//
			// // CTR Accounts
			// System.out.println("Generating CTR Account File...");
			// callstmt = connection.prepareCall("call gen_ctr_ac('" + fromdate
			// + "','"
			// + enddate + "','" + ctrseq + "')");
			// callstmt.execute();
			// System.out.println("CTR Accounts Generated...");
			//
			// // CTR Person Details
			// System.out.println("Generating CTR PersonDetails File...");
			// callstmt = connection.prepareCall("call gen_ctr_persondtls('" +
			// fromdate
			// + "','" + enddate + "','" + ctrseq + "')");
			// callstmt.execute();
			// System.out.println("CTR PersonDetails Generated...");

			// CTR Control File
			//System.out.println("Generating CTR Control File...");
			//callstmt = connection.prepareCall("call gen_ctr_ctrl('" + fromdate
			//		+ "','" + enddate + "','" + ctrseq + "')");
			//callstmt.execute();
			//System.out.println("CTR ControlFile Generated...");

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

	
	

	public void createBranchStatement(String startDate, String endDate) {
		StringBuilder build = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> arrSolId = new ArrayList<String>();
		String acNo = "";
		String acName = "";
		String trnAmt = "";
		String creDeb = "";
		String txnParticular = "";
		String txnRemark = "";
		Statement stmtSplit = null;
		ResultSet rsSplit = null;
		File file;
		FileOutputStream fop = null;

		final ResourceBundle bundle_path = ResourceBundle
				.getBundle("com.idbi.intech.aml.branchctr.branchctr");
		String filePath = bundle_path.getString("ctr_path");
		String host = bundle_path.getString("SMTPSERVER");
		String from = bundle_path.getString("FROM");
		String password = bundle_path.getString("PASSWORD");
		ArrayList<String> arrTo;

		try {
			// Data Filling Process
			
			String seq = startDate.substring(3).replace("-", "");
			
			genCtrDetails(startDate, endDate, seq);
			
			stmt = connection.createStatement();
			stmtSplit = connection.createStatement();

			rs = stmt
					.executeQuery("select distinct a.sol_id||'~'||sol_Desc||'~'||region_name from ctr_transaction a,aml_sol b where a.sol_id=b.sol_id and to_date(tran_date,'yyyy-mm-dd') between '"+startDate+"' and '"+endDate+"'");
			while (rs.next()) {
				arrSolId.add(rs.getString(1));
			}

			for (String solDetails : arrSolId) {
				build = new StringBuilder();

				String solDesc[] = solDetails.split("~");

				build.append("|====================================================================================================================================|\n");
				build.append("|                             C A S H    T R A N S A C T I O N    R E P O R T     F O R     A      B R A N C H                       |\n");
				build.append("|                                                                                                                                    |\n");
				build.append("|        REPORTING OF TRANSACTIONS OF AMOUNT >= 50K FOR THOSE ACCOUNTS HAVING TOTAL DEBIT OR CREDIT DURING MONTH  > 10 LAC           |\n");
				build.append("|                                                                                                                                    |\n");
				build.append("|                              GENERATED BY i-AML ANTI MONEY LAUNDERING SOLUTION                                                     |\n");
				build.append("|====================================================================================================================================|\n");

				if (solDesc[0].length() == 3) {
					solDesc[0] = solDesc[0].concat(" ");
				}

				build.append("|SOL_ID  : "
						+ solDesc[0]
						+ "                                                                                                                      |\n");

				int lengthSolDesc = solDesc[1].length() + 11;

				build.append("|SOLDESC : ");
				build.append(solDesc[1]);
				for (int i = 1; i < (134 - lengthSolDesc); i++)
					build.append(" ");
				build.append("|\n");

				int lengthRegion = solDesc[2].length() + 11;

				build.append("|REGION  : ");
				build.append(solDesc[2]);
				for (int i = 1; i < (134 - lengthRegion); i++)
					build.append(" ");
				build.append("|\n");

				int lengthstart = startDate.length() + 11;

				build.append("|FROM    : ");
				build.append(startDate);
				for (int i = 1; i < (134 - lengthstart); i++)
					build.append(" ");
				build.append("|\n");

				int lengthend = endDate.length() + 11;

				build.append("|UPTO    : ");
				build.append(endDate);
				for (int i = 1; i < (134 - lengthend); i++)
					build.append(" ");
				build.append("|\n");

				build.append("|====================================================================================================================================|\n");
				build.append("|FORACID         |ACCT_NAME                    |TOTAL AMOUNT   |CR_DR | TXN AMT(>=50k)| TXN_DATE | TXN PARTICULARS   | REMARKS       |\n");
				build.append("|================|=============================|===============|======|===============|==========|===================|===============|\n");

				rs = stmt
						.executeQuery("select rpad(account_no,16,' '),rpad(ACCT_NAME,29,' '),lpad(sum(tran_amount),15,' '),rpad(decode(cre_deb_flg,'C','CREDIT','DEBIT'),6,' ') cre_deb from ctr_transaction a,aml_ac_master b  where b.cust_ac_no=a.account_no and to_date(tran_date,'yyyy-mm-dd') between '"
								+ startDate
								+ "' and '"
								+ endDate
								+ "' and sol_id = '"
								+ solDesc[0].trim()
								+ "' having sum(tran_amount)> 1000000  group by sol_id,account_no,cre_deb_flg,acct_name");
				while (rs.next()) {
					build.append("|----------------|-----------------------------|---------------|------|---------------|----------|-------------------|---------------|\n");
					acNo = rs.getString(1);
					acName = rs.getString(2);
					trnAmt = rs.getString(3);
					creDeb = rs.getString(4);
					int cnt = 0;

					rsSplit = stmtSplit
							.executeQuery("select lpad(tran_amount,15,' '),to_char(to_date(a.tran_date,'yyyy-mm-dd'),'dd-mm-yyyy'),lpad(UPPER(TRAN_PARTICULAR),19,' '),lpad(UPPER(TRAN_RMKS),15,' ') from ctr_transaction a,aml_transaction_master_h b where a.tran_id = b.tran_id and to_date(a.tran_date,'yyyy-mm-dd')=b.tran_date and to_date(a.tran_date,'yyyy-mm-dd') between '"
									+ startDate
									+ "' and '"
									+ endDate
									+ "' and tran_amt  >= 50000 and tran_acid  in (select cust_acid from aml_ac_master where cust_ac_no='"
									+ acNo.trim() + "') order by b.tran_date");
					while (rsSplit.next()) {
						if (cnt == 0) {
							build.append("|");
							build.append(acNo + "|");
							build.append(acName + "|");
							build.append(trnAmt + "|");
							build.append(creDeb + "|");
							build.append(rsSplit.getString(1) + "|");
							build.append(rsSplit.getString(2) + "|");

							txnParticular = rsSplit.getString(3);
							if (txnParticular == null) {
								txnParticular = "                   |\n";
								build.append(txnParticular);
							} else
								build.append(txnParticular.length() > 19 ? txnParticular
										.substring(0, 19) : txnParticular + "|");

							txnRemark = rsSplit.getString(4);
							if (txnRemark == null) {
								txnRemark = "               |\n";
								build.append(txnRemark);
							} else
								build.append(txnRemark.length() > 15 ? txnRemark
										.substring(0, 15) : txnRemark + "|\n");

							
						}

						if (cnt > 0) {
							build.append("|");
							build.append("                " + "|");
							build.append("                             |");
							build.append("               |");
							build.append("      |");
							build.append(rsSplit.getString(1) + "|");
							build.append(rsSplit.getString(2) + "|");

							txnParticular = rsSplit.getString(3);
							if (txnParticular == null) {
								txnParticular = "                   |\n";
								build.append(txnParticular);
							} else
								build.append(txnParticular.length() > 19 ? txnParticular
										.substring(0, 19) : txnParticular + "|");

							txnRemark = rsSplit.getString(4);
							if (txnRemark == null) {
								txnRemark = "               |\n";
								build.append(txnRemark);
							} else
								build.append(txnRemark.length() > 15 ? txnRemark
										.substring(0, 15) : txnRemark + "|\n");
						}
						cnt++;
					}
					build.append("|----------------|-----------------------------|---------------|------|---------------|----------|-------------------|---------------|\n");
				}

				build.append("|====================================================================================================================================|");

				file = new File(filePath + "CTR-" + solDesc[0].trim() + "-"
						+ startDate.substring(3) + ".txt");
				fop = new FileOutputStream(file);

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				// get the content in bytes
				byte[] contentInBytes = (build.toString()).getBytes();

				fop.write(contentInBytes);
				fop.flush();
				fop.close();
				
				arrTo =  new ArrayList<String>();
				//arrTo.add("rohit.singh@idbiintech.com");
				//arrTo.add("anand.mudaliyar@idbiintech.com");
				
				arrTo.add("bm"+solDesc[0].trim()+"@obc.co.in");
				//arrTo.add("insp@obc.co.in");
				
				//System.out.println("File Name :: "+"CTR-"+solDesc[0].trim() + "-" + startDate.substring(3) + ".txt");
				//System.out.println("FilePath"+filePath);
				

				if (SendMail.sendEmailNew(
						arrTo,
						new ArrayList<String>(),
						new ArrayList<String>(),
						"Cash Transaction Report For Branch ("
								+ startDate.substring(3) + ")",
						"<HTML><BODY>Dear Sir/Madam,<br>"
								+ "<p>Please find the attached CTR for your scrutiny and reporting of suspicious transactions if any. Also save this file and take a print out for future references.</p><br><br><br>Regards,<br><b>INSP-AML<br>Head Office<b>"
								+ "</BODY></HTML>", host, from, password,
								"CTR-"+solDesc[0].trim() + "-" + startDate.substring(3) + ".txt",
						filePath+"CTR-"+solDesc[0].trim() + "-" + startDate.substring(3) + ".txt")) {
					System.out.println("Mail send successfully to the branch -> "+solDesc[0]);
				}

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rsSplit != null) {
					rsSplit.close();
					rsSplit = null;
				}
				if (stmtSplit != null) {
					stmtSplit.close();
					stmtSplit = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (fop != null) {
					fop.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public static void main(String args[]) {
		makeConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String start="";
		String end="";
		try {
			System.out.println("Please Enter Start Date In DD-MON-YY");
			start = br.readLine();
			System.out.println("Please Enter End Date In DD-MON-YY");
			end  =  br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new CreateBranchCTR().createBranchStatement(start,
				end);
	}

}
