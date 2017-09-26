# QPPCT-323

`git checkout 1e61efb54df381aeef13fe3561b465df75212301`

## Changes
- Adds new CPC+ validation testing files

## Steps
1. Show Jira.  https://jira.cms.gov/browse/QPPCT-323
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithQuality_SampleQRDA-III-success.xml
1. Show that it passes
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithOnlyACI_SampleQRDA-III-failure.xml
1. Show that it fails
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithOnlyIA_SampleQRDA-III-failure.xml
1. Show that it fails
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithOnlyACIandIA_SampleQRDA-III-failure.xml
1. Show that it fails