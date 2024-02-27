## Code Transformation Summary By Q

_Amazon Q made the following changes to your code. We verified the changes in Java 17.
You can review the summary details below._

### Files changed

| Files        | Action  |
|--------------|---------|
| `acceptance-tests/pom.xml` | Updated |
| `commandline/pom.xml` | Updated |
| `commons/pom.xml` | Updated |
| `converter/pom.xml` | Updated |
| `converter/qrdaToQppAssociations.txt` | Added |
| `generate/pom.xml` | Updated |
| `generate-race-cpcplus/pom.xml` | Updated |
| `pom.xml` | Updated |
| `qrda3-update-measures/pom.xml` | Updated |
| `rest-api/pom.xml` | Updated |
| `test-commons/pom.xml` | Updated |
| `test-coverage/pom.xml` | Updated |


The final build failed with the following errors:
```
Failed to execute goal org.gaul:modernizer-maven-plugin:2.0.0:modernizer (modernizer) on project test-commons: Execution modernizer of goal org.gaul:modernizer-maven-plugin:2.0.0:modernizer failed: Unsupported class file major version 61 -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/PluginExecutionException
[ERROR] 
[ERROR] After correcting the problems, you can resume the build with the command
[ERROR]   mvn <args> -rf :test-commons
```

### Next Steps
Please review and accept the code changes using the diff viewer. If you are using a Private Repository, please ensure that updated dependencies are available.

In order to successfully verify these changes on your machine, you will need to change your project to
use Java 17. We verified the changes using [Amazon Corretto](https://aws.amazon.com/corretto) Java 
17 build environment.