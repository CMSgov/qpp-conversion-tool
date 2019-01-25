require_relative 'qpp_page'

class Validation_Messages_Page < QPP_Page
  include Test::Unit::Assertions
  include PageObject

  @@Perf_period_aci_help = 'A Performance Period of 90 days or greater allows a higher score potential for the Advancing Care Information category. If a date range less than 90 days is selected, then the highest Category Score for Base Measures available is 50 points, and Bonus Measures will be unavailable for attestation.'
  @@Perf_period_ia_help = 'A Performance Period of at least 1 day or greater is required for the Improvement Activities category, other than the Patient Centered Medical Home attestation.'
  @@Pre_Attestation_ACI_help = 'You must attest Yes to these Attestation Statements in order to move forward with the Advancing Care Information category. You can read more about these statements by clicking the arrow to the left of each statement.'
  @@Base_measure_help = "These measures are required for the Advancing Care Information Category. Please enter your corresponding Numerator and Denominator information or attest 'Yes' or 'No' for all measures."
  @@OPM_help = "These are additional Performance Measures that can be submitted to increase the Advancing Care Information Category Score. Please enter your corresponding Numerator and Denominator information or attest 'Yes' or 'No' for all measures."
  @@Bonus_measure_help = 'Attesting to one of these measures will increase the Advancing Care Information Category Score by 5 points. Submissions of more than 1 will not give additional bonus points. A 5 point maximum can be obtained.'
  @@CEHRT_measures_help = 'Attesting to one of these measures will increase the Advancing Care Information Category Score by 10 points. This requires an attestation of an Improvement Activity that is CEHRT eligible.'
  @@CEHRT_ELIGIBLE_HELP = 'This activity is Eligible for Certified Electronic Health Record Technology (CEHRT) bonus in the Advancing Care Information category. For more Information regarding CEHRT, please visit the CMS CEHRT Guideline Page'
  @@PCMH_measure_help = 'By attesting to this activity, you will receive 100% (40 points) for the Improvement Activities category. You cannot obtain above 40 points for the Improvement Activities category but you can submit additional activities.'

  @@Less_than_90_days = 'You are entering a date range less than 90 days. By proceeding, you will remove any Optional Performance Measures and Bonus Measures selected, and will cap the Category Score for Advancing Care Information to 50 possible points. Are you sure you wish to continue?'
  @@REGISTRY_READ_FULL_INSTRUCTIONS = '''Registry and QCDR Submissions
As a Registry or Qualified Clinical Data Registry (QCDR) user, you have the ability to submit data on behalf of group and individual practitioners through the file upload process. Each submission will be immediately recorded in the database. Scoring for each category is described below:
Improvement Activities
Standard Category Scoring
A performance period of greater than 1 day must be submitted to achieve a score. These fields must be a part of the submission for the file to be accepted.
The Improvement Activities category is based on a 40 point scale. The activities are broken down into two scoring categories:
High - giving the user 20 points for standard scoring
Medium - giving the user 10 points for standard scoring
The user can submit any assortment of activities to get to 40 points to fulfill the requirements for the Improvement Activity category. For example, 1 high weighted activity and 2 medium activities, will give the user 40 points. If the user obtains 40 points, they will get 15 points for the Improvement Activities category. Submitting additional activities will be logged but the user cannot obtain more than 40 points in the category or 15 points toward Final Score. If a user obtains less than 40 points, it will be based around on the scores below:
30 points equals 11.25 points towards Final Score
20 points equals 7.5 points towards Final Score
10 points equals 3.75 points towards Final Score
0 points equals 0 points towards Final Score
Special Scoring Categories

Non-Patient Facing Practice, Small Practice, Rural Area Practice and Health Professional Shortage Area
Automatically receive half credit for 1 medium-weight activity (20 points year 1) or full credit for two medium weight activities / one high weighted weight activities (40 points)
Accredited Professional Centered Medical Home (Attestation on Page)
Receive 100% success for category automatically upon attestation of "Yes" (year 1 & year 2)
MIPS Alternative Payment Models
Receive 100% success for category automatically
Non MIPS APMs
Receive 50% success for category automatically
Improvement Activity Study Participation
Receive 100% success for category automatically
Bonus Criteria
With the Improvement Activities category, there are measures that are eligible for Advancing Care Information Bonus CEHRT. By submitting one of these activities, you will be eligible to submit ACI_IACEHRT_1, which attests that they have performed the activity using CEHRT in the Advancing Care Information category. This bonus will give 10 points towards the Advancing Care Information category score, regardless of how many CEHRT eligible activities have been submitted.
Advancing Care Information
Information must be submitted in a specific stepwise manner in order to earn a Category Score for Advancing Care Information. First, a performance period of at least 1 day must be provided for partial scoring potential, or at least 90 days for full scoring potential. Second, you must submit Yes for the first two required Attestation Statements. Third, you must successfully attest valid values for the available Base Measures in order to begin earning a Category Score. This includes 5 measures for the Advancing Care Information Measures set, or 4 available measures for the 2017 Advancing Care Information Transition Measures set. Finally, you will have the option of attesting to Optional Performance Measures and / or available Bonus Measures. These measure categories are explained in further detail below.
Category Scoring
The Advancing Care Information Category is based on 5 available sections. The scoring for each section is as follows:
Advancing Care Information Measures
Attestation Statements - Required but no points are provided.
Base Score Measures - Required and will provide 50 points and performance points for corresponding measures and values (up to 30 additional points) to the Category Score upon successful completion.
Optional Performance Scores - Not required but can provide up to 60 points to the Category Score depending on Numerator and Denominator values.
Additional Registry Bonus - Not Required but can provide 5 points for answering Yes for any of these available measures. A maximum of 5 points to the Category Score can be obtained by attesting to the measures in this section.
Advancing Care Information Improvement Activities Bonus - Not Required but can provide 10 points to the Category Score by attesting Yes to this measure in conjunction with an associated CEHRT Eligible Activity in the Improvement Activities category.
2017 Advancing Care Information Transition Measures
Attestation Statements - Required but no points are provided.
Base Score Measures - Required and will provide 50 points and performance points for corresponding measures and values (up to 40 additional points) to the Category Score upon successful completion.
Optional Performance Scores - Not required but can provide up to 50 points to the Category Score depending on Numerator and Denominator values.
Additional Registry Bonus - Not Required but can provide 5 points for answering Yes for any of these available measures. A maximum of 5 points to the Category Score can be obtained by attesting to the measures in this section.
Advancing Care Information Improvement Activities Bonus - Not Required but can provide 10 points to the Category Score by attesting Yes to this measure in conjunction with an associated CEHRT Eligible Activity in the Improvement Activities category.
A Combination of Both Tracks
A user can select measures based on the year of their EHR technology
User is still required to answer Attestation Statements and Base Score in order to obtain any credit in the Advancing Care Information category. If the user does not submit the necessary measures, they will receive a 0 score.
Corresponding measures based on the year of EHR will be unselectable upon its counter being selected.
Special Scoring Categories

Advancing Care Information Reweighting Application, Certified Registered Nurse Practitioner (CRNP), Physician Assistant (PA), Certified Registered Nurse Anesthetists (CRNA), Clinical Nurse Specialist (CNS), and Non-patient-facing Practice (CNS)
Advancing Care Information effect on Final Score is reweighted to 0% which causes Quality to be reweighted to 85%*
User selects a performance window of less than 90 days (CNS)
The highest Category Score attainable is 50 points for the Base Measures. No additional measures or performance scores are applicable.
*If data is submitted for the Advancing Care Information category while a 0% reweight is in place, then the reweight may potentially be discarded and a score provided for this category.
Bonus Criteria
By Submitting ACI_IACEHRT_1, which attests that they have performed the activity using CEHRT in the Advancing Care Information category, this will award 10 points. This bonus is only applicable if you have submitted CEHRT eligible Improvement Activities.
Quality
Individual Clinicians or Groups of 15 or less
Groups of 15 or less are required to submit 6 measures, one of which must be:
A high priority outcome measure
A high priority patient experience measure
A high priority measure that falls into the remaining measure types
Failure to submit one of these measures will result in the lowest scored measure receiving 0 points. The denominator for these groups will be out of 60 points.
Groups of 16 or more
Groups of 16 are required to submit 6 measures, one of which must be:
A high priority outcome measure
A high priority patient experience measure
A high priority measure that falls into the remaining measure types
And if applicable, the Hospital Readmission Administrative Claims measure will be applied:
A hospital readmission measure
This is only applicable for 200 or more cases
Failure to submit one of these measures will result in the lowest scored measure receiving 0 points. The denominator for these groups will be out of 70 points unless you do not apply for Hospital Readmission measure then your denominator will be reduced to 60.
Standard Category Scoring
Quality measures scoring is based around the performance score and analyzed against the benchmarks to create a score between 3-10 points. For measures that do not have benchmarks, CMS will attempt to create a benchmark from the data submitted. If sufficient data is submitted during the submission period, the performance score will be compared to the benchmark and will be eligible to receive a score higher than 3.
Measures that have a benchmark must meet the reporting requirements to be analyzed to achieve a score greater than 3 per measure. The requirements are as follows
Data completeness must be greater than 50%
Measure case volume must be greater than or equal to 20
Measures that do not have an established benchmark will fall into one of two categories:
Benchmark will be established and score will be changed after the close of the submission period
A user will never score less than 3 points per measure. If a benchmark is established for measure after the submission period the score will either increase or remain at 3 point
Benchmark not established from submission data
Measures will receive 3 points
Benchmarked measure scoring explanation:
Calculate Performance Rate based on submitted data, Performance Met / Performance Met + Performance Not Met
Compare Performance Rate to benchmark data to find which decile the performance rate falls into
The decile will be the starting score, for example if a measure falls into decile 4, the point contribution will begin with 4 points
Calculate partial points
The QPP Quality algorithm is based on the formula used for linear interpolation, where a partial point will be awarded depending on where the performance rate falls between decile 4 lower bound and decile 5 lower bound
The decile points will be added to the partial points to establish the base quality score for the measure
Bonus Criteria
Bonus points can be obtained on submitted measures through the following categories:
Submission of 2 or more High quality measures (bonus will not be awarded for the first High quality measure). Bonus points for this category cannot exceed 10% of the denominator.
Outcome or Patient Experience measures will be awarded 2 points after the initial criteria is met for the first High Priority
Remaining category High Priority measures will be awarded 1
If no Outcome or Patient Experience measure is submitted, 1 point per measure will be awarded after first High Priority
If Outcome or Patient Experience measure is submitted, 1 point per measure will be awarded for all measures
Submission using End to End Reporting. Bonus points for this category cannot exceed 10% of the denominator. End to End Reporting will be awarded 1 point for each associated measure.
Bonus points can be applied to the Quality score, even if the measure is not being utilized as one of the 6 measures being used to create the score. All bonus points will be applied; however, not to exceed 10% of denominator
Creating Category Score

Measure Count greater than 6 measures (if no, see EMA process)
Calculate Performance Measurement Information
For measures that meet the reporting requirements and have a benchmark will be calculated using the information above
Remaining measures will be awarded 3 points
High Priority Requirement
If High Priority requirement met, there will be no implication on scoring
If High Priority requirement not met, lowest scoring measure out of the 6 measure being utilized to create score will be awarded 0 points for failure to comply
Calculating High Priority Bonus Points
2 points for every High Priority Outcome or Patient Experience measure submitted after first successful submission
1 point for every remaining High Priority measure category, if Outcome or Patient Experience submitted. If no Outcome or Patient Experience High Priority submitted, bonus points will be awarded for every high priority after the first submitted measure.
Calculating End to End Reporting Bonus
1 point for every measure submitted that is submitted using End to End electronic reporting
Creating Category Score
If Step 3 met, Measure score for 6 highest measure + Bonus in Step 4 + Bonus in Step 5 / 60 (for Individual Clinicians or Groups of 15 or less)
If Step 3 met, Measure score for 6 highest measure + Hospital Readmission Measure (if applicable) + Bonus in Step 4 + Bonus in Step 5 / 70 (Groups of 16 or more that apply for hospital readmission measure)
If Step 3 not met, Measure score for 5 highest measure + Bonus in Step 4 + Bonus in Step 5 / 60 (for Individual Clinicians or Groups of 15 or less)
If Step 3 not met, Measure score for 5 highest measure + Hospital Readmission Measure (if applicable) + Bonus in Step 4 + Bonus in Step 5 / 70 (Groups of 16 or more that apply for hospital readmission measure)
Special Scoring Categories

For clinicians and groups that apply for Advancing Care Information reweighting, Quality will be weighted at 85% for your final score, as opposed to the standard 60%.
Certain APMs are not required to submit Quality Information. The quality page will notify you if you are not required to report quality data for QPP.
For APMs submitting Quality to the Beneficiary Sampling User Interface, your score will be displayed on this page. Quality will be weighted at 50% of your total final score
Specialty Set and EMA Submissions
For submissions that contain less than 6 measures, a denominator reduction may occur if you are submitting either a Registry Specialty Set or Registry Clinical Cluster that contains less than 6 measures.' ''

  @@aci_read_full_instructions = "Basic Navigation
On this page, the user can attest to Advancing Care Information Measures. Depending on the measure, these can be attested either using Yes and No selections, or specific Numerator and Denominator values. The Advancing Care Information category contains 3 available Measure Set choices:
Advancing Care Information Measures
2017 Advancing Care Information Transition Measures
A combination of Advancing Care Information Measures and 2017 Advancing Care Information Transition Measures
You must attest to information in a specific stepwise manner in order to earn a Category Score for Advancing Care Information. First, you must select a performance period of at least 1 day for partial scoring potential, or at least 90 days for full scoring potential. Second, you must select the Advancing Care Information Measurement Set that applies your Electronic Health Record Technology edition. Third, you must select Yes for the first two required Attestation Statements. Fourth, you must successfully attest valid values for the available Base Measures in order to begin earning a Category Score. This includes 5 measures for the Advancing Care Information Measures set, or 4 available measures for the 2017 Advancing Care Information Transition Measures set. Finally, you will have the option of attesting to Optional Performance Measures and / or available Bonus Measures. These measure categories are explained in further detail below.
Category Scoring
The Advancing Care Information Category is based on 5 available sections. The scoring for each section is as follows:
Advancing Care Information Measures
Attestation Statements - Required but no points are provided.
Base Score Measures - Required and will provide 50 points and performance points for corresponding measures and values (up to 30 additional points) to the Category Score upon successful completion.
Optional Performance Scores - Not required but can provide up to 60 points to the Category Score depending on Numerator and Denominator values.
Additional Registry Bonus - Not Required but can provide 5 points for answering Yes for any of these available measures. A maximum of 5 points to the Category Score can be obtained by attesting to the measures in this section.
Advancing Care Information Improvement Activities Bonus - Not Required but can provide 10 points to the Category Score by attesting Yes to this measure in conjunction with an associated CEHRT Eligible Activity in the Improvement Activities category.
2017 Advancing Care Information Transition Measures
Attestation Statements - Required but no points are provided.
Base Score Measures - Required and will provide 50 points and performance points for corresponding measures and values (up to 40 additional points) to the Category Score upon successful completion.
Optional Performance Scores - Not required but can provide up to 50 points to the Category Score depending on Numerator and Denominator values.
Additional Registry Bonus - Not Required but can provide 5 points for answering Yes for any of these available measures. A maximum of 5 points to the Category Score can be obtained by attesting to the measures in this section.
Advancing Care Information Improvement Activities Bonus - Not Required but can provide 10 points to the Category Score by attesting Yes to this measure in conjunction with an associated CEHRT Eligible Activity in the Improvement Activities category.
A Combination of Both Tracks
A user can select measures based on the year of their EHR technology
User is still required to answer Attestation Statements and Base Score in order to obtain any credit in the Advancing Care Information category. If the user does not submit the necessary measures, they will receive a 0 score.
Corresponding measures based on the year of EHR will be unselectable upon its counter being selected.
Special Scoring Categories

Advancing Care Information Reweighting Application, Certified Registered Nurse Practitioner (CRNP), Physician Assistant (PA), Certified Registered Nurse Anesthetists (CRNA), Clinical Nurse Specialist (CNS), and Non-patient-facing Practice (CNS)
Advancing Care Information effect on Final Score is reweighted to 0% which causes Quality to be reweighted to 85%*
User selects a performance window of less than 90 days (CNS)
The highest Category Score attainable is 50 points for the Base Measures. No additional measures or performance scores are applicable.
*If data is submitted for the Advancing Care Information category while a 0% reweight is in place, then the reweight may potentially be discarded and a score provided for this category.
Bonus Criteria
On the Improvement Activities page there are specific activities that are labeled with a CEHRT Eligible symbol. This notates that the activity is eligible for bonus points in the Advancing Care Information category. Selecting one of these activities can provide 10 points to the Category Score by attesting Yes to this activity in conjunction with an associated CEHRT Bonus Measure in the Advancing Care Information category."

  @@qm_read_full_instructions = '''Basic Navigation
