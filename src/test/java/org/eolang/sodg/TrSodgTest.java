/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.util.logging.Level;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for {@link TrSodg}.
 * @since 0.0.2
 */
final class TrSodgTest {

    @Test
    void usesSourceDateEpochForTimeMeta() throws IOException {
        MatcherAssert.assertThat(
            "The time meta does not match SOURCE_DATE_EPOCH",
            new Xsline(
                new TrSodg(Level.FINEST, "1700000000")
            ).pass(
                new EoSyntax("[] > foo").parsed()
            ).xpath(
                "//sodg/i[@name='meta'][a[1]='$meta-time']/a[2]/text()"
            ),
            Matchers.contains(
                new HexedUtf("2023-11-14T22:13:20Z").asString()
            )
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/maven/sodg/sodg-packs/", glob = "**.yaml")
    void checksSodgPacks(final String yaml) {
        MatcherAssert.assertThat(
            "Doesn't tell the story as it's expected",
            new XtSticky(
                new XtYaml(
                    yaml,
                    eo -> new EoSyntax(eo).parsed(), new TrSodg(Level.FINEST)
                )
            ),
            new XtoryMatcher()
        );
    }
}
