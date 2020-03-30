package org.openlmis.stockmanagement.dto;

public class StockCardBakDto {

    private Long facilityId;
    private Long productId;
    private Long userId;
    private String clientMovements;
    private String serverMovements;

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getClientMovements() {
        return clientMovements;
    }

    public void setClientMovements(String clientMovements) {
        this.clientMovements = clientMovements;
    }

    public String getServerMovements() {
        return serverMovements;
    }

    public void setServerMovements(String serverMovements) {
        this.serverMovements = serverMovements;
    }
}
