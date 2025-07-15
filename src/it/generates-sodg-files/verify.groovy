/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): assertionMessage("Build was not successful")

def generated = new File(basedir, 'target/eo/sodg/org/eolang/examples/')
assert generated.toPath().resolve("fibonacci.sodg").toFile().exists(): assertionMessage("SODG file was not generated")
assert generated.toPath().resolve("fibonacci.sodg.xe").toFile().exists(): assertionMessage("SODG Xembly file was not generated")
assert generated.toPath().resolve("fibonacci.sodg.xml").toFile().exists(): assertionMessage("SODG XMIR file was not generated")

true

private String assertionMessage(String message) {
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}
