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
 *
 * @since 0.0.3
 * @todo #9:45min Add more tests for `SodgInstructions#textInstructions()` method.
 *  The method not only computes the number, but also performs XMIR-to-SODG transformations,
 *  and leaves footprint on the system. Would be nice to test as much as we can.
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
                            "\n",
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
}
