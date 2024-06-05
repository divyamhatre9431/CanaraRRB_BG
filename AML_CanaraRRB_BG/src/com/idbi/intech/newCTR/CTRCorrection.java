package com.idbi.intech.newCTR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CTRCorrection {

	public static List<String> listf(String directoryName) {
		File directory = new File(directoryName);

		List<String> resultList = new ArrayList<String>();

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				// System.out.println(file.getName());
				if ((file.getName()).contains(".xml"))
					resultList.add(file.getName());
			}
		}
		// System.out.println(resultList);
		return resultList;
	}

	public static void main(String[] args) {
		BufferedReader br = null;
		FileOutputStream fop = null;
		File file;
		int startIdx = 0;
		int endIdx = 0;
		String content = "";
		String regex = "\\d+";
		String prevTag = "";
		String nextPrevTag="";
		
		ArrayList<String> ctryArr = new ArrayList<String>();
		ctryArr.add("AF");
		ctryArr.add("AX");
		ctryArr.add("AL");
		ctryArr.add("DZ");
		ctryArr.add("AS");
		ctryArr.add("AD");
		ctryArr.add("AO");
		ctryArr.add("AI");
		ctryArr.add("AQ");
		ctryArr.add("AG");
		ctryArr.add("AR");
		ctryArr.add("AM");
		ctryArr.add("AW");
		ctryArr.add("AU");
		ctryArr.add("AT");
		ctryArr.add("AZ");
		ctryArr.add("BS");
		ctryArr.add("BH");
		ctryArr.add("BD");
		ctryArr.add("BB");
		ctryArr.add("BY");
		ctryArr.add("BE");
		ctryArr.add("BZ");
		ctryArr.add("BJ");
		ctryArr.add("BM");
		ctryArr.add("BT");
		ctryArr.add("BO");
		ctryArr.add("BA");
		ctryArr.add("BW");
		ctryArr.add("BV");
		ctryArr.add("BR");
		ctryArr.add("IO");
		ctryArr.add("BN");
		ctryArr.add("BG");
		ctryArr.add("BF");
		ctryArr.add("BI");
		ctryArr.add("KH");
		ctryArr.add("CM");
		ctryArr.add("CA");
		ctryArr.add("CV");
		ctryArr.add("KY");
		ctryArr.add("CF");
		ctryArr.add("TD");
		ctryArr.add("CL");
		ctryArr.add("CN");
		ctryArr.add("CX");
		ctryArr.add("CC");
		ctryArr.add("CO");
		ctryArr.add("KM");
		ctryArr.add("CG");
		ctryArr.add("CD");
		ctryArr.add("CK");
		ctryArr.add("CR");
		ctryArr.add("CI");
		ctryArr.add("HR");
		ctryArr.add("CU");
		ctryArr.add("CY");
		ctryArr.add("CZ");
		ctryArr.add("DK");
		ctryArr.add("DJ");
		ctryArr.add("DM");
		ctryArr.add("DO");
		ctryArr.add("EC");
		ctryArr.add("EG");
		ctryArr.add("SV");
		ctryArr.add("GQ");
		ctryArr.add("ER");
		ctryArr.add("EE");
		ctryArr.add("ET");
		ctryArr.add("FK");
		ctryArr.add("FO");
		ctryArr.add("FJ");
		ctryArr.add("FI");
		ctryArr.add("FR");
		ctryArr.add("GF");
		ctryArr.add("PF");
		ctryArr.add("TF");
		ctryArr.add("GA");
		ctryArr.add("GM");
		ctryArr.add("GE");
		ctryArr.add("DE");
		ctryArr.add("GH");
		ctryArr.add("GI");
		ctryArr.add("GR");
		ctryArr.add("GL");
		ctryArr.add("GD");
		ctryArr.add("GP");
		ctryArr.add("GU");
		ctryArr.add("GT");
		ctryArr.add("GG");
		ctryArr.add("GN");
		ctryArr.add("GW");
		ctryArr.add("GY");
		ctryArr.add("HT");
		ctryArr.add("HM");
		ctryArr.add("VA");
		ctryArr.add("HN");
		ctryArr.add("HK");
		ctryArr.add("HU");
		ctryArr.add("IS");
		ctryArr.add("IN");
		ctryArr.add("ID");
		ctryArr.add("IR");
		ctryArr.add("IQ");
		ctryArr.add("IE");
		ctryArr.add("IM");
		ctryArr.add("IL");
		ctryArr.add("IT");
		ctryArr.add("JM");
		ctryArr.add("JP");
		ctryArr.add("JE");
		ctryArr.add("JO");
		ctryArr.add("KZ");
		ctryArr.add("KE");
		ctryArr.add("KI");
		ctryArr.add("KP");
		ctryArr.add("KR");
		ctryArr.add("KW");
		ctryArr.add("KG");
		ctryArr.add("LA");
		ctryArr.add("LV");
		ctryArr.add("LB");
		ctryArr.add("LS");
		ctryArr.add("LR");
		ctryArr.add("LY");
		ctryArr.add("LI");
		ctryArr.add("LT");
		ctryArr.add("LU");
		ctryArr.add("MO");
		ctryArr.add("MK");
		ctryArr.add("MG");
		ctryArr.add("MW");
		ctryArr.add("MY");
		ctryArr.add("MV");
		ctryArr.add("ML");
		ctryArr.add("MT");
		ctryArr.add("MH");
		ctryArr.add("MQ");
		ctryArr.add("MR");
		ctryArr.add("MU");
		ctryArr.add("YT");
		ctryArr.add("MX");
		ctryArr.add("FM");
		ctryArr.add("MD");
		ctryArr.add("MC");
		ctryArr.add("MN");
		ctryArr.add("ME");
		ctryArr.add("MS");
		ctryArr.add("MA");
		ctryArr.add("MZ");
		ctryArr.add("MM");
		ctryArr.add("NA");
		ctryArr.add("NR");
		ctryArr.add("NP");
		ctryArr.add("NL");
		ctryArr.add("AN");
		ctryArr.add("NC");
		ctryArr.add("NZ");
		ctryArr.add("NI");
		ctryArr.add("NE");
		ctryArr.add("NG");
		ctryArr.add("NU");
		ctryArr.add("NF");
		ctryArr.add("MP");
		ctryArr.add("NO");
		ctryArr.add("OM");
		ctryArr.add("PK");
		ctryArr.add("PW");
		ctryArr.add("PS");
		ctryArr.add("PA");
		ctryArr.add("PG");
		ctryArr.add("PY");
		ctryArr.add("PE");
		ctryArr.add("PH");
		ctryArr.add("PN");
		ctryArr.add("PL");
		ctryArr.add("PT");
		ctryArr.add("PR");
		ctryArr.add("QA");
		ctryArr.add("RE");
		ctryArr.add("RO");
		ctryArr.add("RU");
		ctryArr.add("RW");
		ctryArr.add("BL");
		ctryArr.add("SH");
		ctryArr.add("KN");
		ctryArr.add("LC");
		ctryArr.add("MF");
		ctryArr.add("PM");
		ctryArr.add("VC");
		ctryArr.add("WS");
		ctryArr.add("SM");
		ctryArr.add("ST");
		ctryArr.add("SA");
		ctryArr.add("SN");
		ctryArr.add("RS");
		ctryArr.add("SC");
		ctryArr.add("SL");
		ctryArr.add("SG");
		ctryArr.add("SK");
		ctryArr.add("SI");
		ctryArr.add("SB");
		ctryArr.add("SO");
		ctryArr.add("ZA");
		ctryArr.add("GS");
		ctryArr.add("ES");
		ctryArr.add("LK");
		ctryArr.add("SD");
		ctryArr.add("SR");
		ctryArr.add("SJ");
		ctryArr.add("SZ");
		ctryArr.add("SE");
		ctryArr.add("CH");
		ctryArr.add("SY");
		ctryArr.add("TW");
		ctryArr.add("TJ");
		ctryArr.add("TZ");
		ctryArr.add("TH");
		ctryArr.add("TL");
		ctryArr.add("TG");
		ctryArr.add("TK");
		ctryArr.add("TO");
		ctryArr.add("TT");
		ctryArr.add("TN");
		ctryArr.add("TR");
		ctryArr.add("TM");
		ctryArr.add("TC");
		ctryArr.add("TV");
		ctryArr.add("UG");
		ctryArr.add("UA");
		ctryArr.add("AE");
		ctryArr.add("GB");
		ctryArr.add("US");
		ctryArr.add("UM");
		ctryArr.add("UY");
		ctryArr.add("UZ");
		ctryArr.add("VU");
		ctryArr.add("VE");
		ctryArr.add("VN");
		ctryArr.add("VG");
		ctryArr.add("VI");
		ctryArr.add("WF");
		ctryArr.add("EH");
		ctryArr.add("YE");
		ctryArr.add("ZM");
		ctryArr.add("ZW");
		ctryArr.add("BQ");
		ctryArr.add("CW");
		ctryArr.add("SX");
		ctryArr.add("SS");
		ctryArr.add("XX");
		ctryArr.add("ZZ");



		try {
			for (String xmlFile : listf("D:\\AML\\XMLCorrect\\Incorrect")) {

				System.out.println(xmlFile);
				
				String sCorrectLine;

				file = new File(
						"D:\\AML\\XMLCorrect\\Correct\\"+xmlFile);
				fop = new FileOutputStream(file);

				if (!file.exists()) {
					file.createNewFile();
				}

				br = new BufferedReader(
						new FileReader(
								"D:\\AML\\XMLCorrect\\Incorrect\\"+xmlFile));
				while ((sCorrectLine = br.readLine()) != null) {
					//System.out.println(sCorrectLine);
					if (sCorrectLine.contains("<Telephone>")) {
						startIdx = sCorrectLine.indexOf("<Telephone>");
						endIdx = sCorrectLine.indexOf("</Telephone>");
						content = sCorrectLine.substring(startIdx + 11, endIdx);
						if (content.length() < 11 || (!content.matches(regex))) {
							sCorrectLine = "<Telephone>00000000000</Telephone>";
						}
					}
					
					/*
					if (sCorrectLine.contains("<CountryCode>")) {
						startIdx = sCorrectLine.indexOf("<CountryCode>");
						endIdx = sCorrectLine.indexOf("</CountryCode>");
						content = sCorrectLine.substring(startIdx + 13, endIdx);
						if (!ctryArr.contains(content)) {
							sCorrectLine = "<CountryCode>ZZ</CountryCode>";
						}
					}
					*/
					
					/*if (sCorrectLine.contains("<Mobile>")) {
						//System.out.println(sCorrectLine);
						startIdx = sCorrectLine.indexOf("<Mobile>");
						endIdx = sCorrectLine.indexOf("</Mobile>");
						content = sCorrectLine.substring(startIdx + 8, endIdx);
						if (content.length() <= 10 || (!content.matches(regex))) {
							sCorrectLine = "<Mobile>0000000000</Mobile>";
						}
					}*/
					if (sCorrectLine.contains("<Address>")) {
						startIdx = sCorrectLine.indexOf("<Address>");
						endIdx = sCorrectLine.indexOf("</Address>");
						content = sCorrectLine.substring(startIdx + 9, endIdx);
						content = content.contains(" ")?content.replace(" ", ""):content;
						if ((content.trim()).length() < 15) {
							sCorrectLine = "<Address>XXXXXXXXXXXXXXX</Address>";
						}
					}
					if (sCorrectLine.contains("<PAN>")) {
						startIdx = sCorrectLine.indexOf("<PAN>");
						endIdx = sCorrectLine.indexOf("</PAN>");
						content = sCorrectLine.substring(startIdx + 5, endIdx);
						if ((content.trim()).length() < 10) {
							sCorrectLine = "<PAN></PAN>";
						}
					}
					
					/*
					if (sCorrectLine.contains("<PersonName>")) {
						startIdx = sCorrectLine.indexOf("<PersonName>");
						endIdx = sCorrectLine.indexOf("</PersonName>");
						content = sCorrectLine.substring(startIdx + 12, endIdx);
						if ((content.trim()).length() < 3) {
							sCorrectLine = "<PersonName>XXX</PersonName>";
						}
						if((content.trim()).length() >78) {
							content = content.substring(0, 75);
							sCorrectLine = "<PersonName>"+content+"</PersonName>";
						}
					}
					if (sCorrectLine.contains("</Branch>")) {
						prevTag = "</Branch>";
					}
					if (sCorrectLine.contains("<PersonDetails>")) {
						prevTag = "<PersonDetails>";
					}
					if (sCorrectLine.contains("<Transaction>")
							&& prevTag.equals("</Branch>")) {
						prevTag = "</Transaction>";
						sCorrectLine = "<PersonDetails>\n"
								+ "<PersonName>XXX</PersonName>\n"
								+ "<CustomerId>000000000</CustomerId>\n"
								+ "<RelationFlag>A</RelationFlag>\n"
								+ "<CommunicationAddress>\n"
								+ "<Address>XXXXXXXXXXXXXXXX</Address>\n"
								+ "<City>XX</City>\n"
								+ "<StateCode>XX</StateCode>\n"
								+ "<PinCode>000000</PinCode>\n"
								+ "<CountryCode>ZZ</CountryCode>\n"
								+ "</CommunicationAddress>\n"
								+ "<Phone>\n"
								+ "<Telephone>00000000000</Telephone>\n"
								+ "<Mobile>0000000000</Mobile>\n"
								+ "<Fax></Fax>\n"
								+ "</Phone>\n"
								+ "<Email></Email>\n"
								+ "<PAN>XXXXX0000X</PAN>\n"
								+ "<UIN></UIN>\n"
								+ "<LegalPerson>\n"
								+ "<ConstitutionType>Z</ConstitutionType>\n"
								+ "<RegistrationNumber></RegistrationNumber>\n"
								+ "<PlaceOfRegistration></PlaceOfRegistration>\n"
								+ "<CountryCode>ZZ</CountryCode>\n"
								+ "<NatureOfBusiness></NatureOfBusiness>\n"
								+ "</LegalPerson>\n"
								+ "</PersonDetails>\n<Transaction>";
					}
					if (sCorrectLine.contains("</Account>")
							&& prevTag.equals("</Branch>")) {
						prevTag = "</PersonDetails>";
						sCorrectLine = "<PersonDetails>\n"
								+ "<PersonName>XXX</PersonName>\n"
								+ "<CustomerId>000000000</CustomerId>\n"
								+ "<RelationFlag>A</RelationFlag>\n"
								+ "<CommunicationAddress>\n"
								+ "<Address>XXXXXXXXXXXXXXXX</Address>\n"
								+ "<City>XX</City>\n"
								+ "<StateCode>XX</StateCode>\n"
								+ "<PinCode>000000</PinCode>\n"
								+ "<CountryCode>ZZ</CountryCode>\n"
								+ "</CommunicationAddress>\n"
								+ "<Phone>\n"
								+ "<Telephone>00000000000</Telephone>\n"
								+ "<Mobile>0000000000</Mobile>\n"
								+ "<Fax></Fax>\n"
								+ "</Phone>\n"
								+ "<Email></Email>\n"
								+ "<PAN>XXXXX0000X</PAN>\n"
								+ "<UIN></UIN>\n"
								+ "<LegalPerson>\n"
								+ "<ConstitutionType>Z</ConstitutionType>\n"
								+ "<RegistrationNumber></RegistrationNumber>\n"
								+ "<PlaceOfRegistration></PlaceOfRegistration>\n"
								+ "<CountryCode>ZZ</CountryCode>\n"
								+ "<NatureOfBusiness></NatureOfBusiness>\n"
								+ "</LegalPerson>\n"
								+ "</PersonDetails>\n</Account>";
					}
					*/
					
					// cbwt branch tag
					if (sCorrectLine.contains("</Transaction>")) {
						prevTag = "</Transaction>";
					}
					if (sCorrectLine.contains("</Report>")
							&& prevTag.equals("</Transaction>")) {
						prevTag = "</Transaction>";
						sCorrectLine = "<Branch>\n"
								+ "<InstitutionName>XXXX</InstitutionName>\n"
								+ "<InstitutionBranchName>XXXX</InstitutionBranchName>\n"
								+ "<InstitutionRefNum>X</InstitutionRefNum>\n"
								+ "<ReportingRole>X</ReportingRole>\n"
								+ "<BIC></BIC>\n"
								+ "<BranchAddress>\n"
								+ "<Address>XXXXXXXXXXXXXXXXXXXXXXXXXX</Address>\n"
								+ "<City>ZZ</City>\n"
								+ "<StateCode>XX</StateCode>\n"
								+ "<PinCode>000000</PinCode>\n"
								+ "<CountryCode>ZZ</CountryCode>\n"
								+ "</BranchAddress>\n"
								+ "<Phone>\n"
								+ "<Telephone>00000000000</Telephone>\n"
								+ "<Mobile></Mobile>\n"
								+ "<Fax></Fax>\n"
								+ "</Phone>\n"
								+ "<Email></Email>\n"
								+ "<Remarks></Remarks>\n"
								+ "</Branch>\n"
								+ "</Report>";
					}
					
					
					//System.out.println(sCorrectLine);
					byte[] contentInBytes = (sCorrectLine + "\n").getBytes();
					fop.write(contentInBytes);
				}
				fop.flush();
				fop.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fop != null) {
					fop.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
