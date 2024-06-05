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
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ReadingLexisNexisFile {

	public static void isFilePresent(String dirPath) throws JAXBException,
			IOException, XMLStreamException, URISyntaxException {
		
		String filePath = "";
		boolean delFlg=false;
		
		File file = new File(dirPath);
		
		Date currDt = new Date();
		SimpleDateFormat format = new SimpleDateFormat(
				"ddMMyyyy");
		String folderName = format.format(currDt);

		String destDir = dirPath + "\\" + folderName;
		
		String fileList[] = file.list();
		if (fileList.length > 0) {
			for (int i = 0; i < fileList.length; i++)
				if (fileList[i].lastIndexOf(".") != -1
						&& fileList[i].lastIndexOf(".") != 0) {
					String fileExt = fileList[i].substring(fileList[i]
							.lastIndexOf(".") + 1);
					if (fileExt.equals("xml")) {
						System.out.println(fileList[i]);
						filePath = dirPath + fileList[i];
						System.out.println(filePath);
						//delFlg=true;
						delFlg=uploadXmlFile(filePath);
						if(delFlg){
							new File(filePath).delete();
						}
					} else {
						filePath = dirPath + fileList[i];
						destDir = dirPath + "\\" + folderName;
						if (new File(destDir).mkdir()) {
							moveFiles(new File(filePath), new File(destDir
									+ "\\" + fileList[i]));
							new File(filePath).delete();
						}
					}
				}
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

	public static boolean uploadXmlFile(String filePath) throws JAXBException,
			IOException, XMLStreamException, URISyntaxException {

		boolean uploadFlg = false;
		int updateCnt =0;
		JAXBContext jaxbContext = null;
		Unmarshaller um = null;
		XMLStreamReader reader = null;
		EntititesRecord recObj = new EntititesRecord();

		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

		// creating object for DAO
		LexisNexisDao daoObj = new LexisNexisDao();

		// ----------------------------------------------EntitiesSources---------------------------------------------------------------------
		// interested in "EntitiesSources" elements only. Skip up to first
		// "EntitiesSources"
		int cnt = 0;
		/*
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

		recObj.setEntitiesSources(cnt);
		System.out.println("EntitiesSources Uploaded");

		// ---------------------------------------------------EntitiesEntryTypes----------------------------------------------------------------
		// interested in "EntitiesEntryType" elements only. Skip up to first
		// "EntitiesEntryType"
		cnt = 0;
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

			recObj.setEntitiesEntryType(cnt);
			System.out.println("EntitiesEntryTypes Uploaded");
		
		
		
		// ---------------------------------------------------EntitiesCategories----------------------------------------------------------------
		// interested in "EntitiesCategories" elements only. Skip up to first
		// "EntitiesCategories"
		cnt = 0;
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

		recObj.setEntitiesCategory(cnt);
		System.out.println("EntitiesCategories Uploaded");
		
		// ---------------------------------------------------EntitiesSubCategories----------------------------------------------------------------
		// interested in "EntitiesSubCategories" elements only. Skip up to first
		// "EntitiesSubCategories"
		cnt = 0;
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

		recObj.setEntitiesSubCategory(cnt);
		System.out.println("EntitiesSubCategories Uploaded");
		
		// ---------------------------------------------------EntitiesCountries----------------------------------------------------------------
		// interested in "EntitiesCountries" elements only. Skip up to first
		// "EntitiesCountries"
		cnt = 0;
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

		recObj.setEntitiesCountriesCnt(cnt);
		System.out.println("EntitiesCountries Uploaded");
		
		// ---------------------------------------------------EntitiesLevel----------------------------------------------------------------
		// interested in "EntitiesLevel" elements only. Skip up to first
		// "EntitiesLevel"
		cnt = 0;
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

		recObj.setEntitiesLevelsCnt(cnt);
		System.out.println("EntitiesLevel Uploaded");*/
		
		// ------------------------------------------------Entities-------------------------------------------------------------------
		// interested in "Entities" elements only. Skip up to first "Entities"
//		cnt = 0;
//		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
//				filePath)));
//		jaxbContext = JAXBContext.newInstance(Entities.class);
//
//		// unmarshall: String "source" to Java object
//		um = jaxbContext.createUnmarshaller();
//		while (reader.hasNext()
//				&& (!reader.isStartElement() || !reader.getLocalName().equals(
//						"Entities"))) {
//			reader.next();
//		}
//
//		// read a Entities at a time
//		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
//			JAXBElement<Entities> boolElement = um.unmarshal(reader,
//					Entities.class);
//			Entities entity = boolElement.getValue();
//			if (entity.getEnt_ID() != null && entity.getFirstName() != null) {
//				//System.out.println(entity.getEnt_ID());
//				cnt += daoObj.insertEntityDetails(entity);
//			}
//
//			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
//				reader.next();
//			}
//		}
//
//		recObj.setEntitiesCount(cnt);
//		System.out.println("Entities Uploaded");
		
		// ------------------------------------------------EntitiesAddresses-------------------------------------------------------------------
		// interested in "EntitiesAddresses" elements only. Skip up to first
		// "EntitiesAddresses"
//		cnt = 0;
//		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
//				filePath)));
//		jaxbContext = JAXBContext.newInstance(EntitiesAddresses.class);
//
//		// unmarshall: String "source" to Java object
//		um = jaxbContext.createUnmarshaller();
//		while (reader.hasNext()
//				&& (!reader.isStartElement() || !reader.getLocalName().equals(
//						"EntitiesAddresses"))) {
//			reader.next();
//		}
//
//		// read a EntitiesAddresses at a time
//		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
//			JAXBElement<EntitiesAddresses> boolElement = um.unmarshal(reader,
//					EntitiesAddresses.class);
//			EntitiesAddresses entityAddresses = boolElement.getValue();
//			if (entityAddresses.getAddress_ID() != null) {
//				cnt += daoObj.insertEntityAddresses(entityAddresses);
//			}
//
//			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
//				reader.next();
//			}
//		}
//		recObj.setEntitiesAddress(cnt);
//		System.out.println("EntitiesAddresses Uploaded");
		
		// ------------------------------------------------EntitiesRefDefs-------------------------------------------------------------------
		// interested in "EntitiesRefDefs" elements only. Skip up to first
		// "EntitiesRefDefs"
//		cnt = 0;
//		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
//				filePath)));
//		jaxbContext = JAXBContext.newInstance(EntitiesRelDefs.class);
//
//		// unmarshall: String "source" to Java object
//		um = jaxbContext.createUnmarshaller();
//		while (reader.hasNext()
//				&& (!reader.isStartElement() || !reader.getLocalName().equals(
//						"EntitiesRelDefs"))) {
//			reader.next();
//		}
//
//		// read a EntitiesRefDefs at a time
//		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
//			JAXBElement<EntitiesRelDefs> boolElement = um.unmarshal(reader,
//					EntitiesRelDefs.class);
//			EntitiesRelDefs entityRelDefs = boolElement.getValue();
//			if (entityRelDefs.getRelationID() != null) {
//				cnt += daoObj.insertEntityRelDefs(entityRelDefs);
//			}
//
//			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
//				reader.next();
//			}
//		}
//		recObj.setEntitiesRelDefs(cnt);
//		System.out.println("EntitiesRelDefs Uploaded");
		
		 //------------------------------------------------EntitiesRelationships-------------------------------------------------------------------
		 //interested in "EntitiesRelationships" elements only. Skip up to first
		 //"EntitiesRelationships"
//		 cnt = 0;
//		 reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
//		 filePath)));
//		 jaxbContext = JAXBContext.newInstance(EntitiesRelationships.class);
//		
//		 // unmarshall: String "source" to Java object
//		 um = jaxbContext.createUnmarshaller();
//		 while (reader.hasNext()
//		 && (!reader.isStartElement() || !reader.getLocalName().equals(
//		 "EntitiesRelationships"))) {
//		 reader.next();
//		 }
//		
//		 // read a EntitiesRelationships at a time
//		 while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
//		 JAXBElement<EntitiesRelationships> boolElement = um.unmarshal(
//		 reader, EntitiesRelationships.class);
//		 EntitiesRelationships entityRelationships = boolElement.getValue();
//		 if (entityRelationships.getRID() != null) {
//		 cnt+=daoObj.insertEntityRelationships(entityRelationships);
//		 }
//		
//		 if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
//		 reader.next();
//		 }
//		 }
//		 recObj.setEntitiesRelationships(cnt);
//		 System.out.println("entitiesRelationships Uploaded");

		// ------------------------------------------------EntitiesDeletions-------------------------------------------------------------------
		// interested in "EntitiesDeletions" elements only. Skip up to first
		// "EntitiesDeletions"
//		cnt = 0;
//		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
//				filePath)));
//		jaxbContext = JAXBContext.newInstance(EntitiesDeletions.class);
//
//		// unmarshall: String "source" to Java object
//		um = jaxbContext.createUnmarshaller();
//		while (reader.hasNext()
//				&& (!reader.isStartElement() || !reader.getLocalName().equals(
//						"EntitiesDeletions"))) {
//			reader.next();
//		}
//
//		// read a EntitiesRefDefs at a time
//		while (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
//			JAXBElement<EntitiesDeletions> boolElement = um.unmarshal(reader,
//					EntitiesDeletions.class);
//			EntitiesDeletions entityRelationDeletions = boolElement.getValue();
//			if (entityRelationDeletions.getEnt_ID() != null
//					&& entityRelationDeletions.getDateDeleted() != null) {
//				cnt += daoObj
//						.insertEntityDeletedEntries(entityRelationDeletions);
//			}
//
//			if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
//				reader.next();
//			}
//		}
//
//		recObj.setEntitiesDeletions(cnt);
//		System.out.println("EntitiesDeletions Uploaded");
		
		// ------------------------------------------------SanctionsDOB-------------------------------------------------------------------
		// interested in "SanctionsDOB" elements only. Skip up to first
		// "SanctionsDOB"
		cnt = 0;
		reader = xmlFactory.createXMLStreamReader(new FileReader(new File(
				filePath)));
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

		recObj.setEntitiesSanctionDob(cnt);
		System.out.println("SanctionsDOB Uploaded");
		
		
		reader.close();

		//updateCnt = daoObj.insertRecordCnt(recObj);
		
		if(updateCnt>0){
			uploadFlg=true;
		}else{
			uploadFlg=false;
		}

		System.out.println("Count Updated..");
		return uploadFlg;
	}

	public static void main(String[] args) throws JAXBException, IOException,
			XMLStreamException, URISyntaxException {
		isFilePresent("D:\\WorldCompliance_Full_2018-05-01\\");
	}

}
