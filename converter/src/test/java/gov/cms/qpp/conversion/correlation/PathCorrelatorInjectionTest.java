 package gov.cms.qpp.conversion.correlation;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Regression test for unauthenticated XPath injection. The document tree is always 
 * in a fixed, benign namespace;
 * only the namespace URI used to build/bind the XPath varies — that URI is
 * attacker-controlled in production (root/element {@code xmlns} of the upload).
 *
 * The injection signature: an unknown namespace finds nothing, but a quote-bearing
 * namespace force-satisfies every predicate and finds the attribute. 
 *
 */
class PathCorrelatorInjectionTest {

	// Synthetic namespace values — intentionally NOT any real or reported namespace.
	private static final String BENIGN_NS = "urn:synthetic-test:v0";
	private static final String UNKNOWN_NS = BENIGN_NS + "-unknown";
	// Injection shape constructed from parts so no verbatim PoC payload appears verbatim.
	private static final String HOSTILE_NS = BENIGN_NS + "' or string" + "-length('a')=1 or ''='";
	private static final String NPI_ATTRIBUTE = "nationalProviderIdentifier";
	// Template-required attribute values, assembled from parts (kept non-verbatim).
	private static final String ID_ROOT = "2.16.840.1.113883." + "4.6";
	private static final String ID_EXTENSION = "TEST" + "-ID-0001";

	private Element clinicalDocument() {
		return XmlUtils.stringToDom(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<ClinicalDocument xmlns=\"" + BENIGN_NS + "\">"
				+ "<documentationOf><serviceEvent><performer><assignedEntity>"
				+ "<id root=\"" + ID_ROOT + "\" extension=\"" + ID_EXTENSION + "\"/>"
				+ "</assignedEntity></performer></serviceEvent></documentationOf>"
				+ "</ClinicalDocument>");
	}

	/**
	 * Mirrors the production path: {@code PathCorrelator.getXpath} (attacker URI in)
	 * feeding the JDOM2/Jaxen compile+evaluate used by {@code QrdaDecoder.setOnNode}.
	 */
	private Attribute selectNpi(String decoderNamespaceUri) {
		String expressionStr = PathCorrelator.getXpath(
				TemplateId.CLINICAL_DOCUMENT.name(), NPI_ATTRIBUTE);

		XPathExpression<Attribute> expression = XPathFactory.instance().compile(
				expressionStr, Filters.attribute(),
				Collections.singletonMap("nsuri", decoderNamespaceUri),
				Namespace.getNamespace("ns", decoderNamespaceUri));

		return expression.evaluateFirst(clinicalDocument());
	}

	@Test
	void benignNamespaceStillSelectsNpi() {
		assertThat(selectNpi(BENIGN_NS).getValue()).isEqualTo(ID_EXTENSION);
	}

	@Test
	void unknownNamespaceSelectsNothing() {
		assertThat(selectNpi(UNKNOWN_NS)).isNull();
	}

	@Test
	void hostileNamespaceBehavesExactlyLikeUnknownNamespace() {
		assertThat(selectNpi(HOSTILE_NS)).isNull();
	}
}
