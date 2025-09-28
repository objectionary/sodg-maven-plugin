package org.eolang.sodg;

import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Train;

/**
 * Train depo.
 * @since 0.0.3
 */
interface Depo {

    Train<Shift> train(String name);

    Boolean value(String env);
}
