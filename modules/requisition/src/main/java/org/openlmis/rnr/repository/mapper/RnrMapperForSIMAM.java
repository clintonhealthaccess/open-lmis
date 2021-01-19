package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RnrMapperForSIMAM {
    @Select("SELECT r.id, '' || r.programid form_program_id," +
        "       (SELECT name FROM facilities f WHERE f.id=r.facilityid) facility_name," +
        "       date(r.clientsubmittedtime) date," +
        "       rl.productcode as product_code," +
        "       rl.beginningbalance beginning_balance," +
        "       rl.quantitydispensed quantity_dispensed," +
        "       rl.quantityreceived quantity_received," +
        "       rl.totallossesandadjustments total_losses_and_adjustments," +
        "       rl.stockinhand stock_in_hand," +
        "       rl.stockinhand inventory," +
        "       rl.quantityRequested quantity_requested," +
        "       rl.quantityapproved quantity_approved," +
        "       rl.totalServiceQuantity total_service_quantity" +
        "  FROM requisition_line_items rl" +
        "  LEFT JOIN requisitions r" +
        "   ON rl.rnrid = r.id" +
        "   WHERE rl.skipped<>true" +
        "   AND r.id = #{r.id}")
    List<Map<String,String>> getRnrItemsForSIMAMImport(@Param("r") Rnr rnr);

    @Select("SELECT r.id requisition_id," +
        "       (SELECT code FROM programs p WHERE p.id = r.programid) program_code," +
        "       date(r.clientSubmittedTime) date," +
        "       rli.name regimen_name," +
        "       rli.patientsOnTreatment total," +
        "       rli.regimencategory category" +
        "    FROM regimen_line_items rli" +
        "    LEFT JOIN requisitions r" +
        "    ON r.id = rli.rnrId" +
        "    WHERE r.id = #{rnr.id}" +
        "    And rli.skipped <>true" +
        "    ORDER BY rli.regimendisplayorder"
    )
    List<Map<String, String>> getRegimenItemsForSIMAMImport(@Param("rnr") Rnr rnr);

    /*
     * MMIA ,08Sxxxxx, 87
     * MMIA ,08Sxxxxx, 86
     * */
    @Select("SELECT DISTINCT p.code FROM programs p" +
        "   LEFT JOIN program_products pp" +
        "   ON pp.programid = p.id" +
        "   LEFT JOIN products pr" +
        "   ON pr.id = pp.productid" +
        "   WHERE pr.code = #{productCode}" +
        "   AND pp.active = TRUE" +
        "   AND (p.id = #{formProgramId} OR p.parentid = #{formProgramId})")
    List<String> getProductProgramCode(@Param("productCode") String productCode, @Param("formProgramId") int formProgramId);

    @Select("SELECT   #{rnr.id} id, " +
            "    '' || pp.programid form_program_id, " +
            "     #{rnr.facility.name} facility_name, " +
            "    date(#{rnr.clientSubmittedTime}) date, " +
            "    p2.code AS product_code, " +
            "    '0' beginning_balance, " +
            "    '0' quantity_dispensed, " +
            "    '0' quantity_received, " +
            "    '0' total_losses_and_adjustments, " +
            "    '0' stock_in_hand, " +
            "    '0' inventory, " +
            "    '0' quantity_requested, " +
            "    '0' quantity_approved, " +
            "    '0' total_service_quantity " +
            "FROM  program_products AS pp " +
            "LEFT JOIN products p2 ON " +
            "    pp.productid  = p2.id " +
            "WHERE pp.versioncode ='87' ;")
    List<Map<String, String>> getNewProductList(@Param("rnr") Rnr rnr);


    @Select("SELECT #{rnr.id} requisition_id," +
            "    p.code program_code ," +
            "    date(#{rnr.clientSubmittedTime}) date,"+
            "    r.name regimen_name," +
            "    '0' total," +
            "    rc.name category " +
            " FROM regimens r" +
            " JOIN regimen_categories rc ON" +
            "    rc.id = r.categoryid" +
            " JOIN programs p ON" +
            "    p.id = r.programid" +
            " WHERE r.versioncode = 87;"
    )
    List<Map<String, String>> getNewRegimenItemsForSIMAMImport(@Param("rnr") Rnr rnr);
}
