package org.openlmis.stockmanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockCardBakDTO {
    private Long facilityId;
    private Long productId;
    private Boolean fullyDelete;
    private Long userId;
    private String clientMovements;
    private String serverMovements;
}
