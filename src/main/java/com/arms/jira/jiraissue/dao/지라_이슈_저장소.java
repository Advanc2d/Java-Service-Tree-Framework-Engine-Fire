package com.arms.jira.jiraissue.dao;

import com.arms.jira.jiraissue.model.지라_이슈_엔티티;
import org.springframework.data.jpa.repository.JpaRepository;

public interface 지라_이슈_저장소 extends JpaRepository<지라_이슈_엔티티, String> {
}
