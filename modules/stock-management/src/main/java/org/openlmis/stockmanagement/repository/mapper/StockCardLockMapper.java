package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCardLockMapper {

    @Insert("insert into stock_cards_lock values(#{facilityId}, #{productId}, #{actionType})")
    Integer lockStockCard(@Param("facilityId") Long facilityId, @Param("productId") Long productId,
        @Param("actionType") String actionType);

    @Delete("delete from stock_cards_lock where facilityId = #{facilityId} and productId= #{productId} and actionType = #{actionType}")
    Integer unLockStockCard(@Param("facilityId") Long facilityId, @Param("productId") Long productId,
        @Param("actionType") String actionType);

}
