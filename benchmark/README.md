Benchmarking using [jmh][jmh].

To execute tests:

```
cd benchmarking
mvn clean package && java -jar target/benchmarks.jar
```

Sample output:

```
Result "gov.cms.qpp.conversion.ConverterBenchmark.benchmarkMain":
  0.069 ±(99.9%) 0.010 s/op [Average]
  (min, avg, max) = (0.054, 0.069, 0.085), stdev = 0.009
  CI (99.9%): [0.059, 0.079] (assumes normal distribution)


# Run complete. Total time: 00:00:42

Benchmark                          Mode  Cnt   Score   Error  Units
ConverterBenchmark.benchmarkMain  thrpt   15  15.360 ± 2.140  ops/s
ConverterBenchmark.benchmarkMain   avgt   15   0.069 ± 0.010   s/op
```

[jmh]: http://openjdk.java.net/projects/code-tools/jmh/