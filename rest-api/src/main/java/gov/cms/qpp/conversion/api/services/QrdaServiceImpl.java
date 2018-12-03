package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementation of the QRDA-III to QPP conversion service
 */
@Service
public class QrdaServiceImpl implements QrdaService {
	private static final Logger API_LOG = LoggerFactory.getLogger(QrdaServiceImpl.class);

	/**
	 * Preloads the measure configs data
	 */
	@PostConstruct
	public void preloadMeasureConfigs() {
		MeasureConfigs.init();
	}

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param source Object to be converted
	 * @return Results of the conversion
	 */
	@Override
	public ConversionReport convertQrda3ToQpp(Source source) {
		Converter converter = initConverter(source);
		API_LOG.info("Performing QRDA3 to QPP conversion");
		converter.transform();
		return converter.getReport();
	}

	/**
	 * Instantiate a {@link Converter} with a given {@link Source}
	 *
	 * @param source for qrda input
	 * @return converter instance
	 */
	Converter initConverter(Source source) {
		return new Converter(source);
	}
}
