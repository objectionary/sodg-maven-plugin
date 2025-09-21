# Sodg-Maven-Plugin

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/objectionary/sodg-maven-plugin)](https://www.rultor.com/p/objectionary/sodg-maven-plugin)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn-linux](https://github.com/objectionary/sodg-maven-plugin/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/sodg-maven-plugin/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=objectionary/sodg-maven-plugin)](https://www.0pdd.com/p?name=objectionary/sodg-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/sodg-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/sodg-maven-plugin)
[![codecov](https://codecov.io/gh/objectionary/sodg-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/sodg-maven-plugin)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/sodg-maven-plugin)](https://hitsofcode.com/view/github/objectionary/sodg-maven-plugin)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/sodg-maven-plugin/blob/master/LICENSE.txt)

The `sodg-maven-plugin` builds a graph from an EO program.
SODG stands for Surging Object DiGraph. The primary consumer of SODG graphs is
the [reo] project. You can find [some examples][reo-tests] of `.sodg` files.

To generate these text files, we first use an intermediate XML format:

```xml
<sodg>
  <i name='formation'>
    <a>b1</a>
    <a>bar</a>
  </i>
  <i name='dispatch'>
    <a>b2</a>
    <a>b1</a>
    <a>bar</a>
  </i>
  <i name="dispatch">
    <a>b3</a>
    <a>b2</a>
    <a>mul</a>
  </i>
  <i name="application">
    <a>b4</a>
    <a>b3</a>
    <a>α0</a>
    <a>b2</a>
  </i>
  <i name="put">
    <a>b1</a>
    <a>result</a>
    <a>b4</a>
  </i>
</sodg>
```

Which is equivalent to:

```sodg
formation(b1, "qty")
dispatch(b2, b1, "qty")
dispatch(b3, b2, "mul")
application(b4, b3, α0, b2)
put(b1, "result", b4)
```

This DSL consists of these commands:

* `formation(o, attr0, attr1, ...)` – creates a new formation object
* `dispatch(o, base, attr)` – creates a new dispatch object
* `application(o, proto, attr, arg)` – creates a new application object
* `delta(o, data)` – sets Δ-asset to the object
* `lambda(o, name)` - sets λ-asset to the object
* `put(o, attr, kid)` – sets attribute of the object

The `sodg-maven-plugin` performs the following steps:

1. Turns [`.xmir`][XMIR guide] into
   intermediate XML
2. Turns XML into a `.dot` graph (renderable
   by [GraphViz])
3. Turns XML into `.sodg` text file (with DSL)
4. Turns XML into `.xe` ([Xembly]) file that, if
   executed, would generate a graph in
   XML
5. Turns XML into `.graph.xml`, which is a graph as XML

The `.sodg` file is essentially a sequence of instructions for a virtual machine
that parses them. An example of such a machine can be found in the [SODG] repo.
When the graph is built by the virtual machine, it must be possible to execute
a program using graph traversing algorithm. Once the graph is built, it can be
used to execute a program via a graph traversal algorithm. An example of such
an executor is [reo].

## How to Contribute

Fork repository, make changes, then send us
a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
./mvnw clean install -Pqulice
```

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.

[SODG]: https://github.com/objectionary/sodg
[reo]: https://github.com/objectionary/reo
[Xembly]: https://www.xembly.org
[GraphViz]: https://graphviz.org
[XMIR guide]: https://news.eolang.org/2022-11-25-xmir-guide.html
[reo-tests]: https://github.com/objectionary/reo/tree/master/quick-tests
