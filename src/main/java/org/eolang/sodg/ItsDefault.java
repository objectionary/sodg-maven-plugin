/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Default SODG instruction rendering.
 * @since 0.0.3
 */
final class ItsDefault implements Instructions {

    /**
     * The depot.
     */
    private final Depot depot;

    /**
     * The configuration.
     */
    private final Map<String, Boolean> config;

    /**
     * The version.
     */
    private final String version;

    ItsDefault(final Depot dpt) {
        this(
            dpt,
            new MapOf<>(
                new MapEntry<>("generateSodgXmlFiles", false),
                new MapEntry<>("generateXemblyFiles", false),
                new MapEntry<>("generateXemblyFiles", false),
                new MapEntry<>("generateGraphFiles", false),
                new MapEntry<>("generateDotFiles", false)
            )
        );
    }

    /**
     * Ctor.
     * @param dpt Depot
     * @param cfg Configuration
     */
    ItsDefault(final Depot dpt, final Map<String, Boolean> cfg) {
        this(dpt, cfg, "0.0.0");
    }

    /**
     * Ctor.
     * @param dpt Depot
     * @param cfg Configuration
     * @param vrsn Version
     */
    ItsDefault(final Depot dpt, final Map<String, Boolean> cfg, final String vrsn) {
        this.depot = dpt;
        this.config = cfg;
        this.version = vrsn;
    }

    @Override
    public int textInstructions(final Path xmir, final Path base) throws IOException {
        final XML before = new XMLDocument(xmir);
        if (Logger.isTraceEnabled(this)) {
            Logger.trace(this, "XML before translating to SODG:\n%s", before);
        }
        final XML after = new Xsline(this.depot.train("sodg")).pass(before);
        final String instructions = new Xsline(this.depot.train("text"))
            .pass(after)
            .xpath("/text/text()")
            .get(0);
        if (Logger.isTraceEnabled(this)) {
            Logger.trace(this, "SODGs:\n%s", instructions);
        }
        new Saved(
            String.format("# %s\n\n%s", new Disclaimer(this.version), instructions), base
        ).value();
        if (this.config.get("generateSodgXmlFiles")) {
            new Saved(
                after.toString(), base.resolveSibling(String.format("%s.xml", base.getFileName()))
            ).value();
        }
        if (this.config.get("generateXemblyFiles")) {
            final String xembly = new Xsline(this.depot.train("xembly")).pass(after)
                .xpath("/xembly/text()").get(0);
            new Saved(
                String.format("# %s\n\n%s\n", new Disclaimer(this.version), xembly),
                base.resolveSibling(String.format("%s.xe", base.getFileName()))
            ).value();
            this.makeGraph(xembly, base);
        }
        return (int) new ListOf<>(instructions.trim().split("\n")).stream().filter(
            i -> !(!i.isEmpty() && i.charAt(0) == '#') && !i.isEmpty()
        ).count();
    }

    /**
     * Make graph.
     *
     * @param xembly The Xembly script
     * @param sodg The path of SODG file
     * @throws IOException If fails
     */
    private void makeGraph(final String xembly, final Path sodg) throws IOException {
        if (this.config.get("generateGraphFiles")) {
            final Directives all = new Directives(xembly);
            Logger.debug(
                this, "There are %d Xembly directives for %s",
                new IoChecked<>(new LengthOf(all)).value(), sodg
            );
            final List<Directive> directives = new ListOf<>(all);
            final Directive comment = directives.remove(0);
            final XML graph = new Xsline(this.depot.train("finish")).pass(
                new XMLDocument(
                    new Xembler(
                        new Directives()
                            .append(Collections.singleton(comment))
                            .append(directives)
                            .xpath("/graph")
                            .attr("sodg-path", sodg)
                    ).domQuietly()
                )
            );
            final Path sibling = sodg.resolveSibling(
                String.format("%s.graph.xml", sodg.getFileName())
            );
            new Saved(graph.toString(), sibling).value();
            if (Logger.isTraceEnabled(this)) {
                Logger.trace(this, "Graph:\n%s", graph.toString());
            }
            this.makeDot(graph, sodg);
        }
    }

    /**
     * Make dot.
     *
     * @param graph The graph in XML
     * @param sodg The path of SODG file
     * @throws IOException If fails
     */
    private void makeDot(final XML graph, final Path sodg) throws IOException {
        if (this.config.get("generateDotFiles")) {
            final String dot = new Xsline(this.depot.train("dot"))
                .pass(graph).xpath("//dot/text()").get(0);
            if (Logger.isTraceEnabled(this)) {
                Logger.trace(this, "Dot:\n%s", dot);
            }
            final Path sibling = sodg.resolveSibling(String.format("%s.dot", sodg.getFileName()));
            new Saved(
                String.format("/%s %s %1$s/\n\n%s", "*", new Disclaimer(this.version), dot),
                sibling
            ).value();
        }
    }
}
