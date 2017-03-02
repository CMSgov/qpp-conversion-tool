package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.2.4")
public class IaSectionEncoder extends AciSectionEncoder {

	public IaSectionEncoder() {
	}

	@Override
	public void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		super.internalEcode(wrapper, node);
	}

}
