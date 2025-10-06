/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.tojos.MnJson;
import java.nio.file.Path;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link Catalogs}.
 *
 * @since 0.0.4
 */
final class CatalogsTest {

    @Test
    @ExtendWith(MktmpResolver.class)
    void makesInJson(@Mktmp final Path temp) {
        final Path foreigns = temp.resolve("foreigns.json");
        new MnJson(foreigns).write(
            new ListOf<>(
                new MapOf<>(
                    new MapEntry<>("id", "org.eolang.sodg.examples.应用程序")
                ),
                new MapOf<>(
                    new MapEntry<>("id", "org.eolang.io.stdout")
                ),
                new MapOf<>(
                    new MapEntry<>("id", "org.eolang.io.系统故障")
                )
            )
        );
        MatcherAssert.assertThat(
            "Size of the returned tojos does not match with expected",
            Catalogs.INSTANCE.make(foreigns, "json").select(t -> true).size(),
            Matchers.equalTo(3)
        );
    }
}
