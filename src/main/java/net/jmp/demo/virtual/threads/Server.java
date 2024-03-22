package net.jmp.demo.virtual.threads;

/*
 * (#)Server.java   0.3.0   03/22/2024
 *
 * @author    Jonathan Parker
 * @version   0.3.0
 * @since     0.3.0
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class Server implements Callable<Void> {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    private final Semaphore semaphore;
    private final int port;

    Server(final Semaphore semaphore, final int port) {
        super();

        this.semaphore = semaphore;
        this.port = port;
    }

    @Override
    public Void call() throws Exception {
        this.logger.entry();

        this.logger.info("Will listen on port {}", this.port);

        this.listen();

        this.logger.exit();

        return null;
    }

    private void listen() {
        this.logger.entry();

        final var latch = new CountDownLatch(1);

        var releasedSemaphore = false;

        try (final var serverSocket = new ServerSocket(this.port)) {
            while (latch.getCount() == 1) {
                if (!releasedSemaphore) {
                    this.semaphore.release();
                    this.logger.debug("Released semaphore");

                    releasedSemaphore = true;
                }

                final var clientSocket = serverSocket.accept();    // Accept incoming connections

                 /* Start a service thread */

                final var thread = Thread.ofVirtual().start(() -> {
                    try (
                            final var out = new PrintWriter(clientSocket.getOutputStream(), true);
                            final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    ) {
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            this.logger.info("Received from client: {}", inputLine);

                            if (inputLine.startsWith("exit")) {
                                this.logger.debug("Counting down latch");
                                latch.countDown();
                            }
                        }
                    } catch (final IOException ioe) {
                        this.logger.catching(ioe);
                    }
                });

                try {
                    thread.join();
                } catch (final InterruptedException ie) {
                    this.logger.catching(ie);

                    Thread.currentThread().interrupt(); // Restore the interrupt status
                }
            }
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
            this.logger.error("Exception caught when trying to listen on port {} or listening for a connection", this.port);
        }

        this.logger.exit();
    }
}
