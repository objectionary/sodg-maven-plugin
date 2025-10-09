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

## Quick Start

Then, start with a simple EO program in the `order.eo` file:

```eo
[qty price] > order
  qty.mul > cost
    price
```

That can be translated to the following [XMIR][XMIR guide]:

```xml
<object>
   <o name="order">
      <o base="ξ" name="xi🌵"/>
      <o base="∅" name="qty"/>
      <o base="∅" name="price"/>
      <o base="ξ.qty.mul" name="cost">
         <o as="α0" base="ξ.price"/>
      </o>
   </o>
</object>
```

Then, add this plugin to your `pom.xml`:

```xml
<plugin>
   <groupId>org.eolang</groupId>
   <artifactId>sodg-maven-plugin</artifactId>
   <version>0.0.3</version>
   <executions>
      <execution>
         <goals>
            <goal>sodg</goal>
         </goals>
      </execution>
   </executions>
</plugin>
```

Or invoke it directly:

```bash
mvn org.eolang:sodg-maven-plugin:0.0.3:sodg
```

You should see `order.sodg` file being created under `target/eo/sodg` folder,
with the following content:

```sodg
formation(b1, "xi🌵", "qty", "price", "cost")
dispatch(b2, b1, "xi🌵")
dispatch(b3, b1, "qty")
dispatch(b4, b1, "price")
dispatch(b5, b3, "mul")
application(b6, b5, α0, b4)
put(b1, "cost", b6)
```

## SODG Format

To generate these text files, we first use an intermediate XML format:

```xml
<sodg>
   <i name="formation">
      <a>b1</a>
      <a>xi🌵</a>
      <a>qty</a>
      <a>price</a>
      <a>cost</a>
   </i>
   <i name="dispatch">
      <a>b2</a>
      <a>b1</a>
      <a>xi🌵</a>
   </i>
   <i name="dispatch">
      <a>b3</a>
      <a>b1</a>
      <a>qty</a>
   </i>
   <i name="dispatch">
      <a>b4</a>
      <a>b1</a>
      <a>price</a>
   </i>
   <i name="dispatch">
      <a>b5</a>
      <a>b3</a>
      <a>mul</a>
   </i>
   <i name="application">
      <a>b6</a>
      <a>b5</a>
      <a>α0</a>
      <a>b4</a>
   </i>
   <i name="put">
      <a>b1</a>
      <a>cost</a>
      <a>b6</a>
   </i>
</sodg>
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

## Configuration

You can configure the plugin goal's behavior using `<configuration/>`. The most
important parameters are:

* `scope` - current object scope, default: `compile`.
* `generateSodgXmlFiles` - shall we generate `.sodg.xml` files with SODGs?
* `generateXemblyFiles` - shall we generate `.xe` files with Xembly
instructions graph?
* `generateGraphFiles` - shall we generate `.graph.xml` files with XML graph?
* `generateDotFiles` - shall we generate `.dot` files with DOT language graph
commands?
* `sodgIncludes` - object names to participate in SODG generation, in the
[glob] format, default: `**`.
* `sodgExcludes` - object names to be excluded from SODG generation, in the
[glob] format.
* `failOnXmirErrors` - shall we fail SODG generation if XMIRs contain errors?

For `failOnXmirErrors` default value is `true`. It means that, if `.xmir`
contains `/object/errors`, then SODG generation will be failed:

```xml
<object>
   ...
   <errors>
      <error check="validate-object-name" severity="critical">...</error>
   </errors>
</object>
```

If `failOnXmirErrors` set to `false`, then even `.xmir` contains
`/object/errors`, the plugin will try to convert it to SODG.

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
[reo]: https://github.com/objectionary/reo
[Xembly]: https://www.xembly.org
[GraphViz]: https://graphviz.org
[XMIR guide]: https://news.eolang.org/2022-11-25-xmir-guide.html
[reo-tests]: https://github.com/objectionary/reo/tree/master/quick-tests
[glob]: https://en.wikipedia.org/wiki/Glob_(programming)
