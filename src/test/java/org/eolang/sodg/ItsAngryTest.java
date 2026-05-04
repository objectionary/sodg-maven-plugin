/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link ItsAngry}.
 * @since 0.0.4
 */
@ExtendWith(MktmpResolver.class)
final class ItsAngryTest {

    @Test
    void processesXmirs(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "Instructions weren't generated, but they should",
            new ItsAngry(
                new ItsDefault(new Depot(temp.resolve("measures.csv").toFile())), true
            ).textInstructions(
                ItsAngryTest.writeXmir(
                    temp.resolve("app.xmir"),
                    String.join(
                        System.lineSeparator(),
                        "[] > app",
                        "  QQ.io.stdout \"application started!\" > @"
                    )
                ),
                temp.resolve("sodg")
            ),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void failsBrokenXmir(@Mktmp final Path temp) throws IOException {
        ItsAngryTest.writeXmir(temp.resolve("broken.xmir"), "#");
        MatcherAssert.assertThat(
            "Exception was not thrown, but it should be, since XMIR is broken",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new ItsAngry(
                    new ItsDefault(new Depot(temp.resolve("measures.csv").toFile())), true
                ).textInstructions(temp.resolve("broken.xmir"), temp.resolve("sodg"))
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("Failing SODG generation"),
                Matchers.containsString("broken")
            )
        );
    }

    @Test
    void processesBrokenXmir(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "Instructions were not generated, but they should",
            new ItsAngry(
                new ItsDefault(new Depot(temp.resolve("measures.csv").toFile())), false
            ).textInstructions(
                ItsAngryTest.writeXmir(
                    temp.resolve("safe.xmir"),
                    String.join(
                        System.lineSeparator(),
                        "# it's safe!",
                        "[] > safe",
                        "",
                        "[] > safe"
                    )
                ),
                temp.resolve("sodg")
            ),
            Matchers.equalTo(4)
        );
    }

    /**
     * Write the EO source as parsed XMIR bytes to the given target path.
     * @param target Path to write to
     * @param source The EO source text
     * @return The target path
     * @throws IOException if write fails
     */
    private static Path writeXmir(final Path target, final String source) throws IOException {
        return Files.write(
            target,
            new EoSyntax(source).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
    }
}
