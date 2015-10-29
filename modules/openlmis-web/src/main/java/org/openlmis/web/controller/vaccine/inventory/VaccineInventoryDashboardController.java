/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.vaccine.inventory;


import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.service.inventory.VaccineInventoryDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


@Controller
@RequestMapping(value = "/vaccine/inventory/dashboard")
public class VaccineInventoryDashboardController extends BaseController {

    @Autowired
    VaccineInventoryDashboardService service;

    @RequestMapping(value = "sync", method = RequestMethod.PUT)
    public ResponseEntity<OpenLmisResponse> syncEquipments(HttpServletRequest request) {
        try {
            service.updateNonFunctionalEquipments();
        } catch (DataException e) {
            return OpenLmisResponse.error(e, BAD_REQUEST);
        }
        return new OpenLmisResponse().response(OK);
    }

    @RequestMapping(value = "equipmentalerts", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getNonFunctionalAlerts(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("Alerts", service.getNonFunctionalAlerts(userId));
    }

}
