/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.record_rc3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc3.LastModifiedDate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "address" })
@XmlRootElement(name = "addresses", namespace = "http://www.orcid.org/ns/address")
public class Addresses implements Serializable {
    private static final long serialVersionUID = -128015751933210030L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "address", namespace = "http://www.orcid.org/ns/address")
    List<Address> address;
    @XmlAttribute
    protected String path;

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
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
        Addresses other = (Addresses) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }
    
    public void updateIndexingStatusOnChilds() {
        if (this.getAddress() != null && !this.getAddress().isEmpty()) {
            List<Address> sorted = new ArrayList<Address>();
            List<Address> unsorted = new ArrayList<Address>();
            Long maxDisplayIndex = 0L;
            for(Address a : this.getAddress()) {
                if(a.getDisplayIndex() == null || Long.valueOf(-1).equals(a.getDisplayIndex())) {
                    unsorted.add(a);
                } else {
                    if(a.getDisplayIndex() > maxDisplayIndex) {
                        maxDisplayIndex = a.getDisplayIndex();
                    }
                    sorted.add(a);
                }                
            }      
            
            if(!unsorted.isEmpty()) {
                Collections.sort(unsorted);
                for(Address a : unsorted) {
                    a.setDisplayIndex((maxDisplayIndex++) + 1);
                    sorted.add(a);
                }
            }
        }
    }
}
