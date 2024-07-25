package eu.darkcube.system.impl.minestom.item.attribute;

import eu.darkcube.system.impl.common.EnumConverter;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.attribute.MinestomAttributeModifierOperation;
import eu.darkcube.system.server.item.attribute.AttributeModifierOperationProvider;
import net.minestom.server.entity.attribute.AttributeOperation;

public class MinestomAttributeOperationProviderImpl implements AttributeModifierOperationProvider {
    private final MinestomAttributeModifierOperation[] operations = EnumConverter.convert(AttributeOperation.class, MinestomAttributeModifierOperation.class, MinestomAttributeModifierOperationImpl::new);

    @Override
    public @NotNull MinestomAttributeModifierOperation of(@NotNull Object platformAttributeModifierOperation) {
        if (platformAttributeModifierOperation instanceof MinestomAttributeModifierOperation operation) return operation;
        if (platformAttributeModifierOperation instanceof AttributeOperation operation) return operations[operation.ordinal()];
        throw new IllegalArgumentException("Invalid AttributeOperation: " + platformAttributeModifierOperation);
    }
}
