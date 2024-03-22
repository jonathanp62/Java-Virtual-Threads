package net.jmp.demo.virtual.threads;

/*
 * (#)Main.java 0.3.0   03/22/2024
 * (#)Main.java 0.2.0   03/17/2024
 * (#)Main.java 0.1.0   03/15/2024
 *
 * @author    Jonathan Parker
 * @version   0.3.0
 * @since     0.1.0
 *
 * MIT License
 *
 * Copyright (c) 2024 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.concurrent.*;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

public final class Main {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    private Main() {
        super();
    }

    private void run() {
        this.logger.entry();

        this.logger.info("Begin starting up...");

        this.easy();
        this.builderOneThread();
        this.builderTwoThreads();
        this.executor();
        this.clientServer();

        this.logger.info("Done shutting down.");

        this.logger.exit();
    }

    private void easy() {
        this.logger.entry();

        // Thread.ofVirtual() returns Thread.Builder

        final var thread = Thread.ofVirtual().start(() -> this.logger.info("Hello"));

        try {
            thread.join();
        } catch (final InterruptedException ie) {
            this.logger.catching(ie);

            Thread.currentThread().interrupt(); // Restore the interrupt status
        }

        this.logger.exit();
    }

    private void builderOneThread() {
        this.logger.entry();

        final Thread.Builder builder = Thread.ofVirtual().name("My-Thread");

        final Runnable task = () -> {
            this.logger.info("Running thread");
        };

        final var t = builder.start(task);

        if (this.logger.isInfoEnabled())
            this.logger.info("Thread name: {}", t.getName());

        try {
            t.join();
        } catch (final InterruptedException ie) {
            this.logger.catching(ie);

            Thread.currentThread().interrupt(); // Restore the interrupt status
        }

        this.logger.exit();
    }

    private void builderTwoThreads() {
        this.logger.entry();

        final Thread.Builder builder = Thread.ofVirtual().name("worker-", 0);

        final Runnable task = () -> {
            if (this.logger.isInfoEnabled())
                this.logger.info("Thread ID: {}", Thread.currentThread().threadId());
        };

        final var worker0 = builder.start(task);
        final var worker1 = builder.start(task);

        try {
            worker0.join();
        } catch (final InterruptedException ie) {
            this.logger.catching(ie);

            Thread.currentThread().interrupt(); // Restore the interrupt status
        }

        try {
            worker1.join();
        } catch (final InterruptedException ie) {
            this.logger.catching(ie);

            Thread.currentThread().interrupt(); // Restore the interrupt status
        }

        if (this.logger.isInfoEnabled()) {
            this.logger.info("{} terminated", worker0.getName());
            this.logger.info("{} terminated", worker1.getName());
        }

        this.logger.exit();
    }

    private void executor() {
        this.logger.entry();

        try (final ExecutorService myExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            final Future<?> future = myExecutor.submit(() -> this.logger.info("Running task in a future"));

            try {
                future.get();

                this.logger.info("Future task completed OK");
            } catch (final InterruptedException | ExecutionException e) {
                this.logger.catching(e);

                if (e instanceof InterruptedException)
                    Thread.currentThread().interrupt(); // Restore the interrupt status
            }
        }

        this.logger.exit();
    }

    private void clientServer() {
        this.logger.entry();

        final var server = this.startServer();
        final var client = this.startClient();

        try {
            client.get();
            server.get();
        } catch (final InterruptedException | ExecutionException e) {
            this.logger.catching(e);

            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt(); // Restore the interrupt status
        }

        this.logger.exit();
    }

    private Future<Void> startServer() {
        this.logger.entry();

        Future<Void> future;

        try (final ExecutorService myExecutor = Executors.newFixedThreadPool(1)) {
            future = myExecutor.submit(() -> new Server().call());
        }

        this.logger.exit(future);

        return future;
    }

    private Future<Void> startClient() {
        this.logger.entry();

        Future<Void> future;

        try (final ExecutorService myExecutor = Executors.newFixedThreadPool(1)) {
            future = myExecutor.submit(() -> new Client().call());
        }

        this.logger.exit(future);

        return future;
    }

    public static void main(final String[] arguments) {
        new Main().run();
    }
}
