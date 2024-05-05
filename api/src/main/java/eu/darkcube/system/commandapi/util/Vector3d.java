/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import java.util.EnumSet;

import eu.darkcube.system.annotations.Api;

@Api public class Vector3d implements IPosition {
    public static final Vector3d ZERO = new Vector3d(0.0D, 0.0D, 0.0D);
    public final double x;
    public final double y;
    public final double z;

    @Api public Vector3d(double xIn, double yIn, double zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    @Api public Vector3d(Vector3f vec) {
        this(vec.getX(), vec.getY(), vec.getZ());
    }

    @Api public static Vector3d unpack(int packed) {
        var d0 = (packed >> 16 & 255) / 255.0D;
        var d1 = (packed >> 8 & 255) / 255.0D;
        var d2 = (packed & 255) / 255.0D;
        return new Vector3d(d0, d1, d2);
    }

    @Api public static Vector3d copyCentered(Vector3i toCopy) {
        return new Vector3d(toCopy.getX() + 0.5D, toCopy.getY() + 0.5D, toCopy.getZ() + 0.5D);
    }

    @Api public static Vector3d copy(Vector3i toCopy) {
        return new Vector3d(toCopy.getX(), toCopy.getY(), toCopy.getZ());
    }

    @Api public static Vector3d copyCenteredHorizontally(Vector3i toCopy) {
        return new Vector3d(toCopy.getX() + 0.5D, toCopy.getY(), toCopy.getZ() + 0.5D);
    }

    @Api public static Vector3d copyCenteredWithVerticalOffset(Vector3i toCopy, double verticalOffset) {
        return new Vector3d(toCopy.getX() + 0.5D, toCopy.getY() + verticalOffset, toCopy.getZ() + 0.5D);
    }

    @Api public static Vector3d fromPitchYaw(Vector2f vec) {
        return fromPitchYaw(vec.x, vec.y);
    }

    @Api public static Vector3d fromPitchYaw(float pitch, float yaw) {
        var f = MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        var f1 = MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        var f2 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
        var f3 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
        return new Vector3d(f1 * f2, f3, f * f2);
    }

    @Api public Vector3d subtractReverse(Vector3d vec) {
        return new Vector3d(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    @Api public Vector3d normalize() {
        double d0 = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? ZERO : new Vector3d(this.x / d0, this.y / d0, this.z / d0);
    }

    @Api public double dotProduct(Vector3d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    @Api public Vector3d crossProduct(Vector3d vec) {
        return new Vector3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    @Api public Vector3d subtract(Vector3d vec) {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    @Api public Vector3d subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    @Api public Vector3d add(Vector3d vec) {
        return this.add(vec.x, vec.y, vec.z);
    }

    @Api public Vector3d add(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    @Api public boolean isWithinDistanceOf(IPosition pos, double distance) {
        return this.squareDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < distance * distance;
    }

    @Api public double distanceTo(Vector3d vec) {
        var d0 = vec.x - this.x;
        var d1 = vec.y - this.y;
        var d2 = vec.z - this.z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    @Api public double squareDistanceTo(Vector3d vec) {
        var d0 = vec.x - this.x;
        var d1 = vec.y - this.y;
        var d2 = vec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @Api public double squareDistanceTo(double xIn, double yIn, double zIn) {
        var d0 = xIn - this.x;
        var d1 = yIn - this.y;
        var d2 = zIn - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @Api public Vector3d scale(double factor) {
        return this.mul(factor, factor, factor);
    }

    @Api public Vector3d inverse() {
        return this.scale(-1.0D);
    }

    @Api public Vector3d mul(Vector3d vec) {
        return this.mul(vec.x, vec.y, vec.z);
    }

    @Api public Vector3d mul(double factorX, double factorY, double factorZ) {
        return new Vector3d(this.x * factorX, this.y * factorY, this.z * factorZ);
    }

    @Api public double length() {
        return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    @Api public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vector3d vector3d)) {
            return false;
        } else {
            if (Double.compare(vector3d.x, this.x) != 0) {
                return false;
            } else if (Double.compare(vector3d.y, this.y) != 0) {
                return false;
            } else {
                return Double.compare(vector3d.z, this.z) == 0;
            }
        }
    }

    @Override public int hashCode() {
        var j = Double.doubleToLongBits(this.x);
        var i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.y);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.z);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    @Override public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", this.x, this.y, this.z);
    }

    @Api public Vector3d rotatePitch(float pitch) {
        var f = MathHelper.cos(pitch);
        var f1 = MathHelper.sin(pitch);
        var y = this.y * f + this.z * f1;
        var z = this.z * f - this.y * f1;
        return new Vector3d(this.x, y, z);
    }

    @Api public Vector3d rotateYaw(float yaw) {
        var f = MathHelper.cos(yaw);
        var f1 = MathHelper.sin(yaw);
        var x = this.x * f + this.z * f1;
        var z = this.z * f - this.x * f1;
        return new Vector3d(x, this.y, z);
    }

    @Api public Vector3d rotateRoll(float roll) {
        var f = MathHelper.cos(roll);
        var f1 = MathHelper.sin(roll);
        var x = this.x * f + this.y * f1;
        var y = this.y * f - this.x * f1;
        return new Vector3d(x, y, this.z);
    }

    @Api public Vector3d align(EnumSet<Direction.Axis> axes) {
        var d0 = axes.contains(Direction.Axis.X) ? (double) MathHelper.floor(this.x) : this.x;
        var d1 = axes.contains(Direction.Axis.Y) ? (double) MathHelper.floor(this.y) : this.y;
        var d2 = axes.contains(Direction.Axis.Z) ? (double) MathHelper.floor(this.z) : this.z;
        return new Vector3d(d0, d1, d2);
    }

    @Api public double getCoordinate(Direction.Axis axis) {
        return axis.getCoordinate(this.x, this.y, this.z);
    }

    @Api @Override public final double getX() {
        return this.x;
    }

    @Api @Override public final double getY() {
        return this.y;
    }

    @Api @Override public final double getZ() {
        return this.z;
    }
}
