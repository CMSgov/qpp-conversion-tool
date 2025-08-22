package gov.cms.qpp.conversion.util;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.cms.qpp.test.helper.HelperContract;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Objects;

class CloneHelperTest implements HelperContract {

	@Override
	public Class<?> getHelperClass() {
		return CloneHelper.class;
	}

	static class TestMeasure implements Serializable {
		String measureId;
		int value;

		TestMeasure(String measureId, int value) {
			this.measureId = measureId;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof TestMeasure)) return false;
			TestMeasure that = (TestMeasure) o;
			return value == that.value &&
					Objects.equals(measureId, that.measureId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(measureId, value);
		}
	}

	@Test
	void deepCloneQppMeasureSuccess() {
		TestMeasure original = new TestMeasure("ACI_INFBLO_1", 100);

		TestMeasure clone = CloneHelper.deepClone(original);

		assertWithMessage("Clone should equal original").that(clone).isEqualTo(original);
		assertWithMessage("Clone should be a different reference").that(clone).isNotSameInstanceAs(original);
	}

	@Test
	void deepCloneThrowsUncheckedIOExceptionForNonSerializable() {
		Object notSerializable = new Object();
		assertThrows(UncheckedIOException.class, () -> CloneHelper.deepClone(notSerializable));
	}
}
