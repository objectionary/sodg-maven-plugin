/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
import java.util.logging.Level;

/**
 * SODG.
 *
 * @since 0.1
 */
final class Sodg {

    /**
     * Before transformation.
     */
    private final XML before;

    /**
     * Transformation train.
     */
    private final Train<Shift> train;

    /**
     * Ctor.
     * @param xmir Xmir XML
     */
    Sodg(final XML xmir) {
        this(xmir, Level.INFO);
    }

    /**
     * Ctor.
     * @param before Xmir before transformation.
     * @param level Logging level
     */
    private Sodg(final XML before, final Level level) {
        this(before, new TrSodg(level));
    }

    /**
     * Ctor.
     * @param before Before transformation.
     * @param train Transformation steps.
     */
    private Sodg(final XML before, final Train<Shift> train) {
        this.before = before;
        this.train = train;
    }

    /**
     * Transform to SODG XML.
     * @return SODG XML
     */
    XML value() {
        return new Xsline(this.train).pass(this.before);
    }
}
