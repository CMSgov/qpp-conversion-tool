package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ValidationServiceImpl implements ValidationService {

	@Autowired
	private Environment environment;

	@Override
	public void validateQpp(final JsonWrapper qpp, final Converter converter) {
		String validationUrl = environment.getProperty("VALIDATION_URL");

		if (validationUrl == null) {
			return;
		}

		ResponseEntity<String> validationResponse = callValidationEndpoint(validationUrl, qpp);

		if (HttpStatus.UNPROCESSABLE_ENTITY.equals(validationResponse.getStatusCode())) {
			AllErrors convertedErrors = convertQppValidationErrorsToQrda(validationResponse, converter);
			throw new TransformException("Converted QPP failed validation", null, convertedErrors);
		}
	}

	private ResponseEntity<String> callValidationEndpoint(String url, JsonWrapper qpp) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new NoHandlingErrorHandler());
		HttpEntity<String> request = new HttpEntity<>(qpp.toString());
		return restTemplate.postForEntity(url, request, String.class);
	}

	private AllErrors convertQppValidationErrorsToQrda(ResponseEntity<String> validationResponse, Converter converter) {
		return new AllErrors();
	}

	private class NoHandlingErrorHandler extends DefaultResponseErrorHandler {
		@Override
		public void handleError(final ClientHttpResponse response) throws IOException {
			//do nothing
		}
	}
}
