/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.modules.contingencies;

import eu.itesla_project.contingency.tasks.ModificationTask;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface ActionElement {
	
	String getEquipmentId();

    ActionElementType getType();

    ModificationTask toTask();
    
    Number getImplementationTime();
    
    Number getAchievmentIndex();
    
    ModificationTask toTask(ActionParameters parameters);

}
