package gov.cms.qpp.conversion.api.helper;

import static java.sql.Date.valueOf;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtTestHelper {

	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

	public static String createJwt(JwtPayloadHelper payload) {
		Map<String, Object> claimMap = createClaimMap(payload);

		JwtBuilder builder = createJwtBuilderWithClaimMap(claimMap);

		return builder.compact();
	}

	private static Map<String, Object> createClaimMap(JwtPayloadHelper payload) {
		Map<String, String> dataMap = new HashMap<>();
		Map<String, Object> claimMap = new HashMap<>();

		dataMap.put("cmsId", "40000");
		dataMap.put("programYear", "2018");
		dataMap.put("id", "random-id");

		if (payload.getName() != null) {
			dataMap.put("name", payload.getName());
		}
		if (payload.getOrgType() != null) {
			dataMap.put("orgType", payload.getOrgType());
		}

		claimMap.put("data", dataMap);

		return claimMap;
	}

	private static JwtBuilder createJwtBuilderWithClaimMap(Map<String, Object> claimMap) {
		SecretKey signingKey = Keys.secretKeyFor(SIGNATURE_ALGORITHM); // TEST KEY

		LocalDate now = LocalDate.now();
		LocalDate expirationDate = LocalDate.of(2025, 12, 31);
		return Jwts.builder()
				.setIssuedAt(valueOf(now))
				.setClaims(claimMap)
				.setIssuer("testing-org")
				.setExpiration(valueOf(expirationDate))
				.signWith(signingKey, SIGNATURE_ALGORITHM);
	}
}
