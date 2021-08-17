package gov.cms.qpp.acceptance;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.Strata;
import gov.cms.qpp.conversion.model.validation.SubPopulation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertWithMessage;

class MeasureDataTest {

	private static Map<String, MeasureConfig> glossaryMap;
	private static Map<String, MeasureConfig> configMap;

	@BeforeAll
	static void setup() {
		glossaryMap = MeasureConfigs.grabConfiguration("measureGlossary.json");
		configMap = MeasureConfigs.getConfigurationMap();

	}
}
