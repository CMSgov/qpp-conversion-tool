# QPPCT-326

`git checkout 3aae9fb6b88070c39b44f70bc70e2b3e6897308f`

## Changes
- Adds new CPC+ validation testing files

## Steps
1. Show Jira.  https://jira.cms.gov/browse/QPPCT-326
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithQuality_SampleQRDA-III-success.xml
1. Show that it passes
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithQualityIncorrectStartDate_SampleQRDA-III-failure.xml
1. Show that it fails
1. Run converter on converter/src/test/resources/cpc_plus/CPCPlus_WithQualityIncorrectEndDate_SampleQRDA-III-failure.xml
1. Show that it fails