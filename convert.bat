@echo off
IF NOT EXIST commandline\target\commandline (
    rem file doesn't exist
    echo .
    echo "Jar not found. Building..."
    echo .
    echo .
    start /b /wait cmd /C "mvn package -Dmaven.test.skip=true"
   
)

IF NOT EXIST commandline\target\commandline.jar (
	echo ""
	echo "Build failed. Aborting."

)

echo ""
start /b /wait cmd /C "java -jar commandline/target/commandline.jar %*"

