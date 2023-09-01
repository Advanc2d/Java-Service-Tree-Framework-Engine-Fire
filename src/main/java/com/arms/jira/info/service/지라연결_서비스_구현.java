package com.arms.jira.info.service;

import com.arms.errors.codes.에러코드;
import com.arms.jira.info.repositories.지라연결_저장소;
import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.model.지라연결정보_엔티티;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service("지라연결_서비스")
public class 지라연결_서비스_구현 implements 지라연결_서비스 {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private 지라연결_저장소 지라연결저장소;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Transactional
    public 지라연결정보_데이터 loadConnectInfo(Long connectId) {
        Optional<지라연결정보_엔티티> optionalEntity = 지라연결저장소.findById(connectId);

        if (!optionalEntity.isPresent()) {
            return null;
        }

        지라연결정보_엔티티 지라연결정보_엔티티 = optionalEntity.get();

        지라연결정보_데이터 지라연결정보_데이터 = modelMapper.map(지라연결정보_엔티티, 지라연결정보_데이터.class);

        return 지라연결정보_데이터;
    }

    @Override
    public List<지라연결정보_데이터> loadConnectInfos() {
        List<지라연결정보_엔티티> jiraInfoEntityList = 지라연결저장소.findAll();
        List<지라연결정보_데이터> result = new ArrayList<>();
        for (지라연결정보_엔티티 지라연결정보_엔티티 : jiraInfoEntityList) {
            result.add(modelMapper.map(지라연결정보_엔티티, 지라연결정보_데이터.class));
        }
        return result;
    }

    public String getIssueTypeId(Long connectId) {

        Optional<지라연결정보_엔티티> optionalEntity = 지라연결저장소.findById(connectId);

        if (!optionalEntity.isPresent()) {
            return null;
        }

        지라연결정보_엔티티 지라연결정보_엔티티 = optionalEntity.get();

        return 지라연결정보_엔티티.getIssueId();
    }

    public 지라연결정보_엔티티 saveConnectInfo(지라연결정보_데이터 지라연결정보_데이터) {

        if (지라연결정보_데이터 == null) {
            throw new IllegalArgumentException("JiraInfo 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(지라연결정보_데이터.getUri())) {
            throw new IllegalArgumentException("JiraInfo의 URI 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(지라연결정보_데이터.getUserId())) {
            throw new IllegalArgumentException("JiraInfo의 사용자 아이디 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(지라연결정보_데이터.getPasswordOrToken())) {
            throw new IllegalArgumentException("JiraInfo의 비밀번호나 토큰 정보가 없습니다.");
        }
        else if (StringUtils.isBlank(지라연결정보_데이터.getType())) {
            throw new IllegalArgumentException("JiraInfo의 타입 정보가 없습니다.");
        }

        지라연결정보_데이터 가져온_지라연결정보_데이터 = loadConnectInfo(지라연결정보_데이터.getConnectId());
        지라연결정보_엔티티 지라연결정보_엔티티;

        if (가져온_지라연결정보_데이터 != null) {
            지라연결정보_엔티티 = modelMapper.map(가져온_지라연결정보_데이터, 지라연결정보_엔티티.class);
        }
        else {
            지라연결정보_엔티티 = modelMapper.map(지라연결정보_데이터, 지라연결정보_엔티티.class);
        }

        return 지라연결저장소.save(지라연결정보_엔티티);
    }

    public 지라연결정보_엔티티 saveIssueTypeInfo(지라연결정보_엔티티 지라연결정보_엔티티) {
        return 지라연결저장소.save(지라연결정보_엔티티);
    }
    
    /*
    *  DB에서 조회 한 후 데이터 오류 처리
    *  임시로 checkInfo 메서드 위치 시킴
    * */
    public 지라연결정보_데이터 checkInfo(Long connectId){

        지라연결정보_데이터 info = loadConnectInfo(connectId);

        if (info == null) {
            logger.info("비정상적인 정보가 조회되었습니다.");
            throw new IllegalArgumentException(에러코드.서버정보_오류.getErrorMsg());
        }

        if(info.getUserId() == null){
            logger.info("사용자 아이디 조회에 실패했습니다.");
            throw new IllegalArgumentException(에러코드.서버정보_오류_아이디.getErrorMsg());
        }

        if(info.getPasswordOrToken()== null){
            logger.info("비밀 번호 및 토큰 정보 조회에 실패했습니다.");
            throw new IllegalArgumentException(에러코드.서버정보_오류_비밀번호.getErrorMsg());
        }
        return info;
    }

}
