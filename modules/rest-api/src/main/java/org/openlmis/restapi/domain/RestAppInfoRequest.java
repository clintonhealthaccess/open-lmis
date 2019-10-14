package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize
public class RestAppInfoRequest {
    private String userName;
    private String versionCode; //86
    private String appVersion; //1.12.86
    private Long facilityId;
    private String androidVersion;
    private String deviceInfo;

}
