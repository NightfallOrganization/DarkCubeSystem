/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger("AsyncExecutor");
    private static final Thread.UncaughtExceptionHandler UEC = (t, e) -> LOGGER.error("Uncaught Exception in Thread {}", t.getName(), e);
    private static ScheduledExecutorService scheduledService;
    private static ExecutorService cachedService;
    private static ExecutorService virtualService;

    @ApiStatus.Internal
    public static void start() {
        var factory = new DefaultThreadFactory();
        scheduledService = new WrappedScheduledService(Executors.newScheduledThreadPool(1, factory));
        cachedService = new WrappedService(Executors.newCachedThreadPool(factory));
        virtualService = new WrappedService(Executors.newVirtualThreadPerTaskExecutor());
    }

    @ApiStatus.Internal
    public static void stop() {
        cachedService.shutdown();
        scheduledService.shutdown();
        virtualService.shutdown();
    }

    private static Runnable wrap(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                LOGGER.error("Uncaught exception in Thread {}", Thread.currentThread().getName(), throwable);
            }
        };
    }

    private static <T> Callable<T> wrap(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Throwable throwable) {
                LOGGER.error("Uncaught exception in Thread {}", Thread.currentThread().getName(), throwable);
                throwable.setStackTrace(new StackTraceElement[0]);
                throw throwable;
            }
        };
    }

    public static ScheduledExecutorService scheduledService() {
        return scheduledService;
    }

    public static ExecutorService cachedService() {
        return cachedService;
    }

    public static ExecutorService virtualService() {
        return virtualService;
    }

    @Deprecated(forRemoval = true)
    public static ExecutorService service() {
        return cachedService;
    }

    private static class WrappedScheduledService extends WrappedService implements ScheduledExecutorService {
        private final ScheduledExecutorService delegate;

        public WrappedScheduledService(ScheduledExecutorService delegate) {
            super(delegate);
            this.delegate = delegate;
        }

        @Override
        public @NotNull ScheduledFuture<?> schedule(@NotNull Runnable command, long delay, @NotNull TimeUnit unit) {
            return delegate.schedule(wrap(command), delay, unit);
        }

        @Override
        public <V> @NotNull ScheduledFuture<V> schedule(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
            return delegate.schedule(wrap(callable), delay, unit);
        }

        @Override
        public @NotNull ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable command, long initialDelay, long period, @NotNull TimeUnit unit) {
            return delegate.scheduleAtFixedRate(wrap(command), initialDelay, period, unit);
        }

        @Override
        public @NotNull ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable command, long initialDelay, long delay, @NotNull TimeUnit unit) {
            return delegate.scheduleWithFixedDelay(wrap(command), initialDelay, delay, unit);
        }
    }

    private static class WrappedService implements ExecutorService {
        private final ExecutorService delegate;

        public WrappedService(ExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

        @Override
        public @NotNull List<Runnable> shutdownNow() {
            return delegate.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        public <T> @NotNull Future<T> submit(@NotNull Callable<T> task) {
            return delegate.submit(wrap(task));
        }

        @Override
        public <T> @NotNull Future<T> submit(@NotNull Runnable task, T result) {
            return delegate.submit(wrap(task), result);
        }

        @Override
        public @NotNull Future<?> submit(@NotNull Runnable task) {
            return delegate.submit(wrap(task));
        }

        @Override
        public <T> @NotNull List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return delegate.invokeAll(tasks.stream().map(AsyncExecutor::wrap).toList());
        }

        @Override
        public <T> @NotNull List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
            return delegate.invokeAll(tasks.stream().map(AsyncExecutor::wrap).toList(), timeout, unit);
        }

        @Override
        public <T> @NotNull T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return delegate.invokeAny(tasks.stream().map(AsyncExecutor::wrap).toList());
        }

        @Override
        public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.invokeAny(tasks.stream().map(AsyncExecutor::wrap).toList(), timeout, unit);
        }

        @Override
        public void close() {
            delegate.close();
        }

        @Override
        public void execute(@NotNull Runnable command) {
            delegate.execute(wrap(command));
        }
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
            t.setUncaughtExceptionHandler(UEC);
            if (t.isDaemon()) t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
