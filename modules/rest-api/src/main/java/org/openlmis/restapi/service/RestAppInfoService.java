/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */
package org.openlmis.restapi.service;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.report.model.dto.AppInfo;
import org.openlmis.report.repository.AppInfoRepository;
import org.openlmis.restapi.domain.RestAppInfoRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestAppInfoService {
    private Map<String,String> versionCodeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        versionCodeMap.put("82", "1.11.82");
        versionCodeMap.put("83", "1.12.83");
        versionCodeMap.put("84", "1.12.84");
        versionCodeMap.put("85", "1.12.85");
        versionCodeMap.put("86", "1.12.86");
        versionCodeMap.put("87", "1.12.87");
        versionCodeMap.put("88", "1.12.88");
        versionCodeMap.put("89", "1.12.89");

    }

    @Autowired
    AppInfoRepository appInfoRepository;
    @Autowired
    FacilityRepository facilityRepository;

    private int getAppVersionUpdateStatus(String oldAppVersion, String currentVersionCode) {
        String oldVersionCode = oldAppVersion.substring(oldAppVersion.lastIndexOf(".") + 1);
        return Integer.compare(Integer.parseInt(currentVersionCode), Integer.parseInt(oldVersionCode));
    }

    @Transactional
    public int createOrUpdateVersion(RestAppInfoRequest appInfoRequest) {
        appInfoRequest
            .setAppVersion(versionCodeMap.get(appInfoRequest.getVersionCode()));
        AppInfo appInfo = appInfoRepository.getAppInfoByFacilityId(appInfoRequest.getFacilityId());
        if (appInfo == null) {
            appInfo = new AppInfo();
            BeanUtils.copyProperties(appInfoRequest, appInfo);
            return appInfoRepository.create(appInfo);
        }
        appInfo.setAndroidVersion(appInfoRequest.getAndroidVersion());
        appInfo.setDeviceInfo(appInfoRequest.getDeviceInfo());
        appInfoRepository.updateInfo(appInfo);
        int updateStatus = getAppVersionUpdateStatus(appInfo.getAppVersion(),
            appInfoRequest.getVersionCode());
        if (updateStatus == 1) {
            appInfoRepository.updateAppVersion(appInfo);
        }
        return updateStatus;
    }
}