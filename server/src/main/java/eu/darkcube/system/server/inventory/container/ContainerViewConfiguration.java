package eu.darkcube.system.server.inventory.container;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;

public interface ContainerViewConfiguration {
    void configureView(@NotNull User user, @NotNull ContainerView view);

    @NotNull
    ContainerViewConfiguration EMPTY = (_, _) -> {
    };
}
