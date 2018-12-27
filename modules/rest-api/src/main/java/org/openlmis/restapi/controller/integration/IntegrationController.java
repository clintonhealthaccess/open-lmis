package org.openlmis.restapi.controller.integration;

import org.openlmis.restapi.controller.BaseController;
import org.openlmis.restapi.service.integration.IntegrationToFCService;
import org.openlmis.restapi.service.integration.ProductIntegrationFromFCService;
import org.openlmis.restapi.service.integration.ProgramIntegrationFromFCService;
import org.openlmis.restapi.service.integration.RegimenIntegrationFromFCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class IntegrationController extends BaseController {

    private IntegrationToFCService integrationToFCService;

    private ProgramIntegrationFromFCService programIntegrationFromFCService;

    private RegimenIntegrationFromFCService regimenIntegrationFromFCService;

    private ProductIntegrationFromFCService productIntegrationFromFCService;

    @Autowired
    public IntegrationController(IntegrationToFCService integrationToFCService,
                                 ProgramIntegrationFromFCService programIntegrationFromFCService,
                                 RegimenIntegrationFromFCService regimenIntegrationFromFCService,
                                 ProductIntegrationFromFCService productIntegrationFromFCService) {
        this.integrationToFCService = integrationToFCService;
        this.programIntegrationFromFCService = programIntegrationFromFCService;
        this.regimenIntegrationFromFCService = regimenIntegrationFromFCService;
        this.productIntegrationFromFCService = productIntegrationFromFCService;
    }

    @RequestMapping(value = "/rest-api/sync/page", method = RequestMethod.GET)
    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam String type) {
        return ResponseEntity.ok(integrationToFCService.getPageInfo(fromStartDate, type));
    }

    @RequestMapping(value ="/rest-api/sync/sohs", method = RequestMethod.GET)
    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getSohByDate(fromStartDate, startPage));
    }

    @RequestMapping(value = "rest-api/sync/movs", method = RequestMethod.GET)
    public ResponseEntity getStockMovements(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getStockMovementsByDate(fromStartDate, startPage));
    }

    @RequestMapping(value = "/rest-api/sync/requisitions", method = RequestMethod.GET)
    public ResponseEntity getRequisitions(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getRequisitionsByDate(fromStartDate, startPage));
    }

    @RequestMapping(value = "/rest-api/sync/all", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void synAll(@RequestParam(required = false) String fromStartDate) {
        programIntegrationFromFCService.sycDataFromFC(fromStartDate);
        regimenIntegrationFromFCService.sycDataFromFC(fromStartDate);
    }

    @RequestMapping(value = "/rest-api/sync/program", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void synProgram(@RequestParam(required = false) String fromStartDate) {
        programIntegrationFromFCService.sycDataFromFC(fromStartDate);
    }

    @RequestMapping(value = "/rest-api/sync/regimen", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void synRegimen(@RequestParam(required = false) String fromStartDate) {
        regimenIntegrationFromFCService.sycDataFromFC(fromStartDate);
    }

    @RequestMapping(value = "/rest-api/sync/product", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void synProduct(@RequestParam(required = false) String fromStartDate) {
        productIntegrationFromFCService.sycDataFromFC(fromStartDate);
    }

}