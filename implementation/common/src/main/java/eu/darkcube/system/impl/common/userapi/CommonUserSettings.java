package eu.darkcube.system.impl.common.userapi;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.UserSettings;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataTypes;

public final class CommonUserSettings implements UserSettings {
    private static final DataKey<Boolean> ANIMATIONS_KEY = DataKey.of(CommonUserAPI.key("animations"), PersistentDataTypes.BOOLEAN);
    private static final DataKey<Language> LANGUAGE_KEY = DataKey.of(CommonUserAPI.key("language"), PersistentDataTypes.enumType(Language.class));
    private static final DataKey<Boolean> SOUNDS_KEY = DataKey.of(CommonUserAPI.key("sounds"), PersistentDataTypes.BOOLEAN);
    private final PersistentDataStorage storage;

    public CommonUserSettings(PersistentDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean animations() {
        return storage.get(ANIMATIONS_KEY, () -> true);
    }

    @Override
    public void animations(boolean animations) {
        storage.set(ANIMATIONS_KEY, animations);
    }

    @Override
    public boolean sounds() {
        return storage.get(SOUNDS_KEY, () -> true);
    }

    @Override
    public void sounds(boolean sounds) {
        storage.set(SOUNDS_KEY, sounds);
    }

    @Override
    public @NotNull Language language() {
        return storage.get(LANGUAGE_KEY, () -> Language.DEFAULT);
    }

    @Override
    public void language(@NotNull Language language) {
        storage.set(LANGUAGE_KEY, language);
    }
}
