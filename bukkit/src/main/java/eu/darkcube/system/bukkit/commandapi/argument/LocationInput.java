/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;

public class LocationInput implements ILocationArgument {
    private final LocationPart x;
    private final LocationPart y;
    private final LocationPart z;

    public LocationInput(LocationPart x, LocationPart y, LocationPart z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static LocationInput parseInt(StringReader reader) throws CommandSyntaxException {
        var i = reader.getCursor();
        var locationpart = LocationPart.parseInt(reader);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            var locationpart1 = LocationPart.parseInt(reader);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                var locationpart2 = LocationPart.parseInt(reader);
                return new LocationInput(locationpart, locationpart1, locationpart2);
            }
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.setCursor(i);
        throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
    }

    public static LocationInput parseDouble(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
        var i = reader.getCursor();
        var locationpart = LocationPart.parseDouble(reader, centerIntegers);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            var locationpart1 = LocationPart.parseDouble(reader, false);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                var locationpart2 = LocationPart.parseDouble(reader, centerIntegers);
                return new LocationInput(locationpart, locationpart1, locationpart2);
            }
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.setCursor(i);
        throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
    }

    public static LocationInput current() {
        return new LocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D), new LocationPart(true, 0.0D));
    }

    @Override
    public Vector3d getPosition(CommandSource source) {
        var vector3d = source.getPos();
        return new Vector3d(this.x.get(vector3d.x), this.y.get(vector3d.y), this.z.get(vector3d.z));
    }

    @Override
    public Vector2f getRotation(CommandSource source) {
        var vector2f = source.getRotation();
        return new Vector2f((float) this.x.get(vector2f.x), (float) this.y.get(vector2f.y));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof LocationInput)) {
            return false;
        } else {
            var locationinput = (LocationInput) p_equals_1_;
            if (!this.x.equals(locationinput.x)) {
                return false;
            }
            return !this.y.equals(locationinput.y) ? false : this.z.equals(locationinput.z);
        }
    }

    @Override
    public int hashCode() {
        var i = this.x.hashCode();
        i = 31 * i + this.y.hashCode();
        return 31 * i + this.z.hashCode();
    }
}
