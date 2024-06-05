//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//


package com.idbi.intech.aml.STR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FIUReportSubmissionExternalComplexTypeLibraryVersionNumber">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *               &lt;/extension>
 *             &lt;/simpleContent>
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
@XmlType(name = "", propOrder = {
    "fiuReportSubmissionExternalComplexTypeLibraryVersionNumber"
})
@XmlRootElement(name = "FIUReportSubmissionExternalLibrary")
public class FIUReportSubmissionExternalLibrary {

    @XmlElement(name = "FIUReportSubmissionExternalComplexTypeLibraryVersionNumber", required = true, defaultValue = "1.0")
    protected FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber fiuReportSubmissionExternalComplexTypeLibraryVersionNumber;

    /**
     * Gets the value of the fiuReportSubmissionExternalComplexTypeLibraryVersionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber }
     *     
     */
    public FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber getFIUReportSubmissionExternalComplexTypeLibraryVersionNumber() {
        return fiuReportSubmissionExternalComplexTypeLibraryVersionNumber;
    }

    /**
     * Sets the value of the fiuReportSubmissionExternalComplexTypeLibraryVersionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber }
     *     
     */
    public void setFIUReportSubmissionExternalComplexTypeLibraryVersionNumber(FIUReportSubmissionExternalLibrary.FIUReportSubmissionExternalComplexTypeLibraryVersionNumber value) {
        this.fiuReportSubmissionExternalComplexTypeLibraryVersionNumber = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class FIUReportSubmissionExternalComplexTypeLibraryVersionNumber {

        @XmlValue
        protected String value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
