package org.openlmis.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class NewRegimesItem {
    private String regimeCode;
    private String regimeCategory;
}
