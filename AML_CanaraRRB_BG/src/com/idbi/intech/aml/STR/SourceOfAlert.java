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
 * <p>Java class for SourceOfAlert.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SourceOfAlert">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CV"/>
 *     &lt;enumeration value="WL"/>
 *     &lt;enumeration value="TY"/>
 *     &lt;enumeration value="TM"/>
 *     &lt;enumeration value="RM"/>
 *     &lt;enumeration value="MR"/>
 *     &lt;enumeration value="LQ"/>
 *     &lt;enumeration value="EI"/>
 *     &lt;enumeration value="PC"/>
 *     &lt;enumeration value="BA"/>
 *     &lt;enumeration value="ZZ"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SourceOfAlert")
@XmlEnum
public enum SourceOfAlert {

    CV,
    WL,
    TY,
    TM,
    RM,
    MR,
    LQ,
    EI,
    PC,
    BA,
    ZZ,
    XX;

    public String value() {
        return name();
    }

    public static SourceOfAlert fromValue(String v) {
        return valueOf(v);
    }

}
