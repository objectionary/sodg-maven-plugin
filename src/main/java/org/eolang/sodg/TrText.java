/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;

/**
 * Convert to text.
 * @since 0.1
 */
final class TrText extends TrEnvelope {

    /**
     * Ctor.
     */
    TrText() {
        super(
            new TrFast(
                new TrClasspath<>(
                    "/org/eolang/maven/sodg-to/normalize-names.xsl",
                    "/org/eolang/maven/sodg-to/to-text.xsl"
                ).back(),
                Sodg.class
            )
        );
    }
}
