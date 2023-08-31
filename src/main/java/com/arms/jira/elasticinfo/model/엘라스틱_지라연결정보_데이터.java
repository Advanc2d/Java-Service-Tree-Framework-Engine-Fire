package com.arms.jira.elasticinfo.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class 엘라스틱_지라연결정보_데이터 {
    private Long connectId;
    private String type;
    private String userId;
    private String passwordOrToken;
    private String uri;
}
