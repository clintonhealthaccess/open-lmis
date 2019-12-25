package org.openlmis.restapi.controller;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Date;
import java.util.List;
import lombok.NoArgsConstructor;
import org.openlmis.LmisThreadLocalUtils;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.RequisitionServiceResponse;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestRequisitionServiceService;
import org.openlmis.restapi.utils.TranslateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@NoArgsConstructor
public class RestRequisitionServiceController extends BaseController{

    @Autowired
    private RestRequisitionServiceService restRequisitionServiceService;

    @RequestMapping(method = RequestMethod.GET, value = "/rest-api/services")
    public ResponseEntity<RestResponse> getLatestServices(@RequestParam(required = false) Long afterUpdatedTime,
                                                          @RequestParam(required = false) String programCode) {

        try {
            Date afterUpdatedTimeInDate = (afterUpdatedTime == null ? null : new Date(afterUpdatedTime));
            List<RequisitionServiceResponse> requisitionServices = restRequisitionServiceService.getRequisitionServices(afterUpdatedTimeInDate, programCode);
            if ("pt".equalsIgnoreCase(
                LmisThreadLocalUtils.getHeader(LmisThreadLocalUtils.HEADER_LANGUAGE))) {
                TranslateUtils.translate(requisitionServices);
            }
            RestResponse restResponse = new RestResponse("latestServices", requisitionServices);
            restResponse.addData("latestUpdatedTime", new Date());
            return new ResponseEntity<>(restResponse, HttpStatus.OK);
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }
    }
}