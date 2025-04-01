/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.Xtory;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test cases for {@link Sodg}.
 *
 * @since 0.1
 * @todo #5:90min Enable all the {@link Sodg} tests.
 *  Currently they are disabled because the SODG generation is outdated.
 *  We need to update the SODG generation and enable the tests.
 *  Also, you might need to use {@link #inclusion(Xtory)} method to
 *  get the correct inclusion for the {@link #generatesSodgForPacks(String)} test.
 */
final class SodgTest {

    @Test
    @Disabled
    void convertsToGraph() throws IOException {
        final StringBuilder program = new StringBuilder(1000);
        for (int idx = 0; idx < 40; ++idx) {
            for (int spc = 0; spc < idx; ++spc) {
                program.append("  ");
            }
            program.append("[x y z] > foo\n");
        }
        final XML graph = new Sodg(new EoSyntax(program.toString()).parsed()).value();
        MatcherAssert.assertThat(
            "Expected locator to exist in the generated graph",
            ".foo .foo",
            new ExistsIn(graph)
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/maven/sodgs/", glob = "**.yaml")
    @SuppressWarnings({
        "unchecked",
        "PMD.JUnitTestContainsTooManyAsserts",
        "PMD.ProhibitPlainJunitAssertionsRule"
    })
    @Disabled
    void generatesSodgForPacks(final String pack) throws IOException {
        final Xtory xtory = new XtSticky(new XtYaml(pack));
        Assumptions.assumeTrue(xtory.map().get("skip") == null, "This pack is skipped");
        final XML graph = new Sodg(
            new EoSyntax(xtory.map().get("input").toString()).parsed()
        ).value();
        final Collection<Executable> assertions = new LinkedList<>();
        for (final String loc : (Iterable<String>) xtory.map().get("locators")) {
            assertions.add(
                () -> MatcherAssert.assertThat(
                    "Expected locator to be present in the generated graph",
                    loc,
                    new ExistsIn(graph)
                )
            );
        }
        Assertions.assertAll(assertions);
    }

    /**
     * Get the inclusion string from the Xtory object.
     * @param xtory The Xtory object.
     * @return The inclusion string.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static String inclusion(final Xtory xtory) {
        Object inclusion = xtory.map().get("inclusion");
        if (inclusion == null) {
            inclusion = "**";
        } else {
            inclusion = inclusion.toString().substring(1, inclusion.toString().length() - 1);
        }
        return inclusion.toString();
    }
}
