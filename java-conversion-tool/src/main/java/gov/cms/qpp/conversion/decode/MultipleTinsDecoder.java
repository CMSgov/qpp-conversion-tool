package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */
@Decoder(TemplateId.QRDA_CATEGORY_III_REPORT_V3)
public class MultipleTinsDecoder extends QppXmlDecoder {

	public static final String NPI_TIN_ID = "NPITIN";
	public static final String NATIONAL_PROVIDER_IDENTIFIER = "nationalProviderIdentifier";
	public static final String TAX_PAYER_IDENTIFICATION_NUMBER = "taxpayerIdentificationNumber";
	private static final String PERFORMED_ASSIGNED_ENTITY_PATH =
			"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity";
	private static final String ID = "id";
	private static final String EXTENSION = "extension";
	private static final String REPRESENTED_ORGANIZATION = "representedOrganization";

	/**
	 * internalDecode parses the xml fragment into thisNode
	 *
	 * @param element Element represents the XML input
	 * @param thisNode Node internal representation of input
	 * @return DecodeResult.TreeFinished thisNode gets the newly parsed xml fragment
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setNationalProviderIdOnNode(element, thisNode);

		Node child = new Node(TemplateId.CLINICAL_DOCUMENT);
		ClinicalDocumentDecoder clinicalDocument = new ClinicalDocumentDecoder();
		clinicalDocument.setNamespace(element, clinicalDocument);
		clinicalDocument.internalDecode(element, child);
		thisNode.addChildNode(child);

		return DecodeResult.TREE_FINISHED;
	}


	/**
	 * Parses out the multiple NPI / TIN numbers from multiple submission
	 *
	 * @param element XML Element
	 * @param thisNode internal Node representation
	 */
	@SuppressWarnings("unchecked")
	private void setNationalProviderIdOnNode(Element element, Node thisNode) {

		Namespace ns = element.getNamespace();

		XPathExpression<?> expression = XPathFactory.instance().compile(PERFORMED_ASSIGNED_ENTITY_PATH,
				Filters.element(), null, xpathNs);
		List<Element> assignedEntities = (List<Element>) expression.evaluate(element);

		assignedEntities.stream()
				.filter(this.validAssignedEntity(ns))
				.forEach(this.mapNpiTin(ns, thisNode));
	}

	/**
	 * Create Namespace primed assignedEntity precondition / filter
	 *
	 * @param ns namespace
	 * @return filter
	 */
	private Predicate<Element> validAssignedEntity(final Namespace ns) {
		return assignedEntity -> {
			Element npiEl = assignedEntity.getChild(ID, ns);
			Element taxEl = assignedEntity.getChild(REPRESENTED_ORGANIZATION, ns);
			Element taxChildEl = null;
			if (npiEl != null && taxEl != null) {
				taxChildEl = taxEl.getChild(ID, ns);
				if (taxChildEl != null) {
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * Create a consumer primed with namespace that will update a given target node with NPI and TIN information.
	 *
	 * @param ns namespace
	 * @param thisNode target node
	 * @return consumer
	 */
	private Consumer<Element> mapNpiTin(final Namespace ns, final Node thisNode) {
		return perform -> {
			String npi = perform
					.getChild(ID, ns)
					.getAttributeValue(EXTENSION);
			String tin = perform
					.getChild(REPRESENTED_ORGANIZATION, ns)
					.getChild(ID, ns)
					.getAttributeValue(EXTENSION);
			if (npi != null && tin != null) { //Only create the child if both values are available
				Node child = new Node(NPI_TIN_ID);
				child.putValue(NATIONAL_PROVIDER_IDENTIFIER, npi);
				child.putValue(TAX_PAYER_IDENTIFICATION_NUMBER, tin);
				thisNode.addChildNode(child);
			}
		};
	}
}

