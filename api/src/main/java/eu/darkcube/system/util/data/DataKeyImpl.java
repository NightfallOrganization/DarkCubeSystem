package eu.darkcube.system.util.data;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

record DataKeyImpl<DataType>(@NotNull Key key, @NotNull PersistentDataType<DataType> dataType) implements DataKey<DataType> {
}
