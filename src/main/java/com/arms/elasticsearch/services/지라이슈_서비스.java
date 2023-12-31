package com.arms.elasticsearch.services;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색조건;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface 지라이슈_서비스 {

    public 지라이슈 이슈_추가하기(지라이슈 지라이슈);

    public int 대량이슈_추가하기(List<지라이슈> 대량이슈_리스트);

    public Iterable<지라이슈> 이슈리스트_추가하기(List<지라이슈> 지라이슈_리스트);

    public 지라이슈 이슈_갱신하기(지라이슈 지라이슈);

    public 지라이슈 이슈_삭제하기(지라이슈 지라이슈);

    public 지라이슈 이슈_조회하기(String 조회조건_아이디);

    public List<지라이슈> 이슈_검색하기(검색조건 검색조건);

    public Map<String, Long> 특정필드의_값들을_그룹화하여_빈도수가져오기(String indexName, String groupByField) throws IOException;

    public List<검색결과> 특정필드_검색후_다른필드_그룹결과(String 인덱스이름, String 특정필드, String 특정필드검색어, String 그룹할필드) throws IOException;

    public 지라이슈 이슈_검색엔진_저장(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long 제품서비스_버전) throws Exception;

    public int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long 제품서비스_버전) throws Exception;

    public List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, 검색조건 검색조건);

    public Map<String,Integer> 요구사항_릴레이션이슈_상태값_전체통계(Long 지라서버_아이디) throws IOException;

    public Map<String, Map<String, Integer>> 요구사항_릴레이션이슈_상태값_프로젝트별통계(Long 지라서버_아이디) throws IOException;

    public Map<String, Long> 제품서비스_버전별_상태값_통계(Long 제품서비스_아이디, Long 버전_아이디) throws IOException;
}
