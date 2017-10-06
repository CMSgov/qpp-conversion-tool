package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.google.common.truth.Truth.assertWithMessage;

public class ExceptionHandlerControllerV1Test {

	private ExceptionHandlerControllerV1 objectUnderTest = new ExceptionHandlerControllerV1();

	@Test
	public void testStatusCode() {
		TransformException exception = new TransformException("test transform exception", new NullPointerException(), new AllErrors());

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception, null);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	public void testHeaderContentType() {
		TransformException exception = new TransformException("test transform exception", new NullPointerException(), new AllErrors());

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception, null);

		assertWithMessage("The response entity's content type was incorrect.")
				.that(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	public void testBody() {
		AllErrors allErrors = new AllErrors();
		TransformException exception = new TransformException("test transform exception", new NullPointerException(), allErrors);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception, null);
		assertWithMessage("The response entity's content type was incorrect.")
				.that(responseEntity.getBody())
				.isEqualTo(allErrors);
	}
}