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
 * <p>Java class for AccountRiskRating.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccountRiskRating">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="A1"/>
 *     &lt;enumeration value="A2"/>
 *     &lt;enumeration value="A3"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccountRiskRating")
@XmlEnum
public enum AccountRiskRating {

    @XmlEnumValue("A1")
    A_1("A1"),
    @XmlEnumValue("A2")
    A_2("A2"),
    @XmlEnumValue("A3")
    A_3("A3"),
    XX("XX");
    private final String value;

    AccountRiskRating(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccountRiskRating fromValue(String v) {
        for (AccountRiskRating c: AccountRiskRating.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
