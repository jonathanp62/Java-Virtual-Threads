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

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class Server implements Callable<Void> {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));
    private final Semaphore semaphore;
    private final int port;

    Server(final @NotNull Semaphore semaphore, final @Positive int port) {
        super();

        assert semaphore != null;
        assert port > 0;

        this.semaphore = semaphore;
        this.port = port;
    }

    @Override
    public Void call() {
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
                    this.logger.debug("Releasing semaphore...");

                    releasedSemaphore = true;

                    this.semaphore.release();
                }

                final var clientSocket = serverSocket.accept();    // Accept incoming connections

                handleClientRequest(clientSocket, latch);
            }
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
            this.logger.error("Exception caught when trying to listen on port {} or listening for a connection", this.port);
        }

        this.logger.exit();
    }

    private void handleClientRequest(final @NotNull Socket clientSocket, final @NotNull CountDownLatch latch) {
        this.logger.entry(clientSocket, latch);

        assert clientSocket != null;
        assert latch != null;

        final var thread = Thread.ofVirtual().start(() -> {
            try (final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
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

        this.logger.exit();
    }
}
