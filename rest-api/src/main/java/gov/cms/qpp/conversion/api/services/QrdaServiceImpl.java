package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.QrdaSource;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the QRDA-III to QPP conversion service
 */
@Service
public class QrdaServiceImpl implements QrdaService {
	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param source Object to be converted
	 * @return Results of the conversion
	 */
	@Override
	public JsonWrapper convertQrda3ToQpp(QrdaSource source) {
		Converter converter = new Converter(source);
		API_LOG.info("Performing QRDA3 to QPP conversion");
		return converter.transform();
	}
}
