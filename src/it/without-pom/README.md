# Generates SODG files

Integration test that generates [SODG] files without `pom.xml`.

If you only need to run this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=without-pom -PskipUTs
```

[SODG]: https://github.com/objectionary/sodg-maven-plugin
