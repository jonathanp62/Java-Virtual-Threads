package net.jmp.demo.virtual.threads;

/*
 * (#)Tasks.java    0.4.0   03/23/2024
 *
 * @author    Jonathan Parker
 * @version   0.4.0
 * @since     0.4.0
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

import java.util.concurrent.Executors;

import java.util.stream.IntStream;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

final class Tasks implements Runnable {
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    Tasks() {
        super();
    }

    @Override
    public void run() {
        this.logger.entry();

        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> System.out.print(String.valueOf(i) + " "));
            });
        }  // executor.close() is called implicitly, and waits for the threads to complete

        System.out.println();

        this.logger.exit();
    }
}
