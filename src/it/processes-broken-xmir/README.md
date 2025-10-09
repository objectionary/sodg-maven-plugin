# Generates SODG files

Integration test that generates [SODG] for broken XMIR.

If you only need to run this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=processes-broken-xmir -PskipUTs
```

[SODG]: https://github.com/objectionary/sodg-maven-plugin
