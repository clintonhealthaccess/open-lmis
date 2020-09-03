package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractDrugReportGenerator;
import org.springframework.stereotype.Component;

@Component(value = "tracerDrug")
public class TracerDrugReportGenerator extends AbstractDrugReportGenerator {

    private final static String WEEKLY_TRACER_SOH_CUBE_AFTER = "vw_weekly_tracer_soh_after_2020_05_31";
    private final static String WEEKLY_TRACER_SOH_CUBE_BEFORE = "vw_weekly_tracer_soh_before_2020_05_31";
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