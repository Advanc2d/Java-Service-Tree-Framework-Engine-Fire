package com.engine.jira.cloud.scheduler;

import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueDTO;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueEntity;
import com.arms.jira.cloud.jiraissue.model.CloudJiraIssueSearchDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO;
import com.arms.jira.cloud.jiraissue.model.FieldsDTO.IssueLink;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Stack;

public class CloudIssueSchedulerTest {
    static WebClient webClient;
    public ModelMapper modelMapper = new ModelMapper();

    public String baseUrl = "https://advanc2d.atlassian.net";
    public String id = "gkfn185@gmail.com";
    public String pass = "ATATT3xFfGF0OhyPJU1DlcjJmtsZBXsuXPmet-VBfz07AN6R_vGsV6rOeO6loKVV7iEBsMsmW0WPO4vpPokpcRR_QMrpHi9VJtWdLDLKrhG27j6aGFCeQh5_0sDjWjK45jcJsmQ606vB2Mt9ZYfSAdrRRjlUHceqBiU_Mq7--spJIpAOy7Wi0w4=0122341F";
    public String projectKeyOrId = "PHM";
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

    public static CloudJiraIssueDTO getIssue(String issueIdOrKey) {
        String uri = "/rest/api/3/issue/" + issueIdOrKey;

        CloudJiraIssueDTO issue = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CloudJiraIssueDTO.class).block();

        return issue;
    }

    @Test
    @DisplayName("이슈 타입이 요구사항인 이슈를 전체 조회하고 이슈 링크 내용을 전부 가져오는 스케줄러")
    public void test() {
        CloudJiraIssueSearchDTO issues = getIssueListByIssueTypeName("Requirement");
        List<CloudJiraIssueDTO> cloudJiraIssueList = issues.getIssues();

        List<CloudJiraIssueEntity> allDtos = new ArrayList<>();
        try {
            for (CloudJiraIssueDTO issue : cloudJiraIssueList) {
                List<CloudJiraIssueEntity> issueLinkDTOs = findAllLinkedDtos(issue, new ArrayList<>(), null, null);

                allDtos.addAll(issueLinkDTOs);
                // printLinkedIssues(issueLinkDTO, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("allDtos = " + allDtos.size());
        for (CloudJiraIssueEntity dto : allDtos) {
            System.out.println(dto.getKey() +"/"+ dto.getOutwardId() +"/"+ dto.getParentId());
        }
    }

    public List<CloudJiraIssueEntity> findAllLinkedDtos(CloudJiraIssueDTO dto, List<CloudJiraIssueEntity> allDtos,
                                                               String outwardId, String parentId) {
        // 현재 DTO의 하위에 연결된 DTO들을 allDtos에 추가
        CloudJiraIssueEntity entity = modelMapper.map(dto, CloudJiraIssueEntity.class);

        if (outwardId != null) {
            entity.setOutwardId(outwardId);
        }

        if(parentId != null) {
            entity.setParentId(parentId);
        }

        allDtos.add(entity);
        System.out.println("dto.getKey() = " + dto.getKey());
        // 현재 DTO와 연결된 모든 하위 DTO를 탐색
        if (dto.getFields().getIssuelinks() != null) {
                for (IssueLink issueLink : dto.getFields().getIssuelinks()) {
                    if (issueLink.getInwardIssue() == null) {
                        continue;
                    }

                System.out.println("issueLink.getInwardIssue().getKey() = " + issueLink.getInwardIssue().getKey());
                CloudJiraIssueDTO linkedDto = getIssue(issueLink.getInwardIssue().getKey());
                findAllLinkedDtos(linkedDto, allDtos, dto.getId(), null);
            }
        }

        if (dto.getFields().getSubtasks() != null ) {
            for (CloudJiraIssueDTO subtask : dto.getFields().getSubtasks()) {
                System.out.println("subtask.getKey() = " + subtask.getKey());
                CloudJiraIssueDTO subtaskDTO = getIssue(subtask.getKey());
                findAllLinkedDtos(subtaskDTO, allDtos, null, dto.getId());
            }
        }

        return allDtos;
    }

    public static List<CloudJiraIssueDTO> findAllLinkedDtosUsingDFS(CloudJiraIssueDTO root) {
        List<CloudJiraIssueDTO> allDtos = new ArrayList<>();
        Stack<CloudJiraIssueDTO> stack = new Stack<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            CloudJiraIssueDTO currentDto = stack.pop();
            allDtos.add(currentDto);

            for (FieldsDTO.IssueLink issueLink : currentDto.getFields().getIssuelinks()) {
                CloudJiraIssueDTO linkedDto = getIssue(issueLink.getInwardIssue().getId());
                stack.push(linkedDto);
            }
        }

        return allDtos;
    }

    public CloudJiraIssueSearchDTO getIssueListByIssueTypeName(String issueTypName) {
        String uri = "/rest/api/3/search?jql=issuetype=" + issueTypName;

        CloudJiraIssueSearchDTO issues = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CloudJiraIssueSearchDTO.class).block();

        return issues;
    }
}