/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api public enum Direction {
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vector3i(0, -1, 0)),

    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vector3i(0, 1, 0)),

    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vector3i(0, 0, -1)),

    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vector3i(0, 0, 1)),

    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vector3i(-1, 0, 0)),

    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vector3i(1, 0, 0));

    private static final Direction[] VALUES = Direction.values();
    private static final Map<String, Direction> NAME_LOOKUP = Arrays.stream(Direction.VALUES).collect(Collectors.toMap(Direction::getName2, (direction) -> direction));
    private static final Direction[] BY_INDEX = Arrays.stream(Direction.VALUES).sorted(Comparator.comparingInt((direction) -> direction.index)).toArray(Direction[]::new);
    private static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(Direction.VALUES).filter((direction) -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt((direction) -> direction.horizontalIndex)).toArray(Direction[]::new);
    private final int index;
    private final int opposite;
    private final int horizontalIndex;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vector3i directionVec;

    Direction(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, AxisDirection axisDirectionIn, Axis axisIn, Vector3i directionVecIn) {
        this.index = indexIn;
        this.horizontalIndex = horizontalIndexIn;
        this.opposite = oppositeIn;
        this.name = nameIn;
        this.axis = axisIn;
        this.axisDirection = axisDirectionIn;
        this.directionVec = directionVecIn;
    }

    @Api public static Direction byName(String name) {
        return name == null ? null : Direction.NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
    }

    @Api public static Direction byIndex(int index) {
        return Direction.BY_INDEX[MathHelper.abs(index % Direction.BY_INDEX.length)];
    }

    @Api public static Direction byHorizontalIndex(int horizontalIndexIn) {
        return Direction.BY_HORIZONTAL_INDEX[MathHelper.abs(horizontalIndexIn % Direction.BY_HORIZONTAL_INDEX.length)];
    }

    @Api public static Direction fromAngle(double angle) {
        return Direction.byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
    }

    @Api public static Direction getFacingFromAxisDirection(Axis axisIn, AxisDirection axisDirectionIn) {
        return switch (axisIn) {
            case X -> axisDirectionIn == AxisDirection.POSITIVE ? EAST : WEST;
            case Y -> axisDirectionIn == AxisDirection.POSITIVE ? UP : DOWN;
            default -> axisDirectionIn == AxisDirection.POSITIVE ? SOUTH : NORTH;
        };
    }

    @Api public static Direction getFacingFromVector(double x, double y, double z) {
        return Direction.getFacingFromVector((float) x, (float) y, (float) z);
    }

    @Api public static Direction getFacingFromVector(float x, float y, float z) {
        var direction = NORTH;
        var f = Float.MIN_VALUE;

        for (var direction1 : Direction.VALUES) {
            var f1 = x * direction1.directionVec.getX() + y * direction1.directionVec.getY() + z * direction1.directionVec.getZ();
            if (f1 > f) {
                f = f1;
                direction = direction1;
            }
        }

        return direction;
    }

    @Api public static Direction getFacingFromAxis(AxisDirection axisDirectionIn, Axis axisIn) {
        for (var direction : Direction.VALUES) {
            if (direction.getAxisDirection() == axisDirectionIn && direction.getAxis() == axisIn) {
                return direction;
            }
        }

        throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
    }

    @Api public Quaternion getRotation() {
        var quaternion = Vector3f.XP.rotationDegrees(90.0F);
        return switch (this) {
            case DOWN -> Vector3f.XP.rotationDegrees(180.0F);
            case UP -> Quaternion.ONE.copy();
            case NORTH -> {
                quaternion.multiply(Vector3f.ZP.rotationDegrees(180.0F));
                yield quaternion;
            }
            case SOUTH -> quaternion;
            case WEST -> {
                quaternion.multiply(Vector3f.ZP.rotationDegrees(90.0F));
                yield quaternion;
            }
            default -> {
                quaternion.multiply(Vector3f.ZP.rotationDegrees(-90.0F));
                yield quaternion;
            }
        };
    }

    @Api public int getIndex() {
        return this.index;
    }

    @Api public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    @Api public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    @Api public Direction getOpposite() {
        return Direction.byIndex(this.opposite);
    }

    @Api public Direction rotateY() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    @Api public Direction rotateYCCW() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    @Api public int getXOffset() {
        return this.directionVec.getX();
    }

    @Api public int getYOffset() {
        return this.directionVec.getY();
    }

    @Api public int getZOffset() {
        return this.directionVec.getZ();
    }

    @Api public Vector3f toVector3f() {
        return new Vector3f(this.getXOffset(), this.getYOffset(), this.getZOffset());
    }

    @Api public String getName2() {
        return this.name;
    }

    @Api public Axis getAxis() {
        return this.axis;
    }

    @Api public float getHorizontalAngle() {
        return (this.horizontalIndex & 3) * 90;
    }

    @Api @Override public String toString() {
        return this.name;
    }

    @Api public String getString() {
        return this.name;
    }

    @Api public Vector3i getDirectionVec() {
        return this.directionVec;
    }

    @Api public boolean hasOrientation(float degrees) {
        var f = degrees * ((float) Math.PI / 180F);
        var f1 = -MathHelper.sin(f);
        var f2 = MathHelper.cos(f);
        return this.directionVec.getX() * f1 + this.directionVec.getZ() * f2 > 0.0F;
    }

    @Api public enum Axis implements Predicate<Direction> {
        X("x") {
            @Override public int getCoordinate(int x, int y, int z) {
                return x;
            }

            @Override public double getCoordinate(double x, double y, double z) {
                return x;
            }
        }, Y("y") {
            @Override public int getCoordinate(int x, int y, int z) {
                return y;
            }

            @Override public double getCoordinate(double x, double y, double z) {
                return y;
            }
        }, Z("z") {
            @Override public int getCoordinate(int x, int y, int z) {
                return z;
            }

            @Override public double getCoordinate(double x, double y, double z) {
                return z;
            }
        };

        private static final Axis[] VALUES = Axis.values();
        private static final Map<String, Axis> NAME_LOOKUP = Arrays.stream(Axis.VALUES).collect(Collectors.toMap(Axis::getName2, (p_199785_0_) -> p_199785_0_));
        private final String name;

        Axis(String nameIn) {
            this.name = nameIn;
        }

        @Api public static Axis byName(String name) {
            return Axis.NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
        }

        @Api public String getName2() {
            return this.name;
        }

        @Api public boolean isVertical() {
            return this == Y;
        }

        @Api public boolean isHorizontal() {
            return this == X || this == Z;
        }

        @Override public String toString() {
            return this.name;
        }

        @Override public boolean test(Direction p_test_1_) {
            return p_test_1_ != null && p_test_1_.getAxis() == this;
        }

        @Api public Plane getPlane() {
            return switch (this) {
                case X, Z -> Plane.HORIZONTAL;
                case Y -> Plane.VERTICAL;
            };
        }

        @Api public String getString() {
            return this.name;
        }

        @Api public abstract int getCoordinate(int x, int y, int z);

        @Api public abstract double getCoordinate(double x, double y, double z);
    }

    @Api public enum AxisDirection {
        @Api POSITIVE(1, "Towards positive"),

        @Api NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        @Api public int getOffset() {
            return this.offset;
        }

        @Override public String toString() {
            return this.description;
        }

        @Api public AxisDirection inverted() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    @Api public enum Plane implements Iterable<Direction>, Predicate<Direction> {
        @Api HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}),

        @Api VERTICAL(new Direction[]{Direction.UP, Direction.DOWN});

        private final Direction[] facingValues;

        Plane(Direction[] facingValuesIn) {
            this.facingValues = facingValuesIn;
        }

        @Override public boolean test(Direction direction) {
            return direction != null && direction.getAxis().getPlane() == this;
        }

        @Override public @NotNull Iterator<Direction> iterator() {
            return Arrays.asList(this.facingValues).iterator();
        }

        @Api public Stream<Direction> getDirectionValues() {
            return Arrays.stream(this.facingValues);
        }
    }
}
