# sodg-maven-plugin

SODG (Surging Object DiGraph) Maven Plugin is a plugin for Maven that allows to
build a graph from an EO program.
SODG is our own format of graph representation.
It essentially is a text file that consists of instructions for a virtual
machine that is capable of parsing them and building a graph. An example
of such a machine can be found
in [this repository](https://github.com/objectionary/sodg).
When the graph is built by the virtual machine, it must be possible to execute
a program using graph traversing algorithm. An example of such an executor
of a graph can be found
in [this repository](https://github.com/objectionary/reo).