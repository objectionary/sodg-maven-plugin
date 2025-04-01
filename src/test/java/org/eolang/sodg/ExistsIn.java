/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.xml.XML;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher for a single locator against the graph.
 *
 * @since 0.27
 */
final class ExistsIn extends TypeSafeMatcher<String> {
    /**
     * Graph in XML.
     */
    private final XML graph;

    /**
     * The description of a failure.
     */
    private String failure;

    /**
     * Ctor.
     *
     * @param xml The graph
     */
    ExistsIn(final XML xml) {
        this.graph = xml;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(this.failure)
            .appendText(" in this XML:\n")
            .appendText(this.graph.toString());
    }

    @Override
    public boolean matchesSafely(final String item) {
        boolean matches = true;
        try {
            this.matches(item);
        } catch (final IllegalArgumentException ex) {
            matches = false;
            this.failure = ex.getMessage();
        }
        return matches;
    }

    /**
     * Check and throw if fails.
     *
     * @param item The path to check
     * @checkstyle CyclomaticComplexityCheck (10 lines)
     * @checkstyle NPathComplexityCheck (10 lines)
     */
    @SuppressWarnings({
        "PMD.NPathComplexity",
        "PMD.ExcessiveMethodLength",
        "PMD.CognitiveComplexity"
    })
    private void matches(final String item) {
        String vertex = "ν0";
        final String[] parts = item.split(" ");
        for (int pos = 0; pos < parts.length; ++pos) {
            String sub = parts[pos];
            boolean inverse = false;
            final XML node = this.graph.nodes(
                String.format("/graph/v[@id='%s']", vertex)
            ).get(0);
            if (sub.charAt(0) == '!') {
                inverse = true;
                sub = sub.substring(1);
            }
            if (sub.charAt(0) == '.') {
                final List<String> opts = node.xpath(
                    String.format(
                        "e[@title='%s']/@to",
                        sub.substring(1)
                    )
                );
                if (opts.isEmpty() && !inverse) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Can't find path '%s' (#%d) while staying at %s",
                            sub, pos, vertex
                        )
                    );
                }
                if (!opts.isEmpty() && inverse) {
                    throw new IllegalArgumentException(
                        String.format(
                            "The path '%s' (#%d) must not exist at %s, but it does",
                            sub, pos, vertex
                        )
                    );
                }
                if (!inverse) {
                    vertex = opts.get(0);
                }
                continue;
            }
            if (sub.charAt(0) == '>') {
                final List<XML> inputs = this.graph.nodes(
                    String.format("/graph/v/e[@to='%s']", vertex)
                );
                if (inputs.isEmpty() && !inverse) {
                    throw new IllegalArgumentException(
                        String.format(
                            "There is no '%s' (#%d) edge coming into %s",
                            sub.substring(1), pos, vertex
                        )
                    );
                }
                continue;
            }
            if (sub.startsWith("δ=")) {
                if (node.nodes("data").isEmpty()) {
                    throw new IllegalArgumentException(
                        String.format(
                            "There is no data (%s) at %s (#%d)",
                            sub, vertex, pos
                        )
                    );
                }
                final String data = sub.substring(2);
                final boolean matches = !node.xpath(
                    String.format(
                        "data[text() = '%s']/text()", data
                    )
                ).isEmpty();
                if (!matches) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Data '%s' at '%s' (#%d) is not equal to '%s'",
                            node.xpath("data/text()").get(0), vertex, pos, data
                        )
                    );
                }
                continue;
            }
            if (sub.startsWith("τ=")) {
                if (node.nodes("data").isEmpty()) {
                    throw new IllegalArgumentException(
                        String.format(
                            "There is no lambda (%s) at %s (#%d)",
                            sub, vertex, pos
                        )
                    );
                }
                final String data = sub.substring(2);
                final String hex = ExistsIn.bytesToHex(
                    data.getBytes(StandardCharsets.UTF_8)
                );
                final boolean matches = !node.xpath(
                    String.format(
                        "data[text() = '%s']/text()",
                        hex
                    )
                ).isEmpty();
                if (!matches) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Lambda '%s' at '%s' (#%d) is not equal to '%s' (%s)",
                            node.xpath("data/text()").get(0), vertex, pos, data, hex
                        )
                    );
                }
                continue;
            }
            if (sub.startsWith("ν=")) {
                final String expected = sub.substring(2);
                final boolean matches = vertex.equals(expected);
                if (!matches && !inverse) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Current vertex '%s' is not '%s' (#%d), as expected",
                            vertex, expected, pos
                        )
                    );
                }
                continue;
            }
            throw new IllegalArgumentException(
                String.format(
                    "Can't understand path element '%s' (#%d) in '%s'",
                    sub, pos, item
                )
            );
        }
    }

    /**
     * Bytes to HEX.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
    private static String bytesToHex(final byte... bytes) {
        final StringJoiner out = new StringJoiner("-");
        for (final byte bty : bytes) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }
}
