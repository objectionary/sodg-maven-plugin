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
 *
 * @since 0.0.4
 */
@ExtendWith(MktmpResolver.class)
final class ItsAngryTest {

    @Test
    void processesXmirs(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "Instructions weren't generated, but they should",
            new ItsAngry(new ItsDefault(new Depot(temp.resolve("measures.csv").toFile())), true)
                .textInstructions(
                    Files.write(
                        temp.resolve("app.xmir"),
                        new EoSyntax(
                            String.join(
                                "\n",
                                "[] > app",
                                "  QQ.io.stdout \"application started!\" > @"
                            )
                        ).parsed().toString().getBytes(StandardCharsets.UTF_8)
                    ),
                    temp.resolve("sodg")
                ),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void failsBrokenXmir(@Mktmp final Path temp) {
        MatcherAssert.assertThat(
            "Exception was not thrown, but it should be, since XMIR is broken",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new ItsAngry(new ItsDefault(new Depot(temp.resolve("measures.csv").toFile())), true)
                    .textInstructions(
                        Files.write(
                            temp.resolve("broken.xmir"), new EoSyntax("#")
                                .parsed().toString().getBytes(StandardCharsets.UTF_8)
                        ),
                        temp.resolve("sodg")
                    )
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
            new ItsAngry(new ItsDefault(new Depot(temp.resolve("measures.csv").toFile())), false)
                .textInstructions(
                    Files.write(
                        temp.resolve("safe.xmir"), new EoSyntax("# it's safe!\n[] > safe\n\n[] > safe")
                            .parsed().toString().getBytes(StandardCharsets.UTF_8)
                    ),
                    temp.resolve("sodg")
                ),
            Matchers.equalTo(4)
        );
    }
}
