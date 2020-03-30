package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.openlmis.stockmanagement.dto.StockCardBakDto;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCardBakMapper {

    @Insert("INSERT INTO stock_cards_bak (facilityId" +
        ", productId" +
        ", serverMovements" +
        ", clientMovements" +
        ", createdBy" +
        ", createdDate" +
        ") VALUES ( #{facilityId}" +
        ", #{productId}" +
        ", #{serverMovements}" +
        ", #{clientMovements}" +
        ", #{userId}" +
        ", NOW() )")
    void insertBack(StockCardBakDto stockCardBakDto);
}
