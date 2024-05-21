/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import static eu.darkcube.system.commandapi.util.Direction.*;

import eu.darkcube.system.commandapi.util.Direction;
import eu.darkcube.system.commandapi.util.MathHelper;
import org.bukkit.entity.Entity;

public final class BukkitDirection {
    public static Direction[] getFacingDirections(Entity entityIn) {
        var f = entityIn.getLocation().getPitch() * ((float) Math.PI / 180F);
        var f1 = -entityIn.getLocation().getYaw() * ((float) Math.PI / 180F);
        var f2 = MathHelper.sin(f);
        var f3 = MathHelper.cos(f);
        var f4 = MathHelper.sin(f1);
        var f5 = MathHelper.cos(f1);
        var flag = f4 > 0.0F;
        var flag1 = f2 < 0.0F;
        var flag2 = f5 > 0.0F;
        var f6 = flag ? f4 : -f4;
        var f7 = flag1 ? -f2 : f2;
        var f8 = flag2 ? f5 : -f5;
        var f9 = f6 * f3;
        var f10 = f8 * f3;
        var direction = flag ? EAST : WEST;
        var direction1 = flag1 ? UP : DOWN;
        var direction2 = flag2 ? SOUTH : NORTH;
        if (f6 > f8) {
            if (f7 > f9) {
                return compose(direction1, direction, direction2);
            }
            return f10 > f7 ? compose(direction, direction2, direction1) : compose(direction, direction1, direction2);
        } else if (f7 > f10) {
            return compose(direction1, direction2, direction);
        } else {
            return f9 > f7 ? compose(direction2, direction, direction1) : compose(direction2, direction1, direction);
        }
    }

    private static Direction[] compose(Direction first, Direction second, Direction third) {
        return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
    }
}
