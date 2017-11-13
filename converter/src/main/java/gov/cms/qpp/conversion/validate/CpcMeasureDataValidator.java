package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataPayerDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataRaceDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataSexDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Validator(value = TemplateId.MEASURE_DATA_CMS_V2, program = Program.CPC)
public class CpcMeasureDataValidator extends NodeValidator {

	@Override
	protected void internalValidateSingleNode(Node node) {
		List<Node> raceDataNodes =
				node.getChildNodes(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2).collect(Collectors.toList());
		List<SupplementalData> raceTypes = SupplementalData.getSupplementalDataListByType("R");

		List<Node> sexDataNodes =
				node.getChildNodes(TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2).collect(Collectors.toList());
		List<SupplementalData> sexTypes = SupplementalData.getSupplementalDataListByType("S");

		List<Node> ethnicityDataNodes =
				node.getChildNodes(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2).collect(Collectors.toList());
		List<SupplementalData> ethnicityTypes = SupplementalData.getSupplementalDataListByType("E");

		List<Node> payerDataNodes =
				node.getChildNodes(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2).collect(Collectors.toList());
		List<SupplementalData> payerTypes = SupplementalData.getSupplementalDataListByType("P");

		for(SupplementalData raceData : raceTypes) {
			Node validateRaceNode = raceDataNodes.stream()
					.filter(makeValidatorByCategoryAndCode(SupplementalDataRaceDecoder.RACE_NAME, raceData.getCode()))
					.findFirst()
					.orElse(null);

			if (validateRaceNode == null) {
				MeasureConfig config =
						MeasureConfigs.getConfigurationMap()
								.get(node.getParent().getValue(QualityMeasureIdValidator.MEASURE_ID));
				LocalizedError error = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(raceData.getCode(),
						config.getElectronicMeasureId(), node.getValue(MeasureDataDecoder.MEASURE_TYPE));
				addValidationError(Detail.forErrorAndNode(error, node));
			}
		}

		for(SupplementalData sexData : sexTypes) {
			Node validateSexNode = sexDataNodes.stream()
					.filter(makeValidatorByCategoryAndCode(SupplementalDataSexDecoder.SEX_NAME, sexData.getCode()))
					.findFirst()
					.orElse(null);
			if (validateSexNode == null) {
				MeasureConfig config =
						MeasureConfigs.getConfigurationMap()
								.get(node.getParent().getValue(QualityMeasureIdValidator.MEASURE_ID));
				LocalizedError error = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(sexData.getCode(),
						config.getElectronicMeasureId(), node.getValue(MeasureDataDecoder.MEASURE_TYPE));
				addValidationError(Detail.forErrorAndNode(error, node));
			}
		}

		for(SupplementalData ethnicityData : ethnicityTypes) {
			Node validateRaceNode = ethnicityDataNodes.stream()
					.filter(makeValidatorByCategoryAndCode(SupplementalDataEthnicityDecoder.ETHNICITY_NAME,
							ethnicityData.getCode()))
					.findFirst()
					.orElse(null);
			if (validateRaceNode == null) {
				MeasureConfig config =
						MeasureConfigs.getConfigurationMap()
								.get(node.getParent().getValue(QualityMeasureIdValidator.MEASURE_ID));
				LocalizedError error = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(ethnicityData.getCode(),
						config.getElectronicMeasureId(), node.getValue(MeasureDataDecoder.MEASURE_TYPE));
				addValidationError(Detail.forErrorAndNode(error, node));
			}
		}

		for(SupplementalData payerData : payerTypes) {
			Node validateRaceNode = payerDataNodes.stream()
					.filter(makeValidatorByCategoryAndCode(SupplementalDataPayerDecoder.PAYER_NAME,
							payerData.getCode()))
					.findFirst()
					.orElse(null);
			if (validateRaceNode == null) {
				MeasureConfig config =
						MeasureConfigs.getConfigurationMap()
								.get(node.getParent().getValue(QualityMeasureIdValidator.MEASURE_ID));
				LocalizedError error = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(payerData.getCode(),
						config.getElectronicMeasureId(), node.getValue(MeasureDataDecoder.MEASURE_TYPE));
				addValidationError(Detail.forErrorAndNode(error, node));
			}
		}
	}

	private Predicate<Node> makeValidatorByCategoryAndCode(String category, String code) {
		return thisNode -> {
				return code.equalsIgnoreCase(thisNode.getValue(category));
		};
	}
}
