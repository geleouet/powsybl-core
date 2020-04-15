/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface IdentifiableAdder<T extends IdentifiableAdder> {

    T setId(String id);

    T setEnsureIdUnicity(boolean ensureIdUnicity);

    T setName(String name);

    default T setFictitious(boolean fictitious) {
        throw new UnsupportedOperationException();
    }
}
