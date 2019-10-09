package org.openlmis.restapi.controller;

import org.openlmis.LmisThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RestReSyncController {

  private static final Logger LOG = LoggerFactory.getLogger(RestReSyncController.class);
  private static final String template = "facility re-sycn data from the web server, facilityId is %s";

  @RequestMapping("/rest-api/re-sync")
  public ResponseEntity reSync() {
    LOG.warn(String.format(template, LmisThreadLocal.getFacilityId()));
    return ResponseEntity.ok(null);
  }

}
