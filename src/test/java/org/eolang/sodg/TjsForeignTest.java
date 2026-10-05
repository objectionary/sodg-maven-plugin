/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link TjsForeign}.
 * @since 0.30
 */
@ExtendWith(MktmpResolver.class)
final class TjsForeignTest {

    @Test
    void closesResourcesInTryWithResources(@Mktmp final Path temp) throws IOException {
        try (TjsForeign tojos = new TjsForeign(
            () -> Catalogs.INSTANCE.make(
                temp.resolve("foreign.csv"), "csv"
            ),
            () -> "compile"
        )) {
            MatcherAssert.assertThat(
                "withXmir() must return empty for a fresh catalog",
                tojos.withXmir(),
                Matchers.empty()
            );
        }
    }

    @Test
    void closesCleanlyWithoutPriorAccess(@Mktmp final Path temp) throws IOException {
        try (TjsForeign tojos = new TjsForeign(
            () -> Catalogs.INSTANCE.make(
                temp.resolve("unused.csv"), "csv"
            ),
            () -> "test"
        )) {
            MatcherAssert.assertThat(
                "TjsForeign must be non-null inside try-with-resources",
                tojos,
                Matchers.notNullValue()
            );
        }
    }
}
