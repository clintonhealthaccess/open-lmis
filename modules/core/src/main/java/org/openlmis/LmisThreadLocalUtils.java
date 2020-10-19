package org.openlmis;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * If create a new thread to execute you code, the class should't be used
 */
public class LmisThreadLocalUtils {

    public static final int VERSION_87  = 87;
    public static final String STR_VERSION_87  = "87";
    public static final int VERSION_86  = 86;
    public static final String STR_VERSION_86  = "86";

    public static final String SESSION_USER = "USER";
    public static final String HEADER_USER_NAME = "UserName";
    public static final String HEADER_FACILITY_ID = "FacilityId";
    public static final String HEADER_VERSION_CODE = "VersionCode";
    public static final String HEADER_UNIQUE_ID = "UniqueId";
    public static final String HEADER_LANGUAGE = "language";
    public static final String HEADER_ANDROID_VERSION = "AndroidVersion";
    public static final String HEADER_DEVICE_INFO = "DeviceInfo";

    public static String getHeader(String key) {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        if (httpServletRequest != null) {
            return httpServletRequest.getHeader(key);
        }
        return null;
    }


    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
