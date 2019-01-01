package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.util.EnvironmentHelper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

/**
 * Validates the Clinical Document level node for the given program: CPC+
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.CPC)
public class CpcClinicalDocumentValidator extends NodeValidator {

	static final String END_DATE_VARIABLE = "CPC_END_DATE";
	static final DateTimeFormatter END_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	static final String DEFAULT_CPC_PLUS_CONTACT_EMAIL = "cpcplus@telligen.com";
	static final String CPC_PLUS_CONTACT_EMAIL = "CPC_PLUS_CONTACT_EMAIL";
	// LocalDate.now() creates extra unneeded clock objects before Java 9.
	// It also uses the system clock, rather than Eastern Time.
	private static final Clock CLOCK = Clock.system(ZoneId.of("US/Eastern"));

	/**
	 * Validates a single clinical document node
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
			validateSubmissionDate(node);

			LocalizedError addressError = ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
				.format(Context.REPORTING_YEAR);

			checkErrors(node)
					.valueIsNotEmpty(addressError, ClinicalDocumentDecoder.PRACTICE_SITE_ADDR)
					.singleValue(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED,
							ClinicalDocumentDecoder.PRACTICE_ID)
					.valueIsNotEmpty(ErrorCode.CPC_CLINICAL_DOCUMENT_EMPTY_APM, ClinicalDocumentDecoder.PRACTICE_ID)
					.childMinimum(ErrorCode.CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED,
							1, TemplateId.MEASURE_SECTION_V2);

			validateApmEntityId(node);
	}

	/**
	 * Validates the APM Entity ID in the given node is valid.
	 *
	 * A validation error is created if the APM Entity ID is invalid.
	 *
	 * @param node The node to validate
	 */
	private void validateApmEntityId(Node node) {
		String apmEntityId = node.getValue(ClinicalDocumentDecoder.PRACTICE_ID);

		if (StringUtils.isEmpty(apmEntityId)) {
			return;
		}

		if (!ApmEntityIds.idExists(apmEntityId)) {
			addError(Detail.forErrorAndNode(ErrorCode.CPC_CLINICAL_DOCUMENT_INVALID_APM, node));
		}
	}

	/**
	 * Validates the submission is not after the set end date
	 *
	 * @param node The node to give in the error if the submission is after the set end date
	 */
	private void validateSubmissionDate(Node node) {
		LocalDate endDate = endDate();
		if (now().isAfter(endDate)) {
			String formatted = endDate.format(END_DATE_FORMAT);
			addError(Detail.forErrorAndNode(
				ErrorCode.CPC_PLUS_SUBMISSION_ENDED.format(formatted,
					EnvironmentHelper.getOrDefault(CPC_PLUS_CONTACT_EMAIL, DEFAULT_CPC_PLUS_CONTACT_EMAIL)),
				node));
		}
	}

	/**
	 * @return the current local date, in Eastern Time
	 */
	private LocalDate now() {
		return LocalDate.now(CLOCK);
	}

	/**
	 * @return the configured cpc+ end date, or {@link LocalDate#MAX} if none is set
	 */
	private LocalDate endDate() {
		String endDate = EnvironmentHelper.get(END_DATE_VARIABLE);
		if (endDate == null) {
			return LocalDate.MAX;
		}
		return LocalDate.parse(endDate);
	}
}
