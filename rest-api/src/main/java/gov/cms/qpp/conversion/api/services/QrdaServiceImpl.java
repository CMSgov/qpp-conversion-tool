package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.api.model.ConversionResult;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
@Service
public class QrdaServiceImpl implements QrdaService {

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param fileInputStream Object to be converted
	 * @return Results of the conversion
	 * @throws IOException If error occurs during file upload or conversion
	 */
	@Override
	public ConversionResult convertQrda3ToQpp(final InputStream fileInputStream) throws IOException {
		InputStream conversionResult;
		TransformationStatus status;

		try {
			Converter converter = new Converter(fileInputStream);
			converter.transform();

			conversionResult = converter.getConversionResult();
			status = converter.getStatus();
		} catch (Exception exception) {
			//here because the logging doesn't work yet
			System.out.println("Exception!");
			exception.printStackTrace();
			throw exception;
		}

		String stringConversionResult = IOUtils.toString(conversionResult, "UTF-8");
		//need to work on optimization so this doesn't try to copy a potentially large QPP file
		return new ConversionResult(stringConversionResult, status);
	}
}
