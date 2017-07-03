package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.Strata;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class MeasureDataTest {

	private static Map<String, MeasureConfig> glossaryMap;
	private static Map<String, MeasureConfig> configMap;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() throws IOException {
		glossaryMap = MeasureConfigs.initMeasureConfigs("measureGlossary.json");
		configMap = MeasureConfigs.getConfigurationMap();
	}

	@Test
	public void inspectMeasureData() {
		glossaryMap.forEach((key, value) -> {
			MeasureConfig config = configMap.get(key);
			assertNotNull("Missing measure configuration for: " + key, config);
			assertEquals("Should have the same amount of sub populations",
					config.getStrata().size(), value.getStrata().size());

			value.getStrata().forEach(stratum -> {
				List<SubPopulation> pops = config.getStrata().stream()
						.map(Strata::getElectronicMeasureUuids)
						.collect(Collectors.toList());
				assertThat("Required sub population was not present",
						pops, hasItem(stratum.getElectronicMeasureUuids()));
			});
		});
	}

}
