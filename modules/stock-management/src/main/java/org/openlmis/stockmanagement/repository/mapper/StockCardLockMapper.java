package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCardLockMapper {

    @Insert("INSERT INTO stock_cards_lock "
        + "VALUES(#{facilityId}, #{productId}, #{actionType})")
    Integer lock(@Param("facilityId") Long facilityId, @Param("productId") Long productId,
        @Param("actionType") String actionType);

    @Select("SELECT 1 FROM stock_cards_lock "
        + "WHERE facilityId = #{facilityId} "
        + "AND productId= #{productId} "
        + "AND actionType = #{actionType}")
    Integer findLock(@Param("facilityId") Long facilityId, @Param("productId") Long productId,
        @Param("actionType") String actionType);

    @Delete("DELETE FROM stock_cards_lock "
        + "WHERE facilityId = #{facilityId} "
        + "AND productId= #{productId} "
        + "AND actionType = #{actionType}")
    Integer release(@Param("facilityId") Long facilityId,
        @Param("productId") Long productId,
        @Param("actionType") String actionType);

}
