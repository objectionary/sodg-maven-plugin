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
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.set.SetOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link SodgFiles}.
 *
 * @since 0.0.3
 */
final class SodgFilesTest {

    @Test
    @ExtendWith(MktmpResolver.class)
    void generatesFilesWithDefaultConfig(@Mktmp final Path temp) throws IOException {
        final Path sodg = temp.resolve("sodg");
        new SodgFiles(
            SodgFilesTest.instructions(temp, false, false, false, false),
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
        MatcherAssert.assertThat(
            ".sodg file was not generated, but it should",
            Files.exists(
                sodg.resolve("org")
                    .resolve("eolang")
                    .resolve("sodg")
                    .resolve("examples")
                    .resolve("app.sodg")
            ),
            Matchers.equalTo(true)
        );
    }



    private static SodgInstructions instructions(
        final Path temp, final boolean xml, final boolean xembly, final boolean graph, final boolean dot
    ) {
        return new SodgInstructions(
            new Depot(temp.resolve("measures.csv").toFile()),
            new MapOf<>(
                new MapEntry<>("generateSodgXmlFiles", xml),
                new MapEntry<>("generateXemblyFiles", xembly),
                new MapEntry<>("generateGraphFiles", graph),
                new MapEntry<>("generateDotFiles", dot)
            )
        );
    }
}
