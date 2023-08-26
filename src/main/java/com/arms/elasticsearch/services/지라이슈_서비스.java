package com.arms.elasticsearch.services;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.util.SearchDTO;

import java.util.List;

public interface 지라이슈_서비스 {

    public 지라이슈 이슈_추가하기(지라이슈 지라이슈);

    public 지라이슈 이슈_갱신하기(지라이슈 지라이슈);

    public 지라이슈 이슈_삭제하기(지라이슈 지라이슈);

    public 지라이슈 이슈_조회하기(String 조회조건_아이디);

    public List<지라이슈> 이슈_검색하기(SearchDTO 검색조건);

}
