package com.marcinogon.ibmismartcamel.model;

import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

@FixedLengthRecord()
public class DtaqMessageFormat {

    // % processing unit used
    @DataField(pos = 1, length = 5, trim = true)
    private Integer unitUsed;

    // % current interactive performance
    @DataField(pos = 6, length = 5, trim = true)
    private Integer inerPerfomance;

    // % DB capability
    @DataField(pos = 11, length = 5, trim = true)
    private Integer dbCapability;

    public Integer getUnitUsed() {
        return unitUsed / 10;
    }

    public Integer getInerPerfomance() {
        return inerPerfomance;
    }

    public Integer getDbCapability() {
        return dbCapability;
    }

    @Override
    public String toString() {
        return "DtaqMessageFormat{" +
            "unitUsed=" + unitUsed +
            ", inerPerfomance=" + inerPerfomance +
            ", dbCapability=" + dbCapability +
            '}';
    }

}