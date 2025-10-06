/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
log.contains("BUILD SUCCESS")
def generated = new File(basedir, 'target/eo/sodg/org/eolang/sodg/examples/')
assert generated.toPath().resolve("app.sodg").toFile().exists()
true
