# QPPCT-420

`git checkout 39ebf7e8e923158078f41c30c95f52bc4b27c65b`

## Steps
1. Show JIRA. https://jira.cms.gov/browse/QPPCT-420
1. Show conversion of MIPS file with measures missing performance rate nodes.
	* ./convert.sh sample-files/MIPS_GROUP_QRDA_III_ACI_IA_Sample1.xml
1. Show that no errors occurred.
1. Show conversion of MIPS file with measure containing incorrect performance rate UUID nodes.
	* ./convert.sh converter/src/test/resources/negative/mipsInvalidPerformanceRateUuid.xml
1. Show that error(s) occured.
1. Show conversion of valid CPC+ performance rate file.
	* ./convert.sh converter/src/test/resources/cpc_plus/success/CPCPLUS_Performance_Rate_Sample.xml
1. Show that no errors occur.	
1. Show conversion of CPC+ file with measure missing performance rate nodes.
    * ./convert.sh converter/src/test/resources/cpc_plus/failure/CPCPLUS_Performance_Rate_Number.xml
1. Show that errors occurred.
