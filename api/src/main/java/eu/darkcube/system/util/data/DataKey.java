package eu.darkcube.system.util.data;

import java.util.function.Function;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface DataKey<DataType> {
    @Api
    @NotNull
    Key key();

    @Api
    @NotNull
    PersistentDataType<DataType> dataType();

    @Api
    @NotNull
    static <DataType> DataKey<DataType> of(@NotNull Key key, @NotNull PersistentDataType<DataType> dataType) {
        return new DataKeyImpl<>(key, dataType);
    }

    @Api
    @NotNull
    static <DataType> DataKey<DataType> of(@NotNull Key key, @NotNull Class<DataType> clazz, @NotNull Function<DataType, DataType> clone) {
        return of(key, PersistentDataTypes.simple(clazz, clone));
    }

    @Api
    @NotNull
    static <DataType> DataKey<DataType> ofImmutable(@NotNull Key key, @NotNull Class<DataType> clazz) {
        return of(key, PersistentDataTypes.simpleImmutable(clazz));
    }
}
