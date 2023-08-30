package com.arms.errors.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum 에러코드 {

    요청한_커넥트아이디_없음("connectId 데이터가 수신되지 않았습니다."), // 파라메터로 connectId 넘어 오지 않았을 경유
    요청한_데이터가_유효하지않음("호출 된 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않습니다"),
    요청본문_오류체크("요청 본문이 없거나 JSON 형식이 아닙니다."),
    AP경로_오류("잘못된 주소로 요청하였습니다."),   // 경로 오류
    연결정보_오류("등록된 연결 정보가 아닙니다."),  //  디비에 저장된 정보가 아닐시
    검색정보_오류("조회 대상 정보가 없습니다."),     //이슈 및 프로젝트 아이디 및 키 조회시 오류
    이슈생성_오류("이슈 생성시 오류 발생하여 이슈 생성에 실패하였습니다."),
    사용자_정보조회_실패("사용자 정보 조회시 조회를 실패 하였습니다.")
    ;  


     private final String errorMsg;

     public String getErrorMsg(Object... arg) {
         return String.format(errorMsg, arg);
     }
}
