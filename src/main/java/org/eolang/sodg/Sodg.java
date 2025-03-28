/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.TrLambda;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.cactoos.set.SetOf;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * The main class for SODG generation.
 *
 * @since 0.1
 */
final class Sodg {

    /**
     * SODG to plain text.
     */
    private static final Train<Shift> TO_TEXT = new TrText();

    /**
     * SODG to Xembly.
     */
    private static final Train<Shift> TO_XEMBLY = new TrXembly();

    /**
     * SODG to Dot.
     */
    private static final Train<Shift> TO_DOT = new TrDot(Sodg.loggingLevel());

    /**
     * Graph modification right after it's generated from Xembly.
     */
    private static final Train<Shift> FINISH = new TrFinish(Sodg.loggingLevel());

    /**
     * The train that generates SODG.
     */
    private static final Train<Shift> TRAIN = new TrSodg(Sodg.loggingLevel());

    /**
     * The directory where to save SODG to.
     */
    private static final String DIR = "sodg";

    /**
     * Shall we generate .graph.xml files with XML graph?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    private final boolean generateGraphFiles;

    /**
     * Shall we generate .xe files with Xembly instructions graph?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    private final boolean generateXemblyFiles;

    /**
     * Shall we generate .xml files with SODGs?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    @SuppressWarnings("PMD.LongVariable")
    private final boolean generateSodgXmlFiles;

    /**
     * The path of the file where XSL measurements (time of execution
     * in milliseconds) will be stored.
     *
     * @checkstyle MemberNameCheck (10 lines)
     * @checkstyle VisibilityModifierCheck (10 lines)
     * @since 0.41.0
     */
    private final File xslMeasures;

    /**
     * Shall we generate .dot files with DOT language graph commands?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    @SuppressWarnings("PMD.LongVariable")
    private final boolean generateDotFiles;

    /**
     * Target directory.
     *
     * @checkstyle MemberNameCheck (10 lines)
     * @checkstyle VisibilityModifierCheck (10 lines)
     */
    private final File targetDir;

    /**
     * Cached tojos.
     *
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    private final TjsForeign tojos;

    /**
     * List of object names to participate in SODG generation.
     *
     * @implNote {@code property} attribute is omitted for collection
     *     properties since there is no way of passing it via command line.
     * @checkstyle MemberNameCheck (15 lines)
     */
    private final Set<String> sodgIncludes;

    /**
     * List of object names which are excluded from SODG generation.
     *
     * @implNote {@code property} attribute is omitted for collection
     *     properties since there is no way of passing it via command line.
     * @checkstyle MemberNameCheck (15 lines)
     */
    private final Set<String> sodgExcludes;

    /**
     * Constructor.
     *
     * @param xslMeasures The path of the file where XSL measurements
     * @param targetDir The target directory
     * @param tojos The tojos
     */
    Sodg(
        final File xslMeasures,
        final File targetDir,
        final TjsForeign tojos
    ) {
        this(
            false,
            false,
            false,
            xslMeasures,
            false,
            targetDir,
            tojos,
            new SetOf<>("**"),
            new SetOf<>()
        );
    }

    /**
     * Constructor.
     *
     * @param generateGraphFiles Shall we generate .graph.xml files with XML graph?
     * @param generateXemblyFiles Shall we generate .xe files with Xembly instructions graph?
     * @param generateSodgXmlFiles Shall we generate .xml files with SODGs?
     * @param xslMeasures The path of the file where XSL measurements
     * @param generateDotFiles Shall we generate .dot files with DOT language graph commands?
     * @param targetDir The target directory
     * @param tojos The tojos
     * @param sodgIncludes List of object names to participate in SODG generation
     * @param sodgExcludes List of object names which are excluded from SODG generation
     */
    Sodg(
        final boolean generateGraphFiles,
        final boolean generateXemblyFiles,
        final boolean generateSodgXmlFiles,
        final File xslMeasures,
        final boolean generateDotFiles,
        final File targetDir,
        final TjsForeign tojos,
        final Set<String> sodgIncludes,
        final Set<String> sodgExcludes
    ) {
        this.generateGraphFiles = generateGraphFiles;
        this.generateXemblyFiles = generateXemblyFiles;
        this.generateSodgXmlFiles = generateSodgXmlFiles;
        this.xslMeasures = xslMeasures;
        this.generateDotFiles = generateDotFiles;
        this.targetDir = targetDir;
        this.tojos = tojos;
        this.sodgIncludes = sodgIncludes;
        this.sodgExcludes = sodgExcludes;
    }

