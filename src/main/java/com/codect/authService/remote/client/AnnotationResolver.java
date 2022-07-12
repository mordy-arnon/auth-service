package com.codect.authService.remote.client;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codect.authService.rest.JwtService;

@Aspect
@Component
public class AnnotationResolver {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private JwtService jwtService;

	@Around("@annotation(RemotePreAuthorize)")
	public Object preAuthorize(ProceedingJoinPoint joinPoint) throws Throwable {
		Map<String, Object> acls = (Map<String, Object>) request.getAttribute("acls");
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
	    Method method = signature.getMethod();
	    RemotePreAuthorize myAnnotation = method.getAnnotation(RemotePreAuthorize.class);
		if (acls == null)
			acls = createAclsFromJWT(request);
		return joinPoint.proceed();
	}

	private Map<String, Object> createAclsFromJWT(HttpServletRequest request) {
		if (request.getAttribute("jwtToken") == null) {
			final String requestTokenHeader = request.getHeader("Authorization");
			String jwtToken = null;
			String bearer = "Bearer ";
			if (requestTokenHeader != null && requestTokenHeader.startsWith(bearer)) {
				jwtToken = requestTokenHeader.substring(bearer.length());
				List<String> perms=(List<String>) jwtService.getAllClaimsFromToken(jwtToken).get("prm");
			}
		}
		
		return null;
	}
}
