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
 * <p>Java class for ReportingEntityCategory.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReportingEntityCategory">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BAPUB"/>
 *     &lt;enumeration value="BAPVT"/>
 *     &lt;enumeration value="BAFOR"/>
 *     &lt;enumeration value="BARRB"/>
 *     &lt;enumeration value="BALAB"/>
 *     &lt;enumeration value="BASUC"/>
 *     &lt;enumeration value="BANUC"/>
 *     &lt;enumeration value="BASCO"/>
 *     &lt;enumeration value="BADCB"/>
 *     &lt;enumeration value="BAOTH"/>
 *     &lt;enumeration value="FIINL"/>
 *     &lt;enumeration value="FIINN"/>
 *     &lt;enumeration value="FIHFC"/>
 *     &lt;enumeration value="FIAD1"/>
 *     &lt;enumeration value="FIAD2"/>
 *     &lt;enumeration value="FIAD3"/>
 *     &lt;enumeration value="FIFFM"/>
 *     &lt;enumeration value="FIMTP"/>
 *     &lt;enumeration value="FIMTA"/>
 *     &lt;enumeration value="FICSO"/>
 *     &lt;enumeration value="FICCP"/>
 *     &lt;enumeration value="FIAFI"/>
 *     &lt;enumeration value="FIHPC"/>
 *     &lt;enumeration value="FICFC"/>
 *     &lt;enumeration value="FINBA"/>
 *     &lt;enumeration value="FINBN"/>
 *     &lt;enumeration value="FIOTH"/>
 *     &lt;enumeration value="CASIN"/>
 *     &lt;enumeration value="INCOL"/>
 *     &lt;enumeration value="INDEP"/>
 *     &lt;enumeration value="INDPP"/>
 *     &lt;enumeration value="INBRO"/>
 *     &lt;enumeration value="INBDS"/>
 *     &lt;enumeration value="INSTA"/>
 *     &lt;enumeration value="INRTA"/>
 *     &lt;enumeration value="INMER"/>
 *     &lt;enumeration value="INUND"/>
 *     &lt;enumeration value="INBAN"/>
 *     &lt;enumeration value="INREG"/>
 *     &lt;enumeration value="INPOM"/>
 *     &lt;enumeration value="INADV"/>
 *     &lt;enumeration value="INTRU"/>
 *     &lt;enumeration value="INCRE"/>
 *     &lt;enumeration value="INVCD"/>
 *     &lt;enumeration value="INCUS"/>
 *     &lt;enumeration value="INFII"/>
 *     &lt;enumeration value="INVCF"/>
 *     &lt;enumeration value="INCOM"/>
 *     &lt;enumeration value="INSBR"/>
 *     &lt;enumeration value="INOTH"/>
 *     &lt;enumeration value="RGRBI"/>
 *     &lt;enumeration value="ZZZZZ"/>
 *     &lt;enumeration value="XXXXX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReportingEntityCategory")
@XmlEnum
public enum ReportingEntityCategory {

    BAPUB("BAPUB"),
    BAPVT("BAPVT"),
    BAFOR("BAFOR"),
    BARRB("BARRB"),
    BALAB("BALAB"),
    BASUC("BASUC"),
    BANUC("BANUC"),
    BASCO("BASCO"),
    BADCB("BADCB"),
    BAOTH("BAOTH"),
    FIINL("FIINL"),
    FIINN("FIINN"),
    FIHFC("FIHFC"),
    @XmlEnumValue("FIAD1")
    FIAD_1("FIAD1"),
    @XmlEnumValue("FIAD2")
    FIAD_2("FIAD2"),
    @XmlEnumValue("FIAD3")
    FIAD_3("FIAD3"),
    FIFFM("FIFFM"),
    FIMTP("FIMTP"),
    FIMTA("FIMTA"),
    FICSO("FICSO"),
    FICCP("FICCP"),
    FIAFI("FIAFI"),
    FIHPC("FIHPC"),
    FICFC("FICFC"),
    FINBA("FINBA"),
    FINBN("FINBN"),
    FIOTH("FIOTH"),
    CASIN("CASIN"),
    INCOL("INCOL"),
    INDEP("INDEP"),
    INDPP("INDPP"),
    INBRO("INBRO"),
    INBDS("INBDS"),
    INSTA("INSTA"),
    INRTA("INRTA"),
    INMER("INMER"),
    INUND("INUND"),
    INBAN("INBAN"),
    INREG("INREG"),
    INPOM("INPOM"),
    INADV("INADV"),
    INTRU("INTRU"),
    INCRE("INCRE"),
    INVCD("INVCD"),
    INCUS("INCUS"),
    INFII("INFII"),
    INVCF("INVCF"),
    INCOM("INCOM"),
    INSBR("INSBR"),
    INOTH("INOTH"),
    RGRBI("RGRBI"),
    ZZZZZ("ZZZZZ"),
    XXXXX("XXXXX");
    private final String value;

    ReportingEntityCategory(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReportingEntityCategory fromValue(String v) {
        for (ReportingEntityCategory c: ReportingEntityCategory.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
