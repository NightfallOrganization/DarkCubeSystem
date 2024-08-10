package eu.darkcube.system.userapi;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Language;

public interface UserSettings {
    @Api
    boolean animations();

    @Api
    void animations(boolean animations);

    @Api
    boolean sounds();

    @Api
    void sounds(boolean sounds);

    @Api
    @NotNull
    Language language();

    @Api
    void language(@NotNull Language language);
}
