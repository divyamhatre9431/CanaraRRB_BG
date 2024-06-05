//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//

package com.idbi.intech.aml.STR;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Complex BatchDetails type to provide information regarding the batch
 * submitted
 * 
 * <p>
 * Java class for BatchDetails complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="BatchDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BatchNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="11"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="BatchDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="MonthOfReport">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="4"/>
 *               &lt;enumeration value="5"/>
 *               &lt;enumeration value="6"/>
 *               &lt;enumeration value="7"/>
 *               &lt;enumeration value="8"/>
 *               &lt;enumeration value="9"/>
 *               &lt;enumeration value="01"/>
 *               &lt;enumeration value="02"/>
 *               &lt;enumeration value="03"/>
 *               &lt;enumeration value="04"/>
 *               &lt;enumeration value="05"/>
 *               &lt;enumeration value="06"/>
 *               &lt;enumeration value="07"/>
 *               &lt;enumeration value="08"/>
 *               &lt;enumeration value="09"/>
 *               &lt;enumeration value="10"/>
 *               &lt;enumeration value="11"/>
 *               &lt;enumeration value="12"/>
 *               &lt;enumeration value="NA"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="YearOfReport">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="2005"/>
 *               &lt;enumeration value="2006"/>
 *               &lt;enumeration value="2007"/>
 *               &lt;enumeration value="2008"/>
 *               &lt;enumeration value="2009"/>
 *               &lt;enumeration value="2010"/>
 *               &lt;enumeration value="2011"/>
 *               &lt;enumeration value="2012"/>
 *               &lt;enumeration value="2013"/>
 *               &lt;enumeration value="2014"/>
 *               &lt;enumeration value="2015"/>
 *               &lt;enumeration value="2016"/>
 *               &lt;enumeration value="2017"/>
 *               &lt;enumeration value="2018"/>
 *               &lt;enumeration value="2019"/>
 *               &lt;enumeration value="2020"/>
 *               &lt;enumeration value="NA"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OperationalMode" type="{}OperationalMode"/>
 *         &lt;element name="BatchType" type="{}BatchType"/>
 *         &lt;element name="OriginalBatchID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *               &lt;totalDigits value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ReasonOfRevision" type="{}ReasonForRevision"/>
 *         &lt;element name="PKICertificateNum" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="10"/>
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
@XmlRootElement(name = "BatchDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchDetails", propOrder = { "batchNumber", "batchDate", "monthOfReport", "yearOfReport", "operationalMode", "batchType",
		"originalBatchID", "reasonOfRevision", "pkiCertificateNum" })
public class BatchDetails {

	@XmlElement(name = "BatchNumber", required = true)
	protected String batchNumber;

	@XmlElement(name = "BatchDate", required = true)
	protected String batchDate;

	@XmlElement(name = "MonthOfReport", required = true)
	protected String monthOfReport;

	@XmlElement(name = "YearOfReport", required = true)
	protected String yearOfReport;

	@XmlElement(name = "OperationalMode", required = true)
	protected OperationalMode operationalMode;

	@XmlElement(name = "BatchType", required = true)
	protected BatchType batchType;

	@XmlElement(name = "OriginalBatchID", required = true)
	protected BigInteger originalBatchID;

	@XmlElement(name = "ReasonOfRevision", required = true)
	protected ReasonForRevision reasonOfRevision;

	@XmlElement(name = "PKICertificateNum")
	protected String pkiCertificateNum;

	/**
	 * Gets the value of the batchNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * Sets the value of the batchNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBatchNumber(String value) {
		this.batchNumber = value;
	}

	/**
	 * Gets the value of the batchDate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBatchDate() {
		return batchDate;
	}

	/**
	 * Sets the value of the batchDate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBatchDate(String value) {
		this.batchDate = value;
	}

	/**
	 * Gets the value of the monthOfReport property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMonthOfReport() {
		return monthOfReport;
	}

	/**
	 * Sets the value of the monthOfReport property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMonthOfReport(String value) {
		this.monthOfReport = value;
	}

	/**
	 * Gets the value of the yearOfReport property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getYearOfReport() {
		return yearOfReport;
	}

	/**
	 * Sets the value of the yearOfReport property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setYearOfReport(String value) {
		this.yearOfReport = value;
	}

	/**
	 * Gets the value of the operationalMode property.
	 * 
	 * @return possible object is {@link OperationalMode }
	 * 
	 */
	public OperationalMode getOperationalMode() {
		return operationalMode;
	}

	/**
	 * Sets the value of the operationalMode property.
	 * 
	 * @param value
	 *            allowed object is {@link OperationalMode }
	 * 
	 */
	public void setOperationalMode(OperationalMode value) {
		this.operationalMode = value;
	}

	/**
	 * Gets the value of the batchType property.
	 * 
	 * @return possible object is {@link BatchType }
	 * 
	 */
	public BatchType getBatchType() {
		return batchType;
	}

	/**
	 * Sets the value of the batchType property.
	 * 
	 * @param value
	 *            allowed object is {@link BatchType }
	 * 
	 */
	public void setBatchType(BatchType value) {
		this.batchType = value;
	}

	/**
	 * Gets the value of the originalBatchID property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getOriginalBatchID() {
		return originalBatchID;
	}

	/**
	 * Sets the value of the originalBatchID property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setOriginalBatchID(BigInteger value) {
		this.originalBatchID = value;
	}

	/**
	 * Gets the value of the reasonOfRevision property.
	 * 
	 * @return possible object is {@link ReasonForRevision }
	 * 
	 */
	public ReasonForRevision getReasonOfRevision() {
		return reasonOfRevision;
	}

	/**
	 * Sets the value of the reasonOfRevision property.
	 * 
	 * @param value
	 *            allowed object is {@link ReasonForRevision }
	 * 
	 */
	public void setReasonOfRevision(ReasonForRevision value) {
		this.reasonOfRevision = value;
	}

	/**
	 * Gets the value of the pkiCertificateNum property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPKICertificateNum() {
		return pkiCertificateNum;
	}

	/**
	 * Sets the value of the pkiCertificateNum property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPKICertificateNum(String value) {
		this.pkiCertificateNum = value;
	}

}
