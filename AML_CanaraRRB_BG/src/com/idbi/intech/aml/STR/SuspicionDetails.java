//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//

package com.idbi.intech.aml.STR;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for SuspicionDetails complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="SuspicionDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SourceOfAlert" type="{}SourceOfAlert"/>
 *         &lt;element name="AlertIndicator" maxOccurs="unbounded" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SuspicionDueToProceedsOfCrime" type="{}YesNo"/>
 *         &lt;element name="SuspicionDueToComplexTrans" type="{}YesNo"/>
 *         &lt;element name="SuspicionDueToNoEcoRationale" type="{}YesNo"/>
 *         &lt;element name="SuspicionOfFinancingOfTerrorism" type="{}YesNo"/>
 *         &lt;element name="AttemptedTransaction" type="{}YesNo"/>
 *         &lt;element name="GroundsOfSuspicion">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="4000"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DetailsOfInvestigations" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LEAInformed" type="{}LEAInformed"/>
 *         &lt;element name="LEADetails" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="250"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PriorityRating" type="{}PriorityRating"/>
 *         &lt;element name="ReportCoverage" type="{}ReportCoverage"/>
 *         &lt;element name="AdditionalDocuments" type="{}YesNo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "SuspicionDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuspicionDetails", propOrder = { "sourceOfAlert", "alertIndicator", "suspicionDueToProceedsOfCrime",
		"suspicionDueToComplexTrans", "suspicionDueToNoEcoRationale", "suspicionOfFinancingOfTerrorism", "attemptedTransaction",
		"groundsOfSuspicion", "detailsOfInvestigations", "leaInformed", "leaDetails", "priorityRating", "reportCoverage", "additionalDocuments" })
public class SuspicionDetails {

	@XmlElement(name = "SourceOfAlert", required = true)
	protected SourceOfAlert sourceOfAlert;
	@XmlElement(name = "AlertIndicator")
	protected List<String> alertIndicator;
	@XmlElement(name = "SuspicionDueToProceedsOfCrime", required = true)
	protected YesNo suspicionDueToProceedsOfCrime;
	@XmlElement(name = "SuspicionDueToComplexTrans", required = true)
	protected YesNo suspicionDueToComplexTrans;
	@XmlElement(name = "SuspicionDueToNoEcoRationale", required = true)
	protected YesNo suspicionDueToNoEcoRationale;
	@XmlElement(name = "SuspicionOfFinancingOfTerrorism", required = true)
	protected YesNo suspicionOfFinancingOfTerrorism;
	@XmlElement(name = "AttemptedTransaction", required = true)
	protected YesNo attemptedTransaction;
	@XmlElement(name = "GroundsOfSuspicion", required = true)
	protected String groundsOfSuspicion;
	@XmlElement(name = "DetailsOfInvestigations")
	protected String detailsOfInvestigations;
	@XmlElement(name = "LEAInformed", required = true)
	protected LEAInformed leaInformed;
	@XmlElement(name = "LEADetails")
	protected String leaDetails;
	@XmlElement(name = "PriorityRating", required = true)
	protected PriorityRating priorityRating;
	@XmlElement(name = "ReportCoverage", required = true)
	protected ReportCoverage reportCoverage;
	@XmlElement(name = "AdditionalDocuments", required = true)
	protected YesNo additionalDocuments;

	/**
	 * Gets the value of the sourceOfAlert property.
	 * 
	 * @return possible object is {@link SourceOfAlert }
	 * 
	 */
	public SourceOfAlert getSourceOfAlert() {
		return sourceOfAlert;
	}

	/**
	 * Sets the value of the sourceOfAlert property.
	 * 
	 * @param value
	 *            allowed object is {@link SourceOfAlert }
	 * 
	 */
	public void setSourceOfAlert(SourceOfAlert value) {
		this.sourceOfAlert = value;
	}

	/**
	 * Gets the value of the alertIndicator property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the alertIndicator property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAlertIndicator().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getAlertIndicator() {
		if (alertIndicator == null) {
			alertIndicator = new ArrayList<String>();
		}
		return this.alertIndicator;
	}

	/**
	 * Gets the value of the suspicionDueToProceedsOfCrime property.
	 * 
	 * @return possible object is {@link YesNo }
	 * 
	 */
	public YesNo getSuspicionDueToProceedsOfCrime() {
		return suspicionDueToProceedsOfCrime;
	}

	/**
	 * Sets the value of the suspicionDueToProceedsOfCrime property.
	 * 
	 * @param value
	 *            allowed object is {@link YesNo }
	 * 
	 */
	public void setSuspicionDueToProceedsOfCrime(YesNo value) {
		this.suspicionDueToProceedsOfCrime = value;
	}

	/**
	 * Gets the value of the suspicionDueToComplexTrans property.
	 * 
	 * @return possible object is {@link YesNo }
	 * 
	 */
	public YesNo getSuspicionDueToComplexTrans() {
		return suspicionDueToComplexTrans;
	}

