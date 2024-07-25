package eu.darkcube.system.impl.minestom.item.attribute;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifierOperation;
import net.minestom.server.entity.attribute.AttributeOperation;

public record MinestomAttributeModifierOperationImpl(@NotNull AttributeOperation minestomType) implements MinestomAttributeModifierOperation {
}
