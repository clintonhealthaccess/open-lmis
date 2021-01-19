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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.AppInfo;
import org.openlmis.report.repository.AppInfoRepository;
import org.openlmis.restapi.domain.RestAppInfoRequest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RestAppInfoService.class, RestAppInfoServiceTest.class})
public class RestAppInfoServiceTest {
    @InjectMocks
    RestAppInfoService service;

    @Mock
    AppInfoRepository appInfoRepository;
    @Mock
    FacilityRepository facilityRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldCreateAppInfo() {
        when(appInfoRepository.getAppInfoByFacilityId(anyLong())).thenReturn(null);
        when(facilityRepository.getIdForCode(anyString())).thenReturn(1L);
        service.insertOrUpdateAppInfo(new RestAppInfoRequest());

        verify(appInfoRepository).insert(any(AppInfo.class));
    }

    @Test
    public void shouldUpdateAppInfo() {
        AppInfo logedAppInfo = mock(AppInfo.class);
        when(logedAppInfo.getAppVersion()).thenReturn("1.12.84");
        when(appInfoRepository.getAppInfoByFacilityId(anyLong())).thenReturn(logedAppInfo);

        RestAppInfoRequest restAppInfoRequest = mock(RestAppInfoRequest.class);
        when(restAppInfoRequest.getVersionCode()).thenReturn("85");
        when(restAppInfoRequest.getFacilityId()).thenReturn(0L);

        int result = service.insertOrUpdateAppInfo(restAppInfoRequest);
        Assert.assertEquals(result, 1);
    }

    @Test
    public void shouldNotUpdateAppInfo() {
        AppInfo logedAppInfo = mock(AppInfo.class);
        when(logedAppInfo.getAppVersion()).thenReturn("1.12.84");
        when(appInfoRepository.getAppInfoByFacilityId(anyLong())).thenReturn(logedAppInfo);

        RestAppInfoRequest restAppInfoRequest = mock(RestAppInfoRequest.class);
        when(restAppInfoRequest.getVersionCode()).thenReturn("84");
        when(restAppInfoRequest.getFacilityId()).thenReturn(0L);

        int result = service.insertOrUpdateAppInfo(restAppInfoRequest);
        Assert.assertEquals(result, 0);
    }

    @Test
    public void shouldNotUpdateAppInfoWithReduceVersion() {
        AppInfo logedAppInfo = mock(AppInfo.class);
        when(logedAppInfo.getAppVersion()).thenReturn("1.12.84");
        when(appInfoRepository.getAppInfoByFacilityId(anyLong())).thenReturn(logedAppInfo);

        RestAppInfoRequest restAppInfoRequest = mock(RestAppInfoRequest.class);
        when(restAppInfoRequest.getVersionCode()).thenReturn("83");
        when(restAppInfoRequest.getFacilityId()).thenReturn(0L);

        int result = service.insertOrUpdateAppInfo(restAppInfoRequest);
        Assert.assertEquals(result, -1);
    }
}
