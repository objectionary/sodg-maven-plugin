<img alt="logo" src="https://www.objectionary.com/cactus.svg" height="100px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/objectionary/sodg-maven-plugin)](https://www.rultor.com/p/objectionary/sodg-maven-plugin)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn-linux](https://github.com/objectionary/sodg-maven-plugin/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/sodg-maven-plugin/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=objectionary/sodg-maven-plugin)](https://www.0pdd.com/p?name=objectionary/sodg-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/sodg-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/sodg-maven-plugin)
[![codecov](https://codecov.io/gh/objectionary/sodg-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/sodg-maven-plugin)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/sodg-maven-plugin)](https://hitsofcode.com/view/github/objectionary/sodg-maven-plugin)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/sodg-maven-plugin/blob/master/LICENSE.txt)

> ⚠️ **Note:** This project is currently outdated.  
> You may need to update dependencies and ensure compatibility with the latest
> EO versions before using it.  
> It is **not deprecated**, but active maintenance is currently paused.

The `sodg-maven-plugin` builds a graph from an EO program.
SODG stands for Surging Object DiGraph.  
The primary consumer of SODG graphs is
the [reo](https://github.com/objectionary/reo) project.
You can
find [some examples](https://github.com/objectionary/reo/tree/master/quick-tests)
of `.sodg` files.

To generate these text files, we first use an intermediate XML format:

```xml

<i name='ADD'>
  <a>v1</a>
</i>
<i name='ADD'>
  <a>v2</a>
</i>
<i name='BIND'>
  <a>v1</a>
  <a>v2</a>
</i>
```

Which is equivalent to:

```
ADD v1
ADD v2
BIND v1, v2
```

This DSL consists of only three commands:

`ADD` (one argument) - adds a new node to the graph
`BIND` (two arguments) - creates a directed edge from one node to another
`PUT` (one argument) - attaches data to a node

The `sodg-maven-plugin` performs the following steps:

1. Turns [`.xmir`](https://news.eolang.org/2022-11-25-xmir-guide.html) into
   intermediate XML
2. Turns XML into a `.dot` graph (renderable
   by [GraphViz](https://graphviz.org))
3. Turns XML into `.sodg` text file (with DSL)
4. Turns XML into `.xe` ([Xembly](https://www.xembly.org)) file that, if
   executed, would generate a graph in
   XML
5. Turns XML into `.graph.xml`, which is a graph as XML

The `.sodg` file is essentially a sequence of instructions for a virtual machine
that parses them.
An example of such a machine can be found in the [SODG] repo.
When the graph is built by the virtual machine, it must be possible to execute
a program using graph traversing algorithm. Once the graph is built,
it can be used to execute a program via a graph traversal algorithm.
An example of such an executor is [reo](https://github.com/objectionary/reo).

## How to Contribute

Fork repository, make changes, then send us
a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.

[SODG]: https://github.com/objectionary/sodg
