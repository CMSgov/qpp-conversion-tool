#QPPCT-315

`git checkout 96e07b4cf0e77fdd68b10a402b439009472ade97`
## Changes
- Adds new CPC+ validation to ensure the number of strata in measure-data.json for each measure match the number of
    performance rates that are decoded.

## Steps
1) Show JIRA (https://jira.cms.gov/browse/QPPCT-315)
2) Run converter on converter/src/test/resources/cpc_plus/CPCPLUS_Performance_Rate_Number-failure.xml
3) Show that it fails and show errors.
4) Run converter on converter/src/test/resources/cpc_plus/CPCPLUS_Performance_Rate_Sample-success.xml
5) Show that it converts properly.