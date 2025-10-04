# Generates SODG files

Integration test that fails [SODG] generation on broken XMIR.

If you only need to run this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=fails-on-broken-xmir -PskipUTs
```

[SODG]: https://github.com/objectionary/sodg-maven-plugin
