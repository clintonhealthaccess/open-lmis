/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.stockmanagement.service;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.NoArgsConstructor;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.service.FacilityService;
import org.openlmis.stockmanagement.domain.*;
import org.openlmis.stockmanagement.dto.StockCardBakDTO;
import org.openlmis.stockmanagement.repository.LotRepository;
import org.openlmis.stockmanagement.repository.StockCardRepository;
import org.openlmis.stockmanagement.repository.mapper.StockCardBakMapper;
import org.openlmis.stockmanagement.repository.mapper.StockCardLockMapper;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.springframework.util.CollectionUtils;


/**
 * Exposes the services for handling stock cards.
 */

@Service
@NoArgsConstructor
public class StockCardService {
  private static final Logger LOG = LoggerFactory.getLogger(StockCardService.class);

  @Autowired
  FacilityService facilityService;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  LotRepository lotRepository;

  @Autowired
  StockCardRepository repository;
  @Autowired
  StockCardLockMapper stockCardLockMapper;
  @Autowired
  StockCardMapper stockCardMapper;
  @Autowired
  StockCardBakMapper stockCardBakMapper;
  @Autowired
  ProductMapper productMapper;

  StockCardService(FacilityService facilityService,
                   ProductRepository productRepository,
                   LotRepository lotRepository,
                   StockCardRepository repository) {
    this.facilityService = Objects.requireNonNull(facilityService);
    this.productRepository = Objects.requireNonNull(productRepository);
    this.lotRepository = Objects.requireNonNull(lotRepository);
    this.repository = Objects.requireNonNull(repository);
  }

  @Transactional
  public LotOnHand getOrCreateLotOnHand(Lot lot, StockCard stockCard) {
    LotOnHand lotOnHand = lotRepository.getLotOnHandByStockCardAndLotObject(stockCard.getId(), lot);
    if (null == lotOnHand) {
      Lot l = lotRepository.getOrCreateLot(lot);
      lotOnHand = LotOnHand.createZeroedLotOnHand(l, stockCard);
      lotRepository.saveLotOnHand(lotOnHand);
    }

    Objects.requireNonNull(lotOnHand);
    return lotOnHand;
  }

  public LotOnHand createLotOnHandIfNotExist(Lot lot, StockCard stockCard) {
    if (stockCard.getLotsOnHand() == null) {
      stockCard.setLotsOnHand(new ArrayList<LotOnHand>());
    }

    LotOnHand lotOnHand = lotRepository.getLotOnHandByStockCardAndLot(stockCard.getId(), lot.getId());
    if (null == lotOnHand) {
      lotOnHand = LotOnHand.createZeroedLotOnHand(lot, stockCard);
      lotRepository.saveLotOnHand(lotOnHand);
      stockCard.getLotsOnHand().add(lotOnHand);
    }

    Objects.requireNonNull(lotOnHand);
    return lotOnHand;
  }


  public LotOnHand getLotOnHand(Long lotId, Lot lotObj, String productCode, StockCard card, StringBuilder str) {
    LotOnHand lotOnHand = null;
    if (null != lotId) { // Lot specified by id
      lotOnHand = lotRepository.getLotOnHandByStockCardAndLot(card.getId(), lotId);
      if (null == lotOnHand) {
        str.append("error.lot.unknown");
      }
    } else if (null != lotObj) { // Lot specified by object
      if (null == lotObj.getProduct()) {
        lotObj.setProduct(productRepository.getByCode(productCode));
      }
      if (!lotObj.isValid()) {
        str.append("error.lot.invalid");
      } else {
        //TODO:  this call might create a lot if it doesn't exist, need to implement permission check
        lotOnHand = getOrCreateLotOnHand(lotObj, card);
      }
    }

    return lotOnHand;
  }

  public StockCard getOrCreateStockCard(Long facilityId, String productCode, Long userId) {
    return repository.getOrCreateStockCard(facilityId, productCode, userId);
  }

  public StockCard getStockCardById(Long facilityId, Long stockCardId) {
    return repository.getStockCardById(facilityId, stockCardId);
  }

  public List<StockCard> getStockCards(Long facilityId) {
    return repository.getStockCards(facilityId);
  }

  public List<StockCard> queryStockCardByOccurred(Long facilityId, Date startTime, Date endTime) {
    return repository.queryStockCardByOccurred(facilityId, startTime, endTime);
  }

  @Transactional
  public void addStockCardEntry(StockCardEntry entry) {
    entry.validStockCardEntry();
    StockCard card = entry.getStockCard();
    card.setLatestStockCardEntry(entry);
    card.setTotalQuantityOnHand(entry.getStockOnHand());
    repository.updateStockCard(card);
    repository.persistStockCardEntry(entry);

    createLotMovementsAndUpdateLotOnHand(entry);

    LotOnHand lotOnHand = entry.getLotOnHand();
    if (null != lotOnHand) {
      lotOnHand.addToQuantityOnHand(entry.getQuantity());
      lotRepository.saveLotOnHand(lotOnHand);
    }
  }

