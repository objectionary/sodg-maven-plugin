/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

/**
 * If the attributes were not found in the Tojo.
 * This class was copy-pasted from objectionary/eo/eo-maven-plugin.
 * @since 0.35.0
 */
final class AttributeNotFoundException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 0x2F3A4B5C6D7E8F9AL;

    /**
     * Ctor.
     * @param attribute The attribute of Tojo.
     */
    AttributeNotFoundException(final TjsForeign.Attribute attribute) {
        super(String.format("There is no '%s' attribute in the tojo", attribute));
    }
}
