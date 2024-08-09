package eu.darkcube.system.impl.standalone.userapi;

import java.util.UUID;

import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;

public class StandaloneUser extends CommonUser {
    public StandaloneUser(UUID uniqueId) {
        super(query(uniqueId));
    }

    @Override
    public @NotNull Audience audience() {
        return AdventureSupport.adventureSupport().audienceProvider().player(uniqueId());
    }

    private static CommonUserData query(UUID uniqueId) {
        var persistentData = (UserStandalonePersistentDataStorage) CustomPersistentDataProvider.dataProvider().persistentData("userapi_users", Key.key("", uniqueId.toString()));
        var name = persistentData.name();
        return new CommonUserData(uniqueId, name, persistentData);
    }
}
