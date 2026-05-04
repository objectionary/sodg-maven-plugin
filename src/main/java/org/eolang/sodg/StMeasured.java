/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Shift;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Shift that measures and saves stats into a file.
 * This class was copy-pasted from objectionary/eo/eo-maven-plugin.
 * @since 0.30
 */
final class StMeasured implements Shift {

    /**
     * Origin shift.
     */
    private final Shift origin;

    /**
     * Log file.
     */
    private final Path path;

    /**
     * Ctor.
     * @param shift Origin shift
     * @param log Log file
     */
    StMeasured(final Shift shift, final Path log) {
        this.origin = shift;
        this.path = log;
    }

    @Override
    public String uid() {
        return this.origin.uid();
    }

    @Override
    public XML apply(final int position, final XML xml) {
        long elapsed = System.currentTimeMillis();
        try {
            return this.origin.apply(position, xml);
        } finally {
            elapsed = System.currentTimeMillis() - elapsed;
            this.recordDuration(elapsed);
        }
    }

    /**
     * Record the duration of the last shift.
     * @param duration The duration in milliseconds
     */
    private void recordDuration(final long duration) {
        try {
            Files.write(
                this.path,
                String.format("%s,%d%n", this.origin.uid(), duration).getBytes(
                    StandardCharsets.UTF_8
                ),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
