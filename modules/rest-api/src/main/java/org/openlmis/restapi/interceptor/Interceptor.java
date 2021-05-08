package org.openlmis.restapi.interceptor;

import static org.openlmis.LmisThreadLocalUtils.HEADER_ANDROID_VERSION;
import static org.openlmis.LmisThreadLocalUtils.HEADER_DEVICE_INFO;
import static org.openlmis.LmisThreadLocalUtils.HEADER_FACILITY_ID;
import static org.openlmis.LmisThreadLocalUtils.HEADER_UNIQUE_ID;
import static org.openlmis.LmisThreadLocalUtils.HEADER_USER_NAME;
import static org.openlmis.LmisThreadLocalUtils.HEADER_VERSION_CODE;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.UserService;
import org.openlmis.restapi.domain.RestAppInfoRequest;
import org.openlmis.restapi.service.RestAppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Interceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RestAppInfoService restAppInfoService;

    @Autowired
    private UserService userService;

    @Value("${andorid.app.old.version.expiration.date}")
    private String expirationDate;

    @Value("${andorid.app.version.code}")
    private String versionCode;




    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Date expirationDateOfAndoridApp = getExpirationDate();

        // if the android version is less than 86, the request version code will be null
        validAppVersion(request, expirationDateOfAndoridApp);
        insertOrUpdateAppInfo(request);
        return true;
    }

    public void insertOrUpdateAppInfo(HttpServletRequest request) {
        String versionCode = request.getHeader(HEADER_VERSION_CODE);
        String userName = request.getHeader(HEADER_USER_NAME);
        String facilityId = request.getHeader(HEADER_FACILITY_ID);
        String androidVersion = request.getHeader(HEADER_ANDROID_VERSION);
        String deviceInfo = request.getHeader(HEADER_DEVICE_INFO);
        String uniqueId = request.getHeader(HEADER_UNIQUE_ID);

        RestAppInfoRequest appInfoRequest = new RestAppInfoRequest();
        if(Integer.valueOf(versionCode) < Integer.valueOf("87")){
            User user = userService.getByUserName(userName);
            appInfoRequest.setFacilityId(user.getFacilityId());
        }else {
            appInfoRequest.setFacilityId(Long.valueOf(facilityId));
        }
        if (StringUtils.isNotBlank(appInfoRequest.getFacilityId().toString()) && StringUtils.isNotBlank(versionCode)) {
            appInfoRequest.setUserName(userName);
            appInfoRequest.setUniqueId(uniqueId);
            appInfoRequest.setVersionCode(versionCode);
            appInfoRequest.setAndroidVersion(androidVersion);
            appInfoRequest.setDeviceInfo(deviceInfo);
            restAppInfoService.insertOrUpdateAppInfo(appInfoRequest);
        }
    }

    private void validAppVersion(HttpServletRequest request, Date expirationDateOfAndoridApp) {
        Date currentDate = new Date();
        if (currentDate.before(expirationDateOfAndoridApp)) {
            return;
        }
        Integer androidVersionCode = Integer.valueOf(versionCode);
        String requestAppInfo = request.getHeader("VersionCode");
        if (StringUtils.isBlank(requestAppInfo) || Integer.valueOf(requestAppInfo) < androidVersionCode) {
            throw new DataException("Please upgrade your android version");
        }
    }

    private Date getExpirationDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(expirationDate);
    }
}