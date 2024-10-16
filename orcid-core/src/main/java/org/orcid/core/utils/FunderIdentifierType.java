//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.08.17 at 03:04:42 PM BST 
//


package org.orcid.core.utils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
* TODO: Once the jersey migration is over, this could be moved back to the orcid-utils package so it could be reused form the orcid-persistence package
* */
@Deprecated
@XmlType(name = "funderIdentifierType")
@XmlEnum
public enum FunderIdentifierType {

    ISNI("ISNI"),
    GRID("GRID"),
    @XmlEnumValue("Crossref Funder ID")
    CROSSREF_FUNDER_ID("Crossref Funder ID"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    FunderIdentifierType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FunderIdentifierType fromValue(String v) {
        for (FunderIdentifierType c: FunderIdentifierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
