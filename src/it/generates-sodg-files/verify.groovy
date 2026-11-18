/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS"): assertionMessage("Build was not successful")

def generated = new File(basedir, 'target/eo/sodg/org/eolang/sodg/examples/')
assert generated.toPath().resolve("fibonacci.sodg").toFile().exists(): assertionMessage("SODG file was not generated")
assert generated.toPath().resolve("fibonacci.sodg.xe").toFile().exists(): assertionMessage("SODG Xembly file was not generated")
assert generated.toPath().resolve("fibonacci.sodg.xml").toFile().exists(): assertionMessage("SODG XMIR file was not generated")

def instructions = new groovy.xml.XmlSlurper().parse(generated.toPath().resolve("fibonacci.sodg.xml").toFile()).i
assert instructions.findAll { it.@name == 'application' }.size() == 9: assertionMessage("Missing applications")
assert instructions.findAll { it.@name == 'application' }.every { it.a.size() == 4 }: assertionMessage("Incomplete application")
assert instructions.findAll { it.@name == 'put' }.every { it.a.size() == 3 }: assertionMessage("Incomplete put")
assert instructions.a.every { !it.text().trim().isEmpty() }: assertionMessage("Empty operand")
def created = instructions.findAll { it.@name in ['formation', 'dispatch', 'application'] }.collect { it.a[0].text() }.toSet()
instructions.each { instruction ->
    def name = instruction.@name.text()
    def operands = []
    if (name in ['delta', 'lambda']) {
        operands = [0]
    } else if (name == 'dispatch') {
        operands = [1]
    } else if (name == 'application') {
        operands = [1, 3]
    } else if (name == 'put') {
        operands = [0, 2]
    }
    operands.each { index ->
        def vertex = instruction.a[index].text()
        assert created.contains(vertex): assertionMessage("Uncreated object ${vertex} in ${name}")
    }
}

true

private String assertionMessage(String message) {
    return String.format(
      "'%s', you can find the entire log in the 'file://%s' file",
      message,
      new File(basedir, 'build.log').absolutePath)
}
