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
import java.util.Locale;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
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
    void executesMojoWithoutMavenProject(@Mktmp final Path temp) throws MavenInvocationException {
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
    private static InvocationResult executed(final File wdir, final String goal)
        throws MavenInvocationException {
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
    private static Path executableMavenPath() {
        final String executable;
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
            executable = "mvnw.cmd";
        } else {
            executable = "mvnw";
        }
        return Paths.get(executable).toAbsolutePath();
    }
}
