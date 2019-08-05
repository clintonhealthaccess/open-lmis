package org.openlmis.rnr.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class TherapeuticLinesItem extends LineItem {
    private String code;
    private Integer patientsOnTreatment;
    private Integer comunitaryPharmacy;

    @Override
    public boolean compareCategory(LineItem lineItem) {
        return false;
    }

    @Override
    public String getCategoryName() {
        return code;
    }

    @Override
    public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException {
        Field field = TherapeuticLinesItem.class.getDeclaredField(columnName);
        field.setAccessible(true);
        Object fieldValue = field.get(this);
        String value = (fieldValue == null) ? "" : fieldValue.toString();
        return value;
    }

    @Override
    public boolean isRnrLineItem() {
        return false;
    }
}
