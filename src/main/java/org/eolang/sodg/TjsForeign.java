/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.tojos.Tojo;
import com.yegor256.tojos.Tojos;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * Foreign tojos.
 *
 * @since 0.30
 */
@SuppressWarnings("PMD.TooManyMethods")
final class TjsForeign implements Closeable {

    /**
     * The delegate.
     */
    private final Unchecked<? extends Tojos> tojos;

    /**
     * Scope.
     */
    private final Supplier<String> scope;

    /**
     * Ctor.
     * @param scalar Scalar
     * @param scope Scope
     */
    TjsForeign(final Scalar<Tojos> scalar, final Supplier<String> scope) {
        this(new Unchecked<>(new Sticky<>(scalar)), scope);
    }

    /**
     * Main constructor.
     * @param tojos The tojos.
     * @param scope The scope.
     */
    private TjsForeign(
        final Unchecked<Tojos> tojos,
        final Supplier<String> scope
    ) {
        this.tojos = tojos;
        this.scope = scope;
    }

    @Override
    public void close() throws IOException {
        this.tojos.value().close();
    }

    /**
     * Get the tojos that have corresponding shaken XMIR.
     * @return The tojos.
     */
    Collection<TjForeign> withShaken() {
        return this.select(row -> row.exists(Attribute.SHAKEN.getKey()));
    }

    /**
     * Select tojos.
     * @param filter Filter.
     * @return Selected tojos.
     */
    private Collection<TjForeign> select(final Predicate<? super Tojo> filter) {
        final Predicate<Tojo> scoped = t ->
            t.get(Attribute.SCOPE.getKey()).equals(this.scope.get());
        return this.tojos.value()
            .select(t -> filter.test(t) && scoped.test(t))
            .stream().map(TjForeign::new).collect(Collectors.toList());
    }

    /**
     * Foreign tojo attributes.
     */
    enum Attribute {

        /**
         * FQN of the object, e.g. {@code org.eolang.number}.
         */
        ID("id"),

        /**
         * Absolute path of the shaken {@code .xmir} file.
         */
        SHAKEN("shaken"),

        /**
         * Absolute path of the SODG file.
         */
        SODG("sodg"),

        /**
         * The scope of compilation, either {@code compile} or {@code test}.
         */
        SCOPE("scope");


        /**
         * Attribute name.
         */
        private final String key;

        /**
         * Ctor.
         * @param attribute The attribute name.
         */
        Attribute(final String attribute) {
            this.key = attribute;
        }

        /**
         * Get the attribute name.
         * @return The attribute name.
         */
        String getKey() {
            return this.key;
        }
    }
}
