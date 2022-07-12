package com.codect.authService.rest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codect.authService.services.AuthenticationService;
import com.codect.authService.services.JwtService;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	private AuthenticationService authenticationService; 
	@Autowired
	JwtService jwtService;

//--------------------- no Authorization header -------------------------
	@PostMapping("/service/login")
	public Map<String,Object> serviceLogin(@RequestBody Map<String,Object> serviceLoginReq) {
		//{"serviceName": "{{serviceName}}","key": "{{key}}","secret": "{{secret}}","audiences": ["{{audienceA}}", "{{audienceB}}"]}
		//{"tokens":[{"audience": "service-b","token": {"accessToken": "","typeAs": "Bearer","expiration": 3600}}]}"
		return new HashMap<String, Object>();
	}
	
	@GetMapping("/service/{serviceName}/jwks")
	public Map<String,Object> jwks(@PathVariable("serviceName") String serviceName) {
		return new HashMap<String, Object>();        //jwtService.JWKs.get(serviceName);
	}

	@GetMapping("/user/jwks")
	public Map<String,Object> userjwks() {
		return new HashMap<String, Object>();        //jwtService.JWKs.get("user");
	}

	@PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginReq login) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
            		new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
            User user = (User) authenticate.getPrincipal();
            String token=authenticationService.createJWT(user,login.getExpirationDuration());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,token).body("jwt in header");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

	@RequestMapping(value="/refresh",method=RequestMethod.PUT)
	public Map<String,Object> refresh(@RequestBody Map<String,Object> refresh) {
		//{"refreshToken": "{{refreshToken}}"}
		//{"accessToken":"","typeAs":"Bearer","expiration":600}
		return new HashMap<String, Object>();
	}
	
	@GetMapping(value="/signup")
	public Map<String,Object> signup(@RequestBody Map<String,Object> refresh) {
		//{"firstName":"{{firstName}}","lastName":"{{lastName}}","email":"{{userName}}","password":"{{userPassword}}","redirectUrl":"{{redirectURL}}",
		// "birthday":"{{birthday}}","title":"{{title}}","gender":"{{gender}}","phone":"{{phone}}","address":{"country":"{{country}}","city":"{{city}}",
		// "street":"{{street}}","postalCode":"{{postalCode}}"}}
		// {"id":"2bd1290a-16e5-4d94-b0aa-01cd6d0e077b"}
		return new HashMap<String, Object>();
	}	

	@PostMapping(value="/resend")
	public void resend(@RequestBody Map<String,Object> email) {
		//{"email":"{{userName}}"}
	}
	
	@PostMapping(value="/forgot-password")
	public void forgotPassword(@RequestBody Map<String,Object> email) {
		//{"email":"{{userName}}"}
	}

	@RequestMapping(value="/user",method=RequestMethod.DELETE)
	public void deleteUser(@RequestBody Map<String,Object> userPass) {
		//{"username":"{{usernameToDelete}}","password":"{{passwordToDelete}}"}
	}

	@GetMapping("/password-policy")
	public Map<String,Object> getPasswordPolicy(){
		Map<String, Object> ret=new HashMap<String, Object>();
		ret.put("minimum_characters",8);
		ret.put("maximum_characters",32);
		ret.put("white_spaces","illegal");
		ret.put("non_alphanumeric",0);
		return ret;
	}

	@PostMapping("/oauth2/token")
	public Map<String,Object> token(@RequestBody Map<String,Object> tokenReq) {
		//{"token_id":"{{token_id}}","accessToken":"{{accessToken}}"}
		//{"accessToken":"eyJraVTrHJ7A","typeAs":"Bearer","expiration":600}
		return new HashMap<String, Object>();
	}
	
// -------------------------- with Authorization header ----------------------------
	@GetMapping(value="/verify")
	public Map<String,Object> verify() {
		//{"token":"","status":"Verified"}
		return new HashMap<String, Object>();
	}
		
	@RequestMapping(value="/logout",method=RequestMethod.PUT)
	public Map<String,Object> logout(@RequestBody Map<String,Object> refresh) {
		//{"refreshToken": "{{refreshToken}}"}
		//{"accessToken":"","typeAs":"Bearer","expiration":600}
		return new HashMap<String, Object>();
	}

	@RequestMapping(value="/user",method=RequestMethod.PUT)
	public Map<String,Object> updateUser(@RequestBody Map<String,Object> refresh) {
		//{"firstName":"{{firstName}}","lastName":"{{lastName}}","email":"{{userName}}","password":"{{userPassword}}","redirectUrl":"{{redirectURL}}",
		// "birthday":"{{birthday}}","title":"{{title}}","gender":"{{gender}}","phone":"{{phone}}","address":{"country":"{{country}}","city":"{{city}}",
		// "street":"{{street}}","postalCode":"{{postalCode}}"}}
		// {"id":"2bd1290a-16e5-4d94-b0aa-01cd6d0e077b"}
		return new HashMap<String, Object>();
	}
	
	@RequestMapping(value="/user/password",method=RequestMethod.PUT)
	public void changePassword(@RequestBody Map<String,Object> refresh) {
		//{"oldPassword":"{{oldPassword}}","newPassword":"{{newPassword}}"}
		// {"id":"2bd1290a-16e5-4d94-b0aa-01cd6d0e077b"}
	}
	
	@GetMapping(value="/user")
	public Map<String,Object> getUser() {
		//{"id":"2bd1290a-16e5-4d94-b0aa-01cd6d0e077b","userInfo":{"email":"cloud.services@flir.com","federatedIdentities":[],"firstName":"Cloud",
		//"lastName":"Services","title":"Mr","birthday":"1987-01-01","gender":"male","phone":"050-9876543","address":{"country":"Israel","city":"Tel-Aviv",
		//"street":"Allenby","postalCode":"6699887"},"pictureUrl":"https://int-lambda-au..."},"verified": true}
		return new HashMap<String, Object>();
	}

	@PostMapping(value="/user/picture",consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void uploadUserPicture(InputStream picture) {
		
	}
	
	@GetMapping("/user/picture")
	public Map<String,Object> getPicture(){
		//{"url":"https://int-lambda-authentication.s3.amazonaws.com/2bd1290a-16e5-4d94-b0aa-01cd6d0e077b?response-content-disposition=attachm"}
		return new HashMap<String, Object>();
	}
	
	@RequestMapping(value="/user/picture",method=RequestMethod.DELETE)
	public void deletePicture(){
	}
}
