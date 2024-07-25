package eu.darkcube.system.impl.bukkit.version.latest.item.attribute;

import eu.darkcube.system.bukkit.item.attribute.BukkitAttributeModifierOperation;
import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperationProvider;
import org.bukkit.attribute.AttributeModifier;

public class BukkitAttributeModifierOperationProvider implements AttributeModifierOperationProvider {
    private final BukkitAttributeModifierOperation[] operations = EnumConverter.convert(AttributeModifier.Operation.class, BukkitAttributeModifierOperation.class, BukkitAttributeModifierOperationImpl::new);

    @Override
    public @NotNull BukkitAttributeModifierOperation of(@NotNull Object platformAttributeModifierOperation) {
        if (platformAttributeModifierOperation instanceof BukkitAttributeModifierOperation operation) return operation;
        if (platformAttributeModifierOperation instanceof AttributeModifier.Operation operation) return operations[operation.ordinal()];
        throw new IllegalArgumentException("Invalid AttributeModifierOperation: " + platformAttributeModifierOperation);
    }
}
