package com.atipera.ghapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchDetails {

    private String name;

    private CommitDetails commitDetails;

    @JsonProperty("commit")
    public void setCommit(CommitDetails commitDetails) {
        this.commitDetails = commitDetails;
    }


}
