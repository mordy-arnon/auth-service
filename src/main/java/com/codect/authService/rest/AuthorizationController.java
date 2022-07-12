package com.codect.authService.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codect.authService.entities.Organization;

@RestController
public class AuthorizationController {

	@Autowired
	private SecurityService ss;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtService jwtService;
	
	@GetMapping("/home1")
	@PreAuthorize("hasRole('USER')")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		ss.grant("aaaa",new Organization(123,"mordy"),"ADMINISTRATION");
		return "all good";
	}
	
	@PostMapping("/home2")
	public String home2(@RequestBody Organization d) {
		return ss.dothat2(d);
	}
	
	@PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserPass login) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
            		new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
            User user = (User) authenticate.getPrincipal();
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,jwtService.generateToken(user)).body("jwt in header");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
	
	@GetMapping("/home3")
	public String testJWTAuth(@RequestBody Organization d) {
		ss.home3(d);
		return "all good";
	}
}
