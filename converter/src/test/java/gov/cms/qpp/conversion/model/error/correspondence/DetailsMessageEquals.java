package gov.cms.qpp.conversion.model.error.correspondence;

import java.util.Objects;

import com.google.common.truth.Correspondence;

import gov.cms.qpp.conversion.model.error.Detail;

public final class DetailsMessageEquals extends Correspondence<Detail, String> {

	private DetailsMessageEquals() {
	}

	public static DetailsMessageEquals INSTANCE = new DetailsMessageEquals();

	@Override
	public boolean compare(Detail actual, String expected) {
		if (actual == null) {
			return expected == null;
		}
		return Objects.equals(actual.getMessage(), expected);
	}

	@Override
	public String toString() {
		return null;
	}
}
