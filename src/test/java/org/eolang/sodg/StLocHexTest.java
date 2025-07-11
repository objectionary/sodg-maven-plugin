/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Tests for {@link StLocHex}.
 *
 * @since 0.0.2
 */
final class StLocHexTest {

    @Test
    void replacesLocatorsWithTheirHexes() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Transformed XML does not match with expected XPaths",
            new StLocHex().apply(
                0,
                new XMLDocument(
                    new Xembler(
                        new Directives()
                            .add("object")
                            .add("o")
                            .attr("name", "foo")
                            .attr("loc", "Φ.org.eolang.f")
                            .add("o")
                            .attr("name", "λ")
                    ).xml()
                )
            ),
            XhtmlMatchers.hasXPaths(
                "/object/o[@lambda='6F-72-67-2E-65-6F-6C-61-6E-67-2E-66']"
            )
        );
    }
}
