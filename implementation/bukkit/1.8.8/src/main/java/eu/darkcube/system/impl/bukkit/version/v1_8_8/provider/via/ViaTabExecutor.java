/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.provider.via;

import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.Language;

@ApiStatus.Internal
public class ViaTabExecutor {

    private static final Cache[] cached = Cache.create(16);
    private static int cid = 0;

    public ViaTabExecutor() {
    }

    public static int work(String commandLine, Suggestions suggestions) {
        var id = cid;
        cid = cid + 1;
        cid = cid % 16;
        var tooltips = new Component[suggestions.getList().size()];
        var ssuggestions = new String[suggestions.getList().size()];
        var range = suggestions.getRange();

        for (var i = 0; i < tooltips.length; i++) {
            var completion = suggestions.getList().get(i);

            ssuggestions[i] = completion.apply(commandLine).substring(range.getStart());

            Component hover = Component.empty();
            if (completion.getTooltip() != null && completion.getTooltip().getString() != null) {
                if (completion.getTooltip() instanceof Messages.MessageWrapper(var message, var c)) {
                    hover = hover.append(message.getMessage(Language.ENGLISH, c)); // TODO correct executor here instead of Language.ENGLISH???
                } else {
                    hover = hover.append(Component.text(completion.getTooltip().getString())).append(Component.newline());
                }
            }
            tooltips[i] = hover;
        }

        cached[id].data = new Data(range.getStart(), range.getLength(), ssuggestions, tooltips);
        return id;
    }

    public static @Nullable Data take(int id) {
        var cache = cached[id];
        var a = cache.data;
        if (a != null) cache.data = null;
        return a;
    }

    private static class Cache {
        private volatile Data data;

        public Cache() {
        }

        public static Cache[] create(int length) {
            var a = new Cache[length];
            for (var i = 0; i < a.length; i++) a[i] = new Cache();
            return a;
        }
    }

    public static class Data {
        private final int start;
        private final int length;
        private final String[] suggestions;
        private final Component[] tooltips;

        public Data(int start, int length, String[] suggestions, Component[] tooltips) {
            this.start = start;
            this.length = length;
            this.suggestions = suggestions;
            this.tooltips = tooltips;
        }

        public String[] suggestions() {
            return suggestions;
        }

        public int length() {
            return length;
        }

        public int start() {
            return start;
        }

        public Component[] tooltips() {
            return tooltips;
        }
    }
}
