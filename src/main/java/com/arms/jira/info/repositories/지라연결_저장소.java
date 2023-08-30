package com.arms.jira.info.repositories;

import com.arms.jira.info.model.지라연결정보_엔티티;
import org.springframework.data.jpa.repository.JpaRepository;

public interface 지라연결_저장소 extends JpaRepository<지라연결정보_엔티티, Long> {
}
