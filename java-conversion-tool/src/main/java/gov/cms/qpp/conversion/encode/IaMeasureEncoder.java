package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.3.33")
public class IaMeasureEncoder extends QppOutputEncoder {

	public IaMeasureEncoder() {
	}

	@Override
	protected void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putObject("measureId", node.getValue("measureId"));
		wrapper.putBoolean("value", node.getValue("iaMeasured"));
	}

}
