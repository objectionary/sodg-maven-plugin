/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link SodgFiles}.
 *
 * @since 0.0.3
 */
final class SodgFilesTest {

    @ParameterizedTest
    @MethodSource("fileMatrix")
    @ExtendWith(MktmpResolver.class)
    void generatesFilesWithDefaultConfig(
        final Map<Map<String, Boolean>, List<String>> setup, @Mktmp final Path temp
    ) throws IOException {
        final Path sodg = temp.resolve("sodg");
        final Map<String, Boolean> config = setup.keySet().iterator().next();
        new SodgFiles(
            new SodgInstructions(
                new Depot(temp.resolve("measures.csv").toFile()),
                config
            ),
            new SetOf<>("**"),
            new SetOf<>()
        ).generate(
            new TjsXmir(
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
        setup.get(config).forEach(
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
                    new MapOf<>(
                        new MapEntry<>("generateSodgXmlFiles", false),
                        new MapEntry<>("generateXemblyFiles", false),
                        new MapEntry<>("generateGraphFiles", false),
                        new MapEntry<>("generateDotFiles", false)
                    ),
                    new ListOf<>(".sodg")
                )
            ),
            Arguments.of(
                new MapOf<>(
                    new MapOf<>(
                        new MapEntry<>("generateSodgXmlFiles", true),
                        new MapEntry<>("generateXemblyFiles", false),
                        new MapEntry<>("generateGraphFiles", false),
                        new MapEntry<>("generateDotFiles", false)
                    ),
                    new ListOf<>(".sodg", ".sodg.xml")
                )
            )
        );
    }
}
