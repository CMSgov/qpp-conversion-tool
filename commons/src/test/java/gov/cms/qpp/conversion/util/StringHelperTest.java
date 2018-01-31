package gov.cms.qpp.conversion.util;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class StringHelperTest {

	@Test
	void testJoining() {
		String joined = StringHelper.join(Lists.newArrayList("Dog", "Cow", "Moof"), ", ", "or ");

		assertThat(joined).isEqualTo("Dog, Cow, or Moof");
	}

	@Test
	void testJoiningOneElement() {
		String joined = StringHelper.join(Lists.newArrayList("Dog"), ", ", "or ");

		assertThat(joined).isEqualTo("Dog");
	}

	@Test
	void testJoiningTwoElement() {
		String joined = StringHelper.join(Lists.newArrayList("Dog", "Cow"), ", ", "or ");

		assertThat(joined).isEqualTo("Dog or Cow");
	}

	@Test
	void testSomethingDifferent() {
		String joined = StringHelper.join(Lists.newArrayList("Completely", "Utterly", "Incontrovertibly", "Capriciously"), " DogCow, ", "and even perhaps ");

		assertThat(joined).isEqualTo("Completely DogCow, Utterly DogCow, Incontrovertibly DogCow, and even perhaps Capriciously");
	}
}