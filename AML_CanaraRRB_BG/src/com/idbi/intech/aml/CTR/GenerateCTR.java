package com.idbi.intech.aml.CTR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.idbi.intech.aml.STR.AccountHolderType;
import com.idbi.intech.aml.STR.AccountRiskRating;
import com.idbi.intech.aml.STR.AccountStatus;
import com.idbi.intech.aml.STR.AccountType;
import com.idbi.intech.aml.STR.Address;
import com.idbi.intech.aml.STR.Batch.Report.Account;
import com.idbi.intech.aml.STR.Batch.Report.Account.AccountDetails;
import com.idbi.intech.aml.STR.Batch.Report.Account.Branch;
import com.idbi.intech.aml.STR.Batch.Report.Account.Branch.BranchDetails;
import com.idbi.intech.aml.STR.Batch.Report.Account.PersonDetails;
import com.idbi.intech.aml.STR.Batch.Report.Account.PersonDetails.Individual;
import com.idbi.intech.aml.STR.Batch.Report.Account.PersonDetails.LegalPerson;
import com.idbi.intech.aml.STR.Batch.Report.Account.Transaction;
import com.idbi.intech.aml.STR.BatchDetails;
import com.idbi.intech.aml.STR.BatchHeader;
import com.idbi.intech.aml.STR.BatchType;
import com.idbi.intech.aml.STR.BranchRefNumType;
import com.idbi.intech.aml.STR.ConstitutionType;
import com.idbi.intech.aml.STR.CountryCode;
import com.idbi.intech.aml.STR.CurrencyCode;
import com.idbi.intech.aml.STR.DataSource;
import com.idbi.intech.aml.STR.DebitCredit;
import com.idbi.intech.aml.STR.Gender;
import com.idbi.intech.aml.STR.IdentificationType;
import com.idbi.intech.aml.STR.LEAInformed;
import com.idbi.intech.aml.STR.OperationalMode;
import com.idbi.intech.aml.STR.Phone;
import com.idbi.intech.aml.STR.PrincipalOfficer;
import com.idbi.intech.aml.STR.PriorityRating;
import com.idbi.intech.aml.STR.ReasonForRevision;
import com.idbi.intech.aml.STR.RelationalFlag;
import com.idbi.intech.aml.STR.ReportCoverage;
import com.idbi.intech.aml.STR.ReportingEntity;
import com.idbi.intech.aml.STR.ReportingEntityCategory;
import com.idbi.intech.aml.STR.SourceOfAlert;
import com.idbi.intech.aml.STR.StateCode;
import com.idbi.intech.aml.STR.SuspicionDetails;
import com.idbi.intech.aml.STR.TransactionMode;
import com.idbi.intech.aml.STR.YesNo;
import com.idbi.intech.aml.bg_process.AMLConstants;
import com.idbi.intech.aml.command.OpenAlertCommand;
import com.idbi.intech.aml.common.CommonMappingMethod;
import com.idbi.intech.aml.common.CommonMethod;
import com.idbi.intech.aml.databeans.STRConfigFileBean;
import com.idbi.intech.aml.util.InfoLogger;

public class GenerateCTR {

	/**
	 * 
	 * @param reportId
	 * @param infoLogger
	 * @return
	 * @throws JAXBException
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public void mainCTR(InfoLogger infoLogger, String reportId, String reportType, String isMaster) throws JAXBException, XMLStreamException,
			IOException {

		infoLogger.logVerboseText("GenerateCTR", "mainCTR", "Start");

		/********************************* xml logic *********************************/
		ResourceBundle rb = ResourceBundle.getBundle("AMLProp");

		String directory = "";

		if (reportType.equalsIgnoreCase("CTR")) {
			directory = rb.getString("CTRXML");
		} else if (reportType.equalsIgnoreCase("NTR")) {
			directory = rb.getString("NTRXML");
		} else if (reportType.equalsIgnoreCase("STR")) {
			directory = rb.getString("STRXML");
		} else if (reportType.equalsIgnoreCase("SIDBI")) {
			directory = rb.getString("SIDBICTRXML");
		}

