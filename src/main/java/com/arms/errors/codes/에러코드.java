package com.arms.errors.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum 에러코드 {

    서버_아이디_없음("서버 아이디가 없습니다."),

    서버_유형_정보없음("서버 유형 정보가 없습니다."),

    요청한_데이터가_유효하지않음("호출 된 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않습니다"),

    요청본문_오류체크("요청 본문이 없거나 JSON 형식이 아닙니다."), //이슈 생성 및 수정 관련 오류

    AP경로_오류("잘못된 주소로 요청하였습니다."),   // 경로 오류

    연결정보_오류("등록된 연결 정보가 아닙니다."),  //  디비에 저장된 정보가 아닐시
    연결정보_오류_아이디("사용자 아이디 조회에 실패했습니다."),
    연결정보_오류_비밀번호("비밀 번호 및 토큰 정보 조회에 실패했습니다."),

    검색정보_오류("조회 대상 정보가 없습니다."),     //이슈 및 프로젝트 아이디 및 키 조회시 오류


    사용자_정보조회_실패("사용자 정보 조회를 실패하였습니다."),

    프로젝트_조회_오류("프로젝트 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다."),

    이슈_조회_오류("이슈 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다."),
    이슈생성_오류("이슈 생성시 오류 발생하여 이슈 생성에 실패하였습니다."),
    우선순위_조회_오류("우선순위 정보 가져오기에 실패하였습니다."),
    이슈수정_오류("이슈 수정시 오류가 발생하였습니다. 수정 대상 정보 확인이 필요합니다."),

    이슈유형_조회_오류("이슈 유형 정보 가져오기에 실패하였습니다."),

    이슈상태_조회_오류("이슈 상태 정보 가져오기에 실패하였습니다."),

    이슈해결책_조회_오류("이슈 해결책 정보 가져오기에 실패하였습니다.")
    ;

     private final String errorMsg;

     public String getErrorMsg(Object... arg) {
         return String.format(errorMsg, arg);
     }
}
