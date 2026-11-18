/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import java.time.Instant;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Disclaimer}.
 * @since 0.27
 */
final class DisclaimerTest {

    @Test
    void resolvesFromSourceDateEpoch() {
        MatcherAssert.assertThat(
            "Timestamp does not match SOURCE_DATE_EPOCH",
            Disclaimer.resolved("1700000000"),
            Matchers.equalTo(Instant.ofEpochSecond(1_700_000_000L))
        );
    }

    @Test
    void resolvesSameInstantForSameSourceDateEpoch() {
        MatcherAssert.assertThat(
            "Two resolutions with the same SOURCE_DATE_EPOCH must be equal",
            Disclaimer.resolved("1700000000"),
            Matchers.equalTo(Disclaimer.resolved("1700000000"))
        );
    }

    @Test
    void fallsBackToNowWhenSourceDateEpochIsAbsent() {
        MatcherAssert.assertThat(
            "Timestamp must fall back to current time when SOURCE_DATE_EPOCH is unset",
            Disclaimer.resolved(null),
            Matchers.lessThanOrEqualTo(Instant.now())
        );
    }
}
