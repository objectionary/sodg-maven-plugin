/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.MktmpResolver;
import org.eolang.jucs.ClasspathSource;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link MjSodg}.
 *
 * @since 0.1
 * @todo #1:30min Enable the test {@link MjSodgTest#transformsThroughSheets}. The test was
 *  disabled when we got rid of "abstract" attribute in XMIR. We need to enable the test and make
 *  sure it works correctly.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@ExtendWith(MktmpResolver.class)
final class MjSodgTest {

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/maven/sodg-packs", glob = "**.yaml")
    void transformsThroughSheets(final String yaml) {
        MatcherAssert.assertThat(
            "passes with no exceptions",
            new XtSticky(new XtYaml(yaml)),
            new XtoryMatcher()
        );
    }
}
