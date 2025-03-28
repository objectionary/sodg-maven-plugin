/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.tojos.Tojo;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Foreign tojo.
 * This class was copy-pasted from objectionary/eo/eo-maven-plugin.
 * @since 0.30
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
final class TjForeign {

    /**
     * The delegate.
     */
    private final Tojo delegate;

    /**
     * Ctor.
     * @param original The delegate.
     */
    TjForeign(final Tojo original) {
        this.delegate = original;
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other == null || this.getClass() != other.getClass()) {
            result = false;
        } else {
            final TjForeign tojo = (TjForeign) other;
            result = Objects.equals(this.delegate, tojo.delegate);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.delegate);
    }

    /**
     * The id of the tojo.
     * @return The id of the tojo.
     */
    String identifier() {
        return this.attribute(TjsForeign.Attribute.ID);
    }

    /**
     * The tojo shaken xmir.
     * @return The shaken xmir.
     */
    Path shaken() {
        return Paths.get(this.attribute(TjsForeign.Attribute.SHAKEN));
    }

    /**
     * Set sodg.
     * @param sodg Sodg.
     * @return The tojo itself.
     */
    TjForeign withSodg(final Path sodg) {
        this.delegate.set(TjsForeign.Attribute.SODG.getKey(), sodg.toString());
        return this;
    }

    /**
     * Return the attribute from the tojo.
     * @param attribute The attribute from ForeignTojos.Attribute.
     * @return The attribute.
     */
    private String attribute(final TjsForeign.Attribute attribute) {
        final String attr = this.delegate.get(attribute.getKey());
        if (attr == null) {
            throw new AttributeNotFoundException(attribute);
        }
        return attr;
    }
}
