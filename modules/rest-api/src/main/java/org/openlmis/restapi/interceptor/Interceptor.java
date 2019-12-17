package org.openlmis.restapi.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.exception.DataException;
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

    private void insertOrUpdateAppInfo(HttpServletRequest request) {
        String versionCode = request.getHeader("VersionCode");
        String userName = request.getHeader("UserName");
        String facilityId = request.getHeader("FacilityId");
        String androidVersion = request.getHeader("AndroidVersion");
        String deviceInfo = request.getHeader("DeviceInfo");
        String uniqueId = request.getHeader("UniqueId");
        if (StringUtils.isNotBlank(facilityId) && StringUtils.isNotBlank(versionCode)) {
            RestAppInfoRequest appInfoRequest = new RestAppInfoRequest();
            appInfoRequest.setFacilityId(Long.valueOf(facilityId));
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