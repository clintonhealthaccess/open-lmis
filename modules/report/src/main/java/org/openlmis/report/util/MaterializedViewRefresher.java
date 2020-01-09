package org.openlmis.report.util;

import org.apache.commons.lang.StringUtils;
import org.openlmis.core.repository.mapper.MaterializedViewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MaterializedViewRefresher {

    private static final Logger log = LoggerFactory.getLogger(MaterializedViewRefresher.class);

    @Autowired
    private MaterializedViewMapper materializedViewMapper;
    @Value("${app.environment}")
    private String environment;

    private static final String PROD_ENVIRONMENT = "prod";

    public void refreshDailyMaterializedViews() {
        log.info("start to refresh daily materialized view,environment is {}", environment);
        if (!StringUtils.equalsIgnoreCase(PROD_ENVIRONMENT, environment)) {
            return;
        }
        materializedViewMapper.refreshPeriodMovements();
    }

    public void refreshHourlyMaterializedViews() {
        log.info("start to refresh hourly materialized view,environment is {}", environment);
        if (!StringUtils.equalsIgnoreCase(PROD_ENVIRONMENT, environment)) {
            return;
        }
        materializedViewMapper.refreshLotExpiryDate();
        materializedViewMapper.refreshDailyFullSOH();
        materializedViewMapper.refreshCMMEntries();
        materializedViewMapper.refreshStockouts();
        materializedViewMapper.refreshCarryStartDates();
        materializedViewMapper.refreshWeeklyTracerSOH();
        materializedViewMapper.refreshWeeklyNOSSOH();
    }
}
