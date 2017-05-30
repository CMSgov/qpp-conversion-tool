package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.encode.helper.ReportingParameters;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize the root node of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */

@Encoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentEncoder extends QppOutputEncoder {
	static final String PERFORMANCE_END = "performanceEnd";
	static final String PERFORMANCE_YEAR = "performanceYear";
	static final String PERFORMANCE_START = "performanceStart";
	static final String MEASUREMENT_SETS = "measurementSets";
	static final String SOURCE = "source";
	static final String PROVIDER = "provider";

	/**
	 * internalEncode encodes nodes into Json Wrapper.
	 *
	 * @param wrapper  will hold the json format of nodes
	 * @param thisNode holds the decoded node sections of clinical document
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node thisNode) {
		wrapper.putString(ClinicalDocumentDecoder.PROGRAM_NAME,
			thisNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME));
		wrapper.putString(ClinicalDocumentDecoder.ENTITY_TYPE,
			thisNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE));
		wrapper.putString(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
			thisNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER));
		wrapper.putString(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER,
			thisNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER));

		Map<TemplateId, Node> childMapByTemplateId = thisNode.getChildNodes().stream().collect(
			Collectors.toMap(Node::getType, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
		Optional<Node> reportingNode = ReportingParameters.getReportingNode(childMapByTemplateId);

		Optional<String> performanceStart =
			reportingNode.flatMap(p -> Optional.of(p.getValue(PERFORMANCE_START)));
		Optional<String> performanceEnd =
			reportingNode.flatMap(p -> Optional.of(p.getValue(PERFORMANCE_END)));

		if (performanceStart.isPresent()) {
			wrapper.putInteger(PERFORMANCE_YEAR, performanceStart.get().substring(0, 4));
		}

		JsonWrapper measurementSets =
			encodeMeasurementSets(childMapByTemplateId, performanceStart, performanceEnd);
			wrapper.putObject(MEASUREMENT_SETS, measurementSets);
	}

	/**
	 * Method for encoding each child measurement set
	 *
	 * @param childMapByTemplateId object that represents the document's children
	 * @param performanceStart object that represents the measurement performance start
	 * @param performanceEnd object that represents the measurement performance end
	 * @return
	 * @throws EncodeException If error occurs during encoding
	 */
	private JsonWrapper encodeMeasurementSets(Map<TemplateId, Node> childMapByTemplateId,
												Optional<String> performanceStart,
												Optional<String> performanceEnd) {
		JsonWrapper measurementSetsWrapper = new JsonWrapper();
		JsonWrapper childWrapper;
		JsonOutputEncoder sectionEncoder;

		for (Node child : childMapByTemplateId.values()) {
			if (MultipleTinsDecoder.NPI_TIN_ID.equalsIgnoreCase(child.getType())) {
				continue; //MultiTINS is not a real encoder.
			}
			childWrapper = new JsonWrapper();
			sectionEncoder = ENCODERS.get(child.getType());

			// Section encoder is null when a decoder exists without a corresponding encoder
			// currently don't have a set of IA Encoders, but this will protect against others
			try {
				sectionEncoder.encode(childWrapper, child);

				if (performanceStart.isPresent()) {
					childWrapper.putDate(PERFORMANCE_START, performanceStart.get());
				}
				if (performanceEnd.isPresent()) {
					childWrapper.putDate(PERFORMANCE_END, performanceEnd.get());
				}

				measurementSetsWrapper.putObject(childWrapper);
			} catch (NullPointerException exc) {
				String message = "No encoder for decoder : " + child.getType();
				throw new EncodeException(message, exc);
			}
		}

		return measurementSetsWrapper;
	}

}
