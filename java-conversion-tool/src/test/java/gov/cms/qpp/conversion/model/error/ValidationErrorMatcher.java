package gov.cms.qpp.conversion.model.error;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ValidationErrorMatcher extends TypeSafeMatcher<ValidationError> {
	public static ValidationErrorMatcher validationErrorTextMatches(String errorText) {
		return new ValidationErrorMatcher(errorText);
	}

	public static ValidationErrorMatcher validationErrorTextAndPathMatches(String errorText, String path) {
		return new ValidationErrorMatcher(errorText, path);
	}

	public static Matcher<Iterable<? extends ValidationError>> containsValidationErrorInAnyOrderIgnoringPath(String... errorTexts) {
		Collection<Matcher<? super ValidationError>> validationErrorMatchers = Arrays.stream(errorTexts)
			.map(ValidationErrorMatcher::validationErrorTextMatches).collect(Collectors.toList());

		return Matchers.containsInAnyOrder(validationErrorMatchers);
	}

	private String errorText = null;
	private String path = null;

	private ValidationErrorMatcher(String errorText) {
		this.errorText = errorText;
	}

	private ValidationErrorMatcher(String errorText, String path) {
		this.errorText = errorText;
		this.path = path;
	}

	@Override
	protected boolean matchesSafely(final ValidationError validationError) {
		return errorTextEquals(validationError) && pathEquals(validationError);
	}

	@Override
	public void describeTo(final Description description) {
		describeErrorText(description, null);
		describePath(description, null);
	}

	@Override
	public void describeMismatchSafely(ValidationError validationError, Description description) {
		description.appendText("was ");
		describeErrorText(description, validationError);
		describePath(description, validationError);
	}

	private boolean errorTextEquals(ValidationError validationError) {
		if (null == errorText) {
			return true;
		}

		return errorText.equals(validationError.getErrorText());
	}

	private boolean pathEquals(ValidationError validationError) {
		if (null == path) {
			return true;
		}

		return path.equals(validationError.getPath());
	}

	private void describeErrorText(Description description, ValidationError validationError) {
		if (null == errorText) {
			return;
		}

		String errorTextToUse = (validationError == null ? errorText : validationError.getErrorText());

		description.appendText("an errorText of '" + errorTextToUse + "'");
	}

	private void describePath(Description description, ValidationError validationError) {
		if (null == path) {
			return;
		}

		String pathToUse = (validationError == null ? path : validationError.getPath());

		description.appendText(" and ");
		description.appendText("a path of '" + pathToUse + "'");
	}
}
