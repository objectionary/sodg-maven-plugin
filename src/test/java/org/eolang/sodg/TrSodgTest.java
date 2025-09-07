/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.util.logging.Level;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for {@link TrSodg}.
 *
 * @since 0.0.2
 */
final class TrSodgTest {

    @Test
    void transformsSimpleXmirToGraph() throws IOException {
        final XML xmir = new EoSyntax(
            String.join(
                "\n",
                "[qty price] > order",
                "  qty.mul > cost",
                "    price"
            )
        ).parsed();
        System.out.println(xmir);
        final XML after = new Xsline(new TrSodg(Level.FINEST)).pass(xmir);
        System.out.println(after);
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
