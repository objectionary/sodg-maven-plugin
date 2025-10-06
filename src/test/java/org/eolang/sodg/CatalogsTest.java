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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link Catalogs}.
 *
 * @since 0.0.3
 */
final class CatalogsTest {

    @Test
    @ExtendWith(MktmpResolver.class)
    void makesInJson(@Mktmp final Path temp) throws IOException {
        MatcherAssert.assertThat(
            "Size of the returned tojos does not match with expected",
            Catalogs.INSTANCE.make(
                Files.write(
                    temp.resolve("foreigns.json"),
                    "[1, 2, 3]".getBytes(StandardCharsets.UTF_8)
                ),
                "json"
            ).select(t -> true).size(),
            Matchers.equalTo(3)
        );
    }
}
