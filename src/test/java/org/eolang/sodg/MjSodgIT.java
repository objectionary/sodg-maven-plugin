/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.WeAreOnline;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test for {@link MjSodg}.
 *
 * @since 0.0.0
 */
@SuppressWarnings("JTCOP.RuleEveryTestHasProductionClass")
@ExtendWith(WeAreOnline.class)
@ExtendWith(MktmpResolver.class)
final class MjSodgIT {

    @Test
    void executesMojoWithoutMavenProject(@Mktmp final Path temp) throws Exception {
        MatcherAssert.assertThat(
            "Mojo is not runnable without a pom.xml (project), but it should",
            MjSodgIT.executed(
                temp.toFile(),
                String.format(
                    "org.eolang:sodg-maven-plugin:%s:sodg", System.getProperty("sodg.version")
                )
            ).getExitCode(),
            Matchers.equalTo(0)
        );
    }

    /**
     * Executed invocation.
     * @param wdir Working directory
     * @param goal Goal to run
     * @return Invocation result
     * @throws MavenInvocationException if maven fails
     */
    private static InvocationResult executed(final File wdir, final String goal) throws Exception {
        final InvocationRequest request = new DefaultInvocationRequest();
        request.addArg(goal);
        request.setBaseDirectory(wdir);
        final Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(MjSodgIT.executableMavenPath().toFile());
        return invoker.execute(request);
    }

    /**
     * Executable path for `mvn`.
     * @return Path to executable maven
     */
    private static Path executableMavenPath() throws Exception {
        final String executable;
        if (System.getenv("MAVEN_PATH") != null) {
            executable = System.getenv("MAVEN_PATH");
        } else {
            executable = new TextOf(
                new ProcessBuilder(
                    "/usr/bin/env", "sh", "-lc", "command -v mvn || which mvn"
                ).start().getInputStream()
            ).asString().trim();
        }
        return Paths.get(executable);
    }
}
