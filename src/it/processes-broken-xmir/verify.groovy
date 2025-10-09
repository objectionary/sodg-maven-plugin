/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
assert new File(basedir, 'build.log').text.contains("BUILD SUCCESS")
assert new File(basedir, 'target/eo/sodg/org/eolang/sodg/examples/').toPath()
  .resolve("broken.sodg")
  .toFile()
  .exists()
true
