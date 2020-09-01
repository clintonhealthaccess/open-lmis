package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractDrugReportGenerator;
import org.springframework.stereotype.Component;

@Component(value = "tracerDrug")
public class TracerDrugReportGenerator extends AbstractDrugReportGenerator {

    private final static String WEEKLY_TRACER_SOH_CUBE = "vw_weekly_tracer_soh";
    private final static String WEEKLY_TRACER_SOH_CUBE_AFTER = "vw_tracer_drugs_weekly_stock_history_after_2018_12_28";
    private final static String WEEKLY_TRACER_SOH_CUBE_BEFORE = "vw_tracer_drugs_weekly_stock_history_before_2018_12_28";
    private final static String SPLIT_TIME = "2020,05,31";


    @Override
    protected String getSplitDate() {
        return SPLIT_TIME;
    }

    @Override
    protected String getBeforeFactUri() {
        return super.getBaseFactUri(WEEKLY_TRACER_SOH_CUBE_BEFORE);
    }

    @Override
    protected String getAfterFactUri() {
        return super.getBaseFactUri(WEEKLY_TRACER_SOH_CUBE_AFTER);
    }
}