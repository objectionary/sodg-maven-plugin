/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.set.SetOf;

/**
 * Convert XMIR to SODG.
 * <p>
 * SODG (Surging Object DiGraph) is our own format of graph representation.
 * It essentially is a text file that consists of instructions for a virtual
 * machine that is capable of parsing them and building a graph. An example
 * of such a machine can be found in
 * <a href="https://github.com/objectionary/sodg">this repository</a>. When the
 * graph is built by the virtual machine, it must be possible to execute
 * a program using graph traversing algorithm. An example of such an executor
 * of a graph can be found in
 * <a href="https://github.com/objectionary/reo">this repository</a>.
 * <p>
 * This class was copy-pasted from objectionary/eo/eo-maven-plugin.
 *
 * @since 0.0.1
 */
@Mojo(
    name = "sodg",
    defaultPhase = LifecyclePhase.PROCESS_SOURCES,
    threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresProject = false
)
@SuppressWarnings({"PMD.ImmutableField", "PMD.AvoidProtectedFieldInFinalClass"})
public final class MjSodg extends AbstractMojo {
    /**
     * The directory where to save SODG to.
     */
    private static final String DIR = "sodg";

    /**
     * Whether we should skip goal execution.
     * @checkstyle VisibilityModifierCheck (10 lines)
     */
    @Parameter(property = "eo.sodg.skip", defaultValue = "false")
    @SuppressWarnings("PMD.ImmutableField")
    protected boolean skip;

    /**
     * The path of the file where XSL measurements (time of execution
     * in milliseconds) will be stored.
     *
     * @since 0.41.0
     * @checkstyle MemberNameCheck (10 lines)
     * @checkstyle VisibilityModifierCheck (10 lines)
     */
    @Parameter(
        property = "eo.sodg.xslMeasuresFile",
        required = true,
        defaultValue = "${project.build.directory}/eo/xsl-measures.json"
    )
    protected File xslMeasures;

    /**
     * Current scope (either "compile" or "test").
     *
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Parameter(property = "eo.sodg.scope")
    protected String scope = "compile";

    /**
     * Format of "foreign" file ("json" or "csv").
     *
     * @checkstyle MemberNameCheck (7 lines)
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Parameter(property = "eo.sodg.foreignFormat", required = true, defaultValue = "csv")
    protected String foreignFormat = "csv";

    /**
     * File with foreign "tojos".
     *
     * @checkstyle VisibilityModifierCheck (10 lines)
     */
    @Parameter(
        property = "eo.foreign",
        required = true,
        defaultValue = "${project.build.directory}/eo-foreign.json"
    )
    protected File foreign;

    /**
     * Target directory.
     *
     * @checkstyle MemberNameCheck (10 lines)
     * @checkstyle VisibilityModifierCheck (10 lines)
     */
    @Parameter(
        property = "eo.targetDir",
        required = true,
        defaultValue = "${project.build.directory}/eo"
    )
    protected File targetDir;

    /**
     * Cached tojos.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    private final TjsForeign tojos = new TjsForeign(
        () -> Catalogs.INSTANCE.make(this.foreign.toPath(), this.foreignFormat),
        () -> this.scope
    );

    /**
     * The railway.
     */
    private final Railway railway = new Railway(this.xslMeasures);

    /**
     * Shall we generate .xml files with SODGs?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    @Parameter(
        property = "eo.generateSodgXmlFiles",
        defaultValue = "false"
    )
    @SuppressWarnings("PMD.LongVariable")
    private boolean generateSodgXmlFiles;

    /**
     * Shall we generate .xe files with Xembly instructions graph?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    @Parameter(
        property = "eo.sodg.generateXemblyFiles",
        defaultValue = "false"
    )
    @SuppressWarnings("PMD.LongVariable")
    private boolean generateXemblyFiles;

    /**
     * Shall we generate .graph.xml files with XML graph?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    @Parameter(
        property = "eo.sodg.generateGraphFiles",
        defaultValue = "false"
    )
    @SuppressWarnings("PMD.LongVariable")
    private boolean generateGraphFiles;

    /**
     * Shall we generate .dot files with DOT language graph commands?
     *
     * @checkstyle MemberNameCheck (7 lines)
     */
    @Parameter(
        property = "eo.generateDotFiles",
        defaultValue = "false"
    )
    @SuppressWarnings("PMD.LongVariable")
    private boolean generateDotFiles;

    /**
     * List of object names to participate in SODG generation.
     *
     * @implNote {@code property} attribute is omitted for collection
     *     properties since there is no way of passing it via command line.
     * @checkstyle MemberNameCheck (15 lines)
     */
    @Parameter
    private Set<String> sodgIncludes = new SetOf<>("**");

    /**
     * List of object names which are excluded from SODG generation.
     *
     * @implNote {@code property} attribute is omitted for collection
     *  properties since there is no way of passing it via command line.
     * @checkstyle MemberNameCheck (15 lines)
     */
    @Parameter
    private Set<String> sodgExcludes = new SetOf<>();

    /**
     * Plugin metadata.
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor descriptor;

    @Override
    public void execute() throws MojoFailureException {
        if (this.skip) {
            if (Logger.isInfoEnabled(this)) {
                Logger.info(
                    this, "Execution skipped due to eo.skip option"
                );
            }
        } else {
            try {
                this.generate();
            } catch (final IOException exception) {
                throw new MojoFailureException("Can't convert XMIR to SODG", exception);
            }
        }
    }

    /**
     * Generate SODG.
     * @throws IOException if I/O fails
     */
    private void generate() throws IOException {
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
        final Map<String, Boolean> config = new MapOf<>(
            new MapEntry<>("generateSodgXmlFiles", this.generateSodgXmlFiles),
            new MapEntry<>("generateXemblyFiles", this.generateXemblyFiles),
            new MapEntry<>("generateXemblyFiles", this.generateXemblyFiles),
            new MapEntry<>("generateGraphFiles", this.generateGraphFiles),
            new MapEntry<>("generateDotFiles", this.generateDotFiles)
        );
        final Collection<TjForeign> scoped = this.tojos.withXmir();
        final Path home = this.targetDir.toPath().resolve(MjSodg.DIR);
        int total = 0;
        int instructions = 0;
        final Set<Pattern> includes = this.sodgIncludes.stream()
            .map(i -> Pattern.compile(MjSodg.createMatcher(i)))
            .collect(Collectors.toSet());
        final Set<Pattern> excludes = this.sodgExcludes.stream()
            .map(i -> Pattern.compile(MjSodg.createMatcher(i)))
            .collect(Collectors.toSet());
        for (final TjForeign tojo : scoped) {
            final String name = tojo.identifier();
            if (this.excluded(name, includes, excludes)) {
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
            final int extra = new SodgInstructions(
                this.railway, config, this.descriptor.getVersion()
            ).total(
                xmir, sodg
            );
            instructions += extra;
            tojo.withSodg(sodg.toAbsolutePath());
            Logger.info(
                this, "SODG for %s saved to %[file]s (%d instructions)",
                name, sodg, extra
            );
            ++total;
        }
        if (total == 0) {
            if (scoped.isEmpty()) {
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
     * @param includes Patterns for SODGs to be included
     * @param excludes Patterns for SODGs to be excluded
     * @return TRUE if to exclude
     */
    private boolean excluded(
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
