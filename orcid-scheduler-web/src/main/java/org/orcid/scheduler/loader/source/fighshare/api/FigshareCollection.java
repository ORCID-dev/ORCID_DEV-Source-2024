package org.orcid.scheduler.loader.source.fighshare.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FigshareCollection {

    @JsonProperty("timeline")
    private FigshareCollectionTimeline timeline;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("timeline")
    public FigshareCollectionTimeline getTimeline() {
        return timeline;
    }

    @JsonProperty("timeline")
    public void setTimeline(FigshareCollectionTimeline timeline) {
        this.timeline = timeline;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

}
