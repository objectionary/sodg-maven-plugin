/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.TrLambda;
import com.yegor256.xsline.Train;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * The depot that produces trains.
 * @since 0.0.3
 * @todo #9:35min Add unit tests for `Depot`.
 *  We should test the trains inside of it, and method `train()`, that supposed
 *  to return proper train by it's name. Don't forget to remove this puzzle.
 */
final class Depot {

    /**
     * The trains.
     */
    private final Map<String, Train<Shift>> trains;

    /**
     * Ctor.
     * @param measures Measures
     */
    Depot(final File measures) {
        this(
            new MapOf<>(
                new MapEntry<>(
                    "sodg", Depot.measured(new TrSodg(Depot.loggingLevel()), measures)
                ),
                new MapEntry<>(
                    "dot", Depot.measured(new TrDot(Depot.loggingLevel()), measures)
                ),
                new MapEntry<>("xembly", Depot.measured(new TrXembly(), measures)),
                new MapEntry<>("text", Depot.measured(new TrText(), measures)),
                new MapEntry<>(
                    "finish", Depot.measured(new TrFinish(Depot.loggingLevel()), measures)
                )
            )
        );
    }

    /**
     * Ctor.
     * @param trns The trains
     */
    Depot(final Map<String, Train<Shift>> trns) {
        this.trains = trns;
    }

    /**
     * Train.
     * @param name Name
     * @return Train
     */
    Train<Shift> train(final String name) {
        return this.trains.get(name);
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
     * Measured train.
     * @param train Train to measure
     * @param measures Measures
     * @return Measured train.
     */
    private static Train<Shift> measured(final Train<Shift> train, final File measures) {
        if (measures.getParentFile().mkdirs()) {
            Logger.debug(Depot.class, "Directory created for %[file]s", measures);
        }
        if (!measures.getParentFile().exists()) {
            throw new IllegalArgumentException(
                String.format(
                    "For some reason, the directory %s is absent, can't write measures to %s",
                    measures.getParentFile(),
                    measures
                )
            );
        }
        if (measures.isDirectory()) {
            throw new IllegalArgumentException(
                String.format(
                    "This is not a file but a directory, can't write to it: %s",
                    measures
                )
            );
        }
        return new TrLambda(
            train,
            shift -> new StMeasured(
                shift,
                measures.toPath()
            )
        );
    }
}
