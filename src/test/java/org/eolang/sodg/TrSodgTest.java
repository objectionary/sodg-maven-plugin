/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import java.util.logging.Level;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for {@link TrSodg}.
 *
 * @since 0.0.2
 */
final class TrSodgTest {

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
