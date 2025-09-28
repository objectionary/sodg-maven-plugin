/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.TrLambda;
import com.yegor256.xsline.Train;
import java.io.File;
import java.util.Iterator;

/**
 * Measured train.
 * @since 0.0.3
 */
final class TrMeasured implements Train<Shift> {
    /**
     * The origin.
     */
    private final Train<Shift> origin;

    /**
     * XSL measures.
     */
    private final File measures;

    /**
     * Ctor.
     * @param train Train
     * @param msrs XSL measures
     */
    TrMeasured(final Train<Shift> train, final File msrs) {
        this.origin = train;
        this.measures = msrs;
    }

    @Override
    public Train<Shift> with(final Shift element) {
        if (this.measures.getParentFile().mkdirs()) {
            Logger.debug(this, "Directory created for %[file]s", this.measures);
        }
        if (!this.measures.getParentFile().exists()) {
            throw new IllegalArgumentException(
                String.format(
                    "For some reason, the directory %s is absent, can't write measures to %s",
                    this.measures.getParentFile(),
                    this.measures
                )
            );
        }
        if (this.measures.isDirectory()) {
            throw new IllegalArgumentException(
                String.format(
                    "This is not a file but a directory, can't write to it: %s",
                    this.measures
                )
            );
        }
        return new TrLambda(
            this.origin,
            shift -> new StMeasured(
                shift,
                this.measures.toPath()
            )
        ).with(element);
    }

    @Override
    public Train<Shift> empty() {
        return this.origin.empty();
    }

    @Override
    public Iterator<Shift> iterator() {
        return this.origin.iterator();
    }
}
