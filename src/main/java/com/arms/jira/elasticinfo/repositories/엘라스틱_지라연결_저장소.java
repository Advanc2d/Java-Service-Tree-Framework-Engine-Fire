package com.arms.jira.elasticinfo.repositories;


import com.arms.jira.elasticinfo.model.엘라스틱_지라연결정보_엔티티;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface 엘라스틱_지라연결_저장소  extends ElasticsearchRepository<엘라스틱_지라연결정보_엔티티, String>{
}
