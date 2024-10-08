package org.orcid.core.utils.v3;

import java.net.URI;
import java.net.URISyntaxException;

import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.springframework.beans.factory.annotation.Value;

public class OrcidIdentifierUtils {

    @Value("${org.orcid.core.baseUri:https://orcid.org}")
    private String baseUri;
    
    public OrcidIdentifier buildOrcidIdentifier(String orcid) {
        OrcidIdentifier identifier = new OrcidIdentifier();
        String correctedBaseUri = baseUri;        
        try {
            URI uri = new URI(correctedBaseUri);
            identifier.setHost(uri.getHost());
        } catch(URISyntaxException e) {
            throw new RuntimeException("Error parsing base uri", e);
        }        
        
        identifier.setPath(orcid);
        identifier.setUri(correctedBaseUri + "/" + orcid);
        return identifier;
    }
}
