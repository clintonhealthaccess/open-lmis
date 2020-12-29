package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.openlmis.stockmanagement.dto.StockCardBakDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockCardBakMapper {

    @Insert("<script>" +
            "INSERT INTO stock_cards_bak (facilityId, productId, fullyDelete, serverMovements, clientMovements, createdBy, createdDate) VALUES " +
            "<foreach item='stockCardBakDTO' collection='list' separator=','>" +
            "(#{stockCardBakDTO.facilityId}, #{stockCardBakDTO.productId}, #{stockCardBakDTO.fullyDelete}, #{stockCardBakDTO.serverMovements}, #{stockCardBakDTO.clientMovements}," +
            " #{stockCardBakDTO.userId}, NOW() )" +
            "</foreach>" +
            "</script>")
    void backupStockCards(@Param("list") List<StockCardBakDTO> stockCardBakDTOs);
}
