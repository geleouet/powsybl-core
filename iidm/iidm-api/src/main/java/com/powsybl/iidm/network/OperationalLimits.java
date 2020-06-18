package com.powsybl.iidm.network;

public interface OperationalLimits {

    /**
     * Get the physical unit of the value
     * @return unit
     */
    String getUnit();

    /**
     * Get the operational limits' type (can be APPARENT_POWER, CURRENT or VOLTAGE)
     */
    LimitType getLimitType();

    default void remove() {
        // do nothing
    }
}
