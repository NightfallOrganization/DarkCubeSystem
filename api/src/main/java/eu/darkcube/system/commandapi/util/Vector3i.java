/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import eu.darkcube.system.annotations.Api;

@Api public class Vector3i implements Comparable<Vector3i> {
    @Api
    public static final Vector3i NULL_VECTOR = new Vector3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    @Api public Vector3i(int xIn, int yIn, int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    @Api public Vector3i(double xIn, double yIn, double zIn) {
        this(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vector3i vector3i)) {
            return false;
        } else {
            if (this.getX() != vector3i.getX()) {
                return false;
            } else if (this.getY() != vector3i.getY()) {
                return false;
            } else {
                return this.getZ() == vector3i.getZ();
            }
        }
    }

    @Override public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override public int compareTo(Vector3i p_compareTo_1_) {
        if (this.getY() == p_compareTo_1_.getY()) {
            return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
        }
        return this.getY() - p_compareTo_1_.getY();
    }

    @Api public int getX() {
        return this.x;
    }

    @Api protected void setX(int xIn) {
        this.x = xIn;
    }

    @Api public int getY() {
        return this.y;
    }

    @Api protected void setY(int yIn) {
        this.y = yIn;
    }

    @Api public int getZ() {
        return this.z;
    }

    @Api protected void setZ(int zIn) {
        this.z = zIn;
    }

    @Api public Vector3i up() {
        return this.up(1);
    }

    @Api public Vector3i up(int n) {
        return this.offset(Direction.UP, n);
    }

    @Api public Vector3i down() {
        return this.down(1);
    }

    @Api public Vector3i down(int n) {
        return this.offset(Direction.DOWN, n);
    }

    @Api public Vector3i offset(Direction facing, int n) {
        return n == 0 ? this : new Vector3i(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
    }

    @Api public Vector3i crossProduct(Vector3i vec) {
        return new Vector3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    @Api public boolean withinDistance(Vector3i vector, double distance) {
        return this.distanceSq(vector.getX(), vector.getY(), vector.getZ(), false) < distance * distance;
    }

    @Api public boolean withinDistance(IPosition position, double distance) {
        return this.distanceSq(position.getX(), position.getY(), position.getZ(), true) < distance * distance;
    }

    @Api public double distanceSq(Vector3i to) {
        return this.distanceSq(to.getX(), to.getY(), to.getZ(), true);
    }

    @Api public double distanceSq(IPosition position, boolean useCenter) {
        return this.distanceSq(position.getX(), position.getY(), position.getZ(), useCenter);
    }

    @Api public double distanceSq(double x, double y, double z, boolean useCenter) {
        var d0 = useCenter ? 0.5D : 0.0D;
        var d1 = this.getX() + d0 - x;
        var d2 = this.getY() + d0 - y;
        var d3 = this.getZ() + d0 - z;
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    @Api public int manhattanDistance(Vector3i vector) {
        float f = Math.abs(vector.getX() - this.getX());
        float f1 = Math.abs(vector.getY() - this.getY());
        float f2 = Math.abs(vector.getZ() - this.getZ());
        return (int) (f + f1 + f2);
    }

    @Api public int getCoordinate(Direction.Axis axis) {
        return axis.getCoordinate(this.x, this.y, this.z);
    }

    @Override public String toString() {
        return this.getCoordinatesAsString();
    }

    @Api public String getCoordinatesAsString() {
        return this.getX() + ", " + this.getY() + ", " + this.getZ();
    }
}
