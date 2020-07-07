/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.commons.util.trove.TBooleanArrayList;
import com.powsybl.iidm.network.*;

import java.util.List;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class SwitchImpl extends AbstractIdentifiable<Switch> implements Switch, MultiVariantObject {

    private final OperationalLimitsHolderImpl operationalLimitsHolder1;

    private final OperationalLimitsHolderImpl operationalLimitsHolder2;

    private final VoltageLevelExt voltageLevel;

    private final SwitchKind kind;

    private final TBooleanArrayList open;

    private final TBooleanArrayList retained;

    SwitchImpl(VoltageLevelExt voltageLevel,
               String id, String name, boolean fictitious, SwitchKind kind, final boolean open, boolean retained) {
        super(id, name, fictitious);
        this.voltageLevel = voltageLevel;
        this.kind = kind;
        int variantArraySize = voltageLevel.getNetwork().getVariantManager().getVariantArraySize();
        this.open = new TBooleanArrayList(variantArraySize);
        this.retained = new TBooleanArrayList(variantArraySize);
        for (int i = 0; i < variantArraySize; i++) {
            this.open.add(open);
            this.retained.add(retained);
        }
        operationalLimitsHolder1 = new OperationalLimitsHolderImpl(this, "limits1");
        operationalLimitsHolder2 = new OperationalLimitsHolderImpl(this, "limits2");
    }

    @Override
    public NetworkImpl getNetwork() {
        return voltageLevel.getNetwork();
    }

    @Override
    public VoltageLevelExt getVoltageLevel() {
        return voltageLevel;
    }

    @Override
    public SwitchKind getKind() {
        return kind;
    }

    @Override
    public boolean isOpen() {
        return open.get(getNetwork().getVariantIndex());
    }

    @Override
    public void setOpen(boolean open) {
        NetworkImpl network = getNetwork();
        int index = network.getVariantIndex();
        boolean oldValue = this.open.get(index);
        if (oldValue != open) {
            this.open.set(index, open);
            String variantId = network.getVariantManager().getVariantId(index);
            network.getListeners().notifyUpdate(this, "open", variantId, oldValue, open);
            voltageLevel.invalidateCache();
        }
    }

    @Override
    public boolean isRetained() {
        return retained.get(getNetwork().getVariantIndex());
    }

    @Override
    public void setRetained(boolean retained) {
        if (voltageLevel.getTopologyKind() != TopologyKind.NODE_BREAKER) {
            throw new ValidationException(this, "retain status is not modifiable in a non node/breaker voltage level");
        }
        NetworkImpl network = getNetwork();
        int index = network.getVariantIndex();
        boolean oldValue = this.retained.get(index);
        if (oldValue != retained) {
            this.retained.set(index, retained);
            String variantId = network.getVariantManager().getVariantId(index);
            network.getListeners().notifyUpdate(this, "retained", variantId, oldValue, retained);
            voltageLevel.invalidateCache();
        }
    }

    @Override
    public void setFictitious(boolean fictitious) {
        boolean oldValue = this.fictitious;
        if (oldValue != fictitious) {
            this.fictitious = fictitious;
            voltageLevel.invalidateCache();
            NetworkImpl network = getNetwork();
            network.getListeners().notifyUpdate(this, "fictitious", oldValue, fictitious);
        }
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        open.ensureCapacity(open.size() + number);
        open.fill(initVariantArraySize, initVariantArraySize + number, open.get(sourceIndex));
        retained.ensureCapacity(retained.size() + number);
        retained.fill(initVariantArraySize, initVariantArraySize + number, retained.get(sourceIndex));
    }

    @Override
    public void reduceVariantArraySize(int number) {
        open.remove(open.size() - number, number);
        retained.remove(retained.size() - number, number);
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        // nothing to do
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, final int sourceIndex) {
        for (int index : indexes) {
            open.set(index, open.get(sourceIndex));
            retained.set(index, retained.get(sourceIndex));
        }
    }

    @Override
    protected String getTypeDescription() {
        return "Switch";
    }

    @Override
    public List<OperationalLimits> getOperationalLimits1() {
        return operationalLimitsHolder1.getOperationalLimits();
    }

    @Override
    public <L extends OperationalLimits> L getOperationalLimits1(LimitType limitType, Class<L> limitClazz) {
        return operationalLimitsHolder1.getOperationalLimits(limitType, limitClazz);
    }

    @Override
    public CurrentLimitsAdder newCurrentLimits1() {
        return operationalLimitsHolder1.newCurrentLimits();
    }

    @Override
    public ApparentPowerLimitsAdder newApparentPowerLimits1() {
        return operationalLimitsHolder1.newApparentPowerLimits();
    }

    @Override
    public ActivePowerLimitsAdder newActivePowerLimits1() {
        return operationalLimitsHolder1.newActivePowerLimitsAdder();
    }

    @Override
    public VoltageLimitsAdder newVoltageLimits1() {
        return operationalLimitsHolder1.newVoltageLimits();
    }

    @Override
    public List<OperationalLimits> getOperationalLimits2() {
        return operationalLimitsHolder2.getOperationalLimits();
    }

    @Override
    public <L extends OperationalLimits> L getOperationalLimits2(LimitType limitType, Class<L> limitClazz) {
        return operationalLimitsHolder2.getOperationalLimits(limitType, limitClazz);
    }

    @Override
    public CurrentLimitsAdder newCurrentLimits2() {
        return operationalLimitsHolder2.newCurrentLimits();
    }

    @Override
    public ApparentPowerLimitsAdder newApparentPowerLimits2() {
        return operationalLimitsHolder2.newApparentPowerLimits();
    }

    @Override
    public ActivePowerLimitsAdder newActivePowerLimits2() {
        return operationalLimitsHolder2.newActivePowerLimitsAdder();
    }

    @Override
    public VoltageLimitsAdder newVoltageLimits2() {
        return operationalLimitsHolder2.newVoltageLimits();
    }
}
