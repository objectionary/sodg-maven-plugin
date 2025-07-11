/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import org.cactoos.Text;

/**
 * Hex representation of the UTF string.
 * @since 0.0.2
 */
final class HexedUtf implements Text {
    /**
     * The text to transform.
     */
    private final String text;

    /**
     * Ctor
     * @param txt The text to transform
     */
    HexedUtf(final String txt) {
        this.text = txt;
    }

    @Override
    public String asString() {
        final StringJoiner out = new StringJoiner("-");
        for (final byte bty : this.text.getBytes(StandardCharsets.UTF_8)) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }
}
