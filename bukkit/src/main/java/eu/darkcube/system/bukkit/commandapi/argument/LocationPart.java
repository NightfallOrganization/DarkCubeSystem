/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class LocationPart {
    public static final SimpleCommandExceptionType EXPECTED_DOUBLE = Messages.EXPECTED_DOUBLE.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType EXPECTED_INT = Messages.EXPECTED_INT.newSimpleCommandExceptionType();
    private final boolean relative;
    private final double value;

    public LocationPart(boolean relativeIn, double valueIn) {
        this.relative = relativeIn;
        this.value = valueIn;
    }

    public static LocationPart parseDouble(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
        } else if (!reader.canRead()) {
            throw LocationPart.EXPECTED_DOUBLE.createWithContext(reader);
        } else {
            var flag = LocationPart.isRelative(reader);
            var i = reader.getCursor();
            var d0 = reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
            var s = reader.getString().substring(i, reader.getCursor());
            if (flag && s.isEmpty()) {
                return new LocationPart(true, 0.0D);
            }
            if (!s.contains(".") && !flag && centerIntegers) {
                d0 += 0.5D;
            }

            return new LocationPart(flag, d0);
        }
    }

    public static LocationPart parseInt(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
        } else if (!reader.canRead()) {
            throw LocationPart.EXPECTED_INT.createWithContext(reader);
        } else {
            var flag = LocationPart.isRelative(reader);
            double d0;
            if (reader.canRead() && reader.peek() != ' ') {
                d0 = flag ? reader.readDouble() : (double) reader.readInt();
            } else {
                d0 = 0.0D;
            }

            return new LocationPart(flag, d0);
        }
    }

    public static boolean isRelative(StringReader reader) {
        boolean flag;
        if (reader.peek() == '~') {
            flag = true;
            reader.skip();
        } else {
            flag = false;
        }

        return flag;
    }

    public boolean isRelative() {
        return this.relative;
    }

    public double get(double coord) {
        return this.relative ? this.value + coord : this.value;
    }

    @Override
    public int hashCode() {
        var i = this.relative ? 1 : 0;
        var j = Double.doubleToLongBits(this.value);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof LocationPart locationpart)) {
            return false;
        } else {
            if (this.relative != locationpart.relative) {
                return false;
            }
            return Double.compare(locationpart.value, this.value) == 0;
        }
    }
}
