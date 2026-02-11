package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.InvalidPurposeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.model.error.TransformException;

import com.amazonaws.AmazonServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import gov.cms.qpp.conversion.api.exceptions.BadZipException;

/**
 * Modify the controller to send back different responses for exceptions
 */
@ControllerAdvice
public class ExceptionHandlerControllerV1 extends ResponseEntityExceptionHandler {
	private static final Logger API_LOG = LoggerFactory.getLogger(ExceptionHandlerControllerV1.class);

	private AuditService auditService;

	public ExceptionHandlerControllerV1(final AuditService auditService) {
		this.auditService = auditService;
	}

	@ExceptionHandler(TransformException.class)
	@ResponseBody
	ResponseEntity<AllErrors> handleTransformException(TransformException exception) {
		API_LOG.info("Transform failed validation (422): {}", exception.getMessage());
		API_LOG.debug("TransformException details", exception);

		auditService.failConversion(exception.getConversionReport());
		return cope(exception);
	}

	@ExceptionHandler(QppValidationException.class)
	@ResponseBody
	ResponseEntity<AllErrors> handleQppValidationException(QppValidationException exception) {
		API_LOG.info("Submission validation failed (422): {}", exception.getMessage());
		API_LOG.debug("QppValidationException details", exception);

		auditService.failValidation(exception.getConversionReport());
		return cope(exception);
	}

	/**
	 * Catch Spring static resource misses (hitting random paths).
	 * Return 404 without logging as ERROR.
	 *
	 */
	@Override
	protected ResponseEntity<Object> handleNoResourceFoundException(
			NoResourceFoundException exception,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		API_LOG.debug("No resource found (404): {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Not found");
	}

	@ExceptionHandler(NoFileInDatabaseException.class)
	@ResponseBody
	ResponseEntity<String> handleFileNotFoundException(NoFileInDatabaseException exception) {
		API_LOG.error("A database error occurred", exception);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<>(exception.getMessage(), httpHeaders, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidFileTypeException.class)
	@ResponseBody
	ResponseEntity<String> handleInvalidFileTypeException(InvalidFileTypeException exception) {
		API_LOG.error("A file type error occurred", exception);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<>(exception.getMessage(), httpHeaders, HttpStatus.NOT_FOUND);
	}

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
