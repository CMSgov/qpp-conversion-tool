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
			.listValuesAreInts(ProblemCode.CPC_PCF_PLUS_INVALID_NPI, ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER);

		validateSingleTinNpiPerPerformer(node);
		validateApmEntityId(node, ClinicalDocumentDecoder.PCF_ENTITY_ID);
	}

	private Predicate<Element> filterTinNpi(String name) {
		 return (Element child) -> (child.getContent().stream()
				 .filter(c -> c.getClass().equals(LocatedElement.class) && ((LocatedElement) c).getName().equals(name))
				 .toArray().length >= 1);
	}

	private void validateSingleTinNpiPerPerformer(Node node) {
		String performersXpath = PathCorrelator.getXpath(TemplateId.CLINICAL_DOCUMENT.toString(), "performer", Namespace.NO_NAMESPACE.getURI());
		XPathExpression<Element> expression = XPathFactory.instance().compile(performersXpath, Filters.element(), null,  Namespace.NO_NAMESPACE);
		List<Element> performers = expression.evaluate(node.getElementForLocation());
		// Should be maximum of one TIN and one NPI per performer.
		for (Element performer : performers) {
			List<Element> tins = performer.getChildren().stream().filter(filterTinNpi("representedOrganization")).collect(Collectors.toList());
			List<Element> npis = performer.getChildren().stream().filter(filterTinNpi("id")).collect(Collectors.toList());
			forceCheckErrors(node).predicate(ProblemCode.PCF_MULTI_TIN_NPI_SINGLE_PERFORMER, tins.size() <= 1);
			forceCheckErrors(node).predicate(ProblemCode.PCF_MULTI_TIN_NPI_SINGLE_PERFORMER, npis.size() <= 1);
		}
	}
}
