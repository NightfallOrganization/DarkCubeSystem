/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet;

import java.lang.reflect.Type;
import java.util.Locale;

import dev.derklaro.aerogel.Element;
import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Name;
import dev.derklaro.aerogel.binding.BindingBuilder;
import dev.derklaro.aerogel.util.Qualifiers;
import dev.derklaro.reflexion.Reflexion;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.driver.network.rpc.defaults.object.DefaultObjectMapper;
import eu.cloudnetservice.driver.network.rpc.object.ObjectMapper;
import eu.cloudnetservice.driver.network.rpc.object.ObjectSerializer;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.key.KeyPattern;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AsyncExecutor;
import org.jetbrains.annotations.Nullable;

class ModuleLoader {
    @Inject
    public ModuleLoader() {
    }

    @Inject
    public void start(ComponentInfo componentInfo, @Name("module") InjectionLayer<?> injectionLayer) {
        try {
            var environmentName = componentInfo.environment().name();
            var simpleName = "DarkCubeSystem" + environmentName.substring(0, 1).toUpperCase(Locale.ROOT) + environmentName.substring(1);
            var className = getClass().getPackageName() + "." + environmentName + "." + simpleName;
            var cls = Class.forName(className);
            var reflexion = Reflexion.on(cls);

            injectionLayer.install(BindingBuilder.create().bind(Element.forType(String.class).requireAnnotation(Qualifiers.named("pluginName"))).toInstance(DarkCubeSystemModule.PLUGIN_NAME));
            ModuleHolder.implementation = injectionLayer.instance(reflexion.getWrappedClass().asSubclass(ModuleImplementation.class));
            AsyncExecutor.start();
            registerNetworkMappings();
            ModuleHolder.implementation.start();
        } catch (Throwable t) {
            DarkCubeSystemModule.LOGGER.error("Failed to start", t);
        }
    }

    private void registerNetworkMappings() {
        var GSON = new GsonBuilder().create();
        DefaultObjectMapper.DEFAULT_MAPPER.registerBinding(Key.class, new ObjectSerializer<Key>() {
            @Override
            @SuppressWarnings("PatternValidation")
            public @NotNull Key read(@NotNull DataBuf source, @NotNull Type type, @NotNull ObjectMapper caller) {
                @KeyPattern.Namespace var namespace = source.readString();
                @KeyPattern.Value var value = source.readString();
                return Key.key(namespace, value);
            }

            @Override
            public void write(@NotNull DataBuf.Mutable dataBuf, @NotNull Key object, @NotNull Type type, @NotNull ObjectMapper caller) {
                dataBuf.writeString(object.namespace()).writeString(object.value());
            }
        }, false);
        DefaultObjectMapper.DEFAULT_MAPPER.registerBinding(JsonElement.class, new ObjectSerializer<JsonElement>() {
            @Override
            public @Nullable JsonElement read(@NotNull DataBuf source, @NotNull Type type, @NotNull ObjectMapper caller) {
                return GSON.fromJson(source.readString(), JsonElement.class);
            }

            @Override
            public void write(@NotNull DataBuf.Mutable dataBuf, @NotNull JsonElement object, @NotNull Type type, @NotNull ObjectMapper caller) {
                dataBuf.writeString(GSON.toJson(object));
            }
        }, false);
        DefaultObjectMapper.DEFAULT_MAPPER.registerBinding(JsonObject.class, new ObjectSerializer<JsonObject>() {
            @Override
            public @Nullable JsonObject read(@NotNull DataBuf source, @NotNull Type type, @NotNull ObjectMapper caller) {
                return GSON.fromJson(source.readString(), JsonObject.class);
            }

            @Override
            public void write(@NotNull DataBuf.Mutable dataBuf, @NotNull JsonObject object, @NotNull Type type, @NotNull ObjectMapper caller) {
                dataBuf.writeString(GSON.toJson(object));
            }
        }, false);
    }
}
