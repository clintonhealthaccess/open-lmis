package org.openlmis.restapi.mapper.backup;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.restapi.domain.StockCardsRequestBody;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCardsRequestBodyMapper {
    @Insert("INSERT INTO stock_cards_request_body_bak (facilityId, userId, deviceInfo, versionCode, errorCode, request_body) VALUES(#{facilityId}, #{userId}, #{deviceInfo}, #{versionCode}, #{errorCode}, #{requestBody})")
    @Options(useGeneratedKeys = true)
    int insert(StockCardsRequestBody stockCardsRequestBody);
}
