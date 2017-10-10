package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.Strata;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class MeasureDataTest {

	private static Map<String, MeasureConfig> glossaryMap;
	private static Map<String, MeasureConfig> configMap;

	@BeforeClass
	public static void setup() throws IOException {
		glossaryMap = MeasureConfigs.grabConfiguration("measureGlossary.json");
		configMap = MeasureConfigs.getConfigurationMap();
	}

	@Test
	public void inspectMeasureData() {
		glossaryMap.forEach((key, value) -> {
			MeasureConfig config = configMap.get(key);
			assertWithMessage("Missing measure configuration for: %s", key)
					.that(config)
					.isNotNull();

			assertWithMessage("Should have the same amount of sub populations")
					.that(config.getStrata())
					.hasSize(value.getStrata().size());

			List<SubPopulation> pops = config.getStrata().stream()
					.map(Strata::getElectronicMeasureUuids)
					.collect(Collectors.toList());

			value.getStrata().forEach(stratum -> {
				assertWithMessage("Required sub population was not present within: %s", config.getElectronicMeasureId())
						.that(pops)
						.contains(stratum.getElectronicMeasureUuids());
			});
		});
	}

}
