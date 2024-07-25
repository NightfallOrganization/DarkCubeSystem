package eu.darkcube.system.minestom.item.attribute;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperation;
import net.minestom.server.entity.attribute.AttributeOperation;

public interface MinestomAttributeModifierOperation extends AttributeModifierOperation {
    @NotNull
    AttributeOperation minestomType();
}
