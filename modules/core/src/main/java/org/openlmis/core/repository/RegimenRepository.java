/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RegimenRepository is Repository class for Regimen related database operations.
 */

@Repository
public class RegimenRepository {

  @Autowired
  RegimenMapper mapper;

  @Autowired
  RegimenCategoryMapper regimenCategoryMapper;

  @Autowired
  DosageFrequencyMapper dosageFrequencyMapper;

  @Autowired
  RegimenCombinationConstituentMapper regimenCombinationConstituentMapper;

  @Autowired
  RegimenProductCombinationMapper regimenProductCombinationMapper;

  @Autowired
  RegimenConstituentDosageMapper regimenConstituentDosageMapper;

  private List<Regimen> oldRegimens = new ArrayList<>();

  private List<Regimen> newRegimens = new ArrayList<>();

  @PostConstruct
  public void init(){
    RegimenCategory adultCategory = new RegimenCategory();
    adultCategory.setCode("ADULTS");
    adultCategory.setId(1L);
    adultCategory.setDisplayOrder(1);
    adultCategory.setName("Adults");

    RegimenCategory childCategory = new RegimenCategory();
    childCategory.setCode("PAEDIATRICS");
    childCategory.setId(2L);
    childCategory.setDisplayOrder(2);
    childCategory.setName("Paediatrics");
    initOldRegimens(adultCategory, childCategory);
    initnewRegimens(adultCategory, childCategory);
  }


  public List<Regimen> getByProgram(Long programId) {
    return mapper.getByProgram(programId);
  }
   public Regimen getById(Long id){return mapper.getById(id);}

  public List<RegimenCategory> getAllRegimenCategories() {
    return regimenCategoryMapper.getAll();
  }

  public void save(List<Regimen> regimens, Long userId) {
    for (Regimen regimen : regimens) {
      regimen.setModifiedBy(userId);
      if (regimen.getId() == null) {
        regimen.setCreatedBy(userId);
        mapper.insert(regimen);
      }
      mapper.update(regimen);
    }
  }

  public void save(Regimen regimen, Long userId) {
      regimen.setModifiedBy(userId);
      regimen.setCreatedBy(userId);
      mapper.insert(regimen);
  }

  public List<Regimen> getAllRegimens(){
       return mapper.getAllRegimens();
  }

  public List<DosageFrequency> getAllDosageFrequencies(){
      return dosageFrequencyMapper.getAll();
  }

  public List<RegimenCombinationConstituent> getAllRegimenCombinationConstituents(){
      return regimenCombinationConstituentMapper.getAll();
  }

  public List<RegimenConstituentDosage> getAllRegimenConstituentsDosages(){
      return regimenConstituentDosageMapper.getAll();
  }

  public List<RegimenProductCombination> getAllRegimenProductCombinations(){
      return regimenProductCombinationMapper.getAll();
  }

  public List<Regimen> getRegimensByCategory(RegimenCategory category) {
    return mapper.getRegimensByCategoryId(category.getId());
  }

  public RegimenCategory getRegimenCategoryByName(String name) {
    return regimenCategoryMapper.getByName(name);
  }

  public Regimen getRegimensByCategoryIdAndName(Long categoryId, String code) {

    return mapper.getRegimensByCategoryIdAndName(categoryId, code);
  }

  public List<Regimen> getRegimensByProgramAndIsCustom(Long programId, boolean isCustom,
      String versionCode) {
    if (programId == 1) {
      if (versionCode == null || Integer.valueOf(versionCode) < 87) {
          return new ArrayList<>(oldRegimens);
      } else {
        return new ArrayList<>(newRegimens);
      }
    } else {
      return mapper.getRegimensByProgramAndIsCustom(programId, isCustom);
    }
  }

  @Transactional
  public void toPersistDbByOperationType(List<Regimen> saves, List<Regimen> updates) {

    for (Regimen save : saves) {
      mapper.insert(save);
    }

    for (Regimen update : updates) {
      mapper.updateByCode(update);
    }

  }

