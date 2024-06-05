//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//


package com.idbi.intech.aml.STR;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * com.idbiintech.str in the com.idbiintech.str package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _BatchReportAccountPersonDetailsIndividualIssuingAuthority_QNAME = new QName("", "IssuingAuthority");
    private final static QName _BatchReportAccountPersonDetailsIndividualIdentificationNumber_QNAME = new QName("", "IdentificationNumber");
    private final static QName _BatchReportAccountPersonDetailsIndividualPlaceOfIssue_QNAME = new QName("", "PlaceOfIssue");
    private final static QName _BatchReportAccountTransactionTransactionID_QNAME = new QName("", "TransactionID");
    private final static QName _BatchReport_QNAME=new QName("","Report");
    private final static QName _Batch_QNAME=new QName("","Batch");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.idbiintech.str
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BatchDetails }
     * 
     */
    public BatchDetails createBatchDetails() {
        return new BatchDetails();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.AccountDetails }
     * 
     */
    public Batch.Report.Account.AccountDetails createBatchReportAccountAccountDetails() {
        return new Batch.Report.Account.AccountDetails();
    }

    /**
     * Create an instance of {@link FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber }
     * 
     */
    public FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber createFIUReportSubmissionExternalLibraryFIUReportSubmissionExternalComplexTypeLibraryVersionNumber() {
        return new FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.Branch }
     * 
     */
    public Batch.Report.Account.Branch createBatchReportAccountBranch() {
        return new Batch.Report.Account.Branch();
    }

    /**
     * Create an instance of {@link ReportingEntity }
     * 
     */
    public ReportingEntity createReportingEntity() {
        return new ReportingEntity();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
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
     * Create an instance of {@link SuspicionDetails }
     * 
     */
    public SuspicionDetails createSuspicionDetails() {
        return new SuspicionDetails();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.PersonDetails.LegalPerson }
     * 
     */
    public Batch.Report.Account.PersonDetails.LegalPerson createBatchReportAccountPersonDetailsLegalPerson() {
        return new Batch.Report.Account.PersonDetails.LegalPerson();
    }

    /**
     * Create an instance of {@link Batch.Report }
     * 
     */
    public Batch.Report createBatchReport() {
        return new Batch.Report();
    }

    /**
     * Create an instance of {@link Phone }
     * 
     */
    public Phone createPhone() {
        return new Phone();
    }

    /**
     * Create an instance of {@link Batch.Report.Account }
     * 
     */
    public Batch.Report.Account createBatchReportAccount() {
        return new Batch.Report.Account();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.PersonDetails.Individual }
     * 
     */
    public Batch.Report.Account.PersonDetails.Individual createBatchReportAccountPersonDetailsIndividual() {
        return new Batch.Report.Account.PersonDetails.Individual();
    }

    /**
     * Create an instance of {@link FIUReportSubmissionExternalLibrary }
     * 
     */
    public FIUReportSubmissionExternalLibrary createFIUReportSubmissionExternalLibrary() {
        return new FIUReportSubmissionExternalLibrary();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.PersonDetails }
     * 
     */
    public Batch.Report.Account.PersonDetails createBatchReportAccountPersonDetails() {
        return new Batch.Report.Account.PersonDetails();
    }

    /**
     * Create an instance of {@link Batch }
     * 
     */
    public Batch createBatch() {
        return new Batch();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.Transaction.ProductTransaction }
     * 
     */
    public Batch.Report.Account.Transaction.ProductTransaction createBatchReportAccountTransactionProductTransaction() {
        return new Batch.Report.Account.Transaction.ProductTransaction();
    }

    /**
     * Create an instance of {@link BatchHeader }
     * 
     */
    public BatchHeader createBatchHeader() {
        return new BatchHeader();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.Transaction }
     * 
     */
    public Batch.Report.Account.Transaction createBatchReportAccountTransaction() {
        return new Batch.Report.Account.Transaction();
    }

    /**
     * Create an instance of {@link Error }
     * 
     */
    public Error createError() {
        return new Error();
    }

    /**
     * Create an instance of {@link Batch.Report.Account.Branch.BranchDetails }
     * 
     */
    public Batch.Report.Account.Branch.BranchDetails createBatchReportAccountBranchBranchDetails() {
        return new Batch.Report.Account.Branch.BranchDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IssuingAuthority", scope = Batch.Report.Account.PersonDetails.Individual.class)
    public JAXBElement<String> createBatchReportAccountPersonDetailsIndividualIssuingAuthority(String value) {
        return new JAXBElement<String>(_BatchReportAccountPersonDetailsIndividualIssuingAuthority_QNAME, String.class, Batch.Report.Account.PersonDetails.Individual.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IdentificationNumber", scope = Batch.Report.Account.PersonDetails.Individual.class)
    public JAXBElement<String> createBatchReportAccountPersonDetailsIndividualIdentificationNumber(String value) {
        return new JAXBElement<String>(_BatchReportAccountPersonDetailsIndividualIdentificationNumber_QNAME, String.class, Batch.Report.Account.PersonDetails.Individual.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PlaceOfIssue", scope = Batch.Report.Account.PersonDetails.Individual.class)
    public JAXBElement<String> createBatchReportAccountPersonDetailsIndividualPlaceOfIssue(String value) {
        return new JAXBElement<String>(_BatchReportAccountPersonDetailsIndividualPlaceOfIssue_QNAME, String.class, Batch.Report.Account.PersonDetails.Individual.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TransactionID", scope = Batch.Report.Account.Transaction.class)
    public JAXBElement<String> createBatchReportAccountTransactionTransactionID(String value) {
        return new JAXBElement<String>(_BatchReportAccountTransactionTransactionID_QNAME, String.class, Batch.Report.Account.Transaction.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Report", scope = Batch.Report.class)
    public JAXBElement<Batch.Report> createBatchReport(Batch.Report value) {
        return new JAXBElement<Batch.Report>(_BatchReport_QNAME, Batch.Report.class, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Batch", scope = Batch.Report.class)
    public JAXBElement<Batch> createBatch(Batch value) {
        return new JAXBElement<Batch>(_BatchReport_QNAME, Batch.class, value);
    }
    
}
