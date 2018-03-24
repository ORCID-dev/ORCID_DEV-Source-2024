//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.24 at 04:27:39 PM GMT 
//

package org.orcid.model.notification.institutional_sign_in_rc2;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.notification.permission_rc2.AuthorizationUrl;
import org.orcid.jaxb.model.notification_rc2.Notification;
import org.orcid.jaxb.model.notification_rc2.NotificationType;

public class NotificationInstitutionalConnection extends Notification {

    private final static long serialVersionUID = 1L;
    protected AuthorizationUrl authorizationUrl;
    protected XMLGregorianCalendar actionedDate;
    protected String subject;
    protected String idpName;

    {
        notificationType = NotificationType.INSTITUTIONAL_CONNECTION;
    }

    /**
     * Gets the value of the authorizationUrl property.
     * 
     * @return possible object is {@link AuthorizationUrl }
     * 
     */
    public AuthorizationUrl getAuthorizationUrl() {
        return authorizationUrl;
    }

    /**
     * Sets the value of the authorizationUrl property.
     * 
     * @param value
     *            allowed object is {@link AuthorizationUrl }
     * 
     */
    public void setAuthorizationUrl(AuthorizationUrl value) {
        this.authorizationUrl = value;
    }

    public XMLGregorianCalendar getActionedDate() {
        return actionedDate;
    }

    public void setActionedDate(XMLGregorianCalendar actionedDate) {
        this.actionedDate = actionedDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIdpName() {
        return idpName;
    }

    public void setIdpName(String idpName) {
        this.idpName = idpName;
    }

}
