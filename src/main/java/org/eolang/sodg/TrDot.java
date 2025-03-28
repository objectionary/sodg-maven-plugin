package org.eolang.sodg;

import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;
import com.yegor256.xsline.TrLogged;
import java.util.logging.Level;

final class TrDot extends TrEnvelope {

    /**
     * Ctor.
     */
    TrDot(final Level level) {
        super(
            new TrLogged(
                new TrFast(
                    new TrClasspath<>(
                        "/org/eolang/maven/sodg-to/normalize-attrs.xsl",
                        "/org/eolang/maven/sodg-to/to-dot.xsl"
                    ).back(),
                    Sodg.class
                ),
                Sodg.class,
                level
            )
        );
    }
}
