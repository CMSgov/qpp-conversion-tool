@echo off
IF NOT EXIST java-conversion-tool\target\java-conversion-tool.jar (
    rem file doesn't exist
    echo .
    echo "Jar not found. Building..."
    echo .
    echo .
    start /b /wait cmd /C "mvn package -Dmaven.test.skip=true"
   
)

IF NOT EXIST java-conversion-tool\target\java-conversion-tool.jar (
	echo ""
	echo "Build failed. Aborting."
	exit 1
)

echo ""
start /b /wait cmd /C "java -jar java-conversion-tool/target/java-conversion-tool.jar %*"
