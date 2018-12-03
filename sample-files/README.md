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

The sample file does contain some ACI and IA measures including the ACI attestation measures.

Sample files available for ACI and IA measures are:

* MIPS_GROUP_QRDA_III_ACI_IA_Sample1.xml
* MIPS_GROUP_QRDA_III_ACI_IA_Sample2.xml

These 2 files cover all of the ACI and IA measures.
Other information about the files:

Both files contain all improvement activities for the 2017 performance period.
Some of the advancing care information measures are mutually exclusive, such as ACI_EP_1
(ePrescribing) and ACI_LVPP_1 (Proposed ePrescribing Exclusion). Sample1 contains all
advancing care information measures except the exclusion identifiers (e.g.,
include ACI_EP_1 but not ACI_LVPP_1). Sample 2 also contains all but with the
exclusions (e.g., include ACI_LVPP_1 but not ACI_EP_1).

ACI attestation measures are included in both sample files.

Large QRDA files

    comprehensive-qrda.xml

This is a representation of
* all strata bearing quality measures tracked in measures-data.json
* all proportional ACI measures tracked in measures-data.json
* all IA measures tracked in measures-data.json

Error scenarios

    * error-NaN-numerator-qrda.xml (ACI proportion measure with non-numeric numerator)
    * error-invalid-uuid-qrda.xml (eCQM measure popuation with an invalid uuid)
    * error-missing-subpopulation-qrda.xml (eCQM measure missing a required popuation)
    * error-nonboolean-value-qrda.xml (IA boolean measure with a non-boolean value)

CPC+ Sample Files

These files should validate successfully for both the conversion tool validation and
the Submission Validation.

* CPCPlus_Success_PreProd.xml - For pre-prod environments that require TIN and NPI that start with '000'
* CPCPlus_Success_Prod.xml - For the production environment

CPC+ error scenarios for Conversion Tool CPC+ specific validations

These files do not cover validations performed by the Submission Validation API, nor
do they cover validations that are also performed for MIPS Programs.

All errors and error messages can be found in https://github.com/CMSgov/qpp-conversion-tool/blob/master/ERROR_MESSAGES.md

* CPCPlus_No_TINs.xml
  * Errors: 54, 63, 66
* CPCPlus_No_APM_ID.xml
  * Errors: 35
* CPCPlus_Empty_APM_ID.xml
  * Errors: 62
* CPCPlus_Bad_Address.xml
  * Errors: 34, 63
* CPCPlus_No_Quality.xml
  * Errors: 36, 63
* CPCPlus_WrongStartDate.xml
  * Errors: 63, 55
* CPCPlus_WrongEndDate.xml
  * Errors: 63, 56
* CPCPlus_Group1_Wrong_Num.xml
  * Errors: 63, 64
* CPCPlus_Total_Wrong_Num.xml
  * Errors: 63, 65
* CPCPlus_No_Perf_Rate.xml
  * Errors: 63, 59, 37
* CPCPlus_Strat.xml
  * Errors: 63, 49, 48
* CPCPlus_Strat2.xml
  * Errors: 63, 14, 59, 37
