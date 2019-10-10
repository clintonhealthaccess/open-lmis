/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis;

import java.util.HashMap;
import java.util.Map;

/**
 * LmisThreadLocal is used to get/set/remove current thread's value of a thread-local
 */
public class LmisThreadLocal {

    private static final ThreadLocal<Map<String, String>> lmisThreadLocal = new ThreadLocal<>();

    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_FACILITY_ID = "facilityId";
    public static final String KEY_VERSION_CODE  = "versionCode";
    public static final String KEY_UNIQUE_ID = "uniqueId";
    public static final String KEY_DEVICE_INFO = "deviceInfo";

    public static void set(String key, String userName) {
        Map<String, String> map = lmisThreadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            map.put(key, userName);
        }
        lmisThreadLocal.set(map);
    }

    public static void remove() {
        lmisThreadLocal.remove();
    }

    public static String get(String key) {
        Map<String, String> map = lmisThreadLocal.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static String getUserName() {
        return LmisThreadLocal.get(LmisThreadLocal.KEY_USER_NAME);
    }

    public static String getFacilityId() {
        return LmisThreadLocal.get(LmisThreadLocal.KEY_FACILITY_ID);
    }

    public static String getVersionCode() {
        return LmisThreadLocal.get(LmisThreadLocal.KEY_VERSION_CODE);
    }

    public static String getUniqueId() {
        return LmisThreadLocal.get(LmisThreadLocal.KEY_UNIQUE_ID);
    }

    public static String getDeviceInfo() {
        return LmisThreadLocal.get(LmisThreadLocal.KEY_DEVICE_INFO);
    }
}
