/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.manifests.Manifests;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StBefore;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StSchema;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;
import com.yegor256.xsline.TrJoined;
import com.yegor256.xsline.TrLogged;
import com.yegor256.xsline.TrMapped;
import com.yegor256.xsline.TrWith;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Main transformer for SODG.
 *
 * @since 0.1
 */
final class TrSodg extends TrEnvelope {

    /**
     * Ctor.
     * @param level Logging level.
     * @todo #11:90min Complete SODG file generation.
     *  We removed the following transformations from this train:
     *  "/org/eolang/maven/sodg/bind-sigma.xsl",
     *  "/org/eolang/maven/sodg/bind-rho.xsl",
     *  "/org/eolang/maven/sodg/pi-copies.xsl",
     *  "/org/eolang/maven/sodg/epsilon-bindings.xsl",
     *  "/org/eolang/maven/sodg/connect-dots.xsl",
     *  "/org/eolang/maven/sodg/put-data.xsl",
     *  "/org/eolang/maven/sodg/put-atoms.xsl"
     *  This was done intentionally to avoid failures in the code.
     *  The code fails, because the transformation is too outdated.
     *  We need to update transformations and finish SODG generation.
     * @todo #46:35min Create integration tests for full TrSodg.
     *  Currently, we have only the unit tests in the sodg-packs, that check
     *  sheets application in the isolation only. We should create more integration
     *  test in order to check the integrity of the final results, with all sheets.
     *  Seems that {@link TrSodgTest} is the best place to keep such tests.
     */
    TrSodg(final Level level) {
        super(
            new TrLogged(
                new TrWith(
                    new TrFast(
                        new TrJoined<>(
                            new TrClasspath<>(
                                "/org/eolang/maven/sodg/pre-clean.xsl"
                            ).back(),
                            new TrMapped<>(
                                (Function<String, Shift>) path -> new StBefore(
                                    new StClasspath(path),
                                    new StClasspath(
                                        "/org/eolang/maven/sodg/before-each.xsl",
                                        String.format("sheet %s", path)
                                    )
                                ),
                                "/org/eolang/maven/sodg/add-sodg-root.xsl",
                                "/org/eolang/maven/sodg/add-loc-to-objects.xsl",
                                "/org/eolang/maven/sodg/add-root.xsl",
                                "/org/eolang/maven/sodg/append-xi.xsl",
                                "/org/eolang/maven/sodg/unroll-refs.xsl",
                                "/org/eolang/maven/sodg/remove-leveled.xsl",
                                "/org/eolang/maven/sodg/touch-all.xsl"
                            ).back(),
                            new TrDefault<>(
                                new StClasspath(
                                    "/org/eolang/maven/sodg/add-meta.xsl",
                                    "name version",
                                    String.format(
                                        "value %s",
                                        new HexedUtf(
                                            Manifests.read("EO-Version")
                                        ).asString()
                                    )
                                ),
                                new StClasspath(
                                    "/org/eolang/maven/sodg/add-meta.xsl",
                                    "name time",
                                    String.format(
                                        "value %s",
                                        new HexedUtf(
                                            ZonedDateTime.now(ZoneOffset.UTC).format(
                                                DateTimeFormatter.ISO_INSTANT
                                            )
                                        ).asString()
                                    )
                                )
                            ),
                            new TrClasspath<>(
                                "/org/eolang/maven/sodg/focus.xsl"
                            ).back()
                        ),
                        SodgFiles.class
                    ),
                    new StSchema("/org/eolang/maven/sodg/after.xsd")
                ),
                SodgFiles.class,
                level
            )
        );
    }
}
