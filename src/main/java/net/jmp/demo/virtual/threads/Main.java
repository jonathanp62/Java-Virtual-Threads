package net.jmp.demo.virtual.threads;

/*
 * (#)Main.java 0.4.0   03/23/2024
 * (#)Main.java 0.3.0   03/22/2024
 * (#)Main.java 0.2.0   03/17/2024
 * (#)Main.java 0.1.0   03/15/2024
 *
 * @author    Jonathan Parker
 * @version   0.4.0
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

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

public final class Main {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));
    private static final int PORT_FOR_CLIENT_SERVER = 8080;

    private Main() {
        super();
    }

    private void run() {
        this.logger.entry();

        this.logger.info("Begin starting up...");

        this.easy();
        this.builder();
        this.executor();
        this.clientServer();
        this.tasks();

        this.logger.info("Done shutting down.");

        this.logger.exit();
    }

    private void easy() {
        this.logger.entry();

        new Easy().run();

        this.logger.exit();
    }

    private void builder() {
        this.logger.entry();

        new Builder().run();

        this.logger.exit();
    }

    private void executor() {
        this.logger.entry();

        new Executor().run();

        this.logger.exit();
    }

    private void clientServer() {
        this.logger.entry();

        new ClientServer(PORT_FOR_CLIENT_SERVER).run();

        this.logger.exit();
    }

    private void tasks() {
        this.logger.entry();

        new Tasks().run();

        this.logger.exit();
    }

    public static void main(final String[] arguments) {
        new Main().run();
    }
}
