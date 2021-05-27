package org.openlmis.restapi.mapper.backup;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.restapi.domain.RequisitionRequestBody;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionBackupMapper {

    @Insert("INSERT INTO requisition_request_body_bak (facilityId, userid, programCode, actualPeriodStartDate, errorInfo, request_body) " +
            "values (#{facilityId},#{userId},#{programCode},#{actualPeriodStartDate},#{errorInfo},#{requestBody})")
    int insert(RequisitionRequestBody requisitionRequestBody);

    @Select("select count(*) from requisition_request_body_bak t where " +
            "t.facilityid = #{facilityId} and t.programCode = #{programCode} and t.actualPeriodStartDate = #{actualPeriodStartDate}")
    int queryReqBackUpCount(@Param(value = "facilityId") Long facilityId,
                      @Param(value = "programCode") String programCode,
                      @Param(value = "actualPeriodStartDate") String actualPeriodStartDate);

}
