
package com.arms.serverinfo.service;


import com.arms.errors.codes.에러코드;
import com.arms.serverinfo.model.서버정보_데이터;
import com.arms.serverinfo.model.서버정보_엔티티;
import com.arms.serverinfo.repositories.서버정보_저장소;
import com.arms.serverinfo.service.서버정보_서비스;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j

@Service("서버정보_서비스")
@AllArgsConstructor
public class 서버정보_서비스_구현 implements 서버정보_서비스 {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private 서버정보_저장소 서버정보_저장소;
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Override
    public 서버정보_엔티티 연결정보_저장(서버정보_데이터 서버정보_데이터){
        if (서버정보_데이터 == null) {
            throw new IllegalArgumentException(에러코드.서버정보_오류.getErrorMsg());
        }
        else if (StringUtils.isBlank(서버정보_데이터.getUri())) {
            throw new IllegalArgumentException(에러코드.서버정보_URI_오류.getErrorMsg());
        }
        else if (StringUtils.isBlank(서버정보_데이터.getUserId())) {
            throw new IllegalArgumentException(에러코드.서버정보_ID_오류.getErrorMsg());
        }
        else if (StringUtils.isBlank(서버정보_데이터.getPasswordOrToken())) {
            throw new IllegalArgumentException(에러코드.서버정보_PW_오류.getErrorMsg());
        }
        else if (StringUtils.isBlank(서버정보_데이터.getType())) {
            throw new IllegalArgumentException(에러코드.서버정보_TYPE_오류.getErrorMsg());
        }

        서버정보_데이터 조회한_서버_데이터 = 연결정보_조회(서버정보_데이터.getConnectId());
        서버정보_엔티티 서버정보_엔티티;

        if (조회한_서버_데이터 != null) {
            서버정보_엔티티 = modelMapper.map(조회한_서버_데이터, 서버정보_엔티티.class);
        }
        else {
            서버정보_엔티티 = modelMapper.map(서버정보_데이터, 서버정보_엔티티.class);
        }

        서버정보_엔티티 결과 = 서버정보_저장소.save(서버정보_엔티티);
        if (결과 == null){
            throw new IllegalArgumentException(에러코드.서버정보_생성_오류.getErrorMsg());
        }
        return 결과;
    }
    @Override
    public 서버정보_엔티티 서버정보_삭제하기(서버정보_데이터 서버정보_데이터) {

        서버정보_데이터 이슈 = 연결정보_조회(서버정보_데이터.getConnectId());
        서버정보_엔티티 서버정보 = modelMapper.map(이슈, 서버정보_엔티티.class);
        log.info("왠만하면 쓰지 마시지...");

        if( 이슈 == null ){
            return null;
        }else{
            서버정보_저장소.delete(서버정보);
            return 서버정보;
        }
    }
    @Override
    public void 서버정보_전체_삭제하기(){
        서버정보_저장소.deleteAll();
    }

    public 서버정보_데이터 연결정보_조회(Long 연결대상_아이디) {

        Optional< 서버정보_엔티티 > optionalEntity = Optional.ofNullable(서버정보_저장소.findById(연결대상_아이디).orElse(null));

        if (!optionalEntity.isPresent()) {
            return null;
        }
        서버정보_엔티티 서버정보_엔티티 = optionalEntity.get();
        서버정보_데이터 서버정보_데이터 = modelMapper.map(서버정보_엔티티, 서버정보_데이터.class);
        return 서버정보_데이터;
    }

    public 서버정보_데이터 서버정보_검증(Long 연결대상_아이디){

        서버정보_데이터 조회한_서버정보 = 연결정보_조회(연결대상_아이디);

        if (조회한_서버정보 == null) {
            로그.error("비정상적인 정보가 조회되었습니다.");
            throw new IllegalArgumentException(에러코드.연결정보_오류.getErrorMsg());
        }
        if(조회한_서버정보.getUserId() == null){
            로그.error("사용자 아이디 조회에 실패했습니다.");
            throw new IllegalArgumentException(에러코드.연결정보_오류_아이디.getErrorMsg());
        }
        if(조회한_서버정보.getPasswordOrToken()== null){
            로그.info("비밀 번호 및 토큰 정보 조회에 실패했습니다.");
            throw new IllegalArgumentException(에러코드.연결정보_오류_비밀번호.getErrorMsg());
        }
        return 조회한_서버정보;
    }
}


