//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//

package com.idbi.intech.aml.CTR;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.idbi.intech.aml.STR.Address;
import com.idbi.intech.aml.STR.Batch.Report.Account;
import com.idbi.intech.aml.STR.BatchDetails;
import com.idbi.intech.aml.STR.BatchHeader;
import com.idbi.intech.aml.STR.Phone;
import com.idbi.intech.aml.STR.PrincipalOfficer;
import com.idbi.intech.aml.STR.ReportingEntity;
import com.idbi.intech.aml.STR.SuspicionDetails;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface com.idbiintech.str in the com.idbiintech.str package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _BatchReportAccountPersonDetailsIndividualIssuingAuthority_QNAME = new QName("", "IssuingAuthority");
	private final static QName _BatchReportAccountPersonDetailsIndividualIdentificationNumber_QNAME = new QName("", "IdentificationNumber");
	private final static QName _BatchReportAccountPersonDetailsIndividualPlaceOfIssue_QNAME = new QName("", "PlaceOfIssue");
	private final static QName _BatchReportAccountTransactionTransactionID_QNAME = new QName("", "TransactionID");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.idbiintech.str
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Account.AccountDetails }
	 * 
	 */
	public Account.AccountDetails createBatchReportAccountAccountDetails() {
		return new Account.AccountDetails();
	}

	/**
	 * Create an instance of {@link Account.Branch }
	 * 
	 */
	public Account.Branch createBatchReportAccountBranch() {
		return new Account.Branch();
	}

	/**
	 * Create an instance of {@link Account.PersonDetails.LegalPerson }
	 * 
	 */
	public Account.PersonDetails.LegalPerson createBatchReportAccountPersonDetailsLegalPerson() {
		return new Account.PersonDetails.LegalPerson();
	}

	/**
	 * Create an instance of {@link Phone }
	 * 
	 */
	public Phone createPhone() {
		return new Phone();
	}

	/**
	 * Create an instance of {@link Account }
	 * 
	 */
	public Account createAccount() {
		return new Account();
	}

	/**
	 * Create an instance of {@link Address }
	 * 
	 */
	public Address createBatchReportAddress() {
		return new Address();
	}

	/**
	 * Create an instance of {@link PrincipalOfficer }
	 * 
	 */
	public PrincipalOfficer createPrincipalOfficer() {
		return new PrincipalOfficer();
	}

	/**
	 * /** Create an instance of {@link BatchHeader }
	 * 
	 */
	public BatchHeader createBatchHeader() {
		return new BatchHeader();
	}

	/**
	 * /** Create an instance of {@link ReportingEntity }
	 * 
	 */
	public ReportingEntity createReportingEntity() {
		return new ReportingEntity();
	}

	/**
	 * /** Create an instance of {@link BatchDetails }
	 * 
	 */
	public BatchDetails createBatchDetails() {
		return new BatchDetails();
	}

	/*
	 * /** Create an instance of {@link Phone }
	 */
	public Phone createBatchReportPhone() {
		return new Phone();
	}

	/**
	 * /* /** Create an instance of {@link SuspicionDetails }
	 */
	public SuspicionDetails createSuspicionDetails() {
		return new SuspicionDetails();
	}

	/**
	 * Create an instance of {@link Account.PersonDetails.Individual }
	 * 
	 */
	public Account.PersonDetails.Individual createBatchReportAccountPersonDetailsIndividual() {
		return new Account.PersonDetails.Individual();
	}

	/**
	 * Create an instance of {@link Account.PersonDetails }
	 * 
	 */
	public Account.PersonDetails createBatchReportAccountPersonDetails() {
		return new Account.PersonDetails();
	}

	/**
	 * Create an instance of {@link Account.Transaction.ProductTransaction }
	 * 
	 */
	public Account.Transaction.ProductTransaction createBatchReportAccountTransactionProductTransaction() {
		return new Account.Transaction.ProductTransaction();
	}

	/**
	 * Create an instance of {@link Account.Transaction }
	 * 
	 */
	public Account.Transaction createBatchReportAccountTransaction() {
		return new Account.Transaction();
	}

	/**
	 * Create an instance of {@link Account.Branch.BranchDetails }
	 * 
	 */
	public Account.Branch.BranchDetails createBatchReportAccountBranchBranchDetails() {
		return new Account.Branch.BranchDetails();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "IssuingAuthority", scope = Account.PersonDetails.Individual.class)
	public JAXBElement<String> createBatchReportAccountPersonDetailsIndividualIssuingAuthority(String value) {
		return new JAXBElement<String>(_BatchReportAccountPersonDetailsIndividualIssuingAuthority_QNAME, String.class,
				Account.PersonDetails.Individual.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "IdentificationNumber", scope = Account.PersonDetails.Individual.class)
	public JAXBElement<String> createBatchReportAccountPersonDetailsIndividualIdentificationNumber(String value) {
		return new JAXBElement<String>(_BatchReportAccountPersonDetailsIndividualIdentificationNumber_QNAME, String.class,
				Account.PersonDetails.Individual.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "PlaceOfIssue", scope = Account.PersonDetails.Individual.class)
	public JAXBElement<String> createBatchReportAccountPersonDetailsIndividualPlaceOfIssue(String value) {
		return new JAXBElement<String>(_BatchReportAccountPersonDetailsIndividualPlaceOfIssue_QNAME, String.class,
				Account.PersonDetails.Individual.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "TransactionID", scope = Account.Transaction.class)
	public JAXBElement<String> createBatchReportAccountTransactionTransactionID(String value) {
		return new JAXBElement<String>(_BatchReportAccountTransactionTransactionID_QNAME, String.class, Account.Transaction.class, value);
	}

}
