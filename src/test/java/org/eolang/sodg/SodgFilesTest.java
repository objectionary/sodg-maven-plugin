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
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
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
        final Path xmir = temp.resolve("app.xmir");
        Files.write(
            xmir,
            new EoSyntax(
                String.join(
                    "\n",
                    "[] > app",
                    "  QQ.io.stdout > @",
                    "    \"我是一名来自莫斯科的计算机科学家!\""
                )
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        final Mono mono = new MnCsv(temp.resolve("foreign.csv"));
        mono.write(
            new ListOf<>(
                new MapOf<>(
                    new MapEntry<>("id", "org.eolang.sodg.examples.app"),
                    new MapEntry<>("xmir", xmir.toString()),
                    new MapEntry<>("scope", "compile")
                )
            )
        );
        final Path sodg = temp.resolve("sodg");
        new SodgFiles(
            new SodgInstructions(
                new Depot(temp.resolve("measures.csv").toFile()),
                new SodgDefaultConfig().value()
            ),
            new SetOf<>("**"),
            new SetOf<>()
        ).generate(new TjsForeign(() -> new TjDefault(mono), () -> "compile").withXmir(), sodg);
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
}
