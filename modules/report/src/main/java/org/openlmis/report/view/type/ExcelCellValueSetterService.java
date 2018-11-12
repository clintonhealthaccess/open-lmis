package org.openlmis.report.view.type;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelCellValueSetterService {

    private Map<String, IExcelCellValueSetter> setterMap = new HashMap<>();

    @Autowired
    public void setSetterMap(Map<String, IExcelCellValueSetter> setterMap) {
        this.setterMap = setterMap;
    }

    public void setLegendaCellValue(Map<String, Object> map, Workbook workbook, Row row) {
        String value = map.get("value").toString();
        Integer cellIndex = NumberUtils.toInt(map.get("cellIndex").toString());
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        setCellStyle(map, workbook, cell);
    }

    public void setCellValue(Object params, Workbook workbook, Cell cell) {
        if (params instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)params;
            if (map.containsKey("dataType")) {
                setterMap.get(map.get("dataType").toString()).setCellValue(map, workbook, cell);
            } else {
                cell.setCellValue((String)map.get("value"));
            }
            setCellStyle(map, workbook, cell);
            return;
        }
        cell.setCellValue(String.valueOf(null != params ? params : ""));
    }

    private void setCellStyle(Map<String, Object> map, Workbook workbook, Cell cell) {
        CellStyle cellStyle = createCellStyle(map, workbook);
        if (null != cellStyle) {
            cell.setCellStyle(cellStyle);
        }
    }

    private CellStyle createCellStyle(Map<String, Object> map, Workbook workbook) {
        if (map.containsKey("style")) {
            Map<String, Object> styleMap =  (Map<String,Object>)map.get("style");
            XSSFCellStyle cellStyle =  (XSSFCellStyle)workbook.createCellStyle();
            if (styleMap.containsKey("color")) {
                cellStyle.setFillForegroundColor((XSSFColor) styleMap.get("color"));
                cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            }

            if (styleMap.containsKey("excelDataPattern")) {
                DataFormat format = workbook.createDataFormat();
                cellStyle.setDataFormat(format.getFormat(styleMap.get("excelDataPattern").toString()));
            }

            return cellStyle;
        }
        return null;
    }
}
