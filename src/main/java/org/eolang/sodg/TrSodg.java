/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.manifests.Manifests;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StBefore;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StEndless;
import com.yegor256.xsline.StSchema;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;
import com.yegor256.xsline.TrJoined;
import com.yegor256.xsline.TrLogged;
import com.yegor256.xsline.TrMapped;
import com.yegor256.xsline.TrWith;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.logging.Level;
import org.eolang.parser.StXPath;
import org.xembly.Directives;

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
                            new TrDefault<>(
                                new StEndless(
                                    new StXPath(
                                        "(//o[@name and @atom and not(@base) and @loc and not(@lambda)])[1]",
                                        xml -> {
                                            final String loc = xml.xpath("@loc").get(0);
                                            return new Directives().attr(
                                                "lambda",
                                                TrSodg.utfToHex(
                                                    loc.substring(loc.indexOf('.') + 1)
                                                )
                                            );
                                        }
                                    )
                                )
                            ),
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
                                "/org/eolang/maven/sodg/touch-all.xsl",
                                "/org/eolang/maven/sodg/bind-sigma.xsl",
                                "/org/eolang/maven/sodg/bind-rho.xsl",
                                "/org/eolang/maven/sodg/pi-copies.xsl",
                                "/org/eolang/maven/sodg/epsilon-bindings.xsl",
                                "/org/eolang/maven/sodg/connect-dots.xsl",
                                "/org/eolang/maven/sodg/put-data.xsl",
                                "/org/eolang/maven/sodg/put-atoms.xsl"
                            ).back(),
                            new TrDefault<>(
                                new StClasspath(
                                    "/org/eolang/maven/sodg/add-meta.xsl",
                                    "name version",
                                    String.format(
                                        "value %s",
                                        TrSodg.utfToHex(
                                            Manifests.read("EO-Version")
                                        )
                                    )
                                ),
                                new StClasspath(
                                    "/org/eolang/maven/sodg/add-meta.xsl",
                                    "name time",
                                    String.format(
                                        "value %s",
                                        TrSodg.utfToHex(
                                            ZonedDateTime.now(ZoneOffset.UTC).format(
                                                DateTimeFormatter.ISO_INSTANT
                                            )
                                        )
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

    /**
     * UTF-8 string to HEX.
     *
     * @param txt The string
     * @return Hexadecimal value as string.
     */
    private static String utfToHex(final String txt) {
        final StringJoiner out = new StringJoiner("-");
        for (final byte bty : txt.getBytes(StandardCharsets.UTF_8)) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }
}
