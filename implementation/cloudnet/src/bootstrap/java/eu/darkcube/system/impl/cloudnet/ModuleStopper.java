package eu.darkcube.system.impl.cloudnet;

import dev.derklaro.aerogel.Inject;
import eu.darkcube.system.util.AsyncExecutor;

class ModuleStopper {
    @Inject
    public ModuleStopper() {
    }

    @Inject
    public void stop() {
        if (ModuleHolder.implementation == null) return;
        ModuleHolder.implementation.stop();
        ModuleHolder.implementation = null;
        AsyncExecutor.stop();
    }
}
