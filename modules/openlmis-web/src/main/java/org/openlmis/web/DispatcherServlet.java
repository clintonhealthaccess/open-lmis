/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.web;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.LmisThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

/**
 * This class is the central dispatcher for HTTP request handlers/controllers.
 */
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

  @Override
  protected void doService(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    addRequestInfo(request);
    try {
      super.doService(request, response);
    } finally {
      LmisThreadLocal.remove();
    }
  }


  private void addRequestInfo(HttpServletRequest request) {
    Object userName = request.getSession().getAttribute(USER);
    String facilityId = request.getHeader("FacilityId");
    String uniqueId = request.getHeader("UniqueId");
    String versionCode = request.getHeader("VersionCode");
    String deviceInfo = request.getHeader("DeviceInfo");
    if (userName != null) {
      LmisThreadLocal.set(LmisThreadLocal.KEY_USER_NAME, userName.toString());
    }
    if (StringUtils.isNotBlank(facilityId)) {
      LmisThreadLocal.set(LmisThreadLocal.KEY_FACILITY_ID, facilityId);
    }
    if (StringUtils.isNotBlank(uniqueId)) {
      LmisThreadLocal.set(LmisThreadLocal.KEY_UNIQUE_ID, uniqueId);
    }
    if (StringUtils.isNotBlank(versionCode)) {
      LmisThreadLocal.set(LmisThreadLocal.KEY_VERSION_CODE, versionCode);
    }
    if (StringUtils.isNotBlank(deviceInfo)) {
      LmisThreadLocal.set(LmisThreadLocal.KEY_DEVICE_INFO, deviceInfo);
    }
  }
}
