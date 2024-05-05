/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class AsyncExecutor {

    private static ScheduledExecutorService scheduledService;
    private static ExecutorService cachedService;

    public static void start() {
        scheduledService = Executors.newScheduledThreadPool(1, new DefaultThreadFactory());
        cachedService = Executors.newCachedThreadPool(new DefaultThreadFactory());
    }

    public static void stop() {
        cachedService.shutdown();
        scheduledService.shutdown();
    }

    public static ScheduledExecutorService scheduledService() {
        return scheduledService;
    }

    public static ExecutorService cachedService() {
        return cachedService;
    }

    @Deprecated(forRemoval = true)
    public static ExecutorService service() {
        return cachedService;
    }

    /**
     * The default thread factory.
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = "AsyncExecutor-";
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            var t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
