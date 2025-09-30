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
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link SodgInstructions}.
 *
 * @since 0.0.3
 */
@ExtendWith(MktmpResolver.class)
final class SodgInstructionsTest {

    @Test
    void returnsTotalInstructionsRendered(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "The number of total instructsion does not match with expected",
            new SodgInstructions(
                new Railway(temp.resolve("measures.csv").toFile()),
                SodgInstructionsTest.defaultConfig()
            ).total(
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

    /**
     * Default config.
     * @return Map, where parameter name is key and value is boolean flag
     */
    private static Map<String, Boolean> defaultConfig() {
        return new MapOf<>(
            new MapEntry<>("generateSodgXmlFiles", false),
            new MapEntry<>("generateXemblyFiles", false),
            new MapEntry<>("generateXemblyFiles", false),
            new MapEntry<>("generateGraphFiles", false),
            new MapEntry<>("generateDotFiles", false)
        );
    }
}
