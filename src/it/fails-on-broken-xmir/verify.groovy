/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD FAILURE"): assertionMessage("Build was not successful")

def generated = new File(basedir, 'target/eo/sodg/org/eolang/sodg/examples/')
assert !generated.toPath().resolve("app.sodg").toFile().exists(): assertionMessage("SODG was generated, but it should not")

true

private String assertionMessage(String message) {
  return String.format(
    "'%s', you can find the entire log in the 'file://%s' file",
    message,
    new File(basedir, 'build.log').absolutePath)
}
