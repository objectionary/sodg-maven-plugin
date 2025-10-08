/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.Mono;
import com.yegor256.tojos.TjDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link SodgFiles}.
 *
 * @since 0.0.4
 */
final class SodgFilesTest {

    @ParameterizedTest
    @MethodSource("fileMatrix")
    @ExtendWith(MktmpResolver.class)
    void generatesFilesWithDefaultConfig(
        final Map<String, Boolean> options, final List<String> extensions, @Mktmp final Path temp
    ) throws IOException {
        final Path sodg = temp.resolve("sodg");
        new SodgFiles(
            new SodgInstructions(
                new Depot(temp.resolve("measures.csv").toFile()),
                options
            ),
            new SetOf<>("**"),
            new SetOf<>()
        ).generate(
            new SodgFilesTest.TjsXmir(
                temp,
                new MapOf<>(
                    new MapEntry<>(
                        "app",
                        String.join(
                            "\n",
                            "[] > app",
                            "  QQ.io.stdout > @",
                            "    \"我是一名来自莫斯科的计算机科学家!\""
                        )
                    )
                )
            ).asTojos(),
            sodg
        );
        extensions.forEach(
            ext -> MatcherAssert.assertThat(
                "File was not generated, but it should",
                Files.exists(
                    sodg.resolve("org")
                        .resolve("eolang")
                        .resolve("sodg")
                        .resolve("examples")
                        .resolve(String.format("app%s", ext))
                ),
                Matchers.equalTo(true)
            )
        );
    }

    /**
     * Configuration-files matrix.
     *
     * @return Matrix as stream of arguments
     */
    private static Stream<Arguments> fileMatrix() {
        return Stream.of(
            Arguments.of(
                new MapOf<>(
                    new MapEntry<>("generateSodgXmlFiles", false),
                    new MapEntry<>("generateXemblyFiles", false),
                    new MapEntry<>("generateGraphFiles", false),
                    new MapEntry<>("generateDotFiles", false)
                ),
                new ListOf<>(".sodg")
            ),
            Arguments.of(
                new MapOf<>(
                    new MapEntry<>("generateSodgXmlFiles", true),
                    new MapEntry<>("generateXemblyFiles", false),
                    new MapEntry<>("generateGraphFiles", false),
                    new MapEntry<>("generateDotFiles", false)
                ),
                new ListOf<>(".sodg", ".sodg.xml")
            )
        );
    }

    /**
     * XMIR tojos.
     * @since 0.0.3
     */
    private static final class TjsXmir {

        /**
         * The home.
         */
        private final Path home;

        /**
         * The programs.
         */
        private final Map<String, String> programs;

        /**
         * Ctor.
         * @param hme Home
         * @param prgrms Programs
         */
        TjsXmir(final Path hme, final Map<String, String> prgrms) {
            this.home = hme;
            this.programs = prgrms;
        }

        /**
         * As tojos.
         * @return Collection of tojos.
         * @throws IOException if I/O operation fails
         */
        Collection<TjForeign> asTojos() throws IOException {
            try (Mono mono = new MnCsv(this.home.resolve("foreign.csv"))) {
                final Collection<Map<String, String>> foreigns = new ArrayList<>(16);
                this.programs.forEach(
                    (name, sources) -> {
                        final Path xmir = this.home.resolve(String.format("%s.xmir", name));
                        try {
                            Files.write(
                                xmir, new EoSyntax(sources).parsed().toString().getBytes(
                                    StandardCharsets.UTF_8
                                )
                            );
                        } catch (final IOException exception) {
                            throw new IllegalStateException(
                                String.format("Failed to write XMIR to %s", xmir), exception
                            );
                        }
                        foreigns.add(
                            new MapOf<>(
                                new MapEntry<>(
                                    "id",
                                    String.format("org.eolang.sodg.examples.%s", name)
                                ),
                                new MapEntry<>("xmir", xmir.toString()),
                                new MapEntry<>("scope", "compile")
                            )
                        );
                    }
                );
                mono.write(foreigns);
                return new TjsForeign(() -> new TjDefault(mono), () -> "compile").withXmir();
            }
        }
    }

}
