package com.idbi.intech.newCTR;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenerateRegulatoryReport {
	private static Connection conn;

	static {
		try {
			conn = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public static String getCredentials() {
		Console cons = null;
		String userId = null;
		char[] pwd = null;
		String userDetails = "";
		try {
			cons = System.console();
			if (cons != null) {
				userId = cons.readLine("User Id: ");
				pwd = cons.readPassword("Password: ");
				userDetails = userId + "~" + String.valueOf(pwd);
				cons.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return userDetails;
	}

	public static void main(String args[]) {
		GenNtrReport ntrrpt = new GenNtrReport(conn);
		GenCtrReport ctrrpt = new GenCtrReport(conn);
		// GenCBWTReport cbwtrpt = new GenCBWTReport(conn);
		String option = "";
		String report = "";
		String fromdate = "";
		String todate = "";
		String gendata = "";
		String genPart = "";
		String seqno = "";
		String verifydtls = "";
		String verifyflg[] = null;
		String userdtls[] = null;
		String flag = "";
		userdtls = GenerateRegulatoryReport.getCredentials().split("~");
		System.out.println("userid: " + userdtls[0] + "Password: " + userdtls[1]);
		System.out.print("********Regulatory Report Generator Tool Powered by i-AML**********\n");
		System.out.print("-------------------------------------------------------------------\n");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			verifydtls = ctrrpt.verifyUser(userdtls[0], userdtls[1]);
			verifyflg = verifydtls.split("~");
			if (verifyflg[0].equalsIgnoreCase("Y")) {
				System.out.print("Select any one choice from below option...\n");
				System.out.println("1.CTR");
				System.out.println("2.NTR");
				System.out.println("3.DELETE REPORT");
				System.out.println("4.GENERATE ONLY XML");
				System.out.println("5.EXIT");
				System.out.print("Enter the option: ");

				option = br.readLine();
				if (option.equalsIgnoreCase("2")) {
					report = "NTR";
					System.out.print("Enter the Start Date (DD-MON-YYYY): ");
					fromdate = br.readLine();
					System.out.print("Enter the End Date (DD-MON-YYYY): ");
					todate = br.readLine();
					System.out.print("The " + report + " will be generated for the date range " + fromdate + " to "
							+ todate + "\n");
					System.out.print("To generate the report enter(Y/N): ");
					gendata = br.readLine();
					if (gendata.equalsIgnoreCase("Y")) {
						flag = ntrrpt.checkReport(fromdate);
						// flag="N";
						if (flag.equalsIgnoreCase("N")) {
							seqno = ntrrpt.genNtrSeq(fromdate);
							// seqno = "1019";
							// ntrrpt.genNtrDetails(fromdate, todate,
							// seqno,"DEV1");
							ntrrpt.genNtrDetails(fromdate, todate, seqno, userdtls[0]);
							int count = ntrrpt.getAccountCount(seqno);
							int filecnt = 1;
							System.out.println("Report Count::" + count);
							for (int i = 1; i <= count;) {
								ntrrpt.createXmlFile(seqno, filecnt);
								i += 2000;
								filecnt++;
								System.out.println("Completed :: " + i);
							}
							System.out.println("File Generated Successfully...");
						} else {
							System.out.println("Report is already generated for the given month...");
						}
					}
				} else if (option.equalsIgnoreCase("1")) {
					report = "CTR";
					System.out.print("Enter the Start Date (DD-MON-YYYY): ");
					fromdate = br.readLine();
					System.out.print("Enter the End Date (DD-MON-YYYY): ");
					todate = br.readLine();
					System.out.print("The " + report + " will be generated for the date range " + fromdate + " to "
							+ todate + "\n");
					System.out.print("To generate the report enter(Y/N): ");

					gendata = br.readLine();
					if (gendata.equalsIgnoreCase("Y")) {
						flag = ctrrpt.checkReport(fromdate);
						// flag="N";
						if (flag.equalsIgnoreCase("N")) {
							seqno = ctrrpt.genCtrSeq(fromdate);
							// seqno = "0520";
							ctrrpt.genCtrDetails(fromdate, todate, seqno, userdtls[0]);
							// ctrrpt.genCtrDetails(fromdate, todate,
							// seqno,"DEV1");
							int count = ctrrpt.getAccountCount(seqno);
							int filecnt = 1;
							System.out.println("Report Count::" + count);
							for (int i = 1; i <= count;) {
								ctrrpt.createXmlFile(seqno, filecnt);
								i += 1000;
								filecnt++;
								System.out.println("Completed :: " + i);
							}
							System.out.println("File Generated Successfully...");
						} else {
							System.out.println("Report is already generated for the given month...");
						}
					}
				}
				// else if (option.equalsIgnoreCase("3")) {
				// report = "EFT";
				// System.out.print("Enter the Start Date: ");
				// fromdate = br.readLine();
				// System.out.print("Enter the End Date: ");
				// todate = br.readLine();
				// System.out.print("The CBWT " + report + " will be generated
				// for the date range " + fromdate + " to "
				// + todate + "\n");
				// System.out.print("To generate the report enter(Y/N): ");
				// gendata = br.readLine();
				// if (gendata.equalsIgnoreCase("Y")) {
				// flag = cbwtrpt.checkReport(fromdate);
				// // flag="N";
				// if (flag.equalsIgnoreCase("N")) {
				// seqno = cbwtrpt.genCbwtSeq(fromdate);
				// // seqno = "1219";
				// cbwtrpt.genCbwtDetails(fromdate, todate, seqno, "DEV1");
				// int count = cbwtrpt.getTransactionCount();
				// System.out.println("Report Count::" + count);
				// int filecnt = 1;
				//
				// for (int i = 1; i <= count;) {
				// cbwtrpt.createXmlFile(seqno, filecnt);
				// i += 2000;
				// filecnt++;
				// System.out.println("i :: " + i);
				// }
				// System.out.println("File Generated Successfully...");
				// } else {
				// System.out.println("Report is already generated for the given
				// month...");
				// }
				// }
				// }
				else if (option.equalsIgnoreCase("3")) {
					if (verifyflg[1].equals("LEVEL3")) {
						System.out.print("Select any one choice from below option to delete report...\n");
						System.out.print("1.CTR               2.NTR\n");
						option = br.readLine();
						if (option.equalsIgnoreCase("1")) {
							report = "CTR";
							System.out.print("Enter the Start Date (DD-MON-YYYY): ");
							fromdate = br.readLine();
							System.out.print("Enter the End Date (DD-MON-YYYY): ");
							todate = br.readLine();
							System.out.print("The " + report + " will be deleted for the date range " + fromdate
									+ " to " + todate + "\n");
							System.out.print("To delete the report enter(Y/N): ");
							gendata = br.readLine();
							if (gendata.equalsIgnoreCase("Y")) {
								flag = ctrrpt.checkReport(fromdate);
								if (flag.equalsIgnoreCase("Y")) {
									seqno = ctrrpt.genCtrSeq(fromdate);

									ctrrpt.delCtrDetails(fromdate, todate, seqno, userdtls[0]);

									System.out.println("Files Deleted Successfully...");
								} else {
									System.out.println("Report does not exist for the given month...");
								}
							}
						} else if (option.equalsIgnoreCase("2")) {
							report = "NTR";
							System.out.print("Enter the Start Date (DD-MON-YYYY): ");
							fromdate = br.readLine();
							System.out.print("Enter the End Date (DD-MON-YYYY): ");
							todate = br.readLine();
							System.out.print("The " + report + " will be deleted for the date range " + fromdate
									+ " to " + todate + "\n");
							System.out.print("To delete the report enter(Y/N): ");
							gendata = br.readLine();
							if (gendata.equalsIgnoreCase("Y")) {
								flag = ntrrpt.checkReport(fromdate);
								if (flag.equalsIgnoreCase("Y")) {
									seqno = ntrrpt.genNtrSeq(fromdate);

									ntrrpt.delNtrDetails(fromdate, todate, seqno, userdtls[0]);

									System.out.println("Files Deleted Successfully...");
								} else {
									System.out.println("Report does not exist for the given month...");
								}
							}
						} else {
							System.out.println("Please enter the correct option...\n");
						}
					} else {
						System.out.println("You are not authorized to delete the report...\n");
					}
				} else if (option.equalsIgnoreCase("4")) {
					System.out.print("Select any one choice from below option to generate report...\n");
					System.out.print("1.CTR               2.NTR\n");
					option = br.readLine();
					if (option.equalsIgnoreCase("1")) {
						report = "CTR";
						System.out.print("Enter the Start Date (DD-MON-YYYY): ");
						fromdate = br.readLine();
						System.out.print("Enter the End Date (DD-MON-YYYY): ");
						todate = br.readLine();
						System.out.print("The " + report + " xml will be generated for the date range " + fromdate
								+ " to " + todate + "\n");
						System.out.print("To generate xml report enter(Y/N): ");
						gendata = br.readLine();

						System.out.print("To generate FULL xml report enter(Y/N): ");
						genPart = br.readLine();

						if (gendata.equalsIgnoreCase("Y")) {
							flag = ctrrpt.checkReport(fromdate);
							if (flag.equalsIgnoreCase("Y")) {
								seqno = ctrrpt.genCtrSeq(fromdate);

								if (genPart.equalsIgnoreCase("Y")) {
									ctrrpt.updateAccountFlg(seqno);
								}

								int count = ctrrpt.getAccountCount(seqno);
								int filecnt = 1;
								System.out.println("Report Count::" + count);
								for (int i = 1; i <= count;) {
									ctrrpt.createXmlFile(seqno, filecnt);
									i += 1000;
									filecnt++;
									System.out.println("Completed :: " + i);
								}
								System.out.println("File Generated Successfully...");
							} else {
								System.out.println("Report does not exist for the given month...");
							}
						}
					} else if (option.equalsIgnoreCase("2")) {
						report = "NTR";
						System.out.print("Enter the Start Date (DD-MON-YYYY): ");
						fromdate = br.readLine();
						System.out.print("Enter the End Date (DD-MON-YYYY): ");
						todate = br.readLine();
						System.out.print("The " + report + " xml will be generated for the date range " + fromdate
								+ " to " + todate + "\n");
						System.out.print("To generate xml report enter(Y/N): ");
						gendata = br.readLine();

						System.out.print("To generate FULL xml report enter(Y/N): ");
						genPart = br.readLine();

						if (gendata.equalsIgnoreCase("Y")) {
							flag = ntrrpt.checkReport(fromdate);
							if (flag.equalsIgnoreCase("Y")) {
								seqno = ntrrpt.genNtrSeq(fromdate);

								if (genPart.equalsIgnoreCase("Y")) {
									ntrrpt.updateAccountFlg(seqno);
								}

								int count = ntrrpt.getAccountCount(seqno);
								int filecnt = 1;
								System.out.println("Report Count::" + count);
								for (int i = 1; i <= count;) {
									ntrrpt.createXmlFile(seqno, filecnt);
									i += 2000;
									filecnt++;
									System.out.println("Completed :: " + i);
								}
								System.out.println("File Generated Successfully...");
							} else {
								System.out.println("Report does not exist for the given month...");
							}
						}
					} else {
						System.out.println("Please enter the correct option...\n");
					}
				} else {
					System.out.println("Please enter the correct option...\n");
				}
			} else {
				System.out.println("Invalid User Id or Password...");
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
