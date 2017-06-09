package gov.cms.qpp.conversion.model.error;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsCollectionContaining;

import java.util.Arrays;

/**
 * A Hamcrest matcher for {@link Detail}.
 *
 * Contains three convenience methods that can be statically imported.
 * <ul>
 *     <li>{@link #validationErrorTextMatches}</li>
 *     <li>{@link #validationErrorTextAndPathMatches}</li>
 *     <li>{@link #hasValidationErrorsIgnoringPath}</li>
 * </ul>
 */
public class ValidationErrorMatcher extends TypeSafeMatcher<Detail> {
	/**
	 * Returns a Hamcrest matcher that matches a {@link Detail} containing the provided error text.
	 *
	 * This matcher does not match the path of the {@code Detail}.  Meaning, the path is not tested and could be any
	 * value.
	 *
	 * @param errorText The error text to match.
	 * @return A matcher that matches just the {@code errorText} in a {@code Detail}.
	 */
	public static ValidationErrorMatcher validationErrorTextMatches(String errorText) {
		return new ValidationErrorMatcher(errorText);
	}

	/**
	 * Returns a Hamcrest matcher that matches a {@link Detail} containing the provided error text and path.
	 *
	 * This matcher matches both the error text and path, unlike {@link #validationErrorTextMatches} which only matches the error
	 * text.
	 *
	 * @param errorText The error text to match.
	 * @param path The path to match.
	 * @return A matcher that matches the {@code errorText} and {@code path} in a {@code Detail}.
	 */
	public static ValidationErrorMatcher validationErrorTextAndPathMatches(String errorText, String path) {
		return new ValidationErrorMatcher(errorText, path);
	}

	/**
	 * Returns a Hamcrest matcher that matches a list of {@code errorText}s in a list of {@link Detail}s.
	 *
	 * All of the provided {@code errorText}s must match once.  The {@code errorText}s can be in any order as well as the
	 * {@code Detail}s, just as long as each {@code errorText} matches once.
	 *
	 * @param errorTexts The {@code errorText}s that the matcher should search for.
	 * @return A matcher that matches all the {@code errorTexts} provided in many {@code Detail}s.
	 */
	public static Matcher<Iterable<Detail>> hasValidationErrorsIgnoringPath(String... errorTexts) {
		Matcher<Detail>[] validationErrorMatchers = Arrays.stream(errorTexts)
			.map(ValidationErrorMatcher::validationErrorTextMatches).toArray(ValidationErrorMatcher[]::new);

		return IsCollectionContaining.hasItems(validationErrorMatchers);
	}

	private String errorText = null;
	private String path = null;

	/**
	 * Constructs a matcher matching only {@code errorText} of a {@link Detail}.
	 *
	 * Private.  Use {@link #validationErrorTextMatches}.
	 *
	 * @param errorText The errorText to match.
	 */
	private ValidationErrorMatcher(String errorText) {
		this.errorText = errorText;
	}

	/**
	 * Constructs a matcher matching an {@code errorText} and {@code path} of a {@link Detail}.
	 *
	 * Private.  Use {@link #validationErrorTextAndPathMatches}.
	 *
	 * @param errorText The errorText to match.
	 * @param path The path to match.
	 */
	private ValidationErrorMatcher(String errorText, String path) {
		this.errorText = errorText;
		this.path = path;
	}

	/**
	 * Checks if the {@code errorText} (if applicable) and the {@code path} (if applicable) matches.
	 *
	 * @param detail The {@code Detail} to match against the provided {@code errorText} and {@code path}.
	 * @return True if there is a match, false otherwise.
	 */
	@Override
	protected boolean matchesSafely(final Detail detail) {
		return errorTextEquals(detail) && pathEquals(detail);
	}

	/**
	 * Used by Hamcrest to construct the string for the expected value(s).
	 *
	 * @param description The description to edit.
	 */
	@Override
	public void describeTo(final Description description) {
		describeErrorText(description, null);
		describePath(description, null);
	}

	/**
	 * Used by Hamcrest to construct the string for the actual value(s).
	 *
	 * @param detail The {@code Detail} that did not match.
	 * @param description The description to edit.
	 */
	@Override
	public void describeMismatchSafely(Detail detail, Description description) {
		description.appendText("was ");
		describeErrorText(description, detail);
		describePath(description, detail);
	}

	/**
	 * Checks if the {@code errorText} matches.
	 *
	 * If the {@link #errorText} is null, this check is skipped.
	 *
	 * @param detail The {@code Detail} to check its {@code errorText}.
	 * @return True if the {@code Detail}'s {@code errorText} matches, false otherwise.
	 */
	private boolean errorTextEquals(Detail detail) {
		if (null == errorText) {
			return true;
		}

		return errorText.equals(detail.getMessage());
	}

	/**
	 * Checks if the {@code path} matches.
	 *
	 * If the {@link #path} is null, this check is skipped.
	 *
	 * @param detail The {@code Detail} to check its {@code path}.
	 * @return True if the {@code Detail}'s {@code path} matches, false otherwise.
	 */
	private boolean pathEquals(Detail detail) {
		if (null == path) {
			return true;
		}

		return path.equals(detail.getPath());
	}

	/**
	 * Helps constructing a description of the {@code errorText}.
	 *
	 * Uses the local {@link #errorText} if a {@link Detail} is {@code null}.
	 *
	 * @param description The description to edit.
	 * @param detail A {@code Detail} to get the {@code errorText} from.  Optional.
	 */
	private void describeErrorText(Description description, Detail detail) {
		if (null == errorText) {
			return;
		}

		String errorTextToUse = (detail == null ? errorText : detail.getMessage());

		description.appendText("an errorText of '" + errorTextToUse + "'");
	}

	/**
	 * Helps constructing a description of the {@code path}.
	 *
	 * Uses the local {@link #path} if a {@link Detail} is {@code null}.
	 *
	 * @param description The description to edit.
	 * @param detail A {@code Detail} to get the {@code path} from.  Optional.
	 */
	private void describePath(Description description, Detail detail) {
		if (null == path) {
			return;
		}

		String pathToUse = (detail == null ? path : detail.getPath());

		description.appendText(" and ");
		description.appendText("a path of '" + pathToUse + "'");
	}
}
