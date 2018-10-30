package gov.cms.qpp.conversion.util;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

@Ignore
class StringHelperTest {

	@Test
	void testJoining() {
		String joined = StringHelper.join(Arrays.asList("Dog", "Cow", "Moof"), ",", "or");

		assertThat(joined).isEqualTo("Dog, Cow, or Moof");
	}

	@Test
	void testJoiningOneElement() {
		String joined = StringHelper.join(Collections.singletonList("Dog"), ",", "or");

		assertThat(joined).isEqualTo("Dog");
	}

	@Test
	void testJoiningTwoElement() {
		String joined = StringHelper.join(Arrays.asList("Dog", "Cow"), ",", "or");

		assertThat(joined).isEqualTo("Dog or Cow");
	}

	@Test
	void testSomethingDifferent() {
		String joined = StringHelper.join(
			Arrays.asList("Completely", "Utterly", "Incontrovertibly", "Capriciously"), " DogCow,", "and even perhaps");

		assertThat(joined).isEqualTo("Completely DogCow, Utterly DogCow, Incontrovertibly DogCow, and even perhaps Capriciously");
	}
}