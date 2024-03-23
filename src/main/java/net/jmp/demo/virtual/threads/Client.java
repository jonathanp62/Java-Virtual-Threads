package net.jmp.demo.virtual.threads;

/*
 * (#)Client.java   0.3.0   03/22/2024
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

import jakarta.validation.constraints.Positive;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class Client implements Callable<Void> {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));
    private final int port;
    private static final String HOST_NAME = "localhost";

    Client(final @Positive int port) {
        super();

        assert port > 0;

        this.port = port;
    }

    @Override
    public Void call() {
        this.logger.entry();

        this.logger.info("Will transmit on port {}", port);

        this.transmit();

        this.logger.exit();

        return null;
    }

    private void transmit() {
        this.logger.entry();

        this.start();
        this.natoAlphabet();
        this.exit();

        this.logger.exit();
    }

    private void start() {
        this.logger.entry();

        try (
                final var clientSocket = new Socket(HOST_NAME, this.port);
                final var out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            out.println("start");
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
        }

        this.logger.exit();
    }

    private void natoAlphabet() {
        this.logger.entry();

        final var list = List.of(
                "alpha",
                "bravo",
                "charlie",
                "delta",
                "echo",
                "foxtrot",
                "golf",
                "hotel",
                "india",
                "juliett",
                "kilo",
                "lima",
                "mike",
                "november",
                "oscar",
                "papa",
                "quebec",
                "romeo",
                "sierra",
                "tango",
                "uniform",
                "uniform",
                "victor",
                "whiskey",
                "xray",
                "zulu"
        );

        try (
                final var clientSocket = new Socket(HOST_NAME, this.port);
                final var out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            for (final var word : list)
                out.println(word);
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
        }

        this.logger.exit();
    }

    private void exit() {
        this.logger.entry();

        try (
                final var clientSocket = new Socket(HOST_NAME, this.port);
                final var out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            out.println("exit");    // Signal the server to exit
        } catch (final IOException ioe) {
            this.logger.catching(ioe);
        }

        this.logger.exit();
    }
}
