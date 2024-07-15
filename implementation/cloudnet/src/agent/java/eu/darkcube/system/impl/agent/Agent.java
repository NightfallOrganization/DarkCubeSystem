package eu.darkcube.system.impl.agent;

import java.lang.instrument.Instrumentation;

class Agent {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        AgentAccess.instrumentation = instrumentation;
    }
}
