package com.arms.jira.info.service;

import java.util.List;

import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.model.지라연결정보_엔티티;

public interface 지라연결_서비스 {

    public 지라연결정보_데이터 loadConnectInfo(Long connectId);

	List<지라연결정보_데이터> loadConnectInfos();

	public String getIssueTypeId(Long connectId);

    public 지라연결정보_엔티티 saveConnectInfo(지라연결정보_데이터 지라연결정보_데이터);

    public 지라연결정보_엔티티 saveIssueTypeInfo(지라연결정보_엔티티 지라연결정보_엔티티);

    public 지라연결정보_데이터 checkInfo(Long connectId);
}
