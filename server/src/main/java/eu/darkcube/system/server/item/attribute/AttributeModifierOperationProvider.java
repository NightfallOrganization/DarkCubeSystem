package eu.darkcube.system.server.item.attribute;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface AttributeModifierOperationProvider {
    @NotNull
    AttributeModifierOperation of(@NotNull Object platformAttributeModifierOperation);
}
