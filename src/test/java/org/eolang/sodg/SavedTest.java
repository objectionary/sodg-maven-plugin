/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.io.InputOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Saved}.
 *
 * @since 0.0.4
 */
final class SavedTest {

    @Test
    void savesToRelativeSingleSegmentPath() throws IOException {
        final Path target = Paths.get("saved-161.txt");
        try {
            MatcherAssert.assertThat(
                "File without a parent directory must be saved",
                Files.exists(new Saved(new InputOf("hello"), target).value()),
                Matchers.equalTo(true)
            );
        } finally {
            Files.deleteIfExists(target);
        }
    }
}
