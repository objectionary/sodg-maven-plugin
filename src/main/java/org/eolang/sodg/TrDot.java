/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;
import com.yegor256.xsline.TrLogged;
import java.util.logging.Level;

/**
 * Convert to DOT.
 *
 * @since 0.1
 */
final class TrDot extends TrEnvelope {

    /**
     * Ctor.
     * @param level Logging level.
     */
    TrDot(final Level level) {
        super(
            new TrLogged(
                new TrFast(
                    new TrClasspath<>(
                        "/org/eolang/maven/sodg-to/normalize-attrs.xsl",
                        "/org/eolang/maven/sodg-to/to-dot.xsl"
                    ).back(),
                    Sodg.class
                ),
                Sodg.class,
                level
            )
        );
    }
}
