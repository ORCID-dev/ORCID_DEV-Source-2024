//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.23 at 12:45:35 PM BST 
//

package org.orcid.jaxb.model.clientgroup;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for client-type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="client-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="creator"/>
 *     &lt;enumeration value="premium-creator"/>
 *     &lt;enumeration value="updater"/>
 *     &lt;enumeration value="premium-updater"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "redirect-uri-type")
@XmlEnum
public enum RedirectUriType {

    //@formatter:off
    @XmlEnumValue("default")
    DEFAULT("default"),

    @XmlEnumValue("grant-read-wizard")
    GRANT_READ_WIZARD("grant-read-wizard"),

    @XmlEnumValue("import-funding-wizard")
    IMPORT_FUNDING_WIZARD("import-funding-wizard"),
    
    @XmlEnumValue("import-works-wizard")
    IMPORT_WORKS_WIZARD("import-works-wizard"),
    
    @XmlEnumValue("import-peer-review-wizard")
    IMPORT_PEER_REVIEW_WIZARD("import-peer-review-wizard"),
    
    @XmlEnumValue("sso-authentication")
    SSO_AUTHENTICATION("sso-authentication"),
    
    @XmlEnumValue("institutional-sign-in")
    INSTITUTIONAL_SIGN_IN("institutional-sign-in");
        
    //@formatter:on

    private final String value;

    RedirectUriType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RedirectUriType fromValue(String v) {
        for (RedirectUriType c : RedirectUriType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
