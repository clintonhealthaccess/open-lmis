package org.openlmis.restapi.domain;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@Data
public class RequisitionRequestBody extends BaseModel {
    private Long facilityId;
    private Long userId;
    private String programCode;
    private String actualPeriodStartDate;
    private String errorInfo;
    private String requestBody;


    public RequisitionRequestBody(Long facilityId, Long userId, String programCode, String actualPeriodStartDate, String errorInfo, String requestBody) {
        this.facilityId = facilityId;
        this.userId = userId;
        this.programCode = programCode;
        this.actualPeriodStartDate = actualPeriodStartDate;
        this.errorInfo = errorInfo;
        this.requestBody = requestBody;
    }
}
