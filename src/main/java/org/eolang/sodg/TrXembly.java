package org.eolang.sodg;

import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;

/**
 * Convert to Xembly.
 *
 * @since 0.1
 */
final class TrXembly extends TrEnvelope {

    /**
     * Ctor.
     */
    TrXembly() {
        super(
            new TrFast(
                new TrDefault<Shift>().with(
                    new StClasspath(
                        "/org/eolang/maven/sodg-to/to-xembly.xsl",
                        "testing no"
                    )
                ),
                Sodg.class
            )
        );
    }
}
