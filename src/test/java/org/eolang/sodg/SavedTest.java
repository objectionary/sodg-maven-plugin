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
 * Tests for {@link Saved}.
 * @since 0.41.0
 */
@ExtendWith(MktmpResolver.class)
final class SavedTest {

    @Test
    void savesContentToFile(@Mktmp final Path temp) throws IOException {
        final Path target = temp.resolve("output.txt");
        MatcherAssert.assertThat(
            "Saved must return the target path",
            new Saved("hello", target).value(),
            Matchers.equalTo(target)
        );
    }

    @Test
    void createsParentDirectories(@Mktmp final Path temp) throws IOException {
        final Path target = temp.resolve("a").resolve("b").resolve("deep.txt");
        new Saved("nested", target).value();
        MatcherAssert.assertThat(
            "File must exist after save",
            target.toFile().exists(),
            Matchers.is(true)
        );
    }
}
