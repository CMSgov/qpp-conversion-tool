package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Validates the Clinical Document level node for the given program: PCF
 * Using the same validation of CPC+
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.PCF)
public class PcfClinicalDocumentValidator extends CpcClinicalDocumentValidator {

	public PcfClinicalDocumentValidator(Context context) {
		super(context);
	}

	@Override
	protected void performValidation(final Node node) {
		super.performValidation(node);

		checkErrors(node)
			.singleValue(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED, ClinicalDocumentDecoder.PCF_ENTITY_ID)
			.valueIsNotEmpty(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_EMPTY_APM, ClinicalDocumentDecoder.PCF_ENTITY_ID)
			.childExact(ProblemCode.PCF_NO_PI, 0, TemplateId.PI_SECTION_V2)
			.listValuesAreInts(ProblemCode.CPC_PCF_PLUS_INVALID_NPI.format(node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME)),
				ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER);

		validateApmEntityId(node, ClinicalDocumentDecoder.PCF_ENTITY_ID);
	}
}
