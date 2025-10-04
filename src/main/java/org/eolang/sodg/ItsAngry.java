package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Angry instructions.
 * @since 0.0.4
 */
public class ItsAngry implements Instructions {

    /**
     * The origin.
     */
    private final Instructions origin;

    /**
     * Fail or not.
     */
    private final boolean exit;

    /**
     * Ctor.
     * @param instructions Instructions
     * @param ext Fail or not
     */
    public ItsAngry(final Instructions instructions, final boolean ext) {
        this.origin = instructions;
        this.exit = ext;
    }

    @Override
    public int textInstructions(final Path xmir, final Path base) throws IOException {
        final XML document = new XMLDocument(xmir);
        if (!document.nodes("/object/errors").isEmpty() && this.exit) {
            final String message = String.format(
                "Failing SODG generation, since the object in '%s' contains errors ('failOnXmirErrors=true'):\n%s",
                xmir, document
            );
            Logger.error(this, message);
            throw new IllegalStateException(message);
        } else {
            return this.origin.textInstructions(xmir, base);
        }
    }
}
