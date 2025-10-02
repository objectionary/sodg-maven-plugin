/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Sodg generated files.
 * @since 0.0.3
 * @todo #9:30min Create unit tests for SodgFiles.
 *  For now there are no test for SODG file generation logic. Let's introduce them,
 *  to make our refactorings safer and faster. Don't forget to remove this puzzle.
 */
final class SodgFiles {
    /**
     * The instructions.
     */
    private final SodgInstructions instructions;

    /**
     * The programs to include.
     */
    private final Set<String> includes;

    /**
     * The programs to exclude.
     */
    private final Set<String> excludes;

    /**
     * Ctor.
     * @param instrctns Instructions
     * @param inclds Programs to include
     * @param exclds Programs to exclude
     */
    SodgFiles(
        final SodgInstructions instrctns,
        final Set<String> inclds,
        final Set<String> exclds
    ) {
        this.instructions = instrctns;
        this.includes = inclds;
        this.excludes = exclds;
    }

    /**
     * Generate.
     * @param tojos Tojos
     * @param home Home path
     * @throws IOException if I/O operation fails
     */
    void generate(final Collection<TjForeign> tojos, final Path home) throws IOException {
        final Set<Pattern> inclusions = this.includes.stream()
            .map(i -> Pattern.compile(SodgFiles.createMatcher(i)))
            .collect(Collectors.toSet());
        final Set<Pattern> exclusions = this.excludes.stream()
            .map(i -> Pattern.compile(SodgFiles.createMatcher(i)))
            .collect(Collectors.toSet());
        int total = 0;
        int rendered = 0;
        for (final TjForeign tojo : tojos) {
            final String name = tojo.identifier();
            if (this.excluded(name, inclusions, exclusions)) {
                continue;
            }
            final Path sodg = new Place(name).make(home, "sodg");
            final Path xmir = tojo.shaken();
            if (sodg.toFile().lastModified() >= xmir.toFile().lastModified()) {
                Logger.debug(
                    this, "Already converted %s to %[file]s (it's newer than the source)",
                    name, sodg
                );
                continue;
            }
            final int extra = this.instructions.textInstructions(xmir, sodg);
            rendered += extra;
            tojo.withSodg(sodg.toAbsolutePath());
            Logger.info(
                this, "SODG for %s saved to %[file]s (%d instructions)",
                name, sodg, extra
            );
            ++total;
        }
        if (total == 0) {
            if (tojos.isEmpty()) {
                Logger.info(this, "No .xmir need to be converted to SODGs");
            } else {
                Logger.info(this, "No .xmir converted to SODGs");
            }
        } else {
            Logger.info(
                this, "Converted %d .xmir to SODGs, saved to %[file]s, %d instructions",
                total, home, rendered
            );
        }
    }

    /**
     * Exclude this EO program from processing?
     *
     * @param name The name
     * @param inclusions Patterns for SODGs to be included
     * @param exclusions Patterns for SODGs to be excluded
     * @return TRUE if to exclude
     */
    private boolean excluded(
        final String name,
        final Set<Pattern> inclusions,
        final Set<Pattern> exclusions
    ) {
        boolean exclude = false;
        if (inclusions.stream().noneMatch(p -> p.matcher(name).matches())) {
            Logger.debug(this, "Excluding %s due to sodgIncludes option", name);
            exclude = true;
        }
        if (exclusions.stream().anyMatch(p -> p.matcher(name).matches())) {
            Logger.debug(this, "Excluding %s due to sodgExcludes option", name);
            exclude = true;
        }
        return exclude;
    }

    /**
     * Creates a regular expression out of sodgInclude string.
     *
     * @param pattern String from sodgIncludes
     * @return Created regular expression
     */
    private static String createMatcher(final String pattern) {
        return pattern.replace("**", "[A-Za-z0-9.]+?").replace("*", "[A-Za-z0-9]+");
    }
}
