Latest commit for QPPCT-296 : 3b72d1d37b9e2447ed6ca386165dfd1284c2526a

1) Go into the qpp-conversion-tool/ directory on your terminal window. (command: cd /directories_leading_to/qpp-conversion-tool/)
2) Run git checkout 3b72d1d37b9e2447ed6ca386165dfd1284c2526a without the
3) Run ./convert.sh converter/src/test/resources/negative/mipsDenominatorInitialPopulationFailure.xml
4) After conversion a mipsDenominatorInitialPopulationFailure.err.json will be created, open it.
5) You will see that 3 errors will be inside. These errors pertain to 3 of the SubPopulations containing
Denominators that are greater than their specified Initial Populations.
6) You can search the mipsDenominatorInitialPopulationFailure.xml for 'Invalid Denom' to find the Sub populations containing the Denominator and Initial Population that caused the errors.

OPTIONAL ADDITIONAL STEPS TO FIND THE ERROR IN THE XML USING XPATH PARSING:

7) Using IntelliJ open the mipsDenominatorInitialPopulationFailure.xml located at converter/src/test/resources/negative/
8) Right click and click `Evaluate Xpath` and enter in the Xpath without quotes that is specified as `path: "xpath-located-here"` in the `mipsDenominatorInitialPopulationFailure.err.json`
9) Click `Evaluate` (The window will expand to the right so you mave have to move the window to see the button)
10) This will lead you to the XPATH which broke the validation rules.
