package eu.darkcube.system.impl.standalone.userapi;

import java.util.UUID;

import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserAPI;

public class StandaloneUserAPI extends CommonUserAPI {
    @Override
    protected CommonUser loadUser(UUID uniqueId) {
        return new StandaloneUser(uniqueId);
    }
}
