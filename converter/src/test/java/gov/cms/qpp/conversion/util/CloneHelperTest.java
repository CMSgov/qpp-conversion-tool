package gov.cms.qpp.conversion.util;

import gov.cms.qpp.test.helper.HelperContract;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CloneHelperTest implements HelperContract {

	@Override
	public Class<?> getHelperClass() {
		return MyObject.class;
	}

	@Test
	void myObjectClone() {
		MyObject mo1 = new MyObject();
		mo1.setName("Siva");
		mo1.addData("1", "2");

		MyObject mo2 = CloneHelper.deepClone(mo1);

		Assertions.assertEquals(mo1.getName(), mo2.getName());
		Assertions.assertEquals(mo1.getData(), mo2.getData());
	}

}