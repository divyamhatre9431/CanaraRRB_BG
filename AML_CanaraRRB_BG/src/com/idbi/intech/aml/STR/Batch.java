//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//

package com.idbi.intech.aml.STR;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReportType">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="CTR"/>
 *               &lt;enumeration value="STR"/>
 *               &lt;enumeration value="NTR"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ReportFormatType">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ARF"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="BatchHeader" type="{}BatchHeader"/>
 *         &lt;element name="ReportingEntity" type="{}ReportingEntity"/>
 *         &lt;element name="PrincipalOfficer" type="{}PrincipalOfficer"/>
 *         &lt;element name="BatchDetails" type="{}BatchDetails"/>
 *         &lt;element name="Report" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ReportSerialNum">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *                         &lt;totalDigits value="8"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="OriginalReportSerialNum">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                         &lt;totalDigits value="8"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MainPersonName" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="80"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="SuspicionDetails" type="{}SuspicionDetails" minOccurs="0"/>
 *                   &lt;element name="Account" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="AccountDetails">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="AccountNumber">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="1"/>
 *                                             &lt;maxLength value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="AccountType" type="{}AccountType"/>
 *                                       &lt;element name="HolderName">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="1"/>
 *                                             &lt;maxLength value="80"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="AccountHolderType" type="{}AccountHolderType"/>
 *                                       &lt;element name="AccountStatus" type="{}AccountStatus"/>
 *                                       &lt;element name="DateOfOpening" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                       &lt;element name="RiskRating" type="{}AccountRiskRating"/>
 *                                       &lt;element name="CumulativeCreditTurnover" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                                             &lt;totalDigits value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="CumulativeDebitTurnover" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                                             &lt;totalDigits value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="CumulativeCashDepositTurnover" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                                             &lt;totalDigits value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="CumulativeCashWithdrawalTurnover" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                                             &lt;totalDigits value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="NoTransactionsTobeReported" type="{}YesNo"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Branch">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="BranchRefNumType" type="{}BranchRefNumType"/>
 *                                       &lt;element name="BranchRefNum">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="20"/>
 *                                             &lt;minLength value="1"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="BranchDetails" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="BranchName">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;minLength value="0"/>
 *                                                       &lt;maxLength value="80"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="BranchAddress" type="{}Address"/>
 *                                                 &lt;element name="BranchPhone" type="{}Phone"/>
 *                                                 &lt;element name="BranchEmail" type="{}EmailAddress"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="PersonDetails" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="PersonName">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="1"/>
 *                                             &lt;maxLength value="80"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="CustomerId" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="10"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="RelationFlag" type="{}RelationalFlag"/>
 *                                       &lt;element name="CommunicationAddress" type="{}Address"/>
 *                                       &lt;element name="Phone" type="{}Phone"/>
 *                                       &lt;element name="Email" type="{}EmailAddress"/>
 *                                       &lt;element name="SecondAddress" type="{}Address" minOccurs="0"/>
 *                                       &lt;element name="PAN" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="0"/>
 *                                             &lt;maxLength value="10"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="UIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;choice>
 *                                         &lt;element name="Individual">
 *                                           &lt;complexType>
 *                                             &lt;complexContent>
 *                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                 &lt;sequence>
 *                                                   &lt;element name="Gender" type="{}Gender"/>
 *                                                   &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                   &lt;element name="IdentificationType" type="{}IdentificationType"/>
 *                                                   &lt;element name="IdentificationNumber" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="20"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="IssuingAuthority" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="20"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="PlaceOfIssue" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="20"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="Nationality" type="{}CountryCode"/>
 *                                                   &lt;element name="PlaceOfWork" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="80"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="FatherOrSpouse" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="80"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="Occupation" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="50"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                 &lt;/sequence>
 *                                               &lt;/restriction>
 *                                             &lt;/complexContent>
 *                                           &lt;/complexType>
 *                                         &lt;/element>
 *                                         &lt;element name="LegalPerson">
 *                                           &lt;complexType>
 *                                             &lt;complexContent>
 *                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                 &lt;sequence>
 *                                                   &lt;element name="ConstitutionType" type="{}ConstitutionType"/>
 *                                                   &lt;element name="RegistrationNumber" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;maxLength value="20"/>
 *                                                         &lt;minLength value="0"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="DateOfIncorporation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                   &lt;element name="PlaceOfRegistration" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;maxLength value="20"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                   &lt;element name="CountryCode" type="{}CountryCode"/>
 *                                                   &lt;element name="NatureOfBusiness" minOccurs="0">
 *                                                     &lt;simpleType>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                         &lt;minLength value="0"/>
 *                                                         &lt;maxLength value="50"/>
 *                                                       &lt;/restriction>
 *                                                     &lt;/simpleType>
 *                                                   &lt;/element>
 *                                                 &lt;/sequence>
 *                                               &lt;/restriction>
 *                                             &lt;/complexContent>
 *                                           &lt;/complexType>
 *                                         &lt;/element>
 *                                       &lt;/choice>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Transaction" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="DateOfTransaction" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                       &lt;element name="TransactionID" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="0"/>
 *                                             &lt;maxLength value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="TransactionMode" type="{}TransactionMode"/>
 *                                       &lt;element name="DebitCredit" type="{}DebitCredit"/>
 *                                       &lt;element name="Amount">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                                             &lt;totalDigits value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="Currency" type="{}CurrencyCode"/>
 *                                       &lt;element name="ProductTransaction" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="ProductType" type="{}ProductType"/>
 *                                                 &lt;element name="Identifier" minOccurs="0">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                                       &lt;maxLength value="30"/>
 *                                                       &lt;minLength value="0"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="TransactionType" type="{}ProductTransactionType"/>
 *                                                 &lt;element name="Units">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                                                       &lt;totalDigits value="20"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="Rate">
 *                                                   &lt;simpleType>
 *                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *                                                       &lt;totalDigits value="10"/>
 *                                                       &lt;fractionDigits value="4"/>
 *                                                     &lt;/restriction>
 *                                                   &lt;/simpleType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="DispositionOfFunds" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="0"/>
 *                                             &lt;maxLength value="1"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="RelatedAccountNum" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="RelatedInstitutionName" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="RelatedInstitutionRefNum" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="20"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="Remarks" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="50"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "reportType", "reportFormatType", "batchHeader", "reportingEntity", "principalOfficer", "batchDetails",
		"report" })
@XmlRootElement(name = "Batch")
public class Batch {

	@XmlElement(name = "ReportType", required = true)
	protected String reportType;
	@XmlElement(name = "ReportFormatType", required = true)
	protected String reportFormatType;
	@XmlElement(name = "BatchHeader", required = true)
	protected BatchHeader batchHeader;
	@XmlElement(name = "ReportingEntity", required = true)
	protected ReportingEntity reportingEntity;
	@XmlElement(name = "PrincipalOfficer", required = true)
	protected PrincipalOfficer principalOfficer;
	@XmlElement(name = "BatchDetails", required = true)
	protected BatchDetails batchDetails;
	@XmlElement(name = "Report", required = true)
	protected List<Batch.Report> report;

	/**
	 * Gets the value of the reportType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * Sets the value of the reportType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReportType(String value) {
		this.reportType = value;
	}

	/**
	 * Gets the value of the reportFormatType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReportFormatType() {
		return reportFormatType;
	}

	/**
	 * Sets the value of the reportFormatType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReportFormatType(String value) {
		this.reportFormatType = value;
	}

	/**
	 * Gets the value of the batchHeader property.
	 * 
	 * @return possible object is {@link BatchHeader }
	 * 
	 */
	public BatchHeader getBatchHeader() {
		return batchHeader;
	}

	/**
	 * Sets the value of the batchHeader property.
	 * 
	 * @param value
	 *            allowed object is {@link BatchHeader }
	 * 
	 */
	public void setBatchHeader(BatchHeader value) {
		this.batchHeader = value;
	}

	/**
	 * Gets the value of the reportingEntity property.
	 * 
	 * @return possible object is {@link ReportingEntity }
	 * 
	 */
	public ReportingEntity getReportingEntity() {
		return reportingEntity;
	}

	/**
	 * Sets the value of the reportingEntity property.
	 * 
	 * @param value
	 *            allowed object is {@link ReportingEntity }
	 * 
	 */
	public void setReportingEntity(ReportingEntity value) {
		this.reportingEntity = value;
	}

	public void setReport(List<Batch.Report> report) {
		this.report = report;
	}

	/**
	 * Gets the value of the principalOfficer property.
	 * 
	 * @return possible object is {@link PrincipalOfficer }
	 * 
	 */
	public PrincipalOfficer getPrincipalOfficer() {
		return principalOfficer;
	}

	/**
	 * Sets the value of the principalOfficer property.
	 * 
	 * @param value
	 *            allowed object is {@link PrincipalOfficer }
	 * 
	 */
	public void setPrincipalOfficer(PrincipalOfficer value) {
		this.principalOfficer = value;
	}

	/**
	 * Gets the value of the batchDetails property.
	 * 
	 * @return possible object is {@link BatchDetails }
	 * 
	 */
	public BatchDetails getBatchDetails() {
		return batchDetails;
	}

	/**
	 * Sets the value of the batchDetails property.
	 * 
	 * @param value
	 *            allowed object is {@link BatchDetails }
	 * 
	 */
	public void setBatchDetails(BatchDetails value) {
		this.batchDetails = value;
	}

