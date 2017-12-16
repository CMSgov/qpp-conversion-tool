package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Modify the controller to send back different responses for exceptions
 */
@ControllerAdvice
public class ExceptionHandlerControllerV1 extends ResponseEntityExceptionHandler {
	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	@Autowired
	private AuditService auditService;

	/**
	 * "Catch" the {@link TransformException}.
	 * Return the {@link AllErrors} with an HTTP status 422.
	 *
	 * @param exception The TransformException that was "caught".
	 * @return The AllErrors dto that details the TransformException.
	 */
	@ExceptionHandler(TransformException.class)
	@ResponseBody
	ResponseEntity<AllErrors> handleTransformException(TransformException exception) {
		API_LOG.error("Transform exception occurred", exception);
		auditService.failConversion(exception.getConversionReport());
		return cope(exception);
	}

	/**
	 * "Catch" the {@link QppValidationException}.
	 * Return the {@link AllErrors} with an HTTP status 422.
	 *
	 * @param exception The QppValidationException that was "caught".
	 * @return The AllErrors dto that details the QppValidationException.
	 */
	@ExceptionHandler(QppValidationException.class)
	@ResponseBody
	ResponseEntity<AllErrors> handleQppValidationException(QppValidationException exception) {
		API_LOG.error("Validation exception occurred", exception);
		auditService.failValidation(exception.getConversionReport());
		return cope(exception);
	}

	/**
	 * "Catch" the {@link NoFileInDatabaseException}.
	 * Return the {@link AllErrors} with an HTTP status 422.
	 *
	 * @param exception The NoFileInDatabaseException that was "caught".
	 * @return The NoFileInDatabaseException message
	 */
	@ExceptionHandler(NoFileInDatabaseException.class)
	@ResponseBody
	ResponseEntity<String> handleFileNotFoundException(NoFileInDatabaseException exception) {
		API_LOG.error("A database error occurred", exception);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<>(exception.getMessage(), httpHeaders, HttpStatus.NOT_FOUND);
	}
	
	private ResponseEntity<AllErrors> cope(TransformException exception) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(exception.getDetails(), httpHeaders, HttpStatus.UNPROCESSABLE_ENTITY);
	}

}
