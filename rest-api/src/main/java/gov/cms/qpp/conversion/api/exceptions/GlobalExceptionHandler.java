package gov.cms.qpp.conversion.api.exceptions;

import com.amazonaws.AmazonServiceException;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Modify the controller to send back different responses for exceptions
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger API_LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private AuditService auditService;

	/**
	 * initialize controller
	 *
	 * @param auditService {@link AuditService} facilitates persistence of conversion results
	 */
	public GlobalExceptionHandler(final AuditService auditService) {
		this.auditService = auditService;
	}

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
		API_LOG.info("Transform failed validation (422): {}", exception.getMessage());
		API_LOG.debug("TransformException details", exception);

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
		API_LOG.info("Submission validation failed (422): {}", exception.getMessage());
		API_LOG.debug("QppValidationException details", exception);

		auditService.failValidation(exception.getConversionReport());
		return cope(exception);
	}

	/**
	 * "Catch" the {@link NoFileInDatabaseException}.
	 * Return the {@link AllErrors} with an HTTP status 404.
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

	/**
	 * "Catch" the {@link InvalidFileTypeException}.
	 * Return the {@link AllErrors} with an HTTP status 404.
	 *
	 * @param exception The InvalidFileTypeException that was "caught".
	 * @return The InvalidFileTypeException message
	 */
	@ExceptionHandler(InvalidFileTypeException.class)
	@ResponseBody
	ResponseEntity<String> handleInvalidFileTypeException(InvalidFileTypeException exception) {
		API_LOG.error("A file type error occurred", exception);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<>(exception.getMessage(), httpHeaders, HttpStatus.NOT_FOUND);
	}

	/**
	 * "Catch" the {@link BadZipException}.
	 * Return the {@link AllErrors} with an HTTP status 400.
	 *
	 * @param exception The BadZipException that was "caught".
	 * @return The BadZipException message
	 */
	@ExceptionHandler(BadZipException.class)
	@ResponseBody
	ResponseEntity<String> handleBadZipException(BadZipException exception) {
		API_LOG.error("Zip file is corrupt, incomplete, or invalid.", exception);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<>(exception.getMessage(), httpHeaders, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AmazonServiceException.class)
	@ResponseBody
	ResponseEntity<String> handleAmazonException(AmazonServiceException exception) {
		API_LOG.error("An AWS error occured", exception);

		return ResponseEntity.status(exception.getStatusCode())
			.contentType(MediaType.TEXT_PLAIN)
			.body(exception.getMessage());
	}

	@ExceptionHandler(InvalidPurposeException.class)
	@ResponseBody
	ResponseEntity<String> handleInvalidPurposeException(InvalidPurposeException exception) {
		API_LOG.error("An invalid purpose error occured", exception);

		return ResponseEntity.badRequest()
			.contentType(MediaType.TEXT_PLAIN)
			.body(exception.getMessage());
	}

	/**
	 * "Catch" the {@link MultipartException}.
	 * Return an error message with an HTTP status 400.
	 *
	 * @param exception The MultipartException that was "caught".
	 * @return The error message explaining the malformed multipart request
	 */
	@ExceptionHandler(MultipartException.class)
	@ResponseBody
	ResponseEntity<String> handleMultipartException(MultipartException exception) {
		API_LOG.error("Malformed multipart request", exception);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		String message = "Invalid multipart request: " + 
			(exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage());
		
		return new ResponseEntity<>(message, httpHeaders, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<AllErrors> cope(TransformException exception) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return new ResponseEntity<>(exception.getDetails(), httpHeaders, HttpStatus.UNPROCESSABLE_ENTITY);
	}

}
