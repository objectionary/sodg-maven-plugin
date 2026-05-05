/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Train;
import java.nio.file.Path;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link Depot}.
 *
 * @since 0.0.3
 */
@ExtendWith(MktmpResolver.class)
final class DepotTest {

    @ParameterizedTest
    @ValueSource(strings = {"sodg", "dot", "xembly", "text", "finish"})
    void returnsTrainByName(final String name, @Mktmp final Path temp) {
        final Depot depot = new Depot(temp.resolve("measures.csv").toFile());
        MatcherAssert.assertThat(
            String.format("Train '%s' must be present in the depot", name),
            depot.train(name),
            Matchers.notNullValue()
        );
    }

    @Test
    void returnsNullForUnknownName(@Mktmp final Path temp) {
        final Depot depot = new Depot(temp.resolve("measures.csv").toFile());
        MatcherAssert.assertThat(
            "Unknown train name must yield null",
            depot.train("does-not-exist"),
            Matchers.nullValue()
        );
    }

    @Test
    void returnsTrainFromCustomMap() {
        final Train<Shift> train = new com.yegor256.xsline.TrDefault<>();
        final Map<String, Train<Shift>> trains = new MapOf<>(
            new MapEntry<>("custom", train)
        );
        final Depot depot = new Depot(trains);
        MatcherAssert.assertThat(
            "Custom map must expose its train under its key",
            depot.train("custom"),
            Matchers.sameInstance(train)
        );
    }

    @Test
    void createsParentDirectoryForMeasuresFile(@Mktmp final Path temp) {
        final Path measures = temp.resolve("nested").resolve("dir").resolve("measures.csv");
        new Depot(measures.toFile());
        MatcherAssert.assertThat(
            "Parent directory of measures file must be created",
            measures.getParent().toFile().isDirectory(),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsMeasuresPointingToDirectory(@Mktmp final Path temp) {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Depot(temp.toFile()),
            "Pointing measures to a directory must be rejected"
        );
    }
}
