/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.AlReportData;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * It maps the RegimenLineItem entity to corresponding representation in database.
 */

@Repository
public interface RegimenLineItemMapper {
    
    @Insert({"INSERT INTO regimen_line_items(code, name, regimenDisplayOrder, regimenCategory, regimenCategoryDisplayOrder, rnrId, skipped, modifiedBy, createdBy, patientsontreatment, hf, chw, comunitarypharmacy) values " +
        "(#{code}, #{name}, #{regimenDisplayOrder}, #{category.name}, #{category.displayOrder}, #{rnrId}, #{skipped}, #{modifiedBy}, #{createdBy}, #{patientsOnTreatment},#{hf},#{chw},#{comunitaryPharmacy})"})
    @Options(useGeneratedKeys = true)
    void insert(RegimenLineItem regimenLineItem);
    
    @Select("SELECT * FROM regimen_line_items WHERE rnrId = #{rnrId} ORDER BY regimenCategoryDisplayOrder, regimenDisplayOrder")
    @Results(value = {
        @Result(property = "id", column = "id"),
        @Result(property = "code", column = "code"),
        @Result(property = "name", column = "name"),
        @Result(property = "patientsOnTreatment", column = "patientsOnTreatment"),
        
        @Result(property = "patientsToInitiateTreatment", column = "patientsToInitiateTreatment"),
        @Result(property = "remarks", column = "remarks"),
        @Result(property = "patientsStoppedTreatment", column = "patientsStoppedTreatment"),
        
        @Result(property = "patientsOnTreatmentAdult", column = "patientsOnTreatmentAdult"),
        @Result(property = "patientsToInitiateTreatmentAdult", column = "patientsToInitiateTreatmentAdult"),
        @Result(property = "patientsStoppedTreatmentAdult", column = "patientsStoppedTreatmentAdult"),
        
        @Result(property = "patientsOnTreatmentChildren", column = "patientsOnTreatmentChildren"),
        @Result(property = "patientsToInitiateTreatmentChildren", column = "patientsToInitiateTreatmentChildren"),
        @Result(property = "patientsStoppedTreatmentChildren", column = "patientsStoppedTreatmentChildren"),
        
        @Result(property = "regimenDisplayOrder", column = "regimenDisplayOrder"),
        @Result(property = "category.name", column = "regimenCategory"),
        @Result(property = "category.displayOrder", column = "regimenCategoryDisplayOrder"),
        @Result(property = "hf", column = "hf"),
        @Result(property = "chw", column = "chw"),
        @Result(property = "comunitaryPharmacy", column = "comunitarypharmacy")
    })
    List<RegimenLineItem> getRegimenLineItemsByRnrId(Long rnrId);
    
    @Update("UPDATE regimen_line_items set skipped = #{skipped}, " +
        
        "patientsOnTreatment = #{patientsOnTreatment}, " +
        "patientsToInitiateTreatment = #{patientsToInitiateTreatment}, " +
        "patientsStoppedTreatment = #{patientsStoppedTreatment} ," +
        
        "patientsOnTreatmentAdult = #{patientsOnTreatmentAdult}, " +
        "patientsToInitiateTreatmentAdult = #{patientsToInitiateTreatmentAdult}, " +
        "patientsStoppedTreatmentAdult = #{patientsStoppedTreatmentAdult} ," +
        
        "patientsOnTreatmentChildren = #{patientsOnTreatmentChildren}, " +
        "patientsToInitiateTreatmentChildren = #{patientsToInitiateTreatmentChildren}, " +
        "patientsStoppedTreatmentChildren = #{patientsStoppedTreatmentChildren} ," +
        "hf = #{hf}, chw = #{chw}, comunitarypharmacy = #{comunitaryPharmacy}, " +
        "remarks = #{remarks},modifiedBy = #{modifiedBy}, modifiedDate =CURRENT_TIMESTAMP where id=#{id}")
    void update(RegimenLineItem regimenLineItem);
    
    @Select("SELECT rli.name AS name, sum(hf) AS hf, sum(chw) AS chw\n" +
        "FROM regimen_line_items rli\n" +
        "INNER JOIN requisitions r ON rli.rnrid = r.id\n" +
        "INNER JOIN facilities f ON r.facilityid = f.id\n" +
        "INNER JOIN processing_periods pp ON pp.id = r.periodid\n" +
        "WHERE programid = 5 AND facilityid = #{facilityId} AND pp.startdate >= #{start} AND pp.enddate <= #{end}\n" +
        "GROUP BY rli.name;")
    @Results(value = {
        @Result(property = "name", column = "name"),
        @Result(property = "hf", column = "hf"),
        @Result(property = "chw", column = "chw")
    })
    List<AlReportData> getAlReportRequisitionsByFacilityId(@Param("start") Date start, @Param("end") Date end, @Param("facilityId") Integer facilityId);
    
    @Select("SELECT rli.name AS name, sum(hf) AS hf, sum(chw) AS chw\n" +
        "FROM regimen_line_items rli\n" +
        "INNER JOIN requisitions r ON rli.rnrid = r.id\n" +
        "INNER JOIN facilities f ON r.facilityid = f.id\n" +
        "INNER JOIN geographic_zones gz ON f.geographiczoneid = gz.id\n" +
        "INNER JOIN processing_periods pp ON pp.id = r.periodid\n" +
        "WHERE programid = 5 AND (gz.id = #{zoneId} or gz.parentid = #{zoneId}) AND pp.startdate >= #{start} AND pp.enddate <=#{end}\n" +
        "GROUP BY rli.name;")
    @Results(value = {
        @Result(property = "name", column = "name"),
        @Result(property = "hf", column = "hf"),
        @Result(property = "chw", column = "chw")
    })
    List<AlReportData> getAlReportRequisitionsByZoneId(@Param("start") Date start, @Param("end") Date end, @Param("zoneId") Integer zoneId);
    
    @Select("SELECT rli.name AS name, sum(hf) AS hf, sum(chw) AS chw\n" +
        "FROM regimen_line_items rli\n" +
        "INNER JOIN requisitions r ON rli.rnrid = r.id\n" +
        "INNER JOIN facilities f ON r.facilityid = f.id\n" +
        "INNER JOIN processing_periods pp ON pp.id = r.periodid\n" +
        "WHERE programid = 5 AND pp.startdate >= #{start} AND pp.enddate <=#{end}\n" +
        "GROUP BY rli.name;")
    @Results(value = {
        @Result(property = "name", column = "name"),
        @Result(property = "hf", column = "hf"),
        @Result(property = "chw", column = "chw")
    })
    List<AlReportData> getAlReportRequisitions(@Param("start") Date start, @Param("end") Date end);
}
