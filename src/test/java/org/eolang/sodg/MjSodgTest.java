/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.tojos.Tojo;
import com.yegor256.tojos.Tojos;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.xax.XtSticky;
import org.eolang.xax.XtYaml;
import org.eolang.xax.XtoryMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link MjSodg}.
 * @since 0.1
 */
@ExtendWith(MktmpResolver.class)
final class MjSodgTest {

    @Test
    void closesForeignTojosAfterExecution(@Mktmp final Path temp) throws Exception {
        final AtomicBoolean closed = new AtomicBoolean();
        final MjSodg mojo = new MjSodg(
            ignored -> new TjsForeign(
                () -> new MjSodgTest.EmptyTojos(closed), () -> "compile"
            )
        );
        mojo.targetDir = temp.toFile();
        mojo.xslMeasures = temp.resolve("xsl-measures.json").toFile();
        mojo.execute();
        MatcherAssert.assertThat(
            "foreign tojos should be closed after the mojo finishes",
            closed.get(),
            Matchers.is(true)
        );
    }

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
        for (final Path path : Files.walk(Paths.get("src/main/resources/org/eolang/maven/sodg"))
            .filter(Files::isRegularFile)
            .filter(file -> file.getFileName().toString().endsWith(".xsl"))
            .collect(Collectors.toList())) {
            MatcherAssert.assertThat(
                String.format("@id is wrong in: %s", path),
                XhtmlMatchers.xhtml(new UncheckedText(new TextOf(path)).asString()),
                XhtmlMatchers.hasXPath(
                    String.format(
                        "/xsl:stylesheet[@id='%s']",
                        path.getFileName().toString().replaceAll("\\.xsl$", "")
                    )
                )
            );
        }
    }

    /**
     * Empty tojos that records whether it was closed.
     * @since 0.0.0
     */
    private static final class EmptyTojos implements Tojos {

        /**
         * Closed marker.
         */
        private final AtomicBoolean closed;

        /**
         * Ctor.
         * @param clsd Closed marker
         */
        EmptyTojos(final AtomicBoolean clsd) {
            this.closed = clsd;
        }

        @Override
        public Tojo add(final String name) {
            throw new UnsupportedOperationException("Adding tojos is not expected");
        }

        @Override
        public List<Tojo> select(final Predicate<Tojo> filter) {
            return Collections.emptyList();
        }

        @Override
        public void close() {
            this.closed.set(true);
        }
    }
}
