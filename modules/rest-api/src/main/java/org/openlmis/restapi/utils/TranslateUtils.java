package org.openlmis.restapi.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.openlmis.restapi.domain.RequisitionServiceResponse;

public class TranslateUtils {


  public static Map<String,String> map = new HashMap<>();
  static {
    map.put("Maternity","Maternidade");
    map.put("Farmacy","Farmácia");
  }

  public static void translate(List<RequisitionServiceResponse> serviceResponse) {
    if (CollectionUtils.isNotEmpty(serviceResponse)) {
      for (RequisitionServiceResponse service : serviceResponse) {
        if (map.containsKey(service.getName())) {
          service.setName(map.get(service.getName()));
        }
      }
    }
  }


}
