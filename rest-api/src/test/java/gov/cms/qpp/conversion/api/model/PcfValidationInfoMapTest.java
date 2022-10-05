package gov.cms.qpp.conversion.api.model;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.amazonaws.util.StringInputStream;

import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PcfValidationInfoMapTest {

	@Mock
	private InputStream mockIns;

	@Test
	void test_loadJsonStream() throws Exception {
		String json = "[" +
			    "   {\r\n" + 
				"		\"apm_entity_id\": \"T1AR0503\",\r\n" + 
				"		\"tin\": \"000333333\",\r\n" + 
				"		\"npi\": \"0333333333\"\r\n" + 
				"	},\r\n" + 
				"	{\r\n" + 
				"		\"apm_entity_id\": \"T1AR0518\",\r\n" + 
				"		\"tin\": \"000444444\",\r\n" + 
				"		\"npi\": \"0444444444\"\r\n" + 
				"	}\r\n" + 
				"]\r\n";
		InputStream jsonStream = new StringInputStream(json);
		
		PcfValidationInfoMap cpc = new PcfValidationInfoMap(jsonStream);
		Map<String, Map<String, List<String>>> map = cpc.getApmTinNpiCombinationMap();
		
		assertThat(map).isNotNull();
		assertThat(map.size()).isEqualTo(2);
		assertThat(map.get("T1AR0503").get("000333333").indexOf("0333333333")).isGreaterThan(-1);
		assertThat(map.get("T1AR0518").get("000444444").indexOf("0444444444")).isGreaterThan(-1);
	}

	@Test
	void test_loadNullStream() throws Exception {
		PcfValidationInfoMap cpc = new PcfValidationInfoMap(null);
		Map<String, Map<String, List<String>>> map = cpc.getApmTinNpiCombinationMap();

		assertThat(map).isNull();
	}

	@Test
	void test_loadNullStream_throwsIOE() throws Exception {
		Mockito.when(mockIns.read()).thenThrow(new IOException());
		
		PcfValidationInfoMap cpc = new PcfValidationInfoMap(mockIns);
		Map<String, Map<String, List<String>>> map = cpc.getApmTinNpiCombinationMap();
		
		assertThat(map).isNotNull();
		assertThat(map.size()).isEqualTo(0);
	}
}
