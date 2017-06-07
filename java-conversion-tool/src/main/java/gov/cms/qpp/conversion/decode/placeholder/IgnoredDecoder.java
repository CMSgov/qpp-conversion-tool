package gov.cms.qpp.conversion.decode.placeholder;

import org.jdom2.Element;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class IgnoredDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		return DecodeResult.TREE_FINISHED;
	}

	@Decoder(TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class SexSupplementalDataElementalCmsV2Decoder extends IgnoredDecoder {

	}

	@Decoder(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class RaceSupplementalDataElementCmsV2Decoder extends IgnoredDecoder {

	}

	@Decoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class PayerSupplementalDataElementCmsV2Decoder extends IgnoredDecoder {

	}

	@Decoder(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class EthnicitySupplementalDataElementCmsV2Decoder extends IgnoredDecoder {

	}

}