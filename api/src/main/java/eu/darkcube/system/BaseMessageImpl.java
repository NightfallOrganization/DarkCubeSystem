package eu.darkcube.system;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Language;

class BaseMessageImpl {
    static final BaseMessage EMPTY = new BaseMessage.Transforming() {
        @Override
        public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
            return Component.empty();
        }
    };
}