    void exec() throws IOException {
        if (this.generateGraphFiles && !this.generateXemblyFiles) {
            throw new IllegalStateException(
                "Setting generateGraphFiles and not setting generateXemblyFiles has no effect because .graph files require .xe files"
            );
        }
        if (this.generateDotFiles && !this.generateGraphFiles) {
            throw new IllegalStateException(
                "Setting generateDotFiles and not setting generateGraphFiles has no effect because .dot files require .graph files"
            );
        }
        final Collection<TjForeign> tojos = this.scopedTojos().withShaken();
        final Path home = this.targetDir.toPath().resolve(Sodg.DIR);
        int total = 0;
        int instructions = 0;
        final Set<Pattern> includes = this.sodgIncludes.stream()
            .map(i -> Pattern.compile(Sodg.createMatcher(i)))
            .collect(Collectors.toSet());
        final Set<Pattern> excludes = this.sodgExcludes.stream()
            .map(i -> Pattern.compile(Sodg.createMatcher(i)))
            .collect(Collectors.toSet());
        for (final TjForeign tojo : tojos) {
            final String name = tojo.identifier();
            if (this.exclude(name, includes, excludes)) {
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
            final int extra = this.render(xmir, sodg);
            instructions += extra;
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
                total, home, instructions
            );
        }
    }

    /**
     * Exclude this EO program from processing?
     *
     * @param name The name
     * @param includes Patterns for sodgs to be included
     * @param excludes Patterns for sodgs to be excluded
     * @return TRUE if to exclude
     */
    private boolean exclude(
        final String name,
        final Set<Pattern> includes,
        final Set<Pattern> excludes
    ) {
        boolean exclude = false;
        if (includes.stream().noneMatch(p -> p.matcher(name).matches())) {
            Logger.debug(this, "Excluding %s due to sodgIncludes option", name);
            exclude = true;
        }
        if (excludes.stream().anyMatch(p -> p.matcher(name).matches())) {
            Logger.debug(this, "Excluding %s due to sodgExcludes option", name);
            exclude = true;
        }
        return exclude;
    }

    /**
     * Convert XMIR file to SODG.
     *
     * @param xmir Location of XMIR
     * @param sodg Location of SODG
     * @return Total number of SODG instructions generated
     * @throws IOException If fails
     */
    private int render(final Path xmir, final Path sodg) throws IOException {
        final XML before = new XMLDocument(xmir);
        if (Logger.isTraceEnabled(this)) {
            Logger.trace(this, "XML before translating to SODG:\n%s", before);
        }
        final XML after = new Xsline(this.measured(Sodg.TRAIN)).pass(before);
        final String instructions = new Xsline(this.measured(Sodg.TO_TEXT))
            .pass(after)
            .xpath("/text/text()")
            .get(0);
        if (Logger.isTraceEnabled(this)) {
            Logger.trace(this, "SODGs:\n%s", instructions);
        }
        new Saved(String.format("# %s\n\n%s", new Disclaimer(), instructions), sodg).value();
        if (this.generateSodgXmlFiles) {
            final Path sibling = sodg.resolveSibling(String.format("%s.xml", sodg.getFileName()));
            new Saved(after.toString(), sibling).value();
        }
        if (this.generateXemblyFiles) {
            final String xembly = new Xsline(this.measured(Sodg.TO_XEMBLY))
                .pass(after)
                .xpath("/xembly/text()").get(0);
            final Path sibling = sodg.resolveSibling(String.format("%s.xe", sodg.getFileName()));
            new Saved(String.format("# %s\n\n%s\n", new Disclaimer(), xembly), sibling).value();
            this.makeGraph(xembly, sodg);
        }
        return instructions.split("\n").length;
    }

