/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import eu.darkcube.system.annotations.Api;

@Api public final class Quaternion {
    public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private float x;
    private float y;
    private float z;
    private float w;

    @Api public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Api public Quaternion(Vector3f axis, float angle, boolean degrees) {
        if (degrees) {
            angle *= ((float) Math.PI / 180F);
        }

        var f = sin(angle / 2.0F);
        this.x = axis.getX() * f;
        this.y = axis.getY() * f;
        this.z = axis.getZ() * f;
        this.w = cos(angle / 2.0F);
    }

    @Api public Quaternion(float xAngle, float yAngle, float zAngle, boolean degrees) {
        if (degrees) {
            xAngle *= ((float) Math.PI / 180F);
            yAngle *= ((float) Math.PI / 180F);
            zAngle *= ((float) Math.PI / 180F);
        }

        var f = sin(0.5F * xAngle);
        var f1 = cos(0.5F * xAngle);
        var f2 = sin(0.5F * yAngle);
        var f3 = cos(0.5F * yAngle);
        var f4 = sin(0.5F * zAngle);
        var f5 = cos(0.5F * zAngle);
        this.x = f * f3 * f5 + f1 * f2 * f4;
        this.y = f1 * f2 * f5 - f * f3 * f4;
        this.z = f * f2 * f5 + f1 * f3 * f4;
        this.w = f1 * f3 * f5 - f * f2 * f4;
    }

    @Api public Quaternion(Quaternion quaternionIn) {
        this.x = quaternionIn.x;
        this.y = quaternionIn.y;
        this.z = quaternionIn.z;
        this.w = quaternionIn.w;
    }

    private static float cos(float a) {
        return (float) Math.cos(a);
    }

    private static float sin(float a) {
        return (float) Math.sin(a);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            var quaternion = (Quaternion) o;
            if (Float.compare(quaternion.x, this.x) != 0) {
                return false;
            } else if (Float.compare(quaternion.y, this.y) != 0) {
                return false;
            } else if (Float.compare(quaternion.z, this.z) != 0) {
                return false;
            } else {
                return Float.compare(quaternion.w, this.w) == 0;
            }
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        var i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return 31 * i + Float.floatToIntBits(this.w);
    }

    @Override public String toString() {
        var stringbuilder = new StringBuilder();
        stringbuilder.append("Quaternion[").append(this.getW()).append(" + ");
        stringbuilder.append(this.getX()).append("i + ");
        stringbuilder.append(this.getY()).append("j + ");
        stringbuilder.append(this.getZ()).append("k]");
        return stringbuilder.toString();
    }

    @Api public float getX() {
        return this.x;
    }

    @Api public float getY() {
        return this.y;
    }

    @Api public float getZ() {
        return this.z;
    }

    @Api public float getW() {
        return this.w;
    }

    @Api public void multiply(Quaternion quaternionIn) {
        var f = this.getX();
        var f1 = this.getY();
        var f2 = this.getZ();
        var f3 = this.getW();
        var f4 = quaternionIn.getX();
        var f5 = quaternionIn.getY();
        var f6 = quaternionIn.getZ();
        var f7 = quaternionIn.getW();
        this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
        this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
        this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
        this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
    }

    @Api public void multiply(float multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
        this.w *= multiplier;
    }

    @Api public void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    @Api public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Api public void normalize() {
        var f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
        if (f > 1.0E-6F) {
            var f1 = MathHelper.fastInvSqrt(f);
            this.x *= f1;
            this.y *= f1;
            this.z *= f1;
            this.w *= f1;
        } else {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
            this.w = 0.0F;
        }

    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}
