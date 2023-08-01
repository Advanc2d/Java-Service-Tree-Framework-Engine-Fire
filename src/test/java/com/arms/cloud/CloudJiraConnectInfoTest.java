package com.arms.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.arms.cloud.jiraconnectinfo.dao.CloudJiraConnectInfoJpaRepository;
import com.arms.cloud.jiraconnectinfo.domain.CloudJiraConnectInfoEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CloudJiraConnectInfoTest {

    @Autowired
    private CloudJiraConnectInfoJpaRepository cloudJiraConnectInfo;

    @Test
    @DisplayName("Cloud Jira Connect Info Load Test")
    public void cloudJiraConnectInfoGetTest() {

        CloudJiraConnectInfoEntity found = cloudJiraConnectInfo.findById("gkfn185@gmail.com").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUrl()).isEqualTo("https://advanc2d.atlassian.net");
        assertThat(found.getToken()).isNotEqualTo("AAAAA");
    }
}
