package gov.cms.qpp;

import org.mockito.Mockito;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.QrdaSource;

public class ConverterTestHelper {

	public static Converter newMockConverter() {
		return new Converter(Mockito.mock(QrdaSource.class));
	}

	private ConverterTestHelper() {
	}

}