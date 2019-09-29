package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.TherapeuticLinesItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TherapeuticLinesItemMapper {

  @Insert({"INSERT INTO therapeutic_line_items (rnrId, code, patientsOnTreatment, comunitaryPharmacy, modifiedBy, createdBy) values " +
      "(#{rnrId}, #{code}, #{patientsOnTreatment}, #{comunitaryPharmacy}, #{modifiedBy}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  void insert(TherapeuticLinesItem therapeuticLinesItem);

  @Select("SELECT * FROM therapeutic_line_items WHERE rnrId = #{rnrId}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "code", column = "code"),
      @Result(property = "patientsOnTreatment", column = "patientsOnTreatment"),
      @Result(property = "comunitaryPharmacy", column = "comunitaryPharmacy")
  })
  List<TherapeuticLinesItem> getTherapeuticLinesItemsByRnrId(Long rnrId);
}
