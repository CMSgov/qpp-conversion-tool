package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Implementation of the QRDA-III to QPP conversion service
 */
@Service
public class QrdaServiceImpl implements QrdaService {

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param fileInputStream Object to be converted
	 * @return Results of the conversion
	 */
	@Override
	public JsonWrapper convertQrda3ToQpp(InputStream fileInputStream) {
		Converter converter = new Converter(fileInputStream);
		return converter.transform();
	}
}
