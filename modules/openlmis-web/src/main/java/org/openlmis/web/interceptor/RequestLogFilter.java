package org.openlmis.web.interceptor;

import org.apache.log4j.Logger;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class RequestLogFilter extends AbstractRequestLoggingFilter {

  private Logger logger = Logger.getLogger(getClass());
  private static final String TEMPLATE = "User is %s, facilityId is %s, Unique Device id is %s, DeviceInfo is %s, VersionCode is %s, message is %s";

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    //the request body is only available after request, so don't log anything here
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    String userName = request.getHeader("UserName");
    String facilityId = request.getHeader("FacilityId");
    String uniqueId = request.getHeader("UniqueId");
    String versionCode = request.getHeader("VersionCode");
    String deviceInfo = request.getHeader("DeviceInfo");
    String info;
    if (!message.contains("password")) {
      info = message;
    } else {
      info = "request body contains sensitive data, skipping";
    }
    logger
        .info(
            String.format(TEMPLATE, userName, facilityId, uniqueId, deviceInfo, versionCode, info));
  }
}