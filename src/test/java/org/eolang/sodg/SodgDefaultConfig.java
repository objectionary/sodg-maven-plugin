package org.eolang.sodg;

import java.util.Map;
import org.cactoos.Scalar;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * Default config.
 * @since 0.0.3
 */
final class SodgDefaultConfig implements Scalar<Map<String, Boolean>> {
    @Override
    public Map<String, Boolean> value() {
        return new MapOf<>(
            new MapEntry<>("generateSodgXmlFiles", false),
            new MapEntry<>("generateXemblyFiles", false),
            new MapEntry<>("generateXemblyFiles", false),
            new MapEntry<>("generateGraphFiles", false),
            new MapEntry<>("generateDotFiles", false)
        );
    }
}
