package eu.darkcube.system.server.item.attribute;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;

class AttributeModifierOperationProviderImpl {
    private static final AttributeModifierOperationProvider provider = InternalProvider.instance().instance(AttributeModifierOperationProvider.class);

    public static @NotNull AttributeModifierOperation of(@NotNull Object platformAttributeModifierOperation) {
        return provider.of(platformAttributeModifierOperation);
    }
}
