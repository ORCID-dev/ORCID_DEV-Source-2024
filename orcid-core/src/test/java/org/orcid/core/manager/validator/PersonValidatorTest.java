package org.orcid.core.manager.validator;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.PutCodeRequiredException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class PersonValidatorTest {

    @Test
    public void validateExternalIdentifierTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateExternalIdentifier_invalidTypeTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setType(null);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);
        fail();
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateExternalIdentifier_invalidValueTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setValue(null);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);
        fail();
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateExternalIdentifier_invalidRelationship1_flagOnTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setRelationship(null);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);
        fail();
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateExternalIdentifier_invalidRelationship2_flagOnTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setRelationship(Relationship.PART_OF);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);
        fail();
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateExternalIdentifier_invalidUrl_emptyUrlTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setUrl(new Url());
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);        
        fail();
    }
    
    @Test(expected = OrcidValidationException.class)
    public void validateExternalIdentifier_invalidUrl_nullUrlTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setUrl(null);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);        
        fail();
    }
    
    @Test(expected = InvalidPutCodeException.class)
    public void validateExternalIdentifier_invalidPutCodeOnCreateTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setPutCode(1L);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), true, true, Visibility.PUBLIC);        
        fail();
    }
    
    @Test(expected = PutCodeRequiredException.class)
    public void validateExternalIdentifier_invalidPutCodeOnUpdateTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setPutCode(null);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), false, true, Visibility.PUBLIC);        
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void validateExternalIdentifier_visibilityDoesntChangeTest() {
        PersonExternalIdentifier extId = getPersonExternalIdentifier();
        extId.setPutCode(1L);
        extId.setVisibility(Visibility.LIMITED);
        PersonValidator.validateExternalIdentifier(extId, getSourceEntity(), false, true, Visibility.PUBLIC);        
        fail();
    }
            
    private PersonExternalIdentifier getPersonExternalIdentifier() {
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://test.orcid.org"));
        extId.setValue("extId1");
        extId.setVisibility(Visibility.PUBLIC);
        return extId;
    }
    
    private SourceEntity getSourceEntity() {
        SourceEntity source = mock(SourceEntity.class);
        when(source.getCachedSourceName()).thenReturn("source name");
        return source;
    }
}