		String zipDirectory = directory + reportType + "_ARF_" + rb.getString("FIUREID") + "_" + reportId;

		new File(zipDirectory).mkdir();

		int fileCount = 1;
		String outFile = "";

		// Set up out put file.
		File file = null;
		FileOutputStream outputStream = null;
		XMLOutputFactory outputFactory = null;
		XMLStreamWriter xmlStreamWriter = null;

		// Set up JAXB for Contact class
		Marshaller marshaller = JAXBContext.newInstance(Account.class).createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("jaxb.fragment", Boolean.TRUE);

		/********************************* business logic *********************************/

		BatchHeader batchHeader = null;
		ReportingEntity reportingEntity = null;
		BatchDetails batchDetails = null;
		SuspicionDetails suspicionDetails = null;

		Account account = null;

		Branch branch = null;
		BranchDetails branchDetails = null;

		AccountDetails acDetails = null;
		List<PersonDetails> listPersonDet = null;
		PersonDetails personDetails = null;

		LegalPerson legalPerson = null;
		Individual individual = null;

		Transaction transaction = null;
		List<Transaction> listTransaction = null;

		Address address = null;
		Phone phone = null;

		try {

			OpenAlertCommand oacObj = new OpenAlertCommand();
			oacObj.setLogger(infoLogger);
			oacObj.ctrXMLDetails(reportId, reportType, isMaster);

			STRConfigFileBean configFileBean = new STRConfigFileBean();
			configFileBean = oacObj.getConfigFileBean();
			configFileBean.setReport_id(reportId);

			ObjectFactory factory = new ObjectFactory();

			int i = 0;

			while (true) {
				//outFile = zipDirectory + "//" + reportId + "_" + fileCount++ + ".xml";
				outFile = zipDirectory + "//" + reportType + "_ARF_" + rb.getString("FIUREID") + "_" + reportId + "_" + fileCount++ + ".xml";

				// Set up out put file.
				file = new File(outFile);
				outputStream = new FileOutputStream(file);
				outputFactory = XMLOutputFactory.newInstance();
				xmlStreamWriter = outputFactory.createXMLStreamWriter(outputStream, "UTF-8");

				// Pre-fill the file
				xmlStreamWriter.writeStartDocument("UTF-8", "1.0");

				xmlStreamWriter.writeStartElement("Batch");

				// ReportType
				xmlStreamWriter.writeStartElement("ReportType");
				if (reportType.equalsIgnoreCase("SIDBI")) {
					xmlStreamWriter.writeCharacters("CTR");
				} else {
					xmlStreamWriter.writeCharacters(reportType);
				}
				xmlStreamWriter.writeEndElement();

				// ReportFormatType
				xmlStreamWriter.writeStartElement("ReportFormatType");
				xmlStreamWriter.writeCharacters("ARF");
				xmlStreamWriter.writeEndElement();

				// BatchHeader
				batchHeader = factory.createBatchHeader();

				batchHeader.setDataStructureVersion("2");
				batchHeader.setGenerationUtilityVersion("00000");
				batchHeader.setDataSource(DataSource.XML);

				marshaller.marshal(batchHeader, xmlStreamWriter);

				// ReportingEntity

				reportingEntity = factory.createReportingEntity();

				reportingEntity.setReportingEntityName(rb.getString("ReportingEntityName"));
				reportingEntity.setReportingEntityCategory(ReportingEntityCategory.BAPUB);

				// client doesn't have this number
				reportingEntity.setRERegistrationNumber("22");
				reportingEntity.setFIUREID(rb.getString("FIUREID"));

				marshaller.marshal(reportingEntity, xmlStreamWriter);

				// PrincipalOfficer

				PrincipalOfficer principalOfficer = factory.createPrincipalOfficer();

				principalOfficer.setPOName(rb.getString("NameOfPrincipalOfficer"));
				principalOfficer.setPODesignation(rb.getString("Designation"));

				address = new Address();

				address.setAddress(rb.getString("Address"));
				address.setCity(rb.getString("City"));
				address.setStateCode(StateCode.valueOf("HR"));
				address.setPinCode(rb.getString("Pin"));
				address.setCountryCode(CountryCode.valueOf("IN"));

				principalOfficer.setPOAddress(address);

				phone = new Phone();

				phone.setTelephone(rb.getString("Tel"));
				phone.setMobile(rb.getString("Mobile"));
				phone.setFax(rb.getString("Fax"));

				principalOfficer.setPOPhone(phone);

				principalOfficer.setPOEmail(rb.getString("Email"));

				marshaller.marshal(principalOfficer, xmlStreamWriter);

				// BatchDetails

				batchDetails = factory.createBatchDetails();

				batchDetails.setBatchNumber(String.format("%8s", reportId).replace(' ', '0'));

				DateFormat dateFormat = new SimpleDateFormat("yyyy");
				Date date = new Date();
				Date date1 = new Date();
				date1.setMonth(date1.getMonth()-1);
				
				String year = dateFormat.format(date);
				String pre_year=dateFormat.format(date1);
				
				dateFormat = new SimpleDateFormat("MM");
				String pre_month = dateFormat.format(date1);
				
				dateFormat = new SimpleDateFormat("MM");
				String month = dateFormat.format(date);

				dateFormat = new SimpleDateFormat("dd");
				String day = dateFormat.format(date);
				//String day = "16";

				batchDetails.setBatchDate(year + "-" + String.format("%02d", Integer.parseInt(month)) + "-"
						+ String.format("%02d", Integer.parseInt(day)));

				batchDetails.setMonthOfReport(pre_month);
				batchDetails.setYearOfReport(pre_year);
				batchDetails.setOperationalMode(OperationalMode.P);
				batchDetails.setBatchType(BatchType.N);
				batchDetails.setOriginalBatchID(new BigInteger("0"));
				batchDetails.setReasonOfRevision(ReasonForRevision.N);
				batchDetails.setPKICertificateNum("0000000000");

				marshaller.marshal(batchDetails, xmlStreamWriter);

				// for account
				

				

				// SuspicionDetails

				if (reportType.equalsIgnoreCase("STR")) {

					suspicionDetails = factory.createSuspicionDetails();

					suspicionDetails.setSourceOfAlert(SourceOfAlert.TM);
					suspicionDetails.setSuspicionDueToProceedsOfCrime(YesNo.N);
					suspicionDetails.setSuspicionDueToComplexTrans(YesNo.Y);
					suspicionDetails.setSuspicionDueToNoEcoRationale(YesNo.N);
					suspicionDetails.setSuspicionOfFinancingOfTerrorism(YesNo.N);
					suspicionDetails.setAttemptedTransaction(YesNo.N);
					suspicionDetails.setGroundsOfSuspicion(configFileBean.getComments());

					suspicionDetails.setLEAInformed(LEAInformed.X);
					suspicionDetails.setPriorityRating(PriorityRating.P_3);
					suspicionDetails.setReportCoverage(ReportCoverage.C);
					suspicionDetails.setAdditionalDocuments(YesNo.N);

					marshaller.marshal(suspicionDetails, xmlStreamWriter);
				}

				/******************************** Account *************************/

				if (configFileBean.getAccounts().size() > 0) {

					for (; i < configFileBean.getAccounts().size(); i++) {
						xmlStreamWriter.writeStartElement("Report");
						outputStream.flush();
						int rSrl=i+1;
						xmlStreamWriter.writeStartElement("ReportSerialNum");
						xmlStreamWriter.writeCharacters(String.valueOf(rSrl));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("OriginalReportSerialNum");
						xmlStreamWriter.writeCharacters("0");
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("MainPersonName");
						xmlStreamWriter.writeCharacters(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_name_acc_holder());
						xmlStreamWriter.writeEndElement();
						
						// System.out.println("\nCounter : " + (i + 1));
						// System.out.println("account : " +
						// configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_acc_number());
						// System.out.println("indi : " +
						// configFileBean.getAccounts().get(i).getIndividualFileBeans().size());
						// System.out.println("lpe : " +
						// configFileBean.getAccounts().get(i).getStrlpefIleBeans().size());
						// System.out.println("tran : " +
						// configFileBean.getAccounts().get(i).getTransactionFileBeans().size());

						account = factory.createAccount();

						acDetails = factory.createBatchReportAccountAccountDetails();

						acDetails.setAccountNumber(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_acc_number().trim());
						acDetails.setAccountType(AccountType.valueOf(CommonMappingMethod.accountTypeMapping(configFileBean.getAccounts().get(i)
								.getAccountFileBean().getStraf_type_of_acc())));
						acDetails.setHolderName(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_name_acc_holder());
						acDetails.setAccountHolderType(AccountHolderType.valueOf(configFileBean.getAccounts().get(i).getAccountFileBean()
								.getStraf_type_of_accholder()));
						acDetails.setAccountStatus(AccountStatus.valueOf("A"));

						if (reportType.equalsIgnoreCase("STR")) {

							acDetails.setDateOfOpening(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_date_of_opening()
									.substring(0, 4)
									+ "-"
									+ String.format(
											"%02d",
											Integer.parseInt(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_date_of_opening()
													.substring(5, 7)))
									+ "-"
									+ String.format(
											"%02d",
											Integer.parseInt(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_date_of_opening()
													.substring(8, 10))));

						} else {

							acDetails.setDateOfOpening(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_date_of_opening()
									.substring(4, 8)
									+ "-"
									+ String.format(
											"%02d",
											Integer.parseInt(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_date_of_opening()
													.substring(2, 4)))
									+ "-"
									+ String.format(
											"%02d",
											Integer.parseInt(configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_date_of_opening()
													.substring(0, 2))));

						}

						String riskRate = configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_risk_category().equals("A") ? "A_1"
								: configFileBean.getAccounts().get(i).getAccountFileBean().getStraf_risk_category().equals("B") ? "A_2" : "A_3";
						acDetails.setRiskRating(AccountRiskRating.valueOf(riskRate));

						acDetails.setCumulativeCreditTurnover(new BigInteger(configFileBean.getAccounts().get(i).getAccountFileBean()
								.getStraf_cum_cr_turnover().trim()));
						acDetails.setCumulativeDebitTurnover(new BigInteger(configFileBean.getAccounts().get(i).getAccountFileBean()
								.getStraf_cum_dr_turnover().trim()));
						acDetails.setCumulativeCashDepositTurnover(new BigInteger(configFileBean.getAccounts().get(i).getAccountFileBean()
								.getStraf_cash_dep_turnover().trim()));
						acDetails.setCumulativeCashWithdrawalTurnover(new BigInteger(configFileBean.getAccounts().get(i).getAccountFileBean()
								.getStraf_cash_wdrwl_turnover().trim()));
						acDetails.setNoTransactionsTobeReported(YesNo.N);

						/************************ BRANCH *************************/

						branch = factory.createBatchReportAccountBranch();
						branchDetails = factory.createBatchReportAccountBranchBranchDetails();

						branch.setBranchRefNumType(BranchRefNumType.R); // TODO
						branch.setBranchRefNum(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_ref_no());
						branchDetails.setBranchName(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_name());

						address = factory.createBatchReportAddress();

						address.setAddress(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_address1()
								+ configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_address2());
						address.setCity(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_address4());
						address.setStateCode(StateCode.valueOf(CommonMappingMethod.stateMapping(configFileBean.getAccounts().get(i)
								.getBranchFileBean().getStrbf_branch_address5()==null?"XXX":configFileBean.getAccounts().get(i)
										.getBranchFileBean().getStrbf_branch_address5().trim())));

						address.setPinCode(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_pincode());
						address.setCountryCode(CountryCode.IN);

						branchDetails.setBranchAddress(address);

						phone = factory.createBatchReportPhone();

						phone.setTelephone(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_telno());
						phone.setMobile("");
						phone.setFax(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_fax());

						branchDetails.setBranchPhone(phone);

						branchDetails.setBranchEmail(configFileBean.getAccounts().get(i).getBranchFileBean().getStrbf_branch_email());

						branch.setBranchDetails(branchDetails);
						account.setBranch(branch);

						address = null;
						branch = null;
						branchDetails = null;
						phone = null;

						/******************************* PERSONAL DETAILS ********************************/

						listPersonDet = new ArrayList<PersonDetails>();

						if (configFileBean.getAccounts().get(i).getIndividualFileBeans().size() > 0) {

							for (int k = 0; k < configFileBean.getAccounts().get(i).getIndividualFileBeans().size(); k++) {

								/*********************************** INDIVIDUAL *******************************/

								personDetails = factory.createBatchReportAccountPersonDetails();

								personDetails.setPersonName(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
										.getStrif_full_name_ind());
								personDetails.setCustomerId(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
										.getStrif_customer_id());

								personDetails.setRelationFlag(RelationalFlag.valueOf(configFileBean.getAccounts().get(i)
										.getIndividualFileBeans().get(k).getStrif_relation_flag()));

								address = factory.createBatchReportAddress();

								address.setAddress(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_comm_address1()
										+ configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_comm_address2());

								if (reportType.equalsIgnoreCase("STR")) {
									address.setCity(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_comm_address4());
								} else {
									address.setCity(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_comm_address3());
								}

								if (reportType.equalsIgnoreCase("STR")) {
									address.setStateCode(StateCode.valueOf(CommonMappingMethod.stateMapping(configFileBean.getAccounts().get(i)
											.getIndividualFileBeans().get(k).getStrif_comm_address5().trim())));
								} else {
									address.setStateCode(StateCode.valueOf(CommonMappingMethod.stateMapping(configFileBean.getAccounts().get(i)
											.getIndividualFileBeans().get(k).getStrif_comm_address4().trim())));
								}

								address.setPinCode(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_comm_pincode());
								address.setCountryCode(CountryCode.IN);

								personDetails.setCommunicationAddress(address);

								phone = factory.createBatchReportPhone();

								phone.setTelephone(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_contact_telno());
								phone.setMobile(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_contact_mobile());
								phone.setFax("");

								personDetails.setPhone(phone);

								personDetails.setEmail(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
										.getStrif_contact_email());
								personDetails.setPAN(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_pan());

								// INDIVIDUAL

								individual = factory.createBatchReportAccountPersonDetailsIndividual();

								individual.setGender(Gender.valueOf(CommonMappingMethod.genderMapping(configFileBean.getAccounts().get(i)
										.getIndividualFileBeans().get(k).getStrif_sex())));

								if (!configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_dob().trim()
										.equalsIgnoreCase("")
										&& !configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_dob().trim()
												.equalsIgnoreCase("00000000")) {

									if (reportType.equalsIgnoreCase("STR")) {

										individual.setDateOfBirth(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
												.getStrif_dob().substring(0, 4)
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
																.getStrif_dob().substring(5, 7)))
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
																.getStrif_dob().substring(8, 10))));

									} else {

										individual.setDateOfBirth(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
												.getStrif_dob().substring(4, 8)
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
																.getStrif_dob().substring(2, 4)))
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k)
																.getStrif_dob().substring(0, 2))));

									}

								}

								if (!configFileBean.getAccounts().get(i).getIndividualFileBeans().get(k).getStrif_identification_type().trim()
										.equalsIgnoreCase("")) {
									individual.setIdentificationType(IdentificationType.valueOf(configFileBean.getAccounts().get(i)
											.getIndividualFileBeans().get(k).getStrif_identification_type()));
								} else {
									individual.setIdentificationType(IdentificationType.Z);
								}

								individual.setNationality(CountryCode.IN);
								

								personDetails.setIndividual(individual);

								listPersonDet.add(personDetails);

								personDetails = null;
								individual = null;
								address = null;
								phone = null;
							}
						}

						/************************** LEGAL PERSON ***********************************/

						if (configFileBean.getAccounts().get(i).getStrlpefIleBeans().size() > 0) {

							for (int l = 0; l < configFileBean.getAccounts().get(i).getStrlpefIleBeans().size(); l++) {

								personDetails = factory.createBatchReportAccountPersonDetails();

								personDetails.setPersonName(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
										.getStrlf_full_name_lpe());
								personDetails.setCustomerId(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
										.getStrlf_customer_id());
								personDetails.setRelationFlag(RelationalFlag.valueOf(configFileBean.getAccounts().get(i).getStrlpefIleBeans()
										.get(l).getStrlf_relation_flag()));

								address = factory.createBatchReportAddress();

								address.setAddress(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_comm_address1()
										+ configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_comm_address2());

								if (reportType.equalsIgnoreCase("STR")) {
									address.setCity(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_comm_address4());
								} else {
									address.setCity(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_comm_address3());
								}

								if (reportType.equalsIgnoreCase("STR")) {
									address.setStateCode(StateCode.valueOf(CommonMappingMethod.stateMapping(configFileBean.getAccounts().get(i)
											.getStrlpefIleBeans().get(l).getStrlf_comm_address5().trim())));
								} else {
									address.setStateCode(StateCode.valueOf(CommonMappingMethod.stateMapping(configFileBean.getAccounts().get(i)
											.getStrlpefIleBeans().get(l).getStrlf_comm_address4().trim())));
								}

								address.setPinCode(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_comm_pincode());

								if (reportType.equalsIgnoreCase("STR")) {
									address.setCountryCode(CountryCode.IN);
								} else {
									//address.setCountryCode(CountryCode.valueOf(CommonMappingMethod.countryMapping(configFileBean.getAccounts()
									//		.get(i).getStrlpefIleBeans().get(l).getStrlf_comm_address5().trim())));
									address.setCountryCode(CountryCode.IN);
								}

								personDetails.setCommunicationAddress(address);

								phone = factory.createBatchReportPhone();

								phone.setTelephone(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_second_telno());
								phone.setMobile("");
								phone.setFax(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_second_fax());

								personDetails.setPhone(phone);

								personDetails.setEmail(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_contact_email());
								personDetails.setPAN(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_pan());

								// LEGAL PERSON

								legalPerson = factory.createBatchReportAccountPersonDetailsLegalPerson();

								legalPerson.setConstitutionType(ConstitutionType.valueOf(configFileBean.getAccounts().get(i)
										.getStrlpefIleBeans().get(l).getStrlf_constitution_type()));

								if (!configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_incorporation_date().trim()
										.equalsIgnoreCase("")
										&& !configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l).getStrlf_incorporation_date().trim()
												.equalsIgnoreCase("00000000")) {

									if (reportType.equalsIgnoreCase("STR")) {

										legalPerson.setDateOfIncorporation(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
												.getStrlf_incorporation_date().substring(0, 4)
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
																.getStrlf_incorporation_date().substring(5, 7)))
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
																.getStrlf_incorporation_date().substring(8, 10))));

									} else {

										legalPerson.setDateOfIncorporation(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
												.getStrlf_incorporation_date().substring(4, 8)
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
																.getStrlf_incorporation_date().substring(2, 4)))
												+ "-"
												+ String.format(
														"%02d",
														Integer.parseInt(configFileBean.getAccounts().get(i).getStrlpefIleBeans().get(l)
																.getStrlf_incorporation_date().substring(0, 2))));

									}
								}

								legalPerson.setCountryCode(CountryCode.IN);

								personDetails.setLegalPerson(legalPerson);
								listPersonDet.add(personDetails);

								personDetails = null;
								legalPerson = null;
								address = null;
								phone = null;

							}
						}

						/*********************** TRANSACTION **************************/

						listTransaction = new ArrayList<Transaction>();
						
						System.out.println("reportType :: "+reportType);

						if (configFileBean.getAccounts().get(i).getTransactionFileBeans().size() > 0) {

							for (int m = 0; m < configFileBean.getAccounts().get(i).getTransactionFileBeans().size(); m++) {

								transaction = factory.createBatchReportAccountTransaction();

								if (reportType.equalsIgnoreCase("STR")) {

									transaction.setDateOfTransaction(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
											.getStrtf_tran_date().substring(0, 4)
											+ "-"
											+ String.format(
													"%02d",
													Integer.parseInt(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
															.getStrtf_tran_date().substring(5, 7)))
											+ "-"
											+ String.format(
													"%02d",
													Integer.parseInt(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
															.getStrtf_tran_date().substring(8, 10))));

								} else {

									transaction.setDateOfTransaction(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
											.getStrtf_tran_date().substring(4, 8)
											+ "-"
											+ String.format(
													"%02d",
													Integer.parseInt(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
															.getStrtf_tran_date().substring(2, 4)))
											+ "-"
											+ String.format(
													"%02d",
													Integer.parseInt(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
															.getStrtf_tran_date().substring(0, 2))));

								}

								JAXBElement<String> tranID = factory.createBatchReportAccountTransactionTransactionID(configFileBean
										.getAccounts().get(i).getTransactionFileBeans().get(m).getStrtf_tran_id());
								transaction.setTransactionID(tranID);

								transaction.setTransactionMode(TransactionMode.valueOf(configFileBean.getAccounts().get(i)
										.getTransactionFileBeans().get(m).getStrtf_tran_mode().equalsIgnoreCase("L") ? "A" : configFileBean
										.getAccounts().get(i).getTransactionFileBeans().get(m).getStrtf_tran_mode().equalsIgnoreCase("T") ? "E"
										: "C"));
								transaction.setDebitCredit(DebitCredit.valueOf(configFileBean.getAccounts().get(i).getTransactionFileBeans()
										.get(m).getStrtf_dr_cr()));
								if (configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m).getStrtf_amount() == null) {
									transaction.setAmount(new BigInteger("0"));
								} else {
									transaction.setAmount(new BigInteger(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m)
											.getStrtf_amount()));
								}

								transaction.setCurrency(CurrencyCode.INR);

								// PRODUCT TRANSACTION

								transaction.setRemarks(configFileBean.getAccounts().get(i).getTransactionFileBeans().get(m).getStrtf_remarks());

								listTransaction.add(transaction);

								transaction = null;
							}
						}
						
						System.out.println(listTransaction);

						infoLogger.logVerboseText("GenerateCTR", "mainCTR", "listTransaction is:" + listTransaction.size());

						account.setTransaction(listTransaction);
						account.setAccountDetails(acDetails);
						account.setPersonDetails(listPersonDet);

						// write out the account object
						marshaller.marshal(account, xmlStreamWriter);
						xmlStreamWriter.writeEndElement();
						if (file.length() > AMLConstants.XML_FRAGMENT_SIZE) {
							i++;
							break;
						}
						
					} // loop : account

				} // if : account

				
				xmlStreamWriter.writeEndElement();
				outputStream.flush();
				outputStream.close();

				// all account is accessed
				if (i == configFileBean.getAccounts().size()) {
					break;
				}

			} // loop : while

			if (CommonMethod.makeZipFile(zipDirectory)) {
				CommonMethod.deleteDir(new File(zipDirectory));
			}

		} catch (Exception e) {
			e.printStackTrace();
			infoLogger.logExceptionText("GenerateCTR", "mainCTR", "Exception : " + e);
		}

	}
}
