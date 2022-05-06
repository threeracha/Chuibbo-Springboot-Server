package com.sriracha.ChuibboServer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class OpenApiJobPost {

    private Jobs jobs;

    @Getter
    public static class Jobs {

        private List<Job> job;

        @Getter
        public static class Job {

            private Long id;
            private String url;
            private Position position;
            private Company company;
            @JsonProperty("opening-timestamp")
            private String openingTimestamp;
            @JsonProperty("expiration-timestamp")
            private String expirationTimestamp;

            @Getter
            public static class Position {

                private String title;
                @JsonProperty("job-mid-code")
                private JobMidCode jobMidCode;
                private Location location;
                @JsonProperty("experience-level")
                private ExperienceLevel experienceLevel;

                @Getter
                public static class JobMidCode {
                    private String code;
                }

                @Getter
                public static class Location {
                    private String code;
                }

                @Getter
                public static class ExperienceLevel {
                    private String code;
                }
            }

            @Getter
            public static class Company {

                private Detail detail;

                @Getter
                public static class Detail {
                    private String href;
                    private String name;
                }
            }
        }
    }
}