  public void initOldRegimens(RegimenCategory adultCategory,RegimenCategory childCategory){
    // adult
    Regimen oldRegimen1 = new Regimen();
    oldRegimen1.setCategory(adultCategory);
    oldRegimen1.setCode("001");
    oldRegimen1.setId(19L);
    oldRegimen1.setProgramId(1L);
    oldRegimen1.setName("AZT+3TC+NVP");
    oldRegimen1.setDisplayOrder(1);
    oldRegimen1.setCustom(false);
    oldRegimen1.setActive(true);
    oldRegimen1.setSkipped(false);

    Regimen oldRegimen2 = new Regimen();
    oldRegimen2.setCategory(adultCategory);
    oldRegimen2.setCode("002");
    oldRegimen2.setId(20L);
    oldRegimen2.setProgramId(1L);
    oldRegimen2.setName("TDF+3TC+EFV");
    oldRegimen2.setDisplayOrder(2);
    oldRegimen2.setCustom(false);
    oldRegimen2.setActive(true);
    oldRegimen2.setSkipped(false);


    Regimen oldRegimen3 = new Regimen();
    oldRegimen3.setCategory(adultCategory);
    oldRegimen3.setCode("003");
    oldRegimen3.setId(21L);
    oldRegimen3.setProgramId(1L);
    oldRegimen3.setName("AZT+3TC+EFV");
    oldRegimen3.setDisplayOrder(3);
    oldRegimen3.setCustom(false);
    oldRegimen3.setActive(true);
    oldRegimen3.setSkipped(false);

    Regimen oldRegimen4 = new Regimen();
    oldRegimen4.setCategory(adultCategory);
    oldRegimen4.setCode("004");
    oldRegimen4.setId(22L);
    oldRegimen4.setProgramId(1L);
    oldRegimen4.setName("d4T 30+3TC+NVP");
    oldRegimen4.setDisplayOrder(3);
    oldRegimen4.setCustom(false);
    oldRegimen4.setActive(true);
    oldRegimen4.setSkipped(false);

    Regimen oldRegimen5 = new Regimen();
    oldRegimen5.setCategory(adultCategory);
    oldRegimen5.setCode("005");
    oldRegimen5.setId(23L);
    oldRegimen5.setProgramId(1L);
    oldRegimen5.setName("d4T 30+3TC+EFV");
    oldRegimen5.setDisplayOrder(3);
    oldRegimen5.setCustom(false);
    oldRegimen5.setActive(true);
    oldRegimen5.setSkipped(false);

    Regimen oldRegimen6 = new Regimen();
    oldRegimen6.setCategory(adultCategory);
    oldRegimen6.setCode("006");
    oldRegimen6.setId(24L);
    oldRegimen6.setProgramId(1L);
    oldRegimen6.setName("AZT+3TC+LPV/r");
    oldRegimen6.setDisplayOrder(3);
    oldRegimen6.setCustom(false);
    oldRegimen6.setActive(true);
    oldRegimen6.setSkipped(false);

    Regimen oldRegimen7 = new Regimen();
    oldRegimen7.setCategory(adultCategory);
    oldRegimen7.setCode("007");
    oldRegimen7.setId(25L);
    oldRegimen7.setProgramId(1L);
    oldRegimen7.setName("TDF+3TC+LPV/r");
    oldRegimen7.setDisplayOrder(3);
    oldRegimen7.setCustom(false);
    oldRegimen7.setActive(true);
    oldRegimen7.setSkipped(false);

    Regimen oldRegimen8 = new Regimen();
    oldRegimen8.setCategory(adultCategory);
    oldRegimen8.setCode("001");
    oldRegimen8.setId(19L);
    oldRegimen8.setProgramId(1L);
    oldRegimen8.setName("ABC+3TC+LPV/r");
    oldRegimen1.setDisplayOrder(3);
    oldRegimen8.setCustom(false);
    oldRegimen8.setActive(true);
    oldRegimen8.setSkipped(false);

    // child
    Regimen oldRegimen9 = new Regimen();
    oldRegimen9.setCategory(childCategory);
    oldRegimen9.setCode("009");
    oldRegimen9.setId(27L);
    oldRegimen9.setProgramId(1L);
    oldRegimen9.setName("d4T+3TC+NVP(3DFC Baby)");
    oldRegimen9.setDisplayOrder(3);
    oldRegimen9.setCustom(false);
    oldRegimen9.setActive(true);
    oldRegimen9.setSkipped(false);

    Regimen oldRegimen10 = new Regimen();
    oldRegimen10.setCategory(childCategory);
    oldRegimen10.setCode("010");
    oldRegimen10.setId(28L);
    oldRegimen10.setProgramId(1L);
    oldRegimen10.setName("d4T+3TC+LPV/r(2DFC Baby + LPV/r)");
    oldRegimen10.setDisplayOrder(3);
    oldRegimen10.setCustom(false);
    oldRegimen10.setActive(true);
    oldRegimen10.setSkipped(false);

    Regimen oldRegimen11 = new Regimen();
    oldRegimen11.setCategory(childCategory);
    oldRegimen11.setCode("011");
    oldRegimen11.setId(29L);
    oldRegimen11.setProgramId(1L);
    oldRegimen11.setName("d4T+3TC+ABC(2DFC Baby + ABC)");
    oldRegimen11.setDisplayOrder(3);
    oldRegimen11.setCustom(false);
    oldRegimen11.setActive(true);
    oldRegimen11.setSkipped(false);

    Regimen oldRegimen12 = new Regimen();
    oldRegimen12.setCategory(childCategory);
    oldRegimen12.setCode("012");
    oldRegimen12.setId(30L);
    oldRegimen12.setProgramId(1L);
    oldRegimen12.setName("d4T+3TC+EFV(2DFC Baby + EFV)");
    oldRegimen12.setDisplayOrder(3);
    oldRegimen12.setCustom(false);
    oldRegimen12.setActive(true);
    oldRegimen12.setSkipped(false);

    Regimen oldRegimen13 = new Regimen();
    oldRegimen13.setCategory(childCategory);
    oldRegimen13.setCode("013");
    oldRegimen13.setId(31L);
    oldRegimen13.setProgramId(1L);
    oldRegimen13.setName("AZT60+3TC+NVP(3DFC)");
    oldRegimen13.setDisplayOrder(3);
    oldRegimen13.setCustom(false);
    oldRegimen13.setActive(true);
    oldRegimen13.setSkipped(false);

    Regimen oldRegimen14 = new Regimen();
    oldRegimen14.setCategory(childCategory);
    oldRegimen14.setCode("015");
    oldRegimen14.setId(33L);
    oldRegimen14.setProgramId(1L);
    oldRegimen14.setName("AZT60+3TC+ABC(2DFC + ABC)");
    oldRegimen14.setDisplayOrder(3);
    oldRegimen14.setCustom(false);
    oldRegimen14.setActive(true);
    oldRegimen1.setSkipped(false);

    Regimen oldRegimen15 = new Regimen();
    oldRegimen15.setCategory(childCategory);
    oldRegimen15.setCode("016");
    oldRegimen15.setId(34L);
    oldRegimen15.setProgramId(1L);
    oldRegimen15.setName("AZT60+3TC+LPV/r(2DFC + LPV/r)");
    oldRegimen15.setDisplayOrder(3);
    oldRegimen15.setCustom(false);
    oldRegimen15.setActive(true);
    oldRegimen15.setSkipped(false);

    Regimen oldRegimen16 = new Regimen();
    oldRegimen16.setCategory(childCategory);
    oldRegimen16.setCode("016");
    oldRegimen16.setId(34L);
    oldRegimen16.setProgramId(1L);
    oldRegimen16.setName("AZT60+3TC+LPV/r(2DFC + LPV/r)");
    oldRegimen16.setDisplayOrder(3);
    oldRegimen16.setCustom(false);
    oldRegimen16.setActive(true);
    oldRegimen16.setSkipped(false);

    Regimen oldRegimen17 = new Regimen();
    oldRegimen17.setCategory(childCategory);
    oldRegimen17.setCode("018");
    oldRegimen17.setId(36L);
    oldRegimen17.setProgramId(1L);
    oldRegimen17.setName("ABC+3TC+EFZ");
    oldRegimen17.setDisplayOrder(3);
    oldRegimen17.setCustom(false);
    oldRegimen17.setActive(true);
    oldRegimen17.setSkipped(false);

    Regimen oldRegimen18 = new Regimen();
    oldRegimen18.setCategory(childCategory);
    oldRegimen18.setCode("017");
    oldRegimen18.setId(32L);
    oldRegimen18.setProgramId(1L);
    oldRegimen18.setName("AZT60+3TC+EFV(2DFC + EFV)");
    oldRegimen18.setDisplayOrder(3);
    oldRegimen18.setCustom(false);
    oldRegimen18.setActive(true);
    oldRegimen18.setSkipped(false);

    oldRegimens.add(oldRegimen1);
    oldRegimens.add(oldRegimen2);
    oldRegimens.add(oldRegimen3);
    oldRegimens.add(oldRegimen4);
    oldRegimens.add(oldRegimen5);
    oldRegimens.add(oldRegimen6);
    oldRegimens.add(oldRegimen7);
    oldRegimens.add(oldRegimen8);
    oldRegimens.add(oldRegimen9);
    oldRegimens.add(oldRegimen10);
    oldRegimens.add(oldRegimen11);
    oldRegimens.add(oldRegimen12);
    oldRegimens.add(oldRegimen13);
    oldRegimens.add(oldRegimen14);
    oldRegimens.add(oldRegimen15);
    oldRegimens.add(oldRegimen16);
    oldRegimens.add(oldRegimen17);
    oldRegimens.add(oldRegimen18);

  }



