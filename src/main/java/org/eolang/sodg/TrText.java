package org.eolang.sodg;

import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;

final class TrText extends TrEnvelope {

    /**
     * Ctor.
     */
    TrText() {
        super(
            new TrFast(
                new TrClasspath<>(
                    "/org/eolang/maven/sodg-to/normalize-names.xsl",
                    "/org/eolang/maven/sodg-to/to-text.xsl"
                ).back(),
                Sodg.class
            )
        );
    }
}
