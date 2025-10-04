/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Instructions.
 * @since 0.0.4
 */
interface Instructions {

    /**
     * Total instructions rendered, from XMIR to SODG.
     * @param xmir Path to XMIR
     * @param base Base path
     * @return The number of total instructions rendered
     * @throws IOException if I/O operation fails
     */
    int textInstructions(Path xmir, Path base) throws IOException;
}
