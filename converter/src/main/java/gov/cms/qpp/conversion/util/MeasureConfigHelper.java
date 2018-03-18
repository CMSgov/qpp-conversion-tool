package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

public class MeasureConfigHelper {

	public static final String MEASURE_ID = "measureId";

	private MeasureConfigHelper() {
		// private for this helper class
	}

	/**
	 * Convenience method to retrieve the measure configuration for validation from an ecqm node
	 *
	 * @param node Contains the id that associates with the measure config
	 * @return
	 */
	public static MeasureConfig getMeasureConfig(Node node) {
		String measureId =  node.getValue(MEASURE_ID);
		return MeasureConfigs.getConfigurationMap().get(measureId);
	}
}
