package org.openlmis.stockmanagement.dto;

import lombok.Data;

@Data
public class StockCardDeleteDTO {
    private String productCode;
    private String clientMovements;
}
