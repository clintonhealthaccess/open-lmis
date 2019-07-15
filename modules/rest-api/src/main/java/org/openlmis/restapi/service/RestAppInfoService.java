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

import org.apache.commons.lang.StringUtils;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.report.model.dto.AppInfo;
import org.openlmis.report.repository.AppInfoRepository;
import org.openlmis.restapi.domain.RestAppInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestAppInfoService {
    private final static int VERSION_UPGRADE = 1;
    private final static int VERSION_NOCHANGE = 0;
    private final static int VERSION_DEGRADE = -1;

    @Autowired
    AppInfoRepository appInfoRepository;
    @Autowired
    FacilityRepository facilityRepository;

    public static int getAppUpgradeStatus(String beforeVersion, String currentVersion) {
        if (StringUtils.isEmpty(beforeVersion) || StringUtils.isEmpty(currentVersion)) {
            return VERSION_NOCHANGE;
        }
        char[] beforeChars = beforeVersion.toCharArray(), afterChars = currentVersion.toCharArray();
        int minLength = Math.min(beforeChars.length, afterChars.length);
        for (int i = 0; i < minLength; i++) {
            if (beforeChars[i] < afterChars[i]) {
                return VERSION_UPGRADE;
            }
            if (beforeChars[i] > afterChars[i]) {
                return VERSION_DEGRADE;
            }
        }
        if (afterChars.length - minLength > 0) {
            return VERSION_UPGRADE;
        } else if (beforeChars.length - minLength > 0) {
            return VERSION_DEGRADE;
        }
        return VERSION_NOCHANGE;
    }

    public int createOrUpdateVersion(RestAppInfoRequest appInfoRequest) {
        AppInfo logedAppInfo = appInfoRepository.getAppInfoByFacilityCode(appInfoRequest.getFacilityCode());

        if (logedAppInfo == null) {
            logedAppInfo = new AppInfo(facilityRepository.getIdForCode(appInfoRequest.getFacilityCode()), appInfoRequest.getUserName(), appInfoRequest.getVersion());
            appInfoRepository.create(logedAppInfo);
            return VERSION_UPGRADE;
        }
        int appUpgradeStatus = getAppUpgradeStatus(logedAppInfo.getAppVersion(), appInfoRequest.getVersion());
        if (appUpgradeStatus == VERSION_UPGRADE) {
            logedAppInfo.setAppVersion(appInfoRequest.getVersion());
            appInfoRepository.update(logedAppInfo);
        }
        return appUpgradeStatus;
    }
}