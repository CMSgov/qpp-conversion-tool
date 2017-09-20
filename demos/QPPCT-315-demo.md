Git hub commit hash: 96e07b4cf0e77fdd68b10a402b439009472ade97

1) Go into the qpp-conversion-tool/ directory on your terminal window. (command: cd /directories_leading_to/qpp-conversion-tool/)
2) Update your master branch using: git pull OR Go to the latest git commit for this demo by running: git checkout 96e07b4cf0e77fdd68b10a402b439009472ade97
3) Build this branch using `mvn clean package`. This will take some time to build.
4) Run ./convert.sh converter/src/test/resources/cpc_plus/CPCPLUS_Performance_Rate_Number-failure.xml
5) This will create `CPCPLUS_Performance_Rate_Number-failure.err.json`. Open this file.
6) There will be 3 errors in this file. These errors pertain to the number of Sub populations required for each measure in the measure-data.json file.
7) You can find each error by searching the XML for:
   - `CMS130(No Performance rates)`
   - `CMS131(Too many performance rates)`
   - `CMS137(Too few performance rates)`
8) You will notice that the performance rate sections have either too many or too few performance rate.
