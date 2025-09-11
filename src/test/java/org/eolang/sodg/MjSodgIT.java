/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.jcabi.manifests.Manifests;
import com.yegor256.MayBeSlow;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.WeAreOnline;
import com.yegor256.farea.Farea;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration tests for {@link MjSodg}.
 *
 * @since 0.1
 */
@ExtendWith({WeAreOnline.class, MktmpResolver.class, MayBeSlow.class})
final class MjSodgIT {
    @Test
    void convertsSimpleObjectToGraph(@Mktmp final Path temp) throws Exception {
        new Farea(temp).together(
            f -> {
                f.clean();
                f.files().file("src/main/eo/foo.eo").write(
                    "# Check SodgMojo.\n[] > foo\n".getBytes()
                );
                new AppendedPlugin(f).value().goals("register", "parse");
                f.build().plugins().append(
                    "org.eolang", "sodg-maven-plugin", Manifests.read("Sodg-Version")
                ).execution().goals("sodg");
                f.exec("process-sources");
                MatcherAssert.assertThat(
                    "the .sodg file is generated",
                    f.files().file("target/eo/sodg/foo.sodg").exists(),
                    Matchers.is(true)
                );
            }
        );
    }
}
