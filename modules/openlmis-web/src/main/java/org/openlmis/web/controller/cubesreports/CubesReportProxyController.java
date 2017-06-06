package org.openlmis.web.controller.cubesreports;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.dto.BaseFeedDTO;
import org.openlmis.web.controller.cubesreports.validation.CubesAccessInfo;
import org.openlmis.web.controller.cubesreports.validation.CubesReportValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static java.net.URLDecoder.decode;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class CubesReportProxyController {

    private static final String CUBES_ADDRESS = "http://localhost:5000";
    private static final String CUBES_REQUEST_PREFIX = "\\/cubesreports";
    private static Logger logger = Logger.getLogger(CubesReportProxyController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CubesReportValidationService cubesReportValidationService;

    @RequestMapping(method = GET, value = "/cubesreports/**")
    public ResponseEntity redirect(HttpServletRequest request) throws UnsupportedEncodingException {
        String queryUri = request.getRequestURI().replaceFirst(CUBES_REQUEST_PREFIX, "");
        String queryString = request.getQueryString() == null ? "" : "?" + decode(request.getQueryString(), "UTF-8");

        CubesAccessInfo cubesAccessInfo = cubesReportValidationService.validate(queryUri, queryString);
        if (cubesAccessInfo.isValid()) {
            String cubesRequestUrl = CUBES_ADDRESS + queryUri + cubesAccessInfo.getCubesQueryString();
            logger.error("**********************cubesAccessInfo" + cubesAccessInfo.getCubesQueryString());
            logger.error("**********************url" + cubesRequestUrl);
            ResponseEntity r = restTemplate.exchange(cubesRequestUrl, HttpMethod.GET, EMPTY, String.class);
            logger.error("**********************status" + r.getStatusCode() + "-" + r.getBody().toString());
            return r;
        } else {
            return new ResponseEntity(FORBIDDEN);
        }
    }
}
