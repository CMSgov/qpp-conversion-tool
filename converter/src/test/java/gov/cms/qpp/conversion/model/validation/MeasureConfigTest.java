package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertWithMessage;

public class MeasureConfigTest {

	private static MeasureConfig theConfig = null;
	private static List<MeasureConfig> measureConfigs = null;

	@BeforeClass
	public static void setup() {
		ObjectMapper mapper = new ObjectMapper();

		InputStream stream =
				ClasspathHelper.contextClassLoader().getResourceAsStream("measures-data-aci-test.json");

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			measureConfigs = mapper.readValue(stream, measureConfigType);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}

		theConfig = measureConfigs.get(0);
	}

	@Test
	public void testConfigsDeserialization() {
		assertWithMessage("measure configs should not be empty")
				.that(measureConfigs).hasSize(1);
	}

	@Test
	public void verifyMeasureConfigCategory() {
		assertWithMessage("category should be aci")
				.that(theConfig.getCategory()).isEqualTo("aci");
	}

	@Test
	public void verifyMeasureConfigFirstPerfYear() {
		assertWithMessage("firstPerformanceYear should be 2017")
				.that(theConfig.getFirstPerformanceYear()).isEqualTo(2017);
	}

	@Test
	public void verifyMeasureConfigLastPerfYear() {
		assertWithMessage("lastPerformanceYear should be 0")
				.that(theConfig.getLastPerformanceYear()).isEqualTo(0);
	}

	@Test
	public void verifyMeasureConfigMetricType() {
		assertWithMessage("metricType should be proportion")
				.that(theConfig.getMetricType()).isEqualTo("proportion");
	}

	@Test
	public void verifyMeasureConfigMeasureId() {
		assertWithMessage("measureId should be ACI_EP_1")
				.that(theConfig.getMeasureId()).isEqualTo("ACI_EP_1");
	}

	@Test
	public void verifyMeasureConfigTitle() {
		assertWithMessage("title should be e-Prescribing")
				.that(theConfig.getTitle()).isEqualTo("e-Prescribing");
	}

	@Test
	public void verifyMeasureConfigDescription() {
		String expectation = "At least one permissible prescription written by the MIPS eligible clinician is queried for a drug formulary and transmitted electronically using certified EHR technology.";
		assertWithMessage("description should be long")
				.that(theConfig.getDescription()).isEqualTo(expectation);
	}

	@Test
	public void verifyMeasureConfigIsRequired() {
		assertWithMessage("isRequired should be true")
				.that(theConfig.isRequired()).isTrue();
	}

	@Test
	public void verifyMeasureConfigMeasureSet() {
		assertWithMessage("measureSet should be null")
				.that(theConfig.getMeasureSet()).isNull();
	}

	@Test
	public void verifyMeasureConfigIsBonus() {
		assertWithMessage("isBonus should be false")
				.that(theConfig.isBonus()).isFalse();
	}

	@Test
	public void verifyMeasureConfigObjective() {
		assertWithMessage("objective should be electronicPrescribing")
				.that(theConfig.getObjective()).isEqualTo("electronicPrescribing");
	}

	@Test
	public void testSubPopulations() {
		SubPopulation subPopulation1 = new SubPopulation();
		subPopulation1.setNumeratorUuid("subPopulation1");

		SubPopulation subPopulation2 = new SubPopulation();
		subPopulation1.setNumeratorUuid("subPopulation2");

		SubPopulation subPopulation3 = new SubPopulation();
		subPopulation1.setNumeratorUuid("subPopulation3");

		Strata strata1 = new Strata();
		strata1.setElectronicMeasureUuids(subPopulation1);
		Strata strata2 = new Strata();
		strata2.setElectronicMeasureUuids(subPopulation2);
		Strata strata3 = new Strata();
		strata3.setElectronicMeasureUuids(subPopulation3);

		MeasureConfig measureConfig = new MeasureConfig();
		measureConfig.setStrata(Arrays.asList(strata1, strata2, strata3));

		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		assertWithMessage("The subpopulations are incorrect.")
				.that(subPopulations).containsExactly(subPopulation1, subPopulation2, subPopulation3);
	}
}
