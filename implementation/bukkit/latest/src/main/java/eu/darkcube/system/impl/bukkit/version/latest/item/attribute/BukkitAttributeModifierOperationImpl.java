package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import eu.darkcube.system.bukkit.item.attribute.BukkitAttributeModifierOperation;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.attribute.AttributeModifier;

public record BukkitAttributeModifierOperationImpl(@NotNull AttributeModifier.Operation bukkitType) implements BukkitAttributeModifierOperation {
}
