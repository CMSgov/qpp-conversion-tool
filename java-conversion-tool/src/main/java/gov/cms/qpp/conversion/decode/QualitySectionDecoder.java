package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;

/**
 * Decoder to read XML Data for an Quality Section (eCQM).
 */
@Decoder(TemplateId.MEASURE_SECTION_V2)
public class QualitySectionDecoder extends QppXmlDecoder {

	public static final String CATEGORY = "category";

	public QualitySectionDecoder(Converter converter) {
		super(converter);
	}

	/**
	 * Decodes an Quality Measure Section into the intermediate Node format
	 *
	 * @param element  XML element that represents the Quality Section
	 * @param thisNode Node represents the quality section
	 * @return {@code DecodeResult.TREE_CONTINUE} to continue down the parsed XML
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		thisNode.putValue(CATEGORY, "quality");
		return DecodeResult.TREE_CONTINUE;
	}
}
