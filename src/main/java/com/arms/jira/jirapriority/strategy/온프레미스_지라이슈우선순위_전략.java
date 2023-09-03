package com.arms.jira.jirapriority.strategy;

import com.arms.errors.codes.에러코드;
import com.arms.jira.jirapriority.model.지라이슈우선순위_데이터;
import com.arms.jira.utils.지라유틸;
import com.arms.serverinfo.model.서버정보_데이터;
import com.arms.serverinfo.service.서버정보_서비스;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class 온프레미스_지라이슈우선순위_전략 implements 지라이슈우선순위_전략 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 서버정보_서비스 서버정보_서비스;
    @Override
    public List<지라이슈우선순위_데이터> 우선순위_전체_목록_가져오기(Long 연결_아이디) throws Exception {

        로그.info("온프레미스 지라 이슈 우선순위 전체 목록 가져오기");
        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

        try {
            서버정보_데이터 서버정보 = 서버정보_서비스.서버정보_검증(연결_아이디);
            JiraRestClient restClient = 지라유틸.온프레미스_통신기_생성(서버정보.getUri(),
                                                                             서버정보.getUserId(),
                                                                             서버정보.getPasswordOrToken());

            Iterable<Priority> 모든_지라이슈우선순위 = restClient.getMetadataClient().getPriorities().claim();

            List<지라이슈우선순위_데이터> 반환할_지라이슈우선순위_데이터_목록 = new ArrayList<>();

            for (Priority priority : 모든_지라이슈우선순위) {

                지라이슈우선순위_데이터 온프레미스_지라이슈우선순위_데이터 = new 지라이슈우선순위_데이터();
                온프레미스_지라이슈우선순위_데이터.setSelf(priority.getSelf().toString());
                온프레미스_지라이슈우선순위_데이터.setId(priority.getId().toString());
                온프레미스_지라이슈우선순위_데이터.setName(priority.getName());
                온프레미스_지라이슈우선순위_데이터.setDescription(priority.getDescription());

                반환할_지라이슈우선순위_데이터_목록.add(온프레미스_지라이슈우선순위_데이터);
            }

//            List<지라이슈우선순위_데이터> 반환할_지라이슈우선순위_데이터_목록 = StreamSupport.stream(모든_지라이슈우선순위.spliterator(), false)
//                    .map(priority -> {
//                        지라이슈우선순위_데이터 온프레미스_지라이슈우선순위_데이터 = new 지라이슈우선순위_데이터();
//                        온프레미스_지라이슈우선순위_데이터.setSelf(priority.getSelf().toString());
//                        온프레미스_지라이슈우선순위_데이터.setId(priority.getId().toString());
//                        온프레미스_지라이슈우선순위_데이터.setName(priority.getName());
//                        온프레미스_지라이슈우선순위_데이터.setDescription(priority.getDescription());
//
//                        return 온프레미스_지라이슈우선순위_데이터;
//                    })
//                    .collect(Collectors.toList());

            //실험할 코드 추가

            long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
            long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
            System.out.println("시간차이(m) : "+secDiffTime);
            return 반환할_지라이슈우선순위_데이터_목록;

        } catch (Exception e) {
            로그.error("온프레미스 지라 이슈 우선순위 전체 목록 가져오기에 실패하였습니다." + e.getMessage());
            throw new IllegalArgumentException(에러코드.우선순위_조회_오류.getErrorMsg());
        }
    }

}
