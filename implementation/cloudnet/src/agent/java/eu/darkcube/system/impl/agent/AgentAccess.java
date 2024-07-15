package eu.darkcube.system.impl.agent;

import java.lang.instrument.Instrumentation;

public class AgentAccess {
    static Instrumentation instrumentation;

    public static Instrumentation instrumentation() {
        return instrumentation;
    }
}
