package com.idbi.intech.aml.LN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.idbi.intech.iaml.misc.SendMail;

public class ReadingLexisNexis implements Runnable {
	private static Properties bundle = new Properties();
	static ReadingLexisNexis process_run = new ReadingLexisNexis();
	public static String uploadedFile = "";
	public static String mailFile = "";

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

	public static void stopService(String serviceName) throws IOException,
			InterruptedException {
		String executeCmd = "cmd /c net stop \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Stopped");
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
			Thread t = new Thread(new ReadingLexisNexis());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public static void isFilePresent(String dirPath) {
		try {
			System.out.println("Reading File from::" + dirPath);
			String filePath = "";
			String mailFilePath = "";
			boolean delFlg = false;

			File file = new File(dirPath);

			Date currDt = new Date();
			//SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
			SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyhhmmss");
			String folderName = format.format(currDt);

			String destDir = bundle.getProperty("LNDISTINATIONPATH")
					+ folderName;

			String fileList[] = file.list();

			if (fileList.length > 0) {
				for (int i = 0; i < fileList.length; i++) {
					System.out.println("Processing the file::" + fileList[i]);
					if (fileList[i].contains(".xml")) {
						uploadedFile = fileList[i];
						filePath = dirPath + fileList[i];
						System.out.println("xml::"+filePath);
					}
					if (fileList[i].contains(".txt")) {
						mailFile = fileList[i];
						mailFilePath = dirPath + fileList[i];
						System.out.println("txt::"+mailFilePath);
					}

				}
				
				delFlg = uploadXmlFile(filePath, mailFilePath);
				System.out.println("Files uploaded Successfully:: "+delFlg);
				if (delFlg) {
					if (new File(destDir).mkdir()) {
						moveFiles(new File(filePath), new File(destDir
								+ "\\" + uploadedFile));
						moveFiles(new File(mailFilePath), new File(destDir
								+ "\\" + mailFile));
						new File(filePath).delete();
						new File(mailFilePath).delete();
					}
					
				}
				
				System.out.println("Files moved Successfully:: ");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void moveFiles(File source, File dest) {
		
		InputStream is = null;
		OutputStream os = null; 
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			is.close();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean uploadXmlFile(String filePath, String mailPath)
			throws JAXBException, IOException, XMLStreamException,
			URISyntaxException {
		long startTime = System.currentTimeMillis();
		boolean uploadFlg = false;
		int updateCnt = 0;
		JAXBContext jaxbContext = null;
		Unmarshaller um = null;
		XMLStreamReader reader = null;
		EntititesRecord recObj = new EntititesRecord();

		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		// creating object for DAO
		LexisNexisDao daoObj = new LexisNexisDao();

		daoObj.truncateTables();
		daoObj.commitdetails();

		// ----------------------------------------------EntitiesSources---------------------------------------------------------------------
		// interested in "EntitiesSources" elements only. Skip up to first
		// "EntitiesSources"
		int cnt = 0;
		FileReader fileReader = new FileReader(new File(filePath));
		reader = xmlFactory.createXMLStreamReader(fileReader);
		jaxbContext = JAXBContext.newInstance(EntitiesSources.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesSources"))) {
			reader.next();
		}

		// read a EntitiesSources at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesSources> boolElement = um.unmarshal(reader,
					EntitiesSources.class);
			EntitiesSources entitySource = boolElement.getValue();
			if (entitySource.getSourceID() != null) {
				cnt += daoObj.insertEntitySources(entitySource);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		reader.close();
		fileReader.close();
		daoObj.commitdetails();
		recObj.setEntitiesSources(cnt);
		System.out.println("EntitiesSources Uploaded : " + cnt);

		// ---------------------------------------------------EntitiesEntryTypes----------------------------------------------------------------
		// interested in "EntitiesEntryType" elements only. Skip up to first
		// "EntitiesEntryType"
		cnt = 0;
		FileReader fileReaderEntitiesEntryTypes = new FileReader(new File(
				filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesEntryTypes);
		jaxbContext = JAXBContext.newInstance(EntitiesEntryTypes.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesEntryTypes"))) {
			reader.next();
		}

		// read a entitiesEntryTypes at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesEntryTypes> boolElement = um.unmarshal(reader,
					EntitiesEntryTypes.class);
			EntitiesEntryTypes entityEntryTypes = boolElement.getValue();
			if (entityEntryTypes.getID() != null
					&& entityEntryTypes.getEntryDesc() != null) {
				cnt += daoObj.insertEntityEntryTypes(entityEntryTypes);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesEntryTypes.close();
		daoObj.commitdetails();
		recObj.setEntitiesEntryType(cnt);
		System.out.println("EntitiesEntryTypes Uploaded : " + cnt);

		// ---------------------------------------------------EntitiesCategories----------------------------------------------------------------
		// interested in "EntitiesCategories" elements only. Skip up to first
		// "EntitiesCategories"
		cnt = 0;
		FileReader fileReaderEntitiesCategories = new FileReader(new File(
				filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesCategories);
		jaxbContext = JAXBContext.newInstance(EntitiesCategories.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesCategories"))) {
			reader.next();
		}

		// read a EntitiesCategories at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesCategories> boolElement = um.unmarshal(reader,
					EntitiesCategories.class);
			EntitiesCategories entityCategory = boolElement.getValue();
			if (entityCategory.getID() != null
					&& entityCategory.getEntryCategory() != null) {
				cnt += daoObj.insertEntityCategories(entityCategory);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesCategories.close();
		daoObj.commitdetails();
		recObj.setEntitiesCategory(cnt);
		System.out.println("EntitiesCategories Uploaded : " + cnt);

		// ---------------------------------------------------EntitiesSubCategories----------------------------------------------------------------
		// interested in "EntitiesSubCategories" elements only. Skip up to first
		// "EntitiesSubCategories"
		cnt = 0;
		FileReader fileReaderEntitiesSubCategories = new FileReader(new File(
				filePath));
		reader = xmlFactory
				.createXMLStreamReader(fileReaderEntitiesSubCategories);
		jaxbContext = JAXBContext.newInstance(EntitiesSubCategory.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesSubCategories"))) {
			reader.next();
		}

		// read a EntitiesSubCategories at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesSubCategory> boolElement = um.unmarshal(reader,
					EntitiesSubCategory.class);
			EntitiesSubCategory entitySubCategory = boolElement.getValue();

			if (entitySubCategory.getID() != null
					&& entitySubCategory.getEntrySubCategory() != null) {
				cnt += daoObj.insertEntitySubcategory(entitySubCategory);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesSubCategories.close();
		daoObj.commitdetails();
		recObj.setEntitiesSubCategory(cnt);
		System.out.println("EntitiesSubCategories Uploaded : " + cnt);

		// ---------------------------------------------------EntitiesCountries----------------------------------------------------------------
		// interested in "EntitiesCountries" elements only. Skip up to first
		// "EntitiesCountries"
		cnt = 0;
		FileReader fileReaderEntitiesCountries = new FileReader(new File(
				filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesCountries);
		jaxbContext = JAXBContext.newInstance(EntitiesCountries.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesCountries"))) {
			reader.next();
		}

		// read a EntitiesSubCategories at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesCountries> boolElement = um.unmarshal(reader,
					EntitiesCountries.class);
			EntitiesCountries entitiesCountries = boolElement.getValue();
			if (entitiesCountries.getCountryID() != null) {
				cnt += daoObj.insertEntityCountry(entitiesCountries);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesCountries.close();
		daoObj.commitdetails();
		recObj.setEntitiesCountriesCnt(cnt);
		System.out.println("EntitiesCountries Uploaded : " + cnt);

		// ---------------------------------------------------EntitiesLevel----------------------------------------------------------------
		// interested in "EntitiesLevel" elements only. Skip up to first
		// "EntitiesLevel"
		cnt = 0;
		FileReader fileReaderEntitiesLevels = new FileReader(new File(filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesLevels);
		jaxbContext = JAXBContext.newInstance(EntitiesLevels.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesLevels"))) {
			reader.next();
		}

		// read a EntitiesSubCategories at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesLevels> boolElement = um.unmarshal(reader,
					EntitiesLevels.class);
			EntitiesLevels entitiesLevels = boolElement.getValue();
			if (entitiesLevels.getID() != null
					&& entitiesLevels.getLevelDesc() != null) {
				cnt += daoObj.insertEntityLevels(entitiesLevels);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesLevels.close();
		daoObj.commitdetails();
		recObj.setEntitiesLevelsCnt(cnt);
		System.out.println("EntitiesLevel Uploaded : " + cnt);

		// ------------------------------------------------Entities-------------------------------------------------------------------
		// interested in "Entities" elements only. Skip up to first "Entities"
		cnt = 0;
		FileReader fileReaderEntities = new FileReader(new File(filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntities);
		jaxbContext = JAXBContext.newInstance(Entities.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"Entities"))) {
			reader.next();
		}

		// read a Entities at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			try {
				JAXBElement<Entities> boolElement = um.unmarshal(reader,
						Entities.class);
				Entities entity = boolElement.getValue();
				if (entity.getEnt_ID() != null && entity.getFirstName() != null) {
					// System.out.println(entity.getEnt_ID());
					cnt += daoObj.insertEntityDetails(entity);
					if (cnt == 500000) {
						daoObj.commitdetails();
						cnt = 0;
						System.out.println("Done..");
					}
				}

				if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
					reader.next();
				}
			} catch (Exception ex) {
				System.out.println("exception occured");
				if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
					reader.next();
				}
			}
		}
		fileReaderEntities.close();
		daoObj.commitdetails();
		recObj.setEntitiesCount(cnt);
		System.out.println("Entities Uploaded : " + cnt);

		// ------------------------------------------------EntitiesAddresses-------------------------------------------------------------------
		// interested in "EntitiesAddresses" elements only. Skip up to first
		// "EntitiesAddresses"
		cnt = 0;
		FileReader fileReaderEntitiesAddresses = new FileReader(new File(
				filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesAddresses);
		jaxbContext = JAXBContext.newInstance(EntitiesAddresses.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesAddresses"))) {
			reader.next();
		}

		// read a EntitiesAddresses at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesAddresses> boolElement = um.unmarshal(reader,
					EntitiesAddresses.class);
			EntitiesAddresses entityAddresses = boolElement.getValue();
			if (entityAddresses.getAddress_ID() != null) {
				cnt += daoObj.insertEntityAddresses(entityAddresses);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesAddresses.close();
		daoObj.commitdetails();
		recObj.setEntitiesAddress(cnt);
		System.out.println("EntitiesAddresses Uploaded : " + cnt);

		// ------------------------------------------------EntitiesRefDefs-------------------------------------------------------------------
		// interested in "EntitiesRefDefs" elements only. Skip up to first
		// "EntitiesRefDefs"
		cnt = 0;
		FileReader fileReaderEntitiesRelDefs = new FileReader(
				new File(filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesRelDefs);
		jaxbContext = JAXBContext.newInstance(EntitiesRelDefs.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesRelDefs"))) {
			reader.next();
		}

		// read a EntitiesRefDefs at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesRelDefs> boolElement = um.unmarshal(reader,
					EntitiesRelDefs.class);
			EntitiesRelDefs entityRelDefs = boolElement.getValue();
			if (entityRelDefs.getRelationID() != null) {
				cnt += daoObj.insertEntityRelDefs(entityRelDefs);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesRelDefs.close();
		daoObj.commitdetails();
		recObj.setEntitiesRelDefs(cnt);
		System.out.println("EntitiesRelDefs Uploaded : " + cnt);

		// ------------------------------------------------EntitiesRelationships-------------------------------------------------------------------
		// interested in "EntitiesRelationships" elements only. Skip up to first
		// "EntitiesRelationships"
		cnt = 0;
		FileReader fileReaderEntitiesRelationships = new FileReader(new File(
				filePath));
		reader = xmlFactory
				.createXMLStreamReader(fileReaderEntitiesRelationships);
		jaxbContext = JAXBContext.newInstance(EntitiesRelationships.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesRelationships"))) {
			reader.next();
		}

		// read a EntitiesRelationships at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesRelationships> boolElement = um.unmarshal(
					reader, EntitiesRelationships.class);
			EntitiesRelationships entityRelationships = boolElement.getValue();
			if (entityRelationships.getRID() != null) {
				cnt += daoObj.insertEntityRelationships(entityRelationships);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesRelationships.close();
		daoObj.commitdetails();
		recObj.setEntitiesRelationships(cnt);
		System.out.println("entitiesRelationships Uploaded : " + cnt);

		// ------------------------------------------------EntitiesDeletions-------------------------------------------------------------------
		// interested in "EntitiesDeletions" elements only. Skip up to first
		// "EntitiesDeletions"
		cnt = 0;
		FileReader fileReaderEntitiesDeletions = new FileReader(new File(
				filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderEntitiesDeletions);
		jaxbContext = JAXBContext.newInstance(EntitiesDeletions.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"EntitiesDeletions"))) {
			reader.next();
		}

		// read a EntitiesRefDefs at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesDeletions> boolElement = um.unmarshal(reader,
					EntitiesDeletions.class);
			EntitiesDeletions entityRelationDeletions = boolElement.getValue();
			if (entityRelationDeletions.getEnt_ID() != null
					&& entityRelationDeletions.getDateDeleted() != null) {
				cnt += daoObj
						.insertEntityDeletedEntries(entityRelationDeletions);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderEntitiesDeletions.close();
		daoObj.commitdetails();
		recObj.setEntitiesDeletions(cnt);
		System.out.println("EntitiesDeletions Uploaded : " + cnt);

		// ------------------------------------------------SanctionsDOB-------------------------------------------------------------------
		// interested in "SanctionsDOB" elements only. Skip up to first
		// "SanctionsDOB"
		cnt = 0;
		FileReader fileReaderSanctionsDOB = new FileReader(new File(filePath));
		reader = xmlFactory.createXMLStreamReader(fileReaderSanctionsDOB);
		jaxbContext = JAXBContext.newInstance(EntitiesSanctionDob.class);

		// unmarshall: String "source" to Java object
		um = jaxbContext.createUnmarshaller();
		while (reader.hasNext()
				&& (!reader.isStartElement() || !reader.getLocalName().equals(
						"SanctionsDOB"))) {
			reader.next();
		}

		// read a SanctionsDOB at a time
		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
			JAXBElement<EntitiesSanctionDob> boolElement = um.unmarshal(reader,
					EntitiesSanctionDob.class);
			EntitiesSanctionDob entitySanctionDob = boolElement.getValue();
			if (entitySanctionDob.getSanctionsDobId() != null) {
				cnt += daoObj.insertEntitySanctionDob(entitySanctionDob);
			}

			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
				reader.next();
			}
		}
		fileReaderSanctionsDOB.close();
		recObj.setEntitiesSanctionDob(cnt);
		System.out.println("SanctionsDOB Uploaded : " + cnt);
		// new FileReader(new File(filePath)).close();
		// reader.close();

		if (new File(filePath).renameTo(new File(filePath))) {
			System.out.println("File closed");
		} else {
			System.out.println("File still open");
		}
		updateCnt = daoObj.insertRecordCnt(recObj, uploadedFile);
		reader.close();
		// System.out.println("flg : "+updateCnt);

		if (updateCnt > 0) {
			uploadFlg = true;
			//System.out.println("Data imported successfully!");
		} else {
			uploadFlg = false;
			//System.out.println("Something went wrong !");
		}

		//Sending Mail to the Business
		//mailSendflg(updateCnt, uploadFlg, startTime, mailPath);

		long stopTime = System.currentTimeMillis();
		long timeElapsed = stopTime - startTime;
		System.out.println("Time Taken : " + ((timeElapsed / 1000) / 60)
				+ " minutes");

		daoObj.updateLnDetails();
		uploadFlg = true;

		return uploadFlg;
	}

	public static boolean mailSendflg(int updateCnt, boolean uploadFlg,
			long startTime, String mailPath) {
		boolean mailFlg = false;
		Date localDate = new Date();
		SimpleDateFormat dtformat = new SimpleDateFormat("dd/MM/yyyy");
		String userName = bundle.getProperty("EMAILID");
		String password = bundle.getProperty("PASSWORD");
		String smtpServer = bundle.getProperty("SMTPSERVER");
		String toMail = bundle.getProperty("TOLIST");
		String ccMail = bundle.getProperty("CCLIST");
		String bccMail = bundle.getProperty("BCCLIST");

		String subject = "Watchlist datafeed for LexisNexis has been uploaded for Date "
				+ dtformat.format(localDate);
		// String password = "";
		String mailText = "";
		ArrayList<String> toList = new ArrayList<String>();
		ArrayList<String> cc = new ArrayList<String>();
		ArrayList<String> bcc = new ArrayList<String>();

		ArrayList<String> maillist = new ArrayList<String>(Arrays.asList(toMail
				.split("~")));
		for (int i = 0; i < maillist.size(); i++) {
			toList.add(maillist.get(i));
		}

		ArrayList<String> cclist = new ArrayList<String>(Arrays.asList(ccMail
				.split("~")));
		for (int i = 0; i < cclist.size(); i++) {
			cc.add(cclist.get(i));

		}
		ArrayList<String> bcclist = new ArrayList<String>(Arrays.asList(bccMail
				.split("~")));
		for (int i = 0; i < cclist.size(); i++) {
			bcc.add(bcclist.get(i));

		}

		try {
			if (updateCnt > 0) {
				mailFlg = true;
				System.out.println("Data imported successfully...");
				mailText = "LexisNexis datafeed imported successfully.";
				// Mail Code here
				boolean value = SendMail.sendEmailNew(toList, cc, bcc, subject,
						mailText, smtpServer, userName, password, mailFile,
						mailPath);
				//NSDL Patch
				// boolean value=SendMail.sendEmailWithoutAuth(toList, cc, bcc,
				// subject, mailText,
				// smtpServer, userName);

				if (value) {
					System.out.println("Mail is send successfully...");
				} else {
					System.out.println("There was an issue while sending mail...");
				}

			} else {
				mailFlg = false;
				System.out.println("Something went wrong !!!");
				mailText = "LexisNexis dtafeed not imported successfully.";
				boolean value = SendMail.sendEmailKYC(toList, cc, bcc, subject,
						mailText, smtpServer, userName, password);
				//NSDL Patch
				// boolean value=SendMail.sendEmailWithoutAuth(toList, cc, bcc,
				// subject, mailText,
				// smtpServer, userName);

				if (value) {
					System.out.println("Mail is send successfully...");
				} else {
					System.out.println("There was an issue while sending mail...");
				}

			}

			long stopTime = System.currentTimeMillis();
			long timeElapsed = stopTime - startTime;
			// System.out.println("Count Updated..");
			System.out.println("Time Taken : " + ((timeElapsed / 1000) / 60)
					+ " minutes");

		} catch (Exception exe) {
			exe.getStackTrace();
		}

		return mailFlg;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String dir = System.getProperty("user.dir");
				System.out.println("Current Directory::" + dir);
				InputStream is = new FileInputStream(dir
						+ "/aml-config.properties");
				bundle.load(is);
				is.close();
				isFilePresent(bundle.getProperty("LNSOURCEPATH"));
				Thread.sleep(1000 * 300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Thread t = new Thread(new ReadingLexisNexis());
		t.start();

	}

}
