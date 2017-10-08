package gov.cms.qpp.conversion.model.error.correspondence;


import com.google.common.truth.Correspondence;
import gov.cms.qpp.conversion.model.error.Detail;

public class DetailsMessageEquals extends Correspondence<Detail, String> {

	@Override
	public boolean compare(Detail actual, String expected) {
		if (actual == null) {
			return expected == null;
		}
		return actual.getMessage().equals(expected);
	}

	@Override
	public String toString() {
		return null;
	}
}
