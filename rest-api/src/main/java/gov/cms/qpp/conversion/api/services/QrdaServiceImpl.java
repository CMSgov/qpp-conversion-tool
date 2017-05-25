package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class QrdaServiceImpl implements QrdaService {
	@Override
	public String convertQrda3ToQpp(final InputStream fileInputStream) throws IOException {
		InputStream conversionResult;

		try {
			Converter converter = new Converter(fileInputStream);
			converter.transform();

			conversionResult = converter.getConversionResult();
		} catch(Exception exception) {
			//here because the logging doesn't work yet
			System.out.println("Exception!");
			exception.printStackTrace();
			throw exception;
		}

		return IOUtils.toString(conversionResult, "UTF-8");
	}
}
