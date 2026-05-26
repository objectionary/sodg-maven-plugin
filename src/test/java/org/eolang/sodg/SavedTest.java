/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Saved}.
 * @since 0.0.3
 */
final class SavedTest {

    @Test
    void savesToPathWithoutParentDirectory() throws Exception {
        try {
            Files.deleteIfExists(Paths.get("saved-parentless.txt"));
            new Saved("hello", Paths.get("saved-parentless.txt")).value();
            MatcherAssert.assertThat(
                "Content must be saved to parentless path",
                Files.readString(Paths.get("saved-parentless.txt")),
                Matchers.equalTo("hello")
            );
        } finally {
            Files.deleteIfExists(Paths.get("saved-parentless.txt"));
        }
    }
}
