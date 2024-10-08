package org.orcid.listener.s3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid20Manager;
import org.orcid.listener.persistence.managers.Api20RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Core logic for listeners
 * 
 * @author tom
 *
 */
@Component
public class S3MessageProcessorAPIV2 {

    public static final String VND_ORCID_XML = "application/vnd.orcid+xml";
    public static final String VND_ORCID_JSON = "application/vnd.orcid+json";

    Logger LOG = LoggerFactory.getLogger(S3MessageProcessorAPIV2.class);

    @Value("${org.orcid.messaging.v2_indexing.enabled:false}")
    private boolean isV2IndexingEnabled;
    
    @Resource
    private Orcid20Manager orcid20ApiClient;
    @Resource
    private S3Manager s3Manager;
    @Resource
    private Api20RecordStatusManager api20RecordStatusManager;

    public void update(String orcid) {
        Boolean isSummaryOk = false;
        List<ActivityType> failedElements = new ArrayList<ActivityType>();
        if (isV2IndexingEnabled) {
            Record record = null;
            try {
                record = fetchPublicRecordAndClearIfNeeded(orcid);
            } catch (Exception e) {
                LOG.error("Unable to fetch public record for " + orcid, e);
                api20RecordStatusManager.allFailed(orcid);
            }

            if (record != null) {
                isSummaryOk = updateSummary(record);
                updateActivities(record, failedElements);
                api20RecordStatusManager.save(orcid, isSummaryOk, failedElements);
            }
        }
    }

    public void retry(String orcid, Boolean retrySummary, List<ActivityType> retryList) {
        if(!isV2IndexingEnabled) {
            return;
        }
        Record record = null;
        try {
            record = fetchPublicRecordAndClearIfNeeded(orcid);
        } catch (Exception e) {
            LOG.error("Unable to fetch public record for " + orcid, e);
            api20RecordStatusManager.allFailed(orcid);
        }
        
        if(record == null) {
            return;
        }
        
        if(retrySummary) {
            if(!updateSummary(record)) {
                api20RecordStatusManager.setSummaryFail(orcid);
            }
        }
        
        ActivitiesSummary as = record.getActivitiesSummary();
        Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V2);
        
