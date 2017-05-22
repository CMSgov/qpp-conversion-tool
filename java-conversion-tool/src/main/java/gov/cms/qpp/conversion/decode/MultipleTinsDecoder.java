package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.List;

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */
@Decoder(TemplateId.MULTIPLE_TINS)
public class MultipleTinsDecoder extends QppXmlDecoder {

	public static final String NPI_TIN_ID = "NPITIN";
	private static final String PERFORMED_ASSIGNED_ENTITY_PATH =
		"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity";

	/**
	 * internalDecode parses the xml fragment into thisNode
	 *
	 * @param element Element
	 * @param thisNode Node
	 * @return DecodeResult.TreeFinished thisNode gets the newly parsed xml fragment
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setNationalProviderIdOnNode(element, thisNode);

		Node child = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		ClinicalDocumentDecoder clinicalDocument = new ClinicalDocumentDecoder();
		clinicalDocument.setNamespace(element, clinicalDocument);
		clinicalDocument.internalDecode(element, child);
		thisNode.addChildNode(child);

		return DecodeResult.TREE_CONTINUE;
	}


	/**
	 * Parses out the multiple NPI / TIN numbers from multiple submission
	 * @param element XML Element
	 * @param thisNode internal Node representation
	 */
	private void setNationalProviderIdOnNode(Element element, Node thisNode) {
		final String id = "id";
		final String extension = "extension";
		final String representedOrganization = "representedOrganization";

		XPathExpression<?> expression = XPathFactory.instance().compile(PERFORMED_ASSIGNED_ENTITY_PATH,
			Filters.element(), null,  xpathNs);
		List<Element> assignedEntities = (List<Element>) expression.evaluate(element);
		for ( Element assignedEntity: assignedEntities)
		{
			Element npiEl = assignedEntity.getChild(id, element.getNamespace());
			Element taxEl = assignedEntity.getChild(representedOrganization,
				element.getNamespace()).getChild(id, element.getNamespace());

			String npi = npiEl.getAttributeValue(extension);
			String tin = taxEl.getAttributeValue(extension);

			if ( tin != null && npi != null  ) {
				Node child = new Node(NPI_TIN_ID);
				child.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, tin);
				child.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, npi);
				thisNode.addChildNode(child);
			}
		}
	}
}
