package com.idbi.intech.newCTR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ReadSWIFTTxtFiles {

	private File file;
	private FileReader fr;
	private BufferedReader br;
	private static Connection con_aml = null;

	protected static void LoadDatabaseAml() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
			con_aml.setAutoCommit(false);
		} catch (SQLException sqlExp) {
			System.exit(0);
		}
		System.out.println("===========================================================");
	}

//	public File  readFilesFromFolder( File file) {
//		//PreparedStatement pstmtSeq = null;
//		//PreparedStatement pstmtInsertMsg = null;
//		//PreparedStatement pstmtInsertFld = null;
//		
//		//ResultSet rs = null;
//
//		try {
//			//file = new File("D:/AML/SWIFT/UNPROCESS/");
//			fr = new FileReader(file);
//			br = new BufferedReader(fr);
//			ByteArrayInputStream bai = null;
//
//			String line;	
//			StringBuilder message = null;
//			String msg_id = null;
//
//			String firstToken = null;
//
//			String tagData = "";
//
//			
//
//			/*BufferedWriter out = new BufferedWriter(
//					new FileWriter("D:/AML/SWIFT/UNPROCESS/", true));*/
//			BufferedWriter out = new BufferedWriter(
//					new FileWriter(file, true));
//			out.write("Message Identifier");
//			out.close();
//
//			String pattern = "^([0-9]{2}+[A-Z]{0,1}+:)";
//
//			Pattern patternObj = Pattern.compile(pattern);
//
//			Matcher matcherObj = null;
//
//			ArrayList<String> hsMsgVal = null;
//
//			String senderBic = "";
//			String recieverBic = "";
//			String msgType = "";
//			String InOut = "";
//
////			pstmtInsertMsg = con_aml.prepareStatement(
////					"insert into aml_cbwt_swift_msg_old(msg_id,raw_msg,sender_bic,receiver_bic,in_out_flg,msg_type,upload_date) values(?,?,?,?,?,?,sysdate)");
////
////			pstmtInsertFld = con_aml
////					.prepareStatement("insert into aml_cbwt_swift_fld_old(msg_id,fld_no,fld_val) values(?,?,?)");
////			pstmtSeq = con_aml.prepareStatement(
////					"select 'MSG-'||to_char(sysdate,'ddmmyy')||'-'||lpad(MSG_ID_SEQ.nextval,5,0) from dual");
//
//			while ((line = br.readLine()) != null) {
//				//senderBic="";
//				//recieverBic="";
//				//msgType="";
//				//InOut="";
//				System.out.println(line);
//
//				if (line.contains("Message Identifier")) {
//					System.out.println(message);
//					if (message != null) {
//
//						
////						rs = pstmtSeq.executeQuery();
////						while (rs.next()) {
////							msg_id = rs.getString(1);
////						}
////
////						pstmtInsertMsg.setString(1, msg_id);
////						bai = new ByteArrayInputStream(message.toString().getBytes());
////						pstmtInsertMsg.setAsciiStream(2, bai, bai.available());
////						pstmtInsertMsg.setString(3, senderBic.trim());
////						pstmtInsertMsg.setString(4, recieverBic.trim());
////						pstmtInsertMsg.setString(5, InOut);
////						pstmtInsertMsg.setString(6, msgType);
////
////						pstmtInsertMsg.executeUpdate();
//
//						for (String val : hsMsgVal) {
//							String valArr[] = val.split("\\---");
////							if (valArr.length == 2) {
////								pstmtInsertFld.setString(1, msg_id);
////								pstmtInsertFld.setString(2, valArr[0]);
////								bai = new ByteArrayInputStream(valArr[1].toString().getBytes());
////								pstmtInsertFld.setAsciiStream(3, bai, bai.available());
////								pstmtInsertFld.executeUpdate();
////							}
//						}
//
////						con_aml.commit();
//
//					}
//
//					message = new StringBuilder();
//					message.append(line + "\n");
//					hsMsgVal = new ArrayList<String>();
//
//				} else {
//					if (message != null) {
//						try {
//							firstToken = line.trim().substring(0, line.trim().indexOf(' '));
//						} catch (StringIndexOutOfBoundsException e) {
//							e.getMessage();
//						}
//
//						matcherObj = patternObj.matcher(firstToken);
//						if (matcherObj.matches()) {
//							if (!tagData.equals("")) {
//								String tag = tagData.substring(0, tagData.indexOf(' '));
//								matcherObj = patternObj.matcher(tag);
//								if (matcherObj.matches()) {
//									String fldData = "";
//									String fldArr[] = tagData.split("~");
//									if (fldArr.length > 1) {
//										for (int i = 1; i < fldArr.length; i++) {
//											if (!fldArr[i].contains("Message Trailer"))
//												fldData += fldArr[i] + "~";
//										}
//									}
//
//									hsMsgVal.add(tag.substring(0, tag.length() - 1) + "---" + fldData);
//								}
//							}
//							tagData = "";
//							firstToken = "";
//						}
//
//						tagData += line.trim() + "~";
//
//						if (line.trim().startsWith("Sender")) {
//							senderBic = line.trim().substring(line.trim().indexOf(":") + 1);
//							//System.out.println(senderBic.substring(0, 6));
//							InOut = senderBic.trim().substring(0, 6).equals("IBKLIN") ? "O" : "I";
//						}
//
//						if (line.trim().startsWith("Receiver")){
//							recieverBic = line.trim().substring(line.trim().indexOf(":") + 1);
//						}
//
//						if (line.trim().startsWith("Swift Output")){
//							msgType = line.trim().substring(line.trim().indexOf("FIN") + 4,
//									line.trim().indexOf("FIN") + 7);
//						}
//						
//						if (line.trim().startsWith("Swift Input")){
//							msgType = line.trim().substring(line.trim().indexOf("FIN") + 4,
//									line.trim().indexOf("FIN") + 7);
//						}
//
//						message.append(line + "\n");
//					}
//				}
//
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
////			try {
////				if (pstmtSeq != null) {
////					pstmtSeq.close();
////					pstmtSeq=null;
////					
////				}
////				if (pstmtInsertMsg != null) {
////					pstmtInsertMsg.close();
////					pstmtInsertMsg=null;
////					
////				}
////				if (pstmtInsertFld != null) {
////					pstmtInsertFld.close();
////					pstmtInsertFld=null;
////				
////				}
////				if (rs != null) {
////					rs.close();
////					rs=null;
////					
////				}
////				
////			} catch (SQLException ex) {
////				ex.printStackTrace();
////			}
//		}
//		return file;
//
//	}
	
	public File  readFilesFromFolder( File file) {
		try{
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line;
			while(br.readLine()!=null){
				line=br.readLine();
				System.out.println(line);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return file;
	}
	
	public void uploadSwiftFiles() {
		System.out.println("Processing the file upload");
		File folder = new File("D:/AML/SWIFT/UNPROCESS/");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			System.out.println(file);
			readFilesFromFolder(file);
			System.out.println("File uploaded successfully..." + file.getName());

		}

	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		new ReadSWIFTTxtFiles().uploadSwiftFiles();
	}

}
