/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HexedUtf}.
 * @since 0.0.2
 */
final class HexedUtfTest {

    @Test
    void hexesString() {
        MatcherAssert.assertThat(
            "Hex value does not match with expected",
            new HexedUtf("foo").asString(),
            Matchers.equalTo("66-6F-6F")
        );
    }
}
