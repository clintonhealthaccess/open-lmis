package org.openlmis.restapi.interceptor;

import org.openlmis.core.exception.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Interceptor extends HandlerInterceptorAdapter {

    @Value("${andorid.app.old.version.expiration.date}")
    private String expirationDate;

    @Value("${andorid.app.version.code}")
    private String versionCode;


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Date expirationDateOfAndoridApp = getExpirationDate();

        // if the android version is less than 86, the request version vode will be null
        validAppVersion(request, expirationDateOfAndoridApp);
        return true;
    }

    private void validAppVersion(HttpServletRequest request, Date expirationDateOfAndoridApp) {
        Date currentDate = new Date();
        if (currentDate.before(expirationDateOfAndoridApp)) {
            return;
        }
        Integer androidVersionCode = Integer.valueOf(versionCode);
        String requestAppInfo = request.getHeader("VersionCode");
        if (null == requestAppInfo || Integer.valueOf(requestAppInfo) < androidVersionCode) {
            throw new DataException(String.format("Please upgrade your android version %s", requestAppInfo));
        }
    }

    private Date getExpirationDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(expirationDate);
    }
}