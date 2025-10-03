package org.eolang.sodg;

import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.Mono;
import com.yegor256.tojos.TjDefault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;

/**
 * @since 0.0.3
 */
final class TjsXmir {

    private final Path home;
    private final Map<String, String> programs;

    TjsXmir(final Path home, final Map<String, String> programs) {
        this.home = home;
        this.programs = programs;
    }

    Collection<TjForeign> asTojos() throws IOException {
        try (final Mono mono = new MnCsv(this.home.resolve("foreign.csv"))){
            final Collection<Map<String, String>> foreigns = new ArrayList<>();
            this.programs.forEach(
                (name, sources) -> {
                    final Path xmir = this.home.resolve(String.format("%s.xmir", name));
                    try {
                        Files.write(
                            xmir, new EoSyntax(sources).parsed().toString().getBytes(StandardCharsets.UTF_8)
                        );
                    } catch (final IOException exception) {
                        throw new IllegalStateException(
                            String.format("Failed to write XMIR to %s", xmir), exception
                        );
                    }
                    foreigns.add(
                        new MapOf<>(
                            new MapEntry<>(
                                "id",
                                String.format("org.eolang.sodg.examples.%s", name)
                            ),
                            new MapEntry<>("xmir", xmir.toString()),
                            new MapEntry<>("scope", "compile")
                        )
                    );
                }
            );
            mono.write(foreigns);
            return new TjsForeign(() -> new TjDefault(mono), () -> "compile").withXmir();
        }
    }
}
