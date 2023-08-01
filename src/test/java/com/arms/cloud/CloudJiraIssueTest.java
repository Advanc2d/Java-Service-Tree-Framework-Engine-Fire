package com.arms.cloud;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.arms.cloud.jiraissue.domain.CloudJiraIssueDTO;
import com.arms.cloud.jiraissue.domain.CloudJiraIssueSearchDTO;
import com.arms.cloud.jiraissue.domain.FieldsDTO.IssueLink;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class CloudJiraIssueTest {
    WebClient webClient;

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "ADVANC2D";
    public String issueKeyOrId = projectKeyOrId + "-1";

    @BeforeEach
    void setUp () {
        webClient = WebClient.builder()
                            .baseUrl(baseUrl)
                            .defaultHeader("Authorization", "Basic " + getBase64Credentials(id, pass))
                            .build();
    }

    private String getBase64Credentials(String jiraID, String jiraPass) {
        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    @Test
    @DisplayName("프로젝트 키의 이슈 전체 조회 테스트")
    public void IssueSearchCallTest() {
        String uri = "/rest/api/3/search?jql=project=" + projectKeyOrId;

        CloudJiraIssueSearchDTO issues = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        Assertions.assertThat(issues.getIssues().getClass()).isEqualTo(ArrayList.class);
    }
    
    @Test
    @DisplayName("이슈 상세조회 조회 테스트")
    public void IssueDetailCallTest() {
        CloudJiraIssueDTO issue = issueCall(issueKeyOrId);
        Assertions.assertThat(issue.getSelf()).isEqualTo("https://advanc2d.atlassian.net/rest/api/3/issue/10010");
    }

    @Test
    @DisplayName("이슈 상세 조회시 이슈 링크, 서브테스크 전체 조회 테스트")
    public void IssueLisksSubtaskCallTest() {
        IssueLinkSubtaskDTO result = new IssueLinkSubtaskDTO();

        CloudJiraIssueDTO issue = issueCall(issueKeyOrId);
        result.setNodeIssue(issue);
        List<IssueLink> issueLinks = issue.getFields().getIssuelinks();

        if (issueLinks.size() > 0) {
            List<CloudJiraIssueDTO> inwardIssueList = new ArrayList<CloudJiraIssueDTO>();
            List<CloudJiraIssueDTO> outwardIssueList = new ArrayList<CloudJiraIssueDTO>();

            for(IssueLink issueLink : issueLinks) {

                if (issueLink.getInwardIssue() != null) {
                    CloudJiraIssueDTO inwardIssueLink = issueCall(issueLink.getInwardIssue().getId());
                    inwardIssueList.add(inwardIssueLink);
                }

                if ( issueLink.getOutwardIssue() != null) {
                    CloudJiraIssueDTO inwardIssueLink = issueCall(issueLink.getOutwardIssue().getId());
                    outwardIssueList.add(inwardIssueLink);
                }
            }
            result.setInwardIssues(inwardIssueList);
            result.setOutwardIssues(outwardIssueList);
        }

        List<CloudJiraIssueDTO> subtasks = issue.getFields().getSubtasks();

        if (issue.getFields().getSubtasks().size() > 0) {
            List<CloudJiraIssueDTO> subtaskList = new ArrayList<CloudJiraIssueDTO>();
            for(CloudJiraIssueDTO subtask : subtasks) {
                CloudJiraIssueDTO subIssue = issueCall(subtask.getId());
                subtaskList.add(subIssue);             
            }

            result.setSubtasks(subtaskList);
        }

        Assertions.assertThat(result.getNodeIssue().getSelf()).isEqualTo("https://advanc2d.atlassian.net/rest/api/3/issue/10010");
    }

    public CloudJiraIssueDTO issueCall(String issueIdOrKey) {
        String uri = "/rest/api/3/issue/" + issueIdOrKey;

        CloudJiraIssueDTO issue = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CloudJiraIssueDTO.class).block();

        return issue;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IssueLinkSubtaskDTO {
        CloudJiraIssueDTO nodeIssue;
        List<CloudJiraIssueDTO> inwardIssues;
        List<CloudJiraIssueDTO> outwardIssues;
        List<CloudJiraIssueDTO> subtasks;
    }
}
