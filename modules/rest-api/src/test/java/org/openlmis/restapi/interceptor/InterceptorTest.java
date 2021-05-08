package org.openlmis.restapi.interceptor;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.RestAppInfoRequest;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestAppInfoService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class InterceptorTest {

    @InjectMocks
    private Interceptor interceptor;

    @Mock
    private UserService userService;

    @Mock
    private RestAppInfoService restAppInfoService;

    @Test
    public void shouldCreateInfoWhenAppVersionLessThan87 () {
        User user = new User();
        user.setFacilityId(1L);
        when(userService.getByUserName(anyString())).thenReturn(user);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("VersionCode","86");
        request.addHeader("UserName","CS_Cagore");
        request.addHeader("DeviceInfo","OS: 10 Model: HUAWEI WLZ-AL10");
        request.addHeader("UniqueId","597d889b8600054c");

        interceptor.insertOrUpdateAppInfo(request);
        verify(restAppInfoService).insertOrUpdateAppInfo(any(RestAppInfoRequest.class));
    }

    @Test
    public void shouldCreateInfoWhenAppVersionMoreThan87 () {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("VersionCode","88");
        request.addHeader("UserName","CS_Cagore");
        request.addHeader("DeviceInfo","OS: 10 Model: HUAWEI WLZ-AL10");
        request.addHeader("UniqueId","597d889b8600054c");
        request.addHeader("FacilityId","1");

        interceptor.insertOrUpdateAppInfo(request);
        verify(restAppInfoService).insertOrUpdateAppInfo(any(RestAppInfoRequest.class));
    }

}
