package com.arms.jira.cloud.jiraissue.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransitionsDTO {
    private String expand;
    private List<Transitions> transitions;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Transitions {
        private String id;
        private String name;
        private Map<String, Object> to;

        private boolean hasScreen;
        private boolean isGlobal;
        private boolean isInitial;
        private boolean isAvailable;
        private boolean isConditional;
        private boolean isLooped;
    }
}
