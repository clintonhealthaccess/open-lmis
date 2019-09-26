package org.openlmis.restapi.utils;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.Set;

public class KitProductFilterUtils {
  public static final Integer FILTER_THRESHOLD_VERSION = 87;
  public static final Integer KIT_CODE_CHANGE_VERSION = 86;
  /**
   * filter wrong kit product
   */
  public static final Set<String> WRONG_KIT_PRODUCTS_SET = Sets.newHashSet("SCOD10", "SCOD10-AL", "SCOD12", "SCOD12-AL");
  /**
   * filter right kit product
   */
  public static final Set<String> RIGHT_KIT_PRODUCTS_SET = Sets.newHashSet("26A01", "26B01", "26A02", "26B02");

  public static final Set<String> ALL_FILTER_KIT_PRODUCTS_SET = Sets.newHashSet("SCOD10", "SCOD10-AL", "SCOD12", "SCOD12-AL","26A01", "26B01", "26A02", "26B02");

  public static boolean isBiggerThanThresholdVersion(String currentVersion, Integer thresholdVersion) {
    return StringUtils.isNumeric(currentVersion)
        && Integer.valueOf(currentVersion) >= thresholdVersion;
  }
}
