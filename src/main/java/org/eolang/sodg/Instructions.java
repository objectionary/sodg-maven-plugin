package org.eolang.sodg;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @since 0.0.4
 */
public interface Instructions {

    int textInstructions(Path xmir, Path base) throws IOException;
}
