package org.openlmis.stockmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.exception.DataException;
import org.openlmis.stockmanagement.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown=true)
public class StockCardEntry extends BaseModel {

  @JsonIgnore
  private StockCard stockCard;

  // TODO: will need to determine during stock receipt code if this is necessary to track
  private StockCardEntryType type;

  private Long quantity;

  private String referenceNumber;

  private StockAdjustmentReason adjustmentReason;

  @JsonIgnore
  private LotOnHand lotOnHand;

  String notes;

  private Date occurred;

  private Long requestedQuantity;

  private List<StockCardEntryKV> extensions;

  private List<StockCardEntryLotItem> stockCardEntryLotItems = new ArrayList<>();

  private static final Logger logger = LoggerFactory.getLogger(StockCardEntry.class);

  public StockCardEntry(StockCard card, StockCardEntryType type, long quantity, Date occurred, String referenceNumber, Long requestedQuantity) {
    this.stockCard = Objects.requireNonNull(card);
    this.type = Objects.requireNonNull(type);
    this.quantity = Objects.requireNonNull(quantity);
    this.extensions = new ArrayList<>();
    this.occurred = occurred;
    this.referenceNumber = referenceNumber;
    this.requestedQuantity = requestedQuantity;
  }

  @JsonIgnore
  public final boolean isValid() {
    if(null == type) return false;
    if(null == quantity) return false;

    return true;
  }

  @JsonIgnore
  public final boolean isValidAdjustment() {
    if(false == isValid()) return false;
    if(StockCardEntryType.ADJUSTMENT == type && null == adjustmentReason) return false;

    return true;
  }

  public void addKeyValue(String key, String value) {
    String newKey = key.trim().toLowerCase();
    extensions.add(new StockCardEntryKV(newKey, value, new Date()));
  }

  public void validStockCardEntry() {
    if ((CollectionUtils.isEmpty(this.getStockCard().getEntries())
        && this.getStockCard().getLastestStockCardEntry() == null)) {
      this.validFirstInventory();
    } else {
      List<StockCardEntry> stockCardEntries = stockCard.getEntries();
      StockCardEntry latestStockCardEntry =
          stockCard.getLastestStockCardEntry() == null ? stockCardEntries.get(0)
              : stockCard.getLastestStockCardEntry();
      this.validOccurredDate(latestStockCardEntry);
      this.validStockOnHand(latestStockCardEntry);
    }
  }

  private void validStockOnHand(StockCardEntry latestStockCardEntry) {
    if (latestStockCardEntry.findStockOnHand() + this.getQuantity() != this.findStockOnHand()) {
      logger.error("stock movement quantity error");
      logger.error(
          "stock movement quantity error, facilityId: " + this.getStockCard().getFacility()
              .getId() + ", stockCardId: " + this.getStockCard().getId() + "\nmovement is"
              + JsonUtils.toJsonString(this) + "\nlatestMovement is:" + JsonUtils
              .toJsonString(latestStockCardEntry));
      throw new DataException("error.stock.entry.quantity");
    }
  }

  private void validOccurredDate(StockCardEntry latestStockCardEntry) {
    if (latestStockCardEntry.getOccurred().after(this.getOccurred())) {
      logger.error("stock movement date error");
      logger.error(
          "stock movement date error, facilityId: " + this.getStockCard().getFacility()
              .getId() + ", stockCardId: " + this.getStockCard().getId() + "\nmovement is"
              + JsonUtils.toJsonString(this) + "\nlatestMovement is:" + JsonUtils
              .toJsonString(latestStockCardEntry));
      throw new DataException("error.stock.entry.date.validation");
    }
  }

  private void validFirstInventory() {
    if (!(this.getAdjustmentReason().getName().equals("INVENTORY")
        && this.getQuantity() >= 0)) {
      logger.error("stock movement first inventory error");
      logger.error(
          "stock movement first inventory error, facilityId: " + this.getStockCard().getFacility()
              .getId()
              + ", stockCardId: " + this.getStockCard().getId() + "\nmovement is" + JsonUtils
              .toJsonString(this));
      throw new DataException("error.stock.entry.first.inventory");
    }
  }

  public Long findStockOnHand() {
    for (StockCardEntryKV stockCardEntryKV : extensions) {
      if (stockCardEntryKV.getKey().equals("soh")) {
        return Long.valueOf(stockCardEntryKV.getValue());
      }
    }
    throw new DataException("error.stock.entry.soh.notfound");
  }

}