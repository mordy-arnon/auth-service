package com.codect.authService.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

	@Autowired
	private JwtService jwtService;
	@Autowired
	private ApplicationContext ac;
	
	public String createJWT(User user, int expirationDuration) {
		List<String> acls=ac.getBean(AuthorizationService.class).getUserACLs(user.getUsername());
		return jwtService.generateToken(user, expirationDuration,acls);
	}
}
