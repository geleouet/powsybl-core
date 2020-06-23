package com.powsybl.iidm.network;

public interface OperationalLimits {
    /**
     * Get the operational limits' type (can be APPARENT_POWER, CURRENT or VOLTAGE)
     */
    LimitType getLimitType();

    default void remove() {
        // do nothing
    }
}
