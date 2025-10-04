/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.Mono;
import com.yegor256.tojos.TjDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;

/**
 * XMIR tojos.
 * @since 0.0.3
 */
final class TjsXmir {

    /**
     * The home.
     */
    private final Path home;

    /**
     * The programs.
     */
    private final Map<String, String> programs;

    /**
     * Ctor.
     * @param hme Home
     * @param prgrms Programs
     */
    TjsXmir(final Path hme, final Map<String, String> prgrms) {
        this.home = hme;
        this.programs = prgrms;
    }

    /**
     * As tojos.
     * @return Collection of tojos.
     * @throws IOException if I/O operation fails
     */
    Collection<TjForeign> asTojos() throws IOException {
        try (Mono mono = new MnCsv(this.home.resolve("foreign.csv"))) {
            final Collection<Map<String, String>> foreigns = new ArrayList<>(16);
            this.programs.forEach(
                (name, sources) -> {
                    final Path xmir = this.home.resolve(String.format("%s.xmir", name));
                    try {
                        Files.write(
                            xmir, new EoSyntax(sources).parsed().toString().getBytes(
                                StandardCharsets.UTF_8
                            )
                        );
                    } catch (final IOException exception) {
                        throw new IllegalStateException(
                            String.format("Failed to write XMIR to %s", xmir), exception
                        );
                    }
                    foreigns.add(
                        new MapOf<>(
                            new MapEntry<>(
                                "id",
                                String.format("org.eolang.sodg.examples.%s", name)
                            ),
                            new MapEntry<>("xmir", xmir.toString()),
                            new MapEntry<>("scope", "compile")
                        )
                    );
                }
            );
            mono.write(foreigns);
            return new TjsForeign(() -> new TjDefault(mono), () -> "compile").withXmir();
        }
    }
}
