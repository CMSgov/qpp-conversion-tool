package gov.cms.qpp.conversion.util;

import java.util.Iterator;

/**
 * Helper class for custom code that isn't covered by Apache Commons or Google Guava.
 */
public class StringHelper {
	/**
	 * Joins a {@link Iterable} of {@link String}s together with the specified separator. Between the second to last and last
	 * item, the specified conjunction is used instead.  This allows one to make a {@link String} like {@code Dog, Cow, and Moof}.
	 *
	 * @param iterable The object to join together
	 * @param separator The {@link String} that splits most of the items in the iterable.
	 * @param conjunction The {@link String} that splits the second to last and last item.
	 * @return A joined {@link String}.
	 */
	public static String join(Iterable<String> iterable, String separator, String conjunction) {

		StringBuilder creator = new StringBuilder();

		int timeThroughLoop = 0;

		Iterator<String> iterator = iterable.iterator();
		while (iteratorHasAnotherItem(iterator)) {
			timeThroughLoop++;
			String currentString = iterator.next();

			if (iteratorHasAnotherItem(iterator)) {
				appendStringAndThenString(creator, currentString, separator);
			} else if (isFirstItem(timeThroughLoop)) {
				creator.append(currentString);
			} else {
				if (isSecondItem(timeThroughLoop)) {
					deletePreviousConjunction(conjunction, creator);
				}
				appendStringAndThenString(creator, conjunction, currentString);
			}
		}

		return creator.toString();
	}

	/**
	 * Appends the {@code firstString} and then the {@code secondString} to the {@link StringBuilder}.
	 *
	 * @param creator The {@link StringBuilder} to append to.
	 * @param firstString The first {@link String} to append.
	 * @param secondString The second {@link String} to append.
	 */
	private static void appendStringAndThenString(final StringBuilder creator, final String firstString, final String secondString) {
		creator.append(firstString).append(secondString);
	}

	/**
	 * Do we have an upcoming item or is this the end of the {@link Iterator}?
	 *
	 * @param iterator The {@link Iterator} to check.
	 * @return True if there is at least one more item.
	 */
	private static boolean iteratorHasAnotherItem(final Iterator<?> iterator) {
		return iterator.hasNext();
	}

	/**
	 * Is this the first item in the {@link Iterator}?
	 *
	 * @param timeThroughLoop The loop counter.
	 * @return True if this is the first time through the loop, false otherwise.
	 */
	private static boolean isFirstItem(final int timeThroughLoop) {
		return timeThroughLoop == 1;
	}

	/**
	 * Is this the second item in the {@link Iterator}?
	 *
	 * @param timeThroughLoop The loop counter.
	 * @return True if this is the second time through the loop, false otherwise.
	 */
	private static boolean isSecondItem(final int timeThroughLoop) {
		return timeThroughLoop == 2;
	}

	/**
	 * Delete the previous appended {@code conjunction}.
	 *
	 * @param conjunction The conjunction to delete.
	 * @param creator The {@link StringBuilder} to delete the conjunction from.
	 */
	private static void deletePreviousConjunction(final String conjunction, final StringBuilder creator) {
		creator.delete(creator.length() - conjunction.length() + 1, creator.length());
		creator.append(" ");
	}
}
