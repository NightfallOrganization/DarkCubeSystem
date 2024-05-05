/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import java.util.function.Function;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

@Api public record MinMaxBoundsWrapped(Float min, Float max) {
    @Api
    public static final MinMaxBoundsWrapped UNBOUNDED = new MinMaxBoundsWrapped(null, null);
    @Api
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = Messages.ERROR_INTS_ONLY.newSimpleCommandExceptionType();

    @Api public static MinMaxBoundsWrapped fromReader(StringReader reader, boolean isFloatingPoint, Function<Float, Float> valueFunction) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
        }
        var i = reader.getCursor();
        var f = MinMaxBoundsWrapped.map(MinMaxBoundsWrapped.fromReader(reader, isFloatingPoint), valueFunction);
        Float f1;
        if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
            reader.skip();
            reader.skip();
            f1 = MinMaxBoundsWrapped.map(MinMaxBoundsWrapped.fromReader(reader, isFloatingPoint), valueFunction);
            if (f == null && f1 == null) {
                reader.setCursor(i);
                throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
            }
        } else {
            if (!isFloatingPoint && reader.canRead() && reader.peek() == '.') {
                reader.setCursor(i);
                throw MinMaxBoundsWrapped.ERROR_INTS_ONLY.createWithContext(reader);
            }

            f1 = f;
        }

        if (f == null && f1 == null) {
            reader.setCursor(i);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
        }
        return new MinMaxBoundsWrapped(f, f1);
    }

    private static Float fromReader(StringReader reader, boolean isFloatingPoint) throws CommandSyntaxException {
        var i = reader.getCursor();

        while (reader.canRead() && MinMaxBoundsWrapped.isValidNumber(reader, isFloatingPoint)) {
            reader.skip();
        }

        var s = reader.getString().substring(i, reader.getCursor());
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException numberformatexception) {
            if (isFloatingPoint) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(reader, s);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, s);
        }
    }

    private static boolean isValidNumber(StringReader reader, boolean isFloatingPoint) {
        var c0 = reader.peek();
        if ((c0 < '0' || c0 > '9') && c0 != '-') {
            if (isFloatingPoint && c0 == '.') {
                return !reader.canRead(2) || reader.peek(1) != '.';
            }
            return false;
        }
        return true;
    }

    private static Float map(Float value, Function<Float, Float> valueFunction) {
        return value == null ? null : valueFunction.apply(value);
    }
}