  public void initnewRegimens(RegimenCategory adultCategory,RegimenCategory childCategory){
    // adult
    Regimen newRegimen1 = new Regimen();
    newRegimen1.setCategory(adultCategory);
    newRegimen1.setCode("002");
    newRegimen1.setProgramId(1L);
    newRegimen1.setName("TDF+3TC+DTG");
    newRegimen1.setDisplayOrder(1);
    newRegimen1.setCustom(false);
    newRegimen1.setActive(true);
    newRegimen1.setSkipped(false);

    Regimen newRegimen2 = new Regimen();
    newRegimen2.setCategory(adultCategory);
    newRegimen2.setCode("TO82");
    newRegimen2.setProgramId(1L);
    newRegimen2.setName("TDF+3TC+DTG");
    newRegimen2.setDisplayOrder(2);
    newRegimen2.setCustom(false);
    newRegimen2.setActive(true);
    newRegimen2.setSkipped(false);


    Regimen newRegimen3 = new Regimen();
    newRegimen3.setCategory(adultCategory);
    newRegimen3.setCode("TO83");
    newRegimen3.setProgramId(1L);
    newRegimen3.setName("AZT+3TC+NVP");
    newRegimen3.setDisplayOrder(3);
    newRegimen3.setCustom(false);
    newRegimen3.setActive(true);
    newRegimen3.setSkipped(false);

    Regimen newRegimen4 = new Regimen();
    newRegimen4.setCategory(adultCategory);
    newRegimen4.setCode("003");
    newRegimen4.setProgramId(1L);
    newRegimen4.setName("AZT+3TC+EFV");
    newRegimen4.setDisplayOrder(4);
    newRegimen4.setCustom(false);
    newRegimen4.setActive(true);
    newRegimen4.setSkipped(false);

    Regimen newRegimen5 = new Regimen();
    newRegimen5.setCategory(adultCategory);
    newRegimen5.setCode("TO85");
    newRegimen5.setProgramId(1L);
    newRegimen5.setName("AZT+3TC+ATV/r");
    newRegimen5.setDisplayOrder(5);
    newRegimen5.setCustom(false);
    newRegimen5.setActive(true);
    newRegimen5.setSkipped(false);

    Regimen newRegimen6 = new Regimen();
    newRegimen6.setCategory(adultCategory);
    newRegimen6.setCode("TO86");
    newRegimen6.setProgramId(1L);
    newRegimen6.setName("TDF+3TC+ATV/r");
    newRegimen6.setDisplayOrder(6);
    newRegimen6.setCustom(false);
    newRegimen6.setActive(true);
    newRegimen6.setSkipped(false);

    Regimen newRegimen7 = new Regimen();
    newRegimen7.setCategory(adultCategory);
    newRegimen7.setCode("TO87");
    newRegimen7.setProgramId(1L);
    newRegimen7.setName("ABC+3TC+ATV/r");
    newRegimen7.setDisplayOrder(7);
    newRegimen7.setCustom(false);
    newRegimen7.setActive(true);
    newRegimen7.setSkipped(false);

    Regimen newRegimen8 = new Regimen();
    newRegimen8.setCategory(adultCategory);
    newRegimen8.setCode("006");
    newRegimen8.setId(19L);
    newRegimen8.setProgramId(1L);
    newRegimen8.setName("AZT+3TC+LPV/r");
    newRegimen1.setDisplayOrder(8);
    newRegimen8.setCustom(false);
    newRegimen8.setActive(true);
    newRegimen8.setSkipped(false);

    Regimen newRegimen9 = new Regimen();
    newRegimen9.setCategory(childCategory);
    newRegimen9.setCode("007");
    newRegimen9.setId(27L);
    newRegimen9.setProgramId(1L);
    newRegimen9.setName("TDF+3TC+LPV/r");
    newRegimen9.setDisplayOrder(9);
    newRegimen9.setCustom(false);
    newRegimen9.setActive(true);
    newRegimen9.setSkipped(false);

    Regimen newRegimen10 = new Regimen();
    newRegimen10.setCategory(childCategory);
    newRegimen10.setCode("008");
    newRegimen10.setId(28L);
    newRegimen10.setProgramId(1L);
    newRegimen10.setName("ABC+3TC+LPV/r");
    newRegimen10.setDisplayOrder(10);
    newRegimen10.setCustom(false);
    newRegimen10.setActive(true);
    newRegimen10.setSkipped(false);

    // child
    Regimen newRegimen11 = new Regimen();
    newRegimen11.setCategory(childCategory);
    newRegimen11.setCode("TO811");
    newRegimen11.setId(29L);
    newRegimen11.setProgramId(1L);
    newRegimen11.setName("ABC+3TC+LPV/r (2DFC ped + LPV/r 100/25)");
    newRegimen11.setDisplayOrder(11);
    newRegimen11.setCustom(false);
    newRegimen11.setActive(true);
    newRegimen11.setSkipped(false);

    Regimen newRegimen12 = new Regimen();
    newRegimen12.setCategory(childCategory);
    newRegimen12.setCode("TO812");
    newRegimen12.setId(30L);
    newRegimen12.setProgramId(1L);
    newRegimen12.setName("ABC+3TC+LPV/r (2DFC ped + LPV/r 80/20)");
    newRegimen12.setDisplayOrder(12);
    newRegimen12.setCustom(false);
    newRegimen12.setActive(true);
    newRegimen12.setSkipped(false);

    Regimen newRegimen13 = new Regimen();
    newRegimen13.setCategory(childCategory);
    newRegimen13.setCode("TO813");
    newRegimen13.setId(31L);
    newRegimen13.setProgramId(1L);
    newRegimen13.setName("ABC+3TC+LPV/r (2DFC ped + LPV/r 40/10)");
    newRegimen13.setDisplayOrder(13);
    newRegimen13.setCustom(false);
    newRegimen13.setActive(true);
    newRegimen13.setSkipped(false);

    Regimen newRegimen14 = new Regimen();
    newRegimen14.setCategory(childCategory);
    newRegimen14.setCode("TO814");
    newRegimen14.setId(33L);
    newRegimen14.setProgramId(1L);
    newRegimen14.setName("ABC+3TC+EFV (2DFC ped + EFV200)");
    newRegimen14.setDisplayOrder(14);
    newRegimen14.setCustom(false);
    newRegimen14.setActive(true);
    newRegimen1.setSkipped(false);

    Regimen newRegimen15 = new Regimen();
    newRegimen15.setCategory(childCategory);
    newRegimen15.setCode("TO815");
    newRegimen15.setProgramId(1L);
    newRegimen15.setName("AZT 60+3TC 30+NVP 50 (3DFC)");
    newRegimen15.setDisplayOrder(15);
    newRegimen15.setCustom(false);
    newRegimen15.setActive(true);
    newRegimen15.setSkipped(false);

    Regimen newRegimen16 = new Regimen();
    newRegimen16.setCategory(childCategory);
    newRegimen16.setCode("TO8-16");
    newRegimen16.setProgramId(1L);
    newRegimen16.setName("AZT 60+3TC 30+LPV/r (2DFC + LPV/r 100/25)");
    newRegimen16.setDisplayOrder(16);
    newRegimen16.setCustom(false);
    newRegimen16.setActive(true);
    newRegimen16.setSkipped(false);

    Regimen newRegimen17 = new Regimen();
    newRegimen17.setCategory(childCategory);
    newRegimen17.setCode("TO817");
    newRegimen17.setId(36L);
    newRegimen17.setProgramId(1L);
    newRegimen17.setName("AZT 60+3TC 30+LPV/r (2DFC + LPV/r 80/20)");
    newRegimen17.setDisplayOrder(17);
    newRegimen17.setCustom(false);
    newRegimen17.setActive(true);
    newRegimen17.setSkipped(false);

    Regimen newRegimen18 = new Regimen();
    newRegimen18.setCategory(childCategory);
    newRegimen18.setCode("018");
    newRegimen18.setId(32L);
    newRegimen18.setProgramId(1L);
    newRegimen18.setName("AZT 60+3TC 30+LPV/r (2DFC + LPV/r 40/10)");
    newRegimen18.setDisplayOrder(18);
    newRegimen18.setCustom(false);
    newRegimen18.setActive(true);
    newRegimen18.setSkipped(false);

    newRegimens.add(newRegimen1);
    newRegimens.add(newRegimen2);
    newRegimens.add(newRegimen3);
    newRegimens.add(newRegimen4);
    newRegimens.add(newRegimen5);
    newRegimens.add(newRegimen6);
    newRegimens.add(newRegimen7);
    newRegimens.add(newRegimen8);
    newRegimens.add(newRegimen9);
    newRegimens.add(newRegimen10);
    newRegimens.add(newRegimen11);
    newRegimens.add(newRegimen12);
    newRegimens.add(newRegimen13);
    newRegimens.add(newRegimen14);
    newRegimens.add(newRegimen15);
    newRegimens.add(newRegimen16);
    newRegimens.add(newRegimen17);
    newRegimens.add(newRegimen18);

  }
}
