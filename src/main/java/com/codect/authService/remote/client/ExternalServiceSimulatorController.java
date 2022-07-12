package com.codect.authService.remote.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/exService")
public class ExternalServiceSimulatorController {

	@Autowired
	private RemoteClientService rcServ;
	/*
	 * STEP 1 - Decide on entity to secure, and put the classname/entityType in ACL_CLASS table.
	 */
	/*
	 * STEP 2 - when User login send him to:
	 * 			http://AUTH_SERVICE:PORT/api/authentication/login   with username/password
	 * 			this create a JWT-Token with the ACL permissions in it.
	 */
	/*
	 * STEP 3 - in any event you already have in your services, when you insert-to-DB/create a specific object to secure, CALL our service:
	 *          http://AUTH_SERVICE:PORT/api/authorization/createObject   send the user JWT-Token in the header.  
	 */
	@PostMapping("/registerNewDevice")
	public String registerNewDevice(@RequestBody Map<String,Object> deviceData) {
		// check parameters
		// check camera
		// insert to DB.
		
		// register user as owner of the camera:
		HashMap<String, Object> body=new HashMap<String, Object>();
		body.put("secureType", "Device");
		body.put("objectId",((Number)deviceData.get("objectId")).longValue());
		body.put("owner", rcServ.getUsername());
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(rcServ.getJWTToken());
		HttpEntity<Map> requestEntity = new HttpEntity<>(body,headers);
		ResponseEntity<String> ret =new RestTemplate().exchange("http://localhost:8081/api/authorization/createObject", HttpMethod.POST, requestEntity, String.class);
		return ret.getBody();
	}

	/*
	 * STEP 4 - 
	 */
	@PostMapping("/home2")
	public String home2(@RequestBody SecuredEntityLetsSayCamera d) {
		return "";
	}
	
	
	@GetMapping("/home3")
	public String testJWTAuth(@RequestBody SecuredEntityLetsSayCamera d) {
		rcServ.home3(d);
		return "all good";
	}

}
