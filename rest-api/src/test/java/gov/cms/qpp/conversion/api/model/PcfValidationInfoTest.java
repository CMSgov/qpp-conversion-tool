package gov.cms.qpp.conversion.api.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PcfValidationInfoTest {

	@Test
	void test_constructor() throws Exception {
		
		String npi = "npi";
		String tin = "tin";
		String apm = "apm";
		PcfValidationInfo cpc = new PcfValidationInfo(npi, tin, apm);
		
		assertThat(cpc.getNpi()).isEqualTo(npi);
		assertThat(cpc.getTin()).isEqualTo(tin);
		assertThat(cpc.getApm()).isEqualTo(apm);
	}

	@Test
	void test_noArgsConstructor() throws Exception {
		
		PcfValidationInfo cpc = new PcfValidationInfo();
		
		assertThat(cpc.getNpi()).isNull();
		assertThat(cpc.getTin()).isNull();
		assertThat(cpc.getApm()).isNull();
	}

	@Test
	void test_setters() throws Exception {
		PcfValidationInfo cpc = new PcfValidationInfo();
		
		String npi = "npi";
		String tin = "tin";
		String apm = "apm";

		cpc.setNpi(npi);
		cpc.setTin(tin);
		cpc.setApm(apm);
		
		assertThat(cpc.getNpi()).isEqualTo(npi);
		assertThat(cpc.getTin()).isEqualTo(tin);
		assertThat(cpc.getApm()).isEqualTo(apm);
	}
	
	
}
