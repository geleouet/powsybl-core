/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import java.util.Objects;

import com.powsybl.iidm.network.TieLine;
import com.powsybl.iidm.network.ValidationException;
import com.powsybl.iidm.network.impl.util.Ref;
import com.powsybl.iidm.network.util.XnodeValuesComputation;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class TieLineImpl extends LineImpl implements TieLine {

    static class HalfLineImpl implements HalfLine {

        TieLineImpl parent;
        String id;
        String name;
        double r = Double.NaN;
        double x = Double.NaN;
        double g1 = Double.NaN;
        double g2 = Double.NaN;
        double b1 = Double.NaN;
        double b2 = Double.NaN;
        boolean fictitious = false;

        double initXnodeV = Double.NaN;
        double initXnodeAngle = Double.NaN;
        double initXnodeP = Double.NaN;
        double initXnodeQ = Double.NaN;

        TDoubleArrayList xnodeV;
        TDoubleArrayList xnodeAngle;
        TDoubleArrayList xnodeP;
        TDoubleArrayList xnodeQ;

        void setInitXnodeV(double initXnodeV) {
            this.initXnodeV = initXnodeV;
        }

        void setInitXnodeAngle(double initXnodeAngle) {
            this.initXnodeAngle = initXnodeAngle;
        }

        void setInitXnodeP(double initXnodeP) {
            this.initXnodeP = initXnodeP;
        }

        void setInitXnodeQ(double initXnodeQ) {
            this.initXnodeQ = initXnodeQ;
        }

        private void setParent(Ref<? extends VariantManagerHolder> network, TieLineImpl parent) {
            this.parent = parent;
            int variantArraySize = network.get().getVariantManager().getVariantArraySize();
            xnodeV = new TDoubleArrayList(variantArraySize);
            xnodeAngle = new TDoubleArrayList(variantArraySize);
            xnodeP = new TDoubleArrayList(variantArraySize);
            xnodeQ = new TDoubleArrayList(variantArraySize);
            for (int i = 0; i < variantArraySize; i++) {
                xnodeV.add(initXnodeV);
                xnodeAngle.add(initXnodeAngle);
                xnodeP.add(initXnodeP);
                xnodeQ.add(initXnodeQ);
            }
        }

        private void notifyUpdate(String attribute, Object oldValue, Object newValue) {
            if (Objects.nonNull(parent)) {
                parent.notifyUpdate(() -> getHalfLineAttribute() + "." + attribute, oldValue, newValue);
            }
        }

        @Override
        public String getId() {
            return id;
        }

        void setId(String id) {
            String oldValue = this.id;
            this.id = id;
            notifyUpdate("id", oldValue, id);
        }

        @Override
        public String getName() {
            return name == null ? id : name;
        }

        void setName(String name) {
            String oldValue = this.name;
            this.name = name;
            notifyUpdate("name", oldValue, name);
        }

        @Override
        public double getXnodeV() {
            return xnodeV.get(parent.getNetwork().getVariantIndex());
        }

        private void setXnodeV(double v) {
            int variantIndex = parent.getNetwork().getVariantIndex();
            double oldValue = xnodeV.set(variantIndex, v);
            notifyUpdate("xnodeV", oldValue, v);
        }

        @Override
        public double getXnodeAngle() {
            return xnodeAngle.get(parent.getNetwork().getVariantIndex());
        }

        private void setXnodeAngle(double angle) {
            int variantIndex = parent.getNetwork().getVariantIndex();
            double oldValue = xnodeAngle.set(variantIndex, angle);
            notifyUpdate("xnodeAngle", oldValue, angle);
        }

        @Override
        public double getXnodeP() {
            return xnodeP.get(parent.getNetwork().getVariantIndex());
        }

        private void setXnodeP(double p) {
            int variantIndex = parent.getNetwork().getVariantIndex();
            double oldValue = xnodeP.set(variantIndex, p);
            notifyUpdate("xnodeP", oldValue, p);
        }

        @Override
        public double getXnodeQ() {
            return xnodeQ.get(parent.getNetwork().getRef().get().getVariantIndex());
        }

        private void setXnodeQ(double q) {
            int variantIndex = parent.getNetwork().getVariantIndex();
            double oldValue = xnodeQ.set(variantIndex, q);
            notifyUpdate("xnodeQ", oldValue, q);
        }

        @Override
        public double getR() {
            return r;
        }

        @Override
        public HalfLineImpl setR(double r) {
            double oldValue = this.r;
            this.r = r;
            notifyUpdate("r", oldValue, r);
            return this;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public HalfLineImpl setX(double x) {
            double oldValue = this.x;
            this.x = x;
            notifyUpdate("x", oldValue, x);
            return this;
        }

        @Override
        public double getG1() {
            return g1;
        }

        @Override
        public HalfLineImpl setG1(double g1) {
            double oldValue = this.g1;
            this.g1 = g1;
            notifyUpdate("g1", oldValue, g1);
            return this;
        }

        @Override
        public double getG2() {
            return g2;
        }

        @Override
        public HalfLineImpl setG2(double g2) {
            double oldValue = this.g2;
            this.g2 = g2;
            notifyUpdate("g2", oldValue, g2);
            return this;
        }

        @Override
        public double getB1() {
            return b1;
        }

        @Override
        public HalfLineImpl setB1(double b1) {
            double oldValue = this.b1;
            this.b1 = b1;
            notifyUpdate("b1", oldValue, b1);
            return this;
        }

        @Override
        public double getB2() {
            return b2;
        }

        @Override
        public HalfLineImpl setB2(double b2) {
            double oldValue = this.b2;
            this.b2 = b2;
            notifyUpdate("b2", oldValue, b2);
            return this;
        }

        @Override
        public boolean isFictitious() {
            return fictitious;
        }

        @Override
        public HalfLineImpl setFictitious(boolean fictitious) {
            boolean oldValue = this.fictitious;
            this.fictitious = fictitious;
            notifyUpdate("fictitious", oldValue, fictitious);
            return this;
        }

        private String getHalfLineAttribute() {
            return this == parent.half1 ? "half1" : "half2";
        }

        void extendVariantArraySize(int number, int sourceIndex) {
            xnodeV.ensureCapacity(xnodeV.size() + number);
            xnodeAngle.ensureCapacity(xnodeAngle.size() + number);
            xnodeP.ensureCapacity(xnodeP.size() + number);
            xnodeQ.ensureCapacity(xnodeQ.size() + number);
            for (int i = 0; i < number; i++) {
                xnodeV.add(xnodeV.get(sourceIndex));
                xnodeAngle.add(xnodeAngle.get(sourceIndex));
                xnodeP.add(xnodeP.get(sourceIndex));
                xnodeQ.add(xnodeQ.get(sourceIndex));
            }
        }

        void reduceVariantArraySize(int number) {
            xnodeV.remove(xnodeV.size() - number, number);
            xnodeAngle.remove(xnodeAngle.size() - number, number);
            xnodeP.remove(xnodeP.size() - number, number);
            xnodeQ.remove(xnodeQ.size() - number, number);

        }

        void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
            for (int index : indexes) {
                xnodeV.set(index, xnodeV.get(sourceIndex));
                xnodeAngle.set(index, xnodeAngle.get(sourceIndex));
                xnodeP.set(index, xnodeP.get(sourceIndex));
                xnodeQ.set(index, xnodeQ.get(sourceIndex));
            }
        }
    }

    private final String ucteXnodeCode;

    private final HalfLineImpl half1;

    private final HalfLineImpl half2;

    TieLineImpl(Ref<? extends VariantManagerHolder> network, String id, String name, boolean fictitious, String ucteXnodeCode, HalfLineImpl half1, HalfLineImpl half2) {
        super(id, name, fictitious, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        this.ucteXnodeCode = ucteXnodeCode;
        this.half1 = attach(network, half1);
        this.half2 = attach(network, half2);
    }

    private HalfLineImpl attach(Ref<? extends VariantManagerHolder> network, HalfLineImpl half) {
        half.setParent(network, this);
        return half;
    }

    void computeAndSetXnodeV() {
        XnodeValuesComputation.computeAndSetXnodeV(this, (half, v) -> ((HalfLineImpl) half).setXnodeV(v));
    }

    void computeAndSetXnodeAngle() {
        XnodeValuesComputation.computeAndSetXnodeAngle(this, (half, angle) -> ((HalfLineImpl) half).setXnodeAngle(angle));
    }

    void computeAndSetXnodeP() {
        XnodeValuesComputation.computeAndSetXnodeP(this, (half, p) -> ((HalfLineImpl) half).setXnodeP(p));
    }

    void computeAndSetXnodeQ() {
        XnodeValuesComputation.computeAndSetXnodeQ(this, (half, q) -> ((HalfLineImpl) half).setXnodeQ(q));
    }

    @Override
    public boolean isTieLine() {
        return true;
    }

    @Override
    public String getUcteXnodeCode() {
        return ucteXnodeCode;
    }

    @Override
    public HalfLineImpl getHalf1() {
        return half1;
    }

    @Override
    public HalfLineImpl getHalf2() {
        return half2;
    }

    @Override
    public HalfLineImpl getHalf(Side side) {
        switch (side) {
            case ONE:
                return half1;
            case TWO:
                return half2;
            default:
                throw new AssertionError("Unknown branch side " + side);
        }
    }

    @Override
    public double getR() {
        return half1.getR() + half2.getR();
    }

    private ValidationException createNotSupportedForTieLines() {
        return new ValidationException(this, "direct modification of characteristics not supported for tie lines");
    }

    @Override
    public LineImpl setR(double r) {
        throw createNotSupportedForTieLines();
    }

    @Override
    public double getX() {
        return half1.getX() + half2.getX();
    }

    @Override
    public LineImpl setX(double x) {
        throw createNotSupportedForTieLines();
    }

    @Override
    public double getG1() {
        return half1.getG1() + half1.getG2();
    }

    @Override
    public LineImpl setG1(double g1) {
        throw createNotSupportedForTieLines();
    }

    @Override
    public double getB1() {
        return half1.getB1() + half1.getB2();
    }

    @Override
    public LineImpl setB1(double b1) {
        throw createNotSupportedForTieLines();
    }

    @Override
    public double getG2() {
        return half2.getG1() + half2.getG2();
    }

    @Override
    public LineImpl setG2(double g2) {
        throw createNotSupportedForTieLines();
    }

    @Override
    public double getB2() {
        return half2.getB1() + half2.getB2();
    }

    @Override
    public LineImpl setB2(double b2) {
        throw createNotSupportedForTieLines();
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        super.extendVariantArraySize(initVariantArraySize, number, sourceIndex);

        half1.extendVariantArraySize(number, sourceIndex);
        half2.extendVariantArraySize(number, sourceIndex);
    }

    @Override
    public void reduceVariantArraySize(int number) {
        super.reduceVariantArraySize(number);

        half1.reduceVariantArraySize(number);
        half2.reduceVariantArraySize(number);
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        super.allocateVariantArrayElement(indexes, sourceIndex);

        half1.allocateVariantArrayElement(indexes, sourceIndex);
        half2.allocateVariantArrayElement(indexes, sourceIndex);
    }
}