	/**
	 * Sets the value of the suspicionDueToComplexTrans property.
	 * 
	 * @param value
	 *            allowed object is {@link YesNo }
	 * 
	 */
	public void setSuspicionDueToComplexTrans(YesNo value) {
		this.suspicionDueToComplexTrans = value;
	}

	/**
	 * Gets the value of the suspicionDueToNoEcoRationale property.
	 * 
	 * @return possible object is {@link YesNo }
	 * 
	 */
	public YesNo getSuspicionDueToNoEcoRationale() {
		return suspicionDueToNoEcoRationale;
	}

	/**
	 * Sets the value of the suspicionDueToNoEcoRationale property.
	 * 
	 * @param value
	 *            allowed object is {@link YesNo }
	 * 
	 */
	public void setSuspicionDueToNoEcoRationale(YesNo value) {
		this.suspicionDueToNoEcoRationale = value;
	}

	/**
	 * Gets the value of the suspicionOfFinancingOfTerrorism property.
	 * 
	 * @return possible object is {@link YesNo }
	 * 
	 */
	public YesNo getSuspicionOfFinancingOfTerrorism() {
		return suspicionOfFinancingOfTerrorism;
	}

	/**
	 * Sets the value of the suspicionOfFinancingOfTerrorism property.
	 * 
	 * @param value
	 *            allowed object is {@link YesNo }
	 * 
	 */
	public void setSuspicionOfFinancingOfTerrorism(YesNo value) {
		this.suspicionOfFinancingOfTerrorism = value;
	}

	/**
	 * Gets the value of the attemptedTransaction property.
	 * 
	 * @return possible object is {@link YesNo }
	 * 
	 */
	public YesNo getAttemptedTransaction() {
		return attemptedTransaction;
	}

	/**
	 * Sets the value of the attemptedTransaction property.
	 * 
	 * @param value
	 *            allowed object is {@link YesNo }
	 * 
	 */
	public void setAttemptedTransaction(YesNo value) {
		this.attemptedTransaction = value;
	}

	/**
	 * Gets the value of the groundsOfSuspicion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGroundsOfSuspicion() {
		return groundsOfSuspicion;
	}

	/**
	 * Sets the value of the groundsOfSuspicion property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGroundsOfSuspicion(String value) {
		this.groundsOfSuspicion = value;
	}

	/**
	 * Gets the value of the detailsOfInvestigations property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDetailsOfInvestigations() {
		return detailsOfInvestigations;
	}

	/**
	 * Sets the value of the detailsOfInvestigations property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDetailsOfInvestigations(String value) {
		this.detailsOfInvestigations = value;
	}

	/**
	 * Gets the value of the leaInformed property.
	 * 
	 * @return possible object is {@link LEAInformed }
	 * 
	 */
	public LEAInformed getLEAInformed() {
		return leaInformed;
	}

	/**
	 * Sets the value of the leaInformed property.
	 * 
	 * @param value
	 *            allowed object is {@link LEAInformed }
	 * 
	 */
	public void setLEAInformed(LEAInformed value) {
		this.leaInformed = value;
	}

	/**
	 * Gets the value of the leaDetails property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLEADetails() {
		return leaDetails;
	}

	/**
	 * Sets the value of the leaDetails property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLEADetails(String value) {
		this.leaDetails = value;
	}

	/**
	 * Gets the value of the priorityRating property.
	 * 
	 * @return possible object is {@link PriorityRating }
	 * 
	 */
	public PriorityRating getPriorityRating() {
		return priorityRating;
	}

	/**
	 * Sets the value of the priorityRating property.
	 * 
	 * @param value
	 *            allowed object is {@link PriorityRating }
	 * 
	 */
	public void setPriorityRating(PriorityRating value) {
		this.priorityRating = value;
	}

	/**
	 * Gets the value of the reportCoverage property.
	 * 
	 * @return possible object is {@link ReportCoverage }
	 * 
	 */
	public ReportCoverage getReportCoverage() {
		return reportCoverage;
	}

	/**
	 * Sets the value of the reportCoverage property.
	 * 
	 * @param value
	 *            allowed object is {@link ReportCoverage }
	 * 
	 */
	public void setReportCoverage(ReportCoverage value) {
		this.reportCoverage = value;
	}

	/**
	 * Gets the value of the additionalDocuments property.
	 * 
	 * @return possible object is {@link YesNo }
	 * 
	 */
	public YesNo getAdditionalDocuments() {
		return additionalDocuments;
	}

	/**
	 * Sets the value of the additionalDocuments property.
	 * 
	 * @param value
	 *            allowed object is {@link YesNo }
	 * 
	 */
	public void setAdditionalDocuments(YesNo value) {
		this.additionalDocuments = value;
	}

}
