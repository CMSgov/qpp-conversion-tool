package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.Converter;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/v1/qrda3")
@CrossOrigin
public class QrdaControllerV1 {

	@RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseStatus(HttpStatus.CREATED)
	public String createResource(@RequestParam MultipartFile file) throws IOException {
		InputStream conversionResult;
		try {
			Converter converter = new Converter(file.getInputStream());
			converter.transform();

			conversionResult = converter.getConversionResult();
		} catch(Exception exception) {
			System.out.println("Exception!");
			exception.printStackTrace();
			throw exception;
		}

		return IOUtils.toString(conversionResult, "UTF-8");
	}
}
