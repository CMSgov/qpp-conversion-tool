package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;


/**
 * Encoder to serialize Improvement Activity Section. This class is nearly empty due to the fact that it does the same
 * encoding as its super class {@link gov.cms.qpp.conversion.encode.AciSectionEncoder} but is a different templateId.
 */
@Encoder(templateId = "2.16.840.1.113883.10.20.27.2.4")
public class IaSectionEncoder extends AciSectionEncoder {
}
