package org.openlmis.report.generator.impl;

import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.report.model.dto.LotInfo;
import org.openlmis.report.model.dto.OverStockProductDto;
import org.openlmis.report.model.params.OverStockReportParam;
import org.openlmis.report.service.SimpleTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

@Component(value = "overStockProductReport")
public class OverStockProductReportGenerator extends AbstractReportModelGenerator {

    @Autowired
    private SimpleTableService simpleTableService;

    private final static String KEY_QUERY_RESULT = "KEY_QUERY_RESULT";

    private final static String CMM_COLUMN = "8";

    private final static String MOS_COLUMN = "9";

    @Override
    protected Map<String, Object> getQueryResult(Map<Object, Object> paraMap) {
        OverStockReportParam filterCriteria = new OverStockReportParam();
        filterCriteria.setEndTime(DateUtil.parseDate(paraMap.get("endTime").toString()));
        filterCriteria.setProvinceId(Integer.parseInt(paraMap.get("provinceId").toString()));
        filterCriteria.setDistrictId(Integer.parseInt(paraMap.get("districtId").toString()));
        filterCriteria.setFacilityId(Integer.parseInt(paraMap.get("facilityId").toString()));
        List<OverStockProductDto> overStockProductDtoList = simpleTableService.getOverStockProductReport(filterCriteria);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_QUERY_RESULT, overStockProductDtoList);
        return result;
    }

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("province", getMessage("report.header.province"));
        headers.put("district", getMessage("report.header.district"));
        headers.put("facility", getMessage("report.header.facility"));
        headers.put("drugCode", getMessage("report.header.drug.code"));
        headers.put("drugName", getMessage("report.header.drug.name"));
        headers.put("lot", getMessage("report.header.lot"));
        headers.put("expiryDate", getMessage("report.header.expiry.date"));
        headers.put("soh", getMessage("report.header.stock.on.hand"));
        headers.put("cmm", getMessage("report.header.cmm"));
        headers.put("MoS", getMessage("report.header.MoS"));

        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {

        List<OverStockProductDto> overStockProductDtoList = (List<OverStockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, String>> content = new ArrayList<>();
        for (OverStockProductDto dto : overStockProductDtoList) {
            for (LotInfo lotinfo : dto.getLotList()) {
                Map<String, String> rowMap = new HashMap<>();
                rowMap.put("province", dto.getProvinceName());
                rowMap.put("district", dto.getDistrictName());
                rowMap.put("facility", dto.getFacilityName());
                rowMap.put("drugCode", dto.getProductCode());
                rowMap.put("drugName", dto.getProductName());
                rowMap.put("lot", lotinfo.getLotNumber());
                rowMap.put("expiryDate", DateUtil.formatDate(lotinfo.getExpiryDate()));
                rowMap.put("soh", lotinfo.getStockOnHandOfLot().toString());
                rowMap.put("cmm", getFormatDoubleValue(dto.getCmm()));
                rowMap.put("MoS", getFormatDoubleValue(dto.getMos()));
                content.add(rowMap);
            }
        }

        return content;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        List<OverStockProductDto> overStockProductDtoList = (List<OverStockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, String>> mergedRegions = new ArrayList<>();
        int cmmIndex = 0;
        for (int i = 0; i < overStockProductDtoList.size(); i++) {
            Map<String, String> cmmMergedRegion = new HashMap<>();
            cmmMergedRegion.put("firstCol", CMM_COLUMN);
            cmmMergedRegion.put("lastCol", CMM_COLUMN);
            cmmMergedRegion.put("mergedValue", getFormatDoubleValue(overStockProductDtoList.get(i).getCmm()));
            Map<String, String> mosMergedRegion = new HashMap<>();
            mosMergedRegion.put("firstCol", MOS_COLUMN);
            mosMergedRegion.put("lastCol", MOS_COLUMN);
            mosMergedRegion.put("mergedValue", getFormatDoubleValue(overStockProductDtoList.get(i).getMos()));

            int cmmSpan = overStockProductDtoList.get(i).getLotList().size();
            cmmMergedRegion.put("lastRow", String.valueOf(cmmIndex + cmmSpan));
            cmmMergedRegion.put("firstRow", String.valueOf(cmmIndex + 1));
            mosMergedRegion.put("lastRow", String.valueOf(cmmIndex + cmmSpan));
            mosMergedRegion.put("firstRow", String.valueOf(cmmIndex + 1));
            cmmIndex = cmmIndex + cmmSpan;

            mergedRegions.add(cmmMergedRegion);
            mergedRegions.add(mosMergedRegion);
        }

        return mergedRegions;
    }

    private String getFormatDoubleValue(Double d) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return null != d ? formatter.format(d) : "";
    }


    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        return null;
    }
}
