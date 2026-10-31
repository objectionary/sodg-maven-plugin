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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link ItsDefault}.
 * @since 0.0.3
 */
@ExtendWith(MktmpResolver.class)
final class ItsDefaultTest {

    @Test
    void returnsTextInstructionsRendered(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "The number of total instructions does not match with expected",
            new ItsDefault(
                new Depot(temp.resolve("measures.csv").toFile())
            ).textInstructions(
                Files.write(
                    temp.resolve("foo.xmir"),
                    new EoSyntax(
                        String.join(
                            System.lineSeparator(),
                            "[] > foo",
                            "  QQ.io.stdout \"编程就是我的生命\" > @"
                        )
                    ).parsed().toString().getBytes(StandardCharsets.UTF_8)
                ),
                temp.resolve("foo.sodg")
            ),
            Matchers.equalTo(8)
        );
    }

    @Test
    void returnsTextInstructionsForMinimalObject(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "The number of instructions for a minimal EO object does not match with expected",
            new ItsDefault(
                new Depot(temp.resolve("measures.csv").toFile())
            ).textInstructions(
                Files.write(
                    temp.resolve("min.xmir"),
                    new EoSyntax("[] > foo")
                        .parsed()
                        .toString()
                        .getBytes(StandardCharsets.UTF_8)
                ),
                temp.resolve("min.sodg")
            ),
            Matchers.equalTo(4)
        );
    }

    @Test
    void returnsTextInstructionsForMultipleAttributes(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "The number of instructions for multiple attributes does not match with expected",
            new ItsDefault(
                new Depot(temp.resolve("measures.csv").toFile())
            ).textInstructions(
                Files.write(
                    temp.resolve("multi.xmir"),
                    new EoSyntax(
                        String.join(
                            System.lineSeparator(),
                            "[] > main",
                            "  QQ.io.stdout \"hello\" > out",
                            "  QQ.io.stderr \"world\" > err"
                        )
                    ).parsed().toString().getBytes(StandardCharsets.UTF_8)
                ),
                temp.resolve("multi.sodg")
            ),
            Matchers.equalTo(12)
        );
    }

    @Test
    void returnsTextInstructionsForNestedProgram(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "The number of instructions for a nested program does not match with expected",
            new ItsDefault(
                new Depot(temp.resolve("measures.csv").toFile())
            ).textInstructions(
                Files.write(
                    temp.resolve("nested.xmir"),
                    new EoSyntax(
                        String.join(
                            System.lineSeparator(),
                            "[] > main",
                            "  QQ.io.stdout \"hello\" > out"
                        )
                    ).parsed().toString().getBytes(StandardCharsets.UTF_8)
                ),
                temp.resolve("nested.sodg")
            ),
            Matchers.equalTo(8)
        );
    }
}
