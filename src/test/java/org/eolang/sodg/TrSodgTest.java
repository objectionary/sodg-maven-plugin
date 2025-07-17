/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.util.logging.Level;
import org.eolang.parser.EoSyntax;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TrSodg}.
 *
 * @since 0.0.2
 */
final class TrSodgTest {

    @Test
    void transformsSimpleXmirToGraph() throws IOException {
        final XML after = new Xsline(new TrSodg(Level.FINEST)).pass(
            new EoSyntax(
                String.join(
                    "\n",
                    "# App.",
                    "[] > app",
                    "  QQ.stdout > @",
                    "    QQ.txt.sprintf",
                    "      \"Hello, %s!\"",
                    "      * \"Jeff\""
                )
            ).parsed()
        );
        System.out.println(after);
    }
}