	/**
	 * Gets the value of the report property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the report property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getReport().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Batch.Report }
	 * 
	 * 
	 */
	public List<Batch.Report> getReport() {
		if (report == null) {
			report = new ArrayList<Batch.Report>();
		}
		return this.report;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="ReportSerialNum">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
	 *               &lt;totalDigits value="8"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="OriginalReportSerialNum">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *               &lt;totalDigits value="8"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="MainPersonName" minOccurs="0">
	 *           &lt;simpleType>
	 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *               &lt;maxLength value="80"/>
	 *             &lt;/restriction>
	 *           &lt;/simpleType>
	 *         &lt;/element>
	 *         &lt;element name="SuspicionDetails" type="{}SuspicionDetails" minOccurs="0"/>
	 *         &lt;element name="Account" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="AccountDetails">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="AccountNumber">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;minLength value="1"/>
	 *                                   &lt;maxLength value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="AccountType" type="{}AccountType"/>
	 *                             &lt;element name="HolderName">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;minLength value="1"/>
	 *                                   &lt;maxLength value="80"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="AccountHolderType" type="{}AccountHolderType"/>
	 *                             &lt;element name="AccountStatus" type="{}AccountStatus"/>
	 *                             &lt;element name="DateOfOpening" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
	 *                             &lt;element name="RiskRating" type="{}AccountRiskRating"/>
	 *                             &lt;element name="CumulativeCreditTurnover" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *                                   &lt;totalDigits value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="CumulativeDebitTurnover" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *                                   &lt;totalDigits value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="CumulativeCashDepositTurnover" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *                                   &lt;totalDigits value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="CumulativeCashWithdrawalTurnover" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *                                   &lt;totalDigits value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="NoTransactionsTobeReported" type="{}YesNo"/>
	 *                           &lt;/sequence>
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
	 *                   &lt;element name="Branch">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="BranchRefNumType" type="{}BranchRefNumType"/>
	 *                             &lt;element name="BranchRefNum">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;maxLength value="20"/>
	 *                                   &lt;minLength value="1"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="BranchDetails" minOccurs="0">
	 *                               &lt;complexType>
	 *                                 &lt;complexContent>
	 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                                     &lt;sequence>
	 *                                       &lt;element name="BranchName">
	 *                                         &lt;simpleType>
	 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                             &lt;minLength value="0"/>
	 *                                             &lt;maxLength value="80"/>
	 *                                           &lt;/restriction>
	 *                                         &lt;/simpleType>
	 *                                       &lt;/element>
	 *                                       &lt;element name="BranchAddress" type="{}Address"/>
	 *                                       &lt;element name="BranchPhone" type="{}Phone"/>
	 *                                       &lt;element name="BranchEmail" type="{}EmailAddress"/>
	 *                                     &lt;/sequence>
	 *                                   &lt;/restriction>
	 *                                 &lt;/complexContent>
	 *                               &lt;/complexType>
	 *                             &lt;/element>
	 *                           &lt;/sequence>
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
	 *                   &lt;element name="PersonDetails" maxOccurs="unbounded">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="PersonName">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;minLength value="1"/>
	 *                                   &lt;maxLength value="80"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="CustomerId" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;maxLength value="10"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="RelationFlag" type="{}RelationalFlag"/>
	 *                             &lt;element name="CommunicationAddress" type="{}Address"/>
	 *                             &lt;element name="Phone" type="{}Phone"/>
	 *                             &lt;element name="Email" type="{}EmailAddress"/>
	 *                             &lt;element name="SecondAddress" type="{}Address" minOccurs="0"/>
	 *                             &lt;element name="PAN" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;minLength value="0"/>
	 *                                   &lt;maxLength value="10"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="UIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                             &lt;choice>
	 *                               &lt;element name="Individual">
	 *                                 &lt;complexType>
	 *                                   &lt;complexContent>
	 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                                       &lt;sequence>
	 *                                         &lt;element name="Gender" type="{}Gender"/>
	 *                                         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
	 *                                         &lt;element name="IdentificationType" type="{}IdentificationType"/>
	 *                                         &lt;element name="IdentificationNumber" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="20"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="IssuingAuthority" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="20"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="PlaceOfIssue" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="20"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="Nationality" type="{}CountryCode"/>
	 *                                         &lt;element name="PlaceOfWork" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="80"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="FatherOrSpouse" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="80"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="Occupation" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="50"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                       &lt;/sequence>
	 *                                     &lt;/restriction>
	 *                                   &lt;/complexContent>
	 *                                 &lt;/complexType>
	 *                               &lt;/element>
	 *                               &lt;element name="LegalPerson">
	 *                                 &lt;complexType>
	 *                                   &lt;complexContent>
	 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                                       &lt;sequence>
	 *                                         &lt;element name="ConstitutionType" type="{}ConstitutionType"/>
	 *                                         &lt;element name="RegistrationNumber" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;maxLength value="20"/>
	 *                                               &lt;minLength value="0"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="DateOfIncorporation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
	 *                                         &lt;element name="PlaceOfRegistration" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;maxLength value="20"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                         &lt;element name="CountryCode" type="{}CountryCode"/>
	 *                                         &lt;element name="NatureOfBusiness" minOccurs="0">
	 *                                           &lt;simpleType>
	 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                               &lt;minLength value="0"/>
	 *                                               &lt;maxLength value="50"/>
	 *                                             &lt;/restriction>
	 *                                           &lt;/simpleType>
	 *                                         &lt;/element>
	 *                                       &lt;/sequence>
	 *                                     &lt;/restriction>
	 *                                   &lt;/complexContent>
	 *                                 &lt;/complexType>
	 *                               &lt;/element>
	 *                             &lt;/choice>
	 *                           &lt;/sequence>
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
	 *                   &lt;element name="Transaction" maxOccurs="unbounded" minOccurs="0">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="DateOfTransaction" type="{http://www.w3.org/2001/XMLSchema}date"/>
	 *                             &lt;element name="TransactionID" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;minLength value="0"/>
	 *                                   &lt;maxLength value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="TransactionMode" type="{}TransactionMode"/>
	 *                             &lt;element name="DebitCredit" type="{}DebitCredit"/>
	 *                             &lt;element name="Amount">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *                                   &lt;totalDigits value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="Currency" type="{}CurrencyCode"/>
	 *                             &lt;element name="ProductTransaction" minOccurs="0">
	 *                               &lt;complexType>
	 *                                 &lt;complexContent>
	 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                                     &lt;sequence>
	 *                                       &lt;element name="ProductType" type="{}ProductType"/>
	 *                                       &lt;element name="Identifier" minOccurs="0">
	 *                                         &lt;simpleType>
	 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                             &lt;maxLength value="30"/>
	 *                                             &lt;minLength value="0"/>
	 *                                           &lt;/restriction>
	 *                                         &lt;/simpleType>
	 *                                       &lt;/element>
	 *                                       &lt;element name="TransactionType" type="{}ProductTransactionType"/>
	 *                                       &lt;element name="Units">
	 *                                         &lt;simpleType>
	 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
	 *                                             &lt;totalDigits value="20"/>
	 *                                           &lt;/restriction>
	 *                                         &lt;/simpleType>
	 *                                       &lt;/element>
	 *                                       &lt;element name="Rate">
	 *                                         &lt;simpleType>
	 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
	 *                                             &lt;totalDigits value="10"/>
	 *                                             &lt;fractionDigits value="4"/>
	 *                                           &lt;/restriction>
	 *                                         &lt;/simpleType>
	 *                                       &lt;/element>
	 *                                     &lt;/sequence>
	 *                                   &lt;/restriction>
	 *                                 &lt;/complexContent>
	 *                               &lt;/complexType>
	 *                             &lt;/element>
	 *                             &lt;element name="DispositionOfFunds" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;minLength value="0"/>
	 *                                   &lt;maxLength value="1"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="RelatedAccountNum" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;maxLength value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="RelatedInstitutionName" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;maxLength value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="RelatedInstitutionRefNum" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;maxLength value="20"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                             &lt;element name="Remarks" minOccurs="0">
	 *                               &lt;simpleType>
	 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                                   &lt;maxLength value="50"/>
	 *                                 &lt;/restriction>
	 *                               &lt;/simpleType>
	 *                             &lt;/element>
	 *                           &lt;/sequence>
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
	 *                 &lt;/sequence>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "reportSerialNum", "originalReportSerialNum", "mainPersonName", "suspicionDetails", "account" })
	public static class Report {

		@XmlElement(name = "ReportSerialNum", required = true)
		protected BigInteger reportSerialNum;
		@XmlElement(name = "OriginalReportSerialNum", required = true)
		protected BigInteger originalReportSerialNum;
		@XmlElement(name = "MainPersonName")
		protected String mainPersonName;
		@XmlElement(name = "SuspicionDetails")
		protected SuspicionDetails suspicionDetails;
		@XmlElement(name = "Account")
		protected List<Batch.Report.Account> account;

		/**
		 * Gets the value of the reportSerialNum property.
		 * 
		 * @return possible object is {@link BigInteger }
		 * 
		 */
		public BigInteger getReportSerialNum() {
			return reportSerialNum;
		}

		/**
		 * Sets the value of the reportSerialNum property.
		 * 
		 * @param value
		 *            allowed object is {@link BigInteger }
		 * 
		 */
		public void setReportSerialNum(BigInteger value) {
			this.reportSerialNum = value;
		}

		/**
		 * Gets the value of the originalReportSerialNum property.
		 * 
		 * @return possible object is {@link BigInteger }
		 * 
		 */
		public BigInteger getOriginalReportSerialNum() {
			return originalReportSerialNum;
		}

		/**
		 * Sets the value of the originalReportSerialNum property.
		 * 
		 * @param value
		 *            allowed object is {@link BigInteger }
		 * 
		 */
		public void setOriginalReportSerialNum(BigInteger value) {
			this.originalReportSerialNum = value;
		}

		/**
		 * Gets the value of the mainPersonName property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getMainPersonName() {
			return mainPersonName;
		}

		/**
		 * Sets the value of the mainPersonName property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setMainPersonName(String value) {
			this.mainPersonName = value;
		}

		public void setAccount(List<Batch.Report.Account> account) {
			this.account = account;
		}

		/**
		 * Gets the value of the suspicionDetails property.
		 * 
		 * @return possible object is {@link SuspicionDetails }
		 * 
		 */
		public SuspicionDetails getSuspicionDetails() {
			return suspicionDetails;
		}

		/**
		 * Sets the value of the suspicionDetails property.
		 * 
		 * @param value
		 *            allowed object is {@link SuspicionDetails }
		 * 
		 */
		public void setSuspicionDetails(SuspicionDetails value) {
			this.suspicionDetails = value;
		}

		/**
		 * Gets the value of the account property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the account property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getAccount().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link Batch.Report.Account }
		 * 
		 * 
		 */
		public List<Batch.Report.Account> getAccount() {
			if (account == null) {
				account = new ArrayList<Batch.Report.Account>();
			}
			return this.account;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 * 
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="AccountDetails">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="AccountNumber">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;minLength value="1"/>
		 *                         &lt;maxLength value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="AccountType" type="{}AccountType"/>
		 *                   &lt;element name="HolderName">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;minLength value="1"/>
		 *                         &lt;maxLength value="80"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="AccountHolderType" type="{}AccountHolderType"/>
		 *                   &lt;element name="AccountStatus" type="{}AccountStatus"/>
		 *                   &lt;element name="DateOfOpening" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
		 *                   &lt;element name="RiskRating" type="{}AccountRiskRating"/>
		 *                   &lt;element name="CumulativeCreditTurnover" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
		 *                         &lt;totalDigits value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="CumulativeDebitTurnover" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
		 *                         &lt;totalDigits value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="CumulativeCashDepositTurnover" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
		 *                         &lt;totalDigits value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="CumulativeCashWithdrawalTurnover" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
		 *                         &lt;totalDigits value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="NoTransactionsTobeReported" type="{}YesNo"/>
		 *                 &lt;/sequence>
		 *               &lt;/restriction>
		 *             &lt;/complexContent>
		 *           &lt;/complexType>
		 *         &lt;/element>
		 *         &lt;element name="Branch">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="BranchRefNumType" type="{}BranchRefNumType"/>
		 *                   &lt;element name="BranchRefNum">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;maxLength value="20"/>
		 *                         &lt;minLength value="1"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="BranchDetails" minOccurs="0">
		 *                     &lt;complexType>
		 *                       &lt;complexContent>
		 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                           &lt;sequence>
		 *                             &lt;element name="BranchName">
		 *                               &lt;simpleType>
		 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                   &lt;minLength value="0"/>
		 *                                   &lt;maxLength value="80"/>
		 *                                 &lt;/restriction>
		 *                               &lt;/simpleType>
		 *                             &lt;/element>
		 *                             &lt;element name="BranchAddress" type="{}Address"/>
		 *                             &lt;element name="BranchPhone" type="{}Phone"/>
		 *                             &lt;element name="BranchEmail" type="{}EmailAddress"/>
		 *                           &lt;/sequence>
		 *                         &lt;/restriction>
		 *                       &lt;/complexContent>
		 *                     &lt;/complexType>
		 *                   &lt;/element>
		 *                 &lt;/sequence>
		 *               &lt;/restriction>
		 *             &lt;/complexContent>
		 *           &lt;/complexType>
		 *         &lt;/element>
		 *         &lt;element name="PersonDetails" maxOccurs="unbounded">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="PersonName">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;minLength value="1"/>
		 *                         &lt;maxLength value="80"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="CustomerId" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;maxLength value="10"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="RelationFlag" type="{}RelationalFlag"/>
		 *                   &lt;element name="CommunicationAddress" type="{}Address"/>
		 *                   &lt;element name="Phone" type="{}Phone"/>
		 *                   &lt;element name="Email" type="{}EmailAddress"/>
		 *                   &lt;element name="SecondAddress" type="{}Address" minOccurs="0"/>
		 *                   &lt;element name="PAN" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;minLength value="0"/>
		 *                         &lt;maxLength value="10"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="UIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *                   &lt;choice>
		 *                     &lt;element name="Individual">
		 *                       &lt;complexType>
		 *                         &lt;complexContent>
		 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                             &lt;sequence>
		 *                               &lt;element name="Gender" type="{}Gender"/>
		 *                               &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
		 *                               &lt;element name="IdentificationType" type="{}IdentificationType"/>
		 *                               &lt;element name="IdentificationNumber" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="20"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="IssuingAuthority" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="20"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="PlaceOfIssue" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="20"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="Nationality" type="{}CountryCode"/>
		 *                               &lt;element name="PlaceOfWork" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="80"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="FatherOrSpouse" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="80"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="Occupation" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="50"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                             &lt;/sequence>
		 *                           &lt;/restriction>
		 *                         &lt;/complexContent>
		 *                       &lt;/complexType>
		 *                     &lt;/element>
		 *                     &lt;element name="LegalPerson">
		 *                       &lt;complexType>
		 *                         &lt;complexContent>
		 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                             &lt;sequence>
		 *                               &lt;element name="ConstitutionType" type="{}ConstitutionType"/>
		 *                               &lt;element name="RegistrationNumber" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;maxLength value="20"/>
		 *                                     &lt;minLength value="0"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="DateOfIncorporation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
		 *                               &lt;element name="PlaceOfRegistration" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;maxLength value="20"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                               &lt;element name="CountryCode" type="{}CountryCode"/>
		 *                               &lt;element name="NatureOfBusiness" minOccurs="0">
		 *                                 &lt;simpleType>
		 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                     &lt;minLength value="0"/>
		 *                                     &lt;maxLength value="50"/>
		 *                                   &lt;/restriction>
		 *                                 &lt;/simpleType>
		 *                               &lt;/element>
		 *                             &lt;/sequence>
		 *                           &lt;/restriction>
		 *                         &lt;/complexContent>
		 *                       &lt;/complexType>
		 *                     &lt;/element>
		 *                   &lt;/choice>
		 *                 &lt;/sequence>
		 *               &lt;/restriction>
		 *             &lt;/complexContent>
		 *           &lt;/complexType>
		 *         &lt;/element>
		 *         &lt;element name="Transaction" maxOccurs="unbounded" minOccurs="0">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="DateOfTransaction" type="{http://www.w3.org/2001/XMLSchema}date"/>
		 *                   &lt;element name="TransactionID" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;minLength value="0"/>
		 *                         &lt;maxLength value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="TransactionMode" type="{}TransactionMode"/>
		 *                   &lt;element name="DebitCredit" type="{}DebitCredit"/>
		 *                   &lt;element name="Amount">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
		 *                         &lt;totalDigits value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="Currency" type="{}CurrencyCode"/>
		 *                   &lt;element name="ProductTransaction" minOccurs="0">
		 *                     &lt;complexType>
		 *                       &lt;complexContent>
		 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                           &lt;sequence>
		 *                             &lt;element name="ProductType" type="{}ProductType"/>
		 *                             &lt;element name="Identifier" minOccurs="0">
		 *                               &lt;simpleType>
		 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                                   &lt;maxLength value="30"/>
		 *                                   &lt;minLength value="0"/>
		 *                                 &lt;/restriction>
		 *                               &lt;/simpleType>
		 *                             &lt;/element>
		 *                             &lt;element name="TransactionType" type="{}ProductTransactionType"/>
		 *                             &lt;element name="Units">
		 *                               &lt;simpleType>
		 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
		 *                                   &lt;totalDigits value="20"/>
		 *                                 &lt;/restriction>
		 *                               &lt;/simpleType>
		 *                             &lt;/element>
		 *                             &lt;element name="Rate">
		 *                               &lt;simpleType>
		 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
		 *                                   &lt;totalDigits value="10"/>
		 *                                   &lt;fractionDigits value="4"/>
		 *                                 &lt;/restriction>
		 *                               &lt;/simpleType>
		 *                             &lt;/element>
		 *                           &lt;/sequence>
		 *                         &lt;/restriction>
		 *                       &lt;/complexContent>
		 *                     &lt;/complexType>
		 *                   &lt;/element>
		 *                   &lt;element name="DispositionOfFunds" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;minLength value="0"/>
		 *                         &lt;maxLength value="1"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="RelatedAccountNum" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;maxLength value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="RelatedInstitutionName" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;maxLength value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="RelatedInstitutionRefNum" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;maxLength value="20"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                   &lt;element name="Remarks" minOccurs="0">
		 *                     &lt;simpleType>
		 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *                         &lt;maxLength value="50"/>
		 *                       &lt;/restriction>
		 *                     &lt;/simpleType>
		 *                   &lt;/element>
		 *                 &lt;/sequence>
		 *               &lt;/restriction>
		 *             &lt;/complexContent>
		 *           &lt;/complexType>
		 *         &lt;/element>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlRootElement(name = "Account")
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "accountDetails", "branch", "personDetails", "transaction" })
		public static class Account {

			public void setPersonDetails(List<Batch.Report.Account.PersonDetails> personDetails) {
				this.personDetails = personDetails;
			}

			public void setTransaction(List<Batch.Report.Account.Transaction> transaction) {
				this.transaction = transaction;
			}

			@XmlElement(name = "AccountDetails", required = true)
			protected Batch.Report.Account.AccountDetails accountDetails;
			@XmlElement(name = "Branch", required = true)
			protected Batch.Report.Account.Branch branch;
			@XmlElement(name = "PersonDetails", required = true)
			protected List<Batch.Report.Account.PersonDetails> personDetails;
			@XmlElement(name = "Transaction")
			protected List<Batch.Report.Account.Transaction> transaction;

			/**
			 * Gets the value of the accountDetails property.
			 * 
			 * @return possible object is
			 *         {@link Batch.Report.Account.AccountDetails }
			 * 
			 */
			public Batch.Report.Account.AccountDetails getAccountDetails() {
				return accountDetails;
			}

			/**
			 * Sets the value of the accountDetails property.
			 * 
			 * @param value
			 *            allowed object is
			 *            {@link Batch.Report.Account.AccountDetails }
			 * 
			 */
			public void setAccountDetails(Batch.Report.Account.AccountDetails value) {
				this.accountDetails = value;
			}

			/**
			 * Gets the value of the branch property.
			 * 
			 * @return possible object is {@link Batch.Report.Account.Branch }
			 * 
			 */
			public Batch.Report.Account.Branch getBranch() {
				return branch;
			}

			/**
			 * Sets the value of the branch property.
			 * 
			 * @param value
			 *            allowed object is {@link Batch.Report.Account.Branch }
			 * 
			 */
			public void setBranch(Batch.Report.Account.Branch value) {
				this.branch = value;
			}

			/**
			 * Gets the value of the personDetails property.
			 * 
			 * <p>
			 * This accessor method returns a reference to the live list, not a
			 * snapshot. Therefore any modification you make to the returned
			 * list will be present inside the JAXB object. This is why there is
			 * not a <CODE>set</CODE> method for the personDetails property.
			 * 
			 * <p>
			 * For example, to add a new item, do as follows:
			 * 
			 * <pre>
			 * getPersonDetails().add(newItem);
			 * </pre>
			 * 
			 * 
			 * <p>
			 * Objects of the following type(s) are allowed in the list
			 * {@link Batch.Report.Account.PersonDetails }
			 * 
			 * 
			 */
			public List<Batch.Report.Account.PersonDetails> getPersonDetails() {
				if (personDetails == null) {
					personDetails = new ArrayList<Batch.Report.Account.PersonDetails>();
				}
				return this.personDetails;
			}

			/**
			 * Gets the value of the transaction property.
			 * 
			 * <p>
			 * This accessor method returns a reference to the live list, not a
			 * snapshot. Therefore any modification you make to the returned
			 * list will be present inside the JAXB object. This is why there is
			 * not a <CODE>set</CODE> method for the transaction property.
			 * 
			 * <p>
			 * For example, to add a new item, do as follows:
			 * 
			 * <pre>
			 * getTransaction().add(newItem);
			 * </pre>
			 * 
			 * 
			 * <p>
			 * Objects of the following type(s) are allowed in the list
			 * {@link Batch.Report.Account.Transaction }
			 * 
			 * 
			 */
			public List<Batch.Report.Account.Transaction> getTransaction() {
				if (transaction == null) {
					transaction = new ArrayList<Batch.Report.Account.Transaction>();
				}
				return this.transaction;
			}

			/**
			 * <p>
			 * Java class for anonymous complex type.
			 * 
			 * <p>
			 * The following schema fragment specifies the expected content
			 * contained within this class.
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="AccountNumber">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;minLength value="1"/>
			 *               &lt;maxLength value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="AccountType" type="{}AccountType"/>
			 *         &lt;element name="HolderName">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;minLength value="1"/>
			 *               &lt;maxLength value="80"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="AccountHolderType" type="{}AccountHolderType"/>
			 *         &lt;element name="AccountStatus" type="{}AccountStatus"/>
			 *         &lt;element name="DateOfOpening" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
			 *         &lt;element name="RiskRating" type="{}AccountRiskRating"/>
			 *         &lt;element name="CumulativeCreditTurnover" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
			 *               &lt;totalDigits value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="CumulativeDebitTurnover" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
			 *               &lt;totalDigits value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="CumulativeCashDepositTurnover" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
			 *               &lt;totalDigits value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="CumulativeCashWithdrawalTurnover" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
			 *               &lt;totalDigits value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="NoTransactionsTobeReported" type="{}YesNo"/>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "accountNumber", "accountType", "holderName", "accountHolderType", "accountStatus",
					"dateOfOpening", "riskRating", "cumulativeCreditTurnover", "cumulativeDebitTurnover", "cumulativeCashDepositTurnover",
					"cumulativeCashWithdrawalTurnover", "noTransactionsTobeReported" })
			public static class AccountDetails {

				@XmlElement(name = "AccountNumber", required = true)
				protected String accountNumber;

				@XmlElement(name = "AccountType", required = true)
				protected AccountType accountType;

				@XmlElement(name = "HolderName", required = true)
				protected String holderName;

				@XmlElement(name = "AccountHolderType", required = true)
				protected AccountHolderType accountHolderType;

				@XmlElement(name = "AccountStatus", required = true)
				protected AccountStatus accountStatus;

				@XmlElement(name = "DateOfOpening")
				protected String dateOfOpening;

				@XmlElement(name = "RiskRating", required = true, nillable = true)
				protected AccountRiskRating riskRating;

				@XmlElement(name = "CumulativeCreditTurnover")
				protected BigInteger cumulativeCreditTurnover;

				@XmlElement(name = "CumulativeDebitTurnover")
				protected BigInteger cumulativeDebitTurnover;
				@XmlElement(name = "CumulativeCashDepositTurnover")
				protected BigInteger cumulativeCashDepositTurnover;
				@XmlElement(name = "CumulativeCashWithdrawalTurnover")
				protected BigInteger cumulativeCashWithdrawalTurnover;
				@XmlElement(name = "NoTransactionsTobeReported", required = true)
				protected YesNo noTransactionsTobeReported;

				/**
				 * Gets the value of the accountNumber property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getAccountNumber() {
					return accountNumber;
				}

				/**
				 * Sets the value of the accountNumber property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setAccountNumber(String value) {
					this.accountNumber = value;
				}

				/**
				 * Gets the value of the accountType property.
				 * 
				 * @return possible object is {@link AccountType }
				 * 
				 */
				public AccountType getAccountType() {
					return accountType;
				}

				/**
				 * Sets the value of the accountType property.
				 * 
				 * @param value
				 *            allowed object is {@link AccountType }
				 * 
				 */
				public void setAccountType(AccountType value) {
					this.accountType = value;
				}

				/**
				 * Gets the value of the holderName property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getHolderName() {
					return holderName;
				}

				/**
				 * Sets the value of the holderName property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setHolderName(String value) {
					this.holderName = value;
				}

				/**
				 * Gets the value of the accountHolderType property.
				 * 
				 * @return possible object is {@link AccountHolderType }
				 * 
				 */
				public AccountHolderType getAccountHolderType() {
					return accountHolderType;
				}

				/**
				 * Sets the value of the accountHolderType property.
				 * 
				 * @param value
				 *            allowed object is {@link AccountHolderType }
				 * 
				 */
				public void setAccountHolderType(AccountHolderType value) {
					this.accountHolderType = value;
				}

				/**
				 * Gets the value of the accountStatus property.
				 * 
				 * @return possible object is {@link AccountStatus }
				 * 
				 */
				public AccountStatus getAccountStatus() {
					return accountStatus;
				}

				/**
				 * Sets the value of the accountStatus property.
				 * 
				 * @param value
				 *            allowed object is {@link AccountStatus }
				 * 
				 */
				public void setAccountStatus(AccountStatus value) {
					this.accountStatus = value;
				}

				/**
				 * Gets the value of the dateOfOpening property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getDateOfOpening() {
					return dateOfOpening;
				}

				/**
				 * Sets the value of the dateOfOpening property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setDateOfOpening(String value) {
					this.dateOfOpening = value;
				}

				/**
				 * Gets the value of the riskRating property.
				 * 
				 * @return possible object is {@link AccountRiskRating }
				 * 
				 */
				public AccountRiskRating getRiskRating() {
					return riskRating;
				}

				/**
				 * Sets the value of the riskRating property.
				 * 
				 * @param value
				 *            allowed object is {@link AccountRiskRating }
				 * 
				 */
				public void setRiskRating(AccountRiskRating value) {
					this.riskRating = value;
				}

				/**
				 * Gets the value of the cumulativeCreditTurnover property.
				 * 
				 * @return possible object is {@link BigInteger }
				 * 
				 */
				public BigInteger getCumulativeCreditTurnover() {
					return cumulativeCreditTurnover;
				}

				/**
				 * Sets the value of the cumulativeCreditTurnover property.
				 * 
				 * @param value
				 *            allowed object is {@link BigInteger }
				 * 
				 */
				public void setCumulativeCreditTurnover(BigInteger value) {
					this.cumulativeCreditTurnover = value;
				}

				/**
				 * Gets the value of the cumulativeDebitTurnover property.
				 * 
				 * @return possible object is {@link BigInteger }
				 * 
				 */
				public BigInteger getCumulativeDebitTurnover() {
					return cumulativeDebitTurnover;
				}

				/**
				 * Sets the value of the cumulativeDebitTurnover property.
				 * 
				 * @param value
				 *            allowed object is {@link BigInteger }
				 * 
				 */
				public void setCumulativeDebitTurnover(BigInteger value) {
					this.cumulativeDebitTurnover = value;
				}

				/**
				 * Gets the value of the cumulativeCashDepositTurnover property.
				 * 
				 * @return possible object is {@link BigInteger }
				 * 
				 */
				public BigInteger getCumulativeCashDepositTurnover() {
					return cumulativeCashDepositTurnover;
				}

				/**
				 * Sets the value of the cumulativeCashDepositTurnover property.
				 * 
				 * @param value
				 *            allowed object is {@link BigInteger }
				 * 
				 */
				public void setCumulativeCashDepositTurnover(BigInteger value) {
					this.cumulativeCashDepositTurnover = value;
				}

				/**
				 * Gets the value of the cumulativeCashWithdrawalTurnover
				 * property.
				 * 
				 * @return possible object is {@link BigInteger }
				 * 
				 */
				public BigInteger getCumulativeCashWithdrawalTurnover() {
					return cumulativeCashWithdrawalTurnover;
				}

				/**
				 * Sets the value of the cumulativeCashWithdrawalTurnover
				 * property.
				 * 
				 * @param value
				 *            allowed object is {@link BigInteger }
				 * 
				 */
				public void setCumulativeCashWithdrawalTurnover(BigInteger value) {
					this.cumulativeCashWithdrawalTurnover = value;
				}

				/**
				 * Gets the value of the noTransactionsTobeReported property.
				 * 
				 * @return possible object is {@link YesNo }
				 * 
				 */
				public YesNo getNoTransactionsTobeReported() {
					return noTransactionsTobeReported;
				}

				/**
				 * Sets the value of the noTransactionsTobeReported property.
				 * 
				 * @param value
				 *            allowed object is {@link YesNo }
				 * 
				 */
				public void setNoTransactionsTobeReported(YesNo value) {
					this.noTransactionsTobeReported = value;
				}

			}

			/**
			 * <p>
			 * Java class for anonymous complex type.
			 * 
			 * <p>
			 * The following schema fragment specifies the expected content
			 * contained within this class.
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="BranchRefNumType" type="{}BranchRefNumType"/>
			 *         &lt;element name="BranchRefNum">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;maxLength value="20"/>
			 *               &lt;minLength value="1"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="BranchDetails" minOccurs="0">
			 *           &lt;complexType>
			 *             &lt;complexContent>
			 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *                 &lt;sequence>
			 *                   &lt;element name="BranchName">
			 *                     &lt;simpleType>
			 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                         &lt;minLength value="0"/>
			 *                         &lt;maxLength value="80"/>
			 *                       &lt;/restriction>
			 *                     &lt;/simpleType>
			 *                   &lt;/element>
			 *                   &lt;element name="BranchAddress" type="{}Address"/>
			 *                   &lt;element name="BranchPhone" type="{}Phone"/>
			 *                   &lt;element name="BranchEmail" type="{}EmailAddress"/>
			 *                 &lt;/sequence>
			 *               &lt;/restriction>
			 *             &lt;/complexContent>
			 *           &lt;/complexType>
			 *         &lt;/element>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "branchRefNumType", "branchRefNum", "branchDetails" })
			public static class Branch {

				@XmlElement(name = "BranchRefNumType", required = true)
				protected BranchRefNumType branchRefNumType;
				@XmlElement(name = "BranchRefNum", required = true)
				protected String branchRefNum;
				@XmlElement(name = "BranchDetails")
				protected Batch.Report.Account.Branch.BranchDetails branchDetails;

				/**
				 * Gets the value of the branchRefNumType property.
				 * 
				 * @return possible object is {@link BranchRefNumType }
				 * 
				 */
				public BranchRefNumType getBranchRefNumType() {
					return branchRefNumType;
				}

				/**
				 * Sets the value of the branchRefNumType property.
				 * 
				 * @param value
				 *            allowed object is {@link BranchRefNumType }
				 * 
				 */
				public void setBranchRefNumType(BranchRefNumType value) {
					this.branchRefNumType = value;
				}

				/**
				 * Gets the value of the branchRefNum property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getBranchRefNum() {
					return branchRefNum;
				}

				/**
				 * Sets the value of the branchRefNum property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setBranchRefNum(String value) {
					this.branchRefNum = value;
				}

				/**
				 * Gets the value of the branchDetails property.
				 * 
				 * @return possible object is
				 *         {@link Batch.Report.Account.Branch.BranchDetails }
				 * 
				 */
				public Batch.Report.Account.Branch.BranchDetails getBranchDetails() {
					return branchDetails;
				}

				/**
				 * Sets the value of the branchDetails property.
				 * 
				 * @param value
				 *            allowed object is
				 *            {@link Batch.Report.Account.Branch.BranchDetails }
				 * 
				 */
				public void setBranchDetails(Batch.Report.Account.Branch.BranchDetails value) {
					this.branchDetails = value;
				}

				/**
				 * <p>
				 * Java class for anonymous complex type.
				 * 
				 * <p>
				 * The following schema fragment specifies the expected content
				 * contained within this class.
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;complexContent>
				 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
				 *       &lt;sequence>
				 *         &lt;element name="BranchName">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="80"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="BranchAddress" type="{}Address"/>
				 *         &lt;element name="BranchPhone" type="{}Phone"/>
				 *         &lt;element name="BranchEmail" type="{}EmailAddress"/>
				 *       &lt;/sequence>
				 *     &lt;/restriction>
				 *   &lt;/complexContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "branchName", "branchAddress", "branchPhone", "branchEmail" })
				public static class BranchDetails {

					@XmlElement(name = "BranchName", required = true)
					protected String branchName;
					@XmlElement(name = "BranchAddress", required = true)
					protected Address branchAddress;
					@XmlElement(name = "BranchPhone", required = true)
					protected Phone branchPhone;
					@XmlElement(name = "BranchEmail", required = true)
					protected String branchEmail;

					/**
					 * Gets the value of the branchName property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getBranchName() {
						return branchName;
					}

					/**
					 * Sets the value of the branchName property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setBranchName(String value) {
						this.branchName = value;
					}

					/**
					 * Gets the value of the branchAddress property.
					 * 
					 * @return possible object is {@link Address }
					 * 
					 */
					public Address getBranchAddress() {
						return branchAddress;
					}

					/**
					 * Sets the value of the branchAddress property.
					 * 
					 * @param value
					 *            allowed object is {@link Address }
					 * 
					 */
					public void setBranchAddress(Address value) {
						this.branchAddress = value;
					}

					/**
					 * Gets the value of the branchPhone property.
					 * 
					 * @return possible object is {@link Phone }
					 * 
					 */
					public Phone getBranchPhone() {
						return branchPhone;
					}

					/**
					 * Sets the value of the branchPhone property.
					 * 
					 * @param value
					 *            allowed object is {@link Phone }
					 * 
					 */
					public void setBranchPhone(Phone value) {
						this.branchPhone = value;
					}

					/**
					 * Gets the value of the branchEmail property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getBranchEmail() {
						return branchEmail;
					}

					/**
					 * Sets the value of the branchEmail property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setBranchEmail(String value) {
						this.branchEmail = value;
					}

				}

			}

			/**
			 * <p>
			 * Java class for anonymous complex type.
			 * 
			 * <p>
			 * The following schema fragment specifies the expected content
			 * contained within this class.
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="PersonName">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;minLength value="1"/>
			 *               &lt;maxLength value="80"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="CustomerId" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;maxLength value="10"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="RelationFlag" type="{}RelationalFlag"/>
			 *         &lt;element name="CommunicationAddress" type="{}Address"/>
			 *         &lt;element name="Phone" type="{}Phone"/>
			 *         &lt;element name="Email" type="{}EmailAddress"/>
			 *         &lt;element name="SecondAddress" type="{}Address" minOccurs="0"/>
			 *         &lt;element name="PAN" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;minLength value="0"/>
			 *               &lt;maxLength value="10"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="UIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
			 *         &lt;choice>
			 *           &lt;element name="Individual">
			 *             &lt;complexType>
			 *               &lt;complexContent>
			 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *                   &lt;sequence>
			 *                     &lt;element name="Gender" type="{}Gender"/>
			 *                     &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
			 *                     &lt;element name="IdentificationType" type="{}IdentificationType"/>
			 *                     &lt;element name="IdentificationNumber" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="20"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="IssuingAuthority" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="20"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="PlaceOfIssue" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="20"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="Nationality" type="{}CountryCode"/>
			 *                     &lt;element name="PlaceOfWork" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="80"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="FatherOrSpouse" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="80"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="Occupation" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="50"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                   &lt;/sequence>
			 *                 &lt;/restriction>
			 *               &lt;/complexContent>
			 *             &lt;/complexType>
			 *           &lt;/element>
			 *           &lt;element name="LegalPerson">
			 *             &lt;complexType>
			 *               &lt;complexContent>
			 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *                   &lt;sequence>
			 *                     &lt;element name="ConstitutionType" type="{}ConstitutionType"/>
			 *                     &lt;element name="RegistrationNumber" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;maxLength value="20"/>
			 *                           &lt;minLength value="0"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="DateOfIncorporation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
			 *                     &lt;element name="PlaceOfRegistration" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;maxLength value="20"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                     &lt;element name="CountryCode" type="{}CountryCode"/>
			 *                     &lt;element name="NatureOfBusiness" minOccurs="0">
			 *                       &lt;simpleType>
			 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                           &lt;minLength value="0"/>
			 *                           &lt;maxLength value="50"/>
			 *                         &lt;/restriction>
			 *                       &lt;/simpleType>
			 *                     &lt;/element>
			 *                   &lt;/sequence>
			 *                 &lt;/restriction>
			 *               &lt;/complexContent>
			 *             &lt;/complexType>
			 *           &lt;/element>
			 *         &lt;/choice>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "personName", "customerId", "relationFlag", "communicationAddress", "phone", "email",
					"secondAddress", "pan", "uin", "individual", "legalPerson" })
			public static class PersonDetails {

				@XmlElement(name = "PersonName", required = true)
				protected String personName;
				@XmlElement(name = "CustomerId")
				protected String customerId;
				@XmlElement(name = "RelationFlag", required = true)
				protected RelationalFlag relationFlag;
				@XmlElement(name = "CommunicationAddress", required = true)
				protected Address communicationAddress;
				@XmlElement(name = "Phone", required = true)
				protected Phone phone;
				@XmlElement(name = "Email", required = true)
				protected String email;
				@XmlElement(name = "SecondAddress")
				protected Address secondAddress;
				@XmlElement(name = "PAN")
				protected String pan;
				@XmlElement(name = "UIN")
				protected String uin;
				@XmlElement(name = "Individual")
				protected Batch.Report.Account.PersonDetails.Individual individual;
				@XmlElement(name = "LegalPerson")
				protected Batch.Report.Account.PersonDetails.LegalPerson legalPerson;

				/**
				 * Gets the value of the personName property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getPersonName() {
					return personName;
				}

				/**
				 * Sets the value of the personName property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setPersonName(String value) {
					this.personName = value;
				}

				/**
				 * Gets the value of the customerId property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getCustomerId() {
					return customerId;
				}

				/**
				 * Sets the value of the customerId property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setCustomerId(String value) {
					this.customerId = value;
				}

				/**
				 * Gets the value of the relationFlag property.
				 * 
				 * @return possible object is {@link RelationalFlag }
				 * 
				 */
				public RelationalFlag getRelationFlag() {
					return relationFlag;
				}

				/**
				 * Sets the value of the relationFlag property.
				 * 
				 * @param value
				 *            allowed object is {@link RelationalFlag }
				 * 
				 */
				public void setRelationFlag(RelationalFlag value) {
					this.relationFlag = value;
				}

				/**
				 * Gets the value of the communicationAddress property.
				 * 
				 * @return possible object is {@link Address }
				 * 
				 */
				public Address getCommunicationAddress() {
					return communicationAddress;
				}

				/**
				 * Sets the value of the communicationAddress property.
				 * 
				 * @param value
				 *            allowed object is {@link Address }
				 * 
				 */
				public void setCommunicationAddress(Address value) {
					this.communicationAddress = value;
				}

				/**
				 * Gets the value of the phone property.
				 * 
				 * @return possible object is {@link Phone }
				 * 
				 */
				public Phone getPhone() {
					return phone;
				}

				/**
				 * Sets the value of the phone property.
				 * 
				 * @param value
				 *            allowed object is {@link Phone }
				 * 
				 */
				public void setPhone(Phone value) {
					this.phone = value;
				}

				/**
				 * Gets the value of the email property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getEmail() {
					return email;
				}

				/**
				 * Sets the value of the email property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setEmail(String value) {
					this.email = value;
				}

				/**
				 * Gets the value of the secondAddress property.
				 * 
				 * @return possible object is {@link Address }
				 * 
				 */
				public Address getSecondAddress() {
					return secondAddress;
				}

				/**
				 * Sets the value of the secondAddress property.
				 * 
				 * @param value
				 *            allowed object is {@link Address }
				 * 
				 */
				public void setSecondAddress(Address value) {
					this.secondAddress = value;
				}

				/**
				 * Gets the value of the pan property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getPAN() {
					return pan;
				}

				/**
				 * Sets the value of the pan property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setPAN(String value) {
					this.pan = value;
				}

				/**
				 * Gets the value of the uin property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getUIN() {
					return uin;
				}

				/**
				 * Sets the value of the uin property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setUIN(String value) {
					this.uin = value;
				}

				/**
				 * Gets the value of the individual property.
				 * 
				 * @return possible object is
				 *         {@link Batch.Report.Account.PersonDetails.Individual }
				 * 
				 */
				public Batch.Report.Account.PersonDetails.Individual getIndividual() {
					return individual;
				}

				/**
				 * Sets the value of the individual property.
				 * 
				 * @param value
				 *            allowed object is
				 *            {@link Batch.Report.Account.PersonDetails.Individual }
				 * 
				 */
				public void setIndividual(Batch.Report.Account.PersonDetails.Individual value) {
					this.individual = value;
				}

				/**
				 * Gets the value of the legalPerson property.
				 * 
				 * @return possible object is
				 *         {@link Batch.Report.Account.PersonDetails.LegalPerson }
				 * 
				 */
				public Batch.Report.Account.PersonDetails.LegalPerson getLegalPerson() {
					return legalPerson;
				}

				/**
				 * Sets the value of the legalPerson property.
				 * 
				 * @param value
				 *            allowed object is
				 *            {@link Batch.Report.Account.PersonDetails.LegalPerson }
				 * 
				 */
				public void setLegalPerson(Batch.Report.Account.PersonDetails.LegalPerson value) {
					this.legalPerson = value;
				}

				/**
				 * <p>
				 * Java class for anonymous complex type.
				 * 
				 * <p>
				 * The following schema fragment specifies the expected content
				 * contained within this class.
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;complexContent>
				 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
				 *       &lt;sequence>
				 *         &lt;element name="Gender" type="{}Gender"/>
				 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
				 *         &lt;element name="IdentificationType" type="{}IdentificationType"/>
				 *         &lt;element name="IdentificationNumber" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="20"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="IssuingAuthority" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="20"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="PlaceOfIssue" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="20"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="Nationality" type="{}CountryCode"/>
				 *         &lt;element name="PlaceOfWork" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="80"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="FatherOrSpouse" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="80"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="Occupation" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="50"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *       &lt;/sequence>
				 *     &lt;/restriction>
				 *   &lt;/complexContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "gender", "dateOfBirth", "identificationType", "identificationNumber", "issuingAuthority",
						"placeOfIssue", "nationality", "placeOfWork", "fatherOrSpouse", "occupation" })
				public static class Individual {

					@XmlElement(name = "Gender", required = true)
					protected Gender gender;

					@XmlElement(name = "DateOfBirth")
					protected String dateOfBirth;

					@XmlElement(name = "IdentificationType", required = true, nillable = true)
					protected IdentificationType identificationType;

					@XmlElementRef(name = "IdentificationNumber", type = JAXBElement.class)
					protected JAXBElement<String> identificationNumber;

					@XmlElementRef(name = "IssuingAuthority", type = JAXBElement.class)
					protected JAXBElement<String> issuingAuthority;

					@XmlElementRef(name = "PlaceOfIssue", type = JAXBElement.class)
					protected JAXBElement<String> placeOfIssue;

					@XmlElement(name = "Nationality", required = true)
					protected CountryCode nationality;

					@XmlElement(name = "PlaceOfWork")
					protected String placeOfWork;

					@XmlElement(name = "FatherOrSpouse")
					protected String fatherOrSpouse;

					@XmlElement(name = "Occupation")
					protected String occupation;

					/**
					 * Gets the value of the gender property.
					 * 
					 * @return possible object is {@link Gender }
					 * 
					 */
					public Gender getGender() {
						return gender;
					}

					/**
					 * Sets the value of the gender property.
					 * 
					 * @param value
					 *            allowed object is {@link Gender }
					 * 
					 */
					public void setGender(Gender value) {
						this.gender = value;
					}

					/**
					 * Gets the value of the dateOfBirth property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getDateOfBirth() {
						return dateOfBirth;
					}

					/**
					 * Sets the value of the dateOfBirth property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setDateOfBirth(String value) {
						this.dateOfBirth = value;
					}

					/**
					 * Gets the value of the identificationType property.
					 * 
					 * @return possible object is {@link IdentificationType }
					 * 
					 */
					public IdentificationType getIdentificationType() {
						return identificationType;
					}

					/**
					 * Sets the value of the identificationType property.
					 * 
					 * @param value
					 *            allowed object is {@link IdentificationType }
					 * 
					 */
					public void setIdentificationType(IdentificationType value) {
						this.identificationType = value;
					}

					/**
					 * Gets the value of the identificationNumber property.
					 * 
					 * @return possible object is {@link JAXBElement }{@code <}
					 *         {@link String }{@code >}
					 * 
					 */
					public JAXBElement<String> getIdentificationNumber() {
						return identificationNumber;
					}

					/**
					 * Sets the value of the identificationNumber property.
					 * 
					 * @param value
					 *            allowed object is {@link JAXBElement }{@code <}
					 *            {@link String }{@code >}
					 * 
					 */
					public void setIdentificationNumber(JAXBElement<String> value) {
						this.identificationNumber = value;
					}

					/**
					 * Gets the value of the issuingAuthority property.
					 * 
					 * @return possible object is {@link JAXBElement }{@code <}
					 *         {@link String }{@code >}
					 * 
					 */
					public JAXBElement<String> getIssuingAuthority() {
						return issuingAuthority;
					}

					/**
					 * Sets the value of the issuingAuthority property.
					 * 
					 * @param value
					 *            allowed object is {@link JAXBElement }{@code <}
					 *            {@link String }{@code >}
					 * 
					 */
					public void setIssuingAuthority(JAXBElement<String> value) {
						this.issuingAuthority = value;
					}

					/**
					 * Gets the value of the placeOfIssue property.
					 * 
					 * @return possible object is {@link JAXBElement }{@code <}
					 *         {@link String }{@code >}
					 * 
					 */
					public JAXBElement<String> getPlaceOfIssue() {
						return placeOfIssue;
					}

					/**
					 * Sets the value of the placeOfIssue property.
					 * 
					 * @param value
					 *            allowed object is {@link JAXBElement }{@code <}
					 *            {@link String }{@code >}
					 * 
					 */
					public void setPlaceOfIssue(JAXBElement<String> value) {
						this.placeOfIssue = value;
					}

					/**
					 * Gets the value of the nationality property.
					 * 
					 * @return possible object is {@link CountryCode }
					 * 
					 */
					public CountryCode getNationality() {
						return nationality;
					}

					/**
					 * Sets the value of the nationality property.
					 * 
					 * @param value
					 *            allowed object is {@link CountryCode }
					 * 
					 */
					public void setNationality(CountryCode value) {
						this.nationality = value;
					}

					/**
					 * Gets the value of the placeOfWork property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getPlaceOfWork() {
						return placeOfWork;
					}

					/**
					 * Sets the value of the placeOfWork property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setPlaceOfWork(String value) {
						this.placeOfWork = value;
					}

					/**
					 * Gets the value of the fatherOrSpouse property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getFatherOrSpouse() {
						return fatherOrSpouse;
					}

					/**
					 * Sets the value of the fatherOrSpouse property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setFatherOrSpouse(String value) {
						this.fatherOrSpouse = value;
					}

					/**
					 * Gets the value of the occupation property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getOccupation() {
						return occupation;
					}

					/**
					 * Sets the value of the occupation property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setOccupation(String value) {
						this.occupation = value;
					}

				}

				/**
				 * <p>
				 * Java class for anonymous complex type.
				 * 
				 * <p>
				 * The following schema fragment specifies the expected content
				 * contained within this class.
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;complexContent>
				 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
				 *       &lt;sequence>
				 *         &lt;element name="ConstitutionType" type="{}ConstitutionType"/>
				 *         &lt;element name="RegistrationNumber" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;maxLength value="20"/>
				 *               &lt;minLength value="0"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="DateOfIncorporation" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
				 *         &lt;element name="PlaceOfRegistration" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;maxLength value="20"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="CountryCode" type="{}CountryCode"/>
				 *         &lt;element name="NatureOfBusiness" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;minLength value="0"/>
				 *               &lt;maxLength value="50"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *       &lt;/sequence>
				 *     &lt;/restriction>
				 *   &lt;/complexContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "constitutionType", "registrationNumber", "dateOfIncorporation", "placeOfRegistration",
						"countryCode", "natureOfBusiness" })
				public static class LegalPerson {

					@XmlElement(name = "ConstitutionType", required = true, nillable = true)
					protected ConstitutionType constitutionType;

					@XmlElement(name = "RegistrationNumber")
					protected String registrationNumber;

					@XmlElement(name = "DateOfIncorporation")
					protected String dateOfIncorporation;

					@XmlElement(name = "PlaceOfRegistration")
					protected String placeOfRegistration;

					@XmlElement(name = "CountryCode", required = true)
					protected CountryCode countryCode;

					@XmlElement(name = "NatureOfBusiness")
					protected String natureOfBusiness;

					/**
					 * Gets the value of the constitutionType property.
					 * 
					 * @return possible object is {@link ConstitutionType }
					 * 
					 */
					public ConstitutionType getConstitutionType() {
						return constitutionType;
					}

					/**
					 * Sets the value of the constitutionType property.
					 * 
					 * @param value
					 *            allowed object is {@link ConstitutionType }
					 * 
					 */
					public void setConstitutionType(ConstitutionType value) {
						this.constitutionType = value;
					}

					/**
					 * Gets the value of the registrationNumber property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getRegistrationNumber() {
						return registrationNumber;
					}

					/**
					 * Sets the value of the registrationNumber property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setRegistrationNumber(String value) {
						this.registrationNumber = value;
					}

					/**
					 * Gets the value of the dateOfIncorporation property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getDateOfIncorporation() {
						return dateOfIncorporation;
					}

					/**
					 * Sets the value of the dateOfIncorporation property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setDateOfIncorporation(String value) {
						this.dateOfIncorporation = value;
					}

					/**
					 * Gets the value of the placeOfRegistration property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getPlaceOfRegistration() {
						return placeOfRegistration;
					}

					/**
					 * Sets the value of the placeOfRegistration property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setPlaceOfRegistration(String value) {
						this.placeOfRegistration = value;
					}

					/**
					 * Gets the value of the countryCode property.
					 * 
					 * @return possible object is {@link CountryCode }
					 * 
					 */
					public CountryCode getCountryCode() {
						return countryCode;
					}

					/**
					 * Sets the value of the countryCode property.
					 * 
					 * @param value
					 *            allowed object is {@link CountryCode }
					 * 
					 */
					public void setCountryCode(CountryCode value) {
						this.countryCode = value;
					}

					/**
					 * Gets the value of the natureOfBusiness property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getNatureOfBusiness() {
						return natureOfBusiness;
					}

					/**
					 * Sets the value of the natureOfBusiness property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setNatureOfBusiness(String value) {
						this.natureOfBusiness = value;
					}

				}

			}

			/**
			 * <p>
			 * Java class for anonymous complex type.
			 * 
			 * <p>
			 * The following schema fragment specifies the expected content
			 * contained within this class.
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="DateOfTransaction" type="{http://www.w3.org/2001/XMLSchema}date"/>
			 *         &lt;element name="TransactionID" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;minLength value="0"/>
			 *               &lt;maxLength value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="TransactionMode" type="{}TransactionMode"/>
			 *         &lt;element name="DebitCredit" type="{}DebitCredit"/>
			 *         &lt;element name="Amount">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
			 *               &lt;totalDigits value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="Currency" type="{}CurrencyCode"/>
			 *         &lt;element name="ProductTransaction" minOccurs="0">
			 *           &lt;complexType>
			 *             &lt;complexContent>
			 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *                 &lt;sequence>
			 *                   &lt;element name="ProductType" type="{}ProductType"/>
			 *                   &lt;element name="Identifier" minOccurs="0">
			 *                     &lt;simpleType>
			 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *                         &lt;maxLength value="30"/>
			 *                         &lt;minLength value="0"/>
			 *                       &lt;/restriction>
			 *                     &lt;/simpleType>
			 *                   &lt;/element>
			 *                   &lt;element name="TransactionType" type="{}ProductTransactionType"/>
			 *                   &lt;element name="Units">
			 *                     &lt;simpleType>
			 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
			 *                         &lt;totalDigits value="20"/>
			 *                       &lt;/restriction>
			 *                     &lt;/simpleType>
			 *                   &lt;/element>
			 *                   &lt;element name="Rate">
			 *                     &lt;simpleType>
			 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
			 *                         &lt;totalDigits value="10"/>
			 *                         &lt;fractionDigits value="4"/>
			 *                       &lt;/restriction>
			 *                     &lt;/simpleType>
			 *                   &lt;/element>
			 *                 &lt;/sequence>
			 *               &lt;/restriction>
			 *             &lt;/complexContent>
			 *           &lt;/complexType>
			 *         &lt;/element>
			 *         &lt;element name="DispositionOfFunds" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;minLength value="0"/>
			 *               &lt;maxLength value="1"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="RelatedAccountNum" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;maxLength value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="RelatedInstitutionName" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;maxLength value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="RelatedInstitutionRefNum" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;maxLength value="20"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *         &lt;element name="Remarks" minOccurs="0">
			 *           &lt;simpleType>
			 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
			 *               &lt;maxLength value="50"/>
			 *             &lt;/restriction>
			 *           &lt;/simpleType>
			 *         &lt;/element>
			 *       &lt;/sequence>
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "dateOfTransaction", "transactionID", "transactionMode", "debitCredit", "amount", "currency",
					"productTransaction", "dispositionOfFunds", "relatedAccountNum", "relatedInstitutionName", "relatedInstitutionRefNum",
					"remarks" })
			public static class Transaction {

				@XmlElement(name = "DateOfTransaction", required = true)
				protected String dateOfTransaction;

				@XmlElementRef(name = "TransactionID", type = JAXBElement.class)
				protected JAXBElement<String> transactionID;

				@XmlElement(name = "TransactionMode", required = true)
				protected TransactionMode transactionMode;

				@XmlElement(name = "DebitCredit", required = true)
				protected DebitCredit debitCredit;

				@XmlElement(name = "Amount", required = true)
				protected BigInteger amount;

				@XmlElement(name = "Currency", required = true)
				protected CurrencyCode currency;

				@XmlElement(name = "ProductTransaction")
				protected Batch.Report.Account.Transaction.ProductTransaction productTransaction;

				@XmlElement(name = "DispositionOfFunds", defaultValue = "X")
				protected String dispositionOfFunds;

				@XmlElement(name = "RelatedAccountNum")
				protected String relatedAccountNum;

				@XmlElement(name = "RelatedInstitutionName")
				protected String relatedInstitutionName;

				@XmlElement(name = "RelatedInstitutionRefNum")
				protected String relatedInstitutionRefNum;

				@XmlElement(name = "Remarks")
				protected String remarks;

				/**
				 * Gets the value of the dateOfTransaction property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getDateOfTransaction() {
					return dateOfTransaction;
				}

				/**
				 * Sets the value of the dateOfTransaction property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setDateOfTransaction(String value) {
					this.dateOfTransaction = value;
				}

				/**
				 * Gets the value of the transactionID property.
				 * 
				 * @return possible object is {@link JAXBElement }{@code <}
				 *         {@link String }{@code >}
				 * 
				 */
				public JAXBElement<String> getTransactionID() {
					return transactionID;
				}

				/**
				 * Sets the value of the transactionID property.
				 * 
				 * @param value
				 *            allowed object is {@link JAXBElement }{@code <}
				 *            {@link String }{@code >}
				 * 
				 */
				public void setTransactionID(JAXBElement<String> value) {
					this.transactionID = value;
				}

				/**
				 * Gets the value of the transactionMode property.
				 * 
				 * @return possible object is {@link TransactionMode }
				 * 
				 */
				public TransactionMode getTransactionMode() {
					return transactionMode;
				}

				/**
				 * Sets the value of the transactionMode property.
				 * 
				 * @param value
				 *            allowed object is {@link TransactionMode }
				 * 
				 */
				public void setTransactionMode(TransactionMode value) {
					this.transactionMode = value;
				}

				/**
				 * Gets the value of the debitCredit property.
				 * 
				 * @return possible object is {@link DebitCredit }
				 * 
				 */
				public DebitCredit getDebitCredit() {
					return debitCredit;
				}

				/**
				 * Sets the value of the debitCredit property.
				 * 
				 * @param value
				 *            allowed object is {@link DebitCredit }
				 * 
				 */
				public void setDebitCredit(DebitCredit value) {
					this.debitCredit = value;
				}

				/**
				 * Gets the value of the amount property.
				 * 
				 * @return possible object is {@link BigInteger }
				 * 
				 */
				public BigInteger getAmount() {
					return amount;
				}

				/**
				 * Sets the value of the amount property.
				 * 
				 * @param value
				 *            allowed object is {@link BigInteger }
				 * 
				 */
				public void setAmount(BigInteger value) {
					this.amount = value;
				}

				/**
				 * Gets the value of the currency property.
				 * 
				 * @return possible object is {@link CurrencyCode }
				 * 
				 */
				public CurrencyCode getCurrency() {
					return currency;
				}

				/**
				 * Sets the value of the currency property.
				 * 
				 * @param value
				 *            allowed object is {@link CurrencyCode }
				 * 
				 */
				public void setCurrency(CurrencyCode value) {
					this.currency = value;
				}

				/**
				 * Gets the value of the productTransaction property.
				 * 
				 * @return possible object is
				 *         {@link Batch.Report.Account.Transaction.ProductTransaction }
				 * 
				 */
				public Batch.Report.Account.Transaction.ProductTransaction getProductTransaction() {
					return productTransaction;
				}

				/**
				 * Sets the value of the productTransaction property.
				 * 
				 * @param value
				 *            allowed object is
				 *            {@link Batch.Report.Account.Transaction.ProductTransaction }
				 * 
				 */
				public void setProductTransaction(Batch.Report.Account.Transaction.ProductTransaction value) {
					this.productTransaction = value;
				}

				/**
				 * Gets the value of the dispositionOfFunds property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getDispositionOfFunds() {
					return dispositionOfFunds;
				}

				/**
				 * Sets the value of the dispositionOfFunds property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setDispositionOfFunds(String value) {
					this.dispositionOfFunds = value;
				}

				/**
				 * Gets the value of the relatedAccountNum property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getRelatedAccountNum() {
					return relatedAccountNum;
				}

				/**
				 * Sets the value of the relatedAccountNum property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setRelatedAccountNum(String value) {
					this.relatedAccountNum = value;
				}

				/**
				 * Gets the value of the relatedInstitutionName property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getRelatedInstitutionName() {
					return relatedInstitutionName;
				}

				/**
				 * Sets the value of the relatedInstitutionName property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setRelatedInstitutionName(String value) {
					this.relatedInstitutionName = value;
				}

				/**
				 * Gets the value of the relatedInstitutionRefNum property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getRelatedInstitutionRefNum() {
					return relatedInstitutionRefNum;
				}

				/**
				 * Sets the value of the relatedInstitutionRefNum property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setRelatedInstitutionRefNum(String value) {
					this.relatedInstitutionRefNum = value;
				}

				/**
				 * Gets the value of the remarks property.
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getRemarks() {
					return remarks;
				}

				/**
				 * Sets the value of the remarks property.
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setRemarks(String value) {
					this.remarks = value;
				}

				/**
				 * <p>
				 * Java class for anonymous complex type.
				 * 
				 * <p>
				 * The following schema fragment specifies the expected content
				 * contained within this class.
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;complexContent>
				 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
				 *       &lt;sequence>
				 *         &lt;element name="ProductType" type="{}ProductType"/>
				 *         &lt;element name="Identifier" minOccurs="0">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
				 *               &lt;maxLength value="30"/>
				 *               &lt;minLength value="0"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="TransactionType" type="{}ProductTransactionType"/>
				 *         &lt;element name="Units">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
				 *               &lt;totalDigits value="20"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *         &lt;element name="Rate">
				 *           &lt;simpleType>
				 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
				 *               &lt;totalDigits value="10"/>
				 *               &lt;fractionDigits value="4"/>
				 *             &lt;/restriction>
				 *           &lt;/simpleType>
				 *         &lt;/element>
				 *       &lt;/sequence>
				 *     &lt;/restriction>
				 *   &lt;/complexContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "productType", "identifier", "transactionType", "units", "rate" })
				public static class ProductTransaction {

					@XmlElement(name = "ProductType", required = true)
					protected ProductType productType;
					@XmlElement(name = "Identifier")
					protected String identifier;
					@XmlElement(name = "TransactionType", required = true)
					protected ProductTransactionType transactionType;
					@XmlElement(name = "Units", required = true)
					protected BigInteger units;
					@XmlElement(name = "Rate", required = true)
					protected BigDecimal rate;

					/**
					 * Gets the value of the productType property.
					 * 
					 * @return possible object is {@link ProductType }
					 * 
					 */
					public ProductType getProductType() {
						return productType;
					}

					/**
					 * Sets the value of the productType property.
					 * 
					 * @param value
					 *            allowed object is {@link ProductType }
					 * 
					 */
					public void setProductType(ProductType value) {
						this.productType = value;
					}

					/**
					 * Gets the value of the identifier property.
					 * 
					 * @return possible object is {@link String }
					 * 
					 */
					public String getIdentifier() {
						return identifier;
					}

					/**
					 * Sets the value of the identifier property.
					 * 
					 * @param value
					 *            allowed object is {@link String }
					 * 
					 */
					public void setIdentifier(String value) {
						this.identifier = value;
					}

					/**
					 * Gets the value of the transactionType property.
					 * 
					 * @return possible object is {@link ProductTransactionType }
					 * 
					 */
					public ProductTransactionType getTransactionType() {
						return transactionType;
					}

					/**
					 * Sets the value of the transactionType property.
					 * 
					 * @param value
					 *            allowed object is
					 *            {@link ProductTransactionType }
					 * 
					 */
					public void setTransactionType(ProductTransactionType value) {
						this.transactionType = value;
					}

					/**
					 * Gets the value of the units property.
					 * 
					 * @return possible object is {@link BigInteger }
					 * 
					 */
					public BigInteger getUnits() {
						return units;
					}

					/**
					 * Sets the value of the units property.
					 * 
					 * @param value
					 *            allowed object is {@link BigInteger }
					 * 
					 */
					public void setUnits(BigInteger value) {
						this.units = value;
					}

					/**
					 * Gets the value of the rate property.
					 * 
					 * @return possible object is {@link BigDecimal }
					 * 
					 */
					public BigDecimal getRate() {
						return rate;
					}

					/**
					 * Sets the value of the rate property.
					 * 
					 * @param value
					 *            allowed object is {@link BigDecimal }
					 * 
					 */
					public void setRate(BigDecimal value) {
						this.rate = value;
					}

				}

			}

		}

	}

}
