# Sample files

Current sample files available for eCQM measures are:

* quality-mips-1.xml
* quality-mips-3.xml
* quality-mips-4.xml

These 3 files cover the following eCQM measures:

* 001 - CMS122v5
* 309 - CMS124v5
* 112 - CMS125v5
* 113 - CMS130v5
* 117 - CMS131v5
* 305 - CMS137v5
* 226 - CMS138v5
* 318 - CMS139v5
* 281 - CMS149v5
* 238 - CMS156v5
* 370 - CMS159v5
* 236 - CMS165v5
* 312 - CMS166v6
* 374 - CMS50v5

One additional sample file covers the remaining eCQM measures:

* MIPS_GROUP_Sample_QRDA_III.xml

The sample file contains CMS125v5, CMS130v5, CMS165v5, CMS122v5, CMS68v6, CMS139v5, CMS144v5, CMS127v5, CMS2v6, CMS138v5, and CMS136v6, which includes the data for supplemental data elements.

In the attached sample file, no supplemental data elements were provided for the following eCQMs: CMS22v5, CMS52v5, CMS56v5, CMS65v6, CMS66v5, CMS69v5, CMS74v6, CMS75v5, CMS82v4, CMS90v6, CMS117v5, CMS123v5, CMS128v5, CMS129v6, CMS132v5, CMS133v5, CMS134v5, CMS135v5, CMS142v5, CMS143v5, CMS145v5, CMS146v5, CMS153v5, CMS154v5, CMS155v5, CMS157v5, CMS158v5, CMS160v5, CMS161v5, CMS164v5, CMS167v5, CMS169v5, CMS177v5. For those eCQMs that have reporting stratifications, no reporting stratifications data are provided in the sample. Both supplemental data elements and reporting stratifications are not supported and out of scope for MIPS.

Sample files available for ACI and IA measures are:

* MIPS_GROUP_QRDA_III_ACI_IA_Sample1.xml
* MIPS_GROUP_QRDA_III_ACI_IA_Sample1.xml

These 2 files cover all of the ACI and IA measures.
Other information about the files:

Both files contain all improvement activities for the 2017 performance period.
Some of the advancing care information measures are mutually exclusive, such as ACI_EP_1
(ePrescribing) and ACI_LVPP_1 (Proposed ePrescribing Exclusion). Sample1 contains all
advancing care information measures except the exclusion identifiers (e.g.,
include ACI_EP_1 but not ACI_LVPP_1). Sample 2 also contains all but with the
exclusions (e.g., include ACI_LVPP_1 but not ACI_EP_1). The attestation identifiers
are not included.

Large QRDA file

* comprehensive-qrda.xml

This is a representation of 
* all strata bearing quality measures tracked in measures-data.json
* all proportional ACI measures tracked in measures-data.json
* all IA measures tracked in measures-data.json
