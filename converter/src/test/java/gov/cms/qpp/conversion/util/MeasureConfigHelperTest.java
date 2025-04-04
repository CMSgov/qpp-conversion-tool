package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

public class MeasureConfigHelperTest {
	private static final String THE_UUID = "2c928082-74c2-3313-0174-c60bd07b02a6";
	private static final String THE_E_MEASURE_ID = "CMS122v10";
	private static final String THE_MEASURE_ID = "001";
	private static Set<String> MULTI_TO_SINGLE_MEASURES = Set.of("001", "008", "143", "438");

	@Nested
	@DisplayName("Describe measure value priority")
	class MeasurePriority {
		private MeasureConfig config;

		@BeforeEach
		void setup() {
			config = new MeasureConfig();
		}

		@Test
		@DisplayName("Default for message when no measure is given")
		void noMeasureId() {
			String measureId = MeasureConfigHelper.getPrioritizedId(config);

			assertThat(measureId).isEqualTo(MeasureConfigHelper.NO_MEASURE);
		}

		@Test
		@DisplayName("Measure id only wins when no one else shows up")
		void measureIdPriority() {
			config.setMeasureId(THE_MEASURE_ID);
			String measureId = MeasureConfigHelper.getPrioritizedId(config);

			assertThat(measureId).isEqualTo(THE_MEASURE_ID);
		}

		@Test
		@DisplayName("Electronic measure version id > measure id")
		void electronicMeasureVersionUuidPriority() {
			config.setMeasureId(THE_MEASURE_ID);
			config.setElectronicMeasureVerUuid(THE_UUID);
			String measureId = MeasureConfigHelper.getPrioritizedId(config);

			assertThat(measureId).isEqualTo(THE_UUID);
		}

		@Test
		@DisplayName("Electronic measure id is the best")
		void electronicMeasureIdPriority() {
			config.setMeasureId(THE_MEASURE_ID);
			config.setElectronicMeasureVerUuid(THE_UUID);
			config.setElectronicMeasureId(THE_E_MEASURE_ID);
			String measureId = MeasureConfigHelper.getPrioritizedId(config);

			assertThat(measureId).isEqualTo(THE_E_MEASURE_ID);
		}

		@Test
		void checkMultiConfigMeasures() {
			config.setMeasureId(THE_MEASURE_ID);
			MeasureConfigHelper.setMultiToSinglePerfRateMeasureId(MULTI_TO_SINGLE_MEASURES);
			boolean isMultiToSingle = MeasureConfigHelper.checkMultiToSinglePerformanceRateId(THE_MEASURE_ID);
			assertThat(isMultiToSingle).isTrue();

		}
	}
}
