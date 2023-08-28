package com.arms.jira.onpremise.jiraproject.service;

import com.arms.jira.onpremise.jiraproject.model.OnPremiseJiraProjectDTO;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.arms.jira.info.model.JiraInfoDTO;
import com.arms.jira.info.service.지라연결_서비스;
import com.arms.jira.onpremise.OnPremiseJiraUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service("onPremiseJiraProject")
public class OnPremiseOnPremiseJiraProjectImpl implements OnPremiseJiraProject {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @Override
    public List<OnPremiseJiraProjectDTO> getProjectList(Long connectId) throws Exception {

        JiraInfoDTO info = 지라연결_서비스.checkInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                                                                info.getUserId(),
                                                                info.getPasswordOrToken());

        Iterable<BasicProject> allProject = restClient.getProjectClient().getAllProjects().claim();
        List<OnPremiseJiraProjectDTO> projectList = new ArrayList<>();

        for (BasicProject project : allProject) {

            OnPremiseJiraProjectDTO jiraProject = new OnPremiseJiraProjectDTO();
            jiraProject.setSelf(project.getSelf().toString());
            jiraProject.setId(project.getId().toString());
            jiraProject.setKey(project.getKey());
            jiraProject.setName(project.getName());

            projectList.add(jiraProject);
        }

        return projectList;
    }

    @Override
    public OnPremiseJiraProjectDTO getProject(Long connectId, String projectKey) throws Exception {
        JiraInfoDTO info = 지라연결_서비스.checkInfo(connectId);
        JiraRestClient restClient = OnPremiseJiraUtils.getJiraRestClient(info.getUri(),
                                                                info.getUserId(),
                                                                info.getPasswordOrToken());

        BasicProject project = restClient.getProjectClient().getProject(projectKey).claim();

        OnPremiseJiraProjectDTO onPremiseJiraProjectDTO = new OnPremiseJiraProjectDTO();
        onPremiseJiraProjectDTO.setSelf(project.getSelf().toString());
        onPremiseJiraProjectDTO.setId(project.getId().toString());
        onPremiseJiraProjectDTO.setKey(project.getKey());
        onPremiseJiraProjectDTO.setName(project.getName());

        return onPremiseJiraProjectDTO;
    }

}