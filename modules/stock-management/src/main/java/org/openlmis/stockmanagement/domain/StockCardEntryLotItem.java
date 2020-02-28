package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openlmis.core.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
public class StockCardEntryLotItem extends BaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(StockCardEntryLotItem.class);
    @JsonIgnore
    private Long stockCardEntryId;

    private Lot lot;

    private Long quantity;

    private Date effectiveDate;

    private List<StockCardEntryLotItemKV> extensions = new ArrayList<>();


    public StockCardEntryLotItem(Lot lot, Long quantity) {
        this.lot = lot;
        this.quantity = quantity;
    }

    public void addKeyValue(String key, String value) {
        String newKey = key.trim().toLowerCase();
        extensions.add(new StockCardEntryLotItemKV(newKey, value, new Date()));
    }

    public long getStockOnHand() {
        for (StockCardEntryLotItemKV lotItemKV : extensions) {
            if (lotItemKV.getKey().equals("soh")) {
                return Long.valueOf(lotItemKV.getValue());
            }
        }
        LOG.error("product[{}], lot[{}] has no soh field", this.getLot().getProduct().getCode(),
            this.getLot().getLotCode());
        throw new DataException("error.stock.entry.lot.soh.notfound");
    }
}
