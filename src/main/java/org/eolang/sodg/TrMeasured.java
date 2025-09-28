package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.TrLambda;
import com.yegor256.xsline.Train;
import java.io.File;
import java.util.Iterator;
import org.cactoos.Scalar;

/**
 * Measured train.
 * @since 0.0.3
 */
final class TrMeasured implements Train<Shift> {
    /**
     * The origin
     */
    private final Train<Shift> origin;

    /**
     * XSL Measures.
     */
    private final File measures;

    /**
     * Ctor.
     * @param train Train
     * @param msrs XSL Measures
     */
    TrMeasured(final Train<Shift> train, final File msrs) {
        this.origin = train;
        this.measures = msrs;
    }

    private Train<Shift> measured() {
        if (this.measures.getParentFile().mkdirs()) {
            Logger.debug(this, "Directory created for %[file]s", this.measures);
        }
        if (!this.measures.getParentFile().exists()) {
            throw new IllegalArgumentException(
                String.format(
                    "For some reason, the directory %s is absent, can't write measures to %s",
                    this.measures.getParentFile(),
                    this.measures
                )
            );
        }
        if (this.measures.isDirectory()) {
            throw new IllegalArgumentException(
                String.format(
                    "This is not a file but a directory, can't write to it: %s",
                    this.measures
                )
            );
        }
        return new TrLambda(
            this.origin,
            shift -> new StMeasured(
                shift,
                this.measures.toPath()
            )
        );
    }

    @Override
    public Train<Shift> with(final Shift element) {
        return this.measured().with(element);
    }

    @Override
    public Train<Shift> empty() {
        return this.origin.empty();
    }

    @Override
    public Iterator<Shift> iterator() {
        return this.origin.iterator();
    }
}
