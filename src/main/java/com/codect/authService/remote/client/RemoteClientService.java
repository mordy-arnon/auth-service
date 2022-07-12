package com.codect.authService.remote.client;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class RemoteClientService {

	@Autowired
	private HttpServletRequest request;
	@Value("${auth.server.url}")
	private String authServerUrl;
	@Value("${auth.entitiesType.cache.duration}")
	private int cacheDuration;
	private static int scacheDuration;
	private static Map<String,String> classes;
	private static long lastUpdate;

	@Value("${jwt.secret}")
	private String secret;
	
	public String getJWTToken() {
		final String requestTokenHeader = request.getHeader("Authorization");
		String jwtToken = null;
		String bearer = "Bearer ";
		if (requestTokenHeader != null && requestTokenHeader.startsWith(bearer)) {
			jwtToken = requestTokenHeader.substring(bearer.length());
		}
		return jwtToken;
	}
	
	public String getUsername() {
		try {
			final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(getJWTToken()).getBody();
			return claims.getSubject();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("invalid JWT Token. continue.", e);
		}
		return null;
	}

	@FlirPreAuthorize("hasPermission(#orga, 'ADMINISTRATION')")
	public String home3(SecuredEntityLetsSayCamera orga) {
		return "mordy";
	}

	public short getSecureTypeId(String classToFind) {
		if (classes==null||lastUpdate+scacheDuration<System.currentTimeMillis()) {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(getJWTToken());
			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
			ResponseEntity<Map> response = new RestTemplate().exchange(authServerUrl, HttpMethod.GET, requestEntity, Map.class);
			classes=(Map<String, String>)response.getBody().get("entities");
			lastUpdate=System.currentTimeMillis();
		}
		return new Short(classes.get(classToFind));
	}
}