  private void createLotMovementsAndUpdateLotOnHand(StockCardEntry entry) {
    HashMap<String, LotOnHand> lotOnHandMap = new HashMap<>();
    if (entry.getStockCard().getLotsOnHand() != null) {
      for (LotOnHand lotOnHand : entry.getStockCard().getLotsOnHand()) {
        lotOnHandMap.put(lotOnHand.getLot().getLotCode(), lotOnHand);
      }
    }

    for (StockCardEntryLotItem stockCardEntryLotItem : entry.getStockCardEntryLotItems()) {
      stockCardEntryLotItem.setStockCardEntryId(entry.getId());
      LotOnHand lotOnHand = lotOnHandMap.get(stockCardEntryLotItem.getLot().getLotCode());
      if (lotOnHand != null) {
        if (!CollectionUtils.isEmpty(stockCardEntryLotItem.getExtensions())) {
          lotOnHand.setQuantityOnHand(stockCardEntryLotItem.getStockOnHand());
        } else {
          lotOnHand.addToQuantityOnHand(stockCardEntryLotItem.getQuantity());
        }
      }
      lotRepository.createStockCardEntryLotItem(stockCardEntryLotItem);
    }

    if (entry.getStockCard().getLotsOnHand() != null) {
      for (LotOnHand lotOnHand : entry.getStockCard().getLotsOnHand()) {
        lotRepository.saveLotOnHand(lotOnHand);
        repository.insertLotOnHandValuesForStockEntry(lotOnHand, entry);
      }
    }
  }

  @Transactional
  public void addStockCardEntries(List<StockCardEntry> entries) {
    for (StockCardEntry entry : entries) addStockCardEntry(entry);
  }

  public void updateAllStockCardSyncTimeForFacilityToNow(long facilityId) {
    repository.updateAllStockCardSyncTimeForFacility(facilityId);
  }

  public void updateStockCardSyncTimeToNow(long facilityId, String productCode) {
      repository.updateStockCardSyncTimeToNow(facilityId, productCode);
  }

  public void updateStockCardSyncTimeToNowExclude(long facilityId, final List<String> unSyncProductCodeList) {
    for (StockCard stockCard : getStockCardsNotInList(facilityId, unSyncProductCodeList)) {
      repository.updateStockCardSyncTimeToNow(facilityId, stockCard.getProduct().getCode());
    }
  }

  private List<StockCard> getStockCardsNotInList(long facilityId, final List<String> stockCardProductCodeList) {
    return FluentIterable.from(repository.getStockCards(facilityId)).filter(new Predicate<StockCard>() {
      @Override
      public boolean apply(StockCard input) {
        return !stockCardProductCodeList.contains(input.getProduct().getCode());
      }
    }).toList();
  }

  public LotOnHand getLotOnHandByLotNumberAndProductCodeAndFacilityId(String lotNumber, String code, Long facilityId) {
    return lotRepository.getLotOnHandByLotNumberAndProductCodeAndFacilityId(lotNumber, code, facilityId);
  }

  public void deleteStockCard(Long facilityId, Long productId) {
    stockCardMapper.deleteStockCard(facilityId, productId);
  }

  public Long getProductIdByCode(String productCode){
    return productMapper.getProductIdByCode(productCode);
  }


  @Transactional
  public boolean tryLock(Long facilityId, String productCode, String actionType) {
    Long productId = productMapper.getProductIdByCode(productCode);
    return tryLock(facilityId, productId, actionType);
  }

  @Transactional
  public boolean tryLock(Long facilityId, Long productId, String actionType) {
    try {
      Integer lock = stockCardLockMapper.findLock(facilityId, productId, actionType);
      if (lock == null) {
        stockCardLockMapper.lock(facilityId, productId, actionType);
        return true;
      }
    } catch (DuplicateKeyException e) {
      LOG.error("delete and update stockCard conflict, facilityId {}, productId {}", facilityId,
          productId);
    }
    return false;
  }

  public void backupStockCard(StockCardBakDTO stockCardBakDto) {
    stockCardBakMapper.backupStockCard(stockCardBakDto);
  }

  @Transactional
  public void release(Long facilityId, String productCode, String actionType) {
    Long productId = productMapper.getProductIdByCode(productCode);
    release(facilityId, productId, actionType);
  }

  public void release(Long facilityId, Long productId, String actionType) {
    stockCardLockMapper.release(facilityId, productId, actionType);
  }

  public StockCard getStockCard(Long facilityId, Long productId) {
    return stockCardMapper.getStockCard(facilityId, productId);
  }
}
