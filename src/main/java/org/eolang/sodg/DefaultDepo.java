/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Train;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * Default depo.
 * @since 0.0.3
 */
class DefaultDepo implements Depo {

    /**
     * The train.
     */
    private final Map<String, Train<Shift>> trains;

    /**
     * Ctor.
     * @param measures Measures
     */
    DefaultDepo(final File measures) {
        this(
            new MapOf<>(
                new MapEntry<>(
                    "sodg", new TrMeasured(new TrSodg(DefaultDepo.loggingLevel()), measures)
                ),
                new MapEntry<>(
                    "dot", new TrMeasured(new TrDot(DefaultDepo.loggingLevel()), measures)
                ),
                new MapEntry<>("xembly", new TrMeasured(new TrXembly(), measures)),
                new MapEntry<>("text", new TrMeasured(new TrText(), measures)),
                new MapEntry<>(
                    "finish", new TrMeasured(new TrFinish(DefaultDepo.loggingLevel()), measures)
                )
            )
        );
    }

    /**
     * Ctor.
     * @param trns The trains
     */
    DefaultDepo(final Map<String, Train<Shift>> trns) {
        this.trains = trns;
    }

    @Override
    public Train<Shift> train(final String name) {
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
}
