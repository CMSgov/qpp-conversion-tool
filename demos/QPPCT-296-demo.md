Latest commit for QPPCT-296 : bac0425b965c4eebedb0244bf17e8e5126fc9c61

1) Go into the qpp-conversion-tool/ directory on your terminal window. (command: cd /directories_leading_to/qpp-conversion-tool/)
2) Update your master branch using OR Go to the latest git commit for this demo by running:
   Commands to run from master:
   - git checkout master
   - git pull
   Commands to run from latest commit:
   - git checkout bac0425b965c4eebedb0244bf17e8e5126fc9c61
3) Build your branch using: mvn clean package
4) Run: ./convert.sh converter/src/test/resources/negative/mipsDenominatorInitialPopulationFailure.xml
5) After conversion a mipsDenominatorInitialPopulationFailure.err.json will be created, open it.
6) You will see that 3 errors will be inside. These errors pertain to 3 of the SubPopulations containing
Denominators that are greater than their specified Initial Populations.
7) You can search the mipsDenominatorInitialPopulationFailure.xml for:
   - `Has 1 Invalid Denom`   
   - `Has 2 Invalid Denom`
   File Location: converter/src/test/resources/negative/mipsDenominatorInitialPopulationFailure.xml
   These searches specify a measure that contain(s) a/an incorrect denominator(s).

OPTIONAL ADDITIONAL STEPS TO FIND THE ERROR IN THE XML USING XPATH PARSING IN INTELLIJ:

8) Using IntelliJ open the mipsDenominatorInitialPopulationFailure.xml located at mipsDenominatorInitialPopulationFailure.xml
9) Right click and click `Evaluate Xpath` and enter in the Xpath without quotes that is specified as `path: "xpath-located-here"` in the `mipsDenominatorInitialPopulationFailure.err.json`
10) Click `Evaluate` (The window will expand to the right so you mave have to move the window to see the button)
11) This will lead you to the XPATH which broke the validation rules.
