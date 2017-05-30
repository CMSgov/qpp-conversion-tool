package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.api.model.ConversionResult;
import gov.cms.qpp.conversion.api.services.QrdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller to handle uploading files for QRDA-III Conversion
 */
@RestController
@RequestMapping("/v1/qrda3")
@CrossOrigin
public class QrdaControllerV1 {
	@Autowired
	private QrdaService qrdaService;

	/**
	 * Endpoint to transform an uploaded file into a valid or error json response
	 *
	 * @param file Uploaded file
	 * @param response Servlet response status
	 * @return Valid json or error json content
	 * @throws IOException If errors occur during file upload or conversion
	 */
	@RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public String uploadQrdaFile(@RequestParam MultipartFile file, HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.CREATED.value());

		ConversionResult conversionResult = qrdaService.convertQrda3ToQpp(file.getInputStream());

		if (!TransformationStatus.SUCCESS.equals(conversionResult.getStatus())) {
			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
		}

		return conversionResult.getContent();
	}
}
