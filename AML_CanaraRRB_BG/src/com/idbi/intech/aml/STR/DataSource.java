//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//


package com.idbi.intech.aml.STR;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataSource.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DataSource">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="txt"/>
 *     &lt;enumeration value="xml"/>
 *     &lt;enumeration value="rgu"/>
 *     &lt;enumeration value="pdf"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DataSource")
@XmlEnum
public enum DataSource {

    @XmlEnumValue("txt")
    TXT("txt"),
    @XmlEnumValue("xml")
    XML("xml"),
    @XmlEnumValue("rgu")
    RGU("rgu"),
    @XmlEnumValue("pdf")
    PDF("pdf");
    private final String value;

    DataSource(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataSource fromValue(String v) {
        for (DataSource c: DataSource.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
