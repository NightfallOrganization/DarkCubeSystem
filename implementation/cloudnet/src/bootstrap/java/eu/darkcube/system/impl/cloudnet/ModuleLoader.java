package eu.darkcube.system.impl.cloudnet;

import java.util.Locale;

import dev.derklaro.aerogel.Element;
import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Name;
import dev.derklaro.aerogel.binding.BindingBuilder;
import dev.derklaro.aerogel.util.Qualifiers;
import dev.derklaro.reflexion.Reflexion;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.util.AsyncExecutor;

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
            ModuleHolder.implementation.start();
        } catch (Throwable t) {
            DarkCubeSystemModule.LOGGER.error("Failed to start", t);
        }
    }
}
