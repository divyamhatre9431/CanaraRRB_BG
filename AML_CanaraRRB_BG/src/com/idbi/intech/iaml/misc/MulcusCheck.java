package com.idbi.intech.iaml.misc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.rulethread.CreateThread;

public class MulcusCheck implements Runnable {

	private static MulcusCheck process_run = new MulcusCheck();
	private static Connection connection = null, connectionFin = null,
			connectionAml = null, connectionEkyc = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectioniBUSLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public static void makeConnectionFIN() {
		try {
			connectionFin = ConnectionFactory.makeConnectionFINLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public static void makeConnectionAML() {
		try {
			connectionAml = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public static void makeConnectionEKYC() {
		try {
			connectionEkyc = ConnectionFactory.makeConnectionEKYCLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public static void main(String args[]) {
		Thread t = new Thread(new MulcusCheck());
		t.start();
		/*
		 * MulcusCheck mulcusCheck = new MulcusCheck(); mulcusCheck.nameCheck();
		 */
	}

	public void nameCheck() {
		makeConnection();
		makeConnectionAML();
		makeConnectionFIN();
		makeConnectionEKYC();
		try {
			if (connection.isValid(20) && connectionFin.isValid(20)
					&& connectionAml.isValid(20) && connectionEkyc.isValid(20)) {
				Statement stmt = null, stmtCmg = null, stmtAml = null, stmtEkyc = null;
				ResultSet rs = null, rsCmg = null, rsNameChk = null, rsOtpId = null, rsSmsMsg = null, rsSmsOTP = null, rsMsgId = null, rsDupChk = null, rsEkyc = null;
				String query = "", name = "", natId = "", panNum = "", phoneNo = "", tranId = "", flg = "", primarySol = "", custTitleCode = "", custShortName = "", custTypeCode = "", custStatCode = "", custOccpCode = "", custSex = "", custConst = "", custMinor = "", custNre = "", custParty = "", addressType = "", comuAddr1 = "", comuAddr2 = "", comuCity = "", comuState = "", comuPin = "", comuCntryCode = "", emailId = "", combinedStmtReqd = "", commuCode = "", casteCode = "", hlthCode = "", ratingCode = "", tdsCode = "", crncyCode = "", purgeAllowed = "", allowSweep = "", freeCode1 = "", schmCode = "", modeOfOper = "", pbPsCode = "", psDespatch = "", chqAlwd = "", delFg = "", status = "", dob = "", ratingDate = "", nomAvailableFlg = "", nomName = "", nomReltnCode = "", nomAddr1 = "", nomAddr2 = "", nomCityCode = "", nomStateCode = "", nomCountryCode = "", nomPinCode = "", nomMinorFlg = "", nomDateOfBirth = "", nomGuardName = "", minorGuardCode = "", nomGuardAddr1 = "", nomGuardAddr2 = "", nomGuardCityCode = "", nomGuardStateCode = "", nomGuardCountryCode = "", nomGuardPinCode = "", atmFlg = "", MobFlg = "", inetFlg = "", smsFlg = "", fatcaFlg = "", otpId = "", msgId = "";
				try {
					int dataCnt = 0, dupCnt = 0;
					stmt = connection.createStatement();
					stmtCmg = connectionAml.createStatement();
					stmtAml = connectionAml.createStatement();
					stmtEkyc = connectionEkyc.createStatement();
					// query =
					// "select cust_name,nat_id_card_num,date_of_birth,pan_gir_num,cust_pager_no,tran_id from ibank_ekyc where FLG_1='N'";
					query = "select sol_id,cust_title_code,cust_name,cust_short_name,cust_type_code,cust_stat_code,cust_occp_code,cust_sex,cust_const,cust_minor_Flg,cust_nre_flg, "
							+ "party_flg,nat_id_card_num,date_of_birth,address_type,cust_comu_addr1,cust_comu_addr2,cust_comu_city_code,cust_comu_state_code,cust_comu_pin_code, "
							+ "cust_comu_cntry_code,cust_pager_no,email_id,combined_stmt_reqd,cust_commu_code,cust_caste_code,cust_hlth_code,cust_rating_code,cust_rating_date,tds_tbl_code, "
							+ "pan_gir_num,crncy_code,purge_allowed_flg,allow_sweeps,free_code_1,schm_code,mode_of_oper_code,pb_ps_code,ps_despatch_mode,chq_alwd_flg,del_flg,status, "
							+ "tran_id,NOM_AVAILABLE_FLG,NOM_NAME,NOM_RELTN_CODE,NOM_ADDR1,NOM_ADDR2,NOM_CITY_CODE, "
							+ "NOM_STATE_CODE,NOM_COUNTRY_CODE,NOM_PIN_CODE,NOM_MINOR_FLG,NOM_DATE_OF_BIRTH,NOM_GUARD_NAME,MINOR_GUARD_CODE,NOM_GUARD_ADDR1,NOM_GUARD_ADDR2,NOM_GUARD_CITY_CODE, "
							+ "NOM_GUARD_STATE_CODE,NOM_GUARD_COUNTRY_CODE,NOM_GUARD_PIN_CODE,ATM_FLG,MOB_FLG,INET_FLG,SMS_FLG,FATCA_FLG from ibank_ekyc where flg_1='N'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						primarySol = rs.getString(1) == null ? "" : rs
								.getString(1);
						custTitleCode = rs.getString(2) == null ? "" : rs
								.getString(2);
						name = rs.getString(3) == null ? "" : rs.getString(3);
						custShortName = rs.getString(4) == null ? "" : rs
								.getString(4);
						custTypeCode = rs.getString(5) == null ? "" : rs
								.getString(5);
						custStatCode = rs.getString(6) == null ? "" : rs
								.getString(6);
						custOccpCode = rs.getString(7) == null ? "" : rs
								.getString(7);
						custSex = rs.getString(8) == null ? "" : rs
								.getString(8);
						custConst = rs.getString(9) == null ? "" : rs
								.getString(9);
						custMinor = rs.getString(10) == null ? "" : rs
								.getString(10);
						custNre = rs.getString(11) == null ? "" : rs
								.getString(11);
						custParty = rs.getString(12) == null ? "" : rs
								.getString(12);
						natId = rs.getString(13) == null ? "" : rs
								.getString(13);
						dob = rs.getString(14) == null ? "" : rs.getString(14);
						addressType = rs.getString(15) == null ? "" : rs
								.getString(15);
						comuAddr1 = rs.getString(16) == null ? "" : rs
								.getString(16);
						comuAddr2 = rs.getString(17) == null ? "" : rs
								.getString(17);
						comuCity = rs.getString(18) == null ? "" : rs
								.getString(18);
						comuState = rs.getString(19) == null ? "" : rs
								.getString(19);
						comuPin = rs.getString(20) == null ? "" : rs
								.getString(20);
						comuCntryCode = rs.getString(21) == null ? "" : rs
								.getString(21);
						phoneNo = rs.getString(22) == null ? "" : rs
								.getString(22);
						emailId = rs.getString(23) == null ? "" : rs
								.getString(23);
						combinedStmtReqd = rs.getString(24) == null ? "" : rs
								.getString(24);
						commuCode = rs.getString(25) == null ? "" : rs
								.getString(25);
						casteCode = rs.getString(26) == null ? "" : rs
								.getString(26);
						hlthCode = rs.getString(27) == null ? "" : rs
								.getString(27);
						ratingCode = rs.getString(28) == null ? "" : rs
								.getString(28);
						ratingDate = rs.getString(29) == null ? "" : rs
								.getString(29);
						tdsCode = rs.getString(30) == null ? "" : rs
								.getString(30);
						panNum = rs.getString(31) == null ? "" : rs
								.getString(31);
						crncyCode = rs.getString(32) == null ? "" : rs
								.getString(32);
						purgeAllowed = rs.getString(33) == null ? "" : rs
								.getString(33);
						allowSweep = rs.getString(34) == null ? "" : rs
								.getString(34);
						freeCode1 = rs.getString(35) == null ? "" : rs
								.getString(35);
						schmCode = rs.getString(36) == null ? "" : rs
								.getString(36);
						modeOfOper = rs.getString(37) == null ? "" : rs
								.getString(37);
						pbPsCode = rs.getString(38) == null ? "" : rs
								.getString(38);
						psDespatch = rs.getString(39) == null ? "" : rs
								.getString(39);
						chqAlwd = rs.getString(40) == null ? "" : rs
								.getString(40);
						delFg = rs.getString(41) == null ? "" : rs
								.getString(41);
						status = rs.getString(42) == null ? "" : rs
								.getString(42);
						tranId = rs.getString(43) == null ? "" : rs
								.getString(43);
						nomAvailableFlg = rs.getString(44) == null ? "" : rs
								.getString(44);
						nomName = rs.getString(45) == null ? "" : rs
								.getString(45);
						nomReltnCode = rs.getString(46) == null ? "" : rs
								.getString(46);
						nomAddr1 = rs.getString(47) == null ? "" : rs
								.getString(47);
						nomAddr2 = rs.getString(48) == null ? "" : rs
								.getString(48);
						nomCityCode = rs.getString(49) == null ? "" : rs
								.getString(49);
						nomStateCode = rs.getString(50) == null ? "" : rs
								.getString(50);
						nomCountryCode = rs.getString(51) == null ? "" : rs
								.getString(51);
						nomPinCode = rs.getString(52) == null ? "" : rs
								.getString(52);
						nomMinorFlg = rs.getString(53) == null ? "" : rs
								.getString(53);
						nomDateOfBirth = rs.getString(54) == null ? "" : rs
								.getString(54);
						// System.out.println(nomDateOfBirth);
						nomGuardName = rs.getString(55) == null ? "" : rs
								.getString(55);
						minorGuardCode = rs.getString(56) == null ? "" : rs
								.getString(56);
						nomGuardAddr1 = rs.getString(57) == null ? "" : rs
								.getString(57);
						nomGuardAddr2 = rs.getString(58) == null ? "" : rs
								.getString(58);
						nomGuardCityCode = rs.getString(59) == null ? "" : rs
								.getString(59);
						nomGuardStateCode = rs.getString(60) == null ? "" : rs
								.getString(60);
						nomGuardCountryCode = rs.getString(61) == null ? ""
								: rs.getString(61);
						nomGuardPinCode = rs.getString(62) == null ? "" : rs
								.getString(62);
						atmFlg = rs.getString(63) == null ? "" : rs
								.getString(63);
						MobFlg = rs.getString(64) == null ? "" : rs
								.getString(64);
						inetFlg = rs.getString(65) == null ? "" : rs
								.getString(65);
						smsFlg = rs.getString(66) == null ? "" : rs
								.getString(66);
						fatcaFlg = rs.getString(67) == null ? "" : rs
								.getString(67);

						// check duplicate entry
						query = "select count(1) from ibank_ekyc where nat_id_card_num='"
								+ natId + "' or pan_gir_num='" + panNum + "'";
						rsDupChk = stmt.executeQuery(query);
						while (rsDupChk.next()) {
							dupCnt = rsDupChk.getInt(1);
						}
						flg = "D";
						if (dupCnt == 1) {
							query = "select count(1) from cmg where cust_pager_no = '"
									+ phoneNo
									+ "' "
									+ "or nat_id_card_num = '"
									+ natId + "' "
									// + "or CUST_PAN_NO = '"
									+ "or PAN_GIR_NUM = '" + panNum + "'";
							/* + "or pan_gir_num = '"+panNum+"' "; */
							// System.out.println(query);
							rsCmg = stmtCmg.executeQuery(query);
							while (rsCmg.next()) {
								dataCnt = rsCmg.getInt(1);
							}
							// System.out.println("count : "+dataCnt);
							if (dataCnt == 0) {
								/*
								 * query =
								 * "select count(1) from aml_cust_master where cust_dob = to_char(to_date('"
								 * + dob.substring(0, 10) +
								 * "','yy-mm-dd'),'dd-mon-yyyy') and cust_name like '"
								 * + name + "'"; // System.out.println(query);
								 * rsNameChk = stmtAml.executeQuery(query);
								 * while (rsNameChk.next()) { dataCnt =
								 * rsNameChk.getInt(1); } if (dataCnt == 0)
								 */
								flg = "Y";
							} else {
								flg = "F";
								// otp id
								query = "select 'OTP'||to_char(sysdate,'ddmmyy')||lpad(OTP_SEQ.nextval,6,0) from dual";
								rsOtpId = stmt.executeQuery(query);
								while (rsOtpId.next()) {
									otpId = rsOtpId.getString(1);
								}

								// insert into user_otp table
								query = "insert into user_otp values('"
										+ otpId
										+ "','000000',sysdate,'C','000000000','ekyc',sysdate + (1/1440*5))";
								rsSmsOTP = stmt.executeQuery(query);
								connection.commit();

								// msg id
								query = "select 'MSG'||to_char(sysdate,'ddmmyy')||lpad(MSG_SEQ.nextval,6,0) from dual";
								rsMsgId = stmt.executeQuery(query);
								while (rsMsgId.next()) {
									msgId = rsMsgId.getString(1);
								}

								// insert into msg_rec table
								// msg id 000006 entry made in msg_master
								query = "insert into msg_rec values('"
										+ msgId
										+ "','000006','"
										+ phoneNo
										+ "',sysdate,'','U','It seems that you already have a relationship with IDBI Bank, to open a new account please visit the nearest branch.','"
										+ otpId + "','')";
								rsSmsMsg = stmt.executeQuery(query);
								connection.commit();

							}
						}
						stmt.executeUpdate("update ibank_ekyc set flg_1='"
								+ flg + "' where tran_id = '" + tranId + "'");
						connection.commit();
						String setId = "";
						if (flg.equalsIgnoreCase("Y")) {
							query = "select set_id from sst where sol_id = '"+primarySol+"' and set_id like 'RPU%' and rownum = 1";
							rsEkyc= stmtEkyc.executeQuery(query);
							while(rsEkyc.next()){
								setId = rsEkyc.getString(1);
							}
							
							query = "insert into idbi.idbi_ekyc(rpu_set_id,sol_id,cust_title_code,cust_name,cust_short_name,cust_type_code,cust_occp_code,cust_sex,cust_const,cust_minor_Flg,cust_nre_flg, "
									+ "party_flg,nat_id_card_num,date_of_birth,address_type,cust_comu_addr1,cust_comu_addr2,cust_comu_city_code,cust_comu_state_code,cust_comu_pin_code, "
									+ "cust_comu_cntry_code,cust_pager_no,email_id,combined_stmt_reqd,cust_commu_code,cust_caste_code,cust_hlth_code,cust_rating_code,cust_rating_date,tds_tbl_code, "
									+ "pan_gir_num,crncy_code,purge_allowed_flg,allow_sweeps,free_code_1,schm_code,mode_of_oper_code,pb_ps_code,ps_despatch_mode,chq_alwd_flg,del_flg,status, "
									+ "tran_id,NOM_AVAILABLE_FLG,NOM_NAME,NOM_RELTN_CODE,NOM_ADDR1,NOM_ADDR2,NOM_CITY_CODE, "
									+ "NOM_STATE_CODE,NOM_COUNTRY_CODE,NOM_PIN_CODE,NOM_MINOR_FLG,NOM_DATE_OF_BIRTH,NOM_GUARD_NAME,MINOR_GUARD_CODE,NOM_GUARD_ADDR1,NOM_GUARD_ADDR2,NOM_GUARD_CITY_CODE, "
									+ "NOM_GUARD_STATE_CODE,NOM_GUARD_COUNTRY_CODE,NOM_GUARD_PIN_CODE,ATM_FLG,MOB_FLG,INET_FLG,SMS_FLG,FATCA_FLG,CUST_STAT_CODE,FREE_TEXT_15,INTROD_TITLE_CODE,CUST_INTROD_NAME,ENTERED_USER_ID,ENTERED_TIME) "
									+ "values ('"+setId+"','"
									+ primarySol
									+ "','"
									+ custTitleCode
									+ "','"
									+ name
									+ "' "
									+ ",'"
									+ custShortName
									+ "','"
									+ custTypeCode
									+ "','"
									+ custOccpCode
									+ "','"
									+ custSex
									+ "','"
									+ custConst
									+ "','"
									+ custMinor
									+ "' "
									+ ",'"
									+ custNre
									+ "','"
									+ custParty
									+ "','"
									+ natId
									+ "',to_char(to_date('"
									+ dob.substring(0, 10)
									+ "','yy-mm-dd'),'dd-mon-yyyy'),'"
									+ addressType
									+ "','"
									+ comuAddr1
									+ "','"
									+ comuAddr2
									+ "','"
									+ comuCity
									+ "' "
									+ ",'"
									+ comuState
									+ "','"
									+ comuPin
									+ "','"
									+ comuCntryCode
									+ "','"
									+ phoneNo
									+ "','"
									+ emailId
									+ "','"
									+ combinedStmtReqd
									+ "','"
									+ commuCode
									+ "' "
									+ ",'"
									+ casteCode
									+ "','"
									+ hlthCode
									+ "','"
									+ ratingCode
									+ "',to_char(to_date('"
									+ ratingDate.substring(0, 10)
									+ "','yy-mm-dd'),'dd-mon-yyyy'),'"
									+ tdsCode
									+ "','"
									+ panNum
									+ "','"
									+ crncyCode
									+ "' "
									+ ",'"
									+ purgeAllowed
									+ "','"
									+ allowSweep
									+ "','"
									+ freeCode1
									+ "','"
									+ schmCode
									+ "','"
									+ modeOfOper
									+ "','"
									+ pbPsCode
									+ "','"
									+ psDespatch
									+ "' "
									+ ",'"
									+ chqAlwd
									+ "','"
									+ delFg
									+ "','"
									+ status
									+ "','"
									+ tranId
									+ "' "
									+ ",'"
									+ nomAvailableFlg
									+ "','"
									+ nomName
									+ "','"
									+ nomReltnCode
									+ "','"
									+ nomAddr1
									+ "','"
									+ nomAddr2
									+ "','"
									+ nomCityCode
									+ "' "
									+ ",'"
									+ nomStateCode
									+ "','"
									+ nomCountryCode
									+ "','"
									+ nomPinCode
									+ "','"
									+ nomMinorFlg
									+ "','"
									+ nomDateOfBirth
									+ "','"
									+ nomGuardName
									+ "','"
									+ minorGuardCode
									+ "','"
									+ nomGuardAddr1
									+ "' "
									+ ",'"
									+ nomGuardAddr2
									+ "','"
									+ nomGuardCityCode
									+ "','"
									+ nomGuardStateCode
									+ "','"
									+ nomGuardCountryCode
									+ "','"
									+ nomGuardPinCode
									+ "','"
									+ atmFlg
									+ "','"
									+ MobFlg
									+ "','"
									+ inetFlg
									+ "','"
									+ smsFlg
									+ "' "
									+ ",'"
									+ fatcaFlg
									+ "','"
									+ custStatCode
									+ "','RBG','M/S','IBNKMOB','SYSTEM',sysdate)";
							// System.out.println(query);
							stmtEkyc.executeUpdate(query);
							connectionEkyc.commit();
						}
					}
				} catch (Exception e) {
					System.out.println("Exception occured");
					e.printStackTrace();
				}
				try {
					if (stmt != null) {
						stmt.close();
						stmt = null;
					}
					/*
					 * if (stmtCmg != null) { stmtCmg.close(); stmtCmg = null; }
					 */if (stmtAml != null) {
						stmtAml.close();
						stmtAml = null;
					}
					if (stmtEkyc != null) {
						stmtEkyc.close();
						stmtEkyc = null;
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		} catch (Exception e) {
			System.out.println("Database is down");
		}

	}

	@Override
	public void run() {
		while (true) {
			nameCheck();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void startService(String serviceName) throws IOException,
			InterruptedException {
		String executeCmd = "cmd /c net start \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Started");
		}
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			process_run.start();
		} else {
			process_run.stop();
		}
	}

	public void start() {
		try {
			makeConnection();
			Thread t = new Thread(new CreateThread());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

}