On this page, the user will see their measures they have submitted. The user is able to see their submitted measures; however these measures will not be modifiable. These measures are displayed with the following categories. Performance benchmarks are based around 2015 submission data for each submission method. The QPP UI will accept the following data submissions:
Registry / QCDR
eHR
Individual Clinicians or Groups of 15 or less
Groups of 15 or less are required to submit 6 measures, one of which must be:
A high priority outcome measure
A high priority patient experience measure
A high priority measure that falls into the remaining measure types
Failure to submit one of these measures will result in the lowest scored measure receiving 0 points. The denominator for these groups will be out of 60 points.
Groups of 16 or more
Groups of 16 are required to submit 6 measures, one of which must be:
A high priority outcome measure
A high priority patient experience measure
A high priority measure that falls into the remaining measure types
And if applicable, the Hospital Readmission Administrative Claims measure will be applied:
A hospital readmission measure
This is only applicable for 200 or more cases
Failure to submit one of these measures will result in the lowest scored measure receiving 0 points. The denominator for these groups will be out of 70 points unless you do not apply for Hospital Readmission measure then your denominator will be reduced to 60.
Standard Category Scoring
Quality measures scoring is based around the performance score and analyzed against the benchmarks to create a score between 3-10 points. For measures that do not have benchmarks, CMS will attempt to create a benchmark from the data submitted. If sufficient data is submitted during the submission period, the performance score will be compared to the benchmark and will be eligible to receive a score higher than 3.
Measures that have a benchmark must meet the reporting requirements to be analyzed to achieve a score greater than 3 per measure. The requirements are as follows
Data completeness must be greater than 50%
Measure case volume must be greater than or equal to 20
Measures that do not have an established benchmark will fall into one of two categories:
Benchmark will be established and score will be changed after the close of the submission period
A user will never score less than 3 points per measure. If a benchmark is established for measure after the submission period the score will either increase or remain at 3 point
Benchmark not established from submission data
Measures will receive 3 points
Benchmarked measure scoring explanation:
Calculate Performance Rate based on submitted data, Performance Met / Performance Met + Performance Not Met
Compare Performance Rate to benchmark data to find which decile the performance rate falls into
The decile will be the starting score, for example if a measure falls into decile 4, the point contribution will begin with 4 points
Calculate partial points
The QPP Quality algorithm is based on the formula used for linear interpolation, where a partial point will be awarded depending on where the performance rate falls between decile 4 lower bound and decile 5 lower bound
The decile points will be added to the partial points to establish the base quality score for the measure
Bonus Criteria
Bonus points can be obtained on submitted measures through the following categories:
Submission of 2 or more High quality measures (bonus will not be awarded for the first High quality measure). Bonus points for this category cannot exceed 10% of the denominator.
Outcome or Patient Experience measures will be awarded 2 points after the initial criteria is met for the first High Priority
Remaining category High Priority measures will be awarded 1
If no Outcome or Patient Experience measure is submitted, 1 point per measure will be awarded after first High Priority
If Outcome or Patient Experience measure is submitted, 1 point per measure will be awarded for all measures
Submission using End to End Reporting. Bonus points for this category cannot exceed 10% of the denominator. End to End Reporting will be awarded 1 point for each associated measure.
Bonus points can be applied to the Quality score, even if the measure is not being utilized as one of the 6 measures being used to create the score. All bonus points will be applied; however, not to exceed 10% of denominator
Creating Category Score

