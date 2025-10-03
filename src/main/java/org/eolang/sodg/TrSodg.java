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
                                "/org/eolang/maven/sodg/to-sodg.xsl",
                                "/org/eolang/maven/sodg/drop-lambda.xsl",
                                "/org/eolang/maven/sodg/applications.xsl"
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
