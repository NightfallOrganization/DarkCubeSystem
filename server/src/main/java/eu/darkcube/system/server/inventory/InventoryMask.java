package eu.darkcube.system.server.inventory;

import java.util.Arrays;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public class InventoryMask {
    @Api
    public static int @NotNull [] slots(@NotNull String mask, char character) {
        var simplified = mask.replace("\n", "");
        var size = 0;
        var chars = simplified.toCharArray();
        var slots = new int[chars.length];
        for (var slot = 0; slot < chars.length; slot++) {
            var c = chars[slot];
            if (c == character) {
                slots[size++] = slot;
            }
        }
        return Arrays.copyOf(slots, size);
    }
}
