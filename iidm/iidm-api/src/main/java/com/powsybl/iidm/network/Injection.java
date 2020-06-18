/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network;

import java.util.Collections;
import java.util.List;

/**
 * A equipment with one terminal.
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface Injection<I extends Injection<I>> extends Connectable<I> {

    /**
     * Get the terminal.
     */
    Terminal getTerminal();

    default List<OperationalLimits> getOperationalLimits() {
        return Collections.emptyList();
    }

    default <L extends OperationalLimits> L getOperationalLimits(LimitType type, Class<L> limitClazz) {
        return null;
    }

    default <A extends OperationalLimitsAdder> A newOperationalLimits(Class<A> limitClazz) {
        throw new UnsupportedOperationException();
    }
}
