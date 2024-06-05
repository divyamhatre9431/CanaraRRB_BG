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
 * <p>Java class for TransactionRiskRating.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionRiskRating">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="T1"/>
 *     &lt;enumeration value="T2"/>
 *     &lt;enumeration value="T3"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionRiskRating")
@XmlEnum
public enum TransactionRiskRating {

    @XmlEnumValue("T1")
    T_1("T1"),
    @XmlEnumValue("T2")
    T_2("T2"),
    @XmlEnumValue("T3")
    T_3("T3"),
    XX("XX");
    private final String value;

    TransactionRiskRating(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionRiskRating fromValue(String v) {
        for (TransactionRiskRating c: TransactionRiskRating.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
