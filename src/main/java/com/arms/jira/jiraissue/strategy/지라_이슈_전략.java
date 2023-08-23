package com.arms.jira.jiraissue.strategy;

import com.arms.jira.jiraissue.model.지라_이슈_데이터_전송_객체;
import com.arms.jira.jiraissue.model.지라_이슈_생성_데이터_전송_객체;

import java.util.List;
import java.util.Map;

public interface 지라_이슈_전략 {

    List<지라_이슈_데이터_전송_객체> 이슈_전체_목록_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디);

    지라_이슈_데이터_전송_객체 이슈_상세정보_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디);

    지라_이슈_데이터_전송_객체 이슈_생성하기(Long 연결_아이디,
                                        지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) throws Exception;

    Map<String,Object> 이슈_수정하기(Long 연결_아이디, String 이슈_키_또는_아이디,
                                    지라_이슈_생성_데이터_전송_객체 지라_이슈_생성_데이터_전송_객체) throws Exception;

    Map<String,Object> 이슈_삭제_라벨_처리하기(Long 연결_아이디, String 이슈_키_또는_아이디) throws Exception;

    Map<String,Object> 이슈_연결_링크_및_서브테스크_가져오기(Long 연결_아이디, String 이슈_키_또는_아이디);

}
