package org.openlmis.stockmanagement.dto;

public class StockCardDeleteDto {

    private String produceCode;
    private String clientMovements;

    public String getProduceCode() {
        return produceCode;
    }

    public void setProduceCode(String produceCode) {
        this.produceCode = produceCode;
    }

    public String getClientMovements() {
        return clientMovements;
    }

    public void setClientMovements(String clientMovements) {
        this.clientMovements = clientMovements;
    }
}
