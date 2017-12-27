package gov.cms.qpp.conversion.api.helper;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import static java.sql.Date.valueOf;

public class JwtTestHelper {

	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

	public static String createJwt() {
		Map<String, Object> claimMap = createClaimMapWithId("fb1778dd-a5e3-42c8-836f-47654e003fab");

		JwtBuilder builder = createJwtBuilderWithClaimMap(claimMap);

		return builder.compact();
	}

	public static String createJwtWithInvalidId() {
		Map<String, Object> claimMap = createClaimMapWithId("invalid-id");

		JwtBuilder builder = createJwtBuilderWithClaimMap(claimMap);

		return builder.compact();
	}

	private static Map<String, Object> createClaimMapWithId(String id) {
		Map<String, String> dataMap = new HashMap<>();
		Map<String, Object> claimMap = new HashMap<>();
		dataMap.put("orgType", "registry");
		dataMap.put("cmsId", "40000");
		dataMap.put("programYear", "2017");
		dataMap.put("id", id);
		dataMap.put("name", "testing-org");
		claimMap.put("data", dataMap);

		return claimMap;
	}

	private static JwtBuilder createJwtBuilderWithClaimMap(Map<String, Object> claimMap) {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("testKey");
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, SIGNATURE_ALGORITHM.getJcaName());

		LocalDate now = LocalDate.now();
		LocalDate expirationDate = LocalDate.of(2020, 12, 31);
		return Jwts.builder()
				.setIssuedAt(valueOf(now))
				.setClaims(claimMap)
				.setIssuer("testing-org")
				.setExpiration(valueOf(expirationDate))
				.signWith(SIGNATURE_ALGORITHM, signingKey);
	}
}
