package io.ihankun.framework.spring.server.nacos.properties;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DatasetDbUserInfo {
    
    private String envName;

    private List<HospitalDTO> hospital;

    @NoArgsConstructor
    @Data
    public static class HospitalDTO {

        private String hospitalName;

        private Long hospitalId;

        private Long orgId;

        private List<DatabaseDTO> database;

    }

    @NoArgsConstructor
    @Data
    public static class DatabaseDTO {

        private String dbName;

        private String url;

        private Integer port;

        private String userName;

        private String password;
    }
}
