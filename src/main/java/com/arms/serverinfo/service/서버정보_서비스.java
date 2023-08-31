package com.arms.serverinfo.service;
import com.arms.serverinfo.model.서버정보_데이터;
import com.arms.serverinfo.model.서버정보_엔티티;
public interface 서버정보_서비스 {
    public 서버정보_엔티티 연결정보_저장(서버정보_데이터 서버정보_데이터);

    public 서버정보_엔티티 서버정보_삭제하기(서버정보_데이터 서버정보_데이터);

    public 서버정보_데이터 서버정보_검증(Long 연결대상_아이디);
}
