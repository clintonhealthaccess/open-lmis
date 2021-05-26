package org.openlmis.restapi.service.backup;

import org.apache.commons.lang.StringUtils;
import org.openlmis.restapi.domain.StockCardsRequestBody;
import org.openlmis.restapi.mapper.backup.StockCardsRequestBodyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockCardsBackupRequestBodyService {

    @Autowired
    private StockCardsRequestBodyMapper stockCardsRequestBodyMapper;

    public void backupRequestBody(Long facilityId,Long userId,String deviceInfo, String versionCode, String errorCode,String requestBody) {
        StockCardsRequestBody stockCardsRequestBody = new StockCardsRequestBody(facilityId, userId, deviceInfo, versionCode, errorCode, requestBody);
        stockCardsRequestBodyMapper.insert(stockCardsRequestBody);
    }
}