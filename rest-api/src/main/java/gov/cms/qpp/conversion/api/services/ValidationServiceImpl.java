package gov.cms.qpp.conversion.api.services;


import com.jayway.jsonpath.JsonPathException;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ErrorMessage;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Implementation for the QPP Validation Service
 */
@Service
public class ValidationServiceImpl implements ValidationService {

	private static final Logger API_LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);
	static final String CONTENT_TYPE = "application/json";
	public static final String SV_LABEL = "SV - ";

	private Environment environment;
	private RestTemplate restTemplate;
	protected static final String UNABLE_PROVIDE_XPATH = "Unable to provide an XPath.";

	/**
	 * init ValidationServiceImpl instances
	 *
	 * @param environment hooks to application environment
	 */
	public ValidationServiceImpl(final Environment environment) {
		this.environment = environment;
		this.restTemplate = new RestTemplate();
	}

	/**
	 * Logs on startup whether a validation url is present
	 */
	@PostConstruct
	public void checkForValidationUrlVariable() {
		String validationUrl = environment.getProperty(Constants.VALIDATION_URL_ENV_VARIABLE);
		if (!StringUtils.isEmpty(validationUrl)) {
			apiLog(Constants.VALIDATION_URL_ENV_VARIABLE + " is set to " + validationUrl);
		} else {
			apiLog(Constants.VALIDATION_URL_ENV_VARIABLE + " is unset");
		}
	}

	/**
	 * Workaround to a problem with our logging dependencies preventing us from using TestLogger
	 *
	 * @param message The message to log
	 */
	protected void apiLog(String message) {
		API_LOG.info(message);
	}

	/**
	 * Validates that the given QPP is valid.
	 *
	 * @param conversionReport A report on the status of the conversion.
	 */
	@Override
	public void validateQpp(final Converter.ConversionReport conversionReport) {
		String validationUrl = environment.getProperty(Constants.VALIDATION_URL_ENV_VARIABLE);

		if (validationUrl == null || validationUrl.isEmpty()) {
			return;
		}

		conversionReport.getEncoded().stream().forEach(wrapper -> {
			ResponseEntity<String> validationResponse = callValidationEndpoint(validationUrl, wrapper);

			if (HttpStatus.UNPROCESSABLE_ENTITY.equals(validationResponse.getStatusCode())) {

				API_LOG.warn("Failed QPP validation");

				AllErrors convertedErrors = convertQppValidationErrorsToQrda(validationResponse.getBody(), wrapper);

				conversionReport.setRawValidationDetails(validationResponse.getBody());
				conversionReport.setReportDetails(convertedErrors);

				throw new QppValidationException("Converted QPP failed validation", null, conversionReport);
			}
		});
	}

	/**
	 * Calls the validation API end-point.
	 *
	 * @param url The URL of the validation API end-point.
	 * @param qpp The QPP to validate.
	 * @return The response from the validation API end-point.
	 */
	private ResponseEntity<String> callValidationEndpoint(String url, JsonWrapper qpp) {
		restTemplate.setErrorHandler(new NoHandlingErrorHandler());
		HttpEntity<String> request = new HttpEntity<>(qpp.toString(), getHeaders());

		API_LOG.info("Calling QPP validation API {}", url);

		return restTemplate.postForEntity(url, request, String.class);
	}

	/**
	 * Assemble headers for validation call.
	 *
	 * @return the headers
	 */
	HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
		headers.add(HttpHeaders.ACCEPT, CONTENT_TYPE);

		String submissionToken = environment.getProperty(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE);
		if (submissionToken != null && !submissionToken.isEmpty()) {
			headers.add(HttpHeaders.AUTHORIZATION,
					"Bearer " + submissionToken);
		}

		return headers;
	}

	/**
	 * Converts the QPP error returned from the validation API to QRDA3 errors
	 *
	 * @param validationResponse The JSON response containing a QPP error.
	 * @param wrapper The QPP that resulted in the QPP error.
	 * @return The QRDA3 errors.
	 */
	AllErrors convertQppValidationErrorsToQrda(String validationResponse, JsonWrapper wrapper) {
		AllErrors errors = new AllErrors();
		Error error = getError(validationResponse);

		error.getDetails().forEach(detail -> {
			detail.setMessage(SV_LABEL + detail.getMessage());
			String newPath = UNABLE_PROVIDE_XPATH;
			try {
				newPath = PathCorrelator.prepPath(detail.getPath(), wrapper);
			} catch (ClassCastException | JsonPathException exc) {
				API_LOG.warn("Failed to convert from json path to an XPath.", exc);
			}
			detail.setPath(newPath);
		});

		errors.addError(error);

		return errors;
	}

	/**
	 * Deserializes the JSON QPP error into an {@link Error} object.
	 *
	 * @param response The JSON response containing a QPP error.
	 * @return An Error object.
	 */
	Error getError(String response) {
		return JsonHelper.readJson(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)),
				ErrorMessage.class)
				.getError();
	}

	/**
	 * A private static class that tells the {@link RestTemplate} to not throw an exception on HTTP status 3xx and 4xx.
	 */
	private static class NoHandlingErrorHandler extends DefaultResponseErrorHandler {
		/**
		 * Empty so it doesn't throw an exception.
		 *
		 * @param response The ClientHttpResponse.
		 * @throws IOException An IOException.
		 */
		@Override
		public void handleError(final ClientHttpResponse response) throws IOException {
			//do nothing
		}
	}
}
