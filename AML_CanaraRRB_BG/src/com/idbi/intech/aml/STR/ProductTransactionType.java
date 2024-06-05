//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//


package com.idbi.intech.aml.STR;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductTransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProductTransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BP"/>
 *     &lt;enumeration value="SR"/>
 *     &lt;enumeration value="IA"/>
 *     &lt;enumeration value="IP"/>
 *     &lt;enumeration value="IC"/>
 *     &lt;enumeration value="ID"/>
 *     &lt;enumeration value="IM"/>
 *     &lt;enumeration value="IB"/>
 *     &lt;enumeration value="IF"/>
 *     &lt;enumeration value="IW"/>
 *     &lt;enumeration value="IS"/>
 *     &lt;enumeration value="IG"/>
 *     &lt;enumeration value="IE"/>
 *     &lt;enumeration value="IX"/>
 *     &lt;enumeration value="IR"/>
 *     &lt;enumeration value="IL"/>
 *     &lt;enumeration value="DD"/>
 *     &lt;enumeration value="DR"/>
 *     &lt;enumeration value="DO"/>
 *     &lt;enumeration value="DM"/>
 *     &lt;enumeration value="DI"/>
 *     &lt;enumeration value="DP"/>
 *     &lt;enumeration value="DC"/>
 *     &lt;enumeration value="ZZ"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ProductTransactionType")
@XmlEnum
public enum ProductTransactionType {

    BP,
    SR,
    IA,
    IP,
    IC,
    ID,
    IM,
    IB,
    IF,
    IW,
    IS,
    IG,
    IE,
    IX,
    IR,
    IL,
    DD,
    DR,
    DO,
    DM,
    DI,
    DP,
    DC,
    ZZ,
    XX;

    public String value() {
        return name();
    }

    public static ProductTransactionType fromValue(String v) {
        return valueOf(v);
    }

}
