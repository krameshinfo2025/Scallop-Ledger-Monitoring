/**
 * 
 */
package com.scallop.ledger.config;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scallop.ledger.domain.AppUser;
import com.scallop.ledger.response.ResponseData.LoginResponse;

/**
 * 
 */
@Service(value = "jwtTokenService")
public class JwtTokenService {
	
	private final JwtEncoder jwtEncoder;
	private final AppProperties.Jwt jwtProps;
	
	//private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_USERNAME = "username";
	
	/**
	 * @param jwtEncoder
	 * @param jwtProps
	 */
	public JwtTokenService(JwtEncoder jwtEncoder, AppProperties appProperties) {
		super();
		this.jwtEncoder = jwtEncoder;
		this.jwtProps =  appProperties.jwt();
	}
	
	@Transactional
	public LoginResponse issueTokens(AppUser user) {
		
		Instant now = Instant.now();
		String accessToken = createAccessToken(user, now);
	
		return new LoginResponse(accessToken, "Bearer", jwtProps.accessTokenTtl().toSeconds(),user.getUsername(),user.getRole());
	}
	
	private String createAccessToken(AppUser user, Instant now) {		
				
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer(jwtProps.issuer())
			.subject(user.getId().toString())
			.issuedAt(now)
			.expiresAt(now.plus(jwtProps.accessTokenTtl()))
			.id(UUID.randomUUID().toString())
			.claim(CLAIM_USERNAME, user.getUsername())
			.claim(SecurityConfig.ROLES_CLAIM, List.of(user.getRole().name()))
			.build();
		
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

}
