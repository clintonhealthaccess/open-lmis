package org.openlmis.restapi.domain;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;

@Data
public class StockCardsRequestBody extends BaseModel {
    private Long facilityId;
    private Long userId;
    private String deviceInfo;
    private String versionCode;
    private String errorCode;
    private String requestBody;


    public StockCardsRequestBody(Long facilityId, Long userId, String deviceInfo, String versionCode, String errorCode,String requestBody) {
        this.facilityId = facilityId;
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.versionCode = versionCode;
        this.errorCode = errorCode;
        this.requestBody = requestBody;
    }
}
