package gov.cms.qpp.conversion.api.services.internal;

import com.jayway.jsonpath.JsonPathException;
import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ErrorMessage;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.*;
import gov.cms.qpp.conversion.model.error.Error;
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

import jakarta.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
		if (!StringUtils.isEmpty(validationUrl) && !Constants.VALIDATION_DISABLE_VARIABLE.equalsIgnoreCase(validationUrl)) {
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
	public void validateQpp(ConversionReport conversionReport) {
		String validationUrl = environment.getProperty(Constants.VALIDATION_URL_ENV_VARIABLE);

		if (StringUtils.isEmpty(validationUrl)) {
			return;
		}

		JsonWrapper wrapper = conversionReport.getEncodedWithMetadata();
		ResponseEntity<String> validationResponse = callValidationEndpoint(validationUrl, wrapper.copyWithoutMetadata());
		API_LOG.info("Submission Validation Response Code - " + validationResponse.getStatusCode());

		if (HttpStatus.UNPROCESSABLE_ENTITY == validationResponse.getStatusCode()) {

			API_LOG.warn("Failed QPP validation");

			AllErrors convertedErrors = convertQppValidationErrorsToQrda(validationResponse.getBody(), wrapper);

			conversionReport.setRawValidationDetails(validationResponse.getBody());
			conversionReport.setReportDetails(convertedErrors);

			throw new QppValidationException("Converted QPP failed validation", null, conversionReport);
		}
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

		String implCookie = environment.getProperty(Constants.IMPL_COOKIE);
		if (implCookie != null && !implCookie.isEmpty()) {
			headers.add(HttpHeaders.COOKIE, "ACA=" + implCookie);
		}

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
		if (validationResponse == null) {
			return errors;
		}

		try {
			Error error = getError(validationResponse);
			error.getDetails().forEach(detail -> {
				String newPath = UNABLE_PROVIDE_XPATH;
				detail.setMessage(SV_LABEL + detail.getMessage());
				newPath = PathCorrelator.prepPath(detail.getLocation().getPath(), wrapper);
				detail.getLocation().setPath(newPath);
			});
			errors.addError(error);
		} catch (Exception exception) {
			API_LOG.warn("Failed to convert from json path to an XPath.", exception);
			List<Detail> details = List.of(Detail.forProblemCode(ProblemCode.UNEXPECTED_ERROR));
			errors.addError(new Error("CT", details));
		}

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
		 */
		@Override
		public void handleError(final ClientHttpResponse response) {
			//do nothing
		}
	}
}
