package org.openlmis.restapi.domain;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.report.model.dto.AppInfo;
import org.springframework.beans.BeanUtils;

@Data
public class FacilityHistoryModel extends BaseModel {
    private Long facilityId;
    private String userName;
    private String deviceInfo;
    private String androidVersion;
    private String uniqueId;

    public static FacilityHistoryModel from(AppInfo appInfo) {
        FacilityHistoryModel model = new FacilityHistoryModel();
        BeanUtils.copyProperties(appInfo, model);
        return model;
    }
}
