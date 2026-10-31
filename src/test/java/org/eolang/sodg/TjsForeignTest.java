/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.tojos.MnJson;
import java.io.IOException;
import java.nio.file.Path;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link TjsForeign}.
 * @since 0.0.4
 */
@ExtendWith(MktmpResolver.class)
final class TjsForeignTest {

    @Test
    void skipsTojoWithoutScope(@Mktmp final Path temp) throws IOException {
        final Path foreigns = temp.resolve("foreigns.json");
        new MnJson(foreigns).write(
            new ListOf<>(
                new MapOf<String, String>(
                    new MapEntry<>("id", "org.eolang.io.stdout"),
                    new MapEntry<>("xmir", temp.resolve("stdout.xmir").toString())
                ),
                new MapOf<String, String>(
                    new MapEntry<>("id", "org.eolang.number"),
                    new MapEntry<>("xmir", temp.resolve("number.xmir").toString()),
                    new MapEntry<>("scope", "compile")
                )
            )
        );
        final TjsForeign tojos = new TjsForeign(
            () -> Catalogs.INSTANCE.make(foreigns, "json"),
            () -> "compile"
        );
        MatcherAssert.assertThat(
            "a tojo with no scope must be skipped, not kill the goal",
            tojos.withXmir(),
            Matchers.hasSize(1)
        );
        tojos.close();
    }
}
