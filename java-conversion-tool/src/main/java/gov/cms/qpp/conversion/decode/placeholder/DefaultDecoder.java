package gov.cms.qpp.conversion.decode.placeholder;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Decoder used to "fill-in" decoders where none have been implemented.
 *
 * Once the decoders have been fully developed, the need for this class goes
 * away.
 *
 * @author David Uselmann
 *
 */
public class DefaultDecoder extends QppXmlDecoder {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(DefaultDecoder.class);

	final String description;

	public DefaultDecoder(Context context, String description) {
		super(context);

		this.description = description;
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		DEV_LOG.debug("Default decoder {} is handling templateId {} and is described as '{}' ",
				getClass(), thisnode.getType().name(), description);
		thisnode.putValue("DefaultDecoderFor", description);
		return DecodeResult.TREE_CONTINUE;
	}

	public static void removeDefaultNode(List<Node> nodes) {
		for (int n = nodes.size() - 1; n >= 0; n--) {
			Node node = nodes.get(n);
			if (node.getValue("DefaultDecoderFor") != null) {
				nodes.remove(n);
			} else {
				removeDefaultNode(node.getChildNodes());
			}
		}
	}

	@Decoder(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS)
	public static class ContinuousVariableMeasureValueCmsDecoder extends DefaultDecoder {

		public ContinuousVariableMeasureValueCmsDecoder(Context context) {
			super(context, "Continuous Variable Measure Value - CMS");
		}
	}

	@Decoder(TemplateId.REPORTING_STRATUM_CMS)
	public static class ReportingStratumCmsDataDecoder extends DefaultDecoder {

		public ReportingStratumCmsDataDecoder(Context context) {
			super(context, "Reporting Stratum - CMS");
		}
	}
}

/**
 * A
 * Document-Level Template: QRDA Category III Report - CMS (V2)
 * 2.16.840.1.113883.10.20.27.1.2:2016-11-01 B Measure Section
 * 2.16.840.1.113883.10.20.24.2.2 C QRDA Category III Reporting Parameters
 * Section - CMS (V2)* 2.16.840.1.113883.10.20.27.2.6:2016-11-01* D QRDA
 * Category III Measure Section - CMS (V2)
 * 2.16.840.1.113883.10.20.27.2.3:2016-11-01 E Improvement Activity Section
 * 2.16.840.1.113883.10.20.27.2.4:2016-09-01 F Advancing Care Information
 * Section 2.16.840.1.113883.10.20.27.2.5:2016-09-01 G Measure Reference
 * 2.16.840.1.113883.10.20.24.3.98 H Advancing Care Information Numerator
 * Denominator Type Measure Reference and Results
 * 2.16.840.1.113883.10.20.27.3.28:2016-09-01 I Advancing Care Information
 * Measure Performed Measure Reference and Results
 * 2.16.840.1.113883.10.20.27.3.29:2016-09-01 J Improvement Activity Performed
 * Measure Reference and Results 2.16.840.1.113883.10.20.27.3.33:2016-09-01 K
 * Measure Reference and Results - CMS (V2)
 * 2.16.840.1.113883.10.20.27.3.17:2016-11-01 L Reporting Parameters Act - CMS
 * (V2)* 2.16.840.1.113883.10.20.27.3.23:2016-11-01* M Measure Performed
 * 2.16.840.1.113883.10.20.27.3.27:2016-09-01 N Performance Rate
 * 2.16.840.1.113883.10.20.27.3.30:2016-09-01 O Advancing Care Information
 * Numerator Denominator Type Measure Numerator Data
 * 2.16.840.1.113883.10.20.27.3.31:2016-09-01 P Advancing Care Information
 * Numerator Denominator Type Measure Denominator Data
 * 2.16.840.1.113883.10.20.27.3.32:2016-09-01 Q Aggregate Count
 * 2.16.840.1.113883.10.20.27.3.3 R Aggregate Count - CMS
 * 2.16.840.1.113883.10.20.27.3.24 S Continuous Variable Measure Value - CMS
 * 2.16.840.1.113883.10.20.27.3.26 T Ethnicity Supplemental Data Element – CMS
 * (V2) 2.16.840.1.113883.10.20.27.3.22:2016-11-01 U Measure Data - CMS (V2)
 * 2.16.840.1.113883.10.20.27.3.16:2016-11-01 V Reporting Stratum - CMS
 * 2.16.840.1.113883.10.20.27.3.20 W Sex Supplemental Data Element - CMS (V2)
 * 2.16.840.1.113883.10.20.27.3.21:2016-11-01 X Race Supplemental Data Element -
 * CMS (V2) 2.16.840.1.113883.10.20.27.3.19:2016-11-01 Y Payer Supplemental Data
 * Element - CMS (V2) 2.16.840.1.113883.10.20.27.3.18:2016-11-01 Z Measure
 * Reference and Results- CMS (V2) 2.16.840.1.113883.10.20.27.3.1:2016-09-01 ZZ
 * Performance Rate for Proportion Measure - CMS (V2)
 * 2.16.840.1.113883.10.20.27.3.25:2016-11-01
 */