package org.openlmis.restapi.interceptor;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
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

    private Map<String,String> versionCodeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        versionCodeMap.put("82", "1.11.82");
        versionCodeMap.put("83", "1.12.83");
        versionCodeMap.put("84", "1.12.84");
        versionCodeMap.put("85", "1.12.85");
        versionCodeMap.put("86", "1.12.86");
        versionCodeMap.put("87", "1.12.87");
    }


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Date expirationDateOfAndoridApp = getExpirationDate();

        // if the android version is less than 86, the request version vode will be null
        updateAppVersion(request);
        validAppVersion(request, expirationDateOfAndoridApp);
        return true;
    }

    private void updateAppVersion(HttpServletRequest request) {
        String versionCode = request.getHeader("VersionCode");
        String userName = request.getHeader("UserName");
        String facilityId = request.getHeader("FacilityId");
        if (StringUtils.isNotBlank(versionCode) && StringUtils.isNotBlank(userName) && StringUtils
            .isNotBlank(facilityId)) {
            versionCode = versionCodeMap.get(versionCode);
            if (StringUtils.isNotBlank(versionCode)) {
                RestAppInfoRequest appInfoRequest = new RestAppInfoRequest();
                appInfoRequest.setFacilityId(Long.valueOf(facilityId));
                appInfoRequest.setVersion(versionCode);
                appInfoRequest.setUserName(userName);
                restAppInfoService.createOrUpdateVersion(appInfoRequest);
            }
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