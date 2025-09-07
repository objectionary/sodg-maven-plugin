/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link MjSodg}.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@ExtendWith(MktmpResolver.class)
final class MjSodgTest {

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/maven/sodg/sodg-format", glob = "**.yaml")
    void transformsThroughSheets(final String yaml) {
        MatcherAssert.assertThat(
            "passes with no exceptions",
            new XtSticky(new XtYaml(yaml)),
            new XtoryMatcher()
        );
    }

    @Test
    void checksIdsInXslStylesheets() throws IOException {
        Files.walk(Paths.get("src/main/resources/org/eolang/maven/sodg"))
            .filter(Files::isRegularFile)
            .filter(file -> file.getFileName().toString().endsWith(".xsl"))
            .forEach(
                path -> MatcherAssert.assertThat(
                    String.format("@id is wrong in: %s", path),
                    XhtmlMatchers.xhtml(
                        new UncheckedText(new TextOf(path)).asString()
                    ),
                    XhtmlMatchers.hasXPath(
                        String.format(
                            "/xsl:stylesheet[@id='%s']",
                            path.getFileName().toString().replaceAll("\\.xsl$", "")
                        )
                    )
                )
            );
    }
}
