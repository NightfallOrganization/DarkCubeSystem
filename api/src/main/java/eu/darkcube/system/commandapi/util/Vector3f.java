/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import eu.darkcube.system.annotations.Api;

@Api public final class Vector3f {
    @Api
    public static final Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
    @Api
    public static final Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
    @Api
    public static final Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
    @Api
    public static final Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
    @Api
    public static final Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
    @Api
    public static final Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
    private float x;
    private float y;
    private float z;

    @Api public Vector3f() {
    }

    @Api public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Api public Vector3f(Vector3d vecIn) {
        this((float) vecIn.x, (float) vecIn.y, (float) vecIn.z);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            var vector3f = (Vector3f) o;
            if (Float.compare(vector3f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(vector3f.y, this.y) != 0) {
                return false;
            } else {
                return Float.compare(vector3f.z, this.z) == 0;
            }
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        var i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        return 31 * i + Float.floatToIntBits(this.z);
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

    @Api public void mul(float multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
    }

    @Api public void mul(float mx, float my, float mz) {
        this.x *= mx;
        this.y *= my;
        this.z *= mz;
    }

    @Api public void clamp(float min, float max) {
        this.x = MathHelper.clamp(this.x, min, max);
        this.y = MathHelper.clamp(this.y, min, max);
        this.z = MathHelper.clamp(this.z, min, max);
    }

    @Api public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Api public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Api public void add(Vector3f vectorIn) {
        this.x += vectorIn.x;
        this.y += vectorIn.y;
        this.z += vectorIn.z;
    }

    @Api public void sub(Vector3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    @Api public float dot(Vector3f vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    @Api public boolean normalize() {
        var f = this.x * this.x + this.y * this.y + this.z * this.z;
        if (f < 1.0E-5D) {
            return false;
        }
        var f1 = MathHelper.fastInvSqrt(f);
        this.x *= f1;
        this.y *= f1;
        this.z *= f1;
        return true;
    }

    @Api public void cross(Vector3f vec) {
        var f = this.x;
        var f1 = this.y;
        var f2 = this.z;
        var f3 = vec.getX();
        var f4 = vec.getY();
        var f5 = vec.getZ();
        this.x = f1 * f5 - f2 * f4;
        this.y = f2 * f3 - f * f5;
        this.z = f * f4 - f1 * f3;
    }

    @Api public void transform(Quaternion quaternionIn) {
        var quaternion = new Quaternion(quaternionIn);
        quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
        var quaternion1 = new Quaternion(quaternionIn);
        quaternion1.conjugate();
        quaternion.multiply(quaternion1);
        this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ());
    }

    @Api public void lerp(Vector3f vectorIn, float pctIn) {
        var f = 1.0F - pctIn;
        this.x = this.x * f + vectorIn.x * pctIn;
        this.y = this.y * f + vectorIn.y * pctIn;
        this.z = this.z * f + vectorIn.z * pctIn;
    }

    @Api public Quaternion rotation(float valueIn) {
        return new Quaternion(this, valueIn, false);
    }

    @Api public Quaternion rotationDegrees(float valueIn) {
        return new Quaternion(this, valueIn, true);
    }

    @Api public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }
}
