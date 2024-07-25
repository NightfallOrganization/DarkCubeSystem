package eu.darkcube.system.server.item.attribute;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface AttributeModifierOperation {
    static @NotNull AttributeModifierOperation of(@NotNull Object platformAttributeModifierOperation) {
        return AttributeModifierOperationProviderImpl.of(platformAttributeModifierOperation);
    }
}