    /**
     * Make graph.
     *
     * @param xembly The Xembly script
     * @param sodg The path of SODG file
     * @throws IOException If fails
     */
    private void makeGraph(final String xembly, final Path sodg) throws IOException {
        if (this.generateGraphFiles) {
            final Directives all = new Directives(xembly);
            Logger.debug(
                this, "There are %d Xembly directives for %s",
                new IoChecked<>(new LengthOf(all)).value(), sodg
            );
            final ListOf<Directive> directives = new ListOf<>(all);
            final Directive comment = directives.remove(0);
            final XML graph = new Xsline(this.measured(Sodg.FINISH)).pass(
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
     * Make graph.
     *
     * @param graph The graph in XML
     * @param sodg The path of SODG file
     * @throws IOException If fails
     */
    private void makeDot(final XML graph, final Path sodg) throws IOException {
        if (this.generateDotFiles) {
            final String dot = new Xsline(this.measured(Sodg.TO_DOT))
                .pass(graph).xpath("//dot/text()").get(0);
            if (Logger.isTraceEnabled(this)) {
                Logger.trace(this, "Dot:\n%s", dot);
            }
            final Path sibling = sodg.resolveSibling(String.format("%s.dot", sodg.getFileName()));
            new Saved(
                String.format("/%s %s %1$s/\n\n%s", "*", new Disclaimer(), dot),
                sibling
            ).value();
        }
    }

    /**
     * We are in IntelliJ IDEA at the moment?
     * <p>
     * This is for testing purposes, to enable higher visibility of logs inside
     * tests being executed interactively in the IDE.
     *
     * @return TRUE if inside IDE
     */
    private static Level loggingLevel() {
        Level lvl = Level.FINEST;
        if (System.getProperty("java.class.path").contains("idea_rt.jar")) {
            lvl = Level.INFO;
        }
        return lvl;
    }

    /**
     * Make a measured train from another train.
     *
     * @param train The train
     * @return Measured train
     */
    private Train<Shift> measured(final Train<Shift> train) {
        if (this.xslMeasures.getParentFile().mkdirs()) {
            Logger.debug(this, "Directory created for %[file]s", this.xslMeasures);
        }
        if (!this.xslMeasures.getParentFile().exists()) {
            throw new IllegalArgumentException(
                String.format(
                    "For some reason, the directory %s is absent, can't write measures to %s",
                    this.xslMeasures.getParentFile(),
                    this.xslMeasures
                )
            );
        }
        if (this.xslMeasures.isDirectory()) {
            throw new IllegalArgumentException(
                String.format(
                    "This is not a file but a directory, can't write to it: %s",
                    this.xslMeasures
                )
            );
        }
        return new TrLambda(
            train,
            shift -> new StMeasured(
                shift,
                this.xslMeasures.toPath()
            )
        );
    }

    /**
     * Tojos to use, in my scope only.
     *
     * @return Tojos to use
     * @checkstyle AnonInnerLengthCheck (100 lines)
     */
    private TjsForeign scopedTojos() {
        return this.tojos;
    }

    /**
     * Creates a regular expression out of sodgInclude string.
     *
     * @param pattern String from sodgIncludes
     * @return Created regular expression
     */
    private static String createMatcher(final String pattern) {
        return pattern
            .replace("**", "[A-Za-z0-9.]+?")
            .replace("*", "[A-Za-z0-9]+");
    }
}
