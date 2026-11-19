/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.Tojo;
import com.yegor256.tojos.Tojos;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
 *
 * @since 0.1
 */
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
        final List<Path> sheets;
        try (
            Stream<Path> paths = Files.walk(
                Paths.get("src/main/resources/org/eolang/maven/sodg")
            )
        ) {
            sheets = paths
                .filter(Files::isRegularFile)
                .filter(file -> file.getFileName().toString().endsWith(".xsl"))
                .collect(Collectors.toList());
        }
        for (final Path path : sheets) {
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

    @Test
    void closesForeignTojosAfterExecution(@Mktmp final Path temp) throws Exception {
        final AtomicBoolean closed = new AtomicBoolean();
        final Tojos origin = new TjDefault(new MnCsv(temp.resolve("foreign.csv")));
        final MjSodg mojo = new MjSodg();
        mojo.catalog = () -> new Tojos() {
            @Override
            public Tojo add(final String name) {
                return origin.add(name);
            }

            @Override
            public List<Tojo> select(final Predicate<Tojo> filter) {
                return origin.select(filter);
            }

            @Override
            public void close() throws IOException {
                closed.set(true);
                origin.close();
            }
        };
        mojo.xslMeasures = temp.resolve("measures.csv").toFile();
        mojo.targetDir = temp.resolve("target").toFile();
        mojo.foreign = temp.resolve("foreign.csv").toFile();
        mojo.execute();
        MatcherAssert.assertThat(
            "the foreign tojos must be closed when the goal is done",
            closed.get(),
            Matchers.is(true)
        );
    }
}
