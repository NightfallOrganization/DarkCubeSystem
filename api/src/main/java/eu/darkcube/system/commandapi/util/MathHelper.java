/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import java.util.Random;
import java.util.UUID;

public class MathHelper {

    private static final Random RANDOM = new Random();
    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASINE_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    static {
        for (var i = 0; i < 257; ++i) {
            var d0 = i / 256.0D;
            var d1 = Math.asin(d0);
            MathHelper.COS_TAB[i] = Math.cos(d1);
            MathHelper.ASINE_TAB[i] = d1;
        }
    }

    public static float sin(float value) {
        return (float) Math.sin(value);
    }

    public static float cos(float value) {
        return (float) Math.cos(value);
    }

    public static float sqrt(double value) {
        return (float) Math.sqrt(value);
    }

    public static int floor(double value) {
        var i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static int abs(int value) {
        return Math.abs(value);
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        }
        return Math.min(num, max);
    }

    public static int nextInt(Random random, int minimum, int maximum) {
        return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static float wrapDegrees(float value) {
        var f = value % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static double wrapDegrees(double value) {
        var d0 = value % 360.0D;
        if (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        if (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return d0;
    }

    public static int getInt(String value, int defaultValue) {
        return MathHelper.toInt(value, defaultValue);
    }

    private static int toInt(String s, int defaultV) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultV;
        }
    }

    public static UUID getRandomUUID(Random rand) {
        var i = rand.nextLong() & -61441L | 16384L;
        var j = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
        return new UUID(i, j);
    }

    public static UUID getRandomUUID() {
        return MathHelper.getRandomUUID(MathHelper.RANDOM);
    }

    public static double atan2(double p_181159_0_, double p_181159_2_) {
        var d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        }
        var flag = p_181159_0_ < 0.0D;
        if (flag) {
            p_181159_0_ = -p_181159_0_;
        }

        var flag1 = p_181159_2_ < 0.0D;
        if (flag1) {
            p_181159_2_ = -p_181159_2_;
        }

        var flag2 = p_181159_0_ > p_181159_2_;
        if (flag2) {
            var d1 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d1;
        }

        var d9 = MathHelper.fastInvSqrt(d0);
        p_181159_2_ = p_181159_2_ * d9;
        p_181159_0_ = p_181159_0_ * d9;
        var d2 = MathHelper.FRAC_BIAS + p_181159_0_;
        var i = (int) Double.doubleToRawLongBits(d2);
        var d3 = MathHelper.ASINE_TAB[i];
        var d4 = MathHelper.COS_TAB[i];
        var d5 = d2 - MathHelper.FRAC_BIAS;
        var d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
        var d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
        var d8 = d3 + d7;
        if (flag2) {
            d8 = (Math.PI / 2D) - d8;
        }

        if (flag1) {
            d8 = Math.PI - d8;
        }

        if (flag) {
            d8 = -d8;
        }

        return d8;
    }

    public static float fastInvSqrt(float number) {
        var f = 0.5F * number;
        var i = Float.floatToIntBits(number);
        i = 1597463007 - (i >> 1);
        number = Float.intBitsToFloat(i);
        return number * (1.5F - f * number * number);
    }

    public static double fastInvSqrt(double number) {
        var d0 = 0.5D * number;
        var i = Double.doubleToRawLongBits(number);
        i = 6910469410427058090L - (i >> 1);
        number = Double.longBitsToDouble(i);
        return number * (1.5D - d0 * number * number);
    }

    public static double lerp(double pct, double start, double end) {
        return start + pct * (end - start);
    }

    @Deprecated public static float rotLerp(float p_226167_0_, float p_226167_1_, float p_226167_2_) {
        float f;
        f = p_226167_1_ - p_226167_0_;
        while (f < -180.0F) {
            f += 360.0F;
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return p_226167_0_ + p_226167_2_ * f;
    }

    @Deprecated public static float rotWrap(double p_226168_0_) {
        while (p_226168_0_ >= 180.0D) {
            p_226168_0_ -= 360.0D;
        }

        while (p_226168_0_ < -180.0D) {
            p_226168_0_ += 360.0D;
        }

        return (float) p_226168_0_;
    }

}