Measure Count greater than 6 measures (if no, see EMA process)
Calculate Performance Measurement Information
For measures that meet the reporting requirements and have a benchmark will be calculated using the information above
Remaining measures will be awarded 3 points
High Priority Requirement
If High Priority requirement met, there will be no implication on scoring
If High Priority requirement not met, lowest scoring measure out of the 6 measure being utilized to create score will be awarded 0 points for failure to comply
Calculating High Priority Bonus Points
2 points for every High Priority Outcome or Patient Experience measure submitted after first successful submission
1 point for every remaining High Priority measure category, if Outcome or Patient Experience submitted. If no Outcome or Patient Experience High Priority submitted, bonus points will be awarded for every high priority after the first submitted measure.
Calculating End to End Reporting Bonus
1 point for every measure submitted that is submitted using End to End electronic reporting
Creating Category Score
If Step 3 met, Measure score for 6 highest measure + Bonus in Step 4 + Bonus in Step 5 / 60 (for Individual Clinicians or Groups of 15 or less)
If Step 3 met, Measure score for 6 highest measure + Hospital Readmission Measure (if applicable) + Bonus in Step 4 + Bonus in Step 5 / 70 (Groups of 16 or more that apply for hospital readmission measure)
If Step 3 not met, Measure score for 5 highest measure + Bonus in Step 4 + Bonus in Step 5 / 60 (for Individual Clinicians or Groups of 15 or less)
If Step 3 not met, Measure score for 5 highest measure + Hospital Readmission Measure (if applicable) + Bonus in Step 4 + Bonus in Step 5 / 70 (Groups of 16 or more that apply for hospital readmission measure)'''
  @@Required_For_Base_Score_Message = 'You will be unable to attest to these measures until you have completed the Attestation Statements above.'
  @@Attestation_RequiredBaseScore_Required_message = 'You will be unable to attest to these measures until you have completed both the Attestation Statements and Required for Base Score measures above.'
  @@Max_score_reached = 'Max score for this category has been achieved!'
  @@Confirm_clear_Pre_attestation_Measures = 'This action will clear or invalidate the selected Pre-Attestation measure. This will return the Category Score for Advancing Care Information to zero, and will remove all base measures and any additional non-base measures found below. This action cannot be undone. Do you wish to continue?'
  @@Pre_Attestation_deselect_confirmation_Msg = "By selecting a different track, this will remove all progress and delete submitted measures.
Do you wish to continue?"
  @@Cehrt_warning = 'You have not yet attested to any CEHRT eligible activities on the Improvement Activities page. You must attest to a CEHRT eligible activity if you wish to achieve 10 bonus points for the Advancing Care Information CEHRT Used bonus measure.'
  @@ACI_INFBLO_1_def = "I have not knowingly and willfully take action to limit or restrict the interoperability of certified EHR technology. I have responded to requests to retrieve or exchange information—including requests from patients and other health care providers regardless of the requestor's affiliation or technology. I have implemented appropriate standards and processes to ensure that its certified EHR technology was connected in accordance with applicable law and standards, allowed patients timely access to their electronic health information; and supported exchange of electronic health information with other health care providers."
  @@ACI_ONCDIR_1_def = 'I have (1) acknowledged the requirement to cooperate in good faith with ONC direct review health information technology certified under the ONC Health IT Certification Program if a request to assist in ONC direct review is received; AND (2) If requested, cooperated in good faith with ONC direct review of his or her health information technology certified under the ONC Health IT Certification Program as authorized by 45 CFR part 170, subpart E, to the extent that such technology meets (or can be used to meet) the definition of CEHRT, including by permitting timely access to such technology and demonstrating its capabilities as implemented and used by the MIPS eligible clinician in the field.'
  @@ACI_ONCACB_1_def = 'I have (1) Acknowledged the option to cooperate in good faith with ONC–ACB surveillance of his or her health information technology certified under the ONC Health IT Certification Program if a request to assist in ONC–ACB surveillance is received; and (2) If requested, cooperated in good faith with ONC–ACB surveillance of his or her health information technology certified under the ONC Health IT Certification Program as authorized by 45 CFR part 170, subpart E, to the extent that such technology meets (or can be used to meet) the definition of CEHRT, including by permitting timely access to such technology and demonstrating its capabilities as implemented and used by the MIPS eligible clinician in the field.'
  @@CREDIT_SCORE_50_MESSAGE = "You will receive 50% for this category. To receive maximum credit for this category attest to additional activities."
  @@CREDIT_SCORE_100_MESSAGE = "You will receive 100% for this category but can report additional activities to be eligible to receive the CEHRT bonus in Advancing Care Information. Please note that your QP status is not taken into consideration."
  @@EXTREME_HARDSHIP_MESSAGE = "You have been identified as being located in a Federal Emergency Management Agency designated area affected by natural disasters. You will automatically receive a final score of 3 points, resulting in a neutral payment adjustment, without having to submit any data. No action is required. If you choose to report 2 or more performance categories, you will be scored on each performance category according to existing MIPS scoring policies."
  @@CHP_PRODUCT_MESSAGE = "100-504  unable to contact Certified Health IT Product"
  @@ACI_REWIGHTING_WARNING_MESSAGE ="You are currently identified as a Provider that has an approved reweighting application for the Promoting Interoperability category. By entering information, you will no longer be subject to score reweighting, and the Promoting Interoperability Category will return to being scored. This action cannot be undone, are you sure you wish to continue?
BY PROCEEDING, I ACKNOWLEDGE THAT I WISH TO FORFEIT MY APPROVED REWEIGHTING APPLICATION.
PROCEED"
  @@Max_40_IA_POINTS_REACHED = "MAXIMUM 40 IMPROVEMENT ACTIVITIES POINTS ACHIEVED!
This submission achieved 40 Improvement Activities points for participating in the Improvement Activities study. The maximum improvement Activities performance score is 40 points."

  element(:max_40_ia_msg, css: ".message-content")
  p(:aci_reweight_warning_message, css: "div[aria-labelledby='reWeightedModal'] div.modal-body p")

  span(:toast_msg, xpath: "//span[@class='toast-title']//span[@role='status']")
  span(:popup_message, xpath: "//div[contains(@style,'display: block;')]//span")
  div(:required_for_base_score_msg, xpath: "//div[@class='row subheading' and normalize-space(.)='REQUIRED FOR BASE SCORE']/following-sibling::div//div[@class='notification warning']")
  #---------------------help related page objects----------------------------------
  button(:perf_period_help, xpath: "//button[@aria-label='Performance date range help']")
  button(:pre_attestation_help, xpath: "//button[@aria-label='Pre attestation help']")
  button(:base_measure_help, xpath: "//button[@aria-label='Base measure help']")
  button(:opm_help, xpath: "//button[@aria-label='Non base measure help']")
  button(:bonus_measure_help, xpath: "//button[@aria-label='Bonus measure help']")
  button(:cehrt_measures_help, xpath: "//button[@aria-label='CEHRT measures help']")
  button(:cehrt_eligible_help, xpath: "(//button[@aria-label='CEHRT Eligible help'])[1]")
  button(:pcmh_help, xpath: "//button[@aria-label='Patient Centered Medical Home Attestation help']")
  button(:epe_help, xpath: "//button[@area-label='E-Prescribing Exclusion help']")
  button(:hiee_help, xpath: "//button[@area-label='Health Information Exchange Exclusion help']")
  span(:help_text, xpath: "//div[@class='popover-content popover-body']/span")
  span(:current_group_name, xpath: "//h1[@class='page-title']/span")
  div(:qm_page_desc, css: 'div.page-description')

  @@E_PRESCRIBING_EXCLUSION_help = 'A MIPS eligible clinician (EC) who writes fewer than 100 permissible prescriptions during the performance period is eligible for exclusion from the required e-prescribing measure.'
  @@HEALTH_INFORMATION_EXCHANGE_EXCLUSION_help = 'Any MIPS eligible clinician who transfers a patient to another setting or refers a patient less than 100 times during the performance period.'
  @@QM_PAGE_DESCRIPTION = 'The Quality score is based on the highest score among all submission method scores. Read full instructions'

  # FeebBack
  @@FEEDBACK_AVAILABILITY_MESSAGE = 'The information being displayed below is NOT your Final Score. Your Final Feedback will be available no later than July 1, 2018. View Pending Data'
  p(:processing_submission_data_msg, css: '.message p')

  def verify_EXTREME_HARDSHIP_MESSAGE(expected_status = true)
    #@@EXTREME_HARDSHIP_MESSAGE
  end

  def verify_reweight_warning_message
    actual = aci_reweight_warning_message
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(@@ACI_REWIGHTING_WARNING_MESSAGE,actual, "ACI Reweight Message")

  end

  def verify_feeb_back_available_date_message
    acutal = processing_submission_data_msg_element.text
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(@@FEEDBACK_AVAILABILITY_MESSAGE, actual.to_s, 'FeedBack Availability Date Message')
  end

  def verify_credit_score_message(percentage)
    @browser.div(css: "div.message  div").scroll_into_view
    actual = @browser.div(css: "div.message  div").text
    if percentage == '50%'
      expected = @@CREDIT_SCORE_50_MESSAGE
    elsif percentage == '100%'
      expected = @@CREDIT_SCORE_100_MESSAGE
    end
    Verifications.new(@browser).verify_text(expected,actual, "Credit Score message" + expected)
  end

  def verify_qm_option_details(opt_num, opt_title, _opt_info, btn_name)
    opt_num_ele = @browser.div(xpath: "//div[@class='option-title' and .='#{opt_num}']")
    @verifi = Verifications.new(@browser)
    if opt_num_ele.exists?
      actual_title = @browser.element(xpath: "//div[@class='option-title' and .='#{opt_num}']/ancestor::div[@class='option-box-body']//h5").text
      actual_info = @browser.p(xpath: "//div[@class='option-title' and .='#{opt_num}']/ancestor::div[@class='option-box-body']//p").text
      case opt_num.downcase
      when 'option 1'
        expected_info = 'This method allows the upload of EHR export data in either QPP (JSON) format and QRDA-3 files. There are six required measures, including one High priority measure.'
      when 'option 2'
        expected_info = 'This method is only available if you have registered. There are fourteen required measures.'
      when 'option 3'
        expected_info = 'If using a Registry or EHR to submit data, please contact them for support.'
      end
      @verifi.verify_text(opt_title, actual_title, opt_num + ' title')
      @verifi.verify_text(expected_info, actual_info, opt_num + ' information')
      if btn_name != ''
        actual_btn_name = @browser.div(xpath: "//div[@class='option-title' and .='#{opt_num}']/ancestor::div[@class='option']//div[@class='option-box-footer']").text
        @verifi.verify_text(btn_name, actual_btn_name, btn_name)
      end
    end
  end

  def verify_QM_Page_description
    @verifi = Verifications.new(@browser)
    actual = qm_page_desc_element.text
    @verifi.verify_text(@@QM_PAGE_DESCRIPTION, actual, 'QM Page Description')
  end

  def click_help_button(help_area)
    case help_area.downcase
    when 'perfomance period'
     WebHelper.click(perf_period_help_element)
      # TBD: check if start date is empty (if not get the date diff and decide the message to be selected)
      case current_group_name
      when 'Improvement Activities'
        @@Perf_period_ia_help
      when 'Advancing Care Information'
        @@Perf_period_aci_help
      end

    when 'pre attestation'
      WebHelper.click(pre_attestation_help_element)
      @@Pre_Attestation_ACI_help
    when 'base measures'
      WebHelper.click(base_measure_help_element)
      @@Base_measure_help
    when 'opm'
      WebHelper.click(opm_help_element)
      @@OPM_help
    when 'bonus measures'
      WebHelper.click(bonus_measure_help_element)
      @@Bonus_measure_help
    when 'cehrt measures'
      WebHelper.click(cehrt_measures_help_element)
      @@CEHRT_measures_help
    when 'cehrt eligible'
      WebHelper.click(cehrt_eligible_help_element)
      @@CEHRT_ELIGIBLE_HELP
    when 'pcmh', 'ia_pcmh'
      WebHelper.click(pcmh_help_element)
      @@PCMH_measure_help
    when 'e-prescribing exclusion'
      WebHelper.click(epe_help_element)
      @@E_PRESCRIBING_EXCLUSION_help
    when 'health information exchange exclusion'
      WebHelper.click(hiee_help_element)
      @@HEALTH_INFORMATION_EXCHANGE_EXCLUSION_help
    end
  end

  def verify_help_text(help_area)
    expected = click_help_button(help_area)
    help_text_element.focus
    # help_text_element.flash
    actual = help_text_element.text
    assert_equal(expected, actual)
    click_help_button(help_area)
  end

  def get_popup_message
    popup_message_element.wait_until(&:present?)
    popup_message
  end

  def verify_less_than_90_days_message
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(@@Less_than_90_days, get_popup_message)
  end

  def verify_Req_For_Base_Score_Msg
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(@@Required_For_Base_Score_Message, required_for_base_score_msg_element.text)
  end

  def verify_AttestReqBaseScoreReq_Msg(element_to_check)
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(@@Attestation_RequiredBaseScore_Required_message, element_to_check.text)
  end

  def verify_cehrt_warning(element_to_check)
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(@@Cehrt_warning, element_to_check.text)
  end

  def verify_toast_message(message)
    actual = toast_msg_element.wait_until(&:present?).text
    case message
      when 'Max_score_reached'
        expected_message = @@Max_score_reached
      when 'Less_than_90_days'
        expected_message = @@Less_than_90_days
      when 'CHP_PRODUCT_MESSAGE', 'CHP PRODUCT MESSAGE'
        expected_message = @@CHP_PRODUCT_MESSAGE
    end
    assert_equal(expected_message, actual, "Verifying toast message. \\n Expected: #{expected_message}\\n Actual: #{actual}")
  end

  def verify_modal_dialog_body_content(modal_dialog_body_text)
    # actual = @browser.div(xpath: "//div[contains(@class,'modal fade') and @aria-hidden='false']//div[@class='modal-body']").text
    actual = @browser.div(xpath: "//div[(contains(@class,'modal-dialog modal-sm')) or (contains(@class,'modal fade') and @aria-hidden='false')]//div[starts-with(@class,'modal-body')]").wait_until(&:present?).text
    @verifi = Verifications.new(@browser)
    expected = case modal_dialog_body_text.downcase
               when 'aci_read_full_instructions'
                 @@aci_read_full_instructions
               when 'clear_pre_attestation_measures_confirmation'
                 @@Confirm_clear_Pre_attestation_Measures
               when 'pre_attestation_deselect_confirmation_msg'
                 @@Pre_Attestation_deselect_confirmation_Msg
               when 'quality_read_full_instructions'
                 @@qm_read_full_instructions
               when 'registry__read_full_instructions'
                 @@REGISTRY_READ_FULL_INSTRUCTIONS
               when 'delete submission data'
                 "Are you sure you want to delete all selected data associated with these submissions?
This action cannot be undone."
               else
                 modal_dialog_body_text
               end
    @verifi.verify_text(expected, actual)
  end

  def verify_measure_definition(measure_name, measure_definition)
    actual = @browser.div(xpath: "//button[contains(@class,'#{measure_name}')]//ancestor::div[@class='attestation-measure']//div[@class='measure-description col-xs-12']").text
    case measure_definition
    when 'ACI_INFBLO_1_def'
      expected = @@ACI_INFBLO_1_def
    when 'ACI_ONCDIR_1_def'
      expected = @@ACI_ONCDIR_1_def
    when 'ACI_ONCACB_1_def'
      expected = @@ACI_ONCACB_1_def

    end
    assert_equal(expected, actual, "Verifying #{measure_name} measure definition \n Expected: #{expected} \n Actual: #{actual}")
  end

  def verify_max_40_ia_achieved
    actual = max_40_ia_msg
    Verifications.new(@browser).verify_text(@@Max_40_IA_POINTS_REACHED,actual, "MAXIMUM 40 IMPROVEMENT ACTIVITIES POINTS ACHIEVED!")
  end
end
