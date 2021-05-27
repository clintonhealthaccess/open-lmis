package org.openlmis.restapi.service.backup;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.domain.RequisitionRequestBody;
import org.openlmis.restapi.mapper.backup.RequisitionBackupMapper;
import org.openlmis.stockmanagement.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class RequisitionBackupRequestBodyService {

    @Autowired
    RequisitionBackupMapper requisitionBackupMapper;

    @Autowired
    FacilityService facilityService;

    public void backUpRequisitionRequestBody(Report report, Long userId, String errorInfo){
        Facility reportingFacility = facilityService.getOperativeFacilityByCode(report.getAgentCode());
        Long facilityId = reportingFacility.getId();
        String programCode = report.getProgramCode();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String actualPeriodStartDate = format.format(report.getActualPeriodStartDate());
        String reportJsonStr = JsonUtils.toJsonString(report);
        if (!validIsRepeatedBackUp(facilityId, programCode, actualPeriodStartDate)){
            RequisitionRequestBody requisitionRequestBody = new RequisitionRequestBody(facilityId, userId, programCode, actualPeriodStartDate, errorInfo, reportJsonStr);
            requisitionBackupMapper.insert(requisitionRequestBody);
        }
    }

    public boolean validIsRepeatedBackUp(Long facilityId, String programCode, String actualPeriodStartDate){
        if (requisitionBackupMapper.queryReqBackUpCount(facilityId, programCode, actualPeriodStartDate) > 0){
            return true;
        }
        return false;
    }

}