        if (retryList.contains(ActivityType.EDUCATIONS)) {
            if(!processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.EDUCATIONS);
            }
        }

        if (retryList.contains(ActivityType.EMPLOYMENTS)) {
            if(!processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.EMPLOYMENTS);
            }
        }

        if (retryList.contains(ActivityType.FUNDINGS)) {
            if(!processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.FUNDINGS);
            }
        }

        if (retryList.contains(ActivityType.PEER_REVIEWS)) {
            if(!processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.PEER_REVIEWS);
            }
        }

        if (retryList.contains(ActivityType.WORKS)) {
            if(!processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.WORKS);
            }
        }
    }

    private boolean updateSummary(Record record) {
        if (record == null || !isV2IndexingEnabled) {
            return false;
        }
        String orcid = record.getOrcidIdentifier().getPath();
        LOG.info("Processing summary for record " + orcid);
        try {
            // Index only if it is claimed
            if (record.getHistory() != null && record.getHistory().getClaimed() != null) {
                if (record.getHistory().getClaimed() == true) {
                    s3Manager.uploadV2RecordSummary(orcid, record);
                } else {
                    LOG.warn(orcid + " is unclaimed, so, it will not be indexed");
                }
            }
            return true;
        } catch (AmazonClientException e) {
            LOG.error("Unable to fetch record " + orcid + " for 2.0 API: " + e.getMessage(), e);
        } catch (Exception e) {
            // Something else went wrong fetching record from ORCID
            LOG.error("Unable to fetch record " + orcid + " for 2.0 API: " + e.getMessage(), e);
        }

        // Return false as the Record or OrcidError couldn't be feed
        return false;
    }

    /**
     * 
     * Activities indexing
     * 
     */
    private void updateActivities(Record record, List<ActivityType> failedElements) {
        if (record == null || !isV2IndexingEnabled) {
            return;
        }

        String orcid = record.getOrcidIdentifier().getPath();
        LOG.info("Processing activities for record " + orcid);
        if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == true) {
            if (record.getActivitiesSummary() != null) {
                ActivitiesSummary as = record.getActivitiesSummary();
                Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V2);
                if (!processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS))) {
                    failedElements.add(ActivityType.EDUCATIONS);
                }
                if (!processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS))) {
                    failedElements.add(ActivityType.EMPLOYMENTS);
                }
                if (!processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS))) {
                    failedElements.add(ActivityType.FUNDINGS);
                }
                if (!processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS))) {
                    failedElements.add(ActivityType.PEER_REVIEWS);
                }
                if (!processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS))) {
                    failedElements.add(ActivityType.WORKS);
                }
            }
        } else if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == false) {
            LOG.warn(record.getOrcidIdentifier().getPath() + " is unclaimed, so, his activities would not be indexed");
        }
    }   

    private boolean processEducations(String orcid, Educations educations, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Educations for record " + orcid);
            if (educations != null && !educations.getSummaries().isEmpty()) {
                List<EducationSummary> all = new ArrayList<EducationSummary>();
                educations.getSummaries().forEach(g -> {
                    all.add(g);
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.EDUCATIONS)) {
                    return false;
                }
            } else {
                return s3Manager.clearV2ActivitiesByType(orcid, ActivityType.EDUCATIONS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Educations for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processEmployments(String orcid, Employments employments, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Employments for record " + orcid);
            if (employments != null && !employments.getSummaries().isEmpty()) {
                List<EmploymentSummary> all = new ArrayList<EmploymentSummary>();
                employments.getSummaries().forEach(g -> {
                    all.add(g);
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.EMPLOYMENTS)) {
                    return false;
                }
            } else {
                return s3Manager.clearV2ActivitiesByType(orcid, ActivityType.EMPLOYMENTS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Employments for record " + orcid, e);
            return false;
        }
        return true;
    }

    
    private boolean processFundings(String orcid, Fundings fundingsElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Fundings for record " + orcid);
            if (fundingsElement != null && !fundingsElement.getFundingGroup().isEmpty()) {
                List<FundingSummary> fundings = new ArrayList<FundingSummary>();
                for (FundingGroup g : fundingsElement.getFundingGroup()) {
                    fundings.addAll(g.getFundingSummary());
                }
                if(!processActivities(orcid, fundings, existingElements, ActivityType.FUNDINGS)) {
                    return false;
                }
            } else {
                return s3Manager.clearV2ActivitiesByType(orcid, ActivityType.FUNDINGS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Fundings for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processPeerReviews(String orcid, PeerReviews peerReviewsElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing PeerReviews for record " + orcid);
            if (peerReviewsElement != null && !peerReviewsElement.getPeerReviewGroup().isEmpty()) {
                List<PeerReviewSummary> peerReviews = new ArrayList<PeerReviewSummary>();
                for (PeerReviewGroup g : peerReviewsElement.getPeerReviewGroup()) {
                    for (PeerReviewSummary ps : g.getPeerReviewSummary()) {
                        peerReviews.add(ps);
                    }
                }
                if(!processActivities(orcid, peerReviews, existingElements, ActivityType.PEER_REVIEWS)) {
                    return false;
                }
            } else {
                return s3Manager.clearV2ActivitiesByType(orcid, ActivityType.PEER_REVIEWS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Peer Reviews for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processWorks(String orcid, Works worksElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Works for record " + orcid);
            if (worksElement != null && !worksElement.getWorkGroup().isEmpty()) {
                List<WorkSummary> works = new ArrayList<WorkSummary>();
                for (WorkGroup g : worksElement.getWorkGroup()) {
                    works.addAll(g.getWorkSummary());
                }
                if(!processActivities(orcid, works, existingElements, ActivityType.WORKS)) {
                    return false;
                }
            } else {
                return s3Manager.clearV2ActivitiesByType(orcid, ActivityType.WORKS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Works for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processActivities(String orcid, List<? extends Activity> activities, Map<String, S3ObjectSummary> existingElements, ActivityType type) {
        try {
            for (Activity x : activities) {
                processActivity(orcid, x, existingElements, type);
            }
            // Remove from S3 all element that still exists on the
            // existingEducations map
            boolean anyFailed = false;
            for (String putCode : existingElements.keySet()) {
                boolean removed = s3Manager.removeV2Activity(orcid, putCode, type);
                if(!removed) {
                    // If any element failed to be removed, alert
                    anyFailed = true;
                }
            }

            // If nothing failed, then all activities were properly removed
            return !anyFailed;
        } catch (Exception e) {
            LOG.error("Unable to fetch activities " + type.getValue() + " for orcid " + orcid, e);
        }
        return false;
    }

    private void processActivity(String orcid, Activity activityFromSummary, Map<String, S3ObjectSummary> existingElements, ActivityType type)
            throws AmazonClientException, AmazonServiceException, JAXBException, IOException, InterruptedException {
        Long summaryPutCode = activityFromSummary.getPutCode();
        String summaryPutCodeString = String.valueOf(summaryPutCode);
        Date summaryLastModified = DateUtils.convertToDate(activityFromSummary.getLastModifiedDate().getValue());
        byte [] activity = null;
        if (existingElements.containsKey(summaryPutCodeString)) {
            S3ObjectSummary existingObject = existingElements.get(summaryPutCodeString);
            Date s3LastModified = existingObject.getLastModified();
            if (summaryLastModified.after(s3LastModified)) {
                activity = fetchActivity(orcid, summaryPutCode, type);
            }
            // Remove it from the existingElements list since it was
            // already processed
            existingElements.remove(summaryPutCodeString);
        } else {
            activity = fetchActivity(orcid, summaryPutCode, type);
        }

        if (activity != null) {
            // Upload it to S3
            s3Manager.uploadV2Activity(orcid, summaryPutCodeString, type, summaryLastModified, activity);
            // Remove it from the existingElements list means that the
            // elements was already processed
            existingElements.remove(summaryPutCodeString);
        }
    }

    private byte [] fetchActivity(String orcid, Long putCode, ActivityType type) throws IOException, InterruptedException {
        switch (type) {
        case EDUCATIONS:
            return orcid20ApiClient.fetchActivity(orcid, putCode, "education");
        case EMPLOYMENTS:
            return orcid20ApiClient.fetchActivity(orcid, putCode, "employment");
        case FUNDINGS:
            return orcid20ApiClient.fetchActivity(orcid, putCode, "funding");
        case PEER_REVIEWS:
            return orcid20ApiClient.fetchActivity(orcid, putCode, "peer-review");
        case WORKS:
            return orcid20ApiClient.fetchActivity(orcid, putCode, "work");
        default:
            throw new IllegalArgumentException("Invalid type! Imposible: " + type);
        }
    }

    /**
     * Fetch the public record of the given orcid id, however, if that record is
     * locked or deprecated, it will clear the summary and all its related
     * activities
     * 
     * @param message
     * @return Record element of a valid user or null if the Record is locked or
     *         deprecated
     * @throws Exception
     *             when the record is locked or deprecated but its data couldn't
     *             be cleared in S3 or, when the record ca't be fetched from the
     *             ORCID API
     */
    private Record fetchPublicRecordAndClearIfNeeded(String orcid) throws AmazonClientException, AmazonServiceException, Exception {
        try {
            return orcid20ApiClient.fetchPublicRecord(orcid);
        } catch (LockedRecordException | DeprecatedRecordException e) {
            // Remove all activities from this record
            boolean allCleared = s3Manager.clearV2Activities(orcid);
            // Remove the summary
            OrcidError error = null;
            if (e instanceof LockedRecordException) {
                LOG.error("Record " + orcid + " is locked");
                error = ((LockedRecordException) e).getOrcidError();
            } else {
                LOG.error("Record " + orcid + " is deprecated");
                error = ((DeprecatedRecordException) e).getOrcidError();
            }

            try {
                // Upload deprecated/locked record file
                s3Manager.uploadV2OrcidError(orcid, error);
            } catch (Exception e1) {
                LOG.error("Record " + orcid + " is locked or deprecated, however, S3 couldn't be updated", e1);
                throw new Exception(e1);
            }
            if(allCleared) {
                api20RecordStatusManager.save(orcid, true, new ArrayList<ActivityType>());
            } else {
                // Mark all activities as failed so we try to clear everything again
                api20RecordStatusManager.save(orcid, true, List.of(ActivityType.EDUCATIONS, ActivityType.EMPLOYMENTS, ActivityType.FUNDINGS, ActivityType.PEER_REVIEWS, ActivityType.WORKS));
            }
            
            return null;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
