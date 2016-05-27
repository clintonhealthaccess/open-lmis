package org.openlmis.stockmanagement.repository;

import org.openlmis.stockmanagement.domain.CMMEntry;
import org.openlmis.stockmanagement.repository.mapper.CMMMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CMMRepository {

  @Autowired
  private CMMMapper mapper;

  public void createOrUpdate(CMMEntry entry) {
    CMMEntry existingEntry = mapper.getCMMEntryByFacilityAndPeriodAndProductCode(entry.getFacilityId(), entry.getProductCode(), entry.getPeriodBegin(), entry.getPeriodEnd());
    if (existingEntry == null) {
      mapper.insert(entry);
    } else {
      entry.setId(existingEntry.getId());
      mapper.update(entry);
    }
  }
}
