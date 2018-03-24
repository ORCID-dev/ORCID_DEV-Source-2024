package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "deprecatedDate", "primaryRecord"})
@XmlRootElement(name = "deprecated", namespace = "http://www.orcid.org/ns/deprecated")
public class Deprecated implements Serializable {
    private static final long serialVersionUID = 3305981432682691956L;
    
    @XmlElement(namespace = "http://www.orcid.org/ns/deprecated", name = "deprecated-date")
    protected DeprecatedDate deprecatedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/deprecated", name = "primary-record")
    protected PrimaryRecord primaryRecord;
    public DeprecatedDate getDeprecatedDate() {
        return deprecatedDate;
    }
    public void setDeprecatedDate(DeprecatedDate deprecatedDate) {
        this.deprecatedDate = deprecatedDate;
    }
    public PrimaryRecord getPrimaryRecord() {
        return primaryRecord;
    }
    public void setPrimaryRecord(PrimaryRecord primaryRecord) {
        this.primaryRecord = primaryRecord;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deprecatedDate == null) ? 0 : deprecatedDate.hashCode());
        result = prime * result + ((primaryRecord == null) ? 0 : primaryRecord.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Deprecated other = (Deprecated) obj;
        if (deprecatedDate == null) {
            if (other.deprecatedDate != null)
                return false;
        } else if (!deprecatedDate.equals(other.deprecatedDate))
            return false;
        if (primaryRecord == null) {
            if (other.primaryRecord != null)
                return false;
        } else if (!primaryRecord.equals(other.primaryRecord))
            return false;
        return true;
    }        
}
