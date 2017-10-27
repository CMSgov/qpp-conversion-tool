package gov.cms.qpp.conversion.model.error;

public enum ErrorCode {

	ENCODER_MISSING("Failed to find an encoder"),
	NOT_VALID_XML_DOCUMENT("The file is not a valid XML document"),
	UNEXPECTED_ERROR("Unexpected exception occurred during conversion"),
	UNEXPECTED_ENCODE_ERROR("Unexpected exception occured during encoding"),
	NOT_VALID_QRDA_DOCUMENT("The file is not a QRDA-III XML document"),
	MEASURE_GUID_MISSING("The measure reference results must have a measure GUID"),
	CHILD_MEASURE_MISSING("The measure reference results must have at least one measure"),
	AGGREGATE_COUNT_VALUE_NOT_SINGULAR("A single aggregate count value is required"),
	AGGREGATE_COUNT_VALUE_NOT_INTEGER("Aggregate count value must be an integer"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_MISSING("ACI Measure Performed RnR's Measure Performed is required"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_REPEATED("ACI Measure Performed RnR's Measure Performed can only be present once"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR("ACI Measure Performed RnR's requires a single Measure ID"),
	DENOMINATOR_COUNT_INVALID("Denominator count must be less than or equal to Initial Population count for an eCQM that is proportion measure"),
	POPULATION_CRITERIA_COUNT_INCORRECT("The eCQM (electronic measure id: %s) requires %d %s(s) but there are %d", true),
	ACI_NUMERATOR_DENOMINATOR_PARENT_NOT_ACI_SECTION("ACI Numerator Denominator Node should have an ACI Section Node as a parent"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID("ACI Numerator Denominator Node does not contain a measure name ID"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN("ACI Numerator Denominator Node does not have any child Nodes"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_DENOMINATOR_CHILD_NODE("This ACI Numerator Denominator Node does not contain a Denominator Node child"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_NUMERATOR_CHILD_NODE("This ACI Numerator Denominator Node does not contain a Numerator Node child"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_DENOMINATORS("This ACI Numerator Denominator Node contains too many Denominator Node children"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_NUMERATORS("This ACI Numerator Denominator Node contains too many Numerator Node children"),
	ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT("The ACI Section must have one Reporting Parameter ACT"),
	CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD("Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child"),
	CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME("Clinical Document must have one and only one program name"),
	CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME("Clinical Document program name is not recognized"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ACI_SECTIONS("Clinical Document contains duplicate ACI sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS("Clinical Document contains duplicate IA sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_eCQM_SECTIONS("Clinical Document contains duplicate eCQN sections");

	ErrorCode(String message) {
		this(message, false);
	}

	ErrorCode(String message, boolean hasFormat) {
		
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
